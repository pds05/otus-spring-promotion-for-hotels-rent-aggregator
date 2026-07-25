insert into promo_campaigns (id, title, campaign_type, start_date, create_date, update_date, status, result)
VALUES (100,  'Кампания по низкой стоимости с питанием', 'LOW_COST_WITH_FOOD', '2027-01-01', CURRENT_DATE, null, 'CREATED', null),
       (101,  'Кампания по низкой стоимости', 'LOW_COST', '2027-01-01', CURRENT_DATE, null, 'CREATED', null);;

insert into campaign_hotel_parameters(id, campaign_id, city_id, date_in, date_out, guests)
VALUES (100, 100, 1, '2027-01-02', '2027-01-03', '2'),
       (101, 100, 3, '2027-01-02', '2027-01-03', '2'),
       (102, 101, 1, '2027-01-02', '2027-01-03', '2'),
       (103, 101, 3, '2027-01-02', '2027-01-03', '2');

insert into campaign_hotel_parameters_ct_hotel_type_rel (campaign_hotel_parameter_id, ct_hotel_type_id)
VALUES (100, 1), (100, 3),
       (101, 1), (101,3),
       (102, 1), (102, 3),
       (103, 1), (103,3);;

insert into campaign_hotel_parameters_ct_food_type_rel (campaign_hotel_parameter_id, ct_food_type_id)
VALUES (100, 1), (101, 2),
       (102, 3), (102, 4),
       (102, 5),(103, 5);

insert into campaign_providers (campaign_id, provider_id)
VALUES (100, 1), (100, 2),
       (101, 1), (101, 2)

