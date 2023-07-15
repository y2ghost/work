create table users (
	username varchar_ignorecase(64) not null primary key,
	password varchar_ignorecase(64) not null,
	enabled boolean not null
);

create table authorities (
	username varchar_ignorecase(64) not null,
	authority varchar_ignorecase(32) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);

create unique index ix_auth_username on authorities (username,authority);

insert into users (username, password, enabled) values ('yy', '$2a$10$qsg6Z7SUI2RlB.Wu951RSeisqUCFJU4r2Lqf3ITFfaZvt3caLY9mC', true);
insert into users (username, password, enabled) values ('tt', '$2a$10$qsg6Z7SUI2RlB.Wu951RSeisqUCFJU4r2Lqf3ITFfaZvt3caLY9mC', true);
insert into users (username, password, enabled) values ('yt', '$2a$10$qsg6Z7SUI2RlB.Wu951RSeisqUCFJU4r2Lqf3ITFfaZvt3caLY9mC', true);

insert into authorities (username, authority) values ('yy', 'ROLE_ADMIN');
insert into authorities (username, authority) values ('tt', 'ROLE_USER');
insert into authorities (username, authority) values ('yt', 'ROLE_GUEST');

