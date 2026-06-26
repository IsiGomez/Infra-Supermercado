create table seguimiento(
    id                  bigint      primary key auto_increment,
    compra_id           bigint      not null,
    usuario_id          bigint      not null,
    estado              varchar(20) not null,
    fecha_actualizacion datetime    not null,

    constraint chk_seguimiento_estado_valido
        check (estado in ('PENDIENTE', 'PREPARACION', 'ENVIADO', 'ENTREGADO'))
);
