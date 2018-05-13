# --- !Ups

CREATE TABLE book(
  book_id SERIAL,
  restaurant_id BIGINT,
  custumer_id BIGINT,
  people_quantity INTEGER,
  observation VARCHAR(500),
  date TIMESTAMP,
  status VARCHAR(15),
  rating DECIMAL,
  PRIMARY KEY(book_id)
);

CREATE TABLE dish(
  dish_id SERIAL,
  restaurant_id BIGINT,
  name VARCHAR(150),
  description TEXT,
  image_url TEXT,
  price NUMERIC(13,2),
  category VARCHAR(50),
  PRIMARY KEY(dish_id)
);

CREATE TABLE request(
  request_id SERIAL,
  book_id BIGINT,
  status VARCHAR(15),
  rating DECIMAL,
  PRIMARY KEY(request_id)
);

CREATE TABLE restaurant(
  restaurant_id SERIAL,
  name VARCHAR(300),
  image_url VARCHAR(500),
  endereco VARCHAR(500),
  PRIMARY KEY(restaurant_id)
);

CREATE TABLE custumer(
  custumer_id SERIAL,
  name VARCHAR(300),
  PRIMARY KEY(custumer_id)
);


# --- !Downs

DROP TABLE IF EXISTS book CASCADE;
DROP TABLE IF EXISTS dish CASCADE;
DROP TABLE IF EXISTS request CASCADE;
DROP TABLE IF EXISTS restaurant CASCADE;
DROP TABLE IF EXISTS custumer CASCADE;
