insert into ct_cities (title)
values ('Москва'),
       ('Санкт-Петербург'),
       ('Казань'),
       ('Краснодар'),
       ('Калининград'),
       ('Сочи'),
       ('Ярославль'),
       ('Ростов-на-Дону'),
       ('Волгоград'),
       ('Новосибирск');

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