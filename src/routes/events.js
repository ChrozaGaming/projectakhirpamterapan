// routes/events.js
const express = require('express');
const router = express.Router();
const pool = require('../db');
const authMiddleware = require('../middleware/authMiddleware');

// Helper: generate random qr_code 14 digit
function generateQrCodeString(length = 14) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = '';
    for (let i = 0; i < length; i++) {
        code += chars[Math.floor(Math.random() * chars.length)];
    }
    return code;
}

/**
 * GET /api/events/panitia?createdBy=1
 * Mengambil daftar event milik panitia tertentu + qr_code
 */
router.get('/panitia', authMiddleware, async (req, res) => {
    try {
        const createdBy = parseInt(req.query.createdBy, 10);

        if (!createdBy || Number.isNaN(createdBy)) {
            return res.status(400).json({
                success: false,
                message: 'Parameter createdBy wajib diisi'
            });
        }

        // Hanya pemilik atau admin yang boleh ambil
        if (req.userId !== createdBy && req.userRole !== 'admin') {
            return res.status(403).json({
                success: false,
                message: 'Tidak punya akses untuk event ini'
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
             ORDER BY e.event_date ASC, e.event_time ASC`,
            [createdBy]
        );

        return res.json({
            success: true,
            message: 'Daftar event panitia',
            data: rows
        });
    } catch (err) {
        console.error('GET /api/events/panitia error', err);
        res.status(500).json({
            success: false,
            message: 'Terjadi kesalahan server'
        });
    }
});

/**
 * POST /api/events
 * Body: { created_by, title, event_date, event_time, location, status? }
 * - Insert ke events
 * - Generate qr_code random
 * - Insert ke qrinvitation
 * - Return 1 objek event + qr_code di root (tanpa wrapper success/data)
 */
router.post('/', authMiddleware, async (req, res) => {
    try {
        const { created_by, title, event_date, event_time, location, status } = req.body;

        if (!created_by || !title || !event_date || !event_time || !location) {
            return res.status(400).json({
                success: false,
                message: 'Field created_by, title, event_date, event_time, location wajib diisi'
            });
        }

        // Hanya diri sendiri / admin
        if (req.userId !== created_by && req.userRole !== 'admin') {
            return res.status(403).json({
                success: false,
                message: 'Tidak punya akses membuat event untuk user ini'
            });
        }

        const finalStatus = status || 'Akan Datang';

        // Insert ke events
        const [result] = await pool.query(
            `INSERT INTO events (created_by, title, event_date, event_time, location, status)
             VALUES (?, ?, ?, ?, ?, ?)`,
            [created_by, title, event_date, event_time, location, finalStatus]
        );

        const eventId = result.insertId;

        // Generate qr_code dan insert ke qrinvitation
        const qrCode = generateQrCodeString(14);

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
        console.error('POST /api/events error', err);
        res.status(500).json({
            success: false,
            message: 'Terjadi kesalahan server saat membuat event'
        });
    }
});

/**
 * GET /api/events/peserta?userId=2
 * Mengambil daftar event yang diikuti peserta (via event_participants)
 */
router.get('/peserta', authMiddleware, async (req, res) => {
    try {
        const userId = parseInt(req.query.userId, 10);

        if (!userId || Number.isNaN(userId)) {
            return res.status(400).json({
                success: false,
                message: 'Parameter userId wajib diisi'
            });
        }

        if (req.userId !== userId && req.userRole !== 'admin') {
            return res.status(403).json({
                success: false,
                message: 'Tidak punya akses untuk resource ini'
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
             INNER JOIN event_participants ep ON ep.event_id = e.id
             LEFT JOIN qrinvitation q ON q.event_id = e.id
             WHERE ep.user_id = ?
             ORDER BY e.event_date ASC, e.event_time ASC`,
            [userId]
        );

        return res.json({
            success: true,
            message: 'Daftar event peserta',
            data: rows
        });
    } catch (err) {
        console.error('GET /api/events/peserta error', err);
        res.status(500).json({
            success: false,
            message: 'Terjadi kesalahan server'
        });
    }
});

/**
 * POST /api/events/join-by-qr
 * Body: { qr_code }
 * - Cari qr_code di qrinvitation
 * - Insert ke event_participants (jika belum)
 * - Update events.participants = jumlah peserta
 * - Return event lengkap + qr_code
 */
router.post('/join-by-qr', authMiddleware, async (req, res) => {
    try {
        let { qr_code } = req.body || {};

        if (!qr_code) {
            return res.status(400).json({
                success: false,
                message: 'Field qr_code wajib diisi'
            });
        }

        // Samakan format: uppercase + trim
        qr_code = String(qr_code).trim().toUpperCase();

        // Cari event dari qr_code
        const [rows] = await pool.query(
            `SELECT 
                q.event_id,
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
             INNER JOIN events e ON e.id = q.event_id
             WHERE q.qr_code = ?
             LIMIT 1`,
            [qr_code]
        );

        if (rows.length === 0) {
            return res.status(404).json({
                success: false,
                message: 'QR code tidak valid atau event tidak ditemukan'
            });
        }

        const event = rows[0];
        const eventId = event.event_id || event.id;
        const userId = req.userId;

        // Cek sudah join atau belum
        const [existing] = await pool.query(
            `SELECT id
             FROM event_participants
             WHERE event_id = ? AND user_id = ?`,
            [eventId, userId]
        );

        if (existing.length > 0) {
            // Sudah join, balikan event apa adanya
            return res.json({
                success: true,
                message: 'Kamu sudah tergabung di event ini',
                data: event
            });
        }

        // Insert ke event_participants
        await pool.query(
            `INSERT INTO event_participants (event_id, user_id)
             VALUES (?, ?)`,
            [eventId, userId]
        );

        // Update jumlah participants di events
        await pool.query(
            `UPDATE events
             SET participants = (
                 SELECT COUNT(*) FROM event_participants WHERE event_id = ?
             )
             WHERE id = ?`,
            [eventId, eventId]
        );

        // Ambil ulang event dengan participants terbaru
        const [updatedRows] = await pool.query(
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

        const updatedEvent = updatedRows[0];

        return res.json({
            success: true,
            message: 'Berhasil bergabung ke event',
            data: updatedEvent
        });
    } catch (err) {
        console.error('POST /api/events/join-by-qr error', err);
        res.status(500).json({
            success: false,
            message: 'Terjadi kesalahan server saat join event'
        });
    }
});

module.exports = router;
