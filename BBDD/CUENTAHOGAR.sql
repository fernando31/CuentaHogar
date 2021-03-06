CREATE DATABASE CUENTAHOGAR;
USE CUENTAHOGAR;

DROP TABLE IF EXISTS CUENTAS;
DROP TABLE IF EXISTS MOVIMIENTOS;

CREATE TABLE CUENTAS
(
ID_CUENTA	INTEGER AUTO_INCREMENT,
NOMBRE		VARCHAR(50) NOT NULL UNIQUE,
TIPO        ENUM('C','G','I'),
NUMERO		VARCHAR(23) UNIQUE,
CAPITAL     FLOAT,
CONSTRAINT PK_CUENTAS PRIMARY KEY (ID_CUENTA)
);

CREATE TABLE MOVIMIENTOS
(
ID_MOVIMIENTO	INTEGER AUTO_INCREMENT,
ID_CUENTA_G	    INTEGER,
ID_CUENTA_I     INTEGER,
TIPO            ENUM('G','I','T'),
FECHA		    DATE NOT NULL,
IMPORTE		    FLOAT NOT NULL,
CONSTRAINT PK_ID_MOVIMIENTO PRIMARY KEY (ID_MOVIMIENTO),
CONSTRAINT FK_MOV_ID_CUENTA_G FOREIGN KEY (ID_CUENTA_G) REFERENCES CUENTAS (ID_CUENTA)
    ON DELETE CASCADE,
CONSTRAINT FK_MOV_ID_CUENTA_I FOREIGN KEY (ID_CUENTA_I) REFERENCES CUENTAS (ID_CUENTA)
    ON DELETE CASCADE
);
