package org.example.project.telegram.i18n;

import org.example.project.telegram.enums.BotLanguage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageService {

    private static final Map<String, String[]> MESSAGES = Map.ofEntries(
            Map.entry("welcome", new String[]{
                    "🍽 <b>FoodStore</b> ga xush kelibsiz!\nQuyidagi menyudan tanlang:",
                    "🍽 Добро пожаловать в <b>FoodStore</b>!\nВыберите из меню:",
                    "🍽 Welcome to <b>FoodStore</b>!\nChoose from the menu:"
            }),
            Map.entry("menu.products", new String[]{"🍕 Mahsulotlar", "🍕 Food", "🍕 Food"}),
            Map.entry("menu.categories", new String[]{"📂 Kategoriyalar", "📂 Категории", "📂 Categories"}),
            Map.entry("menu.cart", new String[]{"🛒 Savat", "🛒 Корзина", "🛒 Cart"}),
            Map.entry("menu.wishlist", new String[]{"❤️ Sevimlilar", "❤️ Избранное", "❤️ Wishlist"}),
            Map.entry("menu.orders", new String[]{"📦 Buyurtmalarim", "📦 Мои заказы", "📦 My Orders"}),
            Map.entry("menu.addresses", new String[]{"📍 Manzillarim", "📍 Адреса", "📍 Addresses"}),
            Map.entry("menu.coupons", new String[]{"🎟 Kuponlarim", "🎟 Купоны", "🎟 Coupons"}),
            Map.entry("menu.settings", new String[]{"⚙️ Sozlamalar", "⚙️ Настройки", "⚙️ Settings"}),
            Map.entry("menu.language", new String[]{"🌐 Tilni almashtirish", "🌐 Сменить язык", "🌐 Change Language"}),
            Map.entry("menu.contact", new String[]{"☎️ Aloqa", "☎️ Контакты", "☎️ Contact"}),
            Map.entry("menu.admin", new String[]{"👑 Admin Panel", "👑 Admin Panel", "👑 Admin Panel"}),
            Map.entry("btn.back", new String[]{"⬅️ Orqaga", "⬅️ Назад", "⬅️ Back"}),
            Map.entry("btn.home", new String[]{"🏠 Bosh menyu", "🏠 Главное меню", "🏠 Main Menu"}),
            Map.entry("btn.prev", new String[]{"◀️", "◀️", "◀️"}),
            Map.entry("btn.next", new String[]{"▶️", "▶️", "▶️"}),
            Map.entry("btn.add_cart", new String[]{"🛒 Savatga", "🛒 В корзину", "🛒 Add to Cart"}),
            Map.entry("btn.add_wishlist", new String[]{"❤️ Sevimliarga", "❤️ В избранное", "❤️ Wishlist"}),
            Map.entry("btn.checkout", new String[]{"✅ Buyurtma berish", "✅ Оформить", "✅ Checkout"}),
            Map.entry("btn.clear_cart", new String[]{"🗑 Savatni tozalash", "🗑 Очистить", "🗑 Clear Cart"}),
            Map.entry("empty.cart", new String[]{
                    "Hurmatli mijoz, savatingiz hozircha bo'sh 🛒",
                    "Уважаемый клиент, ваша корзина пока пуста 🛒",
                    "Dear customer, your cart is currently empty 🛒"
            }),
            Map.entry("empty.cart.already", new String[]{
                    "Hurmatli mijoz, savatingiz allaqachon bo'sh 😊",
                    "Уважаемый клиент, корзина уже пуста 😊",
                    "Dear customer, your cart is already empty 😊"
            }),
            Map.entry("empty.wishlist", new String[]{"Sevimlilar bo'sh", "Избранное пусто", "Wishlist is empty"}),
            Map.entry("empty.orders", new String[]{"Buyurtmalar yo'q", "Заказов нет", "No orders"}),
            Map.entry("empty.products", new String[]{"Mahsulot topilmadi", "Товары не найдены", "No products found"}),
            Map.entry("error.generic", new String[]{"Xatolik yuz berdi", "Произошла ошибка", "An error occurred"}),
            Map.entry("success.added_cart", new String[]{"Savatga qo'shildi ✅", "Добавлено в корзину ✅", "Added to cart ✅"}),
            Map.entry("success.added_wishlist", new String[]{"Sevimlilarga qo'shildi ✅", "Добавлено в избранное ✅", "Added to wishlist ✅"}),
            Map.entry("cart.confirm", new String[]{
                    "🛒 Savatga qo'shilsinmi?",
                    "🛒 Добавить в корзину?",
                    "🛒 Add to cart?"
            }),
            Map.entry("checkout.payment_check", new String[]{
                    "💳 Iltimos, to'lov chekini (screenshot) yuboring:",
                    "💳 Пожалуйста, отправьте чек об оплате (скриншот):",
                    "💳 Please send your payment receipt (screenshot):"
            }),
            Map.entry("checkout.payment_check_sent", new String[]{
                    "✅ To'lov chekingiz adminga yuborildi!\nTasdiqlangandan so'ng buyurtmangiz aktivlashadi. Buyurtma",
                    "✅ Чек отправлен администратору!\nПосле подтверждения заказ будет активирован. Заказ",
                    "✅ Payment receipt sent to admin!\nYour order will be activated after confirmation. Order"
            }),
            Map.entry("checkout.phone", new String[]{"📱 Telefon raqamingizni yuboring (contact tugmasi yoki matn)", "📱 Отправьте номер телефона", "📱 Send your phone number"}),
            Map.entry("checkout.location", new String[]{"📍 Lokatsiyangizni yuboring", "📍 Отправьте локацию", "📍 Send your location"}),
            Map.entry("checkout.delivery", new String[]{"Yetkazish turini tanlang:", "Выберите тип доставки:", "Choose delivery type:"}),
            Map.entry("checkout.payment", new String[]{"To'lov turini tanlang:", "Выберите способ оплаты:", "Choose payment method:"}),
            Map.entry("checkout.confirm", new String[]{"Buyurtmani tasdiqlaysizmi?", "Подтвердить заказ?", "Confirm order?"}),
            Map.entry("checkout.success", new String[]{"Buyurtma qabul qilindi! 🎉", "Заказ принят! 🎉", "Order placed! 🎉"}),
            Map.entry("lang.select", new String[]{"Tilni tanlang:", "Выберите язык:", "Select language:"}),
            Map.entry("lang.changed", new String[]{"Til o'zgartirildi ✅", "Язык изменён ✅", "Language changed ✅"}),
            Map.entry("total", new String[]{"Jami", "Итого", "Total"}),
            Map.entry("price", new String[]{"Narx", "Цена", "Price"}),
            Map.entry("discount", new String[]{"Aksiya", "Акция", "Sale"}),
            Map.entry("rating", new String[]{"Reyting", "Рейтинг", "Rating"}),
            Map.entry("description", new String[]{"Tavsif", "Описание", "Description"}),
            Map.entry("order.status", new String[]{"Holat", "Статус", "Status"}),
            Map.entry("notification.new_order", new String[]{"🆕 Yangi buyurtma #", "🆕 Новый заказ #", "🆕 New order #"}),
            Map.entry("notification.status", new String[]{"Buyurtma holati yangilandi:", "Статус заказа обновлён:", "Order status updated:"}),
            Map.entry("settings.phone_request", new String[]{
                    "📱 Telefon raqamingizni ulashing:",
                    "📱 Поделитесь номером телефона:",
                    "📱 Share your phone number:"
            }),
            Map.entry("settings.phone_saved", new String[]{
                    "✅ Telefon raqam saqlandi: %s",
                    "✅ Номер телефона сохранён: %s",
                    "✅ Phone saved: %s"
            }),
            Map.entry("btn.share_phone", new String[]{
                    "📱 Telefon raqamni ulashish",
                    "📱 Поделиться номером",
                    "📱 Share Phone Number"
            }),
            Map.entry("btn.change_lang", new String[]{
                    "🌐 Tilni o'zgartirish",
                    "🌐 Изменить язык",
                    "🌐 Change Language"
            }),
            Map.entry("btn.change_phone", new String[]{
                    "📱 Telefon raqamni o'zgartirish",
                    "📱 Изменить номер телефона",
                    "📱 Change Phone Number"
            }),
            Map.entry("checkout.coupon_ask", new String[]{
                    "🎟 Kupon kodingiz bormi?",
                    "🎟 У вас есть купон?",
                    "🎟 Do you have a coupon?"
            }),
            Map.entry("checkout.coupon_input", new String[]{
                    "📝 Kupon kodini kiriting:",
                    "📝 Введите код купона:",
                    "📝 Enter coupon code:"
            }),
            Map.entry("checkout.coupon_applied", new String[]{
                    "✅ Kupon qo'llanildi! %.0f%% chegirma",
                    "✅ Купон применён! Скидка %.0f%%",
                    "✅ Coupon applied! %.0f%% discount"
            }),
            Map.entry("checkout.coupon_invalid", new String[]{
                    "❌ Kupon topilmadi yoki muddati tugagan",
                    "❌ Купон не найден или истёк",
                    "❌ Coupon not found or expired"
            }),
            Map.entry("btn.coupon_yes", new String[]{
                    "✅ Ha, kiritaman",
                    "✅ Да, введу",
                    "✅ Yes, enter code"
            }),
            Map.entry("btn.coupon_skip", new String[]{
                    "⏭ O'tkazib yuborish",
                    "⏭ Пропустить",
                    "⏭ Skip"
            }),
            Map.entry("checkout.filial_select", new String[]{
                    "🏪 Qaysi filialdan olib ketasiz?",
                    "🏪 Из какого филиала заберёте?",
                    "🏪 Which branch will you pick up from?"
            })
    );

    public String get(String key, BotLanguage lang) {
        String[] values = MESSAGES.get(key);
        if (values == null) return key;
        return values[lang.ordinal()];
    }

    public String get(String key, BotLanguage lang, Object... args) {
        return String.format(get(key, lang), args);
    }
}
