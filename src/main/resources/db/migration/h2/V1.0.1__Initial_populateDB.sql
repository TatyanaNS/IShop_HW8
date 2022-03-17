--======================================================================================================--
insert into roles(role_name)
values('admin');
insert into roles(role_name)
values('user');
--======================================================================================================--
insert into users(email, password, last_name, first_name)
values('admin@gmail.com', '{bcrypt}$2y$10$UtN7sbWNUTZrWOhCnnKk8uXovAAFOEfUK77R6Smlz89T0/0kfY7au', 'Admin', 'Admin');
--======================================================================================================--