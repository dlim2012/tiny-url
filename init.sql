CREATE DATABASE IF NOT EXISTS tiny_url;
USE tiny_url;

create table app_user (
   id integer not null,
    app_user_role varchar(255),
    available_short_url integer,
    email varchar(100) not null,
    firstname varchar(50),
    lastname varchar(50),
    password varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table app_user_seq (
   next_val bigint
) engine=InnoDB;

insert into app_user_seq values ( 1 );

create table short_url_path (
   short_url_path varchar(100) not null,
    created_at datetime(6) not null,
    expire_date date not null,
    is_active bit,
    is_private bit,
    user_id integer,
    primary key (short_url_path)
) engine=InnoDB;

alter table short_url_path
   add constraint FKr66ldsf7g1fwadrw7atououia
   foreign key (user_id)
   references app_user (id);

CREATE EVENT if not exists clean_app_user_urls ON schedule every 1 DAY ENABLE
    DO DELETE FROM short_url_path
    WHERE expire_date < CURDATE();

create table token
    (
        seed integer not null,
        created_at datetime(6) not null,
        expire_date date not null,
         primary key (seed)
    )
engine=InnoDB;

CREATE EVENT if not exists clean_token ON schedule every 1 DAY ENABLE
    DO DELETE FROM token
    WHERE expire_date < CURDATE();