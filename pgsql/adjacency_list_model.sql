-- 领接表是常用的层次关系表达方式之一
DROP SCHEMA IF EXISTS model CASCADE;
CREATE SCHEMA model;
SET search_path = model;

CREATE TABLE Employees (
  EmployeeID int NOT NULL PRIMARY KEY,
  EmpName varchar(255) NOT NULL,
  EmpPosition varchar(255) NOT NULL,
  SupervisorID int NULL
);

ALTER TABLE Employees 
ADD FOREIGN KEY (SupervisorID) 
REFERENCES Employees (EmployeeID);

INSERT INTO Employees (EmployeeID, EmpName, EmpPosition, SupervisorID)
VALUES
	(1,	'Amy Kok', 'President', NULL),
	(2, 'Tom LaPlante', 'Manager', 1),
	(3,	'Aliya Ash', 'Manager', 1),
	(4,	'Nya Maeng', 'Associate', 2),
	(5, 'Lee Devi', 'Associate', 2),
	(6, 'Kush Itō', 'Associate', 2),
	(7, 'Raj Pavlov', 'Senior Editor', 3),
	(8, 'Kusk Pérez', 'Senior Developer', 3),
	(9, 'Zoha Larsson',	'Senior Writer', 3),
	(10, 'Yuri Lee', 'Developer', 8),
	(11, 'Mariam Davis', 'Developer', 8),
	(12, 'Jessica Yosef', 'Developer', 8)
;

UPDATE Employees SET SupervisorID = 5 WHERE EmployeeID = 4;
UPDATE Employees SET SupervisorID = 3 WHERE EmployeeID = 2;


SELECT e1.EmpName AS Employee, e2.EmpName AS Supervisor, 
       e3.EmpName AS SupverisorsSupervsior
FROM Employees AS e1
LEFT JOIN Employees AS e2 ON e1.SupervisorID = e2.EmployeeID
LEFT JOIN Employees AS e3 ON e2.SupervisorID = e3.EmployeeID;

