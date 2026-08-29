drop table if exists transfer_log;
drop table if exists video;
drop table if exists channel;

create table channel (
    id          bigint       primary key,
    name        varchar(100) not null,
    video_count int          not null
);

create table video (
    id         bigint       primary key,
    title      varchar(200) not null,
    channel_id bigint       not null,
    status     varchar(20)  not null   -- ACTIVE | LOCKED
);

create table transfer_log (
    id              bigint auto_increment primary key,
    video_id        bigint    not null,
    from_channel_id bigint    not null,
    to_channel_id   bigint    not null,
    moved_at        timestamp not null
);
