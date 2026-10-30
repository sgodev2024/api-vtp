CREATE TABLE retail.orders (
    
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    order_code VARCHAR(50) NOT NULL,

    status VARCHAR(50) NOT NULL,
    shipping_providers_id BIGINT NOT NULL,
    product_name VARCHAR(255),
    total_amount NUMERIC(15, 2),

    receiver_name VARCHAR(255) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    receiver_address TEXT NOT NULL,

    sender_full_name VARCHAR(100),
    sender_phone VARCHAR(50),
    sender_address VARCHAR(255),

    vtp_service_id VARCHAR(50),
    
    vtp_order_number VARCHAR(50),

    money_collection BIGINT DEFAULT 0,
    money_total BIGINT DEFAULT 0,
    money_total_fee BIGINT DEFAULT 0,
    money_fee BIGINT DEFAULT 0,
    money_collection_fee BIGINT DEFAULT 0,
    money_vat BIGINT DEFAULT 0,
    
    kpi_ht DOUBLE PRECISION DEFAULT 0,
    exchange_weight BIGINT DEFAULT 0,
    
    receiver_province BIGINT,
    receiver_district BIGINT,
    receiver_ward BIGINT,
    sort_code VARCHAR(50),

    vtp_order_status INT,
    vtp_web_hook_response JSONB, 
    vtp_order_status_at TIMESTAMPTZ,

    -- Lưu log Payload
    request_payload JSONB,
    response_payload JSONB,

    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_order_code_tenant UNIQUE (tenant_id, order_code)
);

-- Index để tra cứu nhanh theo mã vận đơn
CREATE INDEX idx_orders_vtp_number ON retail.orders(vtp_order_number);