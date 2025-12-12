package com.kidtask.model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String USERS_FILE = "Users.txt";
    private static final String TASKS_FILE = "Tasks.txt";
    private static final String WISHES_FILE = "Wishes.txt";

    private static List<User> users = new ArrayList<>();
    private static List<Task> tasks = new ArrayList<>();
    private static List<Wish> wishes = new ArrayList<>();
    
    private static boolean initialized = false;

    public static void initialize() {
        if (!initialized) {
            loadAllData();
            initialized = true;
        }
    }

    // --- DOSYA YÖNETİMİ ---
    public static void loadAllData() {
        loadUsers();
        loadTasks();
        loadWishes();
        System.out.println("DataManager: Veriler yüklendi.");
    }

    public static void saveAll() {
        saveUsers();
        saveTasks();
        saveWishes();
    }

    // --- KULLANICILAR (USERS) ---
    public static List<User> getUsers() { return users; }

    public static void addUser(User user) {
        users.add(user);
        saveUsers();
    }

    public static User getUserById(String id) {
        for (User u : users) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    // LoginView için gerekli
    public static User getFirstUserByRole(String role) {
        for (User u : users) {
            if ("CHILD".equalsIgnoreCase(role) && u instanceof Child) return u;
            if ("PARENT".equalsIgnoreCase(role) && u instanceof Parent) return u;
            if ("TEACHER".equalsIgnoreCase(role) && u instanceof Teacher) return u;
        }
        return null;
    }

    // Öğretmen ve Veli panelleri için gerekli
    public static List<Child> getAllChildren() {
        List<Child> children = new ArrayList<>();
        for (User u : users) {
            if (u instanceof Child) children.add((Child) u);
        }
        return children;
    }

    // --- GÖREVLER (TASKS) ---
    public static List<Task> getTasks() { return tasks; }

    public static void addTask(Task t) {
        tasks.add(t);
        saveTasks();
    }

    public static void updateTask(Task t) {
        saveTasks(); // Referans zaten listede olduğu için sadece kaydetmek yeterli
    }

    // Öğrenci paneli için gerekli
    public static List<Task> getTasksForChild(Child c) {
        List<Task> list = new ArrayList<>();
        if (c == null) return list;
        for (Task t : tasks) {
            if (t.getAssignee() != null && t.getAssignee().getId().equals(c.getId())) {
                list.add(t);
            }
        }
        return list;
    }

    // --- DİLEKLER (WISHES) ---
    public static List<Wish> getWishes() { return wishes; }

    public static void addWish(Wish w) {
        wishes.add(w);
        saveWishes();
    }

    public static void updateWish(Wish w) {
        saveWishes();
    }

    // --- DOSYA OKUMA/YAZMA İŞLEMLERİ ---
    
    private static void loadUsers() {
        users.clear();
        File f = new File(USERS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if(p.length < 4) continue;
                if (p[0].equals("CHILD")) {
                    int pts = (p.length > 4) ? Integer.parseInt(p[4]) : 0;
                    users.add(new Child(p[1], p[2], p[3], pts));
                } else if (p[0].equals("PARENT")) users.add(new Parent(p[1], p[2], p[3]));
                else if (p[0].equals("TEACHER")) users.add(new Teacher(p[1], p[2], p[3]));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(USERS_FILE), StandardCharsets.UTF_8))) {
            for (User u : users) {
                String type = (u instanceof Child) ? "CHILD" : (u instanceof Parent) ? "PARENT" : "TEACHER";
                String line = type + "|" + u.getId() + "|" + u.getName() + "|" + u.getEmail();
                if (u instanceof Child) line += "|" + ((Child)u).getTotalPoints();
                pw.println(line);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void loadTasks() {
        tasks.clear();
        File f = new File(TASKS_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if(p.length < 7) continue;
                TaskStatus st = TaskStatus.valueOf(p[5]);
                User u = getUserById(p[6]);
                Child c = (u instanceof Child) ? (Child)u : null;
                Task t = new Task(p[0], p[1], Integer.parseInt(p[2]), p[3], st, c);
                t.setRating(Integer.parseInt(p[4]));
                tasks.add(t);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void saveTasks() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(TASKS_FILE), StandardCharsets.UTF_8))) {
            for (Task t : tasks) {
                String cid = (t.getAssignee()!=null) ? t.getAssignee().getId() : "null";
                String dd = (t.getDueDate()==null) ? "null" : t.getDueDate();
                pw.println(t.getTitle()+"|"+t.getDescription()+"|"+t.getPoints()+"|"+dd+"|"+t.getRating()+"|"+t.getStatus()+"|"+cid);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void loadWishes() {
        wishes.clear();
        File f = new File(WISHES_FILE);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.trim().isEmpty()) continue;
                String[] p = line.split("\\|");
                if(p.length < 4) continue;
                User u = getUserById(p[3]);
                Child c = (u instanceof Child) ? (Child)u : null;
                Wish w = new Wish(p[0], Integer.parseInt(p[1]), c);
                w.setStatus(p[2]);
                wishes.add(w);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void saveWishes() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(WISHES_FILE), StandardCharsets.UTF_8))) {
            for (Wish w : wishes) {
                String cid = (w.getChild()!=null) ? w.getChild().getId() : "null";
                pw.println(w.getName()+"|"+w.getCost()+"|"+w.getStatus()+"|"+cid);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}