/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2008-6-3 20:46:59                            */
/*==============================================================*/


drop table if exists user;

drop table if exists user_address;

/*==============================================================*/
/* Table: user                                                  */
/*==============================================================*/
create table user
(
   user_id              int not null auto_increment,
   full_name            varchar(30) not null,
   encrypted_password   varchar(41) not null,
   creation_date        datetime not null,
   last_modified_date   datetime not null,
   primary key (user_id)
);

/*==============================================================*/
/* Table: user_address                                          */
/*==============================================================*/
create table user_address
(
   user_id              int not null,
   type                 char(1) not null,
   address              varchar(100) not null,
   primary key (user_id, type)
);

