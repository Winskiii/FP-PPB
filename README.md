<![Component 1](https://github.com/user-attachments/assets/d448f526-0877-431e-bf48-63b3afec8e65)
div align="center">
  <h1><b>Roady - Road Damage Detection</b></h1>
  <p><i>Aplikasi Android cerdas yang menggunakan Computer Vision bertenaga AI untuk mendeteksi dan menganalisis kerusakan jalan dari gambar, serta memberikan wawasan dan rekomendasi perbaikan.</i></p>

  <p>
    <img src="https://img.shields.io/badge/Platform-Android-green?style=for-the-badge&logo=android" alt="Platform Android">
    <img src="https://img.shields.io/badge/API-24%2B-blue?style=for-the-badge&logo=android" alt="API 24+">
    <img src="https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=java" alt="Language Java">
    <img src="https://img.shields.io/badge/License-MIT-purple?style=for-the-badge" alt="License MIT">
  </p>
  <br>
</div>

---

## ✨ Fitur Utama

-   **🔍 Deteksi Kerusakan Berbasis AI**
    -   Deteksi kerusakan jalan secara *real-time* menggunakan **Roboflow API**.
    -   Mendukung berbagai jenis kerusakan: lubang (*potholes*), tambalan (*patches*), dan retak (*fatigue cracks*).
    -   Filter tingkat kepercayaan (*confidence threshold*) yang dapat dikonfigurasi.

-   **🤖 Analisis & Rekomendasi AI**
    -   Terintegrasi dengan **Google Gemini AI** untuk analisis kerusakan yang mendalam.
    -   Rekomendasi pemeliharaan kontekstual dalam Bahasa Indonesia.
    -   Menghasilkan laporan analisis profesional untuk setiap kerusakan yang terdeteksi.

-   **📍 Layanan Lokasi**
    -   Deteksi lokasi berbasis GPS dengan integrasi **Google Maps**.
    -   Fitur pencarian alamat dan *reverse geocoding*.
    -   Antarmuka peta interaktif untuk melihat konteks lokasi kerusakan.

-   **📱 Arsitektur Android Modern**
    -   Arsitektur *Single-Activity* dengan navigasi berbasis *Fragment*.
    -   Navigasi bawah dengan empat menu utama: *Devices, Camera, History, Profile*.
    -   Menggunakan komponen UI dari **Material Design**.

-   **📊 Manajemen Riwayat**
    -   Penyimpanan data lokal menggunakan *persistence library* **Room**.
    -   Riwayat deteksi lengkap beserta gambar dan hasil analisis.
    -   Laporan kerusakan yang terorganisir dengan konteks lokasi.

---

## 🛠️ Tumpukan Teknologi (Tech Stack)

### **Teknologi Inti**

| Komponen            | Teknologi                                                              |
| ------------------- | ---------------------------------------------------------------------- |
| **Bahasa Pemrograman** | `Java`                                                                 |
| **Platform** | `Android (API 24+)`                                                    |
| **Arsitektur** | `Single Activity + Fragments`                                          |
| **Database** | `Room Persistence Library`                                             |
| **Networking** | `Volley HTTP Library`                                                  |

### **API Eksternal**

-   **Roboflow API**: Untuk *computer vision* dan deteksi kerusakan.
-   **Google Gemini AI**: Untuk analisis bahasa alami dan rekomendasi.
-   **Google Maps API**: Untuk layanan lokasi dan pemetaan.
-   **Google Play Services**: Untuk layanan lokasi dan peta.

### **Dependensi Kunci**

```gradle
// Google Services
implementation("com.google.android.gms:play-services-maps:18.2.0")
implementation("com.google.android.gms:play-services-location:21.2.0")

// Database
implementation("androidx.room:room-runtime:2.6.1")
annotationProcessor("androidx.room:room-compiler:2.6.1")

// Networking
implementation(libs.volley)

// Image Loading
implementation(libs.glide)
```

---

## 🏗️ Struktur & Arsitektur Aplikasi

### **Izin (Permissions)**

Aplikasi ini memerlukan izin berikut untuk dapat berfungsi dengan baik:

-   `CAMERA`: Untuk mengambil gambar kerusakan jalan.
-   `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION`: Untuk layanan lokasi GPS.
-   `READ_EXTERNAL_STORAGE` / `READ_MEDIA_IMAGES`: Untuk mengakses gambar dari galeri.
-   `INTERNET`: Untuk komunikasi dengan API eksternal.

### **Struktur Komponen Utama**

```
MainActivity (Host Navigasi)
├── DevicesFragment (Antarmuka Deteksi + Peta)
├── CameraFragment (Antarmuka Pengambilan Gambar)
├── HistoryFragment (Manajemen Riwayat Deteksi)
└── ProfileFragment (Pengaturan Profil Pengguna)
```

### **Alur Autentikasi**

`LandingActivity` → `LoginActivity` / `RegisterActivity` → `MainActivity`
> Sesi pengguna dikelola menggunakan `SharedPreferences`.

### **Alur Data**

Pengambilan Gambar → Deteksi Roboflow → Layanan Lokasi → Analisis Gemini AI → Penyimpanan Lokal (Room)

---

## 🚀 Panduan Instalasi

### **Prasyarat**

-   Android Studio (Versi Arctic Fox atau lebih baru)
-   Android SDK API 24+
-   API Key untuk Google Maps
-   API Key untuk Roboflow
-   API Key untuk Google Gemini AI

### **Konfigurasi**

1.  **Clone repositori ini:**
    ```bash
    git clone [https://github.com/Winskiii/FP-PPB.git](https://github.com/Winskiii/FP-PPB.git)
    cd FP-PPB
    ```

2.  **Siapkan API Keys:**
    -   Tambahkan **Google Maps API key** Anda di dalam file `AndroidManifest.xml`.
    -   Konfigurasikan **Roboflow API key** Anda di dalam file `DevicesFragment.java`.
    -   Atur **Gemini AI API key** Anda di dalam file `DevicesFragment.java`.

3.  **Build dan Jalankan Aplikasi:**
    -   Buka proyek di Android Studio.
    -   Jalankan proses Gradle Sync.
    -   Build dan jalankan aplikasi pada emulator atau perangkat fisik.
    ```bash
    ./gradlew assembleDebug
    ```

---

## 📖 Cara Penggunaan

1.  **Buka Aplikasi** - Mulai dengan alur autentikasi (login/register).
2.  **Ambil Gambar** - Gunakan kamera atau pilih gambar dari galeri.
3.  **Analisis AI** - Sistem akan secara otomatis mendeteksi kerusakan dan memberikan analisis.
4.  **Konteks Lokasi** - Lokasi kerusakan akan ditandai secara otomatis menggunakan GPS.
5.  **Tinjau Riwayat** - Akses deteksi dan laporan sebelumnya di menu riwayat.

---

## 🤝 Kontribusi

Merasa tertarik untuk berkontribusi? Kami sangat terbuka!
1.  **Fork** repositori ini.
2.  Buat **Branch** baru untuk fitur Anda (`git checkout -b feature/AmazingFeature`).
3.  **Commit** perubahan Anda (`git commit -m 'Add some AmazingFeature'`).
4.  **Push** ke branch tersebut (`git push origin feature/AmazingFeature`).
5.  Buka sebuah **Pull Request**.

---

## 📄 Lisensi

Proyek ini merupakan bagian dari Proyek Akhir untuk mata kuliah Pemrograman Perangkat Bergerak (PPB). Didistribusikan di bawah Lisensi MIT. Lihat `LICENSE` untuk informasi lebih lanjut.

> **Catatan**: Aplikasi ini dirancang untuk para profesional pemeliharaan jalan dan pihak berwenang untuk mengidentifikasi dan mendokumentasikan kerusakan jalan secara efisien sebagai bagian dari perencanaan pemeliharaan.
