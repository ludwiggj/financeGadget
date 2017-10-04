# --- !Ups

CREATE TABLE PRICES (
  ID BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  FUND_ID BIGINT NOT NULL,
  PRICE_DATE DATE NOT NULL,
  PRICE DECIMAL(8, 4) NOT NULL,
  UNIQUE(FUND_ID, PRICE_DATE),
  CONSTRAINT PRICES_FUNDS_FK FOREIGN KEY (FUND_ID) REFERENCES FUNDS (ID) ON DELETE NO ACTION ON UPDATE NO ACTION
) AUTO_INCREMENT=0;

# --- !Downs

DROP TABLE IF EXISTS PRICES;