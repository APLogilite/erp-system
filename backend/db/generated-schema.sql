
    create table auth_entities (
        is_active boolean not null,
        created_at timestamp(6) not null,
        deleted_at timestamp(6),
        updated_at timestamp(6) not null,
        created_by uuid,
        id uuid not null,
        updated_by uuid,
        primary key (id)
    );

    create table order_lines (
        is_active boolean not null,
        line_total float(53) not null,
        quantity float(53) not null,
        unit_price float(53) not null,
        created_at timestamp(6) not null,
        deleted_at timestamp(6),
        updated_at timestamp(6) not null,
        created_by uuid,
        id uuid not null,
        order_id uuid not null,
        product_id uuid not null,
        updated_by uuid,
        primary key (id)
    );

    create table orders (
        is_active boolean not null,
        total_amount float(53) not null,
        created_at timestamp(6) not null,
        deleted_at timestamp(6),
        order_date timestamp(6) not null,
        updated_at timestamp(6) not null,
        created_by uuid,
        id uuid not null,
        party_id uuid not null,
        updated_by uuid,
        order_number varchar(255) not null unique,
        order_type varchar(255) not null,
        status varchar(255) not null,
        primary key (id)
    );

    create table products (
        cost_price float(53),
        is_active boolean not null,
        sale_price float(53),
        created_at timestamp(6) not null,
        deleted_at timestamp(6),
        updated_at timestamp(6) not null,
        created_by uuid,
        id uuid not null,
        updated_by uuid,
        category varchar(255),
        description varchar(255),
        name varchar(255) not null,
        sku varchar(255) not null unique,
        type varchar(255),
        uom varchar(255),
        primary key (id)
    );

    create table stock_movements (
        is_active boolean not null,
        quantity float(53) not null,
        created_at timestamp(6) not null,
        deleted_at timestamp(6),
        movement_date timestamp(6) not null,
        updated_at timestamp(6) not null,
        created_by uuid,
        id uuid not null,
        product_id uuid not null,
        reference_id uuid,
        updated_by uuid,
        warehouse_id uuid not null,
        movement_type varchar(255) not null,
        reference_type varchar(255),
        primary key (id)
    );

    create table user_entities (
        is_active boolean not null,
        created_at timestamp(6) not null,
        deleted_at timestamp(6),
        updated_at timestamp(6) not null,
        created_by uuid,
        id uuid not null,
        updated_by uuid,
        primary key (id)
    );

    create table warehouses (
        is_active boolean not null,
        created_at timestamp(6) not null,
        deleted_at timestamp(6),
        updated_at timestamp(6) not null,
        created_by uuid,
        id uuid not null,
        updated_by uuid,
        location varchar(255),
        name varchar(255) not null,
        primary key (id)
    );
