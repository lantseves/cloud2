-- 1. База shop. Доработать схему заказов. Заказ должен иметь возможность включать множество продуктов. Продумать наилучший вариант, предоставить DDL команды
USE shop;

CREATE TABLE `shop`.`order_product` (
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `amount` INT NOT NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`order_id`, `product_id`),
  INDEX `product_idx` (`product_id` ASC),
  CONSTRAINT `order`
    FOREIGN KEY (`order_id`)
    REFERENCES `shop`.`order` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `product`
    FOREIGN KEY (`product_id`)
    REFERENCES `shop`.`product` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

ALTER TABLE `shop`.`order` 
DROP FOREIGN KEY `orders_product`;

ALTER TABLE `shop`.`order` 
DROP COLUMN `amount`,
DROP COLUMN `product_id`,
ADD COLUMN `price` INT NOT NULL AFTER `date`,
DROP INDEX `orders_product_idx` ;

-- 2. База shop. Реализовать триггер, срабатывающий на удаление продуктов из таблицы. В этом случае кол-во продуктов в категории должно уменьшаться (product_counts). 
-- Реализовать проверку текущего количества так, чтобы не позволить количеству уходить в минус.
USE shop;

DELIMITER $$
CREATE TRIGGER deleted_products_count
 AFTER DELETE
 ON product FOR EACH ROW
BEGIN
DECLARE count INT DEFAULT 0; 

SELECT product_counts INTO count
FROM category
WHERE category.id = OLD.category_id;

IF(count > 0) THEN
    UPDATE category SET product_counts = product_counts - 1 WHERE id = OLD.category_id;
END IF;
END$$
DELIMITER ;

-- 3. База personnel. Создать функцию, которая вычисляет возраст сотрудника исходя из его дня рождения. Показать ее использование в запросе SELECT
USE personnel ;

DELIMITER $$
CREATE FUNCTION getAge(
 id INT
)
RETURNS INT
DETERMINISTIC
BEGIN
DECLARE val BIGINT ;

SELECT TIMESTAMPDIFF(year, employees.birth_date, CURDATE()) INTO val
FROM employees
WHERE employees.id = id;

RETURN val ;
END$$
DELIMITER ;

SELECT 
name,
surname,
getAge(id) as age
FROM employees;

-- 4. База personnel. Создать процедуру, которая принимает в качестве аргумента id сотрудника, проверяет наличие записи в payments на текущий месяц и если ее нет - создает
USE personnel ;

DELIMITER $$
CREATE PROCEDURE `addPayments`(IN id INT)
BEGIN
DECLARE count INT DEFAULT 0 ;
SELECT COUNT(*) INTO count
FROM payments WHERE MONTH(payment_date) = MONTH(CURDATE()) AND YEAR(payment_date) = YEAR(CURDATE()) AND employee_id = id ;

IF(count = 0) THEN
	INSERT INTO payments (employee_id, bonus, payment_date) VALUES
    (id , 0,  CURDATE());
END IF;
END$$
DELIMITER ;

call personnel.addPayments(1);

-- Необязательные:
-- 1. База shop. Реализовать триггер, срабатывающий на обновление продуктов в таблице. 
-- Проверить, если сменилась категория, то выполнить соответствующие изменения в таблице с category
USE shop;

DELIMITER $$
CREATE TRIGGER update_product_catetegory
 AFTER UPDATE
 ON product FOR EACH ROW
BEGIN
DECLARE count INT DEFAULT 0;

IF(OLD.category_id != NEW.category_id) THEN
	 
SELECT product_counts INTO count
FROM category
WHERE category.id = OLD.category_id;

	IF(count > 0) THEN
		UPDATE category SET product_counts = product_counts - 1 WHERE id = OLD.category_id;
	END IF;
    
    UPDATE category SET product_counts = product_counts + 1 WHERE id = NEW.category_id;
END IF;
END$$
DELIMITER ;



-- 2. База shop. Добавить атрибут status в таблицу order. Реализовать триггер, срабатывающий на вставку заказа в таблицу order. 
-- Проверить, если у пользователя достаточно средств в поле credit - вычесть оттуда, иначе пометить заказ специальным статусом (не важно каким)
USE shop;

ALTER TABLE `shop`.`order` 
ADD COLUMN `status` VARCHAR(45) NOT NULL AFTER `price`;

ALTER TABLE `shop`.`user` 
ADD COLUMN `credit` INT NOT NULL DEFAULT 0 AFTER `age`;

DELIMITER $$
CREATE TRIGGER insert_order
 BEFORE INSERT
 ON `order` FOR EACH ROW
BEGIN
DECLARE credit INT DEFAULT 0;
DECLARE new_credit INT DEFAULT 0;

SELECT `user`.credit INTO credit
FROM `user`
WHERE id = NEW.user_id;

SET new_credit = credit - NEW.price ;

IF(new_credit >= 0) THEN
	UPDATE `user` SET credit = new_credit WHERE id = NEW.user_id;    
ELSE 
    SET NEW.status = 'block';
END IF;
END$$
DELIMITER ;