-- 1. База personnel. Создайте транзакцию, которая переводит сотрудника в другой отдел, меняет его зарплату и переводит его задачи на другого сотрудника из старого отдела. 
-- Проверьте работу транзакции с ROLLBACK и с COMMIT.
USE personnel ;

START TRANSACTION;

SELECT @old_department := department_id FROM employees WHERE id = 1;

UPDATE employees SET department_id = 3 WHERE id = 1 ;
UPDATE salaries SET salary = 10000 WHERE employee_id = 1; 

SELECT @new_task_employee_id := id FROM employees WHERE department_id = @old_department AND id != 1 LIMIT 1;

UPDATE employees_tasks SET employee_id = @new_task_employee_id WHERE employee_id = 1 ;

COMMIT;

-- 2. База shop. Добавьте в таблицу product атрибут amount, характеризующий количество товара на складе. 
-- Доработайте транзакцию из лекции таким образом, чтобы количество товара на складе уменьшалось на соответствующий amount. 
-- Удалите триггер из прошлого домашнего задания (6ая задача) и уменьшите поле credit в этой транзакции на размер стоимости заказа.
USE shop;

ALTER TABLE `shop`.`product` 
ADD COLUMN `amount` INT NOT NULL DEFAULT 0 AFTER `status`;

DELIMITER $$

USE `shop`$$
DROP TRIGGER IF EXISTS `shop`.`insert_order` $$
DELIMITER ;

START TRANSACTION;
-- # Найдем общий прайс
-- # SELECT SUM(price) FROM product WHERE id IN (1,2,3) INTO @price;
SELECT @price := SUM(price) FROM product WHERE id IN (1,2,3);
-- # Создадим заказ
INSERT INTO `order` (user_id, date, status, price) VALUES (1, CURDATE(), 10, @price);
-- # Получим последний идентификатор
SET @order_id = LAST_INSERT_ID();
SELECT @price, @order_id;
-- # Свяжем товары
INSERT INTO order_product (order_id, product_id, amount) VALUES (@order_id, 1, 1);
INSERT INTO order_product (order_id, product_id, amount) VALUES (@order_id, 2, 2);
INSERT INTO order_product (order_id, product_id, amount) VALUES (@order_id, 3, 1);
-- # Изменим статус продуктов в корзине
UPDATE basket SET status = 50 WHERE user_id = 1 and product_id IN (1,2,3);

-- # Доработанные изменения уменьшение продуктов на складе
UPDATE product SET amount = amount - 1 WHERE id = 1 AND amount >= 1 ;
UPDATE product SET amount = amount - 2 WHERE id = 2 AND amount >= 2 ;
UPDATE product SET amount = amount - 1 WHERE id = 3 AND amount >= 1 ;

ROLLBACK;

-- 3. База shop. Проведите операцию отмены заказа. 
-- При этом заказ меняет статус, деньги возвращаются пользователю (поле credit), количество товаров на складе увеличивается на соответствующий amount

SET SQL_SAFE_UPDATES = 0;

START TRANSACTION;

UPDATE `order` SET status = 0 WHERE id = 2 ;

UPDATE user u, `order` o
SET 
u.credit = u.credit + o.price
WHERE o.id = 2 AND u.id = o.user_id ;

UPDATE product p, order_product op
SET p.amount = p.amount + op.amount 
WHERE p.id = op.product_id AND op.order_id = 2;
  
COMMIT;