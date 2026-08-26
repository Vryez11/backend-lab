drop table if exists moderation_log;
drop table if exists videos;

create table videos (
    id      bigint       primary key,
    title   varchar(200) not null,
    status  varchar(20)  not null   -- PUBLIC / PRIVATE / DELETED
);

create table moderation_log (
    id        bigint auto_increment primary key,
    video_id  bigint      not null,
    action    varchar(20) not null
);
