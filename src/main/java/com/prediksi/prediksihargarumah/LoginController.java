package com.prediksi.prediksihargarumah;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    // Menampilkan halaman login dengan pesan error (jika ada)
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Username atau Password salah!");
        }
        return "login"; // Mengarahkan ke login.html
    }

    // Proses login
    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
            @RequestParam String password,
            HttpSession session) {
        // Validasi username dan password sederhana
        if ("admin".equals(username) && "12345".equals(password)) {
            session.setAttribute("user", username); // Menyimpan username ke sesi
            return "redirect:/home"; // Mengarahkan ke halaman utama setelah login
        }
        return "redirect:/login?error=true"; // Kembali ke halaman login dengan pesan error
    }

    // Menampilkan halaman utama setelah login
    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        String user = (String) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login"; // Cegah akses tanpa login
        }
        model.addAttribute("username", user); // Menampilkan nama pengguna di halaman utama
        return "index"; // Menampilkan halaman index.html
    }

    // Proses logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Menghapus sesi pengguna
        return "redirect:/login"; // Kembali ke halaman login
    }
}