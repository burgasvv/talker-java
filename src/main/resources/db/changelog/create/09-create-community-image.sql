--liquibase formatted sql

--changeset burgasvv:1
create table if not exists community_image
(
    id           uuid default gen_random_uuid() unique not null,
    name         varchar                               not null,
    content_type varchar                               not null,
    size         bigint                                not null,
    preview      boolean                               not null,
    data         bytea                                 not null,
    community_id uuid references community (id) on delete cascade on update cascade
)