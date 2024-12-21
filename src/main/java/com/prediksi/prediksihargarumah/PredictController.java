package com.prediksi.prediksihargarumah;

import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/api")
public class PredictController {

    @PostMapping("/predict")
    public String predict(@RequestParam double luas, @RequestParam int kamar,
            @RequestParam int km, @RequestParam int lantai) {
        try {
            // Menjalankan script Python dengan parameter input
            ProcessBuilder pb = new ProcessBuilder("python",
                    "src/main/resources/notebook/PrediksiHargaRumah.py",
                    String.valueOf(kamar), String.valueOf(km),
                    String.valueOf(luas), String.valueOf(lantai));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Menunggu sampai proses selesai
            process.waitFor();

            // Mengembalikan hasil dari Python
            return "Hasil Prediksi: Rp " + output.toString().trim(); // trim() untuk menghapus whitespace tambahan
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}