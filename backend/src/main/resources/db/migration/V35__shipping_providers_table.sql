CREATE TABLE retail.shipping_providers (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id UUID NOT NULL,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    
    token TEXT,
    token_expires_at TIMESTAMPTZ,

    sender_name VARCHAR(100),
    sender_phone VARCHAR(50),
    sender_address VARCHAR(500),

    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    is_configured BOOLEAN NOT NULL DEFAULT FALSE,
    disabled_reason TEXT,

    updated_by UUID ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

