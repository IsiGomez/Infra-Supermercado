create table pago(
    id          bigint      primary key auto_increment,
    usuario_id  bigint      not null,
    monto       double      not null,
    metodo      varchar(20) not null,
    exitoso     boolean     not null    default false,
    fecha_pago  datetime    not null,

    constraint chk_pago_monto_positivo check (monto > 0),
    constraint chk_pago_metodo_valido check (metodo in ('TARJETA', 'CREDITO', 'EFECTIVO'))
);