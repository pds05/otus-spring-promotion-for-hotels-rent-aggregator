create table if not exists campaign_hotel_parameters (
    id            serial primary key,
    campaign_id   int,
    city_name     varchar(50) not null,
    date_in       date        not null default CURRENT_DATE + 1,
    date_out      date        not null default CURRENT_DATE + 2,
    guests        int         not null default 1,
    CONSTRAINT fk_promo_campaigns_hotel_parameters FOREIGN KEY (campaign_id) references promo_campaigns (id) ON DELETE CASCADE ON UPDATE CASCADE
);

create table if not exists campaign_hotel_parameters_ct_hotel_type_rel (
    campaign_hotel_parameter_id int,
    ct_hotel_type_id int,
    CONSTRAINT fk_ct_hotel_type_id FOREIGN KEY (ct_hotel_type_id) references ct_hotel_types (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_hotel_type_rel_campaign_hotel_parameter_id FOREIGN KEY (campaign_hotel_parameter_id) references campaign_hotel_parameters (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_campaign_hotel_parameter_id_ct_hotel_type_id primary key (campaign_hotel_parameter_id, ct_hotel_type_id)
);

create table if not exists campaign_hotel_parameters_ct_food_type_rel
(
    campaign_hotel_parameter_id int,
    ct_food_type_id             int,
    CONSTRAINT fk_ct_food_type_id_id FOREIGN KEY (ct_food_type_id) references ct_food_types (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_food_type_rel_campaign_hotel_parameter_id FOREIGN KEY (campaign_hotel_parameter_id) references campaign_hotel_parameters (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT pk_campaign_hotel_parameter_id_ct_food_type_id primary key (campaign_hotel_parameter_id, ct_food_type_id)
)


