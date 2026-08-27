---------------------------------------------------------
-- 1. USERS SEED DATA (Fırat'ın Kodu - DOKUNULMADI)
---------------------------------------------------------
TRUNCATE TABLE public.users RESTART IDENTITY CASCADE;

INSERT INTO public.users (
    created_date,
    updated_date,
    deleted_date,
    email,
    username,
    first_name,
    last_name,
    password,
    is_locked,
    wrong_attempts,
    title_id
)
SELECT
    NOW() - ((random() * 365)::int || ' days')::interval AS created_date,
    NOW() - ((random() * 30)::int || ' days')::interval AS updated_date,
    CASE
        WHEN random() < 0.05 THEN NOW() - ((random() * 10)::int || ' days')::interval
        ELSE NULL
    END AS deleted_date,
    'user' || gs || '@company.com' AS email,
    'user' || gs || '@company.com' AS username,
    (ARRAY['Ahmet', 'Mehmet', 'Can', 'Zeynep', 'Elif', 'Ayşe', 'Deniz', 'Emre', 'Burak', 'Selin'])[((gs % 10) + 1)] AS first_name,
    (ARRAY['Yılmaz', 'Kaya', 'Demir', 'Çelik', 'Şahin', 'Yıldız', 'Öztürk', 'Aydın', 'Özdemir', 'Arslan'])[((gs % 10) + 1)] AS last_name,
    'Pass1234' AS password,
    (random() < 0.10) AS is_locked,
    (random() * 2)::int AS wrong_attempts,
    (random() * 4 + 1)::int AS title_id
FROM generate_series(1, 1000) gs;


---------------------------------------------------------
-- 2. CATALOGS SEED DATA (English)
---------------------------------------------------------
TRUNCATE TABLE public.catalogs CASCADE;

INSERT INTO public.catalogs (catalog_id, catalog_name, status, created_date, updated_date)
VALUES
    ('CAT-0001', 'Postpaid Mobile Plans', 'ACTIVE', NOW() - '1 year'::interval, NOW()),
    ('CAT-0002', 'Prepaid & Youth Plans', 'ACTIVE', NOW() - '11 months'::interval, NOW()),
    ('CAT-0003', 'Fiber Home Internet', 'ACTIVE', NOW() - '10 months'::interval, NOW()),
    ('CAT-0004', 'DSL / VDSL Internet', 'ACTIVE', NOW() - '9 months'::interval, NOW()),
    ('CAT-0005', 'Smartphone Campaigns', 'ACTIVE', NOW() - '8 months'::interval, NOW()),
    ('CAT-0006', 'Tablets & Computers', 'ACTIVE', NOW() - '7 months'::interval, NOW()),
    ('CAT-0007', 'Wearable Technology', 'ACTIVE', NOW() - '6 months'::interval, NOW()),
    ('CAT-0008', 'Accessories & Hardware', 'ACTIVE', NOW() - '5 months'::interval, NOW()),
    ('CAT-0009', 'TV & Entertainment Packages', 'ACTIVE', NOW() - '4 months'::interval, NOW()),
    ('CAT-0010', 'Smart Home Solutions', 'ACTIVE', NOW() - '3 months'::interval, NOW());

---------------------------------------------------------
-- 3. PRODUCTS SEED DATA (English & Unique)
---------------------------------------------------------
TRUNCATE TABLE public.products CASCADE;

INSERT INTO public.products (
    product_id, catalog_id, created_date, deleted_date, product_name, price, stock_status, updated_date, status
)
SELECT
    'PRD-' || LPAD(gs::text, 4, '0'),
    'CAT-' || LPAD(cat_id::text, 4, '0'),
    NOW() - ((random() * 730)::int || ' days')::interval,
    CASE WHEN random() < 0.05 THEN NOW() - ((random() * 180)::int || ' days')::interval ELSE NULL END,
    CASE cat_id
        WHEN 1 THEN 'Mega ' || (10 + (random()*40)::int) || 'GB Unlimited Plan - SN: ' || gs
        WHEN 2 THEN 'Prepaid ' || (5 + (random()*15)::int) || 'GB Social Plan - SN: ' || gs
        WHEN 3 THEN (100 * (1 + (random()*9)::int)) || ' Mbps No-Contract Fiber - SN: ' || gs
        WHEN 4 THEN (16 + (random()*34)::int) || ' Mbps VDSL Unlimited - SN: ' || gs
        WHEN 5 THEN 'Smartphone Model ' || chr(65 + (random()*25)::int) || ' Pro - SN: ' || gs
        WHEN 6 THEN 'Tablet ' || (8 + (random()*4)::int) || 'th Gen 128GB - SN: ' || gs
        WHEN 7 THEN 'Smart Watch Series - SN: ' || gs
        WHEN 8 THEN 'Premium Hardware Accessory - SN: ' || gs
        WHEN 9 THEN 'Entertainment & Sports Package - SN: ' || gs
        ELSE 'Smart Home Solution - SN: ' || gs
    END,
    ROUND((50 + random() * 4950)::numeric, 2)::float8,
    (20 + random() * 480)::int,
    NOW(),
    CASE WHEN random() > 0.15 THEN 'ACTIVE' ELSE 'INACTIVE' END
FROM (SELECT gs, floor(random() * 10) + 1 AS cat_id FROM generate_series(1, 1000) gs) sub;

---------------------------------------------------------
-- 4. OUTLIER (ANOMALY) TEST DATA
---------------------------------------------------------
UPDATE public.products SET stock_status = 0 WHERE random() < 0.08;
UPDATE public.products SET status = 'INACTIVE' WHERE stock_status = 0 AND random() < 0.5;
UPDATE public.products SET stock_status = -5 WHERE product_id IN ('PRD-0045', 'PRD-0222', 'PRD-0901');
UPDATE public.products SET price = 0 WHERE product_id IN ('PRD-0101', 'PRD-0202', 'PRD-0303');
UPDATE public.products SET updated_date = created_date WHERE random() < 0.20;