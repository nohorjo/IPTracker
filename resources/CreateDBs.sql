CREATE DATABASE iptracker;
USE iptracker;

CREATE TABLE ip_addresses
(
	time_of_connection TIMESTAMP DEFAULT NOW(),
	remote_address VARCHAR(15) NOT NULL,
	session_id TEXT,
	cookie_id TEXT,
	urlid INT,
	visited_before VARCHAR(3),
	is_mobile TEXT
);

CREATE TABLE urls
(
	id INT NOT NULL AUTO_INCREMENT,
	url TEXT NOT NULL,
	title TEXT NOT NULL,
	PRIMARY KEY(id)
);
