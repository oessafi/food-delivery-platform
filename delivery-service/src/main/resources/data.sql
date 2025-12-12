INSERT INTO driver (id, name, phone, vehicle_type, vehicle_number, available)
VALUES (1, 'John Doe', '0699999999', 'Scooter', 'AB-123-CD', true);

INSERT INTO driver (id, name, phone, vehicle_type, vehicle_number, available)
VALUES (2, 'Jane Smith', '0688888888', 'Velo', 'VELO-01', true);

-- Ensure PostgreSQL sequences are updated
SELECT setval('driver_id_seq', (SELECT MAX(id) FROM driver));