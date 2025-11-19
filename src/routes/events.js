// routes/events.js
const express = require('express');
const router = express.Router();
const pool = require('../db');
const authMiddleware = require('../middleware/authMiddleware');

// Helper: generate random QR code string (14 chars)
function generateQrCodeString(length = 14) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = '';
    for (let i = 0; i < length; i++) {
        code += chars[Math.floor(Math.random() * chars.length)];
    }
    return code;
}

/**

 * Helper: cek apakah user adalah participant/panitia di event tertentu
 * return:
 *   - 'peserta' | 'panitia' kalau terdaftar
 *   - null kalau bukan peserta event tsb
 */
async function getEventParticipantRole(eventId, userId) {
    const [rows] = await pool.query(
        `SELECT role_in_event 
         FROM event_participants 
         WHERE event_id = ? AND user_id = ?
         LIMIT 1`,
        [eventId, userId]
    );
    if (rows.length === 0) return null;
    return rows[0].role_in_event;
}

/**
 * Helper: ambil info event (termasuk created_by) untuk keperluan otorisasi
 */
async function getEventById(eventId) {
    const [rows] = await pool.query(
        `SELECT id, created_by, title, event_date, event_time, location, status, participants
         FROM events
         WHERE id = ?
         LIMIT 1`,
        [eventId]
    );
    if (rows.length === 0) return null;
    return rows[0];
}

/* ============================================================
   1. GET /api/events/panitia?createdBy=ID
   Ambil semua event BUATAN panitia tertentu
============================================================ */
router.get('/panitia', authMiddleware, async (req, res) => {
    try {
        const createdBy = Number(req.query.createdBy);

        if (!createdBy) {
            return res.status(400).json({
                success: false,
                message: 'Parameter createdBy wajib diisi'
            });
        }

        // Panitia hanya boleh akses event miliknya (atau admin)
        if (req.userId !== createdBy && req.userRole !== 'admin') {
            return res.status(403).json({
                success: false,
                message: 'Akses ditolak'
            });
        }

        const [rows] = await pool.query(
            `SELECT 
                e.id,
                e.created_by,
                e.title,
                e.event_date,
                e.event_time,
                e.location,
                e.status,
                e.participants,
                q.qr_code
             FROM events e
             LEFT JOIN qrinvitation q ON q.event_id = e.id
             WHERE e.created_by = ?

             ORDER BY e.event_date, e.event_time`,
            [createdBy]
        );

        return res.json({
            success: true,
            message: "Daftar event panitia",
            data: rows
        });

    } catch (err) {
        console.error("GET /events/panitia error:", err);
        return res.status(500).json({
            success: false,
            message: "Kesalahan server"
        });
    }
});



/* ============================================================
   2. POST /api/events
   Panitia membuat event baru + auto generate QR
============================================================ */
router.post('/', authMiddleware, async (req, res) => {
    try {
        const { created_by, title, event_date, event_time, location, status } = req.body;

        // Validasi input
        if (!created_by || !title || !event_date || !event_time || !location) {
            return res.status(400).json({
                success: false,
                message: 'Field wajib belum lengkap'
            });
        }

        // Hanya diri sendiri atau admin yang boleh create event
        if (req.userId !== created_by && req.userRole !== 'admin') {
            return res.status(403).json({
                success: false,
                message: 'Tidak berhak membuat event untuk user ini'
            });
        }

        const finalStatus = status || 'Akan Datang';

        // Insert event
        const [insertEvent] = await pool.query(
            `INSERT INTO events (created_by, title, event_date, event_time, location, status)
             VALUES (?, ?, ?, ?, ?, ?)`,
            [created_by, title, event_date, event_time, location, finalStatus]
        );

        const eventId = insertEvent.insertId;

        // Generate QR Invitation Code
        let qrCode = generateQrCodeString(14);

        await pool.query(
            `INSERT INTO qrinvitation (event_id, qr_code)
             VALUES (?, ?)`,
            [eventId, qrCode]
        );

        return res.status(201).json({
            id: eventId,
            created_by,
            title,
            event_date,
            event_time,
            location,
            status: finalStatus,
            participants: 0,
            qr_code: qrCode
        });

    } catch (err) {
        console.error("POST /events error:", err);
        return res.status(500).json({
            success: false,
            message: "Kesalahan server saat membuat event"
        });
    }
});



/* ============================================================
   3. GET /api/events/peserta?userId=ID
   Ambil semua event yang diikuti peserta
============================================================ */
router.get('/peserta', authMiddleware, async (req, res) => {
    try {
        const userId = Number(req.query.userId);
        if (!userId) {
            return res.status(400).json({
                success: false,
                message: 'Parameter userId wajib diisi'
            });
        }

        if (req.userId !== userId && req.userRole !== 'admin') {
            return res.status(403).json({
                success: false,

                message: 'Tidak boleh akses'
            });
        }

        const [rows] = await pool.query(
            `SELECT 
                e.id,
                e.created_by,
                e.title,
                e.event_date,
                e.event_time,
                e.location,
                e.status,
                e.participants,
                q.qr_code
             FROM event_participants ep
             INNER JOIN events e ON ep.event_id = e.id
             LEFT JOIN qrinvitation q ON q.event_id = e.id
             WHERE ep.user_id = ?
             ORDER BY e.event_date, e.event_time`,
            [userId]
        );
        return res.json({
            success: true,
            message: "Daftar event peserta",
            data: rows
        });

    } catch (err) {
        console.error("GET /events/peserta error:", err);
        return res.status(500).json({
            success: false,
            message: "Kesalahan server"
        });
    }
});

/* ============================================================
   4. POST /api/events/join-by-qr
   Peserta join event berdasarkan QR Invitation Code
============================================================ */
router.post('/join-by-qr', authMiddleware, async (req, res) => {
    try {
        let { qr_code } = req.body;
        if (!qr_code) {
            return res.status(400).json({
                success: false,
                message: 'qr_code wajib diisi'
            });
        }

        qr_code = String(qr_code).trim().toUpperCase();

        // Cek apakah QR code valid
        const [rows] = await pool.query(
            `SELECT 
                e.id,
                e.created_by,
                e.title,
                e.event_date,
                e.event_time,
                e.location,
                e.status,
                e.participants,
                q.qr_code
             FROM qrinvitation q
             INNER JOIN events e ON q.event_id = e.id
             WHERE q.qr_code = ?
             LIMIT 1`,
            [qr_code]
        );

        if (rows.length === 0) {
            return res.json({
                success: false,
                message: "QR code tidak valid"
            });
        }

        const event = rows[0];
        const eventId = event.id;
        const userId = req.userId;

        // Cek apakah sudah join
        const [exist] = await pool.query(
            `SELECT id FROM event_participants WHERE event_id = ? AND user_id = ?`,
            [eventId, userId]
        );

        if (exist.length > 0) {
            return res.json({
                success: true,
                message: "Kamu sudah tergabung di event ini",
                data: event
            });
        }

        // Insert baru
        await pool.query(
            `INSERT INTO event_participants (event_id, user_id)
             VALUES (?, ?)`,
            [eventId, userId]
        );


        // Update jumlah participants
        await pool.query(
            `UPDATE events 
             SET participants = (SELECT COUNT(*) FROM event_participants WHERE event_id = ?)
             WHERE id = ?`,
            [eventId, eventId]
        );
        // Ambil ulang updated event
        const [updated] = await pool.query(
            `SELECT 
                e.id,
                e.created_by,
                e.title,
                e.event_date,
                e.event_time,
                e.location,
                e.status,
                e.participants,
                q.qr_code
             FROM events e
             LEFT JOIN qrinvitation q ON q.event_id = e.id
             WHERE e.id = ?
             LIMIT 1`,
            [eventId]
        );

        return res.json({
            success: true,
            message: "Berhasil bergabung ke event",
            data: updated[0]
        });

    } catch (err) {
        console.error("POST /events/join-by-qr error:", err);
        return res.status(500).json({
            success: false,
            message: "Kesalahan server saat join event"
        });
    }
});

/* ============================================================
   5. ABSENSI PESERTA (dynamic route /:eventId/...)
============================================================ */

/**
 * POST /api/events/:eventId/attendance/check-in
 * Body (opsional):
 *   { "status": "hadir" | "izin" | "alfa" }  default: 'hadir'
 */
router.post('/:eventId/attendance/check-in', authMiddleware, async (req, res) => {
    try {
        const eventId = Number(req.params.eventId);
        if (!eventId) {
            return res.status(400).json({
                success: false,
                message: 'eventId tidak valid'
            });
        }

        const event = await getEventById(eventId);
        if (!event) {
            return res.status(404).json({
                success: false,
                message: 'Event tidak ditemukan'
            });
        }

        const userId = req.userId;

        // Harus peserta event (atau admin)
        const roleInEvent = await getEventParticipantRole(eventId, userId);
        const isAdmin = req.userRole === 'admin';
        const isEventCreator = event.created_by === userId;

        if (!roleInEvent && !isAdmin && !isEventCreator) {
            return res.status(403).json({
                success: false,
                message: 'Kamu belum tergabung di event ini'
            });
        }

        let { status } = req.body;
        if (!status || !['hadir', 'izin', 'alfa'].includes(status)) {
            status = 'hadir';
        }

        // Insert atau update absensi (satu kali per event per user)
        await pool.query(
            `INSERT INTO event_attendance (event_id, user_id, status)
             VALUES (?, ?, ?)
             ON DUPLICATE KEY UPDATE 
                status = VALUES(status),
                checked_in_at = CURRENT_TIMESTAMP`,
            [eventId, userId, status]
        );

        const [rows] = await pool.query(
            `SELECT id, event_id, user_id, status, checked_in_at
             FROM event_attendance
             WHERE event_id = ? AND user_id = ?
             LIMIT 1`,
            [eventId, userId]
        );

        return res.json({
            success: true,
            message: 'Absensi berhasil dicatat',
            data: rows[0]
        });

    } catch (err) {
        console.error("POST /events/:eventId/attendance/check-in error:", err);
        return res.status(500).json({
            success: false,
            message: 'Kesalahan server saat absensi'
        });
    }
});


/**
 * GET /api/events/:eventId/attendance/me
 * Lihat status absensi diri sendiri di event tsb
 */
router.get('/:eventId/attendance/me', authMiddleware, async (req, res) => {
    try {
        const eventId = Number(req.params.eventId);
        if (!eventId) {
            return res.status(400).json({
                success: false,
                message: 'eventId tidak valid'
            });
        }

        const event = await getEventById(eventId);
        if (!event) {
            return res.status(404).json({
                success: false,
                message: 'Event tidak ditemukan'
            });
        }

        const userId = req.userId;

        const roleInEvent = await getEventParticipantRole(eventId, userId);
        const isAdmin = req.userRole === 'admin';
        const isEventCreator = event.created_by === userId;

        if (!roleInEvent && !isAdmin && !isEventCreator) {
            return res.status(403).json({
                success: false,
                message: 'Kamu belum tergabung di event ini'
            });
        }

        const [rows] = await pool.query(
            `SELECT id, event_id, user_id, status, checked_in_at
             FROM event_attendance
             WHERE event_id = ? AND user_id = ?
             LIMIT 1`,
            [eventId, userId]
        );

        if (rows.length === 0) {
            return res.json({
                success: true,
                message: 'Belum ada data absensi untuk event ini',
                data: null
            });
        }

        return res.json({
            success: true,
            message: 'Data absensi ditemukan',
            data: rows[0]
        });

    } catch (err) {
        console.error("GET /events/:eventId/attendance/me error:", err);
        return res.status(500).json({
            success: false,
            message: 'Kesalahan server saat mengambil data absensi'
        });
    }
});


/* ============================================================
   6. ANNOUNCEMENTS EVENT
   - Peserta/panitia/creator/admin: boleh GET
   - Hanya panitia event / creator / admin: boleh POST
============================================================ */

/**
 * GET /api/events/:eventId/announcements
 */
router.get('/:eventId/announcements', authMiddleware, async (req, res) => {
    try {
        const eventId = Number(req.params.eventId);
        if (!eventId) {
            return res.status(400).json({
                success: false,
                message: 'eventId tidak valid'
            });
        }

        const event = await getEventById(eventId);
        if (!event) {
            return res.status(404).json({
                success: false,
                message: 'Event tidak ditemukan'
            });
        }

        const userId = req.userId;

        const roleInEvent = await getEventParticipantRole(eventId, userId);
        const isAdmin = req.userRole === 'admin';
        const isEventCreator = event.created_by === userId;

        // minimal: peserta, panitia, creator, atau admin
        if (!roleInEvent && !isAdmin && !isEventCreator) {
            return res.status(403).json({
                success: false,
                message: 'Kamu belum tergabung di event ini'
            });
        }

        const [rows] = await pool.query(
            `SELECT 
                ea.id,
                ea.event_id,
                ea.title,
                ea.body,
                ea.created_by,
                u.name AS created_by_name,
                ea.created_at
             FROM event_announcements ea
             INNER JOIN users u ON ea.created_by = u.id
             WHERE ea.event_id = ?
             ORDER BY ea.created_at DESC`,
            [eventId]
        );

        return res.json({
            success: true,
            message: 'Daftar announcement event',
            data: rows
        });

    } catch (err) {
        console.error("GET /events/:eventId/announcements error:", err);
        return res.status(500).json({
            success: false,
            message: 'Kesalahan server saat mengambil announcement'
        });
    }
});


/**
 * POST /api/events/:eventId/announcements
 * Body:
 *   { "title": "...", "body": "..." }
 * Hanya:
 *   - admin global, atau
 *   - pembuat event (events.created_by), atau
 *   - panitia di event (role_in_event = 'panitia')
 */
router.post('/:eventId/announcements', authMiddleware, async (req, res) => {
    try {
        const eventId = Number(req.params.eventId);
        if (!eventId) {
            return res.status(400).json({
                success: false,
                message: 'eventId tidak valid'
            });
        }

        const event = await getEventById(eventId);
        if (!event) {
            return res.status(404).json({
                success: false,
                message: 'Event tidak ditemukan'
            });
        }

        const userId = req.userId;
        const { title, body } = req.body;

        if (!title || !body) {
            return res.status(400).json({
                success: false,
                message: 'Judul dan isi announcement wajib diisi'
            });
        }

        const roleInEvent = await getEventParticipantRole(eventId, userId);

        const isAdmin = req.userRole === 'admin';
        const isEventCreator = event.created_by === userId;
        const isPanitiaEvent = roleInEvent === 'panitia';

        if (!isAdmin && !isEventCreator && !isPanitiaEvent) {
            return res.status(403).json({
                success: false,
                message: 'Tidak berhak membuat announcement di event ini'
            });
        }

        const [insertResult] = await pool.query(
            `INSERT INTO event_announcements (event_id, title, body, created_by)
             VALUES (?, ?, ?, ?)`,
            [eventId, title, body, userId]
        );

        const announcementId = insertResult.insertId;

        const [rows] = await pool.query(
            `SELECT 
                ea.id,
                ea.event_id,
                ea.title,
                ea.body,
                ea.created_by,
                u.name AS created_by_name,
                ea.created_at
             FROM event_announcements ea
             INNER JOIN users u ON ea.created_by = u.id
             WHERE ea.id = ?
             LIMIT 1`,
            [announcementId]
        );

        return res.status(201).json({
            success: true,
            message: 'Announcement berhasil dibuat',
            data: rows[0]
        });

    } catch (err) {
        console.error("POST /events/:eventId/announcements error:", err);
        return res.status(500).json({
            success: false,
            message: 'Kesalahan server saat membuat announcement'
        });
    }
});

<<<<<<< HEAD
=======

/* ============================================================
   7. DAFTAR PESERTA + ABSENSI (PANITIA)
   GET /api/events/:eventId/participants
============================================================ */

router.get('/:eventId/participants', authMiddleware, async (req, res) => {
    try {
        const eventId = Number(req.params.eventId);
        if (!eventId) {
            return res.status(400).json({
                success: false,
                message: 'eventId tidak valid'
            });
        }

        const event = await getEventById(eventId);
        if (!event) {
            return res.status(404).json({
                success: false,
                message: 'Event tidak ditemukan'
            });
        }

        const userId = req.userId;
        const roleInEvent = await getEventParticipantRole(eventId, userId);
        const isAdmin = req.userRole === 'admin';
        const isEventCreator = event.created_by === userId;
        const isPanitiaEvent = roleInEvent === 'panitia';

        // Hanya admin, creator, atau panitia yang boleh lihat daftar peserta
        if (!isAdmin && !isEventCreator && !isPanitiaEvent) {
            return res.status(403).json({
                success: false,
                message: 'Tidak berhak melihat daftar peserta event ini'
            });
        }

        const [rows] = await pool.query(
            `SELECT 
                ep.user_id,
                u.name,
                u.email,
                ep.role_in_event,
                ea.status,
                ea.checked_in_at
             FROM event_participants ep
             INNER JOIN users u ON ep.user_id = u.id
             LEFT JOIN event_attendance ea 
                ON ea.event_id = ep.event_id 
               AND ea.user_id = ep.user_id
             WHERE ep.event_id = ?
             ORDER BY u.name ASC`,
            [eventId]
        );

        // Catatan:
        // - Peserta yang belum absen => ea.status = NULL
        //   Di Android nanti kita anggap sebagai "Belum Absen" dan
        //   digabung ke kelompok "Alfa/Belum Absen" di Ringkasan.

        return res.json({
            success: true,
            message: 'Daftar peserta event',
            data: rows
        });

    } catch (err) {
        console.error("GET /events/:eventId/participants error:", err);
        return res.status(500).json({
            success: false,
            message: 'Kesalahan server saat mengambil daftar peserta'
        });
    }
});


/* ============================================================
   8. QR INVITATION – DETAIL & HAPUS
   GET /api/events/:eventId/qr-invitation
   DELETE /api/events/:eventId/qr-invitation
============================================================ */

router.get('/:eventId/qr-invitation', authMiddleware, async (req, res) => {
    try {
        const eventId = Number(req.params.eventId);
        if (!eventId) {
            return res.status(400).json({
                success: false,
                message: 'eventId tidak valid'
            });
        }

        const event = await getEventById(eventId);
        if (!event) {
            return res.status(404).json({
                success: false,
                message: 'Event tidak ditemukan'
            });
        }

        const userId = req.userId;
        const roleInEvent = await getEventParticipantRole(eventId, userId);
        const isAdmin = req.userRole === 'admin';
        const isEventCreator = event.created_by === userId;
        const isPanitiaEvent = roleInEvent === 'panitia';

        if (!isAdmin && !isEventCreator && !isPanitiaEvent) {
            return res.status(403).json({
                success: false,
                message: 'Tidak berhak melihat QR undangan event ini'
            });
        }

        const [rows] = await pool.query(
            `SELECT id, event_id, qr_code, created_at
             FROM qrinvitation
             WHERE event_id = ?
             LIMIT 1`,
            [eventId]
        );

        if (rows.length === 0) {
            return res.json({
                success: false,
                message: 'QR undangan belum dibuat',
                data: null
            });
        }

        return res.json({
            success: true,
            message: 'Detail QR undangan',
            data: rows[0]
        });

    } catch (err) {
        console.error("GET /events/:eventId/qr-invitation error:", err);
        return res.status(500).json({
            success: false,
            message: 'Kesalahan server saat mengambil QR undangan'
        });
    }
});

router.delete('/:eventId/qr-invitation', authMiddleware, async (req, res) => {
    try {
        const eventId = Number(req.params.eventId);
        if (!eventId) {
            return res.status(400).json({
                success: false,
                message: 'eventId tidak valid'
            });
        }

        const event = await getEventById(eventId);
        if (!event) {
            return res.status(404).json({
                success: false,
                message: 'Event tidak ditemukan'
            });
        }

        const userId = req.userId;
        const roleInEvent = await getEventParticipantRole(eventId, userId);
        const isAdmin = req.userRole === 'admin';
        const isEventCreator = event.created_by === userId;
        const isPanitiaEvent = roleInEvent === 'panitia';

        if (!isAdmin && !isEventCreator && !isPanitiaEvent) {
            return res.status(403).json({
                success: false,
                message: 'Tidak berhak menghapus QR undangan event ini'
            });
        }

        await pool.query(
            `DELETE FROM qrinvitation WHERE event_id = ?`,
            [eventId]
        );

        return res.json({
            success: true,
            message: 'QR undangan berhasil dihapus',
            user: null // kompatibel dengan BasicResponse di Android
        });

    } catch (err) {
        console.error("DELETE /events/:eventId/qr-invitation error:", err);
        return res.status(500).json({
            success: false,
            message: 'Kesalahan server saat menghapus QR undangan'
        });
    }
});

>>>>>>> 1d97b52 (Enhance database schema and seed data for events, participants, attendance, and announcements)
module.exports = router;
