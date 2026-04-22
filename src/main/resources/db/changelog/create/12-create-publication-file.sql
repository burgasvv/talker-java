--liquibase formatted sql

--changeset burgasvv:1
create table if not exists publication_file
(
    id             uuid default gen_random_uuid() unique not null,
    name           varchar                               not null,
    content_type   varchar                               not null,
    size           bigint                                not null,
    data           bytea                                 not null,
    publication_id uuid references publication (id) on delete cascade on update cascade
)