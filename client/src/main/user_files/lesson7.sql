-- 1. Рассмотрите следующий запрос: SELECT emp.name, (SELECT salary FROM salaries WHERE salaries.employee_id = emp.id) FROM employees emp 
-- Оцените план выполнения запроса с помощью команды EXPLAIN. Опишите своими словами основные характеристики плана. 
-- Какие проблемы вы видите в этом запросе? Попробуйте оптимизировать запрос

USE personnel ;
set profiling=1;

EXPLAIN
SELECT 
emp.name, 
(SELECT salary FROM salaries WHERE salaries.employee_id = emp.id) 
FROM employees emp ;

-- Мы видим зависимый подзапрос, и время выполнения запроса: 0.00038

-- Заменил подзапрос на join
-- EXPLAIN
SELECT 
emp.name, 
sal.salary
FROM employees emp 
LEFT JOIN salaries sal ON sal.employee_id = emp.id;

-- Сейчас запросы выполняются по 1 разу и соединяются по индексу, время выполнения запроса: 0.00029

SHOW PROFILES ;

-- --------------------------------------------------------------------------------------------------------------------

-- 2. Рассмотрите следующий запрос: SELECT * FROM personnel.payments WHERE payment_date>'2019-01-01';
-- Проверьте план выполнения запроса, добавьте индекс по payment_date и оцените результат. 
-- Используйте конструкцию FORCE INDEX (idx) для форсирования индекса, если субд не использует его. 
-- Изменилась ли скорость выполнения запроса? Какие еще изменения в схеме вы бы сделали в целях оптимизации?

-- EXPLAIN
SELECT * 
FROM personnel.payments 
FORCE INDEX (indx_date)
WHERE payment_date>'2019-01-01';

SHOW PROFILES ;

-- До добавления индекса: требуется обход всей таблицы, ключ не используется, время выполнения: 0,00027
-- После добавления индекса: выбирается диапазон из таблицы, используется индекс, время выполнения: 0,00032. 
-- Интересно получается, наверное, из-за маленького объема таблицы использование индекса замедляет запрос.

-- ---------------------------------------------------------------------------------------------------------------------

-- 3. Найдите 2 проблемных запроса с помощью команды EXPLAIN из примеров ваших домашних заданий или лекций. 
-- Попробуйте их оптимизировать, объясните свой подход.

-- Исходный запрос
-- EXPLAIN
SELECT DISTINCT
dep.name
FROM personnel.departments dep
LEFT JOIN personnel.employees emp ON emp.department_id = dep.id
WHERE emp.department_id IS NULL ;

SHOW PROFILES ;

-- dep перебирается вся таблица, дистинкт, время выполнения: 0,00041


-- EXPLAIN
SELECT 
dep.name
FROM personnel.departments dep
WHERE dep.id NOT IN (SELECT 
				emp.department_id
				FROM personnel.employees emp) ;
                
SHOW PROFILES ;
-- Убрав дистинкт мы получили прирост скорости, также в подзапросе используются только данные индекса, время выполнения: 0,00029
-- -------------------------------