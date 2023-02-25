

create database sample;
create user sample_user;
grant all privileges on database sample to sample_user ;

alter user sample_user with password 'stew';

create table customers (id int primary key, name varchar(50));
create table products (id int primary key, name varchar(50), price decimal);

create table orders (
       id int primary key,
       customer_id int,
       created_date date not null,
       foreign key (customer_id) references customers(id)
);

create table ordered_items (
       order_id int not null,
       product_id int not null,
       foreign key (order_id) references orders(id),
       foreign key (product_id) references products(id)
);



