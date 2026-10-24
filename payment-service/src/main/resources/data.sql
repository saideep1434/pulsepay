INSERT INTO roles (name) VALUES ('USER')  ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;


INSERT INTO merchants (name, category, upi_id, is_active, created_at)
VALUES ('Swiggy', 'FOOD', 'swiggy@icici', true, NOW())
ON CONFLICT (upi_id) DO NOTHING;

INSERT INTO merchants (name, category, upi_id, is_active, created_at)
VALUES ('Amazon', 'SHOPPING', 'amazon@hdfc', true, NOW())
ON CONFLICT (upi_id) DO NOTHING;

INSERT INTO merchants (name, category, upi_id, is_active, created_at)
VALUES ('IRCTC', 'TRAVEL', 'irctc@sbi', true, NOW())
ON CONFLICT (upi_id) DO NOTHING;
