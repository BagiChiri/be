/*
 * Updated com.pos.be.security.rbac.Permissions.java to include Transaction permissions
 */
package com.pos.be.security.rbac;

public class Permissions {

    // Global / Admin
    public static final String FULL_ACCESS = "FULL_ACCESS";

    // Product permissions
    public static final String CREATE_PRODUCT = "CREATE_PRODUCT";
    public static final String READ_PRODUCT   = "READ_PRODUCT";
    public static final String UPDATE_PRODUCT = "UPDATE_PRODUCT";
    public static final String DELETE_PRODUCT = "DELETE_PRODUCT";

    // Category permissions
    public static final String CREATE_CATEGORY = "CREATE_CATEGORY";
    public static final String READ_CATEGORY   = "READ_CATEGORY";
    public static final String UPDATE_CATEGORY = "UPDATE_CATEGORY";
    public static final String DELETE_CATEGORY = "DELETE_CATEGORY";

    // Order permissions
    public static final String CREATE_ORDER = "CREATE_ORDER";
    public static final String READ_ORDER   = "READ_ORDER";
    public static final String UPDATE_ORDER = "UPDATE_ORDER";
    public static final String DELETE_ORDER = "DELETE_ORDER";

    // Transaction permissions (new)
    public static final String CREATE_TRANSACTION = "CREATE_TRANSACTION";
    public static final String READ_TRANSACTION   = "READ_TRANSACTION";
    public static final String UPDATE_TRANSACTION = "UPDATE_TRANSACTION";
    public static final String DELETE_TRANSACTION = "DELETE_TRANSACTION";

    // User permissions
    public static final String CREATE_USER = "CREATE_USER";
    public static final String READ_USER   = "READ_USER";
    public static final String UPDATE_USER = "UPDATE_USER";
    public static final String DELETE_USER = "DELETE_USER";

    // Role permissions
    public static final String CREATE_ROLE = "CREATE_ROLE";
    public static final String READ_ROLE   = "READ_ROLE";
    public static final String UPDATE_ROLE = "UPDATE_ROLE";
    public static final String DELETE_ROLE = "DELETE_ROLE";

    // Authority/Permission permissions
    public static final String CREATE_PERMISSION = "CREATE_PERMISSION";
    public static final String READ_PERMISSION   = "READ_PERMISSION";
    public static final String UPDATE_PERMISSION = "UPDATE_PERMISSION";
    public static final String DELETE_PERMISSION = "DELETE_PERMISSION";

    // Merchant Store permissions
    public static final String CREATE_MERCHANT = "CREATE_MERCHANT";
    public static final String READ_MERCHANT   = "READ_MERCHANT";
    public static final String UPDATE_MERCHANT = "UPDATE_MERCHANT";
    public static final String DELETE_MERCHANT = "DELETE_MERCHANT";

    // Customer permissions (optional)
    public static final String CREATE_CUSTOMER = "CREATE_CUSTOMER";
    public static final String READ_CUSTOMER   = "READ_CUSTOMER";
    public static final String UPDATE_CUSTOMER = "UPDATE_CUSTOMER";
    public static final String DELETE_CUSTOMER = "DELETE_CUSTOMER";
}