create table users (
                       id bigserial primary key,
                       email varchar(200) not null unique,
                       password_hash varchar(200) not null,
                       created_at timestamptz not null default now()
);

create table products (
                          id bigserial primary key,
                          seller_id bigint not null,
                          name varchar(300) not null,
                          price_cents bigint not null,
                          created_at timestamptz not null default now()
);

create table inventory (
                           product_id bigint primary key references products(id),
                           available_qty int not null,
                           reserved_qty int not null,
                           version bigint not null
);

create table orders (
                        id bigserial primary key,
                        buyer_id bigint not null,
                        status varchar(40) not null,
                        total_cents bigint not null,
                        created_at timestamptz not null default now()
);

create table order_items (
                             id bigserial primary key,
                             order_id bigint not null references orders(id),
                             product_id bigint not null references products(id),
                             qty int not null,
                             price_cents bigint not null
);

create table outbox_events (
                               id bigserial primary key,
                               event_type varchar(120) not null,
                               payload_json text not null,
                               status varchar(40) not null,
                               created_at timestamptz not null default now()
);