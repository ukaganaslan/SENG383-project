package com.kidtask.view;

import com.kidtask.model.DataManager;
import com.kidtask.model.User;
import com.kidtask.model.Child;
import com.kidtask.model.Parent;
import com.kidtask.model.Teacher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class LoginView extends JFrame {
    private JComboBox<String> roleComboBox;

    public LoginView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("KidTask - Giriş");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 420);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xE8F5E9)); // Yumuşak Yeşil Arka Plan

        // Ana Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xE8F5E9));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Kart Görünümlü Panel (Beyaz)
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xC8E6C9), 1),
                new EmptyBorder(30, 30, 30, 30)
        ));

        // --- BAŞLIK ALANI ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel("🌱", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        headerPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("KidTask", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0x2E7D32)); // Koyu Yeşil
        headerPanel.add(titleLabel);
        
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // --- FORM ALANI ---
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel subtitleLabel = new JLabel("Hoş Geldiniz! Rolünüzü seçin:", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.BLACK); // Siyah Yazı
        formPanel.add(subtitleLabel);

        // Rol Seçimi
        roleComboBox = new JComboBox<>(new String[]{"Öğrenci (Child)", "Veli (Parent)", "Öğretmen (Teacher)"});
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setForeground(Color.BLACK);
        
        // Liste elemanlarının siyah olmasını garantiye al
        roleComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setForeground(Color.BLACK);
                return this;
            }
        });
        
        // Ortalamak için
        ((JLabel)roleComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(roleComboBox);

        formPanel.add(Box.createVerticalStrut(5));

        // Giriş Butonu (Flat Design)
        JButton loginButton = new JButton("Giriş Yap");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(0x43A047)); // Materyal Yeşil
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true); // Rengi zorla
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        
        formPanel.add(loginButton);

        cardPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void handleLogin() {
        int selectedIndex = roleComboBox.getSelectedIndex();
        String role = "";
        
        switch (selectedIndex) {
            case 0: role = "CHILD"; break;
            case 1: role = "PARENT"; break;
            case 2: role = "TEACHER"; break;
        }

        User user = DataManager.getFirstUserByRole(role);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this, 
                "Bu rol için kayıtlı kullanıcı bulunamadı!\nLütfen önce veri oluşturulduğundan emin olun.", 
                "Giriş Hatası", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.setVisible(false);
        this.dispose();

        if (user instanceof Child) {
            ChildDashboardView childView = new ChildDashboardView();
            childView.setChild((Child) user);
            childView.setVisible(true);
        } else if (user instanceof Parent) {
            ParentDashboardView parentView = new ParentDashboardView();
            parentView.setVisible(true);
        } else if (user instanceof Teacher) {
            TeacherDashboardView teacherView = new TeacherDashboardView();
            teacherView.setVisible(true);
        }
    }
}