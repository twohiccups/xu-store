-- Teams
CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    shipping_fee BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE product_groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);

CREATE TABLE product_group_assignments (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    product_group_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_pga_product
        FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_pga_product_group
        FOREIGN KEY (product_group_id) REFERENCES product_groups (id)
);

CREATE TABLE team_product_groups (
    id BIGSERIAL PRIMARY KEY,
    team_id BIGINT NOT NULL,
    product_group_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_tpg_team
        FOREIGN KEY (team_id) REFERENCES teams (id),
    CONSTRAINT fk_tpg_product_group
        FOREIGN KEY (product_group_id) REFERENCES product_groups (id)
);


-- User
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    store_credits BIGINT NOT NULL DEFAULT 0,
    team_id BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_user_team
        FOREIGN KEY (team_id) REFERENCES teams (id)
);


-- PRODUCTS
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    archived BOOLEAN NOT NULL DEFAULT false,
);


CREATE TABLE product_variations (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    variation_name VARCHAR(255) NOT NULL,  -- e.g. "Size M", "Default Variation"
    price BIGINT NOT NULL,                 -- stored in cents (or smallest currency unit)
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_variation_product
        FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT unique_product_variation UNIQUE (product_id, variation_name)
);



CREATE TABLE product_images (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,

    -- Optional timestamps, recommended for audit/tracking
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_product_images
        FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE TYPE order_status AS ENUM ('PENDING', 'SHIPPED', 'COMPLETED', 'CANCELLED', 'ARCHIVED');


CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    team_id BIGINT,
    shipping_fee total_amount BIGINT DEFAULT 0,
    total_amount BIGINT NOT NULL,
    status order_status DEFAULT 'PENDING',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(50) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);



CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,
    product_variation_id BIGINT NOT NULL,
    quantity INT NOT NULL,

    unit_price BIGINT NOT NULL, -- captured at time of checkout (in cents)

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_oi_order
        FOREIGN KEY (order_id) REFERENCES orders (id),
    CONSTRAINT fk_oi_products
        FOREIGN KEY (product_id) REFERENCES products (id),
   CONSTRAINT fk_oi_product_variation
        FOREIGN KEY (product_variation_id) REFERENCES product_variations (id)
);





-- Store credits
CREATE TABLE credit_transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_id BIGINT,                -- can be null if it's a manual credit
    amount BIGINT NOT NULL,         -- positive or negative
    description VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    CONSTRAINT fk_ct_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_ct_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
);



CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,

    amount BIGINT NOT NULL,  -- how much of the order total this payment covers
    payment_method VARCHAR(50) NOT NULL,  -- e.g. 'STRIPE', 'PAYPAL', etc.
    reference_id VARCHAR(255),            -- e.g. Stripe Payment Intent ID

    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- e.g. 'PENDING', 'PAID', 'FAILED'

    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id) REFERENCES orders (id)
);

