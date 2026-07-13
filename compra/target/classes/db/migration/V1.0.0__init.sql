create table compra(
    id              bigint   primary key auto_increment,
    usuario_id      bigint   not null,
    total           double   not null    default 0,
    fecha_compra    datetime not null,
    finalizada      boolean  not null    default false,
    pago_confirmado boolean  not null    default false,

    constraint chk_compra_total_not_negative check (total >= 0)
);
