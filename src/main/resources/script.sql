create table users (
                       ID bigint primary key,
                       username varchar(100) unique,
                       firstName varchar(100),
                       lastName varchar(100),
                       email varchar(225) unique ,
                       password varchar(225),
                       created timestamp default current_timestamp,
                       updated timestamp default current_timestamp,
                       status varchar(25)
)

create table roles (
                       ID bigint primary key,
                       name varchar(100) unique,
                       created timestamp default current_timestamp,
                       updated timestamp default current_timestamp
)

create table user_roles (
                            user_id bigint,
                            role_id bigint
)

ALTER TABLE user_roles
    add CONSTRAINT fk_user_roles_roles Foreign Key (role_id) references roles(id)

ALTER TABLE user_roles
    add CONSTRAINT fk_user_roles_user Foreign Key (user_id) references users(id)

