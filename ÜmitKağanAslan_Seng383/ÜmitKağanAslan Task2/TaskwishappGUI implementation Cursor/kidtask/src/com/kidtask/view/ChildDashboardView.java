package com.kidtask.view;

import com.kidtask.model.Child;
import com.kidtask.model.DataManager;
import com.kidtask.model.Task;
import com.kidtask.model.TaskStatus;
import com.kidtask.model.Wish;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ChildDashboardView extends JFrame {
    private JLabel welcomeLabel;
    private JLabel pointsLabel;
    private JLabel levelLabel;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private Child currentChild;
    private JButton tasksButton, wishesButton, progressButton;
    private JPanel currentTabPanel;

    public ChildDashboardView() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("KidTask - Öğrenci Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 760);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xE8F5E9));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xE8F5E9));
        mainPanel.setBorder(new EmptyBorder(30, 60, 30, 60));

        // Beyaz Kart
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0xC8E6C9), 1),
                new EmptyBorder(20, 30, 20, 30)
        ));

        cardPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        cardPanel.add(createContentPanel(), BorderLayout.CENTER);

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Sol: Hoşgeldin
        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setBackground(Color.WHITE);

        welcomeLabel = new JLabel("Hoş Geldin!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0x2E7D32));
        leftPanel.add(welcomeLabel, BorderLayout.NORTH);

        JLabel subTitle = new JLabel("Bugün harika işler başarabilirsin! 🚀");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        leftPanel.add(subTitle, BorderLayout.SOUTH);

        // Sağ: Rozetler ve Çıkış
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.WHITE);

        pointsLabel = createBadgeLabel("Puan: 0", new Color(0xFFA000)); // Turuncu
        rightPanel.add(pointsLabel);

        levelLabel = createBadgeLabel("Seviye: 1", new Color(0x42A5F5)); // Mavi
        rightPanel.add(levelLabel);

        JButton logoutButton = createFlatButton("Çıkış Yap", new Color(0xEF5350), Color.WHITE);
        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Çıkış yapmak istediğine emin misin?", 
                    "Çıkış", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginView().setVisible(true);
            }
        });
        rightPanel.add(logoutButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }
    
    // Rozet Görünümlü Label
    private JLabel createBadgeLabel(String text, Color bg) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(Color.WHITE);
        label.setBackground(bg);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(8, 15, 8, 15));
        // Köşeleri yuvarlatmak Swing'de zor olduğu için basit kare bırakıyoruz ama renkli.
        return label;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Sekme Butonları Alanı
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabPanel.setBackground(Color.WHITE);
        tabPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        tasksButton = createFlatButton("Görevlerim 📝", new Color(0x66BB6A), Color.WHITE);
        tasksButton.addActionListener(e -> showTab("tasks"));
        tabPanel.add(tasksButton);

        wishesButton = createFlatButton("Dileklerim 🎁", new Color(0xF5F5F5), Color.BLACK); // Pasif renk
        wishesButton.addActionListener(e -> showTab("wishes"));
        tabPanel.add(wishesButton);

        progressButton = createFlatButton("İlerlemem 🏆", new Color(0xF5F5F5), Color.BLACK); // Pasif renk
        progressButton.addActionListener(e -> showTab("progress"));
        tabPanel.add(progressButton);

        contentPanel.add(tabPanel, BorderLayout.NORTH);

        currentTabPanel = createTasksTab();
        contentPanel.add(currentTabPanel, BorderLayout.CENTER);

        return contentPanel;
    }
    
    // Tam Renkli Flat Buton Oluşturucu
    private JButton createFlatButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 25, 10, 25));
        return btn;
    }

    private void showTab(String tabName) {
        // Hepsini pasif yap
        styleButton(tasksButton, false);
        styleButton(wishesButton, false);
        styleButton(progressButton, false);
        
        JPanel newPanel = null;
        switch (tabName) {
            case "tasks":
                styleButton(tasksButton, true);
                newPanel = createTasksTab();
                break;
            case "wishes":
                styleButton(wishesButton, true);
                newPanel = createWishesTab();
                break;
            case "progress":
                styleButton(progressButton, true);
                newPanel = createProgressTab();
                break;
        }

        if (newPanel != null) {
            Container parent = currentTabPanel.getParent();
            parent.remove(currentTabPanel);
            parent.add(newPanel, BorderLayout.CENTER);
            currentTabPanel = newPanel;
            parent.revalidate();
            parent.repaint();
        }
    }
    
    private void styleButton(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(new Color(0x66BB6A));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(0xF5F5F5));
            btn.setForeground(Color.BLACK);
        }
    }

    private JPanel createTasksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        String[] columnNames = {"Görev Adı", "Açıklama", "Puan", "Son Tarih", "Durum"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(50);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        taskTable.setForeground(Color.BLACK); // Tablo yazısı SİYAH
        taskTable.setGridColor(new Color(0xEEEEEE));
        taskTable.setShowGrid(true);
        
        // Başlık
        taskTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        taskTable.getTableHeader().setForeground(Color.BLACK);
        taskTable.getTableHeader().setBackground(new Color(0xFAFAFA));

        // Durum Kolonu Renklendirme
        taskTable.getColumn("Durum").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                
                if (status.contains("PENDING")) {
                    setForeground(new Color(0xF57C00)); // Turuncu
                    setText("Bekliyor ⏳");
                } else if (status.contains("APPROVED")) {
                    setForeground(new Color(0x388E3C)); // Yeşil
                    setText("Onaylandı ✅");
                } else if (status.contains("REJECTED")) {
                    setForeground(new Color(0xD32F2F)); // Kırmızı
                    setText("Reddedildi ❌");
                } else {
                    setForeground(Color.BLACK);
                }
                
                if (isSelected) setForeground(Color.BLACK); // Seçiliyse okunabilir olsun
                
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        refreshTasks(); 
        return panel;
    }

    private JPanel createWishesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xF1F8E9));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Bir Dilek Tut! 🎁");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x33691E));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        inputPanel.setBackground(new Color(0xF1F8E9));

        JTextField wishField = new JTextField(20);
        wishField.setPreferredSize(new Dimension(250, 40));
        wishField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        wishField.setForeground(Color.BLACK); // Siyah yazı
        inputPanel.add(wishField);

        JComboBox<String> costCombo = new JComboBox<>(new String[]{
            "Küçük (50 Puan)", "Orta (100 Puan)", "Büyük (150 Puan)"
        });
        costCombo.setPreferredSize(new Dimension(180, 40));
        costCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        costCombo.setForeground(Color.BLACK);
        inputPanel.add(costCombo);

        JButton addWishButton = createFlatButton("Dilek Ekle ✨", new Color(0x66BB6A), Color.WHITE);
        addWishButton.addActionListener(e -> {
            String wishName = wishField.getText().trim();
            if (wishName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lütfen dileğini yaz!");
                return;
            }
            
            int cost = 50;
            if (costCombo.getSelectedIndex() == 1) cost = 100;
            if (costCombo.getSelectedIndex() == 2) cost = 150;

            Wish w = new Wish(wishName, cost, currentChild);
            DataManager.addWish(w);
            
            JOptionPane.showMessageDialog(this, "Dileğin listeye eklendi: " + wishName);
            wishField.setText("");
        });
        inputPanel.add(addWishButton);

        panel.add(inputPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProgressTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Gelişim Durumu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setBackground(Color.WHITE);
        
        if (currentChild != null) {
            int points = currentChild.getTotalPoints();
            int level = (points / 50) + 1;
            int progress = points % 50;
            
            JLabel infoLabel = new JLabel("Seviye " + level + " • " + points + " Toplam Puan");
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            infoLabel.setForeground(Color.BLACK);
            
            JProgressBar progressBar = new JProgressBar(0, 50);
            progressBar.setValue(progress);
            progressBar.setStringPainted(true);
            progressBar.setString("Sonraki Seviye: " + progress + "/50");
            progressBar.setFont(new Font("Segoe UI", Font.BOLD, 14));
            progressBar.setForeground(new Color(0x1976D2)); // Koyu Mavi Çubuk
            progressBar.setBackground(new Color(0xE3F2FD));
            progressBar.setPreferredSize(new Dimension(500, 40));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0,0,20,0);
            statsPanel.add(infoLabel, gbc);
            
            gbc.gridy = 1;
            statsPanel.add(progressBar, gbc);
        } else {
            JLabel lbl = new JLabel("Veri yok.");
            lbl.setForeground(Color.BLACK);
            statsPanel.add(lbl);
        }
        
        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    public void setChild(Child child) {
        this.currentChild = child;
        updateHeader();
        refreshTasks();
    }

    private void updateHeader() {
        if (currentChild == null) return;
        welcomeLabel.setText("Hoş Geldin, " + currentChild.getName() + " 👋");
        pointsLabel.setText("Puan: " + currentChild.getTotalPoints());
        levelLabel.setText("Seviye: " + ((currentChild.getTotalPoints() / 50) + 1));
    }

    private void refreshTasks() {
        if (tableModel != null) tableModel.setRowCount(0);
        if (currentChild == null) return;
        
        List<Task> tasks = DataManager.getTasksForChild(currentChild);
        if (tableModel != null) {
            for (Task task : tasks) {
                Object[] row = {
                        task.getTitle(),
                        task.getDescription(),
                        task.getPoints() + " Puan",
                        task.getDueDate(),
                        task.getStatus().toString()
                };
                tableModel.addRow(row);
            }
        }
    }
}