INSERT INTO interest
    ( seq, interest_group_seq, created_at, updated_at, name )
VALUES

    # 아웃도어/ 여행
    (1, 1, now(), now(), '해외여행'),
    (2, 1, now(), now(), '국내여행'),
    (3, 1, now(), now(), '당일치기'),

    # 운동/스포츠
    (4, 2, now(), now(), '자전거'),
    (5, 2, now(), now(), '볼링'),
    (6, 2, now(), now(), '배드민'),
    (7, 2, now(), now(), '헬스'),
    (8, 2, now(), now(), '크로스핏'),

    # 인문학/책/글
    (9, 3, now(), now(), '책읽기'),
    (10, 3, now(), now(), '시 감상'),

    # 외국/언어
    (11, 4, now(), now(), '영어'),
    (12, 4, now(), now(), '독일'),
    (13, 4, now(), now(), '일본거'),

    # 문화/공연/축제
    (14, 5, now(), now(), '콘서트'),
    (15, 5, now(), now(), '클래식'),
    (16, 5, now(), now(), '오페라'),
    (17, 5, now(), now(), '재즈');

