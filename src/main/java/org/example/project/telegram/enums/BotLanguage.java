package org.example.project.telegram.enums;

public enum BotLanguage {
    UZ,
    RU,
    EN;

    public static BotLanguage fromCode(String code) {
        if (code == null) return UZ;
        return switch (code.toUpperCase()) {
            case "RU", "RUS" -> RU;
            case "EN", "ENG" -> EN;
            default -> UZ;
        };
    }
}
