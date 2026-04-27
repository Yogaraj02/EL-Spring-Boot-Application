-- Database Schema for College Event Management System

CREATE DATABASE IF NOT EXISTS college_events_db;
USE college_events_db;

-- 1. Create independent tables first

CREATE TABLE IF NOT EXISTS Admins (
    admin_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS Participants (
    participant_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    roll_no VARCHAR(50) NOT NULL,
    department VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'PARTICIPANT'
);

CREATE TABLE IF NOT EXISTS Coordinators (
    coordinator_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    department VARCHAR(50),
    phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS Venues (
    venue_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    capacity INT,
    facilities TEXT,
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS Events (
    event_id INT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    type VARCHAR(50) NOT NULL,
    date VARCHAR(20) NOT NULL,
    time VARCHAR(20) NOT NULL,
    venue VARCHAR(100) NOT NULL,
    fee INT NOT NULL
);

-- 2. Create dependent tables with foreign keys

CREATE TABLE IF NOT EXISTS Payments (
    payment_id INT PRIMARY KEY,
    participant_id INT NOT NULL,
    event_id INT NOT NULL,
    amount INT NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    is_successful BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (participant_id) REFERENCES Participants(participant_id),
    FOREIGN KEY (event_id) REFERENCES Events(event_id)
);

CREATE TABLE IF NOT EXISTS Certificates (
    certificate_id INT PRIMARY KEY,
    participant_id INT NOT NULL,
    event_id INT NOT NULL,
    achievement VARCHAR(100),
    is_issued BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (participant_id) REFERENCES Participants(participant_id),
    FOREIGN KEY (event_id) REFERENCES Events(event_id)
);

CREATE TABLE IF NOT EXISTS Feedbacks (
    feedback_id INT PRIMARY KEY,
    participant_id INT NOT NULL,
    event_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    category VARCHAR(50),
    FOREIGN KEY (participant_id) REFERENCES Participants(participant_id),
    FOREIGN KEY (event_id) REFERENCES Events(event_id)
);

-- 3. Create relationship tables

CREATE TABLE IF NOT EXISTS Event_Participants (
    participant_id INT,
    event_id INT,
    PRIMARY KEY (participant_id, event_id),
    FOREIGN KEY (participant_id) REFERENCES Participants(participant_id),
    FOREIGN KEY (event_id) REFERENCES Events(event_id)
);

CREATE TABLE IF NOT EXISTS Coordinator_Events (
    coordinator_id INT,
    event_id INT,
    PRIMARY KEY (coordinator_id, event_id),
    FOREIGN KEY (coordinator_id) REFERENCES Coordinators(coordinator_id),
    FOREIGN KEY (event_id) REFERENCES Events(event_id)
);

CREATE TABLE IF NOT EXISTS Admin_Events (
    admin_id INT,
    event_id INT,
    PRIMARY KEY (admin_id, event_id),
    FOREIGN KEY (admin_id) REFERENCES Admins(admin_id),
    FOREIGN KEY (event_id) REFERENCES Events(event_id)
);

-- Counter table to simulate the app's current counter implementation
CREATE TABLE IF NOT EXISTS Counters (
    id INT PRIMARY KEY,
    paymentCounter INT DEFAULT 1,
    certificateCounter INT DEFAULT 1,
    feedbackCounter INT DEFAULT 1,
    participantCounter INT DEFAULT 1
);

INSERT IGNORE INTO Counters (id, paymentCounter, certificateCounter, feedbackCounter, participantCounter) VALUES (1, 1, 1, 1, 1);
