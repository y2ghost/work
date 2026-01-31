SET search_path = SalesOrdersSample;

SELECT TO_CHAR(o.OrderDate, 'DAY') AS OrderDateWeekDay, 
  o.OrderDate,
  TO_CHAR(o.ShipDate, 'DAY') AS ShipDateWeekDay,
  o.ShipDate,
  o.ShipDate - o.OrderDate AS DeliveryLead
FROM Orders AS o
WHERE o.OrderDate >= (DATE_TRUNC('MONTH', CURRENT_DATE) - INTERVAL '2 MONTHS')
  AND o.OrderDate < DATE_TRUNC('MONTH', CURRENT_DATE);


CREATE TABLE DimDate (
  DateKey int NOT NULL,
  DateValue date NOT NULL PRIMARY KEY,
  NextDayValue date NOT NULL,
  YearValue smallint NOT NULL,
  YearQuarter int NOT NULL,
  YearMonth int NOT NULL,
  YearDayOfYear int NOT NULL,
  QuarterValue smallint NOT NULL,
  MonthValue smallint NOT NULL,
  DayOfYear smallint NOT NULL,
  DayOfMonth smallint NOT NULL,
  DayOfWeek smallint NOT NULL,
  YearName varchar(4) NOT NULL,
  YearQuarterName varchar(7) NOT NULL,
  QuarterName varchar(8) NOT NULL,
  MonthName varchar(3) NOT NULL,
  MonthNameLong varchar(9) NOT NULL,
  WeekdayName varchar(3) NOT NULL,
  WeekDayNameLong varchar(9) NOT NULL,
  StartOfYearDate date NOT NULL,
  EndOfYearDate date NOT NULL,
  StartOfQuarterDate date NOT NULL,
  EndOfQuarterDate date NOT NULL,
  StartOfMonthDate date NOT NULL,
  EndOfMonthDate date NOT NULL,
  StartOfWeekStartingSunDate date NOT NULL,
  EndOfWeekStartingSunDate date NOT NULL,
  StartOfWeekStartingMonDate date NOT NULL,
  EndOfWeekStartingMonDate date NOT NULL,
  StartOfWeekStartingTueDate date NOT NULL,
  EndOfWeekStartingTueDate date NOT NULL,
  StartOfWeekStartingWedDate date NOT NULL,
  EndOfWeekStartingWedDate date NOT NULL,
  StartOfWeekStartingThuDate date NOT NULL,
  EndOfWeekStartingThuDate date NOT NULL,
  StartOfWeekStartingFriDate date NOT NULL,
  EndOfWeekStartingFriDate date NOT NULL,
  StartOfWeekStartingSatDate date NOT NULL,
  EndOfWeekStartingSatDate date NOT NULL,
  QuarterSeqNo int NOT NULL,
  MonthSeqNo int NOT NULL,
  WeekStartingSunSeq int NOT NULL,
  WeekStartingMonSeq int NOT NULL,
  WeekStartingTueSeq int NOT NULL,
  WeekStartingWedSeq int NOT NULL,
  WeekStartingThuSeq int NOT NULL,
  WeekStartingFriSeq int NOT NULL,
  WeekStartingSatSeq int NOT NULL,
  JulianDate int NOT NULL,
  ModifiedJulianDate int NOT NULL,
  ISODate varchar(10) NOT NULL,
  ISOYearWeekNo int NOT NULL,
  ISOWeekNo smallint NOT NULL,
  ISODayOfWeek smallint NOT NULL,
  ISOYearWeekName varchar(8) NOT NULL,
  ISOYearWeekDayOfWeekName varchar(10) NOT NULL
);

CREATE INDEX DimDate_WeekDayLong
ON DimDate (DateValue, WeekdayNameLong);

CREATE INDEX DimDate_WeekDayLong_MonthSeqNo
ON DimDate (DateValue, WeekdayNameLong, MonthSeqNo);

CREATE INDEX DimDate_MonthSeqNo
ON DimDate (MonthSeqNo, DateValue, WeekdayNameLong);

CREATE INDEX Orders_OrderDate_ShipDate
ON Orders (OrderDate, ShipDate);

SELECT od.WeekDayNameLong AS OrderDateWeekDay, 
  o.OrderDate,
  sd.WeekDayNameLong AS ShipDateWeekDay,
  o.ShipDate,
  sd.DateKey - od.DateKey AS DeliveryLead
FROM Orders AS o
INNER JOIN DimDate AS od
  ON o.OrderDate = od.DateValue
INNER JOIN DimDate AS sd
  ON o.ShipDate = sd.DateValue
INNER JOIN DimDate AS td
  ON td.DateValue = CURRENT_DATE
WHERE od.MonthSeqNo = (td.MonthSeqNo - 1);

