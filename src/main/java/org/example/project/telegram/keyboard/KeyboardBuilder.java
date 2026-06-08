package org.example.project.telegram.keyboard;

import org.example.project.entity.Filial;
import org.example.project.telegram.callback.CallbackDataFactory;
import org.example.project.telegram.enums.BotLanguage;
import org.example.project.telegram.enums.CallbackAction;
import org.example.project.telegram.i18n.MessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardBuilder {

    private final MessageService messages;

    public KeyboardBuilder(MessageService messages) {
        this.messages = messages;
    }

    public InlineKeyboardMarkup mainMenu(BotLanguage lang, boolean admin, int cartCount) {
        String cartLabel = messages.get("menu.cart", lang) + (cartCount > 0 ? " (" + cartCount + ")" : "");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row(btn(messages.get("menu.products", lang), CallbackDataFactory.build(CallbackAction.PRODUCTS, "0"))));
        rows.add(row(btn(messages.get("menu.categories", lang), CallbackDataFactory.build(CallbackAction.CATEGORIES, "0"))));
        rows.add(row(
                btn(cartLabel, CallbackDataFactory.build(CallbackAction.CART)),
                btn(messages.get("menu.wishlist", lang), CallbackDataFactory.build(CallbackAction.WISHLIST))
        ));
        rows.add(row(
                btn(messages.get("menu.orders", lang), CallbackDataFactory.build(CallbackAction.ORDERS, "0")),
                btn(messages.get("menu.addresses", lang), CallbackDataFactory.build(CallbackAction.ADDRESSES))
        ));
        rows.add(row(
                btn(messages.get("menu.coupons", lang), CallbackDataFactory.build(CallbackAction.COUPONS)),
                btn(messages.get("menu.settings", lang), CallbackDataFactory.build(CallbackAction.SETTINGS))
        ));
        rows.add(row(
                btn(messages.get("menu.language", lang), CallbackDataFactory.build(CallbackAction.LANG)),
                btn(messages.get("menu.contact", lang), CallbackDataFactory.build(CallbackAction.CONTACT))
        ));
        if (admin) {
            rows.add(row(btn(messages.get("menu.admin", lang), CallbackDataFactory.build(CallbackAction.ADMIN))));
        }
        return markup(rows);
    }

    public InlineKeyboardMarkup backHome(BotLanguage lang) {
        return markup(row(
                btn(messages.get("btn.back", lang), CallbackDataFactory.build(CallbackAction.BACK)),
                btn(messages.get("btn.home", lang), CallbackDataFactory.build(CallbackAction.MENU))
        ));
    }

    public InlineKeyboardMarkup homeOnly(BotLanguage lang) {
        return markup(row(btn(messages.get("btn.home", lang), CallbackDataFactory.build(CallbackAction.MENU))));
    }

    public InlineKeyboardMarkup productDetailWithQty(BotLanguage lang, int productId, int qty, int stock, int page, boolean available) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (available) {
            String incCallback = qty >= stock
                    ? CallbackDataFactory.build(CallbackAction.NOOP)
                    : CallbackDataFactory.build(CallbackAction.PRODUCT_INC, String.valueOf(productId));
            rows.add(row(
                    btn("➖", CallbackDataFactory.build(CallbackAction.PRODUCT_DEC, String.valueOf(productId))),
                    btn(String.valueOf(qty), CallbackDataFactory.build(CallbackAction.NOOP)),
                    btn(qty >= stock ? "⛔" : "➕", incCallback)
            ));
            rows.add(row(
                    btn("🛒 " + messages.get("btn.add_cart", lang),
                            CallbackDataFactory.build(CallbackAction.PRODUCT_ADD, String.valueOf(productId))),
                    btn(messages.get("btn.add_wishlist", lang),
                            CallbackDataFactory.build(CallbackAction.ADD_WISHLIST, String.valueOf(productId)))
            ));
        } else {
            rows.add(row(btn("❌ Mavjud emas", CallbackDataFactory.build(CallbackAction.NOOP))));
            rows.add(row(btn(messages.get("btn.add_wishlist", lang),
                    CallbackDataFactory.build(CallbackAction.ADD_WISHLIST, String.valueOf(productId)))));
        }
        rows.add(row(btn(messages.get("btn.back", lang),
                CallbackDataFactory.build(CallbackAction.PRODUCTS, String.valueOf(page)))));
        return markup(rows);
    }

    public InlineKeyboardMarkup productDetailButtons(BotLanguage lang, int productId, int page) {
        return markup(
                row(
                        btn(messages.get("btn.add_cart", lang),
                                CallbackDataFactory.build(CallbackAction.CART_ADD_CONFIRM, String.valueOf(productId), String.valueOf(page))),
                        btn(messages.get("btn.add_wishlist", lang),
                                CallbackDataFactory.build(CallbackAction.ADD_WISHLIST, String.valueOf(productId)))
                ),
                row(btn(messages.get("btn.back", lang),
                        CallbackDataFactory.build(CallbackAction.PRODUCTS, String.valueOf(page))))
        );
    }

    public InlineKeyboardMarkup filialList(BotLanguage lang, List<Filial> filials) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Filial f : filials) {
            String label = "🏪 " + (f.getTitle() != null ? f.getTitle() : "Filial #" + f.getId());
            rows.add(row(btn(label, CallbackDataFactory.build(CallbackAction.CHECKOUT_FILIAL, String.valueOf(f.getId())))));
        }
        rows.addAll(backHome(lang).getKeyboard());
        return markup(rows);
    }

    public InlineKeyboardMarkup couponStep(BotLanguage lang) {
        return markup(row(
                btn(messages.get("btn.coupon_yes", lang), CallbackDataFactory.build(CallbackAction.CHECKOUT_COUPON_YES)),
                btn(messages.get("btn.coupon_skip", lang), CallbackDataFactory.build(CallbackAction.CHECKOUT_COUPON_SKIP))
        ));
    }

    public InlineKeyboardMarkup pagination(BotLanguage lang, CallbackAction action, int page, int totalPages) {
        List<InlineKeyboardButton> nav = new ArrayList<>();
        if (page > 0) {
            nav.add(btn(messages.get("btn.prev", lang), CallbackDataFactory.build(CallbackAction.PAGE, action.name(), String.valueOf(page - 1))));
        }
        nav.add(btn((page + 1) + "/" + Math.max(totalPages, 1), CallbackDataFactory.build(CallbackAction.NOOP)));
        if (page < totalPages - 1) {
            nav.add(btn(messages.get("btn.next", lang), CallbackDataFactory.build(CallbackAction.PAGE, action.name(), String.valueOf(page + 1))));
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(nav);
        rows.addAll(backHome(lang).getKeyboard());
        return markup(rows);
    }

    public InlineKeyboardMarkup productActions(BotLanguage lang, int productId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row(
                btn(messages.get("btn.add_cart", lang), CallbackDataFactory.build(CallbackAction.ADD_CART, String.valueOf(productId))),
                btn(messages.get("btn.add_wishlist", lang), CallbackDataFactory.build(CallbackAction.ADD_WISHLIST, String.valueOf(productId)))
        ));
        rows.add(backHome(lang).getKeyboard().get(0));
        return markup(rows);
    }

    public InlineKeyboardMarkup cartMenu(BotLanguage lang) {
        return markup(
                row(btn(messages.get("btn.checkout", lang), CallbackDataFactory.build(CallbackAction.CART_CHECKOUT))),
                row(
                        btn(messages.get("btn.clear_cart", lang), CallbackDataFactory.build(CallbackAction.CART_CLEAR)),
                        btn(messages.get("btn.back", lang), CallbackDataFactory.build(CallbackAction.BACK))
                )
        );
    }

    public InlineKeyboardMarkup languageSelect(BotLanguage lang) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row(
                btn("🇺🇿 O'zbek", CallbackDataFactory.build(CallbackAction.LANG, "UZ")),
                btn("🇷🇺 Русский", CallbackDataFactory.build(CallbackAction.LANG, "RU")),
                btn("🇬🇧 English", CallbackDataFactory.build(CallbackAction.LANG, "EN"))
        ));
        rows.add(backHome(lang).getKeyboard().get(0));
        return markup(rows);
    }

    public InlineKeyboardMarkup deliveryType(BotLanguage lang) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row(btn("🚚 Yetkazib berish", CallbackDataFactory.build(CallbackAction.CHECKOUT_DELIVERY, "DELEVER"))));
        rows.add(row(btn("🏪 Olib ketish", CallbackDataFactory.build(CallbackAction.CHECKOUT_DELIVERY, "PICKUP"))));
        rows.add(backHome(lang).getKeyboard().get(0));
        return markup(rows);
    }

    public InlineKeyboardMarkup paymentType(BotLanguage lang) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(row(
                btn("💵 Naqd", CallbackDataFactory.build(CallbackAction.CHECKOUT_PAYMENT, "CASH")),
                btn("💳 Karta", CallbackDataFactory.build(CallbackAction.CHECKOUT_PAYMENT, "CARD"))
        ));
        rows.add(backHome(lang).getKeyboard().get(0));
        return markup(rows);
    }

    public InlineKeyboardMarkup cartConfirm(BotLanguage lang, int productId, int page) {
        return markup(row(
                btn("✅ Ha", CallbackDataFactory.build(CallbackAction.CART_ADD_CONFIRM,
                        String.valueOf(productId), String.valueOf(page))),
                btn("❌ Yo'q", CallbackDataFactory.build(CallbackAction.PRODUCTS, String.valueOf(page)))
        ));
    }

    public InlineKeyboardMarkup checkoutConfirm(BotLanguage lang) {
        return markup(row(
                btn("✅ Tasdiqlash", CallbackDataFactory.build(CallbackAction.CHECKOUT_CONFIRM)),
                btn("❌ Bekor", CallbackDataFactory.build(CallbackAction.CHECKOUT_CANCEL))
        ));
    }

    public InlineKeyboardMarkup paymentApproval(int orderId) {
        return markup(row(
                btn("✅ Tasdiqlash", CallbackDataFactory.build(CallbackAction.PAYMENT_APPROVE, String.valueOf(orderId))),
                btn("❌ Rad etish", CallbackDataFactory.build(CallbackAction.PAYMENT_REJECT, String.valueOf(orderId)))
        ));
    }

    public InlineKeyboardMarkup adminOrderActions(BotLanguage lang, int orderId) {
        return markup(
                row(
                        btn("✅ Qabul", CallbackDataFactory.build(CallbackAction.ADMIN_ACCEPT, String.valueOf(orderId))),
                        btn("❌ Bekor", CallbackDataFactory.build(CallbackAction.ADMIN_REJECT, String.valueOf(orderId)))
                ),
                row(
                        btn("📦 CONFIRMED", CallbackDataFactory.build(CallbackAction.ADMIN_STATUS, String.valueOf(orderId), "CONFIRMED")),
                        btn("🚚 ON_THE_WAY", CallbackDataFactory.build(CallbackAction.ADMIN_STATUS, String.valueOf(orderId), "ON_THE_WAY"))
                ),
                row(btn("✅ DELIVERED", CallbackDataFactory.build(CallbackAction.ADMIN_STATUS, String.valueOf(orderId), "DELIVERED"))),
                row(btn(messages.get("btn.back", lang), CallbackDataFactory.build(CallbackAction.ADMIN_ORDERS, "0")))
        );
    }

    public InlineKeyboardMarkup settingsMenu(BotLanguage lang) {
        return markup(
                row(btn(messages.get("btn.change_lang", lang), CallbackDataFactory.build(CallbackAction.SETTINGS_LANG))),
                row(
                        btn(messages.get("btn.back", lang), CallbackDataFactory.build(CallbackAction.BACK)),
                        btn(messages.get("btn.home", lang), CallbackDataFactory.build(CallbackAction.MENU))
                )
        );
    }

    public InlineKeyboardMarkup adminMenu(BotLanguage lang) {
        return markup(
                row(btn("📋 Buyurtmalar", CallbackDataFactory.build(CallbackAction.ADMIN_ORDERS, "0"))),
                row(btn("📊 Statistika", CallbackDataFactory.build(CallbackAction.ADMIN_STATS))),
                row(btn(messages.get("btn.home", lang), CallbackDataFactory.build(CallbackAction.MENU)))
        );
    }

    public InlineKeyboardMarkup listButtons(BotLanguage lang, CallbackAction action, List<ItemButton> items, int page, int totalPages) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (ItemButton item : items) {
            rows.add(row(btn(item.label(), CallbackDataFactory.build(action, item.id(), String.valueOf(page)))));
        }
        rows.addAll(pagination(lang, action, page, totalPages).getKeyboard());
        return markup(rows);
    }

    public record ItemButton(String id, String label) {
    }

    private InlineKeyboardButton btn(String text, String callback) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callback);
        return button;
    }

    @SafeVarargs
    private List<InlineKeyboardButton> row(InlineKeyboardButton... buttons) {
        return List.of(buttons);
    }

    private InlineKeyboardMarkup markup(List<List<InlineKeyboardButton>> rows) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    @SafeVarargs
    private InlineKeyboardMarkup markup(List<InlineKeyboardButton>... rows) {
        return markup(List.of(rows));
    }
}
