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
 * Mengambil daftar event milik panitia tertentu + qr_code dari qrinvitation
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
 * - Return 1 objek event + qr_code di root (sesuai dengan Retrofit kamu)
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

        // Penting: balik langsung 1 objek event (TANPA wrapper success/data)
        // supaya cocok dengan ApiService.createEvent(): Event
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

module.exports = router;
