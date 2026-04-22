--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity_chat
(
    identity_id uuid references identity (id) on delete cascade on update cascade,
    chat_id     uuid references chat (id) on delete cascade on update cascade,
    primary key (identity_id, chat_id)
)