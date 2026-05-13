create schema demo;

CREATE TABLE IF NOT EXISTS `demo`.`users`
(
    `id`       INT         NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(45) NULL,
    `password` VARCHAR(60) NULL,
    `enabled`  INT         NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `demo`.`authorities`
(
    `id`        INT         NOT NULL AUTO_INCREMENT,
    `username`  VARCHAR(45) NULL,
    `authority` VARCHAR(45) NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `demo`.`token`
(
    `id`         INT         NOT NULL AUTO_INCREMENT,
    `identifier` VARCHAR(45) NULL,
    `token`      TEXT        NULL,
    PRIMARY KEY (`id`)
);