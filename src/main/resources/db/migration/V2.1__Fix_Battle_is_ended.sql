alter table battle
    drop column is_ended;
alter table battle
    add column status varchar(255);
