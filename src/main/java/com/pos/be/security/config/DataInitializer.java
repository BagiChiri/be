package com.pos.be.security.config;

import com.pos.be.entity.user.Authority;
import com.pos.be.entity.user.Role;
import com.pos.be.entity.user.User;
import com.pos.be.repository.user.AuthorityRepository;
import com.pos.be.repository.user.RoleRepository;
import com.pos.be.repository.user.UserRepository;
import com.pos.be.security.rbac.Permissions;
import com.pos.be.security.rbac.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @Transactional
    @Override
    public void run(String... args) {
        createRoles();
        createPermissions();
        initializeUsers();
    }

    private void createRoles() {
        if (roleRepository.count() == 0) {
            List<Role> roles = Arrays.asList(
                    new Role(Roles.ADMIN),
                    new Role(Roles.SALES),
                    new Role(Roles.INVENTORY),
                    new Role(Roles.CUSTOMER)
            );
            roleRepository.saveAll(roles);
        }
    }

    private void createPermissions() {
        if (authorityRepository.count() == 0) {
            List<Authority> authorities = Arrays.asList(
                    new Authority(Permissions.FULL_ACCESS),

                    // Product
                    new Authority(Permissions.CREATE_PRODUCT),
                    new Authority(Permissions.READ_PRODUCT),
                    new Authority(Permissions.UPDATE_PRODUCT),
                    new Authority(Permissions.DELETE_PRODUCT),

                    // Category
                    new Authority(Permissions.CREATE_CATEGORY),
                    new Authority(Permissions.READ_CATEGORY),
                    new Authority(Permissions.UPDATE_CATEGORY),
                    new Authority(Permissions.DELETE_CATEGORY),

                    // Order
                    new Authority(Permissions.CREATE_ORDER),
                    new Authority(Permissions.READ_ORDER),
                    new Authority(Permissions.UPDATE_ORDER),
                    new Authority(Permissions.DELETE_ORDER),

                    // User
                    new Authority(Permissions.CREATE_USER),
                    new Authority(Permissions.READ_USER),
                    new Authority(Permissions.UPDATE_USER),
                    new Authority(Permissions.DELETE_USER),

                    //Transaction
                    new Authority(Permissions.CREATE_TRANSACTION),
                    new Authority(Permissions.READ_TRANSACTION),
                    new Authority(Permissions.UPDATE_TRANSACTION),
                    new Authority(Permissions.DELETE_TRANSACTION),

                    // Role
                    new Authority(Permissions.CREATE_ROLE),
                    new Authority(Permissions.READ_ROLE),
                    new Authority(Permissions.UPDATE_ROLE),
                    new Authority(Permissions.DELETE_ROLE),

                    // Permission / Authority
                    new Authority(Permissions.CREATE_PERMISSION),
                    new Authority(Permissions.READ_PERMISSION),
                    new Authority(Permissions.UPDATE_PERMISSION),
                    new Authority(Permissions.DELETE_PERMISSION),

                    // Merchant Store
                    new Authority(Permissions.CREATE_MERCHANT),
                    new Authority(Permissions.READ_MERCHANT),
                    new Authority(Permissions.UPDATE_MERCHANT),
                    new Authority(Permissions.DELETE_MERCHANT),

                    // Customer (optional)
                    new Authority(Permissions.CREATE_CUSTOMER),
                    new Authority(Permissions.READ_CUSTOMER),
                    new Authority(Permissions.UPDATE_CUSTOMER),
                    new Authority(Permissions.DELETE_CUSTOMER)
            );

            authorityRepository.saveAll(authorities);
        }
    }

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void initializeUsers() {
        initializeAdminUser();
        initializeSalesUser();
        initializeInventoryUser();
    }

    private void initializeAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            // Get all authorities
            Set<Authority> allPermissions = new HashSet<>(authorityRepository.findAll());

            Role adminRole = roleRepository.findByName(Roles.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setGender("Other");
            admin.setAddress("123, Admin Street");
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole));
            admin.setAuthorities(allPermissions);

            userRepository.save(admin);

            System.out.println("==============================================");
            System.out.println("Initial Admin User Created");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
            System.out.println("==============================================");
        }
    }

    private void initializeSalesUser() {
        if (!userRepository.existsByUsername("sales")) {
            Set<Authority> salesPermissions = new HashSet<>();
            salesPermissions.add(authorityRepository.findByName(Permissions.READ_CATEGORY)
                    .orElseThrow(() -> new RuntimeException("Authority READ_CATEGORY not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.READ_ORDER)
                    .orElseThrow(() -> new RuntimeException("Authority READ_ORDER not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.CREATE_ORDER)
                    .orElseThrow(() -> new RuntimeException("Authority CREATE_ORDER not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.UPDATE_ORDER)
                    .orElseThrow(() -> new RuntimeException("Authority UPDATE_ORDER not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.READ_PRODUCT)
                    .orElseThrow(() -> new RuntimeException("Authority READ_PRODUCT not found")));

            salesPermissions.add(authorityRepository.findByName(Permissions.READ_TRANSACTION)
                    .orElseThrow(() -> new RuntimeException("Authority READ_TRANSACTION not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.DELETE_TRANSACTION)
                    .orElseThrow(() -> new RuntimeException("Authority DELETE_TRANSACTION not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.CREATE_TRANSACTION)
                    .orElseThrow(() -> new RuntimeException("Authority CREATE_TRANSACTION not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.UPDATE_TRANSACTION)
                    .orElseThrow(() -> new RuntimeException("Authority UPDATE_TRANSACTION not found")));

            Role salesRole = roleRepository.findByName(Roles.SALES)
                    .orElseThrow(() -> new RuntimeException("Sales role not found"));

            User sales = new User();
            sales.setUsername("sales");
            sales.setPassword(passwordEncoder.encode("sales123"));
            sales.setFirstName("Aliza");
            sales.setLastName("Ali");
            sales.setGender("Female");
            sales.setAddress("42, Main Street, USA");
            sales.setEnabled(true);
            sales.setRoles(Set.of(salesRole));
            sales.setAuthorities(salesPermissions);

            userRepository.save(sales);

            System.out.println("==============================================");
            System.out.println("Initial Sales User Created");
            System.out.println("Username: sales");
            System.out.println("Password: sales123");
            System.out.println("==============================================");
        }
    }
    private void initializeInventoryUser() {
        if (!userRepository.existsByUsername("inventory")) {
            Set<Authority> inventoryPermissions = new HashSet<>();
            inventoryPermissions.add(authorityRepository.findByName(Permissions.READ_PRODUCT)
                    .orElseThrow(() -> new RuntimeException("Authority READ_PRODUCT not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.CREATE_PRODUCT)
                    .orElseThrow(() -> new RuntimeException("Authority CREATE_PRODUCT not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.UPDATE_PRODUCT)
                    .orElseThrow(() -> new RuntimeException("Authority UPDATE_PRODUCT not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.DELETE_PRODUCT)
                    .orElseThrow(() -> new RuntimeException("Authority DELETE_PRODUCT not found")));

            inventoryPermissions.add(authorityRepository.findByName(Permissions.READ_CATEGORY)
                    .orElseThrow(() -> new RuntimeException("Authority READ_CATEGORY not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.CREATE_CATEGORY)
                    .orElseThrow(() -> new RuntimeException("Authority CREATE_CATEGORY not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.UPDATE_CATEGORY)
                    .orElseThrow(() -> new RuntimeException("Authority UPDATE_CATEGORY not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.DELETE_CATEGORY)
                    .orElseThrow(() -> new RuntimeException("Authority DELETE_CATEGORY not found")));

            Role inventoryRole = roleRepository.findByName("INVENTORY")
                    .orElseThrow(() -> new RuntimeException("Inventory role not found"));

            User inventoryUser = new User();
            inventoryUser.setUsername("inventory");
            inventoryUser.setPassword(passwordEncoder.encode("inventory123"));
            inventoryUser.setFirstName("Ana");
            inventoryUser.setLastName("Maria");
            inventoryUser.setGender("Female");
            inventoryUser.setAddress("55, Warehouse Road, USA");
            inventoryUser.setEnabled(true);
            inventoryUser.setRoles(Set.of(inventoryRole));
            inventoryUser.setAuthorities(inventoryPermissions);

            userRepository.save(inventoryUser);

            System.out.println("==============================================");
            System.out.println("Initial Inventory User Created");
            System.out.println("Username: inventory");
            System.out.println("Password: inventory123");
            System.out.println("==============================================");
        }
    }


}