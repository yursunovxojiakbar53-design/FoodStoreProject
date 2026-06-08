package org.example.project.telegram.mapper;

import org.example.project.entity.Product;
import org.example.project.entity.Review;
import org.example.project.telegram.enums.BotLanguage;
import org.example.project.telegram.i18n.MessageService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMessageMapper {

    private final MessageService messages;

    public ProductMessageMapper(MessageService messages) {
        this.messages = messages;
    }

    public String toDetailText(Product product, BotLanguage lang, List<Review> reviews) {
        String name = localizedName(product, lang);
        double price = product.getPrice();
        double effective = product.getDiscountPrice() > 0 ? product.getDiscountPrice() : price;
        double avgRating = reviews.isEmpty() ? 0 :
                reviews.stream().mapToInt(Review::getStar).average().orElse(0);

        StringBuilder sb = new StringBuilder();
        sb.append("<b>").append(escape(name)).append("</b>\n\n");
        sb.append(messages.get("price", lang)).append(": ");
        if (product.getDiscountPrice() > 0 && product.getDiscountPrice() < price) {
            sb.append("<s>").append(formatPrice(price)).append("</s> ");
            sb.append("<b>").append(formatPrice(effective)).append("</b> ");
            sb.append("🔥 ").append(messages.get("discount", lang)).append("\n");
        } else {
            sb.append(formatPrice(effective)).append("\n");
        }
        sb.append(messages.get("rating", lang)).append(": ⭐ ").append(String.format("%.1f", avgRating))
                .append(" (").append(reviews.size()).append(")\n\n");
        String desc = localizedDescription(product, lang);
        if (desc != null && !desc.isBlank()) {
            sb.append(messages.get("description", lang)).append(":\n").append(escape(desc));
        }
        return sb.toString();
    }

    public String localizedName(Product product, BotLanguage lang) {
        return switch (lang) {
            case RU -> nonBlank(product.getNameRu(), product.getNameUz());
            case EN -> nonBlank(product.getNameEng(), product.getNameUz());
            default -> product.getNameUz();
        };
    }

    public String localizedDescription(Product product, BotLanguage lang) {
        return switch (lang) {
            case RU -> nonBlank(product.getDescriptionRu(), product.getDescriptionUz());
            case EN -> nonBlank(product.getDescriptionEng(), product.getDescriptionUz());
            default -> product.getDescriptionUz();
        };
    }

    public String localizedCategoryName(org.example.project.entity.Category category, BotLanguage lang) {
        return switch (lang) {
            case RU -> nonBlank(category.getNameRu(), category.getNameUz());
            case EN -> nonBlank(category.getNameEng(), category.getNameUz());
            default -> category.getNameUz();
        };
    }

    private String nonBlank(String primary, String fallback) {
        return primary != null && !primary.isBlank() ? primary : fallback;
    }

    private String formatPrice(double price) {
        return String.format("%,.0f so'm", price);
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
