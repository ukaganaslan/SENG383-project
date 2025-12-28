package com.kidtask.view;

import com.kidtask.model.Child;
import com.kidtask.model.DataManager;
import com.kidtask.model.Task;
import com.kidtask.model.TaskStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherDashboardView extends JFrame {
    private JTable tasksTable;
    private DefaultTableModel model;

    public TeacherDashboardView() {
        initializeUI();
        refreshTable();
    }

    private void initializeUI() {
        setTitle("KidTask - Öğretmen Paneli");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0xFFF3E0));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(0xFFF3E0));

        // --- ÜST PANEL ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0xFFF3E0));
        header.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel title = new JLabel("Okul Görevleri & Takip");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(0xE65100));
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(0xFFF3E0));
        
        JButton addBtn = createFlatButton("➕ Yeni Ödev Ekle", new Color(0xFB8C00));
        addBtn.addActionListener(e -> showAddTaskDialog());
        
        JButton logoutBtn = createFlatButton("Çıkış", new Color(0x546E7A));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });

        btnPanel.add(addBtn);
        btnPanel.add(logoutBtn);
        
        header.add(title, BorderLayout.WEST);
        header.add(btnPanel, BorderLayout.EAST);
        mainPanel.add(header, BorderLayout.NORTH);

        // --- TABLO ---
        String[] cols = {"Görev", "Öğrenci", "Son Tarih", "Durum", "Puanlama", "İşlem"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return col == 5; } 
        };
        tasksTable = new JTable(model);
        tasksTable.setRowHeight(50); // Buton sığsın diye genişletildi
        tasksTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tasksTable.setForeground(Color.BLACK);
        
        tasksTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tasksTable.getTableHeader().setForeground(Color.BLACK);

        tasksTable.getColumn("İşlem").setCellRenderer(new RateButtonRenderer());
        tasksTable.getColumn("İşlem").setCellEditor(new RateButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(tasksTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    // Düz Renkli Buton Yardımcısı
    private JButton createFlatButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); // Tam renkli görünüm için
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Task t : DataManager.getTasks()) {
            model.addRow(new Object[]{
                t.getTitle(),
                t.getAssignee() != null ? t.getAssignee().getName() : "-",
                t.getDueDate(),
                t.getStatus(),
                t.getRating() > 0 ? t.getRating() + "/5" : "-",
                t
            });
        }
    }

    private void showAddTaskDialog() {
        JDialog d = new JDialog(this, "Yeni Okul Ödevi Ver", true);
        d.setLayout(new GridLayout(6, 2, 10, 10));
        d.setSize(450, 400);
        d.setLocationRelativeTo(this);
        d.getContentPane().setBackground(Color.WHITE);

        JTextField titleF = new JTextField(); titleF.setForeground(Color.BLACK);
        JTextField descF = new JTextField(); descF.setForeground(Color.BLACK);
        JTextField pointsF = new JTextField("20"); pointsF.setForeground(Color.BLACK);
        JTextField dateF = new JTextField("2025-12-31"); dateF.setForeground(Color.BLACK);
        
        JComboBox<Child> childBox = new JComboBox<>();
        childBox.setForeground(Color.BLACK);
        List<Child> children = DataManager.getAllChildren();
        for (Child c : children) childBox.addItem(c);
        
        childBox.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(value instanceof Child) setText(((Child)value).getName());
                setForeground(Color.BLACK);
                return this;
            }
        });

        d.add(createLabel("  Ödev Başlığı:")); d.add(titleF);
        d.add(createLabel("  Açıklama:")); d.add(descF);
        d.add(createLabel("  Puan:")); d.add(pointsF);
        d.add(createLabel("  Son Tarih (Y-A-G):")); d.add(dateF);
        d.add(createLabel("  Öğrenci Seç:")); d.add(childBox);

        JButton save = createFlatButton("Ödevi Ata", new Color(0xFB8C00));
    save.addActionListener(e -> {
    try {
        // AI Tutor Müdahalesi: Geçmiş tarih kontrolü eklendi [cite: 30, 56]
        java.time.LocalDate selectedDate = java.time.LocalDate.parse(dateF.getText());
        if (selectedDate.isBefore(java.time.LocalDate.now())) {
            JOptionPane.showMessageDialog(d, "Hata: Geçmiş bir tarihe ödev atayamazsınız!", "Tarih Hatası", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (childBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(d, "Öğrenci seçimi zorunludur.");
            return;
        }
        
        Task t = new Task(titleF.getText(), descF.getText(), Integer.parseInt(pointsF.getText()), 
                dateF.getText(), TaskStatus.PENDING, (Child)childBox.getSelectedItem());
        DataManager.addTask(t);
        refreshTable();
        d.dispose();
    } catch(Exception ex) { 
        JOptionPane.showMessageDialog(d, "Hata: Tarih formatı YYYY-MM-DD olmalıdır."); 
    }
});
        d.add(new JLabel("")); d.add(save);
        d.setVisible(true);
    }
    
    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.BLACK);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private void rateTask(Task t) {
        if (t.getStatus() != TaskStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Bu görev zaten sonuçlanmış.");
            return;
        }
        String score = JOptionPane.showInputDialog(this, "Ödevi Puanla (1-5):", "5");
        if(score == null) return;
        try {
            int rating = Integer.parseInt(score);
            t.setStatus(TaskStatus.APPROVED);
            t.setRating(rating);
            if(t.getAssignee() != null) t.getAssignee().setTotalPoints(t.getAssignee().getTotalPoints() + t.getPoints());
            DataManager.updateTask(t);
            refreshTable();
        } catch(Exception e) {}
    }
    
    // --- Renderers & Editors (FULL RENKLİ BUTON) ---
    class RateButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public RateButtonRenderer() { 
            setText("Puanla & Onayla"); 
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBackground(new Color(0x66BB6A)); // Yeşil
            setForeground(Color.WHITE);
            setOpaque(true);
            setBorderPainted(false);
            setFocusPainted(false);
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) { return this; }
    }
    
    class RateButtonEditor extends DefaultCellEditor {
        JButton b; Task t;
        public RateButtonEditor(JCheckBox cb) {
            super(cb); 
            b = new JButton("Puanla & Onayla");
            b.setFont(new Font("Segoe UI", Font.BOLD, 12));
            b.setBackground(new Color(0x66BB6A));
            b.setForeground(Color.WHITE);
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.addActionListener(e -> { fireEditingStopped(); if(t!=null) rateTask(t); });
        }
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) { t = (Task) value; return b; }
        public Object getCellEditorValue() { return t; }
    }
}