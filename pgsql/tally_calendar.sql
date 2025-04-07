DROP TABLE IF EXISTS Appointments;
CREATE TABLE Appointments (
  AppointmentID serial NOT NULL PRIMARY KEY ,
  ApptStartDate date NOT NULL ,
  ApptStartTime time NOT NULL ,
  ApptEndDate date NOT NULL ,
  ApptEndTime time NOT NULL ,
  ApptDescription varchar(50) NULL 
);

INSERT INTO Appointments (ApptStartDate, ApptStartTime, ApptEndDate, ApptEndTime, ApptDescription) VALUES
(DATE '2017-01-03', '10:30', DATE '2017-01-03', '11:00', 'Meet with John'),
(DATE '2017-01-03', '11:15', DATE '2017-01-03', '12:00', 'Design cover page'),
(DATE '2017-01-05', '09:00', DATE '2017-01-05', '15:00', 'Teach SQL course'),
(DATE '2017-01-05', '15:30', DATE '2017-01-05', '16:30', 'Review with Ben'),
(DATE '2017-01-06', '10:00', DATE '2017-01-06', '11:30', 'Plan for lunch');


DROP TABLE IF EXISTS DimDate2;
CREATE TABLE DimDate2 (
  DateKey int NOT NULL PRIMARY KEY ,
  FullDate date NOT NULL 
);

CREATE INDEX iFullDate
ON DimDate2 (FullDate);

WITH RECURSIVE SeqNumTbl(SeqKey, SeqNum) AS 
  (SELECT 1 AS SeqKey, DATE_TRUNC('YEAR', CURRENT_DATE) AS SeqNum
   UNION ALL
   SELECT SeqKey + 1, SeqNum + INTERVAL '1 DAYS' 
   FROM SeqNumTbl
   WHERE SeqNum < (DATE_TRUNC('YEAR', CURRENT_DATE + INTERVAL '2 YEARS')))
--INSERT INTO DimDate(DateKey, FullDate )
SELECT SeqKey, SeqNum  
FROM SeqNumTbl;


SELECT D.FullDate, 
  A.ApptDescription ,
  A.ApptStartDate + A.ApptStartTime AS ApptStart ,
  A.ApptEndDate + A.ApptEndTime AS ApptEnd
FROM DimDate2 AS D LEFT JOIN Appointments AS A
  ON D.FullDate = A.ApptStartDate
ORDER BY D.FullDate;

