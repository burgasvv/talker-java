--liquibase formatted sql

--changeset burgasvv:1
create table if not exists message
(
    id         uuid default gen_random_uuid() unique not null,
    chat_id    uuid references chat (id) on delete cascade on update cascade,
    sender_id  uuid references identity (id) on delete set null on update cascade,
    text       text                                  not null,
    created_at timestamp                             not null
)