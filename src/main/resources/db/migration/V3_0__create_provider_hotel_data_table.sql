create table if not exists provider_hotel_data
(
    id                   serial primary key,
    campaign_id          int         not null,
    provider_id          int         not null,
    city_name            varchar(50) not null,
    hotel_id             int         not null,
    hotel_name           varchar(50),
    hotel_room_id        int         not null,
    hotel_room_name      varchar(50),
    hotel_room_rate_id   int,
    hotel_room_rate_name varchar(50),
    max_guests           int,
    price                numeric(10, 2),
    food                 varchar(50),
    date_in              timestamp,
    date_out             timestamp,
    date_create        timestamp default now(),

    CONSTRAINT fk_promo_campaigns_provider_hotel_data FOREIGN KEY (campaign_id) REFERENCES promo_campaigns(id) ON DELETE CASCADE ON UPDATE CASCADE

);