create table users (
    username varchar(50) not null unique primary key,
    password varchar(255) not null
);

create table user_authorities (
    username_id varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_user_authorities_users foreign key(username_id) references users(username) on delete cascade,
    constraint pk_username_authority primary key (username_id, authority)
);