--- create table for users
CREATE TABLE users (
	id bigint NOT NULL AUTO_INCREMENT,
	birthdate timestamp not null,
	email varchar(50) not null,
	name varchar(25) not null,
	surname varchar(50) not null,
	password varchar(60) not null,
	primary key (id)
);