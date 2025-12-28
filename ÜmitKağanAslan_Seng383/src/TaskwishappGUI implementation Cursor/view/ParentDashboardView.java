package com.kidtask.view;

import com.kidtask.model.Child;
import com.kidtask.model.DataManager;
import com.kidtask.model.Task;
import com.kidtask.model.TaskStatus;
import com.kidtask.model.Wish;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ParentDashboardView extends JFrame {
    private DefaultTableModel taskTableModel;
    private DefaultTableModel wishTableModel;

    public ParentDashboardView() {
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setTitle("KidTask - Veli Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xE1F5FE));

        // Ana Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xE1F5FE));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- ÜST PANEL ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0xE1F5FE));
        topPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel title = new JLabel("Veli Kontrol Merkezi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(0x0277BD));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(0xE1F5FE));

        JButton addTaskBtn = createFlatButton("➕ Yeni Görev Ekle", new Color(0x29B6F6));
        addTaskBtn.addActionListener(e -> showAddTaskDialog());
        buttonPanel.add(addTaskBtn);

        JButton logoutBtn = createFlatButton("Çıkış Yap", new Color(0xFF7043));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
        buttonPanel.add(logoutBtn);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // --- ORTA PANEL (Tablar) ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Başlığı "Bekleyen Onaylar" olarak güncelledim çünkü artık bitmiş görevler buraya düşecek
        tabbedPane.addTab("Tamamlanan Görev Onayları ✅", createTasksPanel());
        tabbedPane.addTab("Dilek İstekleri 🎁", createWishesPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }
    
    // Yardımcı: Düz Renkli Buton Oluşturucu
    private JButton createFlatButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setOpaque(true);         
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] cols = {"Görev", "Öğrenci", "Puan", "Son Tarih", "İşlemler"};
        taskTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        
        JTable table = new JTable(taskTableModel);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.BLACK);
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setForeground(Color.BLACK);

        table.getColumn("İşlemler").setCellRenderer(new ButtonRenderer(true));
        table.getColumn("İşlemler").setCellEditor(new TaskButtonEditor(new JCheckBox()));
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton refreshBtn = createFlatButton("Listeyi Yenile", new Color(0x78909C));
        refreshBtn.addActionListener(e -> refreshData());
        panel.add(refreshBtn, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createWishesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        String[] cols = {"Dilek", "Öğrenci", "Maliyet", "Durum", "İşlemler"};
        wishTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return col == 4; }
        };
        
        JTable table = new JTable(wishTableModel);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.BLACK);
        
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setForeground(Color.BLACK);

        table.getColumn("İşlemler").setCellRenderer(new ButtonRenderer(false));
        table.getColumn("İşlemler").setCellEditor(new WishButtonEditor(new JCheckBox()));
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void showAddTaskDialog() {
        JDialog d = new JDialog(this, "Yeni Ev Görevi Ekle", true);
        d.setLayout(new GridLayout(6, 2, 10, 10));
        d.setSize(450, 400);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(Color.WHITE);
        
        JTextField titleF = new JTextField(); titleF.setForeground(Color.BLACK);
        JTextField descF = new JTextField(); descF.setForeground(Color.BLACK);
        JTextField pointsF = new JTextField("10"); pointsF.setForeground(Color.BLACK);
        JTextField dateF = new JTextField("2025-12-31"); dateF.setForeground(Color.BLACK);
        
        JComboBox<Child> childBox = new JComboBox<>();
        childBox.setForeground(Color.BLACK);
        List<Child> children = DataManager.getAllChildren();
        for (Child c : children) childBox.addItem(c);
        
        childBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Child) setText(((Child) value).getName());
                setForeground(Color.BLACK);
                return this;
            }
        });

        d.add(createLabel("  Görev Başlığı:")); d.add(titleF);
        d.add(createLabel("  Açıklama:")); d.add(descF);
        d.add(createLabel("  Puan Değeri:")); d.add(pointsF);
        d.add(createLabel("  Son Tarih (Y-A-G):")); d.add(dateF);
        d.add(createLabel("  Öğrenci Seç:")); d.add(childBox);

        JButton saveBtn = createFlatButton("Görevi Ata", new Color(0x66BB6A));
saveBtn.addActionListener(e -> {
            try {
                if (childBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(d, "Lütfen bir öğrenci seçin.");
                    return;
                }
                String title = titleF.getText().trim();
                if (title.isEmpty()) return;

                // --- YENİ ZAMAN KONTROLÜ BAŞLANGICI ---
                try {
                    java.time.LocalDate selectedDate = java.time.LocalDate.parse(dateF.getText());
                    if (selectedDate.isBefore(java.time.LocalDate.now())) {
                        JOptionPane.showMessageDialog(d, "Hata: Geçmiş bir tarihe görev atayamazsınız!", "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (java.time.format.DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(d, "Hata: Geçersiz tarih formatı! (Y-A-G şeklinde giriniz)");
                    return;
                }
                // --- YENİ ZAMAN KONTROLÜ BİTİŞİ ---

                // Yeni görevler PENDING olarak başlar
                Task t = new Task(title, descF.getText(), Integer.parseInt(pointsF.getText()), 
                        dateF.getText(), TaskStatus.PENDING, (Child)childBox.getSelectedItem());
                
                DataManager.addTask(t);
                JOptionPane.showMessageDialog(d, "Görev başarıyla eklendi!");
                refreshData();
                d.dispose();
            } catch(Exception ex) { JOptionPane.showMessageDialog(d, "Hata: " + ex.getMessage()); }
        });
        d.add(new JLabel("")); d.add(saveBtn);
        d.setVisible(true);
    }
    
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(Color.BLACK);
        return l;
    }

    private void refreshData() {
        taskTableModel.setRowCount(0);
        for (Task t : DataManager.getTasks()) {
            // ÖNEMLİ DEĞİŞİKLİK:
            // Veli artık PENDING değil, sadece çocuğun bitirdiği (COMPLETED) görevleri onaylar.
            if (t.getStatus() == TaskStatus.COMPLETED) {
                taskTableModel.addRow(new Object[]{t.getTitle(), t.getAssignee().getName(), t.getPoints(), t.getDueDate(), t});
            }
        }
        
        wishTableModel.setRowCount(0);
        for (Wish w : DataManager.getWishes()) {
            if ("PENDING".equals(w.getStatus())) {
                wishTableModel.addRow(new Object[]{w.getName(), w.getChild().getName(), w.getCost(), w.getStatus(), w});
            }
        }
    }

    // --- Actions ---
    private void approveTask(Task task) {
        String ratingStr = JOptionPane.showInputDialog(this, "Görevi Puanla (1-5):", "5");
        if (ratingStr == null) return;
        int rating = 5;
        try { rating = Integer.parseInt(ratingStr); } catch(Exception e){}
        
        task.setStatus(TaskStatus.APPROVED);
        task.setRating(rating);
        if(task.getAssignee() != null) task.getAssignee().setTotalPoints(task.getAssignee().getTotalPoints() + task.getPoints());
        
        DataManager.updateTask(task);
        JOptionPane.showMessageDialog(this, "Görev onaylandı, puan verildi!");
        refreshData();
    }

    private void rejectTask(Task task) {
        task.setStatus(TaskStatus.REJECTED);
        DataManager.updateTask(task);
        refreshData();
    }

    private void approveWish(Wish wish) {
        if (wish.getChild().getTotalPoints() >= wish.getCost()) {
            wish.getChild().setTotalPoints(wish.getChild().getTotalPoints() - wish.getCost());
            wish.setStatus("APPROVED");
            DataManager.updateWish(wish);
            JOptionPane.showMessageDialog(this, "Dilek onaylandı!");
        } else {
            JOptionPane.showMessageDialog(this, "Yetersiz Puan!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
        refreshData();
    }

    private void rejectWish(Wish wish) {
        wish.setStatus("REJECTED");
        DataManager.updateWish(wish);
        refreshData();
    }
    
    // --- Renderers & Editors ---
    
    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton b1;
        private final JButton b2;

        public ButtonRenderer(boolean isTask) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
            setOpaque(true);
            setBackground(Color.WHITE); 
            
            b1 = createFlatButton("Onayla", new Color(0x43A047)); // Yeşil
            b2 = createFlatButton("Reddet", new Color(0xE53935)); // Kırmızı
            
            add(b1); 
            add(b2);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) { 
            setBackground(isS ? t.getSelectionBackground() : Color.WHITE);
            return this; 
        }
    }

    class TaskButtonEditor extends DefaultCellEditor {
        JPanel p; Task t;
        public TaskButtonEditor(JCheckBox cb) {
            super(cb);
            p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            p.setOpaque(true);
            p.setBackground(Color.WHITE);
            
            JButton b1 = createFlatButton("Onayla", new Color(0x43A047));
            b1.addActionListener(e -> { fireEditingStopped(); if(t!=null) approveTask(t); });
            
            JButton b2 = createFlatButton("Reddet", new Color(0xE53935));
            b2.addActionListener(e -> { fireEditingStopped(); if(t!=null) rejectTask(t); });
            
            p.add(b1); p.add(b2);
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { 
            this.t=(Task)v; 
            p.setBackground(t.getSelectionBackground());
            return p; 
        }
        public Object getCellEditorValue() { return t; }
    }

    class WishButtonEditor extends DefaultCellEditor {
        JPanel p; Wish w;
        public WishButtonEditor(JCheckBox cb) {
            super(cb);
            p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            p.setOpaque(true);
            p.setBackground(Color.WHITE);
            
            JButton b1 = createFlatButton("Onayla", new Color(0x43A047));
            b1.addActionListener(e -> { fireEditingStopped(); if(w!=null) approveWish(w); });
            
            JButton b2 = createFlatButton("Reddet", new Color(0xE53935));
            b2.addActionListener(e -> { fireEditingStopped(); if(w!=null) rejectWish(w); });
            
            p.add(b1); p.add(b2);
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { 
            this.w=(Wish)v; 
            p.setBackground(t.getSelectionBackground());
            return p; 
        }
        public Object getCellEditorValue() { return w; }
    }
}