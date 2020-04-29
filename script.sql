create user prooveuser with encrypted password 'prooveuser';
grant all privileges on database proovit to prooveuser;

create table news.users
(
	user_uuid uuid
		constraint users_pk
			primary key,
	name varchar(50) not null,
	password VARCHAR(50) not null,
	points int not null,
	rank VARCHAR(50) not null
);

create table news.reports
(
	report_uuid uuid not null
		constraint reports_pk
			primary key,
	user_uuid uuid not null
		constraint reports_users_user_uuid_fk
			references news.users,
	article_link varchar(255) not null,
	report_score int not null,
	report_date timestamp not null,
	domain_uuid uuid,
	status VARCHAR(255)
);

create table news.domains
(
	domain_uuid uuid not null
		constraint domains_pk
			primary key,
	domain_link VARCHAR(255) not null,
	domain_report_score int,
	status VARCHAR(255)
);

alter table news.reports
	add constraint reports_domains_domain_uuid_fk
		foreign key (domain_uuid) references news.domains;


create table news.user_reports
(
	user_uuid uuid not null
		constraint user_reports_users_user_uuid_fk
			references news.users,
	report_uuid uuid not null
		constraint user_reports_reports_report_uuid_fk
			references news.reports
);

create unique index user_reports_report_uuid_uindex
	on news.user_reports (report_uuid);

create unique index user_reports_user_uuid_uindex
	on news.user_reports (user_uuid);

alter table news.user_reports
	add constraint user_reports_pk
		primary key (report_uuid);

drop index news.user_reports_report_uuid_uindex;

drop index news.user_reports_user_uuid_uindex;

drop index news.user_reports_pk;

alter table news.user_reports drop constraint user_reports_pk;



