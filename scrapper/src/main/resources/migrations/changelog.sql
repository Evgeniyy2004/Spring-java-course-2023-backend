--liquibase formatted sql
--changeset postgres:3

CREATE TABLE if not exists Groups (
    Id SERIAL PRIMARY KEY,
    GroupName VARCHAR(255) NOT NULL
);

CREATE TABLE if not exists Student (
    Id BIGSERIAL PRIMARY KEY,
    GroupId INTEGER REFERENCES Groups(Id),
    IsNotified BOOLEAN DEFAULT FALSE,
    MailingTime INTEGER
);

CREATE TABLE if not exists Classes (
    Id SERIAL PRIMARY KEY,
    GroupId BIGINT REFERENCES Groups(Id),
    Discipline VARCHAR(255) NOT NULL,
    ClassDate DATE NOT NULL,
    ClassTime VARCHAR(255) NOT NULL,
    Classroom VARCHAR(255) NOT NULL
);





