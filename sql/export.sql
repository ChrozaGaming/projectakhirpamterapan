-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 16, 2025 at 04:38 PM
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
(1, 2, 'fernando', '2025-11-22', '20:08:00', 'Aula', 'Akan Datang', 0, '2025-11-16 10:08:53', '2025-11-16 10:08:53'),
(2, 1, 'Tes', '2025-11-26', '21:28:00', 'Aula', 'Akan Datang', 0, '2025-11-16 10:28:17', '2025-11-16 10:28:17'),
(3, 2, 'Te', '2025-11-19', '21:49:00', 'Aula', 'Akan Datang', 0, '2025-11-16 14:04:54', '2025-11-16 14:04:54'),
(4, 2, 'aa', '2025-11-21', '11:16:00', 'aaa', 'Akan Datang', 0, '2025-11-16 14:05:40', '2025-11-16 14:05:40');

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
(1, 1, 'HU10QPPWUHFGJ1', '2025-11-16 10:08:53'),
(3, 3, 'JLPQSMNBD12GUE', '2025-11-16 14:04:54'),
(4, 4, 'K7HTR1TF532KST', '2025-11-16 14:05:40');

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
(1, 'Hilmy Raihan Alkindy', 'hilmyraihankindy@gmail.com', '$2b$10$YPFc8LSx1cfad42kARrk5.M3Y.ww.eKX22dsxIlmc89g2JIm03x/q', 'panitia', '2025-11-16 08:33:11'),
(2, 'Fernando Putra', 'fernando@gmail.com', '$2a$10$7fjXEPOvrwzi4.qc2p9/.uSprG/MUmHpOso9OWOaXAOVJvAK8Uttq', 'peserta', '2025-11-16 09:02:35');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `qrinvitation`
--
ALTER TABLE `qrinvitation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

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
-- Constraints for table `qrinvitation`
--
ALTER TABLE `qrinvitation`
  ADD CONSTRAINT `fk_qrinvitation_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
