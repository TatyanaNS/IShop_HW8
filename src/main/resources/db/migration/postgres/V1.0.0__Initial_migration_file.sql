-- create data base
-- application roles dictionary
create table roles (
	id        uuid DEFAULT gen_random_uuid() PRIMARY KEY,
	role_name varchar(50) not null
);

-- application users dictionary
create table users (
	id         uuid DEFAULT gen_random_uuid() PRIMARY KEY,
	email      varchar(100) not null,
	password   varchar(100) not null,
	last_name  varchar(100) not null,
	first_name varchar(100) not null
);

-- user_role
create table user_role(
	user_id uuid not null,
	role_id uuid not null,
	CONSTRAINT fk_users_user_id FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
	CONSTRAINT fk_roles_role_id FOREIGN KEY(role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- application users dictionary
create table manufacturers (
	id        uuid DEFAULT gen_random_uuid() PRIMARY KEY,
	manufacturer_name varchar(250) not null
);

-- application products dictionary
create table products (
	id              uuid DEFAULT gen_random_uuid() PRIMARY KEY,
	product_name    varchar(250) not null,
	price		    decimal not null,
	manufacturer_id UUID not null,
	CONSTRAINT fk_manufacturer_id FOREIGN KEY(manufacturer_id) REFERENCES manufacturers(id) ON DELETE CASCADE
);