const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const pool = require('../db');

// POST /api/auth/register
router.post('/register', async (req, res) => {
  try {
    const { name, email, password, role } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({
        success: false,
        message: 'Nama, email, dan password wajib diisi'
      });
    }

    const [exists] = await pool.query(
      'SELECT id FROM users WHERE email = ?',
      [email]
    );
    if (exists.length > 0) {
      return res.status(400).json({
        success: false,
        message: 'Email sudah terdaftar'
      });
    }

    const hash = await bcrypt.hash(password, 10);
    const userRole = role && ['admin', 'panitia', 'peserta'].includes(role)
      ? role
      : 'peserta';

    const [result] = await pool.query(
      'INSERT INTO users (name, email, password_hash, role) VALUES (?,?,?,?)',
      [name, email, hash, userRole]
    );

    const user = {
      id: result.insertId,
      name,
      email,
      role: userRole
    };

    return res.json({
      success: true,
      message: 'Registrasi berhasil',
      user
    });
  } catch (err) {
    console.error('REGISTER error', err);
    res.status(500).json({
      success: false,
      message: 'Terjadi kesalahan server'
    });
  }
});

// POST /api/auth/login
router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({
        success: false,
        message: 'Email dan password wajib diisi'
      });
    }

    const [rows] = await pool.query(
      'SELECT id, name, email, password_hash, role FROM users WHERE email = ?',
      [email]
    );

    if (rows.length === 0) {
      return res.status(401).json({
        success: false,
        message: 'Email atau password salah'
      });
    }

    const userRow = rows[0];
    const match = await bcrypt.compare(password, userRow.password_hash);

    if (!match) {
      return res.status(401).json({
        success: false,
        message: 'Email atau password salah'
      });
    }

    const token = jwt.sign(
      { id: userRow.id, role: userRow.role },
      process.env.JWT_SECRET || 'devsecret',
      { expiresIn: '12h' }
    );

    const user = {
      id: userRow.id,
      name: userRow.name,
      email: userRow.email,
      role: userRow.role
    };

    return res.json({
      success: true,
      message: 'Login berhasil',
      token,
      user
    });
  } catch (err) {
    console.error('LOGIN error', err);
    res.status(500).json({
      success: false,
      message: 'Terjadi kesalahan server'
    });
  }
});

module.exports = router;
