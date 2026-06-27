create table inventario(
    id         bigint primary key auto_increment,
    product_id bigint not null,
    quantity   int not null,

    constraint chk_quantity_not_negative check (quantity >= 0)
);


insert into inventario(id, product_id, quantity) values
(1, 1, 45),
(2, 2, 30);
