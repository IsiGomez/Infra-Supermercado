create table category(
    id   bigint       primary key auto_increment,
    name varchar(100) not null    unique
);

create table product(
    id          bigint       primary key auto_increment,
    name        varchar(100) not null    unique,
    description varchar(280),
    price       int          not null,
    category_id bigint       not null,

    constraint chk_product_valid_price
        check (price > 0 and price <= 1000000)
);

alter table product
    add constraint fk_product_category
        foreign key (category_id) references category(id);



insert into category(name) values
('Electródomesticos'),
('Hogar');

insert into product (category_id, name, description, price) values
(1, 'Hervidor', 'Calienta los liquidos', 50000),
(2, 'Silla de madera', 'Mueble para sentarse, hecho de un material de madera', 3000);