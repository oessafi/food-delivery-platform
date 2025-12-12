INSERT INTO restaurant (id, name, address, phone, cuisine, available, opening_hours)
VALUES (1, 'Pizza Mama', '123 Rue Roma', '0601020304', 'Italien', true, '18:00-23:00');

INSERT INTO menu_item (id, name, description, price, category, available, restaurant_id)
VALUES (1, 'Margherita', 'Tomate, Mozza', 12.50, 'Pizza', true, 1);

INSERT INTO menu_item (id, name, description, price, category, available, restaurant_id)
VALUES (2, 'Pasta Carbonara', 'Crème, Lardon', 14.00, 'Pasta', true, 1);