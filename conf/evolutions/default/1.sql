# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table book_model (
  id                        integer auto_increment not null,
  name                      varchar(255),
  owner_id                  varchar(255),
  holder_id                 varchar(255),
  constraint pk_book_model primary key (id))
;

create table user_model (
  email                     varchar(255) not null,
  password                  varchar(255),
  constraint pk_user_model primary key (email))
;

alter table book_model add constraint fk_book_model_owner_1 foreign key (owner_id) references user_model (email) on delete restrict on update restrict;
create index ix_book_model_owner_1 on book_model (owner_id);
alter table book_model add constraint fk_book_model_holder_2 foreign key (holder_id) references user_model (email) on delete restrict on update restrict;
create index ix_book_model_holder_2 on book_model (holder_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table book_model;

drop table user_model;

SET FOREIGN_KEY_CHECKS=1;

