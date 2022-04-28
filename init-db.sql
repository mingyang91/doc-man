create table device
(
  id            uuid                    not null
      primary key,
  requester     varchar(128),
  address       varchar(128),
  "modelNo"     varchar(128),
  name          varchar(128),
  "sampleNo"    varchar(128),
  "deviceNo"    varchar(128),
  vendor        varchar(128),
  place         varchar(128),
  "accordingTo" varchar(128),
  equipment     varchar(128),
  item          varchar(128),
  "createTime"  timestamp default now() not null,
  "updateTime"  timestamp default now() not null,

  unique (requester, address, name, "modelNo", "deviceNo")
);

create table test_item
(
  name                    varchar(128),
  "conditionFactor"       varchar(128),
  "conditionDefaultValue" varchar(128),
  result                  varchar(128),
  "requireAcceptance"     varchar(128),
  "requireState"          varchar(128)
);


CREATE TYPE role AS ENUM ('admin', 'editor', 'viewer');

create table "user"
(
    id       SERIAL                   not null primary key,
    username varchar                  not null,
    password varchar                  not null,
    role     role    default 'viewer' not null
);

create unique index user_username_uindex
    on "user" (username);

create table "report"
(
  id uuid not null primary key ,
  "reportNo" varchar,
  params jsonb not null,
  file varchar,
  "createTime" timestamp not null default now(),
  "updateTime" timestamp not null default now()
);

create unique index report_no_uindex on "report" ("reportNo");