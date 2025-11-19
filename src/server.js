// src/server.js
const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');

dotenv.config();

const authRoutes = require('./routes/auth');
const eventRoutes = require('./routes/events');

const app = express();

/* ============================
   MIDDLEWARE GLOBAL
   ============================ */

// CORS – sementara dibuka semua origin (cocok untuk development / Android emulator)
app.use(cors({
  origin: '*',
}));

// Supaya bisa baca JSON body
app.use(express.json());

// (Opsional) kalau nanti butuh form-urlencoded:
// app.use(express.urlencoded({ extended: true }));


/* ============================
   ROUTES
   ============================ */

// Root check / health check
app.get('/', (req, res) => {
  res.json({
    success: true,
    message: 'Campus Events API Running',
  });
});

// Routing utama
app.use('/api/auth', authRoutes);
app.use('/api/events', eventRoutes);

// 404 handler JSON (kalau endpoint tidak ketemu)
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint tidak ditemukan',
  });
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
