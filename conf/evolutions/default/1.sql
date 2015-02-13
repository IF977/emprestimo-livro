# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table book_model (
  id                        integer not null,
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

create sequence book_model_seq;

create sequence user_model_seq;

alter table book_model add constraint fk_book_model_owner_1 foreign key (owner_id) references user_model (email) on delete restrict on update restrict;
create index ix_book_model_owner_1 on book_model (owner_id);
alter table book_model add constraint fk_book_model_holder_2 foreign key (holder_id) references user_model (email) on delete restrict on update restrict;
create index ix_book_model_holder_2 on book_model (holder_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists book_model;

drop table if exists user_model;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists book_model_seq;

drop sequence if exists user_model_seq;

