create table if not exists promo_campaigns
(
    id                 serial primary key,
    title              varchar(100) not null,
    campaign_type      varchar(50)  not null,
    start_date         timestamp    not null,
    create_date        timestamp default CURRENT_DATE,
    update_date        timestamp default CURRENT_DATE,
    message_group_name varchar(50),
    status             varchar(50),
    result             varchar(50),
    details            varchar(255)
);