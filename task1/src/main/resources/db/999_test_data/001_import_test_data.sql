truncate table trader;
alter sequence trader_id_seq restart with 1;

insert into trader(trader_id, taxation_type, taxation_method, taxation_amount, taxation_rate)
values (1, 'GENERAL', 'RATE', NULL, 0.1);
insert into trader(trader_id, taxation_type, taxation_method, taxation_amount, taxation_rate)
values (2, 'GENERAL', 'AMOUNT', 2, NULL);
insert into trader(trader_id, taxation_type, taxation_method, taxation_amount, taxation_rate)
values (3, 'WINNING', 'RATE', NULL, 0.1);
insert into trader(trader_id, taxation_type, taxation_method, taxation_amount, taxation_rate)
values (4, 'WINNING', 'AMOUNT', 1, NULL);
