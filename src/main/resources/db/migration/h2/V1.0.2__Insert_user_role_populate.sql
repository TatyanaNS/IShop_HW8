insert into user_role(user_id, role_id)
values((select id from users where email = 'admin@gmail.com'), (select id from roles where role_name = 'admin'));