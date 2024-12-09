#!/bin/sh

# 创建creature和explorer表并加载数据

sqlite3 cryptid.db <<EOF
drop table creature;
drop table explorer;
drop table user;
create table creature (
    name text primary key,
    country text,
    area text,
    description text,
    aka text
);
create table explorer (
    name text primary key,
    country text,
    description text
);
create table user (
    name text primary key,
    hash text
);
.mode list
.import creature.psv creature
.import explorer.psv explorer
EOF
