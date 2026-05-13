-- 不适合频繁更新的业务
-- 需要使用存储过程维护数据表的数据
DROP SCHEMA IF EXISTS model2 CASCADE;
CREATE SCHEMA model2;
SET search_path = model2;

CREATE TABLE Employees (
  EmployeeID int NOT NULL PRIMARY KEY,
  EmpName varchar(255) NOT NULL,
  EmpPosition varchar(255) NOT NULL,
  SupervisorID int NULL,
  lft int NULL,
  rgt int NULL
);

ALTER TABLE Employees 
ADD FOREIGN KEY (SupervisorID) 
REFERENCES Employees (EmployeeID);

INSERT INTO Employees (EmployeeID, EmpName, EmpPosition, SupervisorID, lft, rgt)
VALUES
	(1,	'Amy Kok', 'President', NULL, 1, 24),
	(2, 'Tom LaPlante', 'Manager', 1, 2, 9),
	(3,	'Aliya Ash', 'Manager', 1, 10, 23),
	(4,	'Nya Maeng', 'Associate', 2, 3, 4),
	(5, 'Lee Devi', 'Associate', 2, 5, 6),
	(6, 'Kush Itō', 'Associate', 2, 7, 8),
	(7, 'Raj Pavlov', 'Senior Editor', 3, 11, 12),
	(8, 'Kusk Pérez', 'Senior Developer', 3, 13, 20),
	(9, 'Zoha Larsson',	'Senior Writer', 3, 21, 22),
	(10, 'Yuri Lee', 'Developer', 8, 14, 15),
	(11, 'Mariam Davis', 'Developer', 8, 16, 17),
	(12, 'Jessica Yosef', 'Developer', 8, 18, 19)
;


-- 查询所有子孙
PREPARE test(int, int) AS
SELECT e.*
FROM Employees AS e
WHERE e.lft >= $1
  AND e.rgt <= $2;
  
EXECUTE test(10, 23);

DEALLOCATE test;


-- 查询所有祖先
PREPARE test(int, int) AS
SELECT *
FROM Employees AS e
WHERE e.lft <= $1
  AND e.rgt >= $2
;

EXECUTE test(10, 23);
DEALLOCATE test;

