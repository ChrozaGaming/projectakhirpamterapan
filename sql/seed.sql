USE campus_events;

INSERT INTO `users` (`name`, `email`, `password_hash`, `role`)
VALUES (
  'Hilmy Raihan Alkindy',
  'hilmyraihankindy@gmail.com',
  '$2b$10$YPFc8LSx1cfad42kARrk5.M3Y.ww.eKX22dsxIlmc89g2JIm03x/q',
  'panitia'
);

INSERT INTO `events`
(`created_by`, `title`, `event_date`, `event_time`, `location`, `status`, `participants`)
VALUES
(1, 'Briefing Utama RAJA Brawijaya 2025', '2025-08-10', '19:00:00', 'Gedung G FILKOM', 'Akan Datang', 45),
(1, 'Rapat Koordinasi Divisi Konsumsi', '2025-08-01', '20:00:00', 'Ruang Rapat Lantai 2 FILKOM', 'Berlangsung', 18),
(1, 'Evaluasi Awal Panitia Inti', '2025-07-20', '19:30:00', 'Zoom Meeting', 'Selesai', 32),
(1, 'Gladi Kotor Opening Ceremony', '2025-08-14', '08:00:00', 'Lapangan Rektorat UB', 'Akan Datang', 120),
(1, 'Monitoring Registrasi Peserta Hari 1', '2025-08-02', '09:00:00', 'Posko RAJA Brawijaya', 'Berlangsung', 75);
