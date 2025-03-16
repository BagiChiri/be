delete from product_category;
delete from product_image;
delete from product;
select * from product_image;

-- Electronics – Product 1: Smartphone X
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Smartphone X',
           'Latest model smartphone with 128GB storage, dual cameras, and a 6.1-inch display.',
           699.99,
           50,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1511707171634-5f897ff02aa9', true, LAST_INSERT_ID());

-- Electronics – Product 2: 4K Ultra HD TV
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           '4K Ultra HD TV',
           '55-inch smart TV with 4K resolution, HDR support, and integrated streaming apps.',
           899.99,
           30,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1519648023493-d82b5f8d7e72', true, LAST_INSERT_ID());

-- Electronics – Product 3: Wireless Headphones
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Wireless Headphones',
           'Noise-cancelling over-ear headphones offering up to 30 hours of battery life.',
           199.99,
           75,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1511367461989-f85a21fda167', true, LAST_INSERT_ID());

-- Clothing – Product 1: Men's Casual Shirt
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Men''s Casual Shirt',
           '100% cotton shirt in blue and white patterns, available in various sizes.',
           29.99,
           100,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1512436991641-6745cdb1723f', true, LAST_INSERT_ID());

-- Clothing – Product 2: Women''s Summer Dress
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Women''s Summer Dress',
           'Lightweight dress with a floral design perfect for warm weather, available in multiple colors.',
           49.99,
           80,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1556905055-8f358a7a47b2', true, LAST_INSERT_ID());

-- Clothing – Product 3: Unisex Hoodie
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Unisex Hoodie',
           'Comfortable hoodie with adjustable drawstrings and a spacious front pocket.',
           39.99,
           120,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1521572163474-6864f9cf17ab', true, LAST_INSERT_ID());

-- Home & Kitchen – Product 1: Stainless Steel Cookware Set
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Stainless Steel Cookware Set',
           '10-piece cookware set made of durable stainless steel with heat-resistant handles.',
           249.99,
           40,
           'set'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1507089947368-19c1da9775ae', true, LAST_INSERT_ID());

-- Home & Kitchen – Product 2: Ergonomic Chef Knife
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Ergonomic Chef Knife',
           'High-carbon stainless steel chef knife with a non-slip handle, perfect for slicing and dicing.',
           59.99,
           150,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1512820790803-83ca734da794', true, LAST_INSERT_ID());

-- Home & Kitchen – Product 3: Designer Table Lamp
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Designer Table Lamp',
           'Modern table lamp featuring energy-efficient LED lighting and an elegant design.',
           89.99,
           60,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1556911220-e15b29be8c7d', true, LAST_INSERT_ID());

-- Sports & Outdoors – Product 1: All-Terrain Mountain Bike
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'All-Terrain Mountain Bike',
           'Durable mountain bike with front suspension, perfect for off-road trails.',
           499.99,
           20,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1508610048659-a06b669e332b', true, LAST_INSERT_ID());

-- Sports & Outdoors – Product 2: Inflatable Kayak
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Inflatable Kayak',
           'Lightweight and portable kayak ideal for river and lake adventures.',
           299.99,
           15,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1507525428034-b723cf961d3e', true, LAST_INSERT_ID());

-- Sports & Outdoors – Product 3: High-Performance Running Shoes
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'High-Performance Running Shoes',
           'Lightweight and breathable shoes designed for long-distance running.',
           119.99,
           70,
           'pairs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1579193054148-0f8f6f32c9a1', true, LAST_INSERT_ID());

-- Beauty & Personal Care – Product 1: Hydrating Facial Moisturizer
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Hydrating Facial Moisturizer',
           'Lightweight moisturizer enriched with hyaluronic acid for deep hydration.',
           24.99,
           200,
           'pcs'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1522337660859-02fbefca4702', true, LAST_INSERT_ID());

-- Beauty & Personal Care – Product 2: Organic Hair Oil
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Organic Hair Oil',
           'Nourishing hair oil formulated with natural ingredients for shine and strength.',
           19.99,
           150,
           'bottle'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1519744346369-70c8b01d7f9d', true, LAST_INSERT_ID());

-- Beauty & Personal Care – Product 3: Men's Grooming Kit
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Men''s Grooming Kit',
           'Complete grooming kit including trimmer, razor, and skincare essentials.',
           49.99,
           80,
           'set'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1508214751196-bcfd4ca60f91', true, LAST_INSERT_ID());


INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Wireless Bluetooth Earbuds',
           'True wireless earbuds with 24hr battery life and noise cancellation',
           89.99,
           120,
           'pair'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 1);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1590658006821-04f4008d5717', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Women''s Yoga Leggings',
           'High-waisted stretchy leggings for fitness activities',
           34.95,
           200,
           'piece'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1588589678413-6ab7a3c26b63', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Ceramic Non-Stick Cookware Set',
           '10-piece ceramic coated cookware set with stainless steel handles',
           149.99,
           75,
           'set'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Camping Tent 4-Person',
           'Waterproof dome tent with rainfly and aluminum poles',
           129.95,
           60,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1504280390367-361c6d9f38f4', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Vitamin C Facial Serum',
           'Antioxidant serum with hyaluronic acid for brightening skin',
           29.99,
           150,
           'bottle'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1620916566398-6febd4c9d55f', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Hardcover Recipe Book',
           '500 professional recipes with step-by-step instructions',
           35.00,
           300,
           'copy'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 6);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1544947950-fa07a98d237f', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Building Blocks Set',
           '250-piece interlocking plastic building blocks for creative play',
           39.99,
           90,
           'set'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 7);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1587654780291-39c9404d746b', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Car Jump Starter',
           '2000A peak portable lithium battery with USB charging ports',
           99.95,
           45,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 8);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Organic Arabica Coffee Beans',
           '1kg bag of medium roast specialty grade coffee beans',
           18.99,
           500,
           'bag'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 9);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1533777857889-4be7c70b33f7', true, LAST_INSERT_ID());

INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Digital Blood Pressure Monitor',
           'Automatic upper arm cuff with irregular heartbeat detector',
           49.95,
           85,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 10);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1584306670957-9c8d4cbd2a0d', true, LAST_INSERT_ID());

-- Product 1
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
    'Infrared Thermometer',
    'Non-contact digital forehead thermometer for adults and children',
    29.99,
    120,
    'unit'
);
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 10);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1584433144859-1f0b9d39db80', true, LAST_INSERT_ID());

-- Product 2
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
    'Pulse Oximeter',
    'Finger-tip pulse oximeter for measuring blood oxygen saturation levels',
    19.95,
    200,
    'unit'
);
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 10);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1584382292896-b6a9afc7d0b3', true, LAST_INSERT_ID());

-- Product 3
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
    'Digital Glucose Meter',
    'Accurate and easy-to-use blood glucose monitoring system',
    39.99,
    150,
    'unit'
);
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 10);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1598960134561-713f0e2ec5c1', true, LAST_INSERT_ID());

-- Product 4
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
    'Nebulizer Machine',
    'Portable nebulizer for effective respiratory therapy at home',
    59.99,
    75,
    'unit'
);
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 10);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1599602433682-63b2bdaea4b2', true, LAST_INSERT_ID());

-- Product 5
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
    'Electronic Stethoscope',
    'Advanced digital stethoscope with noise-canceling technology',
    89.99,
    60,
    'unit'
);
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 10);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1594729095022-e2f6d2c2ed3b', true, LAST_INSERT_ID());

-- Product 1
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
    'Men\'s Cotton T-Shirt',
    '100% cotton, breathable, and comfortable casual wear',
    15.99,
    200,
    'unit'
);
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1520975921233-56c8b3f58ebd', true, LAST_INSERT_ID());

-- Product 2
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Women\'s Denim Jacket',
           'Classic blue denim jacket with a relaxed fit',
           49.99,
           120,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1562158070-622a0896b88b', true, LAST_INSERT_ID());

-- Product 3
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Unisex Sports Hoodie',
           'Comfortable hoodie made from soft, durable fabric',
           35.50,
           150,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1556905055-8f358a7a47b2', true, LAST_INSERT_ID());

-- Product 4
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Women\'s Summer Dress',
           'Lightweight floral dress, perfect for summer outings',
           29.99,
           90,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1593032457869-0a08240d2b68', true, LAST_INSERT_ID());

-- Product 5
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Men\'s Formal Pants',
           'Slim fit, wrinkle-resistant trousers for office wear',
           40.00,
           110,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 2);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1520975911443-722d9b8cfcda', true, LAST_INSERT_ID());

-- Product 1
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Stainless Steel Cookware Set',
           '10-piece non-stick stainless steel cookware set, dishwasher safe',
           129.99,
           50,
           'set'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1556910103-1c8c4d8b6b90', true, LAST_INSERT_ID());

-- Product 2
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Ceramic Dinnerware Set',
           '16-piece ceramic dinnerware set with modern design',
           79.99,
           80,
           'set'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1604881991267-1992cfec1c91', true, LAST_INSERT_ID());

-- Product 3
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Electric Kettle',
           '1.7L stainless steel electric kettle with auto shut-off',
           39.95,
           100,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1581307905233-efb4e56ed8ba', true, LAST_INSERT_ID());

-- Product 4
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Memory Foam Pillow',
           'Ergonomic memory foam pillow with washable cover',
           25.50,
           200,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1578894388664-242b4c162f69', true, LAST_INSERT_ID());

-- Product 5
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Wall-Mounted Spice Rack',
           'Metal wall-mounted spice rack with 4 tiers for easy organization',
           45.00,
           70,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 3);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1594910919266-cbd1dc0d38f7', true, LAST_INSERT_ID());

-- Product 1
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Adjustable Dumbbell Set',
           '25kg adjustable dumbbell set with anti-slip grip and secure locking mechanism',
           89.99,
           60,
           'set'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1583454110551-21f19ef60e5e', true, LAST_INSERT_ID());

-- Product 2
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Camping Tent',
           '4-person waterproof camping tent with easy setup and UV protection',
           149.99,
           30,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1518893883800-6185e58b47a1', true, LAST_INSERT_ID());

-- Product 3
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Yoga Mat',
           'Non-slip, eco-friendly yoga mat with carrying strap, 6mm thickness',
           35.00,
           120,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1599058917212-8cd68c12e5c4', true, LAST_INSERT_ID());

-- Product 4
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Mountain Bike Helmet',
           'Lightweight and durable mountain bike helmet with adjustable straps',
           59.95,
           80,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1520697222860-5dba3b5ad1d4', true, LAST_INSERT_ID());

-- Product 5
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Portable Folding Chair',
           'Lightweight and compact outdoor folding chair with cup holder',
           39.99,
           100,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 4);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1585652258441-08a0cfedc3c7', true, LAST_INSERT_ID());

-- Product 1
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Vitamin C Serum',
           'Brightening serum with 20% vitamin C for radiant skin and reducing dark spots',
           29.99,
           150,
           'bottle'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1612977431584-0d78e0ef8f58', true, LAST_INSERT_ID());

-- Product 2
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Organic Shampoo',
           'Sulfate-free shampoo enriched with argan oil for smooth and healthy hair',
           19.50,
           200,
           'bottle'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1600185365523-536de396ef5b', true, LAST_INSERT_ID());

-- Product 3
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Facial Cleansing Brush',
           'Rechargeable silicone facial cleansing brush for deep pore cleansing',
           45.00,
           80,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1612817152244-98ab20f774c4', true, LAST_INSERT_ID());

-- Product 4
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Luxury Lip Balm',
           'Hydrating lip balm with natural oils and SPF 15 protection',
           12.99,
           300,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1618843266421-fd14df4d3fa6', true, LAST_INSERT_ID());

-- Product 5
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Rose Water Toner',
           'Alcohol-free toner with pure rose water for skin hydration and balance',
           16.99,
           180,
           'bottle'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 5);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1627308595229-7830a5c91f9f', true, LAST_INSERT_ID());

-- Product 1
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'The Silent Patient',
           'A psychological thriller novel by Alex Michaelides that explores a gripping mystery.',
           14.99,
           120,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 6);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1553729784-e91953dec042', true, LAST_INSERT_ID());

-- Product 2
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Atomic Habits',
           'James Clear’s guide to building good habits and breaking bad ones with actionable strategies.',
           18.50,
           200,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 6);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1512820790803-83ca734da794', true, LAST_INSERT_ID());

-- Product 3
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Becoming',
           'Michelle Obama’s inspiring memoir chronicling her journey from childhood to First Lady.',
           22.00,
           90,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 6);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1532012197267-da84d127e765', true, LAST_INSERT_ID());

-- Product 4
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'The Alchemist',
           'A philosophical book by Paulo Coelho about a young shepherd’s journey to find his destiny.',
           13.75,
           180,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 6);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1496104679561-38b3b4d5ef63', true, LAST_INSERT_ID());

-- Product 5
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Educated',
           'Tara Westover’s memoir about growing up in a strict household and her quest for education.',
           17.25,
           150,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 6);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1532012197267-da84d127e765', true, LAST_INSERT_ID());

-- Product 1
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Lego Classic Creative Bricks Set',
           'A 500-piece building block set to spark creativity and imaginative play.',
           29.99,
           100,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 7);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1584697964403-0a58c1e8a5c6', true, LAST_INSERT_ID());

-- Product 2
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Monopoly Classic Board Game',
           'The timeless property trading game for family and friends.',
           19.95,
           80,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 7);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1600453403744-1c1915e5b2a2', true, LAST_INSERT_ID());

-- Product 3
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Nerf N-Strike Elite Disruptor',
           'A quick-draw, fast-firing toy blaster with a rotating drum.',
           15.99,
           150,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 7);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1605902711622-cfb43c4437b1', true, LAST_INSERT_ID());

-- Product 4
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Rubik\'s Cube 3x3',
           'The classic color-matching puzzle game that challenges your mind.',
           9.99,
           200,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 7);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1518655048521-f130df041f66', true, LAST_INSERT_ID());

-- Product 5
INSERT INTO product (name, description, price, quantity, unit)
VALUES (
           'Barbie Dreamhouse Dollhouse',
           'A three-story dream house featuring interactive elements and furniture.',
           179.00,
           60,
           'unit'
       );
INSERT INTO product_category (product_id, category_id) VALUES (LAST_INSERT_ID(), 7);
INSERT INTO product_image (url, primary_image, product_id)
VALUES ('https://images.unsplash.com/photo-1587019155496-57e338b024c0', true, LAST_INSERT_ID());
