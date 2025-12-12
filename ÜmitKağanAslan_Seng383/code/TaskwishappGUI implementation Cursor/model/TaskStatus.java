package com.kidtask.model;

public enum TaskStatus {
    PENDING,    // 1. Öğretmen/Veli atadı, çocuk henüz yapmadı.
    COMPLETED,  // 2. Çocuk "Yaptım" dedi, veli onayı bekliyor.
    APPROVED,   // 3. Veli onayladı, puan kazanıldı.
    REJECTED    // 4. Veli reddetti.
}