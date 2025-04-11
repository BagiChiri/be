package com.pos.be.security.rbac;

public class Permissions {
    // Admin permissions
    public static final String FULL_ACCESS = "FULL_ACCESS";

    // Product permissions
    public static final String PRODUCT_VIEW = "PRODUCT_VIEW";
    public static final String PRODUCT_MANAGE = "PRODUCT_MANAGE";

    // Category permissions
    public static final String CATEGORY_VIEW = "CATEGORY_VIEW";
    public static final String CATEGORY_MANAGE = "CATEGORY_MANAGE";

    // Order permissions
    public static final String ORDER_VIEW = "ORDER_VIEW";
    public static final String ORDER_CREATE = "ORDER_CREATE";
    public static final String ORDER_MANAGE = "ORDER_MANAGE";

    // User permissions
    public static final String USER_MANAGE = "USER_MANAGE";
}
