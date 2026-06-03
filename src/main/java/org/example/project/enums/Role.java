package org.example.project.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum Role {

    ROLE_SUPER_ADMIN(

            Permission.MANAGE_USERS,

            Permission.MANAGE_PRODUCTS,
            Permission.MANAGE_CATEGORIES,
            Permission.MANAGE_BRANCHES,

            Permission.MANAGE_SETTINGS,
            Permission.MANAGE_CAROUSEL,
            Permission.MANAGE_ABOUT,

            Permission.VIEW_ALL_ORDERS,
            Permission.UPDATE_ORDER_STATUS,

            Permission.VIEW_STATISTICS,
            Permission.MANAGE_ATTACHMENTS
    ),

    ROLE_ADMIN(

            Permission.MANAGE_PRODUCTS,
            Permission.MANAGE_CATEGORIES,
            Permission.MANAGE_BRANCHES,

            Permission.MANAGE_SETTINGS,
            Permission.MANAGE_CAROUSEL,
            Permission.MANAGE_ABOUT,

            Permission.VIEW_ALL_ORDERS,
            Permission.UPDATE_ORDER_STATUS,

            Permission.VIEW_STATISTICS,
            Permission.MANAGE_ATTACHMENTS
    ),

    ROLE_OPERATOR(

            Permission.VIEW_ALL_ORDERS,
            Permission.UPDATE_ORDER_STATUS
    ),

    ROLE_USER(

            Permission.VIEW_PRODUCTS,
            Permission.MANAGE_OWN_CART,
            Permission.MANAGE_OWN_ADDRESS,

            Permission.CREATE_ORDER,
            Permission.VIEW_OWN_ORDERS,
            Permission.CANCEL_OWN_ORDER
    );

    private final Set<Permission> permissions;

    Role(Permission... permissions) {
        this.permissions = new HashSet<>(Arrays.asList(permissions));
    }
}