mysql -uroot -p
show databases;
create database reggie;
use reggie;


# 或者可以直接source导入sql里面的内容
mysql -uroot -p
show databases;
create database reggie character set utf8mb4;
use reggie;

source db_reggie.sql;