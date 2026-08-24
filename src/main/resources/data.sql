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
-- 2. CATALOGS SEED DATA
---------------------------------------------------------
TRUNCATE TABLE public.catalogs CASCADE;

-- KOLONLAR ENTITY'YE GÖRE DÜZENLENDİ: catalog_id, catalog_name
INSERT INTO public.catalogs (catalog_id, catalog_name, status, created_date, updated_date)
SELECT
    'CAT-' || LPAD(gs::text, 4, '0'),
    'Kategori ' || gs,
    'ACTIVE',
    NOW() - ((random() * 365)::int || ' days')::interval,
    NOW()
FROM generate_series(1, 10) gs;


---------------------------------------------------------
-- 3. PRODUCTS SEED DATA
---------------------------------------------------------
TRUNCATE TABLE public.products CASCADE;

-- KOLONLAR ENTITY'YE GÖRE DÜZENLENDİ: product_id, product_name, stock_status
INSERT INTO public.products (
    product_id,
    catalog_id,
    created_date,
    deleted_date,
    product_name,
    price,
    stock_status,
    updated_date,
    status
)
SELECT
    'PRD-' || LPAD(gs::text, 4, '0'),
    'CAT-' || LPAD((floor(random() * 9) + 1)::text, 4, '0'),
    NOW() - ((random() * 730)::int || ' days')::interval,
    CASE
        WHEN random() < 0.05
            THEN NOW() - ((random() * 180)::int || ' days')::interval
        ELSE NULL
    END AS deleted_date,
    CASE (random() * 7)::int
        WHEN 0 THEN 'Laptop ' || gs
        WHEN 1 THEN 'Phone ' || gs
        WHEN 2 THEN 'Monitor ' || gs
        WHEN 3 THEN 'Keyboard ' || gs
        WHEN 4 THEN 'Mouse ' || gs
        WHEN 5 THEN 'Tablet ' || gs
        WHEN 6 THEN 'Headset ' || gs
        ELSE 'Camera ' || gs
    END AS product_name,
    CASE (random() * 7)::int
        WHEN 0 THEN ROUND((700 + random() * 4300)::numeric, 2)::float8
        WHEN 1 THEN ROUND((500 + random() * 3500)::numeric, 2)::float8
        WHEN 2 THEN ROUND((250 + random() * 1800)::numeric, 2)::float8
        WHEN 3 THEN ROUND((40 + random() * 260)::numeric, 2)::float8
        WHEN 4 THEN ROUND((20 + random() * 150)::numeric, 2)::float8
        WHEN 5 THEN ROUND((350 + random() * 2200)::numeric, 2)::float8
        WHEN 6 THEN ROUND((60 + random() * 550)::numeric, 2)::float8
        ELSE ROUND((600 + random() * 3200)::numeric, 2)::float8
    END AS price,
    CASE (random() * 7)::int
        WHEN 0 THEN (10 + random() * 40)::int
        WHEN 1 THEN (15 + random() * 70)::int
        WHEN 2 THEN (20 + random() * 60)::int
        WHEN 3 THEN (80 + random() * 300)::int
        WHEN 4 THEN (120 + random() * 500)::int
        WHEN 5 THEN (20 + random() * 80)::int
        WHEN 6 THEN (30 + random() * 120)::int
        ELSE (5 + random() * 50)::int
    END AS stock_status,
    NOW() - ((random() * 365)::int || ' days')::interval AS updated_date,
    CASE WHEN random() > 0.10 THEN 'ACTIVE' ELSE 'INACTIVE' END AS status
FROM generate_series(1, 1000) gs;

-- OUTLIER (Uç Değer) Veriler
UPDATE public.products
SET price = ROUND((25000 + random() * 25000)::numeric, 2)::float8
WHERE product_id IN ('PRD-0015', 'PRD-0087', 'PRD-0154', 'PRD-0488', 'PRD-0721');

UPDATE public.products
SET stock_status = 4000 + (random() * 3000)::int
WHERE product_id IN ('PRD-0012', 'PRD-0090', 'PRD-0311', 'PRD-0555', 'PRD-0777');

UPDATE public.products
SET stock_status = 0
WHERE random() < 0.08;

UPDATE public.products
SET stock_status = -5
WHERE product_id IN ('PRD-0045', 'PRD-0222', 'PRD-0901');

UPDATE public.products
SET price = 0
WHERE product_id IN ('PRD-0101', 'PRD-0202', 'PRD-0303');

UPDATE public.products
SET updated_date = created_date
WHERE random() < 0.20;

UPDATE public.products
SET deleted_date = NOW() - ((random() * 120)::int || ' days')::interval
WHERE status = 'INACTIVE'
  AND deleted_date IS NULL
  AND random() < 0.40;