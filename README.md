Roady - Aplikasi Deteksi Kerusakan Jalan
Sebuah aplikasi Android cerdas yang menggunakan Computer Vision berbasis AI untuk mendeteksi, menganalisis, dan mencatat kerusakan jalan dari gambar, serta memberikan wawasan dan rekomendasi perbaikan secara otomatis.

&lt;p align="center">
&lt;img src="https://github.com/Winskiii/FP-PPB/assets/91456950/d24d27da-ae9d-472d-be39-1662a6b297b8" alt="Roady App Demo">
&lt;/p>

✨ Fitur Utama
🔍 Deteksi Kerusakan Berbasis AI
Deteksi kerusakan jalan secara real-time menggunakan Roboflow API.
Mendukung berbagai tipe kerusakan: Pothole (Jalan Berlubang), Patch (Tambalan), Fatigue Crack (Retak).
Filter tingkat keyakinan (confidence threshold) yang dapat diatur oleh pengguna untuk menyaring hasil deteksi.
🤖 Analisis & Rekomendasi AI
Terintegrasi dengan Google Gemini API untuk analisis kerusakan yang mendalam.
Memberikan rekomendasi perbaikan dan penjelasan potensi bahaya dalam Bahasa Indonesia.
Menghasilkan laporan analisis profesional untuk setiap kerusakan yang berhasil dideteksi.
📍 Layanan Lokasi & Peta
Integrasi Google Maps Platform untuk deteksi lokasi berbasis GPS dan menampilkan peta interaktif.
Fitur pencarian alamat dan reverse geocoding untuk mendapatkan nama jalan secara otomatis.
📱 Arsitektur Android Modern
Menggunakan Single-Activity Architecture dengan navigasi berbasis Fragment.
BottomNavigationView dengan empat bagian utama: Devices, Camera, History, dan Profile.
Implementasi komponen Material Design untuk antarmuka pengguna yang bersih dan modern.
Latar belakang navbar dengan gradasi warna yang dinamis sesuai tab yang aktif.
📊 Manajemen Riwayat (History)
Penyimpanan data hasil deteksi secara lokal dan permanen menggunakan database Room.
Menyimpan riwayat deteksi lengkap, termasuk gambar asli dan semua data analisis.
Menampilkan seluruh riwayat dalam daftar yang efisien menggunakan RecyclerView.
🛠️ Tumpukan Teknologi (Tech Stack)
Teknologi Inti
Bahasa: Java
Platform: Android (API 24+)
Arsitektur: Single Activity + Multiple Fragments
Database: Room Persistence Library
Networking: Volley HTTP Library
API & Layanan Eksternal
Roboflow API: Untuk computer vision deteksi objek.
Google Gemini API: Untuk analisis bahasa natural dan pembuatan rekomendasi.
Google Maps API: Untuk layanan peta dan geolokasi.
Izin (Permissions) yang Diperlukan
Aplikasi ini membutuhkan izin berikut di AndroidManifest.xml:

CAMERA: Untuk mengambil gambar kerusakan jalan.
ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION: Untuk layanan lokasi GPS.
READ_MEDIA_IMAGES / READ_EXTERNAL_STORAGE: Untuk mengakses gambar dari galeri.
INTERNET: Untuk komunikasi dengan Roboflow dan Gemini API.
🚀 Cara Menjalankan Proyek
Prasyarat
Android Studio Iguana | 2023.2.1 atau yang lebih baru.
Android SDK API 24 atau yang lebih tinggi.
API Key untuk Google Maps.
API Key untuk Roboflow.
API Key untuk Google Gemini AI.
Konfigurasi
Clone Repository

Bash

git clone https://github.com/Winskiii/FP-PPB.git
cd FP-PPB
Buka Proyek
Buka direktori proyek menggunakan Android Studio dan tunggu hingga Gradle selesai melakukan sinkronisasi.

Setup API Keys (Langkah Wajib)

Google Maps: Buka app/src/main/AndroidManifest.xml dan masukkan API Key Anda di dalam tag <meta-data> untuk Google Maps.
Roboflow & Gemini: Buka file app/src/main/java/app/nbs/kam/DevicesFragment.java dan ganti nilai placeholder berikut dengan Key Anda:
Java

// Ganti dengan URL dan API Key dari Roboflow
private static final String ROBOFLOW_API_URL = "https://detect.roboflow.com/YOUR_PROJECT/YOUR_VERSION?api_key=YOUR_ROBOFLOW_KEY";

// Ganti dengan API Key Anda dari Google AI Studio
private static final String GEMINI_API_KEY = "YOUR_GEMINI_API_KEY";
Build dan Jalankan
Klik tombol 'Run' di Android Studio untuk membangun dan menginstal aplikasi pada emulator atau perangkat fisik Anda.

🏗️ Struktur dan Arsitektur Aplikasi
Aplikasi ini mengikuti pola arsitektur modern yang memisahkan tampilan dan logika untuk setiap fitur.

MainActivity (Host Navigasi & Koordinator Intent Gambar)
│
└── FrameLayout (Wadah untuk Fragment)
    ├── DevicesFragment (Fitur Inti: Peta, Deteksi, Analisis, Simpan History)
    ├── CameraFragment (Antarmuka Pengambilan Gambar)
    ├── HistoryFragment (Menampilkan Daftar History dari Database Room)
    └── ProfileFragment (Info Pengguna & Logout)
Alur Data Utama:
Ambil Gambar → MainActivity → Kirim URI ke DevicesFragment → Roboflow API → Gemini API → Simpan ke Room Database.
