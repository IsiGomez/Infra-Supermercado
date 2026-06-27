create table promocion(
    id           bigint primary key auto_increment,
    codigo       varchar(50) not null unique,
    descuento double not null,
    fecha_inicio date        not null,
    fecha_fin    date        not null,
    acumulable   boolean     not null default false,

    constraint chk_promocion_descuento_rango check (descuento > 0 and descuento <= 100),
    constraint chk_promocion_fechas_validas check (fecha_fin >= fecha_inicio)
);

insert into promocion (codigo, descuento, fecha_inicio, fecha_fin, acumulable) values
('BIENVENIDO10', 10.0, '2026-01-15', '2026-12-30', false),
('VERANO20',     20.0, '2026-02-01', '2026-08-20', true);