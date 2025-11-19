-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 19, 2025 at 08:21 AM
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
(3, 2, 'xxx', '2025-11-19', '18:03:00', 'Aula', 'Berlangsung', 1, '2025-11-19 07:03:29', '2025-11-19 07:03:52');

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
(5, 3, 2, 'peserta', '2025-11-19 07:03:52');

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
(3, 3, 'WN498KRVVJPO4X', '2025-11-19 07:03:29');

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `event_participants`
--
ALTER TABLE `event_participants`
  MODIFY `id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `qrinvitation`
--
ALTER TABLE `qrinvitation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

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
