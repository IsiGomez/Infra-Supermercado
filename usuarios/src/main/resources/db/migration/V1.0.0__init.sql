create table rol(
    id          bigint      primary key auto_increment,
    name        varchar(50) not null    unique,
    description varchar(255)
);

create table person(
    id        bigint       primary key auto_increment,
    rut       varchar(20)  not null    unique,
    name      varchar(100) not null,
    last_name varchar(100) not null,
    email     varchar(150) not null    unique,
    phone     varchar(20)
);

create table login(
    id        bigint       primary key auto_increment,
    username  varchar(100) not null    unique,
    password  varchar(120) not null,
    person_id bigint       not null    unique,
    rol_id    bigint       not null
);

alter table login
    add constraint fk_login_person
        foreign key (person_id) references person(id);

alter table login
    add constraint fk_login_rol
        foreign key (rol_id) references rol(id);


insert into rol(id, name, description) values
(1, 'FUNCIONARIO', 'Controla el sistema'),
(2, 'CLIENTE', 'Ocupa el sistema');

insert into person(id, rut, name, last_name, email, phone) values
(1, '12342885-3', 'Claudia', 'Gonzales', 'clau.gon@funcionarios.com', '+56923542352'),
(2, '99853188-4', 'Juan', 'Perez', 'ju.perez@client.com', '+56913541254');

insert into login(id, username, password, person_id, rol_id) values
(1, 'FunClaudia', '$2a$10$OwY4KE5bo27XnvUCQhGmoOcouhmNRUrDJUfA7avjqivYVkCxX9evC', 1, 1),
(2, 'Juanito', '$2a$10$GYebobepgy2KOrfBQVMIEODRmgHK4RJ0bwVFflPK7UmQeqCwmlESe', 2, 2);