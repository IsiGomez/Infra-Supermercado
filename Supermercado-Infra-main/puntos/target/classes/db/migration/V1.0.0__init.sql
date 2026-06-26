create table puntos(
    id                bigint primary key auto_increment,
    usuario_id         bigint not null    unique,
    puntos_acumulados int    not null    default 0,

    constraint chk_puntos_not_negative check (puntos_acumulados >= 0)
);

create table puntos_historial(
    id               bigint primary key auto_increment,
    usuario_id       bigint not null,
    compra_id        bigint not null    unique ,
    puntos_otorgados int    not null
);