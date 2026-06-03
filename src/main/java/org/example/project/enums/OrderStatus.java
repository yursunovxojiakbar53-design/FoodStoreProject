package org.example.project.enums;


public enum OrderStatus {
    NEW,
    PENDING,      // Kutilmoqda — mijoz buyurtma berdi, restoran hali qabul qilmadi
    CONFIRMED,    // Tasdiqlandi — restoran qabul qildi, tayyorlash boshlandi
    ON_THE_WAY,   // Yo'lda — kuryer olib ketdi, mijozga yetib bormoqda
    DELIVERED,    // Yetkazildi — mijoz oldi, buyurtma yakunlandi
    CANCELED      // Bekor qilindi — mijoz yoki admin tomonidan bekor qilindi

}
