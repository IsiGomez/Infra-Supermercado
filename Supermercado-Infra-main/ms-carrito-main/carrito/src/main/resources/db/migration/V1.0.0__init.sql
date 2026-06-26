create table cart(
    id      bigint primary key auto_increment,
    user_id bigint not null,
    total   int    not null default 0,

    constraint chk_cart_total_not_negative check (total >= 0)
);

create table cart_item(
    id          bigint primary key auto_increment,
    cart_id     bigint not null,
    product_id  bigint not null,
    quantity    int    not null,
    subtotal    int    not null,

    constraint chk_quantity_positive check (quantity > 0),
    constraint chk_subtotal_positive check (subtotal >= 0),
    constraint un_cart_product       unique (cart_id, product_id)

);

alter table cart_item
    add constraint fk_cart_item_cart
        foreign key (cart_id) references cart(id);