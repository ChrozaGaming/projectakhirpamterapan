-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 19, 2025 at 06:29 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `campus_events`
--

-- --------------------------------------------------------

--
-- Table structure for table `events`
--

CREATE TABLE `events` (
  `id` int(11) NOT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `title` varchar(255) NOT NULL,
  `event_date` date NOT NULL,
  `event_time` time NOT NULL,
  `location` varchar(255) NOT NULL,
  `status` enum('Akan Datang','Berlangsung','Selesai') NOT NULL DEFAULT 'Akan Datang',
  `participants` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`id`, `created_by`, `title`, `event_date`, `event_time`, `location`, `status`, `participants`, `created_at`, `updated_at`) VALUES
(1, 1, 'Tess', '2025-11-20', '10:28:00', 'Aula', 'Akan Datang', 2, '2025-11-19 03:28:33', '2025-11-19 04:34:43'),
(2, 2, 'asada', '2025-11-19', '05:41:00', 'Aula', 'Berlangsung', 2, '2025-11-19 05:41:56', '2025-11-19 06:28:13'),
(3, 2, 'xxx', '2025-11-19', '18:03:00', 'Aula', 'Berlangsung', 1, '2025-11-19 07:03:29', '2025-11-19 07:03:52'),
(4, 2, 'helloo', '2025-11-19', '17:25:00', 'Aulaa', 'Berlangsung', 1, '2025-11-19 10:25:17', '2025-11-19 10:26:04'),
(5, 2, 'deded', '2025-11-19', '04:15:00', 'Tess', 'Berlangsung', 1, '2025-11-19 16:15:16', '2025-11-19 16:16:00'),
(6, 2, 'kukuk', '2025-11-20', '00:25:00', 'Aulaa', 'Berlangsung', 0, '2025-11-19 17:26:04', '2025-11-19 17:26:04');

-- --------------------------------------------------------

--
-- Table structure for table `event_announcements`
--

CREATE TABLE `event_announcements` (
  `id` int(11) UNSIGNED NOT NULL,
  `event_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `created_by` int(10) UNSIGNED NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `event_announcements`
--

INSERT INTO `event_announcements` (`id`, `event_id`, `title`, `body`, `created_by`, `created_at`) VALUES
(8, 1, 'Pembukaan Seminar Nasional Kebudayaan Nusantara', 'Kami mengundang seluruh mahasiswa untuk hadir pada pembukaan Seminar Nasional Kebudayaan Nusantara yang akan berlangsung di Aula Utama kampus pada hari Jumat, 6 Desember pukul 09.00. Harap hadir tepat waktu.', 2, '2025-11-19 15:36:09'),
(9, 1, 'Pengumpulan Makalah Peserta Seminar', 'Bagi peserta yang akan mempresentasikan karya tulis, batas akhir pengumpulan makalah adalah tanggal 2 Desember pukul 23.59 melalui portal resmi seminar. Makalah yang terlambat tidak akan diproses.', 1, '2025-11-19 15:36:09'),
(10, 2, 'Pendaftaran Workshop Karier dan Networking Alumni', 'Indonesia Society membuka pendaftaran untuk Workshop Karier dan Networking Alumni yang akan diadakan pada tanggal 12 Desember. Peserta akan mendapatkan e-sertifikat. Kuota terbatas untuk 80 orang.', 3, '2025-11-19 15:36:09'),
(11, 2, 'Perubahan Lokasi Workshop Karier', 'Dikarenakan renovasi ruang seminar, lokasi workshop dipindahkan ke Gedung F lantai 3, ruang F301. Jadwal dan agenda kegiatan tetap sama.', 1, '2025-11-19 15:36:09'),
(12, 3, 'Pengumuman Kurasi Pameran Foto', 'Daftar peserta yang lolos kurasi untuk Pameran Foto “Indonesia di Mata Dunia” telah diumumkan di website resmi organisasi. Selamat bagi peserta yang terpilih!', 2, '2025-11-19 15:36:09'),
(13, 4, 'Pendaftaran Relawan Bakti Sosial Kampus Peduli', 'Indonesia Society membuka kesempatan bagi mahasiswa yang ingin bergabung sebagai relawan dalam kegiatan Bakti Sosial Kampus Peduli pada tanggal 20 Desember. Pendaftaran dibuka hingga 15 Desember.', 3, '2025-11-19 15:36:09'),
(14, 4, 'Arahan Teknis Relawan Sebelum Kegiatan', 'Briefing wajib untuk seluruh relawan akan dilaksanakan pada 18 Desember pukul 16.00 di ruang rapat Student Center. Harap hadir tepat waktu dan mengenakan pakaian rapi.', 2, '2025-11-19 15:36:09'),
(15, 2, 'aaa', 'aaaa', 2, '2025-11-19 16:11:01');

-- --------------------------------------------------------

--
-- Table structure for table `event_attendance`
--

CREATE TABLE `event_attendance` (
  `id` int(11) UNSIGNED NOT NULL,
  `event_id` int(11) NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `status` enum('hadir','izin','alfa') NOT NULL DEFAULT 'hadir',
  `checked_in_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `event_attendance`
--

INSERT INTO `event_attendance` (`id`, `event_id`, `user_id`, `status`, `checked_in_at`) VALUES
(1, 2, 2, 'hadir', '2025-11-19 17:12:25'),
(6, 3, 2, 'hadir', '2025-11-19 15:24:15'),
(7, 1, 2, 'hadir', '2025-11-19 15:25:57');

-- --------------------------------------------------------

--
-- Table structure for table `event_participants`
--

CREATE TABLE `event_participants` (
  `id` int(11) UNSIGNED NOT NULL,
  `event_id` int(11) NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `role_in_event` enum('peserta','panitia') NOT NULL DEFAULT 'peserta',
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `event_participants`
--

INSERT INTO `event_participants` (`id`, `event_id`, `user_id`, `role_in_event`, `joined_at`) VALUES
(1, 1, 1, 'peserta', '2025-11-19 04:31:10'),
(2, 1, 2, 'peserta', '2025-11-19 04:34:43'),
(3, 2, 1, 'peserta', '2025-11-19 05:42:52'),
(4, 2, 2, 'peserta', '2025-11-19 06:28:13'),
(5, 3, 2, 'peserta', '2025-11-19 07:03:52'),
(6, 4, 1, 'peserta', '2025-11-19 10:26:04'),
(7, 5, 1, 'peserta', '2025-11-19 16:16:00');

-- --------------------------------------------------------

--
-- Table structure for table `qrinvitation`
--

CREATE TABLE `qrinvitation` (
  `id` int(11) NOT NULL,
  `event_id` int(11) NOT NULL,
  `qr_code` varchar(32) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `qrinvitation`
--

INSERT INTO `qrinvitation` (`id`, `event_id`, `qr_code`, `created_at`) VALUES
(1, 1, 'J8AP7JLP284YTU', '2025-11-19 03:28:33'),
(2, 2, 'Q1LU0LD9R6S9A1', '2025-11-19 05:41:56'),
(3, 3, 'WN498KRVVJPO4X', '2025-11-19 07:03:29'),
(4, 4, '5WXNJT6B6083WR', '2025-11-19 10:25:17'),
(5, 5, 'O0B9WDSHLJBY8I', '2025-11-19 16:15:16'),
(6, 6, '47SCL1CI16VRLX', '2025-11-19 17:26:04');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('admin','panitia','peserta') NOT NULL DEFAULT 'peserta',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `email`, `password_hash`, `role`, `created_at`) VALUES
(1, 'Hilmy Raihan Alkindy', 'hilmy@gmail.com', '$2a$10$YWhEaL/MT4w4OEQzEE7RUOoEv.6ITcjdGujr4upLTq9NFOe7k3/e2', 'peserta', '2025-11-19 03:28:12'),
(2, 'Fernando Putra', 'fernando@gmail.com', '$2a$10$WKoKaTdhhqxjBkEvprCr3eWlERcbJZaKCoL.WUrXrQTUyfRObF/Iq', 'peserta', '2025-11-19 04:34:07');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `events`
--
ALTER TABLE `events`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_events_created_by` (`created_by`);

--
-- Indexes for table `event_announcements`
--
ALTER TABLE `event_announcements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_event_announcements_event_id` (`event_id`),
  ADD KEY `idx_event_announcements_created_by` (`created_by`);

--
-- Indexes for table `event_attendance`
--
ALTER TABLE `event_attendance`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_event_attendance_event_user` (`event_id`,`user_id`),
  ADD KEY `idx_event_attendance_user_id` (`user_id`);

--
-- Indexes for table `event_participants`
--
ALTER TABLE `event_participants`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_event_participants_event_user` (`event_id`,`user_id`),
  ADD KEY `idx_event_participants_user_id` (`user_id`);

--
-- Indexes for table `qrinvitation`
--
ALTER TABLE `qrinvitation`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_qrinvitation_qr_code` (`qr_code`),
  ADD KEY `idx_qrinvitation_event_id` (`event_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `events`
--
ALTER TABLE `events`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `event_announcements`
--
ALTER TABLE `event_announcements`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `event_attendance`
--
ALTER TABLE `event_attendance`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `event_participants`
--
ALTER TABLE `event_participants`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `qrinvitation`
--
ALTER TABLE `qrinvitation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `events`
--
ALTER TABLE `events`
  ADD CONSTRAINT `fk_events_users` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `event_announcements`
--
ALTER TABLE `event_announcements`
  ADD CONSTRAINT `fk_event_announcements_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_event_announcements_users` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `event_attendance`
--
ALTER TABLE `event_attendance`
  ADD CONSTRAINT `fk_event_attendance_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_event_attendance_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `event_participants`
--
ALTER TABLE `event_participants`
  ADD CONSTRAINT `fk_event_participants_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_event_participants_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `qrinvitation`
--
ALTER TABLE `qrinvitation`
  ADD CONSTRAINT `fk_qrinvitation_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
