--liquibase formatted sql

--changeset burgasvv:1
create table if not exists chat_image
(
    id           uuid default gen_random_uuid() unique not null,
    name         varchar                               not null,
    content_type varchar                               not null,
    size         bigint                                not null,
    preview      boolean                               not null,
    data         bytea                                 not null,
    chat_id      uuid references chat (id) on delete cascade on update cascade
)