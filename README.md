# 🏫 Campus Events Management — Prototype

**Project Akhir PAM Terapan**  
Sistem manajemen event kampus untuk **Panitia** dan **Peserta**, dengan fitur pembuatan agenda acara, QR undangan, dan (ke depannya) absensi serta pengelolaan tugas.

> ⚠️ Status: **Prototype / Perancangan** — struktur, endpoint, dan UI masih bisa berubah seiring pengembangan.

---

## 🎯 Gambaran Umum

Project ini terdiri dari dua bagian besar:

- **Aplikasi Android (Panitia & Peserta)** — dibangun dengan **Kotlin + Jetpack Compose**, fokus ke antarmuka dan experience panitia/peserta.
- **Backend API (branch `backend`)** — dibangun dengan **Node.js + Express + MySQL**, menangani autentikasi, event, dan QR invitation.

Use case utama:

- Panitia membuat event
- Sistem membuat **QR Code undangan** untuk event tersebut
- QR dibagikan ke peserta (scan QR untuk bergabung ke event — *on progress*)
- Ke depan: panitia bisa mengelola absensi, pengumuman, dan task untuk peserta.

---

## ⚙️ Fitur Utama Saat Ini

### ✅ 1. Manajemen Event oleh Panitia

Panitia dapat:

- Login ke aplikasi
- Membuat event dengan data:
  - Judul event
  - Tanggal
  - Waktu
  - Lokasi
  - Status (Akan Datang / Berlangsung / Selesai)
- Melihat daftar event miliknya di **Dashboard Panitia**:
  - Filter berdasarkan status (Semua, Akan Datang, Berlangsung, Selesai)
  - Pencarian berdasarkan judul / lokasi
  - Statistik ringkas jumlah event & peserta

### ✅ 2. QR Invitation Per Event

Setiap event yang berhasil dibuat:

- Akan dibuatkan **QR invitation unik** di tabel `qrinvitation`
- Panitia bisa menekan tombol **Show QR Invite** pada kartu event
- Muncul **popup dialog** berisi:
  - QR Code undangan
  - Credential QR (`qr_code`) sebagai teks
  - Tombol **Download QR** untuk menyimpan gambar ke galeri (folder `Pictures/CampusEvents`)

QR ini nantinya yang akan digunakan peserta untuk **bergabung ke event** (fitur join masih dalam tahap perancangan).

---

## 🧪 Fitur On-Progress

### 🔄 Join Event via Scan QR (Peserta)

Rencana alur:

1. Panitia membagikan QR Invite ke peserta.
2. Peserta scan QR melalui aplikasi peserta.
3. Sistem:
   - Memvalidasi QR
   - Menghubungkan peserta ke event terkait
   - Menambahkan ke daftar peserta event

Status: **On Progress (belum fully implemented)**

---

## 📌 Fitur Coming Soon

### 🧑‍🤝‍🧑 1. Absensi Peserta

Panitia akan dapat:

- Melihat daftar peserta yang tergabung dalam event
- Melakukan check-in / check-out
- Mendapatkan rekap absensi per event

---

### 📣 2. Announcement / Pengumuman

Panitia dapat:

- Membuat pengumuman terkait event
- Mengirim informasi:
  - Perubahan jadwal
  - Perubahan lokasi
  - Informasi teknis acara
- Menampilkan pengumuman pada sisi peserta

---

### 📋 3. Task / Tugas Peserta

Panitia dapat membuat **task/tugas** untuk peserta, misalnya:

- Tugas pre-event (mengisi form, menyiapkan berkas, dsb.)
- Tugas saat event
- Sistem checklist dan status pengerjaan

---

### 🔐 4. Private Events

Mode event privat yang direncanakan:

1. **Password-Protected Event**  
   - Panitia memberikan password event  
   - Peserta yang scan QR harus mengisi password terlebih dahulu

2. **Manual Approval Event**  
   - Peserta tidak langsung auto-join setelah scan QR  
   - Panitia bisa menyetujui atau menolak permintaan bergabung  
   - Cocok untuk event terbatas / khusus

---

## 🗄️ Struktur Database (Ringkasan)

### Tabel `events`

| Kolom       | Tipe    | Deskripsi                              |
|------------|---------|----------------------------------------|
| id         | INT     | Primary key                            |
| created_by | INT     | ID user panitia                        |
| title      | VARCHAR | Judul event                            |
| event_date | DATE    | Tanggal event                          |
| event_time | TIME    | Waktu event                            |
| location   | VARCHAR | Lokasi event                           |
| status     | VARCHAR | Akan Datang / Berlangsung / Selesai    |

---

### Tabel `qrinvitation`

CREATE TABLE `qrinvitation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_id` int(11) NOT NULL,
  `qr_code` varchar(32) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_qrinvitation_qr_code` (`qr_code`),
  KEY `idx_qrinvitation_event_id` (`event_id`),
  CONSTRAINT `fk_qrinvitation_events`
    FOREIGN KEY (`event_id`)
    REFERENCES `events` (`id`)
    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

---

## 🧱 Tech Stack

### 🖥️ Backend (branch `backend`)

- Node.js
- Express.js
- MySQL (via `mysql2`)
- JSON Web Token (JWT) untuk autentikasi
- Struktur endpoint RESTful

Contoh route utama:

- POST /api/auth/login
- POST /api/auth/register
- GET  /api/events/panitia?createdBy={userId}
- POST /api/events (create event + generate QR)

---

### 📱 Android App

- Kotlin
- Jetpack Compose
- MVVM + ViewModel + StateFlow
- Retrofit untuk HTTP request
- Coroutine (viewModelScope) untuk async operation
- Manual polling (mis. setiap 3 detik / 10 detik) untuk refresh event list di dashboard

---

## 🚀 Cara Menjalankan

### 1. Backend

Pastikan sudah membuat database MySQL dan mengisi konfigurasi di `.env`.

```bash
# Install dependency
npm install

# Jalankan dalam mode development
npm run dev
```

Default server:

http://localhost:3000

---

### 2. Android App

1. Buka folder project di Android Studio.
2. Pastikan permission berikut sudah diaktifkan di `AndroidManifest.xml`:

   <uses-permission android:name="android.permission.INTERNET" />
   <uses-permission
       android:name="android.permission.WRITE_EXTERNAL_STORAGE"
       android:maxSdkVersion="28" />

3. Jalankan di emulator atau device fisik (disarankan device agar QR & saving image berjalan maksimal).

## 🛤️ Roadmap Pengembangan

- [x] Panitia membuat event
- [x] Generate & tampilkan QR undangan
- [x] Download QR ke galeri
- [x] Dashboard panitia dengan filter & pencarian
- [x] Join event lewat scan QR (peserta)
- [ ] Manajemen peserta per event
- [ ] Sistem absensi (check-in / check-out)
- [ ] Pengumuman (announcement)
- [ ] Task/tugas peserta
- [ ] Mode private event (password / manual approval)
- [ ] Real-time update (WebSocket / push)
- [ ] Panel admin global

---

## 🧪 Status

Project ini masih skala prototype / perancangan:
Fokus utama saat ini adalah:

- Merapikan alur kerja panitia
- Menjaga konsistensi antara Android app dan backend
- Menyiapkan pondasi untuk fitur peserta dan absensi

---

## ✨ Catatan Penutup

Prototype ini dirancang untuk menjawab kebutuhan nyata:
mengelola event kampus dengan cara yang lebih modern, rapi, dan terintegrasi antara panitia dan peserta.

Masih banyak ruang untuk dikembangkan: dari sisi desain UI, keamanan, hingga skalabilitas backend.
