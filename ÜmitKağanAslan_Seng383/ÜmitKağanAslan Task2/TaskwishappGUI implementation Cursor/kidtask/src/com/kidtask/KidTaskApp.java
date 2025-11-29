package com.kidtask;

import com.kidtask.model.Child;
import com.kidtask.model.DataManager;
import com.kidtask.model.Parent;
import com.kidtask.model.Teacher;
import com.kidtask.model.Task;
import com.kidtask.model.TaskStatus;
import com.kidtask.model.User;
import com.kidtask.model.Wish;
import com.kidtask.view.LoginView;

import javax.swing.*;

/**
 * Main launcher for KidTask Swing application.
 * Starts with login screen.
 */
public class KidTaskApp {
    
    public static void main(String[] args) {
        System.out.println("Starting KidTask Application...");
        
        // Arayüz görünümünü sistem temasına ayarla
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set look and feel: " + e.getMessage());
        }

        // GUI oluşturma işlemini Event Dispatch Thread üzerinde başlat
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Initializing DataManager...");
                DataManager.initialize();
                
                System.out.println("Checking for existing data...");
                if (DataManager.getUsers().isEmpty()) {
                    System.out.println("No users found, seeding mock data...");
                    seedMockData();
                } else {
                    System.out.println("Found " + DataManager.getUsers().size() + " existing users.");
                }

                // Giriş ekranını göster
                System.out.println("Showing login screen...");
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
                System.out.println("Login screen displayed.");
            } catch (Exception e) {
                System.err.println("FATAL ERROR: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error starting application:\n" + e.getMessage() + "\n\nCheck console for details.", 
                    "Application Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        System.out.println("Main method completed. GUI should appear shortly...");
    }

    private static void seedMockData() {
        try {
            System.out.println("Creating mock users, tasks and wishes...");
            
            // 1. Kullanıcıları Oluştur
            // Child puanı 50 olarak başlatıldı ki dilek sistemi test edilebilsin.
            Child child = new Child("c1", "Ali", "ali@example.com", 50);
            User parent = new Parent("p1", "Ayşe", "parent@example.com");
            User teacher = new Teacher("t1", "Mehmet Hoca", "teacher@example.com");

            DataManager.addUser(child);
            DataManager.addUser(parent);
            DataManager.addUser(teacher);
            System.out.println("Users created.");

            // 2. Görevleri Oluştur (Yeni format: Başlık, Açıklama, Puan, SON TARİH, Durum, Atanan)
            Task t1 = new Task("Bitkileri Sula", "Salon bitkilerini sula", 10, "2025-11-30", TaskStatus.PENDING, child);
            Task t2 = new Task("Matematik Çalış", "Sayfa 45-50 arası testleri çöz", 20, "2025-12-01", TaskStatus.PENDING, child);
            Task t3 = new Task("Odanı Topla", "Oyuncakları kutusuna koy", 15, "2025-11-29", TaskStatus.APPROVED, child); // Onaylanmış örnek
            t3.setRating(5); // Onaylanmış göreve puan ver

            DataManager.addTask(t1);
            DataManager.addTask(t2);
            DataManager.addTask(t3);
            System.out.println("Tasks created.");
            
            // 3. Dilekleri Oluştur (Yeni özellik)
            Wish w1 = new Wish("LEGO Seti", 100, child);
            Wish w2 = new Wish("Sinema Bileti", 40, child);
            
            DataManager.addWish(w1);
            DataManager.addWish(w2);
            System.out.println("Wishes created.");
            
        } catch (Exception e) {
            System.err.println("Error seeding mock data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}