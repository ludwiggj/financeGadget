# --- !Ups

CREATE TABLE TRANSACTIONS (
  ID BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  FUND_ID BIGINT NOT NULL,
  USER_ID BIGINT NOT NULL,
  TRANSACTION_DATE DATE NOT NULL,
  DESCRIPTION VARCHAR(254) NOT NULL,
  AMOUNT_IN DECIMAL(8, 4) DEFAULT NULL,
  AMOUNT_OUT DECIMAL(8, 4) DEFAULT NULL,
  PRICE_DATE DATE NOT NULL,
  UNITS DECIMAL(10, 4) NOT NULL,
  CONSTRAINT TRANSACTIONS_FUNDS_FK FOREIGN KEY (FUND_ID) REFERENCES FUNDS (ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT TRANSACTIONS_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS (ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT TRANSACTIONS_PRICES_FK FOREIGN KEY (FUND_ID, PRICE_DATE) REFERENCES PRICES (FUND_ID, PRICE_DATE)  ON DELETE NO ACTION ON UPDATE NO ACTION
);

# --- !Downs

DROP TABLE IF EXISTS TRANSACTIONS;