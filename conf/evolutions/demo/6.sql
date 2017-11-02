# --- !Ups

INSERT INTO PRICES
   (FUND_ID, PRICE_DATE, PRICE)
VALUES
   (1, '2016-02-01', 1.289),
   (2, '2016-02-01', 7.042),
   (1, '2016-02-20', 1.288),
   (2, '2016-02-20', 7.028),
   (1, '2016-03-01', 1.275),
   (2, '2016-03-01', 6.683),
   (1, '2016-03-20', 1.288),
   (2, '2016-03-20', 6.678),
   (1, '2016-04-01', 1.296),
   (2, '2016-04-01', 6.825),
   (1, '2016-04-20', 1.302),
   (2, '2016-04-20', 6.992),
   (1, '2016-05-01', 1.289),
   (2, '2016-05-01', 7.173),
   (1, '2016-05-20', 1.261),
   (2, '2016-05-20', 7.509),
   (1, '2016-06-01', 1.266),
   (2, '2016-06-01', 7.502),
   (1, '2016-06-20', 1.261),
   (2, '2016-06-20', 7.509),
   (1, '2016-07-01', 1.256),
   (2, '2016-07-01', 7.523),
   (1, '2016-07-20', 1.254),
   (2, '2016-07-20', 7.612),
   (1, '2016-08-01', 1.25),
   (2, '2016-08-01', 7.535),
   (1, '2016-08-20', 1.252),
   (2, '2016-08-20', 7.701),
   (1, '2016-09-01', 1.242),
   (2, '2016-09-01', 7.911),
   (1, '2016-09-20', 1.243),
   (2, '2016-09-20', 7.812),
   (1, '2016-10-01', 1.244),
   (2, '2016-10-01', 7.538),
   (1, '2016-10-20', 1.24),
   (2, '2016-10-20', 7.598),
   (1, '2016-11-01', 1.234),
   (2, '2016-11-01', 7.523),
   (1, '2016-11-20', 1.212),
   (2, '2016-11-20', 7.525),
   (1, '2016-12-01', 1.234),
   (2, '2016-12-01', 7.521),
   (1, '2016-12-10', 1.28),
   (1, '2016-12-20', 1.291),
   (2, '2016-12-20', 7.5),
   (1, '2017-01-01', 1.300),
   (2, '2017-01-01', 7.485),
   (3, '2017-01-01', 0.31),
   (1, '2017-01-15', 1.35),
   (1, '2017-01-20', 1.348),
   (2, '2017-01-20', 7.534),
   (1, '2017-02-01', 1.337),
   (2, '2017-02-01', 7.56),
   (3, '2017-02-01', 0.361),
   (1, '2017-02-10', 1.341);

# --- !Downs

DELETE FROM PRICES;