CREATE USER promo_admin PASSWORD 'promo_pass';
CREATE DATABASE promotion_db OWNER promo_admin ENCODING 'UTF8' LC_COLLATE ='ru_RU.utf8' LC_CTYPE ='ru_RU.utf8' TEMPLATE ='template0' CONNECTION LIMIT 10;
GRANT ALL PRIVILEGES ON DATABASE promotion_db TO promo_admin;