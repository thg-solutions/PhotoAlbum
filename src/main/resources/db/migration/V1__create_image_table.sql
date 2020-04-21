create table image (
    creation_date varchar(32) primary key,
    filename varchar(32),
    latitude double precision,
    longitude double precision,
    last_modified timestamp without time zone,
    version bigint not null
);
