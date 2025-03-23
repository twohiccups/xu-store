INSERT INTO teams (name) VALUES ('Team Alpha');
INSERT INTO teams (name) VALUES ('Team Beta');

-- 2. Insert Product Groups (e.g., Electronics and Books)
INSERT INTO product_groups (name) VALUES ('Electronics');
INSERT INTO product_groups (name) VALUES ('Books');

-- 3. Insert Team-ProductGroup associations
-- Assuming Team Alpha (id=1) is linked with Electronics (id=1) and Team Beta (id=2) with Books (id=2)
INSERT INTO team_product_groups (team_id, product_group_id) VALUES (1, 1);
INSERT INTO team_product_groups (team_id, product_group_id) VALUES (2, 2);

-- 4. Insert Users
-- User for Team Alpha
INSERT INTO users (email, password_hash, role, store_credits, team_id)
VALUES ('alpha@example.com', 'hash1', 'ADMIN', 10000, 1);
-- User for Team Beta
INSERT INTO users (email, password_hash, role, store_credits, team_id)
VALUES ('beta@example.com', 'hash2', 'USER', 5000, 2);

-- 5. Insert Products
-- Note: This assumes your Product table now has a column for product_group_id.
INSERT INTO products (name, description, product_group_id)
VALUES ('Smartphone', 'Latest model smartphone', 1);
INSERT INTO products (name, description, product_group_id)
VALUES ('Novel Book', 'Bestselling novel', 2);

-- 6. Insert Product Variations
-- For the Smartphone (assumed id=1) and Novel Book (assumed id=2)
INSERT INTO product_variations (product_id, variation_name, price)
VALUES (1, 'Default', 69900);
INSERT INTO product_variations (product_id, variation_name, price)
VALUES (2, 'Hardcover', 1999);

-- 7. Insert Product Images
INSERT INTO product_images (product_id, image_url)
VALUES (1, 'https://example.com/images/smartphone.jpg');
INSERT INTO product_images (product_id, image_url)
VALUES (2, 'https://example.com/images/novel.jpg');

-- 8. Insert an Order
-- Create an order for the user from Team Alpha (user id=1, team id=1) for the Smartphone.
INSERT INTO orders (user_id, team_id, total_amount, status)
VALUES (1, 1, 69900, 'PENDING');

-- 9. Insert an Order Item
-- For order id=1, add one order item for the smartphone variation (assumed product_variation id=1).
INSERT INTO order_items (order_id, product_variation_id, quantity, unit_price)
VALUES (1, 1, 1, 69900);

-- 10. Insert a Credit Transaction
-- For user id=1, associated with order id=1 (e.g., store credit deduction for the purchase)
INSERT INTO credit_transactions (user_id, order_id, amount, description)
VALUES (1, 1, -69900, 'Store credit deduction for Smartphone purchase');

-- 11. Insert a Payment
-- For order id=1, a payment via STRIPE
INSERT INTO payments (order_id, amount, payment_method, reference_id, status)
VALUES (1, 69900, 'STRIPE', 'pi_123456', 'PAID');