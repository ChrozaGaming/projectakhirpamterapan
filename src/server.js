<<<<<<< HEAD

=======
// src/server.js
>>>>>>> e1b225ffa7ea496cef74ff7231a1044b473216ff
const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');

dotenv.config();

const authRoutes = require('./routes/auth');
const eventRoutes = require('./routes/events');

const app = express();

<<<<<<< HEAD
// Middleware global
app.use(cors());
app.use(express.json());

// Root check
app.get('/', (req, res) => {
  res.json({ message: 'Campus Events API' });
=======
/* ============================
   MIDDLEWARE GLOBAL
   ============================ */

// CORS – sementara dibuka semua origin (development)
app.use(cors({
  origin: '*',
}));

// Supaya bisa baca JSON body
app.use(express.json());

/* ============================
   ROUTES
   ============================ */

// Root check / health check
app.get('/', (req, res) => {
  res.json({
    success: true,
    message: 'Campus Events API Running',
  });
>>>>>>> e1b225ffa7ea496cef74ff7231a1044b473216ff
});

// Routing utama
app.use('/api/auth', authRoutes);
app.use('/api/events', eventRoutes);

<<<<<<< HEAD
// 404 handler JSON (opsional, biar ga keluar HTML kalau salah path)
=======
// 404 handler JSON (kalau endpoint tidak ketemu)
>>>>>>> e1b225ffa7ea496cef74ff7231a1044b473216ff
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint tidak ditemukan',
  });
<<<<<<< HEAD
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});

=======
});

/* ============================
   START SERVER
   ============================ */

const PORT = process.env.PORT || 3000;
// HOST 0.0.0.0 supaya bisa diakses dari Android emulator / perangkat lain di jaringan
const HOST = process.env.HOST || '0.0.0.0';

app.listen(PORT, HOST, () => {
  console.log(`🔥 Server running on http://${HOST}:${PORT}`);
});

module.exports = app;
>>>>>>> e1b225ffa7ea496cef74ff7231a1044b473216ff
