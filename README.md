Roady - Aplikasi Deteksi Kerusakan Jalan Berbasis Android
&lt;p align="center">
&lt;img src="https://i.imgur.com/your-app-icon.png" width="150" alt="Roady App Icon">  &lt;/p>

Roady adalah sebuah aplikasi Android inovatif yang dirancang untuk membantu pengguna mendeteksi, menganalisis, dan mencatat kerusakan jalan secara otomatis. Dengan memanfaatkan kamera perangkat, aplikasi ini dapat mengidentifikasi jenis kerusakan, memberikan analisis menggunakan AI, dan menyimpan riwayat temuan untuk dokumentasi.

Proyek ini merupakan implementasi dari konsep Pemrograman Perangkat Bergerak yang mengintegrasikan berbagai teknologi modern seperti pengolahan gambar, layanan peta, database lokal, dan AI generatif.

📸 Screenshot Aplikasi
Halaman Utama (Peta)	Hasil Deteksi	Halaman History
&lt;img src="https://i.imgur.com/your-map-screen.jpg" width="250">	&lt;img src="https://i.imgur.com/your-detection-screen.jpg" width="250">	&lt;img src="https://i.imgur.com/your-history-screen.jpg" width="250">
Halaman Profile	Halaman Login
&lt;img src="https://i.imgur.com/your-profile-screen.jpg" width="250">	&lt;img src="https://i.imgur.com/your-login-screen.jpg" width="250">

Export to Sheets
✨ Fitur Utama
Autentikasi Pengguna: Sistem Register dan Login untuk mengelola akses pengguna.
Deteksi Kerusakan Jalan: Menggunakan kamera atau galeri untuk mengambil gambar, yang kemudian dikirim ke Roboflow API untuk deteksi objek kerusakan jalan (Pothole, Patch, Fatigue Crack).
Analisis Berbasis AI: Hasil deteksi dari Roboflow (jenis, skala, dan tingkat keyakinan) dikirimkan ke Google Gemini API untuk mendapatkan analisis dan deskripsi dalam bahasa natural.
Tampilan Peta Interaktif: Menggunakan Google Maps Platform untuk menampilkan lokasi pengguna saat ini dan mencari alamat.
Tampilan Hasil Detail: Menampilkan gambar kerusakan beserta data analisis lengkap seperti Nama Jalan, Tipe Kerusakan, Skala Kerusakan (Major, Moderate, Minimum), dan Level Keyakinan.
UI Modern dan Responsif:
Navigasi berbasis Fragment (Devices, Camera, History, Profile).
Penggunaan CardView untuk presentasi data yang bersih.
Latar belakang navbar dengan gradasi warna yang dinamis sesuai tab yang aktif.
Fitur History:
Setiap hasil deteksi yang berhasil akan disimpan secara lokal di perangkat.
Menampilkan seluruh riwayat deteksi dalam daftar yang bisa di-scroll menggunakan RecyclerView.
Gambar dan data riwayat tersimpan secara permanen menggunakan Room Database dan penyimpanan internal aplikasi.
🛠️ Teknologi dan Library yang Digunakan
Bahasa: Java
Arsitektur: Single-Activity Architecture dengan multiple Fragments.
UI:
Android XML Layouts
Material Design Components (BottomNavigationView, CardView, SeekBar, MaterialButton)
Networking:
Volley: Untuk melakukan permintaan HTTP ke Roboflow dan Gemini API.
Database Lokal:
Room Persistence Library: Sebagai abstraksi di atas SQLite untuk menyimpan, membaca, dan mengelola data riwayat deteksi.
Layanan Eksternal:
Google Maps Platform SDK: Untuk menampilkan peta dan layanan lokasi.
Roboflow API: Untuk inferensi model deteksi objek.
Google Gemini API: Untuk analisis teks dan chatbot assistant.
Asynchronous Tasks:
ExecutorService: Untuk menjalankan operasi database di background thread agar tidak memblokir UI.
🏗️ Arsitektur & Alur Kerja Aplikasi
Aplikasi ini menggunakan MainActivity sebagai host tunggal yang mengelola BottomNavigationView dan wadah FrameLayout untuk menampung berbagai Fragment.

Alur Kerja Deteksi:

Pengguna memilih gambar dari CameraFragment.
MainActivity menerima gambar, menyimpannya ke cache, dan mengirimkan alamat (URI) file ke DevicesFragment.
DevicesFragment menerima URI, memuat gambar, dan menampilkannya.
Gambar diubah ukurannya dan dikirim ke Roboflow API.
Setelah menerima hasil deteksi dari Roboflow, DevicesFragment: a. Menampilkan data deteksi (tipe, skala, keyakinan) di UI. b. Mengirim data tersebut ke Gemini API untuk dianalisis. c. Menyimpan gambar beserta data deteksi ke Room Database.
Respons dari Gemini ditampilkan di kartu AI Assistant.
Data yang tersimpan dapat dilihat kapan saja di HistoryFragment.
🚀 Cara Menjalankan Proyek
Untuk menjalankan proyek ini di Android Studio, ikuti langkah-langkah berikut:

Clone Repository
Bash

git clone https://github.com/Winskiii/kam.git
Buka proyek menggunakan Android Studio.
Tunggu hingga proses Gradle Sync selesai.
Masukkan API Keys Anda (Langkah Wajib)
Buka file app/src/main/java/app/nbs/kam/DevicesFragment.java.

Cari dan ganti nilai konstanta berikut dengan API Key Anda sendiri:

Java

// Ganti dengan API Key Anda dari Roboflow
private static final String ROBOFLOW_API_URL = "https://detect.roboflow.com/YOUR_PROJECT_ID/YOUR_VERSION?api_key=YOUR_ROBOFLOW_KEY";

// Ganti dengan API Key Anda dari Google AI Studio
private static final String GEMINI_API_KEY = "YOUR_GEMINI_API_KEY";
Pastikan Anda telah mengaktifkan Vertex AI API di Google Cloud Console untuk proyek yang terkait dengan Gemini API Key Anda.
Build dan Jalankan aplikasi pada emulator atau perangkat fisik.
