package com.prediksi.prediksihargarumah;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.*;
import java.util.regex.*;

@Controller
public class PredictionController {

    // Menampilkan halaman index.html dengan form input
    @GetMapping("/")
    public String showForm() {
        return "index"; // Mengarahkan ke templates/index.html
    }

    // Memproses input dan mengembalikan hasil prediksi
    @GetMapping("/predict")
    public String predict(
            @RequestParam int kamarTidur,
            @RequestParam int kamarMandi,
            @RequestParam int luasRumah,
            @RequestParam int lantai,
            Model model) {
        try {
            // Membuat perintah untuk menjalankan skrip Python
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python",
                    "C:/tugasakhir/prediksihargarumah/src/main/resources/notebook/PrediksiHargaRumah.py",
                    String.valueOf(kamarTidur),
                    String.valueOf(kamarMandi),
                    String.valueOf(luasRumah),
                    String.valueOf(lantai));

            // Gabungkan error stream dengan output stream untuk debugging
            processBuilder.redirectErrorStream(true);

            // Menjalankan proses
            Process process = processBuilder.start();

            // Membaca output dari proses
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // Debugging log untuk memeriksa output dari Python
            System.out.println("Output dari Python: " + output.toString());

            // Mencari pola angka desimal terakhir (harga)
            String result = output.toString();
            Pattern pattern = Pattern.compile("\\d+(\\.\\d{1,2})?"); // Mencocokkan angka dengan format desimal
            Matcher matcher = pattern.matcher(result);
            String predictedPrice = "";

            // Menemukan angka terakhir yang cocok dengan format harga
            while (matcher.find()) {
                predictedPrice = matcher.group();
            }

            // Jika hasil prediksi valid, format angka dengan pemisah ribuan
            if (!predictedPrice.isEmpty()) {
                // Menghapus titik (jika ada) dan menambahkan format ribuan
                long price = (long) (Double.parseDouble(predictedPrice.replace(",", "")) * 1_000_000_000);
                result = String.format("Prediksi harga rumah: Rp. %,d", price);
            } else {
                result = "Error: Tidak dapat memproses hasil prediksi.";
            }

            // Menambahkan hasil prediksi ke model
            model.addAttribute("result", result);
        } catch (IOException e) {
            // Menangani error dan menambahkan pesan error ke model
            model.addAttribute("result", "Error: " + e.getMessage());
        }

        return "index"; // Mengembalikan ke halaman index.html dengan hasil
    }
}