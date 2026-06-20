package org.example.project.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project.dto.AddressDto;
import org.example.project.dto.CartDto;
import org.example.project.dto.CartItemDto;
import org.example.project.dto.OrderDto;
import org.example.project.dto.OrderResponseDto;
import org.example.project.entity.*;
import org.example.project.enums.DeliverType;
import org.example.project.enums.OrderStatus;
import org.example.project.enums.PaymentType;
import org.example.project.extra.ApiResponse;
import org.example.project.telegram.callback.CallbackDataFactory;
import org.example.project.telegram.callback.CallbackDataFactory.ParsedCallback;
import org.example.project.telegram.config.TelegramBotProperties;
import org.example.project.telegram.entity.TelegramSession;
import org.example.project.telegram.entity.TelegramUser;
import org.example.project.telegram.enums.BotLanguage;
import org.example.project.telegram.enums.BotState;
import org.example.project.telegram.enums.CallbackAction;
import org.example.project.telegram.exception.TelegramExceptionHandler;
import org.example.project.telegram.i18n.MessageService;
import org.example.project.telegram.keyboard.KeyboardBuilder;
import org.example.project.telegram.mapper.ProductMessageMapper;
import org.example.project.telegram.service.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotUpdateHandler {

    private final TelegramUserService userService;
    private final TelegramSessionService sessionService;
    private final FoodStoreFacadeService store;
    private final BotMessenger messenger;
    private final KeyboardBuilder keyboard;
    private final MessageService messages;
    private final ProductMessageMapper productMapper;
    private final TelegramNotificationService notifications;
    private final TelegramBotProperties properties;
    private final TelegramExceptionHandler exceptionHandler;

    public void handle(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                handleCallback(update);
            } else if (update.hasMessage()) {
                handleMessage(update);
            }
        }catch (Exception ex) {
            Long chatId = resolveChatId(update);
            if (chatId != null) exceptionHandler.handle(chatId, ex);
        }
    }

    private void handleMessage(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        TelegramUser user = userService.getOrCreate(message.getFrom());
        TelegramSession session = sessionService.getSession(user);

        if (message.hasText() && message.getText().trim().startsWith("/")) {
            String cmd = message.getText().trim().split("\\s+")[0].toLowerCase();
            switch (cmd) {
                case "/start" -> {
                    sessionService.resetToMain(user);
                    userService.save(user);
                    if (user.getPhoneNumber() == null) {
                        requestPhoneNumber(user.getChatId(), user.getLanguage());
                    } else {
                        showMainMenu(user);
                    }
                }
                case "/help" -> sendHelpMessage(user, chatId);
                case "/menu" -> { sessionService.resetToMain(user); showMainMenu(user); }
                case "/orders" -> sendOrdersList(user, chatId);
                case "/cart" -> sendCartInfo(user, chatId);
                case "/cancel" -> { sessionService.resetToMain(user); showMainMenu(user); }
                default -> showMainMenu(user);
            }
            return;
        }

        if (message.hasContact()) {
            Contact contact = message.getContact();
            if (session.getState() == BotState.CHECKOUT_PHONE) {
                sessionService.setPhoneNumber(user, contact.getPhoneNumber());
                askDeliveryType(user);
            } else {
                user.setPhoneNumber(contact.getPhoneNumber());
                userService.save(user);
                messenger.removeReplyKeyboard(chatId,
                        messages.get("settings.phone_saved", user.getLanguage(), contact.getPhoneNumber()));
                sessionService.resetToMain(user);
                showMainMenu(user);
            }
            return;
        }

        if (message.hasPhoto() && session.getState() == BotState.CHECKOUT_PAYMENT_CHECK) {
            String fileId = message.getPhoto().get(message.getPhoto().size() - 1).getFileId();
            processCardPaymentCheck(user, session, chatId, fileId);
            return;
        }

        if (message.hasLocation() && session.getState() == BotState.CHECKOUT_LOCATION) {
            Location loc = message.getLocation();
            sessionService.setLocationData(user, loc.getLatitude(), loc.getLongitude());
            TelegramSession freshSession = sessionService.getSession(user);
            saveAddressAndContinueCheckout(user, freshSession);
            return;
        }

        if (message.hasText()) {
            String text = message.getText().trim();
            switch (session.getState()) {
                case CHECKOUT_PHONE -> {
                    sessionService.setPhoneNumber(user, text);
                    askDeliveryType(user);
                }
                case ADMIN_BROADCAST_INPUT -> doBroadcast(user, chatId, text);
                case ADMIN_CAT_ADD_NAME -> finishAddCategory(user, chatId, text);
                case ADMIN_PROD_ADD_NAME -> {
                    TelegramSession s = sessionService.getSession(user);
                    s.setDraftMessage(text);
                    s.setState(BotState.ADMIN_PROD_ADD_PRICE);
                    user.setCurrentState(BotState.ADMIN_PROD_ADD_PRICE);
                    sessionService.save(s);
                    messenger.sendText(chatId, "2️⃣ Narxini yuboring (faqat raqam, masalan: <b>45000</b>):");
                }
                case ADMIN_PROD_ADD_PRICE -> handleProductPriceInput(user, chatId, text);
                case CHECKOUT_COUPON -> {
                    List<Coupon> coupons = store.getCoupons();
                    Optional<Coupon> found = coupons.stream()
                            .filter(c -> c.isActive()
                                    && c.getCode().equalsIgnoreCase(text)
                                    && (c.getExpiresAt() == null || c.getExpiresAt().isAfter(LocalDateTime.now())))
                            .findFirst();
                    if (found.isPresent()) {
                        sessionService.setCouponCode(user, text.trim());
                        messenger.sendText(user.getChatId(),
                                messages.get("checkout.coupon_applied", user.getLanguage(), found.get().getDiscountPercent()));
                    } else {
                        messenger.sendText(user.getChatId(), messages.get("checkout.coupon_invalid", user.getLanguage()));
                    }
                    sessionService.updateState(user, BotState.CHECKOUT_PAYMENT);
                    messenger.sendText(user.getChatId(), messages.get("checkout.payment", user.getLanguage()),
                            keyboard.paymentType(user.getLanguage()));
                }
                default -> showMainMenu(user);
            }
        }
    }

    private void handleCallback(Update update) {
        var cq = update.getCallbackQuery();
        Long chatId = cq.getMessage().getChatId();
        Integer messageId = cq.getMessage().getMessageId();
        TelegramUser user = userService.getOrCreate(cq.getFrom());
        TelegramSession session = sessionService.getSession(user);
        ParsedCallback cb = CallbackDataFactory.parse(cq.getData());

        try {
            switch (cb.action()) {
                case MENU -> {
                    sessionService.resetToMain(user);
                    editMainMenu(user, chatId, messageId);
                }
                case BACK -> goBack(user, session, chatId, messageId);
                case LANG -> handleLanguage(user, session, cb, chatId, messageId);
                case PRODUCTS -> showProducts(user, session, cb.paramAsInt(0, 0), chatId, messageId);
                case PRODUCT -> showProductConfirm(user, cb.paramAsInt(0, 0), cb.paramAsInt(1, 0), chatId, messageId);
                case CART_ADD_CONFIRM -> {
                    int productId = cb.paramAsInt(0, 0);
                    int page = cb.paramAsInt(1, 0);
                    store.addToCart(user, productId, 1);
                    showProducts(user, session, page, chatId, messageId);
                    messenger.sendText(chatId, messages.get("success.added_cart", user.getLanguage()));
                }
                case CATEGORIES -> showCategories(user, session, cb.paramAsInt(0, 0), chatId, messageId);
                case CATEGORY -> showCategoryProducts(user, session, cb.paramAsInt(0, 0), cb.paramAsInt(1, 0), chatId, messageId);
                case CART -> showCart(user, chatId, messageId);
                case CART_INC -> changeCartQty(user, cb.paramAsInt(0, 0), 1, chatId, messageId);
                case CART_DEC -> changeCartQty(user, cb.paramAsInt(0, 0), -1, chatId, messageId);
                case CART_REMOVE -> removeCartItem(user, cb.paramAsInt(0, 0), chatId, messageId);
                case CART_CLEAR -> {
                    CartDto currentCart = store.getCart(user);
                    if (currentCart.getItems() == null || currentCart.getItems().isEmpty()) {
                        messenger.editText(chatId, messageId,
                                messages.get("empty.cart.already", user.getLanguage()),
                                keyboard.backHome(user.getLanguage()));
                    } else {
                        store.clearCart(user);
                        showCart(user, chatId, messageId);
                    }
                }
                case CART_CHECKOUT -> startCheckout(user, session, chatId);
                case ADD_CART -> {
                    store.addToCart(user, cb.paramAsInt(0, 0), 1);
                    messenger.sendText(chatId, messages.get("success.added_cart", user.getLanguage()));
                }
                case ADD_WISHLIST -> {
                    store.addToWishlist(user, cb.paramAsInt(0, 0));
                    messenger.sendText(chatId, messages.get("success.added_wishlist", user.getLanguage()));
                }
                case PRODUCT_INC -> {
                    int pid = cb.paramAsInt(0, 0);
                    Product prod = store.getProduct(pid).orElseThrow();
                    TelegramSession s = sessionService.getSession(user);
                    int stock = prod.getStockQuantity() != null ? prod.getStockQuantity() : 0;
                    int curQty = s.getDraftQuantity() != null ? s.getDraftQuantity() : 1;
                    if (curQty < stock) sessionService.setDraftQuantity(user, curQty + 1);
                    showProductConfirm(user, pid, s.getPage() != null ? s.getPage() : 0, chatId, messageId);
                }
                case PRODUCT_DEC -> {
                    int pid = cb.paramAsInt(0, 0);
                    TelegramSession s = sessionService.getSession(user);
                    int curQty = s.getDraftQuantity() != null ? s.getDraftQuantity() : 1;
                    if (curQty > 1) sessionService.setDraftQuantity(user, curQty - 1);
                    showProductConfirm(user, pid, s.getPage() != null ? s.getPage() : 0, chatId, messageId);
                }
                case PRODUCT_ADD -> {
                    int pid = cb.paramAsInt(0, 0);
                    TelegramSession s = sessionService.getSession(user);
                    int addQty = s.getDraftQuantity() != null ? s.getDraftQuantity() : 1;
                    store.addToCart(user, pid, addQty);
                    sessionService.setDraftQuantity(user, 1);
                    messenger.sendText(chatId, messages.get("success.added_cart", user.getLanguage()));
                }
                case WISHLIST -> showWishlist(user, chatId, messageId);
                case WISHLIST_REMOVE -> {
                    store.removeWishlist(user, cb.paramAsInt(0, 0));
                    showWishlist(user, chatId, messageId);
                }
                case ORDERS -> showOrders(user, chatId, messageId);
                case ORDER -> showOrderDetail(user, cb.paramAsInt(0, 0), chatId, messageId);
                case ORDER_CANCEL -> {
                    store.cancelOrder(user, cb.paramAsInt(0, 0));
                    showOrders(user, chatId, messageId);
                }
                case ADDRESSES -> showAddresses(user, chatId, messageId);
                case ADDRESS_DEL -> {
                    store.deleteAddress(user, cb.paramAsInt(0, 0));
                    showAddresses(user, chatId, messageId);
                }
                case COUPONS -> showCoupons(user, chatId, messageId);
                case COUPON_APPLY -> {
                    session.setCouponCode(cb.param(0));
                    sessionService.save(session);
                    messenger.sendText(chatId, "Kupon saqlandi: " + cb.param(0));
                }
                case SETTINGS -> showSettings(user, chatId, messageId);
                case SETTINGS_LANG -> {
                    messenger.editText(chatId, messageId, messages.get("lang.select", user.getLanguage()), keyboard.languageSelect(user.getLanguage()));
                    sessionService.updateState(user, BotState.LANGUAGE_SELECT);
                }
                case CONTACT -> showContact(user, chatId, messageId);
                case CHECKOUT_COUPON_YES -> {
                    messenger.sendText(chatId, messages.get("checkout.coupon_input", user.getLanguage()));
                }
                case CHECKOUT_COUPON_SKIP -> {
                    sessionService.updateState(user, BotState.CHECKOUT_PAYMENT);
                    messenger.editText(chatId, messageId, messages.get("checkout.payment", user.getLanguage()),
                            keyboard.paymentType(user.getLanguage()));
                }
                case CHECKOUT_FILIAL -> {
                    int filialId = cb.paramAsInt(0, 0);
                    sessionService.setFilialIdAndState(user, filialId, BotState.CHECKOUT_COUPON);
                    messenger.editText(chatId, messageId, messages.get("checkout.coupon_ask", user.getLanguage()),
                            keyboard.couponStep(user.getLanguage()));
                }
                case CHECKOUT_DELIVERY -> handleDeliveryType(user, session, cb.param(0), chatId, messageId);
                case CHECKOUT_PAYMENT -> handlePaymentType(user, session, cb.param(0), chatId, messageId);
                case CHECKOUT_CONFIRM -> confirmOrder(user, session, chatId);
                case CHECKOUT_CANCEL -> {
                    session.resetCheckout();
                    sessionService.resetToMain(user);
                    editMainMenu(user, chatId, messageId);
                }
                case PAYMENT_APPROVE -> {
                    int orderId = cb.paramAsInt(0, 0);
                    store.changeOrderStatus(orderId, OrderStatus.CONFIRMED);
                    notifications.findAndNotify(orderId, OrderStatus.CONFIRMED);
                    messenger.sendText(chatId, "✅ Buyurtma #" + orderId + " tasdiqlandi va aktivlashtirildi");
                }
                case PAYMENT_REJECT -> {
                    int orderId = cb.paramAsInt(0, 0);
                    store.changeOrderStatus(orderId, OrderStatus.CANCELED);
                    notifications.findAndNotify(orderId, OrderStatus.CANCELED);
                    messenger.sendText(chatId, "❌ Buyurtma #" + orderId + " rad etildi");
                }
                case ADMIN -> showAdminMenu(user, chatId, messageId);
                case ADMIN_ORDERS -> showAdminOrders(user, cb.paramAsInt(0, 0), chatId, messageId);
                case ADMIN_ORDER -> showAdminOrderDetail(user, cb.paramAsInt(0, 0), chatId, messageId);
                case ADMIN_ACCEPT -> adminChangeStatus(user, cb.paramAsInt(0, 0), OrderStatus.CONFIRMED, chatId);
                case ADMIN_REJECT -> adminChangeStatus(user, cb.paramAsInt(0, 0), OrderStatus.CANCELED, chatId);
                case ADMIN_STATUS -> adminChangeStatus(user, cb.paramAsInt(0, 0), OrderStatus.valueOf(cb.param(1)), chatId);
                case ADMIN_STATS -> showAdminStats(user, chatId, messageId);
                case ADMIN_USERS -> showAdminUsers(user, chatId, messageId);
                case ADMIN_BROADCAST -> startBroadcast(user, chatId, messageId);
                case ADMIN_PRODUCTS -> showAdminProducts(user, cb.paramAsInt(0, 0), chatId, messageId);
                case ADMIN_PRODUCT_TOGGLE -> toggleProduct(user, cb.paramAsInt(0, 0), cb.paramAsInt(1, 0), chatId, messageId);
                case ADMIN_PROD_ADD -> startAddProduct(user, chatId, messageId);
                case ADMIN_PROD_CAT -> finishAddProduct(user, cb.paramAsInt(0, 0), chatId, messageId);
                case ADMIN_CATEGORIES -> showAdminCategories(user, chatId, messageId);
                case ADMIN_CAT_ADD -> startAddCategory(user, chatId, messageId);
                case ADMIN_COUPONS -> showAdminCoupons(user, chatId, messageId);
                case ADMIN_COUPON_TOGGLE -> toggleCoupon(user, cb.paramAsInt(0, 0), chatId, messageId);
                case PAGE -> handlePage(user, session, cb, chatId, messageId);
                default -> { }
            }
        } catch (Exception ex) {
            exceptionHandler.handle(chatId, ex);
        }
    }

    private void handlePage(TelegramUser user, TelegramSession session, ParsedCallback cb, Long chatId, Integer messageId) {
        CallbackAction action = CallbackAction.valueOf(cb.param(0));
        int page = cb.paramAsInt(1, 0);
        switch (action) {
            case PRODUCTS -> showProducts(user, session, page, chatId, messageId);
            case CATEGORIES -> showCategories(user, session, page, chatId, messageId);
            case CATEGORY -> showCategoryProducts(user, session, session.getCategoryId(), page, chatId, messageId);
            case ADMIN_ORDERS -> showAdminOrders(user, page, chatId, messageId);
            case ADMIN_PRODUCTS -> showAdminProducts(user, page, chatId, messageId);
            default -> showProducts(user, session, page, chatId, messageId);
        }
    }

    private void goBack(TelegramUser user, TelegramSession session, Long chatId, Integer messageId) {
        BotState state = session.getState();
        if (state == BotState.PRODUCT_DETAIL) {
            showProducts(user, session, session.getPage() != null ? session.getPage() : 0, chatId, messageId);
        } else if (state == BotState.CATEGORY_PRODUCTS) {
            showCategories(user, session, 0, chatId, messageId);
        } else if (state == BotState.ADMIN_ORDER_DETAIL) {
            showAdminOrders(user, 0, chatId, messageId);
        } else {
            sessionService.resetToMain(user);
            editMainMenu(user, chatId, messageId);
        }
    }

    private void handleLanguage(TelegramUser user, TelegramSession session, ParsedCallback cb, Long chatId, Integer messageId) {
        if (cb.params().length == 0) {
            messenger.editText(chatId, messageId, messages.get("lang.select", user.getLanguage()), keyboard.languageSelect(user.getLanguage()));
            sessionService.updateState(user, BotState.LANGUAGE_SELECT);
            return;
        }
        user.setLanguage(BotLanguage.fromCode(cb.param(0)));
        userService.save(user);
        sessionService.resetToMain(user);
        messenger.sendText(chatId, messages.get("lang.changed", user.getLanguage()));
        showMainMenu(user);
    }

    private void showMainMenu(TelegramUser user) {
        int cartCount = getCartCount(user);
        messenger.sendText(user.getChatId(), messages.get("welcome", user.getLanguage()),
                keyboard.mainMenu(user.getLanguage(), user.isAdmin(), cartCount));
    }

    private void editMainMenu(TelegramUser user, Long chatId, Integer messageId) {
        int cartCount = getCartCount(user);
        messenger.editText(chatId, messageId, messages.get("welcome", user.getLanguage()),
                keyboard.mainMenu(user.getLanguage(), user.isAdmin(), cartCount));
    }

    private int getCartCount(TelegramUser user) {
        try {
            CartDto cart = store.getCart(user);
            return cart.getItems() != null ? cart.getItems().size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void showProducts(TelegramUser user, TelegramSession session, int page, Long chatId, Integer messageId) {
        Page<Product> products = store.getProducts(page);
        if (products.isEmpty()) {
            messenger.editText(chatId, messageId, messages.get("empty.products", user.getLanguage()), keyboard.backHome(user.getLanguage()));
            return;
        }
        session.setPage(page);
        session.setState(BotState.PRODUCTS_LIST);
        sessionService.save(session);

        List<KeyboardBuilder.ItemButton> items = products.stream()
                .map(p -> new KeyboardBuilder.ItemButton(String.valueOf(p.getId()) + ":" + page,
                        productMapper.localizedName(p, user.getLanguage())))
                .toList();

        InlineKeyboardMarkup markup = buildProductListKeyboard(
                user, items, page, products.getTotalPages(), (int) products.getTotalElements());
        messenger.editText(chatId, messageId,
                messages.get("menu.products", user.getLanguage()) + " (" + (page + 1) + "/" + products.getTotalPages() + ")", markup);
    }

    private InlineKeyboardMarkup buildProductListKeyboard(TelegramUser user, List<KeyboardBuilder.ItemButton> items,
                                                           int page, int totalPages, int totalElements) {
        int cols = 2;

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (KeyboardBuilder.ItemButton item : items) {
            String[] parts = item.id().split(":");
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(item.label());
            button.setCallbackData(CallbackDataFactory.build(CallbackAction.PRODUCT, parts[0], parts[1]));
            currentRow.add(button);
            if (currentRow.size() == cols) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }
        if (!currentRow.isEmpty()) {
            rows.add(new ArrayList<>(currentRow));
        }

        rows.addAll(keyboard.pagination(user.getLanguage(), CallbackAction.PRODUCTS, page, totalPages).getKeyboard());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }

    private void showProductConfirm(TelegramUser user, int productId, int page, Long chatId, Integer messageId) {
        Product product = store.getProduct(productId)
                .orElseThrow(() -> new IllegalArgumentException("Mahsulot topilmadi: " + productId));

        // Reset draft qty when switching to a different product
        TelegramSession fresh = sessionService.getSession(user);
        if (!Integer.valueOf(productId).equals(fresh.getProductId())) {
            fresh.setDraftQuantity(1);
        }
        fresh.setProductId(productId);
        fresh.setPage(page);
        fresh.setState(BotState.PRODUCT_DETAIL);
        user.setCurrentState(BotState.PRODUCT_DETAIL);
        sessionService.save(fresh);

        int stock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        boolean available = product.isAvailable() && stock > 0;
        int qty = fresh.getDraftQuantity() != null ? fresh.getDraftQuantity() : 1;
        qty = Math.min(qty, Math.max(stock, 1));

        String name = escapeHtml(productMapper.localizedName(product, user.getLanguage()));
        double price = product.getDiscountPrice() > 0 ? product.getDiscountPrice() : product.getPrice();
        String desc = productMapper.localizedDescription(product, user.getLanguage());

        StringBuilder sb = new StringBuilder();
        sb.append("🍽 <b>").append(name);
        if (!available) sb.append(" ❌");
        sb.append("</b>\n");
        sb.append("💰 ").append(String.format("%,.0f so'm", price))
                .append(" | ⚖️ ").append(String.format("%.0f g", product.getWeight())).append("\n");
        sb.append("━━━━━━━━━━━━━━━━━━\n");
        if (!available) {
            sb.append("⚠️ Mahsulot mavjud emas");
        } else {
            sb.append("📦 Miqdor: ").append(qty).append(" ta");
        }
        if (desc != null && !desc.isBlank()) {
            sb.append("\n\n").append(escapeHtml(desc));
        }

        messenger.editText(chatId, messageId, sb.toString(),
                keyboard.productDetailWithQty(user.getLanguage(), productId, qty, stock, page, available));
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void showProduct(TelegramUser user, TelegramSession session, int productId, int page, Long chatId, Integer messageId) {
        Product product = store.getProduct(productId).orElseThrow();
        session.setProductId(productId);
        session.setPage(page);
        session.setState(BotState.PRODUCT_DETAIL);
        sessionService.save(session);

        List<Review> reviews = store.getProductReviews(product);
        String text = productMapper.toDetailText(product, user.getLanguage(), reviews);
        InlineKeyboardMarkup markup = keyboard.productActions(user.getLanguage(), productId);

        java.io.File image = store.getProductImageFile(product);
        if (image != null) {
            messenger.sendPhoto(chatId, image, text, markup);
        } else {
            messenger.editText(chatId, messageId, text, markup);
        }
    }

    private void showCategories(TelegramUser user, TelegramSession session, int page, Long chatId, Integer messageId) {
        Page<Category> categories = store.getCategories(page);
        session.setPage(page);
        session.setState(BotState.CATEGORIES_LIST);
        sessionService.save(session);

        List<KeyboardBuilder.ItemButton> items = categories.stream()
                .filter(Category::isStatus)
                .map(c -> new KeyboardBuilder.ItemButton(String.valueOf(c.getId()) + ":0",
                        productMapper.localizedCategoryName(c, user.getLanguage())))
                .toList();

        InlineKeyboardMarkup markup = keyboard.listButtons(user.getLanguage(), CallbackAction.CATEGORY, items, page, categories.getTotalPages());
        messenger.editText(chatId, messageId, messages.get("menu.categories", user.getLanguage()), markup);
    }

    private void showCategoryProducts(TelegramUser user, TelegramSession session, int categoryId, int page, Long chatId, Integer messageId) {
        session.setCategoryId(categoryId);
        session.setPage(page);
        session.setState(BotState.CATEGORY_PRODUCTS);
        sessionService.save(session);

        Page<Product> products = store.getProductsByCategory(categoryId, page);
        List<KeyboardBuilder.ItemButton> items = products.stream()
                .map(p -> new KeyboardBuilder.ItemButton(String.valueOf(p.getId()) + ":" + page,
                        productMapper.localizedName(p, user.getLanguage())))
                .toList();
        InlineKeyboardMarkup markup = buildProductListKeyboard(
                user, items, page, products.getTotalPages(), (int) products.getTotalElements());
        messenger.editText(chatId, messageId, messages.get("menu.products", user.getLanguage()), markup);
    }

    private void showCart(TelegramUser user, Long chatId, Integer messageId) {
        CartDto cart = store.getCart(user);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            messenger.editText(chatId, messageId, messages.get("empty.cart", user.getLanguage()), keyboard.cartMenu(user.getLanguage()));
            return;
        }
        StringBuilder sb = new StringBuilder("🛒 <b>" + messages.get("menu.cart", user.getLanguage()) + "</b>\n\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (CartItemDto item : cart.getItems()) {
            sb.append("• ").append(item.getProductName())
                    .append(" x").append(item.getQuantity())
                    .append(" = ").append(String.format("%,.0f", item.getLineTotal())).append(" so'm\n");
            String shortName = item.getProductName().length() > 12
                    ? item.getProductName().substring(0, 12) + "…"
                    : item.getProductName();
            rows.add(List.of(
                    inlineBtn("➖", CallbackDataFactory.build(CallbackAction.CART_DEC, String.valueOf(item.getId()))),
                    inlineBtn(shortName + " x" + item.getQuantity(), CallbackDataFactory.build(CallbackAction.NOOP)),
                    inlineBtn("➕", CallbackDataFactory.build(CallbackAction.CART_INC, String.valueOf(item.getId()))),
                    inlineBtn("🗑", CallbackDataFactory.build(CallbackAction.CART_REMOVE, String.valueOf(item.getId())))
            ));
        }
        sb.append("\n<b>").append(messages.get("total", user.getLanguage())).append(": ")
                .append(String.format("%,.0f", cart.getTotalPrice())).append(" so'm</b>");
        rows.add(List.of(inlineBtn(messages.get("btn.checkout", user.getLanguage()),
                CallbackDataFactory.build(CallbackAction.CART_CHECKOUT))));
        rows.add(List.of(
                inlineBtn(messages.get("btn.clear_cart", user.getLanguage()), CallbackDataFactory.build(CallbackAction.CART_CLEAR)),
                inlineBtn(messages.get("btn.back", user.getLanguage()), CallbackDataFactory.build(CallbackAction.BACK))
        ));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        messenger.editText(chatId, messageId, sb.toString(), markup);
    }

    private InlineKeyboardButton inlineBtn(String text, String callback) {
        InlineKeyboardButton btn = new InlineKeyboardButton();
        btn.setText(text);
        btn.setCallbackData(callback);
        return btn;
    }

    private void changeCartQty(TelegramUser user, int cartItemId, int delta, Long chatId, Integer messageId) {
        CartDto cart = store.getCart(user);
        CartItemDto item = cart.getItems().stream().filter(i -> i.getId().equals(cartItemId)).findFirst().orElseThrow();
        int newQty = item.getQuantity() + delta;
        if (newQty <= 0) store.removeCartItem(user, cartItemId);
        else store.updateCartItem(user, cartItemId, newQty);
        showCart(user, chatId, messageId);
    }

    private void removeCartItem(TelegramUser user, int cartItemId, Long chatId, Integer messageId) {
        store.removeCartItem(user, cartItemId);
        showCart(user, chatId, messageId);
    }

    private void showWishlist(TelegramUser user, Long chatId, Integer messageId) {
        List<Wishlist> list = store.getWishlist(user);
        if (list.isEmpty()) {
            messenger.editText(chatId, messageId, messages.get("empty.wishlist", user.getLanguage()), keyboard.backHome(user.getLanguage()));
            return;
        }
        StringBuilder sb = new StringBuilder("❤️ <b>" + messages.get("menu.wishlist", user.getLanguage()) + "</b>\n\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int num = 1;
        for (Wishlist w : list) {
            String name = productMapper.localizedName(w.getProduct(), user.getLanguage());
            sb.append(num++).append(". ").append(name).append("\n");
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("🗑 " + name);
            button.setCallbackData(CallbackDataFactory.build(CallbackAction.WISHLIST_REMOVE, String.valueOf(w.getId())));
            rows.add(List.of(button));
        }
        rows.add(keyboard.backHome(user.getLanguage()).getKeyboard().get(0));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        messenger.editText(chatId, messageId, sb.toString(), markup);
    }

    private void showOrders(TelegramUser user, Long chatId, Integer messageId) {
        List<OrderResponseDto> orders = store.getMyOrders(user);
        if (orders.isEmpty()) {
            messenger.editText(chatId, messageId, messages.get("empty.orders", user.getLanguage()), keyboard.backHome(user.getLanguage()));
            return;
        }
        List<KeyboardBuilder.ItemButton> items = orders.stream()
                .map(o -> new KeyboardBuilder.ItemButton(String.valueOf(o.getId()),
                        "#" + o.getId() + " — " + o.getOrderStatus() + " — " + String.format("%,.0f", o.getTotalPrice())))
                .toList();
        InlineKeyboardMarkup markup = keyboard.listButtons(user.getLanguage(), CallbackAction.ORDER, items, 0, 1);
        messenger.editText(chatId, messageId, messages.get("menu.orders", user.getLanguage()), markup);
    }

    private void showOrderDetail(TelegramUser user, int orderId, Long chatId, Integer messageId) {
        ApiResponse response = store.getOrder(user, orderId);
        OrderResponseDto order = (OrderResponseDto) response.getData();
        StringBuilder sb = new StringBuilder();
        sb.append("📦 <b>Buyurtma #").append(order.getId()).append("</b>\n");
        sb.append(messages.get("order.status", user.getLanguage())).append(": ").append(order.getOrderStatus()).append("\n");
        sb.append(messages.get("total", user.getLanguage())).append(": ").append(String.format("%,.0f", order.getTotalPrice())).append(" so'm\n");
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                sb.append("• ").append(item.getProductName()).append(" x").append(item.getQuantity()).append("\n");
            }
        }
        InlineKeyboardMarkup markup = keyboard.backHome(user.getLanguage());
        messenger.editText(chatId, messageId, sb.toString(), markup);
    }

    private void showAddresses(TelegramUser user, Long chatId, Integer messageId) {
        List<Address> addresses = store.getAddresses(user);
        StringBuilder sb = new StringBuilder("📍 <b>" + messages.get("menu.addresses", user.getLanguage()) + "</b>\n\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Address a : addresses) {
            sb.append("• ").append(a.getTitle()).append("\n");
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("❌ " + a.getTitle());
            button.setCallbackData(CallbackDataFactory.build(CallbackAction.ADDRESS_DEL, String.valueOf(a.getId())));
            rows.add(List.of(button));
        }
        rows.add(keyboard.backHome(user.getLanguage()).getKeyboard().get(0));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        messenger.editText(chatId, messageId, sb.toString(), markup);
    }

    private void showCoupons(TelegramUser user, Long chatId, Integer messageId) {
        List<Coupon> coupons = store.getCoupons();
        StringBuilder sb = new StringBuilder("🎁 <b>" + messages.get("menu.coupons", user.getLanguage()) + "</b>\n\n");
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Coupon c : coupons) {
            if (!c.isActive()) continue;
            sb.append("• <code>").append(c.getCode()).append("</code> — ").append(c.getDiscountPercent()).append("%\n");
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText("✅ " + c.getCode());
            button.setCallbackData(CallbackDataFactory.build(CallbackAction.COUPON_APPLY, c.getCode()));
            rows.add(List.of(button));
        }
        rows.add(keyboard.backHome(user.getLanguage()).getKeyboard().get(0));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        messenger.editText(chatId, messageId, sb.toString(), markup);
    }

    private void showSettings(TelegramUser user, Long chatId, Integer messageId) {
        String text = "⚙️ <b>" + messages.get("menu.settings", user.getLanguage()) + "</b>\n\n"
                + "🌐 Til: " + user.getLanguage().name() + "\n"
                + "📱 Telefon: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "—");
        messenger.editText(chatId, messageId, text, keyboard.settingsMenu(user.getLanguage()));
    }

    private void sendHelpMessage(TelegramUser user, Long chatId) {
        String text = "ℹ️ <b>FoodStore — Yordam</b>\n\n"
                + "/start — Botni qayta boshlash\n"
                + "/menu — Asosiy menyuga qaytish\n"
                + "/orders — Buyurtmalarimni ko'rish\n"
                + "/cart — Savatchamni ko'rish\n"
                + "/cancel — Amalni bekor qilish\n"
                + "/help — Ushbu yordam xabarini ko'rsatish";
        messenger.sendText(chatId, text, keyboard.homeOnly(user.getLanguage()));
    }

    private void sendOrdersList(TelegramUser user, Long chatId) {
        List<OrderResponseDto> orders = store.getMyOrders(user);
        if (orders.isEmpty()) {
            messenger.sendText(chatId, messages.get("empty.orders", user.getLanguage()), keyboard.homeOnly(user.getLanguage()));
            return;
        }
        StringBuilder sb = new StringBuilder("📦 <b>" + messages.get("menu.orders", user.getLanguage()) + "</b>\n\n");
        for (OrderResponseDto o : orders) {
            sb.append("#").append(o.getId()).append(" — ").append(o.getOrderStatus())
                    .append(" — ").append(String.format("%,.0f so'm", o.getTotalPrice())).append("\n");
        }
        messenger.sendText(chatId, sb.toString(), keyboard.homeOnly(user.getLanguage()));
    }

    private void sendCartInfo(TelegramUser user, Long chatId) {
        CartDto cart = store.getCart(user);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            messenger.sendText(chatId, messages.get("empty.cart", user.getLanguage()), keyboard.homeOnly(user.getLanguage()));
            return;
        }
        StringBuilder sb = new StringBuilder("🛒 <b>" + messages.get("menu.cart", user.getLanguage()) + "</b>\n\n");
        for (CartItemDto item : cart.getItems()) {
            sb.append("• ").append(item.getProductName()).append(" x").append(item.getQuantity())
                    .append(" = ").append(String.format("%,.0f", item.getLineTotal())).append(" so'm\n");
        }
        sb.append("\n<b>").append(messages.get("total", user.getLanguage())).append(": ")
                .append(String.format("%,.0f", cart.getTotalPrice())).append(" so'm</b>");
        messenger.sendText(chatId, sb.toString(), keyboard.cartMenu(user.getLanguage()));
    }

    private void requestPhoneNumber(Long chatId, BotLanguage lang) {
        KeyboardButton phoneBtn = KeyboardButton.builder()
                .text(messages.get("btn.share_phone", lang))
                .requestContact(true)
                .build();
        ReplyKeyboardMarkup replyKb = ReplyKeyboardMarkup.builder()
                .keyboard(List.of(new KeyboardRow(List.of(phoneBtn))))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
        messenger.sendReplyKeyboard(chatId, messages.get("settings.phone_request", lang), replyKb);
    }

    private void showContact(TelegramUser user, Long chatId, Integer messageId) {
        String text = "☎️ <b>" + messages.get("menu.contact", user.getLanguage()) + "</b>\n\n"
                + "📞 +998903630177\n"
                + "📸 Instagram: <a href=\"https://www.instagram.com/murodilovich_16s\">@murodilovich_16s</a>\n"
                + "🌐 Telegram: <a href=\"https://t.me/murodilovich_16s\">@murodilovich_16s</a>\n"
                + "💬 Qo'llab-quvvatlash: <a href=\"https://t.me/murodilovich_16s\">@murodilovich_16s</a>";
        messenger.editText(chatId, messageId, text, keyboard.homeOnly(user.getLanguage()));
    }

    private void startCheckout(TelegramUser user, TelegramSession session, Long chatId) {
        CartDto cart = store.getCart(user);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            messenger.sendText(chatId, messages.get("empty.cart", user.getLanguage()));
            return;
        }
        sessionService.resetCheckoutAndSetState(user, BotState.CHECKOUT_PHONE);
        ReplyKeyboardMarkup replyKb = ReplyKeyboardMarkup.builder()
                .keyboard(List.of(new KeyboardRow(List.of(
                        KeyboardButton.builder().text("📱 Telefon yuborish").requestContact(true).build()
                ))))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
        messenger.sendReplyKeyboard(chatId, messages.get("checkout.phone", user.getLanguage()), replyKb);
    }

    private void askDeliveryType(TelegramUser user) {
        sessionService.updateState(user, BotState.CHECKOUT_DELIVERY);
        messenger.removeReplyKeyboard(user.getChatId(), "✅ Telefon qabul qilindi!");
        messenger.sendText(user.getChatId(), messages.get("checkout.delivery", user.getLanguage()),
                keyboard.deliveryType(user.getLanguage()));
    }

    private void askLocation(TelegramUser user) {
        sessionService.updateState(user, BotState.CHECKOUT_LOCATION);
        ReplyKeyboardMarkup replyKb = ReplyKeyboardMarkup.builder()
                .keyboard(List.of(new KeyboardRow(List.of(
                        KeyboardButton.builder().text("📍 Lokatsiya yuborish").requestLocation(true).build()
                ))))
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .build();
        messenger.sendReplyKeyboard(user.getChatId(), messages.get("checkout.location", user.getLanguage()), replyKb);
    }

    private void saveAddressAndContinueCheckout(TelegramUser user, TelegramSession session) {
        AddressDto dto = new AddressDto();
        dto.setTitle(session.getAddressTitle() != null ? session.getAddressTitle() : "📍 Manzil");
        dto.setLatitude(session.getLatitude());
        dto.setLongitude(session.getLongitude());
        store.addAddress(user, dto);

        List<Address> addresses = store.getAddresses(user);
        if (addresses.isEmpty()) {
            messenger.removeReplyKeyboard(user.getChatId(), messages.get("error.generic", user.getLanguage()));
            return;
        }
        Address last = addresses.get(addresses.size() - 1);
        sessionService.setAddressIdAndState(user, last.getId(), BotState.CHECKOUT_COUPON);

        messenger.removeReplyKeyboard(user.getChatId(), "✅ Manzil qabul qilindi!");
        messenger.sendText(user.getChatId(), messages.get("checkout.coupon_ask", user.getLanguage()),
                keyboard.couponStep(user.getLanguage()));
    }

    private void handleDeliveryType(TelegramUser user, TelegramSession session, String type, Long chatId, Integer messageId) {
        DeliverType deliverType = DeliverType.valueOf(type);
        if (deliverType == DeliverType.DELEVER) {
            sessionService.setDeliverTypeAndState(user, deliverType, BotState.CHECKOUT_LOCATION);
            askLocation(user);
        } else {
            sessionService.setDeliverTypeAndState(user, deliverType, BotState.CHECKOUT_FILIAL);
            List<Filial> filials = store.getFilials();
            if (filials.isEmpty()) {
                sessionService.updateState(user, BotState.CHECKOUT_COUPON);
                messenger.editText(chatId, messageId, messages.get("checkout.coupon_ask", user.getLanguage()),
                        keyboard.couponStep(user.getLanguage()));
            } else {
                messenger.editText(chatId, messageId,
                        messages.get("checkout.filial_select", user.getLanguage()),
                        keyboard.filialList(user.getLanguage(), filials));
            }
        }
    }

    private void handlePaymentType(TelegramUser user, TelegramSession session, String type, Long chatId, Integer messageId) {
        PaymentType paymentType = PaymentType.valueOf(type);
        if (paymentType == PaymentType.CARD) {
            sessionService.setPaymentTypeAndState(user, paymentType, BotState.CHECKOUT_PAYMENT_CHECK);
            messenger.sendText(user.getChatId(),
                    messages.get("checkout.payment_check", user.getLanguage()),
                    keyboard.backHome(user.getLanguage()));
        } else {
            TelegramSession fresh = sessionService.setPaymentTypeAndState(user, paymentType, BotState.CHECKOUT_CONFIRM);
            showCheckoutSummary(user, fresh);
        }
    }

    private void showCheckoutSummary(TelegramUser user, TelegramSession session) {
        CartDto cart = store.getCart(user);
        StringBuilder sb = new StringBuilder("<b>Buyurtma xulosasi</b>\n\n");
        sb.append("📱 ").append(session.getPhoneNumber() != null ? session.getPhoneNumber() : "—").append("\n");
        String deliverLabel = session.getDeliverType() == DeliverType.PICKUP ? "Olib ketish 🏪" : "Yetkazib berish 🚚";
        sb.append("🚚 ").append(deliverLabel).append("\n");
        if (session.getDeliverType() == DeliverType.PICKUP && session.getFilialId() != null) {
            store.getFilials().stream()
                    .filter(f -> f.getId().equals(session.getFilialId()))
                    .findFirst()
                    .ifPresent(f -> sb.append("🏪 Filial: ").append(f.getTitle()).append("\n"));
        }
        String paymentLabel = session.getPaymentType() == PaymentType.CARD ? "Karta 💳" : "Naqd 💵";
        sb.append("💳 To'lov: ").append(paymentLabel).append("\n");
        sb.append(messages.get("total", user.getLanguage())).append(": ")
                .append(String.format("%,.0f", cart.getTotalPrice())).append(" so'm\n\n");
        sb.append(messages.get("checkout.confirm", user.getLanguage()));
        messenger.sendText(user.getChatId(), sb.toString(), keyboard.checkoutConfirm(user.getLanguage()));
    }

    private void confirmOrder(TelegramUser user, TelegramSession session, Long chatId) {
        TelegramSession fresh = sessionService.getSession(user);
        OrderDto dto = store.buildOrderFromCart(user, fresh.getPhoneNumber(), fresh.getDeliverType(),
                fresh.getPaymentType(), fresh.getAddressId(), fresh.getFilialId(),
                fresh.getCouponCode(), fresh.getDraftMessage());
        ApiResponse response = store.createOrder(user, dto);
        if (!response.isStatus()) {
            messenger.sendText(chatId, response.getMessage());
            return;
        }
        OrderResponseDto order = (OrderResponseDto) response.getData();
        notifications.trackOrder(order.getId(), chatId, order.getOrderStatus());
        notifications.notifyAdmins(properties.getAdminChatIdList(),
                messages.get("notification.new_order", BotLanguage.UZ) + order.getId());

        sendOrderReceipt(user, chatId, order, fresh);
        sessionService.resetToMain(user);
        showMainMenu(user);
    }

    private void sendOrderReceipt(TelegramUser user, Long chatId, OrderResponseDto order, TelegramSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("━━━━━━━━━━━━━━━━━━\n");
        sb.append("🧾 <b>BUYURTMA CHEKI</b>\n");
        sb.append("━━━━━━━━━━━━━━━━━━\n");
        sb.append("✅ Buyurtma <b>#").append(order.getId()).append("</b> qabul qilindi!\n\n");
        sb.append("📋 <b>Mahsulotlar:</b>\n");
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                double lineTotal = item.getPrice() * item.getQuantity();
                sb.append("• ").append(item.getProductName())
                        .append(" x").append((int) item.getQuantity())
                        .append(" — ").append(String.format("%,.0f so'm", lineTotal)).append("\n");
            }
        }
        sb.append("\n💰 <b>Jami: ").append(String.format("%,.0f so'm", order.getTotalPrice())).append("</b>\n");
        String deliverLabel = order.getDeliverType() == DeliverType.PICKUP ? "Olib ketish 🏪" : "Yetkazib berish 🚚";
        sb.append("🚚 Yetkazish: ").append(deliverLabel).append("\n");
        String paymentLabel = order.getPaymentType() == PaymentType.CARD ? "Karta 💳" : "Naqd 💵";
        sb.append("💳 To'lov: ").append(paymentLabel).append("\n");
        if (order.getDeliverType() == DeliverType.PICKUP && session.getFilialId() != null) {
            store.getFilials().stream()
                    .filter(f -> f.getId().equals(session.getFilialId()))
                    .findFirst()
                    .ifPresent(f -> sb.append("🏪 Filial: ").append(f.getTitle()).append("\n"));
        }
        sb.append("\n⏱ Taxminiy vaqt: 30-45 daqiqa\n");
        sb.append("━━━━━━━━━━━━━━━━━━\n");
        sb.append("Rahmat! 🙏");
        messenger.sendText(chatId, sb.toString(), keyboard.homeOnly(user.getLanguage()));
    }

    private void processCardPaymentCheck(TelegramUser user, TelegramSession session, Long chatId, String photoFileId) {
        TelegramSession fresh = sessionService.getSession(user);
        OrderDto dto = store.buildOrderFromCart(user, fresh.getPhoneNumber(), fresh.getDeliverType(),
                fresh.getPaymentType(), fresh.getAddressId(), fresh.getFilialId(),
                fresh.getCouponCode(), fresh.getDraftMessage());
        ApiResponse response = store.createOrder(user, dto);
        if (!response.isStatus()) {
            messenger.sendText(chatId, response.getMessage() != null ? response.getMessage() : messages.get("error.generic", user.getLanguage()));
            return;
        }
        OrderResponseDto order = (OrderResponseDto) response.getData();
        notifications.trackOrder(order.getId(), chatId, order.getOrderStatus());

        messenger.sendText(chatId,
                messages.get("checkout.payment_check_sent", user.getLanguage()) + " #" + order.getId());

        String adminCaption = "💳 <b>Yangi karta to'lovi!</b>\n"
                + "📦 Buyurtma #" + order.getId() + "\n"
                + "📱 " + (fresh.getPhoneNumber() != null ? fresh.getPhoneNumber() : "—") + "\n"
                + "💰 Jami: " + String.format("%,.0f so'm", order.getTotalPrice());
        notifications.notifyAdminsWithPhoto(properties.getAdminChatIdList(), photoFileId,
                adminCaption, keyboard.paymentApproval(order.getId()));

        sessionService.resetToMain(user);
        showMainMenu(user);
    }

    private void showAdminMenu(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        messenger.editText(chatId, messageId, "👑 Admin Panel", keyboard.adminMenu(user.getLanguage()));
        sessionService.updateState(user, BotState.ADMIN_MENU);
    }

    private void showAdminOrders(TelegramUser user, int page, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        Page<Order> orders = store.getAllOrders(page);
        List<KeyboardBuilder.ItemButton> items = orders.stream()
                .map(o -> new KeyboardBuilder.ItemButton(String.valueOf(o.getId()),
                        "#" + o.getId() + " " + o.getOrderStatus()))
                .toList();
        InlineKeyboardMarkup markup = keyboard.listButtons(user.getLanguage(), CallbackAction.ADMIN_ORDER, items, page, orders.getTotalPages());
        messenger.editText(chatId, messageId, "📋 Buyurtmalar", markup);
    }

    private void showAdminOrderDetail(TelegramUser user, int orderId, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        Order order = store.getOrderById(orderId).orElseThrow();
        String text = "📦 #" + order.getId() + "\nHolat: " + order.getOrderStatus() + "\nSumma: " + order.getTotalPrice();
        messenger.editText(chatId, messageId, text, keyboard.adminOrderActions(user.getLanguage(), orderId));
        sessionService.updateState(user, BotState.ADMIN_ORDER_DETAIL);
    }

    private void adminChangeStatus(TelegramUser user, int orderId, OrderStatus status, Long chatId) {
        if (!user.isAdmin()) return;
        ApiResponse response = store.changeOrderStatus(orderId, status);
        if (response.isStatus()) {
            OrderResponseDto order = (OrderResponseDto) response.getData();
            notifications.findAndNotify(orderId, status);
            messenger.sendText(chatId, "✅ Status: " + status.name());
        }
    }

    private void showAdminStats(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        long total = store.getAllOrders(0).getTotalElements();
        long confirmed = store.countOrdersByStatus(OrderStatus.CONFIRMED);
        long delivered = store.countOrdersByStatus(OrderStatus.DELIVERED);
        String text = "📊 <b>Statistika</b>\n\nJami: " + total + "\nTasdiqlangan: " + confirmed + "\nYetkazilgan: " + delivered;
        messenger.editText(chatId, messageId, text, keyboard.adminMenu(user.getLanguage()));
    }

    // 👥 Bot foydalanuvchilari: soni + oxirgi faollar
    private void showAdminUsers(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        long total = userService.countUsers();
        List<TelegramUser> recent = userService.recentUsers(10);

        StringBuilder sb = new StringBuilder();
        sb.append("👥 <b>Bot foydalanuvchilari</b>\n\n");
        sb.append("Jami: <b>").append(total).append("</b> ta\n\n");
        sb.append("🕒 <b>Oxirgi faol foydalanuvchilar:</b>\n");
        int i = 1;
        for (TelegramUser u : recent) {
            String name = u.getFirstName() != null ? u.getFirstName() : "—";
            String uname = u.getUsername() != null ? " (@" + u.getUsername() + ")" : "";
            sb.append(i++).append(". ").append(escapeHtml(name)).append(escapeHtml(uname)).append("\n");
        }
        messenger.editText(chatId, messageId, sb.toString(), keyboard.adminBack(user.getLanguage()));
    }

    // 📢 Broadcast: admin'dan xabar matnini so'rash
    private void startBroadcast(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        sessionService.updateState(user, BotState.ADMIN_BROADCAST_INPUT);
        messenger.editText(chatId, messageId,
                "📢 <b>Broadcast xabar</b>\n\nBarcha foydalanuvchilarga yubormoqchi bo'lgan xabaringizni yozib yuboring.\n\nBekor qilish uchun: /cancel",
                keyboard.adminBack(user.getLanguage()));
    }

    // 🍽 Mahsulotlar: aktiv/noaktiv qilish
    private void showAdminProducts(TelegramUser user, int page, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        Page<Product> products = store.getAllProductsAdmin(page);
        if (products.isEmpty()) {
            messenger.editText(chatId, messageId, "📦 Mahsulotlar yo'q", keyboard.adminBack(user.getLanguage()));
            return;
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(inlineBtn("➕ Mahsulot qo'shish", CallbackDataFactory.build(CallbackAction.ADMIN_PROD_ADD))));
        for (Product p : products) {
            String status = p.isAvailable() ? "✅" : "❌";
            double price = p.getDiscountPrice() > 0 ? p.getDiscountPrice() : p.getPrice();
            String label = status + " " + productMapper.localizedName(p, user.getLanguage())
                    + " — " + String.format("%,.0f", price);
            rows.add(List.of(inlineBtn(label,
                    CallbackDataFactory.build(CallbackAction.ADMIN_PRODUCT_TOGGLE, String.valueOf(p.getId()), String.valueOf(page)))));
        }
        rows.addAll(keyboard.pagination(user.getLanguage(), CallbackAction.ADMIN_PRODUCTS, page, products.getTotalPages()).getKeyboard());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        messenger.editText(chatId, messageId,
                "🍽 <b>Mahsulotlar</b>\nTugmani bosib aktiv/noaktiv qiling.\n✅ = aktiv | ❌ = noaktiv", markup);
    }

    private void toggleProduct(TelegramUser user, int productId, int page, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        store.toggleProductAvailable(productId);
        showAdminProducts(user, page, chatId, messageId);
    }

    // 🎁 Kuponlar: aktiv/noaktiv qilish
    private void showAdminCoupons(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        List<Coupon> coupons = store.getCoupons();
        if (coupons == null || coupons.isEmpty()) {
            messenger.editText(chatId, messageId, "🎁 Kuponlar yo'q", keyboard.adminBack(user.getLanguage()));
            return;
        }
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (Coupon c : coupons) {
            String status = c.isActive() ? "✅" : "❌";
            String label = status + " " + c.getCode() + " — " + (int) c.getDiscountPercent() + "%";
            rows.add(List.of(inlineBtn(label,
                    CallbackDataFactory.build(CallbackAction.ADMIN_COUPON_TOGGLE, String.valueOf(c.getId())))));
        }
        rows.addAll(keyboard.adminBack(user.getLanguage()).getKeyboard());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        messenger.editText(chatId, messageId,
                "🎁 <b>Kuponlar</b>\nTugmani bosib aktiv/noaktiv qiling.\n✅ = aktiv | ❌ = noaktiv", markup);
    }

    private void toggleCoupon(TelegramUser user, int couponId, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        store.toggleCoupon(couponId);
        showAdminCoupons(user, chatId, messageId);
    }

    // 📂 Kategoriyalar ro'yxati + qo'shish
    private void showAdminCategories(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        List<Category> cats = store.getAllCategoriesAdmin();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(List.of(inlineBtn("➕ Kategoriya qo'shish", CallbackDataFactory.build(CallbackAction.ADMIN_CAT_ADD))));
        for (Category c : cats) {
            rows.add(List.of(inlineBtn("📂 " + c.getNameUz(), CallbackDataFactory.build(CallbackAction.NOOP))));
        }
        rows.addAll(keyboard.adminBack(user.getLanguage()).getKeyboard());
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        messenger.editText(chatId, messageId, "📂 <b>Kategoriyalar</b> (" + cats.size() + " ta)", markup);
    }

    // 📂 Kategoriya qo'shish: nom so'rash
    private void startAddCategory(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        sessionService.updateState(user, BotState.ADMIN_CAT_ADD_NAME);
        messenger.editText(chatId, messageId,
                "📂 <b>Yangi kategoriya</b>\n\nKategoriya nomini yuboring:\n\nBekor qilish: /cancel",
                keyboard.adminBack(user.getLanguage()));
    }

    private void finishAddCategory(TelegramUser user, Long chatId, String name) {
        if (!user.isAdmin()) return;
        if (name == null || name.isBlank()) {
            messenger.sendText(chatId, "❌ Nom bo'sh. Qaytadan yuboring:");
            return;
        }
        Category created = store.createCategory(name.trim());
        sessionService.resetToMain(user);
        messenger.sendText(chatId,
                "✅ Kategoriya qo'shildi: <b>" + escapeHtml(created.getNameUz()) + "</b>",
                keyboard.adminBack(user.getLanguage()));
    }

    // 🍽 Mahsulot qo'shish: 1) nom
    private void startAddProduct(TelegramUser user, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        TelegramSession s = sessionService.getSession(user);
        s.setDraftMessage(null);
        s.setCouponCode(null);
        s.setCategoryId(null);
        s.setState(BotState.ADMIN_PROD_ADD_NAME);
        user.setCurrentState(BotState.ADMIN_PROD_ADD_NAME);
        sessionService.save(s);
        messenger.editText(chatId, messageId,
                "🍽 <b>Yangi mahsulot</b>\n\n1️⃣ Mahsulot nomini yuboring:\n\nBekor qilish: /cancel",
                keyboard.adminBack(user.getLanguage()));
    }

    // 🍽 Mahsulot qo'shish: 2) narx
    private void handleProductPriceInput(TelegramUser user, Long chatId, String text) {
        if (!user.isAdmin()) return;
        double price;
        try {
            price = Double.parseDouble(text.trim().replace(" ", "").replace(",", "."));
        } catch (NumberFormatException e) {
            messenger.sendText(chatId, "❌ Noto'g'ri narx. Faqat raqam yuboring (masalan: 45000):");
            return;
        }
        TelegramSession s = sessionService.getSession(user);
        s.setCouponCode(String.valueOf(price));
        s.setState(BotState.ADMIN_PROD_ADD_CATEGORY);
        user.setCurrentState(BotState.ADMIN_PROD_ADD_CATEGORY);
        sessionService.save(s);

        List<Category> cats = store.getAllCategoriesAdmin();
        if (cats.isEmpty()) {
            sessionService.resetToMain(user);
            messenger.sendText(chatId, "⚠️ Avval kategoriya qo'shing — kategoriyasiz mahsulot bo'lmaydi.",
                    keyboard.adminBack(user.getLanguage()));
            return;
        }
        messenger.sendText(chatId, "3️⃣ Kategoriyani tanlang:", keyboard.adminCategoryPick(user.getLanguage(), cats));
    }

    // 🍽 Mahsulot qo'shish: 3) kategoriya tanlandi -> saqlash
    private void finishAddProduct(TelegramUser user, int categoryId, Long chatId, Integer messageId) {
        if (!user.isAdmin()) return;
        TelegramSession s = sessionService.getSession(user);
        String name = s.getDraftMessage();
        double price = 0;
        try {
            price = Double.parseDouble(s.getCouponCode());
        } catch (Exception ignored) {
        }
        if (name == null || name.isBlank()) {
            sessionService.resetToMain(user);
            messenger.editText(chatId, messageId, "❌ Xatolik. Qaytadan urinib ko'ring.", keyboard.adminBack(user.getLanguage()));
            return;
        }
        Product created = store.createProduct(name.trim(), price, categoryId);
        sessionService.resetToMain(user);
        messenger.editText(chatId, messageId,
                "✅ <b>Mahsulot qo'shildi!</b>\n\n🍽 " + escapeHtml(created.getNameUz())
                        + "\n💰 " + String.format("%,.0f so'm", price),
                keyboard.adminBack(user.getLanguage()));
    }

    // 📢 Broadcast: xabarni barcha foydalanuvchilarga yuborish
    private void doBroadcast(TelegramUser user, Long chatId, String text) {
        if (!user.isAdmin()) return;
        List<TelegramUser> all = userService.findAll();
        String body = "📢 <b>E'lon</b>\n\n" + escapeHtml(text);
        int sent = 0;
        int failed = 0;
        for (TelegramUser target : all) {
            try {
                messenger.sendText(target.getChatId(), body);
                sent++;
            } catch (Exception e) {
                failed++; // foydalanuvchi botni bloklagan bo'lishi mumkin
            }
        }
        sessionService.resetToMain(user);
        messenger.sendText(chatId,
                "✅ <b>Broadcast yakunlandi!</b>\n\n📨 Yuborildi: <b>" + sent + "</b>\n❌ Yuborilmadi: <b>" + failed + "</b>",
                keyboard.adminBack(user.getLanguage()));
    }

    private Long resolveChatId(Update update) {
        if (update.hasMessage()) return update.getMessage().getChatId();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getMessage().getChatId();
        return null;
    }
}
