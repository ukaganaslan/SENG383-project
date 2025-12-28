
package com.kidtask;

import com.kidtask.model.*;
import com.kidtask.view.LoginView;
import javax.swing.*;

public class KidTaskApp {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            // 1. Veri Yöneticisini Başlat (Dosyaları Oku)
            DataManager.initialize();

            // 2. Eğer hiç kullanıcı yoksa ilk açılış verilerini ekle
            if (DataManager.getUsers().isEmpty()) {
                System.out.println("İlk açılış: Örnek veriler oluşturuluyor...");
                seedMockData();
            }

            // 3. Giriş Ekranını Aç
            new LoginView().setVisible(true);
        });
    }

    private static void seedMockData() {
        // Örnek Kullanıcılar
        Child ali = new Child("C1", "Ali", "ali@okul.com", 50);
        Parent veli = new Parent("P1", "Ayşe Hanım", "ayse@veli.com");
        Teacher ogretmen = new Teacher("T1", "Mehmet Hoca", "mehmet@okul.com");

        DataManager.addUser(ali);
        DataManager.addUser(veli);
        DataManager.addUser(ogretmen);

        // Örnek Görevler
        DataManager.addTask(new Task("Odanı Topla", "Oyuncakları sepete at", 20, "2023-12-30", TaskStatus.PENDING, ali));
        DataManager.addTask(new Task("Ödev Yap", "Matematik syf 40", 30, "2023-12-31", TaskStatus.APPROVED, ali)); // Puanlanmış varsayalım

        // Örnek Dilek
        DataManager.addWish(new Wish("Bisiklet", 500, ali));
        
        System.out.println("Örnek veriler eklendi ve kaydedildi.");
    }
}