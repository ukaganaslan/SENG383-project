package com.kidtask.view;

import com.kidtask.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ChildDashboardView extends JFrame {
    private JLabel welcomeLabel, pointsLabel, levelLabel, ratingLabel;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private Child currentChild;
    private JButton tasksButton, wishesButton, progressButton;
    private JPanel currentTabPanel;
    private String currentFilter = "ALL"; // ALL, DAILY, WEEKLY 

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

        JPanel leftPanel = new JPanel(new BorderLayout(0, 5));
        leftPanel.setBackground(Color.WHITE);
        welcomeLabel = new JLabel("Hoş Geldin!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(0x2E7D32));
        leftPanel.add(welcomeLabel, BorderLayout.NORTH);

        JLabel subTitle = new JLabel("Görevlerini tamamla, puanları topla! 🚀");
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitle.setForeground(Color.GRAY);
        leftPanel.add(subTitle, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.WHITE);

        pointsLabel = createBadgeLabel("Puan: 0", new Color(0xFFA000));
        rightPanel.add(pointsLabel);
        
        //  Ortalama puan göstergesi eklendi
        ratingLabel = createBadgeLabel("Ort. Başarı: -", new Color(0x9C27B0));
        rightPanel.add(ratingLabel);

        levelLabel = createBadgeLabel("Seviye: 1", new Color(0x42A5F5));
        rightPanel.add(levelLabel);

        JButton logoutButton = createFlatButton("Çıkış", new Color(0xEF5350), Color.WHITE);
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
        rightPanel.add(logoutButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JLabel createBadgeLabel(String text, Color bg) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setBackground(bg);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(8, 15, 8, 15));
        return label;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navigationPanel.setBackground(Color.WHITE);
        
        tasksButton = createFlatButton("Görevlerim 📝", new Color(0x66BB6A), Color.WHITE);
        wishesButton = createFlatButton("Dileklerim 🎁", new Color(0xF5F5F5), Color.BLACK);
        progressButton = createFlatButton("İlerlemem 🏆", new Color(0xF5F5F5), Color.BLACK);

        tasksButton.addActionListener(e -> showTab("tasks"));
        wishesButton.addActionListener(e -> showTab("wishes"));
        progressButton.addActionListener(e -> showTab("progress"));

        navigationPanel.add(tasksButton);
        navigationPanel.add(wishesButton);
        navigationPanel.add(progressButton);

        contentPanel.add(navigationPanel, BorderLayout.NORTH);
        currentTabPanel = createTasksTab();
        contentPanel.add(currentTabPanel, BorderLayout.CENTER);

        return contentPanel;
    }
    
    //  Filtreleme Paneli
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel lbl = new JLabel("Filtrele: ");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lbl);

        JButton allBtn = createFlatButton("Tümü", Color.LIGHT_GRAY, Color.BLACK);
        JButton dailyBtn = createFlatButton("Bugün", Color.LIGHT_GRAY, Color.BLACK);
        JButton weeklyBtn = createFlatButton("Bu Hafta", Color.LIGHT_GRAY, Color.BLACK);

        allBtn.addActionListener(e -> { currentFilter = "ALL"; refreshTasks(); });
        dailyBtn.addActionListener(e -> { currentFilter = "DAILY"; refreshTasks(); });
        weeklyBtn.addActionListener(e -> { currentFilter = "WEEKLY"; refreshTasks(); });

        panel.add(allBtn);
        panel.add(dailyBtn);
        panel.add(weeklyBtn);
        return panel;
    }

    private JPanel createTasksTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        panel.add(createFilterPanel(), BorderLayout.NORTH);

        String[] cols = {"Görev", "Açıklama", "Puan", "Son Tarih", "Durum", "İşlem"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return col == 5; } // Sadece buton kolonu tıklanabilir
        };
        
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(50);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        taskTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        //  Mark as completed butonu
        taskTable.getColumn("İşlem").setCellRenderer(new ButtonRenderer());
        taskTable.getColumn("İşlem").setCellEditor(new ButtonEditor(new JCheckBox()));

        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        refreshTasks();
        return panel;
    }

    private JPanel createWishesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xF1F8E9));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Dilek Ekle (Seviye Kontrolü Var)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(new Color(0xF1F8E9));
        
        JTextField wishF = new JTextField(15);
        JComboBox<String> costBox = new JComboBox<>(new String[]{"Küçük (50 Puan)", "Büyük (150 Puan)"});
        JButton addBtn = createFlatButton("Ekle", new Color(0x66BB6A), Color.WHITE);

        addBtn.addActionListener(e -> {
            int cost = costBox.getSelectedIndex() == 0 ? 50 : 150;
            // [cite: 36] Seviye kontrolü örneği
            int currentLevel = (currentChild.getTotalPoints() / 50) + 1;
            if (cost > 100 && currentLevel < 2) {
                JOptionPane.showMessageDialog(this, "Büyük dilekler için Seviye 2 olmalısın!");
                return;
            }
            DataManager.addWish(new Wish(wishF.getText(), cost, currentChild));
            JOptionPane.showMessageDialog(this, "Dilek eklendi!");
            wishF.setText("");
        });

        inputPanel.add(new JLabel("Dilek:")); inputPanel.add(wishF);
        inputPanel.add(costBox); inputPanel.add(addBtn);
        panel.add(inputPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProgressTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40,40,40,40));
        
        if (currentChild != null) {
            int p = currentChild.getTotalPoints();
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(p % 100);
            bar.setStringPainted(true);
            bar.setString("Seviye İlerlemesi: " + (p%100) + "/100");
            panel.add(bar, BorderLayout.NORTH);
        }
        return panel;
    }

    public void setChild(Child child) {
        this.currentChild = child;
        updateHeader();
        refreshTasks();
    }

    private void updateHeader() {
        if (currentChild == null) return;
        welcomeLabel.setText("Hoş Geldin, " + currentChild.getName());
        pointsLabel.setText("Puan: " + currentChild.getTotalPoints());
        
        // Ortalama Puan Hesapla 
        List<Task> tasks = DataManager.getTasksForChild(currentChild);
        double totalRating = 0;
        int count = 0;
        for(Task t : tasks) {
            if(t.getStatus() == TaskStatus.APPROVED && t.getRating() > 0) {
                totalRating += t.getRating();
                count++;
            }
        }
        String avg = count > 0 ? String.format("%.1f", totalRating/count) : "-";
        ratingLabel.setText("Ort. Başarı: " + avg + "/5.0");
        levelLabel.setText("Seviye: " + ((currentChild.getTotalPoints()/100)+1));
    }

    private void refreshTasks() {
        if (tableModel == null || currentChild == null) return;
        tableModel.setRowCount(0);
        List<Task> tasks = DataManager.getTasksForChild(currentChild);
        LocalDate today = LocalDate.now();

        for (Task t : tasks) {
            //  Filtreleme Mantığı
            boolean show = false;
            if (currentFilter.equals("ALL")) show = true;
            else if (currentFilter.equals("DAILY") && t.getDueDate().equals(today.toString())) show = true;
            else if (currentFilter.equals("WEEKLY")) {
                try {
                    LocalDate d = LocalDate.parse(t.getDueDate());
                    if (!d.isBefore(today) && d.isBefore(today.plusDays(7))) show = true;
                } catch(Exception e) { show = true; }
            }

            if (show) {
                tableModel.addRow(new Object[]{t.getTitle(), t.getDescription(), t.getPoints(), t.getDueDate(), t.getStatus(), t});
            }
        }
    }

    private void showTab(String name) {
        if (currentTabPanel != null) currentTabPanel.setVisible(false);
        Container parent = currentTabPanel.getParent();
        parent.remove(currentTabPanel);
        
        if (name.equals("tasks")) currentTabPanel = createTasksTab();
        else if (name.equals("wishes")) currentTabPanel = createWishesTab();
        else currentTabPanel = createProgressTab();
        
        parent.add(currentTabPanel);
        parent.revalidate();
        parent.repaint();
    }
    
    private JButton createFlatButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }
    
    // --- Renderers for "Tamamla" Button ---
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Task task = (Task)v;
            if(task.getStatus() == TaskStatus.PENDING) {
                setText("Tamamla ✅"); setBackground(new Color(0x4CAF50)); setEnabled(true);
            } else {
                setText(task.getStatus().toString()); setBackground(Color.WHITE); setEnabled(false);
            }
            return this;
        }
    }
    class ButtonEditor extends DefaultCellEditor {
        JButton btn; Task task;
        public ButtonEditor(JCheckBox cb) {
            super(cb);
            btn = new JButton();
            btn.addActionListener(e -> {
                if (task != null && task.getStatus() == TaskStatus.PENDING) {
                    task.setStatus(TaskStatus.COMPLETED); //  Durumu güncelle
                    DataManager.updateTask(task);
                    JOptionPane.showMessageDialog(null, "Harika! Görev onaya gönderildi.");
                    fireEditingStopped();
                    refreshTasks();
                }
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            task = (Task)v;
            if(task.getStatus() == TaskStatus.PENDING) {
                btn.setText("Tamamla ✅"); btn.setBackground(new Color(0x4CAF50));
            } else {
                btn.setText(task.getStatus().toString()); btn.setBackground(Color.GRAY);
            }
            return btn;
        }
        public Object getCellEditorValue() { return task; }
    }
}