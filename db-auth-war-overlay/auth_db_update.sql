alter table user add column credentialId binary(16) DEFAULT NULL;

create table credential(uuid binary(16) NOT NULL, password mediumtext collate  utf8_bin, created_on date);

