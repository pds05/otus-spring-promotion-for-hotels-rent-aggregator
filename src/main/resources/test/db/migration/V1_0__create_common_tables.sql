create table if not exists ct_cities
(
    id           serial primary key,
    phone_prefix varchar(6),
    title        varchar(50) UNIQUE
);

create table if not exists ct_hotel_types
(
    id          serial primary key,
    name        varchar(50) not null UNIQUE,
    description varchar(255)
);

create table if not exists ct_food_types
(
    id          serial primary key,
    name        varchar(50) not null UNIQUE,
    description varchar(255)
);