create table users (id int not null,cnt int not null default 0,primary key(id))
create table user (id int not null auto_increment,username varchar(20) not null,password varchar(20) not null,cnt decimal(4,2) default 0,primary key(id));
