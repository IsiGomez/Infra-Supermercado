create table puntos(
    id                bigint primary key auto_increment,
    usuario_id        bigint not null    unique,
    puntos_acumulados int    not null    default 0,

    constraint chk_puntos_not_negative check (puntos_acumulados >= 0)
);

create table puntos_historial(
    id               bigint      primary key auto_increment,
    usuario_id       bigint      not null,
    compra_id        bigint      null,
    puntos_otorgados int         not null,
    tipo             varchar(20) not null    default 'ACUMULACION',

    constraint un_compra_id_acumulacion unique (compra_id, tipo)
);