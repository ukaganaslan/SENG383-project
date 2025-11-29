package com.kidtask.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Kullanıcı, Görev ve Dilek verilerini yöneten dosya tabanlı veri tabanı sınıfı.
 */
public final class DataManager {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";
    private static final String TASKS_FILE = DATA_DIR + File.separator + "tasks.txt";
    private static final String WISHES_FILE = DATA_DIR + File.separator + "wishes.txt";
    
    // Verileri bellekte tutacak listeler
    private static final List<User> USERS = new ArrayList<>();
    private static final List<Task> TASKS = new ArrayList<>();
    private static final List<Wish> WISHES = new ArrayList<>();
    
    private static boolean initialized = false;

    private DataManager() {
        // Utility sınıfı olduğu için private constructor
    }

    /**
     * Veri yöneticisini başlatır ve dosyaları yükler.
     */
    public static void initialize() {
        if (initialized) return;
        
        try {
            // Data klasörünü oluştur
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            
            // Dosyaları yükle
            loadUsers();
            loadTasks();
            loadWishes();
            
            initialized = true;
            System.out.println("DataManager başlatıldı. Kullanıcılar: " + USERS.size());
        } catch (Exception e) {
            System.err.println("DataManager başlatılırken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void saveAll() {
        try {
            saveUsers();
            saveTasks();
            saveWishes();
        } catch (IOException e) {
            System.err.println("Veriler kaydedilirken hata: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ================= USERS (KULLANICILAR) =================

    public static void addUser(User user) {
        ensureInitialized();
        if (user != null) {
            // ID çakışmasını önlemek için kontrol eklenebilir
            USERS.add(user);
            saveAll();
        }
    }

    public static List<User> getUsers() {
        ensureInitialized();
        return Collections.unmodifiableList(USERS);
    }
    
    /**
     * ID'ye göre tek bir kullanıcı getirir.
     */
    public static User getUserById(String id) {
        ensureInitialized();
        for (User u : USERS) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Rolüne göre (CHILD, PARENT, TEACHER) ilk kullanıcıyı getirir (Login için).
     */
    public static User getFirstUserByRole(String role) {
        ensureInitialized();
        for (User u : USERS) {
            if ("CHILD".equalsIgnoreCase(role) && u instanceof Child) return u;
            if ("PARENT".equalsIgnoreCase(role) && u instanceof Parent) return u;
            if ("TEACHER".equalsIgnoreCase(role) && u instanceof Teacher) return u;
        }
        return null;
    }
    
    /**
     * Tüm öğrencileri listeler (Öğretmen panelinde seçim için).
     */
    public static List<Child> getAllChildren() {
        ensureInitialized();
        List<Child> children = new ArrayList<>();
        for (User u : USERS) {
            if (u instanceof Child) {
                children.add((Child) u);
            }
        }
        return children;
    }

    private static void loadUsers() throws IOException {
        USERS.clear();
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    User u = parseUser(line);
                    if (u != null) USERS.add(u);
                } catch (Exception e) {
                    System.err.println("Kullanıcı satırı okunamadı: " + line);
                }
            }
        }
    }

    private static void saveUsers() throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(USERS_FILE), StandardCharsets.UTF_8))) {
            writer.println("# TYPE|ID|NAME|EMAIL|POINTS");
            for (User user : USERS) {
                writer.println(serializeUser(user));
            }
        }
    }

    private static User parseUser(String line) {
        // Format: TYPE|ID|NAME|EMAIL|POINTS
        String[] parts = line.split("\\|");
        if (parts.length < 4) return null;

        String type = parts[0].trim();
        String id = parts[1].trim();
        String name = parts[2].trim();
        String email = parts[3].trim();

        if ("CHILD".equals(type)) {
            int points = 0;
            if (parts.length > 4) {
                try {
                    points = Integer.parseInt(parts[4].trim());
                } catch (NumberFormatException e) {
                    points = 0;
                }
            }
            return new Child(id, name, email, points);
        } else if ("PARENT".equals(type)) {
            return new Parent(id, name, email);
        } else if ("TEACHER".equals(type)) {
            return new Teacher(id, name, email);
        }
        return null;
    }

    private static String serializeUser(User u) {
        StringBuilder sb = new StringBuilder();
        if (u instanceof Child) {
            sb.append("CHILD|");
        } else if (u instanceof Parent) {
            sb.append("PARENT|");
        } else if (u instanceof Teacher) {
            sb.append("TEACHER|");
        } else {
            return "";
        }
        
        sb.append(u.getId()).append("|")
          .append(u.getName()).append("|")
          .append(u.getEmail());

        if (u instanceof Child) {
            sb.append("|").append(((Child) u).getTotalPoints());
        }
        return sb.toString();
    }

    // ================= TASKS (GÖREVLER) =================

    public static void addTask(Task t) {
        ensureInitialized();
        if (t != null) {
            TASKS.add(t);
            saveAll();
        }
    }

    public static void updateTask(Task t) {
        // Task nesnesi referans olduğu için listede zaten günceldir, sadece dosyaya yazıyoruz.
        saveAll();
    }

    public static List<Task> getTasks() {
        ensureInitialized();
        return TASKS; // Modifiable list dönüyoruz ki arayüzde filtreleme yapılabilsin
    }

    public static List<Task> getTasksForChild(Child c) {
        ensureInitialized();
        List<Task> list = new ArrayList<>();
        if (c == null) return list;
        for (Task t : TASKS) {
            if (t.getAssignee() != null && t.getAssignee().getId().equals(c.getId())) {
                list.add(t);
            }
        }
        return list;
    }
    
    public static List<Task> getPendingTasks() {
        ensureInitialized();
        List<Task> list = new ArrayList<>();
        for (Task t : TASKS) {
            if (t.getStatus() == TaskStatus.PENDING) {
                list.add(t);
            }
        }
        return list;
    }

    private static void loadTasks() throws IOException {
        TASKS.clear();
        File file = new File(TASKS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    Task t = parseTask(line);
                    if (t != null) TASKS.add(t);
                } catch (Exception e) {
                    System.err.println("Görev satırı okunamadı: " + line);
                }
            }
        }
    }

    private static void saveTasks() throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(TASKS_FILE), StandardCharsets.UTF_8))) {
            writer.println("# TITLE|DESC|POINTS|DUEDATE|RATING|STATUS|ASSIGNEE_ID");
            for (Task t : TASKS) {
                writer.println(serializeTask(t));
            }
        }
    }

    private static Task parseTask(String line) {
        // Format: TITLE|DESC|POINTS|DUEDATE|RATING|STATUS|ASSIGNEE_ID
        String[] parts = line.split("\\|");
        if (parts.length < 7) return null;

        String title = parts[0].trim();
        String desc = parts[1].trim();
        int points = Integer.parseInt(parts[2].trim());
        String dueDate = parts[3].trim();
        int rating = Integer.parseInt(parts[4].trim());
        TaskStatus status = TaskStatus.valueOf(parts[5].trim());
        String assigneeId = parts[6].trim();

        Child assignee = null;
        for (User u : USERS) {
            if (u instanceof Child && u.getId().equals(assigneeId)) {
                assignee = (Child) u;
                break;
            }
        }
        
        // DueDate null kontrolü
        if (dueDate.equals("null") || dueDate.equals("None")) dueDate = "";

        Task t = new Task(title, desc, points, dueDate, status, assignee);
        t.setRating(rating);
        return t;
    }

    private static String serializeTask(Task t) {
        String assigneeId = (t.getAssignee() != null) ? t.getAssignee().getId() : "";
        String dDate = (t.getDueDate() == null || t.getDueDate().isEmpty()) ? "None" : t.getDueDate();
        
        return t.getTitle() + "|" + 
               t.getDescription() + "|" + 
               t.getPoints() + "|" + 
               dDate + "|" + 
               t.getRating() + "|" + 
               t.getStatus() + "|" + 
               assigneeId;
    }

    // ================= WISHES (DİLEKLER) =================

    public static void addWish(Wish w) {
        ensureInitialized();
        if (w != null) {
            WISHES.add(w);
            saveAll();
        }
    }
    
    public static void updateWish(Wish w) {
        saveAll();
    }

    public static List<Wish> getWishes() {
        ensureInitialized();
        return WISHES;
    }

    public static List<Wish> getWishesForChild(Child c) {
        ensureInitialized();
        List<Wish> list = new ArrayList<>();
        if (c == null) return list;
        for (Wish w : WISHES) {
            if (w.getChild() != null && w.getChild().getId().equals(c.getId())) {
                list.add(w);
            }
        }
        return list;
    }

    private static void loadWishes() throws IOException {
        WISHES.clear();
        File file = new File(WISHES_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                try {
                    Wish w = parseWish(line);
                    if (w != null) WISHES.add(w);
                } catch (Exception e) {
                    System.err.println("Dilek satırı okunamadı: " + line);
                }
            }
        }
    }

    private static void saveWishes() throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WISHES_FILE), StandardCharsets.UTF_8))) {
            writer.println("# NAME|COST|STATUS|CHILD_ID");
            for (Wish w : WISHES) {
                writer.println(serializeWish(w));
            }
        }
    }

    private static Wish parseWish(String line) {
        // Format: NAME|COST|STATUS|CHILD_ID
        String[] parts = line.split("\\|");
        if (parts.length < 4) return null;

        String name = parts[0].trim();
        int cost = Integer.parseInt(parts[1].trim());
        String status = parts[2].trim();
        String childId = parts[3].trim();

        Child child = null;
        for (User u : USERS) {
            if (u instanceof Child && u.getId().equals(childId)) {
                child = (Child) u;
                break;
            }
        }

        Wish w = new Wish(name, cost, child);
        w.setStatus(status);
        return w;
    }

    private static String serializeWish(Wish w) {
        String childId = (w.getChild() != null) ? w.getChild().getId() : "";
        return w.getName() + "|" + w.getCost() + "|" + w.getStatus() + "|" + childId;
    }
    
    // ================= YARDIMCI METOTLAR =================
    
    private static void ensureInitialized() {
        if (!initialized) {
            initialize();
        }
    }
}