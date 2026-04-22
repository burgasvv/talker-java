--liquibase formatted sql

--changeset burgasvv:1
create table if not exists comment_file
(
    id           uuid default gen_random_uuid() unique not null,
    name         varchar                               not null,
    content_type varchar                               not null,
    size         bigint                                not null,
    data         bytea                                 not null,
    comment_id   uuid references comment (id) on delete cascade on update cascade
)