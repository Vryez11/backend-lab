create table if not exists lab27_video (
    id bigint auto_increment primary key,
    title varchar(200) not null,
    uploader_id varchar(50) not null,
    created_at timestamp not null
);

create table if not exists lab27_video_stat (
    video_id bigint primary key,
    view_count bigint not null
);

-- 다른 팀 소유의 레거시 감사 테이블. 스키마 변경 불가(컬럼 크기 포함).
create table if not exists lab27_upload_audit_log (
    id bigint auto_increment primary key,
    video_id bigint not null,
    uploader_id varchar(50) not null,
    video_title varchar(15) not null,
    created_at timestamp not null
);
