package org.example.project.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
public enum Role {

    ROLE_SUPER_ADMIN(

            Permission.MANAGE_USERS,
            Permission.MANAGE_OWN_ADDRESS,

            Permission.MANAGE_CAROUSEL,
            Permission.CHANGE_ROLE,
            Permission.MANAGE_PRODUCTS,
            Permission.MANAGE_CATEGORIES,
            Permission.MANAGE_BRANCHES,


            Permission.MANAGE_ABOUT,

            Permission.VIEW_ALL_ORDERS,
            Permission.UPDATE_ORDER_STATUS,

            Permission.MANAGE_ATTACHMENTS,
            Permission.MANAGE_COUPONS

    ),

    ROLE_ADMIN(

            Permission.MANAGE_PRODUCTS,
            Permission.MANAGE_CATEGORIES,
            Permission.MANAGE_BRANCHES,
            Permission.MANAGE_OWN_ADDRESS,
            Permission.MANAGE_CAROUSEL,




            Permission.MANAGE_ABOUT,

            Permission.VIEW_ALL_ORDERS,
            Permission.UPDATE_ORDER_STATUS,

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
            Permission.CANCEL_OWN_ORDER,
            Permission.MANAGE_OWN_REVIEW,
            Permission.MANAGE_OWN_WISHLIST,
            Permission.VIEW_OWN_NOTIFICATIONS,
            Permission.VIEW_OWN_PAYMENTS,
            Permission.VIEW_ALL_CATEGORIES
    );

    private final Set<Permission> permissions;

    Role(Permission... permissions) {
        this.permissions = new HashSet<>(Arrays.asList(permissions));
    }
}