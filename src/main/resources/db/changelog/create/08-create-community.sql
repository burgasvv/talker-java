--liquibase formatted sql

--changeset burgasvv:1
create table if not exists community
(
    id          uuid                    default gen_random_uuid() unique not null,
    name        varchar unique not null,
    description text           not null,
    admin_id    uuid references identity (id) on delete set null on update cascade,
    created_at  timestamp      not null default now()
)