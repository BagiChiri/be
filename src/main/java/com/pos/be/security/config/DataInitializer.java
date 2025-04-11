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
                    new Authority(Permissions.PRODUCT_VIEW),
                    new Authority(Permissions.PRODUCT_MANAGE),
                    new Authority(Permissions.CATEGORY_VIEW),
                    new Authority(Permissions.CATEGORY_MANAGE),
                    new Authority(Permissions.ORDER_VIEW),
                    new Authority(Permissions.ORDER_CREATE),
                    new Authority(Permissions.ORDER_MANAGE),
                    new Authority(Permissions.USER_MANAGE)
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
            // Fetch authorities from DB
            Set<Authority> salesPermissions = new HashSet<>();
            salesPermissions.add(authorityRepository.findByName(Permissions.CATEGORY_VIEW)
                    .orElseThrow(() -> new RuntimeException("Authority CATEGORY_VIEW not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.ORDER_VIEW)
                    .orElseThrow(() -> new RuntimeException("Authority ORDER_VIEW not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.ORDER_CREATE)
                    .orElseThrow(() -> new RuntimeException("Authority ORDER_CREATE not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.PRODUCT_VIEW)
                    .orElseThrow(() -> new RuntimeException("Authority PRODUCT_VIEW not found")));
            salesPermissions.add(authorityRepository.findByName(Permissions.ORDER_MANAGE)
                    .orElseThrow(() -> new RuntimeException("Authority ORDER_MANAGE not found")));

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
            // Fetch authorities from DB
            Set<Authority> inventoryPermissions = new HashSet<>();
            inventoryPermissions.add(authorityRepository.findByName(Permissions.PRODUCT_VIEW)
                    .orElseThrow(() -> new RuntimeException("Authority PRODUCT_VIEW not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.PRODUCT_MANAGE)
                    .orElseThrow(() -> new RuntimeException("Authority PRODUCT_MANAGE not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.CATEGORY_VIEW)
                    .orElseThrow(() -> new RuntimeException("Authority CATEGORY_VIEW not found")));
            inventoryPermissions.add(authorityRepository.findByName(Permissions.CATEGORY_MANAGE)
                    .orElseThrow(() -> new RuntimeException("Authority CATEGORY_MANAGE not found")));

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