insert into ct_cities (id, title)
values (1, 'Москва'),
       (2, 'Санкт-Петербург'),
       (3, 'Казань'),
       (4, 'Краснодар'),
       (5, 'Калининград'),
       (6,  'Сочи'),
       (7,  'Ярославль'),
       (8,  'Ростов-на-Дону'),
       (9,  'Волгоград'),
       (10,  'Новосибирск');

insert into ct_hotel_types (id, name, description)
values (1, 'hotel', 'Отель'),
       (2, 'hostel', 'Хостел'),
       (3, 'apartments', 'Апартаменты, квартира'),
       (4, 'guest_house', 'Гостевой дом'),
       (5, 'cottage', 'Коттедж, вилла, бунгало'),
       (6, 'sanatorium', 'Санаторий'),
       (7, 'camping', 'Кемпинг');

insert into ct_food_types (id, name, description)
values (1,'all_inclusive', 'Все включено'),
       (2, 'breakfast', 'Завтрак включен'),
       (3, 'half_board', 'Завтрак, обед или ужин включен'),
       (4, 'full_board', 'Завтрак, обед и ужин включены'),
       (5, 'none', 'Без питания');