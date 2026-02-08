CREATE TABLE public.addresses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,

    full_name VARCHAR(150),
    phone_number VARCHAR(15),

    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    pincode VARCHAR(10),
    country VARCHAR(100) NOT NULL,

    is_default BOOLEAN NOT NULL DEFAULT false,
    address_type VARCHAR(30) NOT NULL DEFAULT 'HOME',

    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_addresses_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);
