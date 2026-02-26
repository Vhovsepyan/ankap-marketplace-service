create table idempotency_keys (
                                  idempotency_key varchar(80) primary key,
                                  order_id bigint not null references orders(id),
                                  created_at timestamptz not null default now()
);