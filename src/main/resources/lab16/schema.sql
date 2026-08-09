create table video (
    id bigint primary key,
    title varchar(200) not null,
    view_count bigint not null default 0,
    reported boolean not null default false
);

create table video_report (
    video_id bigint primary key,
    reason varchar(300) not null
);
