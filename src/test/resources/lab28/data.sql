insert into channel(id, name, video_count) values
 (1, '메인채널', 5),
 (2, '새채널', 0);

insert into video(id, title, channel_id, status) values
 (101, '통폐합 대상 영상 1', 1, 'ACTIVE'),
 (102, '통폐합 대상 영상 2', 1, 'ACTIVE'),
 (103, '신고 누적으로 잠긴 영상', 1, 'LOCKED'),
 (104, '통폐합 대상 영상 3', 1, 'ACTIVE'),
 (105, '통폐합 대상 영상 4', 1, 'ACTIVE');
