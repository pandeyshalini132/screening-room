UPDATE bookings
SET total = total * 250.0 / 13.50
WHERE showing_id IN (
    SELECT id FROM showings WHERE ticket_price = 13.50
);

UPDATE bookings
SET total = total * 300.0 / 16.50
WHERE showing_id IN (
    SELECT id FROM showings WHERE ticket_price = 16.50
);

UPDATE booking_seats
SET price = 250.00
WHERE showing_id IN (
    SELECT id FROM showings WHERE ticket_price = 13.50
);

UPDATE booking_seats
SET price = 300.00
WHERE showing_id IN (
    SELECT id FROM showings WHERE ticket_price = 16.50
);

UPDATE showings
SET ticket_price = 250.00
WHERE ticket_price = 13.50;

UPDATE showings
SET ticket_price = 300.00
WHERE ticket_price = 16.50;
