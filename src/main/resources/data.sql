INSERT INTO customers (id, name, email, phone, company) VALUES (nextval('customers_seq'), '田中太郎', 'tanaka@example.com', '03-1234-5678', '株式会社サンプル') ON CONFLICT DO NOTHING;
INSERT INTO customers (id, name, email, phone, company) VALUES (nextval('customers_seq'), '佐藤花子', 'sato@example.com', '03-2345-6789', 'テスト株式会社') ON CONFLICT DO NOTHING;
INSERT INTO customers (id, name, email, phone, company) VALUES (nextval('customers_seq'), '鈴木一郎', 'suzuki@example.com', '03-3456-7890', 'デモ企業') ON CONFLICT DO NOTHING;
