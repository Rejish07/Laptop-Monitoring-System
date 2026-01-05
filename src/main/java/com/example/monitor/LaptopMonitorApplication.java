package com.example.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@SpringBootApplication
@Controller
public class LaptopMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaptopMonitorApplication.class, args);
    }

    // --- MAIL CONFIGURATION ---
    @Autowired
    private JavaMailSender mailSender;

    // Cooldown system: Only send 1 email every 5 minutes
    private LocalDateTime lastAlertTime = LocalDateTime.MIN; 

    // --- DATA MODEL ---
    static class LaptopStats {
        public double cpuLoad;
        public long freeRam;
        public double batteryLevel;
        public int strengthScore;
        public String message;
        public String timestamp;
    }

    private LaptopStats latestStats = new LaptopStats();

    // --- ROUTES ---

    @GetMapping("/")
    public String index(Model model) {
        // Updated port to 8082 based on your setup
        model.addAttribute("serverUrl", "http://localhost:8082/api/report");
        return "index";
    }

    @GetMapping("/api/latest")
    @ResponseBody
    public ResponseEntity<LaptopStats> getLatestStats() {
        return ResponseEntity.ok(latestStats);
    }

    @PostMapping("/api/report")
    @ResponseBody
    public ResponseEntity<String> receiveReport(@RequestBody LaptopStats stats) {
        calculateScore(stats);
        
        // CHECK FOR ALERTS
        checkAndAlert(stats);

        stats.timestamp = LocalDateTime.now().toString();
        this.latestStats = stats;
        return ResponseEntity.ok("Received");
    }

    @PostMapping("/api/simulate")
    @ResponseBody
    public ResponseEntity<LaptopStats> simulate() {
        Random rand = new Random();
        LaptopStats stats = new LaptopStats();
        stats.cpuLoad = 85 + (15 * rand.nextDouble()); // Simulates High Load (85-100%)
        stats.freeRam = (long) (rand.nextDouble() * 2L * 1024 * 1024 * 1024); // Low RAM
        stats.batteryLevel = 15; // Low Battery
        
        calculateScore(stats);
        checkAndAlert(stats); // Check alert for simulation too
        
        stats.timestamp = LocalDateTime.now().toString();
        this.latestStats = stats;
        return ResponseEntity.ok(stats);
    }

    // --- LOGIC ---

    private void calculateScore(LaptopStats stats) {
        int score = 100;
        if (stats.cpuLoad > 80) score -= 30;
        if (stats.freeRam < 2L * 1024 * 1024 * 1024) score -= 30;
        if (stats.batteryLevel < 20) score -= 20;
        
        stats.strengthScore = Math.max(0, score);
        
        if (score >= 80) stats.message = "System Healthy";
        else if (score >= 60) stats.message = "Moderate Load";
        else stats.message = "System Struggling";
    }

    private void checkAndAlert(LaptopStats stats) {
        // Thresholds: CPU > 90% OR Battery < 15%
        boolean isCritical = stats.cpuLoad > 90 || stats.batteryLevel < 15;

        if (isCritical) {
            // Check if 5 minutes have passed since last email
            long minutesSinceLast = ChronoUnit.MINUTES.between(lastAlertTime, LocalDateTime.now());
            
            if (minutesSinceLast >= 5) {
                sendEmail(stats);
                lastAlertTime = LocalDateTime.now();
            }
        }
    }

    private void sendEmail(LaptopStats stats) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("YOUR_EMAIL@gmail.com");
            message.setSubject(" ALERT: Laptop Critical Status");
            message.setText(
                "Warning! Your laptop has reached critical levels.\n\n" +
                "CPU Load: " + String.format("%.2f", stats.cpuLoad) + "%\n" +
                "Battery: " + stats.batteryLevel + "%\n" +
                "RAM Free: " + (stats.freeRam / (1024*1024*1024)) + " GB\n\n" +
                "Please check your system immediately."
            );

            mailSender.send(message);
            System.out.println(">>> EMAIL ALERT SENT SUCCESSFULLY <<<");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}