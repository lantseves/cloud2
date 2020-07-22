-- Создайте базу данных shop используя следующие предикаты:
CREATE SCHEMA `shop` ;

USE shop ;

-- 1. Пользователь USER с идентификатором ID имеет имя NAME и фамилию SURNAME, а так же уникальный адрес EMAIL и возраст AGE
CREATE TABLE `shop`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `surname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL ,
  `age` INT NOT NULL ,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC));

-- 2. Категория CATEGORY имеет идентификатор ID и название NAME и возможно является подкатегорией другой категории с идентификатором PARENT_ID
CREATE TABLE `shop`.`categories` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `parent_id` INT NULL,
  PRIMARY KEY (`id`));
  
ALTER TABLE `shop`.`categories` 
ADD INDEX `categories_categories_idx` (`parent_id` ASC);

ALTER TABLE `shop`.`categories` 
ADD CONSTRAINT `categories_categories`
  FOREIGN KEY (`parent_id`)
  REFERENCES `shop`.`categories` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

-- 3. Продукт PRODUCT имеет идентификатор ID и название NAME, имеет цену PRICE и относится к категории CATEGORY_ID. STATUS определяет текущий статус товара
CREATE TABLE `shop`.`products` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `price` INT NOT NULL,
  `category_id` INT NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `products_categories_idx` (`category_id` ASC),
  CONSTRAINT `products_categories`
    FOREIGN KEY (`category_id`)
    REFERENCES `shop`.`categories` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- 4. Корзина BASKET содержит в себе продукт PRODUCT_ID, который купил пользователь USER_ID. STATUS определяет текущий статус товара в корзине.
CREATE TABLE `shop`.`baskets` (
  `product_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `status` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`product_id`, `user_id`),
  INDEX `baskets_users_idx` (`user_id` ASC),
  CONSTRAINT `baskets_users`
    FOREIGN KEY (`user_id`)
    REFERENCES `shop`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `baskets_products`
    FOREIGN KEY (`product_id`)
    REFERENCES `shop`.`products` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- 5. Заказ ORDER с идентификатором ID совершил пользователь USER_ID, купив товар PRODUCT_ID в количестве AMOUNT в день DATE.
CREATE TABLE `shop`.`orders` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `amount` INT NOT NULL,
  `date` DATE NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `orders_users_idx` (`user_id` ASC),
  INDEX `orders_product_idx` (`product_id` ASC),
  CONSTRAINT `orders_users`
    FOREIGN KEY (`user_id`)
    REFERENCES `shop`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `orders_product`
    FOREIGN KEY (`product_id`)
    REFERENCES `shop`.`products` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

-- 6. Журнал LOG с идентификатором ID содержит запись о действии ACTION которое совершил пользователь USER_ID в день DATE
-- Задачи:
-- 2. Укажите для таблицы log подсистему хранения ARCHIVE. Внесите записи в таблицу. Попробуйте их обновить и удалить. Оцените эффект
CREATE TABLE `shop`.`log` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `action` VARCHAR(45) NOT NULL,
  `user_id` VARCHAR(45) NOT NULL,
  `date` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = ARCHIVE;

-- Задачи:
-- 3. Заметьте какие индексы создаются при создании внешних связей и какие имеет смысл создать дополнительно, например в таблице user.
ALTER TABLE `shop`.`users` 
ADD INDEX `name` (`name` ASC),
ADD INDEX `surname` (`surname` ASC);

-- Задачи:
-- 4. Создайте csv файлы category_external.csv и product_external.csv со столбцами аналогичными таблицам. Используя команду ниже импортируйте данные

LOAD DATA INFILE 'C:/temp_lesson4/category_external.csv'
INTO TABLE categories
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;

LOAD DATA INFILE 'C:/temp_lesson4/product_external.csv'
INTO TABLE products
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS;

-- Последняя задача аналогична предыдущей, но усложненная и не обязательна к выполнению.
-- Создайте таблицы category_external, product_external с подсистемой хранения CSV и схемой аналогичной исходным таблицам. 
CREATE TABLE `shop`.`category_external` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `parent_id` INT NOT NULL)
ENGINE = CSV;

CREATE TABLE `shop`.`product_external` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `price` INT NOT NULL,
  `category_id` INT NOT NULL,
  `status` VARCHAR(45) NOT NULL)
ENGINE = CSV;

-- Создайте csv файлы category_external.csv и product_external.csv со столбцами аналогичными таблицам. 
-- Попробуйте скопировать их в папку с данными для базы данных shop (расположение папки можно посмотреть в разделе сервер статус в воркбенче: Server Directories).
-- Атрибуты должны быть разделены запятыми. Чтобы данные сразу отобразились в инетрфейсе воркбенча, выполните сначала FLUSH TABLE ...:
-- mysql> FLUSH TABLE category_external;
-- mysql> SELECT * FROM category_external;
FLUSH TABLE category_external;
SELECT * FROM category_external;

FLUSH TABLE product_external;
SELECT * FROM product_external;

-- Перенесите данные в исходные таблицы category и product с помощью запроса SELECT ... INSERT
INSERT INTO categories SELECT * FROM category_external;

INSERT INTO products SELECT * FROM product_external;
