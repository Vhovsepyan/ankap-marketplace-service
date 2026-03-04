create table users (
                       id bigserial primary key,
                       email varchar(200) not null unique,
                       password_hash varchar(200) not null,
                       role varchar(40) not null default 'BUYER',
                       created_at timestamptz not null default now()
);