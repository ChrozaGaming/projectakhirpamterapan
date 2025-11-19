CREATE DATABASE IF NOT EXISTS campus_events CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE campus_events;

-- campus_events.users definition

CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('admin','panitia','peserta') NOT NULL DEFAULT 'peserta',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- campus_events.events definition

CREATE TABLE `events` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `created_by` int(10) unsigned NOT NULL,
  `title` varchar(255) NOT NULL,
  `event_date` date NOT NULL,
  `event_time` time NOT NULL,
  `location` varchar(255) NOT NULL,
  `status` enum('Akan Datang','Berlangsung','Selesai') NOT NULL DEFAULT 'Akan Datang',
  `participants` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_events_created_by` (`created_by`),
  CONSTRAINT `fk_events_users` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- campus_events.qrinvitation definition

CREATE TABLE `qrinvitation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_id` int(11) NOT NULL,
  `qr_code` varchar(32) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_qrinvitation_qr_code` (`qr_code`),
  KEY `idx_qrinvitation_event_id` (`event_id`),
  CONSTRAINT `fk_qrinvitation_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- campus_events.event_announcements definition

CREATE TABLE `event_announcements` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `created_by` int(10) unsigned NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_event_announcements_event_id` (`event_id`),
  KEY `idx_event_announcements_created_by` (`created_by`),
  CONSTRAINT `fk_event_announcements_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_event_announcements_users` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- campus_events.event_attendance definition

CREATE TABLE `event_attendance` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` int(11) NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `status` enum('hadir','izin','alfa') NOT NULL DEFAULT 'hadir',
  `checked_in_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_event_attendance_event_user` (`event_id`,`user_id`),
  KEY `idx_event_attendance_user_id` (`user_id`),
  CONSTRAINT `fk_event_attendance_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_event_attendance_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- campus_events.event_participants definition

CREATE TABLE `event_participants` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `event_id` int(11) NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `role_in_event` enum('peserta','panitia') NOT NULL DEFAULT 'peserta',
  `joined_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_event_participants_event_user` (`event_id`,`user_id`),
  KEY `idx_event_participants_user_id` (`user_id`),
  CONSTRAINT `fk_event_participants_events` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_event_participants_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;