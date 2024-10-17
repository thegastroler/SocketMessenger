drop schema if exists day_09 cascade;
create schema if not exists day_09;

create table if not exists day_09.user
(
    id    serial,
    email varchar(255) not null,
    password varchar(255)
);

create table if not exists day_09.room
(
    id serial,
    name varchar(255) not null
);

create table if not exists day_09.message
(
    id serial,
    roomId integer not null,
    authorId integer not null,
    text varchar(255) default ''
);

insert into day_09.room (name) values
    ('First room'),
    ('Bathroom'),
    ('Java room');

alter table day_09.user
add unique (email);