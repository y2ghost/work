CREATE SCHEMA EntertainmentAgencySample;
SET search_path TO EntertainmentAgencySample;

CREATE TABLE Agents (
    AgentID serial NOT NULL ,
    AgtFirstName varchar (25) NULL ,
    AgtLastName varchar (25) NULL ,
    AgtStreetAddress varchar (50) NULL ,
    AgtCity varchar (30) NULL ,
    AgtState varchar (2) NULL ,
    AgtZipCode varchar (10) NULL ,
    AgtPhoneNumber varchar (15) NULL ,
    DateHired date NULL ,
    Salary decimal(19,4) NULL DEFAULT 0 ,
    CommissionRate float(24) NULL DEFAULT 0 
);

CREATE TABLE Customers (
    CustomerID serial NOT NULL ,
    CustFirstName varchar (25) NULL ,
    CustLastName varchar (25) NULL ,
    CustStreetAddress varchar (50) NULL ,
    CustCity varchar (30) NULL ,
    CustState varchar (2) NULL ,
    CustZipCode varchar (10) NULL ,
    CustPhoneNumber varchar (15) NULL 
);

CREATE TABLE Engagements (
    EngagementNumber serial NOT NULL ,
    StartDate date NULL ,
    EndDate date NULL ,
    StartTime time NULL ,
    StopTime time NULL ,
    ContractPrice decimal(19,4) NULL DEFAULT 0 ,
    CustomerID int NULL DEFAULT 0 ,
    AgentID int NULL DEFAULT 0 ,
    EntertainerID int NULL DEFAULT 0
);

CREATE TABLE Entertainer_Members (
    EntertainerID int NOT NULL ,
    MemberID int NOT NULL DEFAULT 0 ,
    Status smallint NULL DEFAULT 0
);

CREATE TABLE Entertainer_Styles (
    EntertainerID int NOT NULL ,
    StyleID int NOT NULL  ,
    StyleStrength smallint NOT NULL
);

CREATE TABLE Entertainers (
    EntertainerID serial NOT NULL ,
    EntStageName varchar (50) NULL ,
    EntSSN varchar (12) NULL ,
    EntStreetAddress varchar (50) NULL ,
    EntCity varchar (30) NULL ,
    EntState varchar (2) NULL ,
    EntZipCode varchar (10) NULL ,
    EntPhoneNumber varchar (15) NULL ,
    EntWebPage varchar (50) NULL ,
    EntEMailAddress varchar (50) NULL ,
    DateEntered date NULL ,
    EntPricePerDay decimal(19,4) NULL 
);

CREATE TABLE Members (
    MemberID serial NOT NULL ,
    MbrFirstName varchar (25) NULL ,
    MbrLastName varchar (25) NULL ,
    MbrPhoneNumber varchar (15) NULL ,
    Gender varchar (2) NULL 
);

CREATE TABLE Musical_Preferences (
    CustomerID int NOT NULL ,
    StyleID int NOT NULL ,
    PreferenceSeq smallint NOT NULL DEFAULT 0
);

CREATE TABLE Musical_Styles (
    StyleID serial NOT NULL ,
    StyleName varchar (75) NULL 
);

CREATE TABLE ztblDays (
        DateField date NOT NULL 
);

CREATE TABLE ztblMonths ( 
        MonthYear varchar (15) NULL ,
        YearNumber smallint NOT NULL ,
        MonthNumber smallint NOT NULL ,
        MonthStart date NULL ,
        MonthEnd date NULL ,
        January smallint NULL DEFAULT 0 ,
        February smallint NULL DEFAULT 0 ,
        March smallint NULL DEFAULT 0 ,
        April smallint NULL DEFAULT 0 ,
        May smallint NULL DEFAULT 0 ,
        June smallint NULL DEFAULT 0 ,
        July smallint NULL DEFAULT 0 ,
        August smallint NULL DEFAULT 0 ,
        September smallint NULL DEFAULT 0 ,
        October smallint NULL DEFAULT 0 ,
        November smallint NULL DEFAULT 0 ,
        December smallint NULL DEFAULT 0
);

CREATE TABLE ztblQuarters ( 
        QuarterYear varchar (15) NULL ,
        YearNumber smallint NOT NULL ,
        QuarterNumber smallint NOT NULL ,
        QuarterStart date NULL ,
        QuarterEnd date NULL ,
        Qtr_1st smallint NULL ,
        Qtr_2nd smallint NULL ,
        Qtr_3rd smallint NULL ,
        Qtr_4th smallint NULL          
);

CREATE TABLE ztblSkipLabels (
        LabelCount int NOT NULL  
);

CREATE TABLE ztblWeeks ( 
        WeekStart date NOT NULL ,
        WeekEnd date NULL 
);

 ALTER TABLE Agents 
 ADD CONSTRAINT Agents_PK PRIMARY KEY   
    (
        AgentID
    )  
;

CREATE INDEX Agents_AgtZipCode ON Agents(AgtZipCode);

ALTER TABLE Customers 
ADD CONSTRAINT Customers_PK PRIMARY KEY   
    (
        CustomerID
    )  
;

CREATE INDEX Customers_CustZipCode ON Customers(CustZipCode);

ALTER TABLE Engagements 
ADD CONSTRAINT Engagements_PK PRIMARY KEY   
    (
        EngagementNumber
    )  
;

CREATE INDEX Engagements_AgentsEngagements ON Engagements(AgentID);

CREATE INDEX Engagements_CustomerID ON Engagements(CustomerID);

CREATE INDEX Engagements_EmployeeID ON Engagements(AgentID);

CREATE INDEX Engagements_EntertainerID ON Engagements(EntertainerID);

ALTER TABLE Entertainer_Members 
ADD CONSTRAINT Entertainer_Members_PK PRIMARY KEY   
    (
        EntertainerID,
        MemberID
    )  
;

CREATE INDEX EntertainersEntertainerMembers ON Entertainer_Members(EntertainerID);

CREATE INDEX MembersEntertainerMembers ON Entertainer_Members(MemberID);

ALTER TABLE Entertainer_Styles 
ADD CONSTRAINT Entertainer_Styles_PK PRIMARY KEY   
    (
        EntertainerID,
        StyleID
    )  
;

CREATE INDEX Entertainer_Styles_EntertainersEntertainerStyles ON Entertainer_Styles(EntertainerID);

CREATE INDEX Entertainer_Styles_MusicalStylesEntStyles ON Entertainer_Styles(StyleID);

ALTER TABLE Entertainers 
ADD CONSTRAINT Entertainers_PK PRIMARY KEY   
    (
        EntertainerID
    )  
;

CREATE UNIQUE INDEX Entertainers_EntertainerID ON Entertainers(EntertainerID);

CREATE INDEX EntZipCode ON Entertainers(EntZipCode);

ALTER TABLE Members ADD 
    CONSTRAINT Members_PK PRIMARY KEY   
    (
        MemberID
    )  
;

CREATE INDEX MemberID ON Members(MemberID);

ALTER TABLE Musical_Preferences 
ADD CONSTRAINT Musical_Preferences_PK PRIMARY KEY   
    (
        CustomerID,
        StyleID
    )  
;

CREATE INDEX Musical_Preferences_CustomerID ON Musical_Preferences(CustomerID);

CREATE INDEX Musical_Preferences_StyleID ON Musical_Preferences(StyleID);

ALTER TABLE Musical_Styles 
ADD CONSTRAINT Musical_Styles_PK PRIMARY KEY   
    (
        StyleID
    )  
;

ALTER TABLE ztblDays 
ADD CONSTRAINT ztblDays_PK PRIMARY KEY 
        ( 
                DateField 
        )
;

ALTER TABLE ztblMonths 
ADD CONSTRAINT ztblMonths_PK PRIMARY KEY 
        ( 
                YearNumber, 
                MonthNumber
        )
;

CREATE UNIQUE INDEX ztblMontths_MonthEnd ON ztblMonths(MonthEnd);

CREATE UNIQUE INDEX ztblMonths_MonthStart ON ztblMonths(MonthStart); 

CREATE UNIQUE INDEX ztblMonths_MonthYear ON ztblMonths(MonthYear);

ALTER TABLE ztblQuarters 
ADD CONSTRAINT ztblQuarters_PK PRIMARY KEY 
        ( 
                YearNumber, 
                QuarterNumber
        )
;

CREATE UNIQUE INDEX ztblQuarters_QuarterEnd ON ztblQuarters(QuarterEnd);

CREATE UNIQUE INDEX ztblQuarters_QuarterStart ON ztblQuarters(QuarterStart);

CREATE UNIQUE INDEX ztblQuarters_QuarterYear ON ztblQuarters(QuarterYear); 

ALTER TABLE ztblSkipLabels 
ADD CONSTRAINT ztblSkipLabels_PK PRIMARY KEY 
        (
                LabelCount 
        ) 
;

ALTER TABLE ztblWeeks 
ADD CONSTRAINT ztblWeeks_PK PRIMARY KEY 
        ( 
                WeekStart 
        ) 
;

ALTER TABLE Engagements 
ADD CONSTRAINT Engagements_FK00 FOREIGN KEY 
    (
        AgentID
    ) REFERENCES Agents (
        AgentID
    ),
ADD CONSTRAINT Engagements_FK01 FOREIGN KEY 
    (
        CustomerID
    ) REFERENCES Customers (
        CustomerID
    ),
ADD CONSTRAINT Engagements_FK02 FOREIGN KEY 
    (
        EntertainerID
    ) REFERENCES Entertainers (
        EntertainerID
    )
;

ALTER TABLE Entertainer_Members 
ADD CONSTRAINT Entertainer_Members_FK00 FOREIGN KEY 
    (
        EntertainerID
    ) REFERENCES Entertainers (
        EntertainerID
    ),
ADD CONSTRAINT Entertainer_Members_FK01 FOREIGN KEY 
    (
        MemberID
    ) REFERENCES Members (
        MemberID
    )
;

ALTER TABLE Entertainer_Styles 
ADD CONSTRAINT Entertainer_Styles_FK00 FOREIGN KEY 
    (
        EntertainerID
    ) REFERENCES Entertainers (
        EntertainerID
    ) ON DELETE CASCADE,
ADD CONSTRAINT Entertainer_Styles_FK01 FOREIGN KEY 
    (
        StyleID
    ) REFERENCES Musical_Styles (
        StyleID
    )
;

ALTER TABLE Musical_Preferences 
ADD CONSTRAINT Musical_Preferences_FK00 FOREIGN KEY 
    (
        CustomerID
    ) REFERENCES Customers (
        CustomerID
    ) ON DELETE CASCADE,
ADD CONSTRAINT Musical_Preferences_FK01 FOREIGN KEY 
    (
        StyleID
    ) REFERENCES Musical_Styles (
        StyleID
    )
;


SELECT A.AgtFirstName, A.AgtLastName, 
    EXTRACT(MONTH FROM E.StartDate) AS ContractMonth, 
    SUM(E.ContractPrice) AS TotalContractValue
FROM Agents AS A INNER JOIN Engagements AS E
  ON A.AgentID = E.AgentID
WHERE EXTRACT(YEAR FROM E.StartDate) = 2015
GROUP BY A.AgtFirstName, A.AgtLastName, EXTRACT(MONTH FROM E.StartDate);


SELECT A.AgtFirstName, A.AgtLastName, 
    EXTRACT(YEAR FROM E.StartDate) AS ContractYear,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 1 
             THEN E.ContractPrice END) AS January,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 2 
             THEN E.ContractPrice END) AS February,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 3 
             THEN E.ContractPrice END) AS March,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 4 
             THEN E.ContractPrice END) AS April,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 5 
             THEN E.ContractPrice END) AS May,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 6 
             THEN E.ContractPrice END) AS June,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 7 
             THEN E.ContractPrice END) AS July,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 8 
             THEN E.ContractPrice END) AS August,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 9 
             THEN E.ContractPrice END) AS September,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 10 
             THEN E.ContractPrice END) AS October,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 11 
             THEN E.ContractPrice END) AS November,
    SUM(CASE WHEN EXTRACT(MONTH FROM E.StartDate) = 12 
             THEN E.ContractPrice END) AS December
FROM Agents AS A LEFT JOIN
    (SELECT En.AgentID, En.StartDate, En.ContractPrice
     FROM Engagements AS En
     WHERE En.StartDate >= '2015-01-01'
       AND En.StartDate < '2016-01-01') AS E
  ON A.AgentID = E.AgentID
GROUP BY AgtFirstName, AgtLastName, EXTRACT(YEAR FROM E.StartDate);

SELECT AE.AgtFirstName, AE.AgtLastName, z.YearNumber,
    SUM(AE.ContractPrice * Z.Qtr_1st) AS First_Quarter,
    SUM(AE.ContractPrice * Z.Qtr_2nd) AS Second_Quarter,
    SUM(AE.ContractPrice * Z.Qtr_3rd) AS Third_Quarter,
    SUM(AE.ContractPrice * Z.Qtr_4th) AS Fourth_Quarter
FROM ztblQuarters AS Z CROSS JOIN 
  (SELECT A.AgtFirstName, A.AgtLastName, 
       E.StartDate, E.ContractPrice
   FROM Agents AS A LEFT JOIN Engagements AS E
    ON A.AgentID = E.AgentID) AS AE
WHERE (AE.StartDate BETWEEN Z.QuarterStart AND Z.QuarterEnd)
   OR (AE.StartDate IS NULL AND Z.YearNumber = 2015)
GROUP BY AgtFirstName, AgtLastName, YearNumber;

