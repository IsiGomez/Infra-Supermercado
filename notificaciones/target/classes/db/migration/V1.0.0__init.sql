create table notificacion(
    id          bigint primary key auto_increment,
    usuario_id  bigint       not null,
    mensaje     varchar(500) not null,
    fecha_envio datetime     not null,
    enviado     boolean      not null default false,
    leido       boolean      not null default false
);
