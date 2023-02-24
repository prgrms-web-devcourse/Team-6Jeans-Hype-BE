create table battle
(
    id                     bigint not null auto_increment,
    created_at             TIMESTAMP,
    updated_at             TIMESTAMP,
    challenged_vote_count  integer,
    challenging_vote_count integer,
    challenged_post_id     bigint,
    challenging_post_id    bigint,
    member_id              bigint,
    primary key (id)
) engine=InnoDB;

create table likes
(
    id         bigint not null auto_increment,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    member_id  bigint,
    post_id    bigint,
    primary key (id)
) engine=InnoDB;

create table member
(
    id                        bigint  not null auto_increment,
    created_at                TIMESTAMP,
    updated_at                TIMESTAMP,
    count_of_challenge_ticket integer not null,
    ranking                   integer not null,
    victory_count             integer not null,
    victory_point             integer not null,
    nickname                  varchar(24),
    profile_image_url         varchar(2000),
    refresh_token             varchar(255),
    social_id                 varchar(255),
    social_type               varchar(255),
    primary key (id)
) engine=InnoDB;

create table post
(
    id                 bigint  not null auto_increment,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    content            longtext,
    is_possible_battle bit     not null,
    like_count         integer not null,
    album_cover_url    varchar(255),
    genre              varchar(255),
    music_id           varchar(255),
    music_url          varchar(255),
    singer             varchar(255),
    title              varchar(255),
    member_id          bigint,
    primary key (id)
) engine=InnoDB;

create table vote
(
    id         bigint not null auto_increment,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    battle_id  bigint,
    post_id    bigint,
    member_id  bigint,
    primary key (id)
) engine=InnoDB;

alter table battle
    add constraint FK44g0dk93ew8jm94i8imquwps2
        foreign key (challenged_post_id)
            references post (id);

alter table battle
    add constraint FKrs5thx0uawi7bs9y66d2wwf4f
        foreign key (challenging_post_id)
            references post (id);

alter table battle
    add constraint FKfhohk3campcgr4i0738qr95n7
        foreign key (member_id)
            references member (id);

alter table likes
    add constraint FKa4vkf1skcfu5r6o5gfb5jf295
        foreign key (member_id)
            references member (id);

alter table likes
    add constraint FKowd6f4s7x9f3w50pvlo6x3b41
        foreign key (post_id)
            references post (id);

alter table post
    add constraint FK83s99f4kx8oiqm3ro0sasmpww
        foreign key (member_id)
            references member (id);

alter table vote
    add constraint FKm1nxvydnsl77b26t5kpbl3c1c
        foreign key (battle_id)
            references battle (id);

alter table vote
    add constraint FKl3c067ewaw5xktl5cjvniv3e9
        foreign key (post_id)
            references post (id);

alter table vote
    add constraint FKgkbgl6xp2rpgwghb7mtyuv48h
        foreign key (member_id)
            references member (id);
