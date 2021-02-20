create table board
(
    board_seq bigint auto_increment
        primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    content varchar(255) null,
    title varchar(255) null
);

create table club
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    description text null,
    maximum_number bigint not null,
    name varchar(255) null,
    main_image_url varchar(511) null
)
    collate=utf8mb4_unicode_ci;

create table hibernate_sequence
(
    next_val bigint null
)
    collate=utf8mb4_unicode_ci;

create table interest_group
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    name varchar(255) null
)
    collate=utf8mb4_unicode_ci;

create table interest
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    name varchar(255) null,
    interest_group_seq bigint null,
    sequence bigint not null,
    constraint FK641m4p065t2ymbn5txsbsm1as
        foreign key (interest_group_seq) references interest_group (seq)
)
    collate=utf8mb4_unicode_ci;

create table club_interest
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    priority bigint not null,
    club_seq bigint null,
    interest_seq bigint null,
    constraint FKc1wkvxn6t5pfb2fv9mn91s6un
        foreign key (club_seq) references club (seq),
    constraint FKpibb4vu42tncr81neqhfidtss
        foreign key (interest_seq) references interest (seq)
)
    collate=utf8mb4_unicode_ci;

create table region
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    level bigint not null,
    name varchar(255) null,
    super_region_root varchar(255) null,
    super_region_seq bigint null
)
    collate=utf8mb4_unicode_ci;

create table club_region
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    priority bigint not null,
    club_seq bigint null,
    region_seq bigint null,
    constraint FK6ndg1ekavq86qj6y34sbwh5ys
        foreign key (region_seq) references region (seq),
    constraint FKb3bf99jys98t6ii8juav9kid4
        foreign key (club_seq) references club (seq)
)
    collate=utf8mb4_unicode_ci;

create index FKg4jeiuwjkv7phuycq163lnba4
	on club_region (region_seq);

create index FKh608kxmggam08l91vexbyhim
	on club_region (club_seq);

create table role_group
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    name varchar(255) null,
    role_type varchar(255) null
)
    collate=utf8mb4_unicode_ci;

create table role
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    name varchar(255) null,
    role_group_seq bigint null,
    level int not null,
    constraint FKtptev34twpe5lbnkr1plcx3kx
        foreign key (role_group_seq) references role_group (seq)
)
    collate=utf8mb4_unicode_ci;

create table user
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    access_token varchar(255) null,
    refresh_token varchar(255) null,
    user_id varchar(255) null,
    user_type varchar(255) null,
    birthday date null,
    profile_image_link varchar(255) null,
    user_name varchar(255) null,
    is_registered bit not null
)
    collate=utf8mb4_unicode_ci;

create table club_user
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    club_seq bigint null,
    user_seq bigint null,
    is_liked tinyint(1) default 0 null,
    constraint FK62ekt7c05ms3q5j80ouk1b4ot
        foreign key (club_seq) references club (seq),
    constraint FKogm3lyntcwe641wcd3lb2m9pt
        foreign key (user_seq) references user (seq)
)
    collate=utf8mb4_unicode_ci;

create table club_album
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    title varchar(100) null,
    file_name varchar(200) null,
    delete_flag bit null,
    img_url varchar(400) null,
    club_seq bigint null,
    club_user_seq bigint null,
    constraint FKef2hv0dli9ku7wdui5luxekrq
        foreign key (club_seq) references club (seq),
    constraint FKq0ssx32vp8htq3kngistkpxi0
        foreign key (club_user_seq) references club_user (seq)
);

create table club_album_comment
(
    seq bigint auto_increment
        primary key,
    parent_comment_seq bigint null,
    updated_at datetime null,
    club_user_seq bigint null,
    content varchar(400) null,
    created_at datetime null,
    club_album_seq bigint null,
    depth bigint null,
    constraint FK3uebctrsu98a5nptfiyk8gho4
        foreign key (club_album_seq) references club_album (seq),
    constraint FKjtn6sar92g2n836r0erpe8dnr
        foreign key (club_user_seq) references club_user (seq),
    constraint FKpbhpgmk6o38qv9i4iha1ldgsi
        foreign key (parent_comment_seq) references club_album_comment (seq)
);

create table club_album_like
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_At datetime null,
    club_album_seq bigint null,
    club_user_seq bigint null,
    constraint FK4s523umaiic8ecnexigps9xpk
        foreign key (club_user_seq) references club_user (seq),
    constraint FKhpo3h1ufutngf8vwvlsxxt64o
        foreign key (club_album_seq) references club_album (seq)
);

create table club_board
(
    seq bigint auto_increment
        primary key,
    club_seq bigint null,
    updated_at datetime null,
    content varchar(255) null,
    delete_flag bit not null,
    notification_flag bit not null,
    title varchar(255) null,
    top_fixed_flag bit not null,
    club_user_seq bigint null,
    title_img_seq bigint null,
    created_at datetime null,
    category int null,
    constraint FKhjgbcrv2i19rtlf77ydmh2hdd
        foreign key (club_user_seq) references club_user (seq),
    constraint FKmqpw1u0yrl7sura39i7c51fsv
        foreign key (club_seq) references club (seq)
)
    collate=utf8mb4_unicode_ci;

create table board_comment
(
    seq bigint auto_increment
        primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    content varchar(255) null,
    delete_flag bit not null,
    depth bigint not null,
    club_board_seq bigint null,
    club_user_seq bigint null,
    parent_comment_seq bigint null,
    constraint FK97sbraq8i5of5ww49rgkyieyd
        foreign key (club_board_seq) references club_board (seq),
    constraint FKec2gq9fb858uvrd2t8i62nao7
        foreign key (parent_comment_seq) references board_comment (seq),
    constraint FKr5289may1w0hsacsbr3me6pis
        foreign key (club_user_seq) references club_user (seq)
);

create index FK2s30dsmnxi4scxwmai0wwbepf
	on club_board (title_img_seq);

create table club_board_comment
(
    seq bigint auto_increment
        primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    content varchar(255) null,
    delete_flag bit not null,
    depth bigint not null,
    club_board_seq bigint null,
    club_user_seq bigint null,
    parent_comment_seq bigint null,
    constraint FKbcj4qn1c83supq54dxbdd6tj9
        foreign key (club_user_seq) references club_user (seq),
    constraint FKkspr6a9verlqi2992tm2yfgjw
        foreign key (club_board_seq) references club_board (seq),
    constraint FKo8912766p9cx76wx9uhbkw7fl
        foreign key (parent_comment_seq) references club_board_comment (seq)
);

create table club_board_img
(
    seq bigint auto_increment
        primary key,
    img_name varchar(50) null,
    img_url varchar(400) null,
    club_board_seq bigint not null,
    updated_at datetime null,
    delete_flag bit not null,
    created_at datetime null,
    constraint FKdih47mhn3tw2r6qrw26aa2xgg
        foreign key (club_board_seq) references club_board (seq)
)
    collate=utf8mb4_unicode_ci;

create table club_board_like
(
    seq bigint auto_increment
        primary key,
    created_at datetime(6) null,
    updated_at datetime(6) null,
    club_board_seq bigint null,
    club_user_seq bigint null,
    constraint FKcxek9ktisuvs49wcj0t6rehb1
        foreign key (club_board_seq) references club_board (seq),
    constraint FKsiurgq3qpo6fbtjmu2tt2op26
        foreign key (club_user_seq) references club_user (seq)
);

create table club_user_role
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    club_user_seq bigint null,
    role_seq bigint null,
    constraint FK16v81lw0qtbwmgowrvv9kj4f1
        foreign key (role_seq) references role (seq),
    constraint FK77aw8u5rqnqkfulcg7dmx8k18
        foreign key (club_user_seq) references club_user (seq)
)
    collate=utf8mb4_unicode_ci;

create table meeting
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    content varchar(255) null,
    delete_flag bit not null,
    end_timestamp datetime null,
    start_timestamp datetime null,
    title varchar(255) null,
    club_seq bigint null,
    maximum_number bigint null,
    reg_club_user_seq bigint not null,
    region varchar(1000) null,
    regionURL varchar(1000) null,
    cost int null,
    constraint FK2p2i0pqotdrunkgkl7659d5eo
        foreign key (club_seq) references club (seq),
    constraint FKcyexdmtpr4eo3y0s90p8tkpv6
        foreign key (reg_club_user_seq) references club_user (seq)
)
    collate=utf8mb4_unicode_ci;

create table meeting_application
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    delete_flag bit not null,
    club_user_seq bigint null,
    meeting_seq bigint null,
    constraint FKhwprcrvbgy23n70k3b0aosubw
        foreign key (club_user_seq) references club_user (seq),
    constraint FKr5unea7i15x7rw67f8httpt2d
        foreign key (meeting_seq) references meeting (seq)
)
    collate=utf8mb4_unicode_ci;

create table user_interest
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    priority bigint not null,
    interest_seq bigint null,
    user_seq bigint null,
    constraint FK8bkdvsxu32diislo8ebrskqek
        foreign key (interest_seq) references interest (seq),
    constraint FK9ry12q7d032bpt5dpq7jgtkoh
        foreign key (user_seq) references user (seq)
)
    collate=utf8mb4_unicode_ci;

create index FK2ouj0bgl46rx54qjkm9950rka
	on user_interest (interest_seq);

create table user_region
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    priority bigint not null,
    region_seq bigint null,
    user_seq bigint null,
    constraint FKt14k0fdc7622mujq7jitu3w4x
        foreign key (region_seq) references region (seq),
    constraint FKtrn222rvy23garm4l4i409jda
        foreign key (user_seq) references user (seq)
)
    collate=utf8mb4_unicode_ci;

create index FKloot64u32vp2nink5mce5l69
	on user_region (region_seq);

create index FKslxw1alk86h74pd0rcspfuy6v
	on user_region (user_seq);

create table user_role
(
    seq bigint auto_increment
        primary key,
    created_at datetime null,
    updated_at datetime null,
    role_name varchar(255) null,
    user_seq bigint null
)
    collate=utf8mb4_unicode_ci;

create index FKiy9g0hyj33rambfmatd6e1h2n
	on user_role (user_seq);

