--liquibase formatted sql

--changeset burgasvv:1
create table if not exists identity_community
(
    identity_id  uuid references identity (id) on delete cascade on update cascade,
    community_id uuid references community (id) on delete cascade on update cascade,
    primary key (identity_id, community_id)
)