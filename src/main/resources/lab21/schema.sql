create table if not exists video (
    id           bigint auto_increment primary key,
    handle       varchar(30)  not null unique,
    title        varchar(100) not null,
    category     varchar(20)  not null,
    duration_sec int          not null
);
