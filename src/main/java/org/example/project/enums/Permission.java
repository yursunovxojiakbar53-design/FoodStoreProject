package org.example.project.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Permission implements GrantedAuthority {

    // USER
    VIEW_PRODUCTS,
    MANAGE_OWN_CART,
    MANAGE_OWN_ADDRESS,
    CREATE_ORDER,
    VIEW_OWN_ORDERS,
    CANCEL_OWN_ORDER,

    // ORDER
    VIEW_ALL_ORDERS,
    UPDATE_ORDER_STATUS,

    // CATEGORY
    MANAGE_CATEGORIES,

    // PRODUCT
    MANAGE_PRODUCTS,

    // BRANCH
    MANAGE_BRANCHES,

    // SETTINGS
    MANAGE_SETTINGS,

    // CAROUSEL
    MANAGE_CAROUSEL,

    // ABOUT
    MANAGE_ABOUT,

    // USERS
    MANAGE_USERS,

    // REPORTS
    VIEW_STATISTICS,

    // FILES
    MANAGE_ATTACHMENTS;

    @Override
    public String getAuthority() {
        return "PERMISSION_" + name();
    }
}