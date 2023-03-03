CREATE DATABASE IF NOT EXISTS tiny_url;
USE tiny_url;

create table if not exists app_user (
   id integer not null,
    app_user_created_at datetime(6) not null,
    app_user_role varchar(255),
    available_short_url integer,
    email varchar(100) not null,
    firstname varchar(50),
    lastname varchar(50),
    password varchar(255) not null,
    primary key (id)
) engine=InnoDB;

create table if not exists app_user_seq (
   next_val bigint
) engine=InnoDB;

create table if not exists url (
   url_id integer not null,
    url_created_at datetime(6) not null,
    url_expires_at datetime(6) not null,
    is_active bit,
    is_private bit,
    long_url varchar(2047) not null,
    short_url_path varchar(30) not null,
    text varchar(255),
    user_id integer not null,
    primary key (url_id)
) engine=InnoDB;

create table if not exists url_seq (
       next_val bigint
    ) engine=InnoDB;

alter table app_user
       add constraint UK_1j9d9a06i600gd43uu3km82jw unique (email);

CREATE EVENT if not exists clean_app_user_urls ON schedule every 1 DAY ENABLE
    DO DELETE FROM url
    WHERE url_expires_at < NOW();

create table if not exists token
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