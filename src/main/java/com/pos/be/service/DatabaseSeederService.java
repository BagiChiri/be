package com.pos.be.service;

import com.pos.be.entity.category.Category;
import com.pos.be.entity.customer.Customer;
import com.pos.be.entity.order.Orders;
import com.pos.be.entity.order.OrderDetail;
import com.pos.be.entity.product.CustomOptions;
import com.pos.be.entity.product.Product;
import com.pos.be.entity.shipper.Shipper;
import com.pos.be.entity.supplier.Supplier;
import com.pos.be.entity.user.Role;
import com.pos.be.entity.user.User;
import com.pos.be.repository.category.CategoryRepository;
import com.pos.be.repository.customer.CustomerRepository;
import com.pos.be.repository.order.OrderDetailRepository;
import com.pos.be.repository.order.OrdersRepository;
import com.pos.be.repository.product.CustomOptionsRepository;
import com.pos.be.repository.product.ProductRepository;
import com.pos.be.repository.shipper.ShipperRepository;
import com.pos.be.repository.supplier.SupplierRepository;
import com.pos.be.repository.user.RoleRepository;
import com.pos.be.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.IntStream;

//@Service
public class DatabaseSeederService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ShipperRepository shipperRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomOptionsRepository customOptionsRepository;

    private final Random random = new Random();

    @Transactional
    public void seedDatabase() {
        // Insert specific Users
        insertUsers();

        // Insert 12,000 records for other entities
        insertCustomers();
        insertShippers();
        insertProducts();
        insertSuppliers();
        insertCategories();
        insertOrdersAndDetails();
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

    private void insertCustomers() {
        List<Customer> customers = new ArrayList<>();
        IntStream.range(0, 12000).forEach(i -> {
            Customer customer = new Customer();
            customer.setName("Customer " + i);
            customer.setAddress("Address " + i);
            customer.setCity("City " + i);
            customer.setPostalCode("1000" + i);
            customer.setCountry("Country " + (i % 100));
            customer.setPhoneNumber("123456789" + i);
            customers.add(customer);
        });
        customerRepository.saveAll(customers);
    }

    private void insertShippers() {
        List<Shipper> shippers = new ArrayList<>();
        IntStream.range(0, 12000).forEach(i -> {
            Shipper shipper = new Shipper();
            shipper.setShipperName("Shipper " + i);
            shipper.setPhoneNumber("987654321" + i);
            shippers.add(shipper);
        });
        shipperRepository.saveAll(shippers);
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

    private void insertSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        IntStream.range(0, 12000).forEach(i -> {
            Supplier supplier = new Supplier();
            supplier.setName("Supplier " + i);
            supplier.setContactName("Contact " + i);
            supplier.setAddress("Address " + i);
            supplier.setCity("City " + i);
            supplier.setPostalCode("1000" + i);
            supplier.setCountry("Country " + (i % 100));
            supplier.setPhoneNumber("123456789" + i);
            suppliers.add(supplier);
        });
        supplierRepository.saveAll(suppliers);
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

    private void insertOrdersAndDetails() {
        List<Orders> orders = new ArrayList<>();
        List<OrderDetail> orderDetails = new ArrayList<>();

        List<User> users = userRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        List<Shipper> shippers = shipperRepository.findAll();
        List<Product> products = productRepository.findAll();

        IntStream.range(0, 12000).forEach(i -> {
            Orders order = new Orders();
            order.setUser(users.get(random.nextInt(users.size())));
            order.setCustomer(customers.get(random.nextInt(customers.size())));
            order.setShipper(shippers.get(random.nextInt(shippers.size())));
            order.setOrderDate(new Date());

            orders.add(order);

            OrderDetail detail = new OrderDetail();
            detail.setOrders(order);
            detail.setProduct(products.get(random.nextInt(products.size())));
            detail.setQuantity(random.nextInt(10) + 1);

            orderDetails.add(detail);
        });

        ordersRepository.saveAll(orders);
        orderDetailRepository.saveAll(orderDetails);
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
            int numCategories = random.nextInt(5) + 1; // Each product gets 1-5 categories
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
