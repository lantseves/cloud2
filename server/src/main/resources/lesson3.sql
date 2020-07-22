-- Используя функцию SUM() найдите сумму всех зарплат сотрудников из IT Отдела
SELECT 
SUM(sal.salary)
FROM personnel.salaries sal
JOIN personnel.employees emp ON emp.id = sal.employee_id 
WHERE emp.department_id = 3 ;

-- Составьте запрос для нахождения сотрудника без зарплаты
SELECT 
surname,
name
FROM personnel.salaries sal
RIGHT JOIN personnel.employees emp ON emp.id = sal.employee_id 
WHERE sal.salary IS NULL ;

-- Составьте запрос для нахождения отдела без сотрудников
SELECT DISTINCT
dep.name
FROM personnel.departments dep
LEFT JOIN personnel.employees emp ON emp.department_id = dep.id
WHERE emp.department_id IS NULL ;

-- Заполните таблицу tasks разными задачами и заполните связующую таблицу employees_tasks
INSERT INTO personnel.tasks (`name`, `employee_id`, `deadline`) 
VALUES 
('Задача 1', '2', '2020-09-01'),
('Задача 2', '1', '2020-09-01'),
('Задача 3', '2', '2020-09-01');

INSERT INTO personnel.employees_tasks (`task_id`, `employee_id`) 
VALUES 
(3, 2),
(4, 3),
(5, 4);

--  Выведите все задачи сотрудников из отдела "IT Отдел"
SELECT DISTINCT
t.name
FROM tasks t
JOIN employees_tasks et ON t.id = et.task_id
JOIN employees e ON e.id = et.employee_id
WHERE e.department_id = 3;

-- Используя DDL, создайте таблицу payments с атрибутами:
-- id int primary ai;
-- employee_id int (внешний ключ на employees);
-- bonus int(11) default 0;
-- payment_date date not null;
CREATE TABLE `personnel`.`payments` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `employee_id` INT NULL,
  `bonus` INT(11) NULL DEFAULT 0,
  `payment_date` DATE NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `payment_employee_idx` (`employee_id` ASC),
  CONSTRAINT `payment_employee`
    FOREIGN KEY (`employee_id`)
    REFERENCES `personnel`.`employees` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);
    -- и заполните ее данными за период 2019 год. Эта таблица представляет собой ежемесячные выплаты зарплат сотрудникам
INSERT INTO `personnel`.`payments` (`employee_id`, `bonus`, `payment_date`) VALUES 
('1', '0', '2019-01-01'),
('2', '0', '2019-01-01'),
('3', '200', '2019-01-01'),
('4', '400', '2019-01-01'),
('5', '500', '2019-01-01'),
('1', '0', '2019-02-01'),
('2', '0', '2019-02-01'),
('3', '300', '2019-02-01'),
('4', '400', '2019-02-01'),
('5', '500', '2019-02-01'),
('1', '0', '2019-03-01'),
('2', '0', '2019-03-01'),
('3', '20', '2019-03-01'),
('4', '30', '2019-03-01'),
('5', '40', '2019-03-01'),
('1', '0', '2019-04-01'),
('2', '0', '2019-04-01'),
('3', '30', '2019-04-01'),
('4', '40', '2019-04-01'),
('5', '50', '2019-04-01'),
('1', '0', '2019-05-01'),
('2', '0', '2019-05-01'),
('3', '60', '2019-05-01'),
('4', '60', '2019-05-01'),
('5', '0', '2019-05-01'),
('1', '0', '2019-06-01'),
('2', '0', '2019-06-01'),
('3', '0', '2019-06-01'),
('4', '0', '2019-06-01'),
('5', '0', '2019-06-01'),
('1', '0', '2019-07-01'),
('2', '0', '2019-07-01'),
('3', '0', '2019-07-01'),
('4', '0', '2019-07-01'),
('5', '0', '2019-07-01'),
('1', '0', '2019-08-01'),
('2', '0', '2019-08-01'),
('3', '22', '2019-08-01'),
('4', '33', '2019-08-01'),
('5', '44', '2019-08-01'),
('1', '0', '2019-09-01'),
('2', '0', '2019-09-01'),
('3', '0', '2019-09-01'),
('4', '0', '2019-09-01'),
('5', '0', '2019-09-01'),
('1', '0', '2019-10-01'),
('2', '0', '2019-10-01'),
('3', '0', '2019-10-01'),
('4', '40', '2019-10-01'),
('5', '55', '2019-10-01'),
('1', '0', '2019-11-01'),
('2', '0', '2019-11-01'),
('3', '0', '2019-11-01'),
('4', '5', '2019-11-01'),
('5', '6', '2019-11-01'),
('1', '0', '2019-12-01'),
('2', '0', '2019-12-01'),
('3', '0', '2019-12-01'),
('4', '3', '2019-12-01'),
('5', '5', '2019-12-01');

-- Выведите всех сотрудников, которые получили бонус в 2019 году. Вывод должен состоять из атрибутов Имя Фамилия Зарплата Бонус Дата 
-- (Сделал как понял, первое пледложение просит вывести список сотрудников(Подумал нужна сумма и кто получил больше 0), а второе приклеить зп и дату, т.е. группировка не нужна, страно но зделал как понял)
SELECT 
e.name,
e.surname,
s.salary,
p.bonus,
p.payment_date
FROM personnel.payments p 
JOIN personnel.employees e ON e.id = p.employee_id
JOIN personnel.salaries s ON e.id = s.employee_id
WHERE YEAR(p.payment_date) = 2019 AND p.bonus > 0 ;

-- Выведите суммы выплат по каждому сотруднику за 2019
SELECT 
e.name,
e.surname,
SUM(p.bonus) + SUM(s.salary) payouts
FROM personnel.payments p 
JOIN personnel.employees e ON e.id = p.employee_id
JOIN personnel.salaries s ON e.id = s.employee_id
WHERE YEAR(p.payment_date) = 2019
GROUP BY e.name, e.surname ;


-- Выведите имя и фамилию сотрудника с максимальной суммой выплат за 2019
SELECT
result.name,
result.surname
FROM
	(SELECT
	MAX(payouts),
	temp.name,
	temp.surname
	FROM (SELECT 
		e.name,
		e.surname,
		SUM(p.bonus) + SUM(s.salary) payouts
		FROM personnel.payments p 
		JOIN personnel.employees e ON e.id = p.employee_id
		JOIN personnel.salaries s ON e.id = s.employee_id
		WHERE YEAR(p.payment_date) = 2019
		GROUP BY e.name, e.surname) temp
	) result ;
    
