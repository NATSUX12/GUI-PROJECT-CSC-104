USE cashierdb;

CREATE TABLE products (
  id    INT(11) AUTO_INCREMENT PRIMARY KEY,
  name  VARCHAR(100) NOT NULL,
  price DOUBLE NOT NULL
);

CREATE TABLE sales (
  id            INT(11) AUTO_INCREMENT PRIMARY KEY,
  total         DOUBLE NOT NULL,
  payment       DOUBLE NOT NULL,
  change_amount DOUBLE NOT NULL,
  date          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO products (name, price) VALUES
  ('Coca Cola 1.5L', 75.00),
  ('Pepsi 1.5L', 70.00),
  ('Mineral Water', 20.00),
  ('Nescafe Coffee', 15.00),
  ('Bread Loaf', 50.00),
  ('Instant Noodles', 15.00),
  ('Milk 1L', 85.00),
  ('Shampoo Sachet', 5.00),
  ('Bath Soap', 25.00),
  ('Biscuits Pack', 25.00);