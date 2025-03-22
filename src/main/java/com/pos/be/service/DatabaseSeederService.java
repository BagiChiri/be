package com.pos.be.service;

import com.pos.be.entity.category.Category;
import com.pos.be.entity.product.CustomOptions;
import com.pos.be.entity.product.Product;
import com.pos.be.entity.user.Role;
import com.pos.be.entity.user.User;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.repository.product.CustomOptionsRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.repository.user.RoleRepository;
import com.pos.be.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

//@Service
public class DatabaseSeederService {

    private final Random random = new Random();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomOptionsRepository customOptionsRepository;

    @Transactional
    public void seedDatabase() {
        // Insert specific Users
        insertUsers();

        // Insert 12,000 records for other entities
        insertProducts();
        insertCategories();
        seedProductCategoryRelationships();
        seedCustomOptions();
    }


    private void insertUsers() {
        Role adminRole = roleRepository.save(new Role("ADMIN"));
        Role salespersonRole = roleRepository.save(new Role("SALESPERSON"));

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEnabled(true);
        admin.getRoles().add(adminRole);

        User salesperson1 = new User();
        salesperson1.setFirstName("John");
        salesperson1.setLastName("Doe");
        salesperson1.setUsername("john.doe");
        salesperson1.setPassword(passwordEncoder.encode("password"));
        salesperson1.setEnabled(true);
        salesperson1.getRoles().add(salespersonRole);

        User salesperson2 = new User();
        salesperson2.setFirstName("Jane");
        salesperson2.setLastName("Smith");
        salesperson2.setUsername("jane.smith");
        salesperson2.setPassword(passwordEncoder.encode("password"));
        salesperson2.setEnabled(true);
        salesperson2.getRoles().add(salespersonRole);

        userRepository.saveAll(List.of(admin, salesperson1, salesperson2));
    }


    private void insertProducts() {
        List<Product> products = new ArrayList<>();
        IntStream.range(0, 12000).forEach(i -> {
            Product product = new Product();
            product.setName("Product " + i);
            product.setDescription("Description for Product " + i);
            product.setPrice(random.nextDouble() * 100);
            product.setQuantity(random.nextInt(1000));
            products.add(product);
        });
        productRepository.saveAll(products);
    }


    private void insertCategories() {
        List<Category> categories = new ArrayList<>();
        IntStream.range(0, 120).forEach(i -> {
            Category category = new Category();
            category.setName("Category " + i);
            categories.add(category);
        });
        categoryRepository.saveAll(categories);
    }


    private void seedProductCategoryRelationships() {
        Iterable<Product> p = productRepository.findAll();
        Iterable<Category> c = categoryRepository.findAll();
        List<Product> products = new ArrayList<>();
        List<Category> categories = new ArrayList<>();
        p.forEach(product -> products.add(product));
        c.forEach(category -> categories.add(category));

        Random random = new Random();
        for (Product product : products) {
            int numCategories = random.nextInt(5) + 1; // Each product gets 1-5 categoryIds
            Set<Category> assignedCategories = new HashSet<>();

            for (int i = 0; i < numCategories; i++) {
                Category randomCategory = categories.get(random.nextInt(categories.size()));
                assignedCategories.add(randomCategory);
            }
            product.setCategories(assignedCategories);
        }

        productRepository.saveAll(products);
        System.out.println("Product-category relationships seeded.");
    }

    private void seedCustomOptions() {
        Random random = new Random();
        Iterable<Product> p = productRepository.findAll();
        List<Product> products = new ArrayList<>();
        p.forEach(product -> products.add(product));
        List<CustomOptions> customOptions = new ArrayList<>();

        for (int i = 1; i <= 12000; i++) {
            Product randomProduct = products.get(random.nextInt(products.size()));
            List<String> values = List.of("Value A", "Value B", "Value C");

            customOptions.add(
                    CustomOptions.builder()
                            .optionLabel("Option Label " + i)
                            .optionType(random.nextBoolean() ? "Type A" : "Type B")
                            .optionValue(values)
                            .product(randomProduct)
                            .build()
            );
        }

        customOptionsRepository.saveAll(customOptions);
        System.out.println("12,000 custom options inserted.");
    }

}
