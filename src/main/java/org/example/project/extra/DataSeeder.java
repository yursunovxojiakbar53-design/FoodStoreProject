package org.example.project.extra;

import lombok.RequiredArgsConstructor;
import org.example.project.entity.Attachment;
import org.example.project.entity.Category;
import org.example.project.entity.Product;
import org.example.project.repository.AttachmentRepository;
import org.example.project.repository.CategoryRepo;
import org.example.project.repository.ProductRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)  // DataInitializer dan keyin ishlaydi
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;
    private final AttachmentRepository attachmentRepository;

    @Override
    public void run(String... args) {

        // Allaqachon ma'lumot bor bo'lsa — qayta qo'shmasin
        if (categoryRepo.count() > 0) {
            log.info("Data already seeded, skipping...");
            return;
        }

        // Barcha mahsulotlar uchun 1 ta attachment (id=1)
        Attachment attachment = attachmentRepository.findById(1)
                .orElse(null);

        // ── Kategoriyalar ──────────────────────────────────────────────────────

        Category pizza = categoryRepo.save(Category.builder()
                .nameUz("Pitsa")
                .nameRu("Пицца")
                .nameEng("Pizza")
                .orderId(1)
                .telegramSticker("🍕")
                .status(true)
                .attachment(attachment)
                .build());

        Category burger = categoryRepo.save(Category.builder()
                .nameUz("Burger")
                .nameRu("Бургер")
                .nameEng("Burger")
                .orderId(2)
                .telegramSticker("🍔")
                .status(true)
                .attachment(attachment)
                .build());

        Category sushi = categoryRepo.save(Category.builder()
                .nameUz("Sushi")
                .nameRu("Суши")
                .nameEng("Sushi")
                .orderId(3)
                .telegramSticker("🍣")
                .status(true)
                .attachment(attachment)
                .build());

        Category drinks = categoryRepo.save(Category.builder()
                .nameUz("Ichimliklar")
                .nameRu("Напитки")
                .nameEng("Drinks")
                .orderId(4)
                .telegramSticker("🥤")
                .status(true)
                .attachment(attachment)
                .build());

        log.info("Categories seeded: {}", categoryRepo.count());

        // ── Mahsulotlar ────────────────────────────────────────────────────────

        // Pizza
        productRepo.save(Product.builder()
                .nameUz("Margarita")
                .price(45000)
                .discountPrice(39000)
                .currentPrice(39000)
                .weight(500)
                .descriptionUz("Klassik italyan pitsasi")
                .descriptionRu("Классический итальянский пицца")
                .descriptionEng("Classic Italian pizza")
                .isAvailable(true)
                .category(pizza)
                .attachment(attachment)
                .orderCount(0)
                .build());

        productRepo.save(Product.builder()
                .nameUz("Pepperoni")
                .price(55000)
                .discountPrice(0)
                .currentPrice(55000)
                .weight(550)
                .descriptionUz("Pepperoni va mozzarella bilan")
                .isAvailable(true)
                .category(pizza)
                .attachment(attachment)
                .orderCount(0)
                .build());

        productRepo.save(Product.builder()
                .nameUz("Hawai")
                .price(50000)
                .discountPrice(45000)
                .currentPrice(45000)
                .weight(520)
                .descriptionUz("Ananas va tovuq go'shti bilan")
                .isAvailable(true)
                .category(pizza)
                .attachment(attachment)
                .orderCount(0)
                .build());

        // Burger
        productRepo.save(Product.builder()
                .nameUz("Classic Burger")
                .price(35000)
                .discountPrice(0)
                .currentPrice(35000)
                .weight(300)
                .descriptionUz("Mol go'shti kotleti bilan")
                .isAvailable(true)
                .category(burger)
                .attachment(attachment)
                .orderCount(0)
                .build());

        productRepo.save(Product.builder()
                .nameUz("Double Burger")
                .price(50000)
                .discountPrice(45000)
                .currentPrice(45000)
                .weight(400)
                .descriptionUz("Ikki qavat kotlet bilan")
                .isAvailable(true)
                .category(burger)
                .attachment(attachment)
                .orderCount(0)
                .build());

        productRepo.save(Product.builder()
                .nameUz("Chicken Burger")
                .price(38000)
                .discountPrice(0)
                .currentPrice(38000)
                .weight(320)
                .descriptionUz("Tovuq go'shti bilan")
                .isAvailable(true)
                .category(burger)
                .attachment(attachment)
                .orderCount(0)
                .build());

        // Sushi
        productRepo.save(Product.builder()
                .nameUz("Philadelphia")
                .price(65000)
                .discountPrice(59000)
                .currentPrice(59000)
                .weight(280)
                .descriptionUz("Qizil baliq va krem pishloq bilan")
                .isAvailable(true)
                .category(sushi)
                .attachment(attachment)
                .orderCount(0)
                .build());

        productRepo.save(Product.builder()
                .nameUz("California")
                .price(60000)
                .discountPrice(0)
                .currentPrice(60000)
                .weight(260)
                .descriptionUz("Krab go'shti va avokado bilan")
                .isAvailable(true)
                .category(sushi)
                .attachment(attachment)
                .orderCount(0)
                .build());

        // Ichimliklar
        productRepo.save(Product.builder()
                .nameUz("Coca Cola")
                .price(12000)
                .discountPrice(0)
                .currentPrice(12000)
                .weight(500)
                .descriptionUz("Sovuq ichimlik 0.5L")
                .isAvailable(true)
                .category(drinks)
                .attachment(attachment)
                .orderCount(0)
                .build());

        productRepo.save(Product.builder()
                .nameUz("Fresh Limonad")
                .price(18000)
                .discountPrice(15000)
                .currentPrice(15000)
                .weight(400)
                .descriptionUz("Tabiiy limon sharbati")
                .isAvailable(true)
                .category(drinks)
                .attachment(attachment)
                .orderCount(0)
                .build());

        log.info("Products seeded: {}", productRepo.count());
    }
}