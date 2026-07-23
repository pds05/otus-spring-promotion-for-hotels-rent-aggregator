insert into ct_cities (phone_prefix, title)
values (7495, 'Москва'),
       (7812, 'Санкт-Петербург'),
       (7843, 'Казань'),
       (7861, 'Краснодар'),
       (7401, 'Калининград'),
       (7862, 'Сочи'),
       (7485, 'Ярославль'),
       (7863, 'Ростов-на-Дону'),
       (7844, 'Волгоград'),
       (7383, 'Новосибирск');

insert into ct_hotel_types (name, description)
values ('hotel', 'Отель'),
       ('hostel', 'Хостел'),
       ('apartments', 'Апартаменты, квартира'),
       ('guest_house', 'Гостевой дом'),
       ('cottage', 'Коттедж, вилла, бунгало'),
       ('sanatorium', 'Санаторий'),
       ('camping', 'Кемпинг');

insert into ct_food_types (name, description)
values ('all_inclusive', 'Все включено'),
       ('breakfast', 'Завтрак включен'),
       ('half_board', 'Завтрак, обед или ужин включен'),
       ('full_board', 'Завтрак, обед и ужин включены'),
       ('none', 'Без питания');