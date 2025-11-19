const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');

dotenv.config();

const authRoutes = require('./routes/auth');
const eventRoutes = require('./routes/events');

const app = express();

// Middleware global
app.use(cors());
app.use(express.json());

// Root check
app.get('/', (req, res) => {
  res.json({ message: 'Campus Events API' });
});

// Routing utama
app.use('/api/auth', authRoutes);
app.use('/api/events', eventRoutes);

// 404 handler JSON (opsional, biar ga keluar HTML kalau salah path)
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint tidak ditemukan',
  });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
