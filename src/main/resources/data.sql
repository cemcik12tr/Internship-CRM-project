---------------------------------------------------------
-- 1. USERS SEED DATA
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
-- 2. PRODUCTS SEED DATA
---------------------------------------------------------
TRUNCATE TABLE public.products RESTART IDENTITY CASCADE;

INSERT INTO public.products (
    catalog_id,
    created_date,
    deleted_date,
    name,
    price,
    stock,
    updated_date,
    is_active
)
SELECT
    (random() * 9 + 1)::int AS catalog_id,
    NOW() - ((random() * 730)::int || ' days')::interval AS created_date,
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
END AS name,
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
END AS stock,
    NOW() - ((random() * 365)::int || ' days')::interval AS updated_date,
    random() > 0.10 AS is_active
FROM generate_series(1, 1000) gs;

-- OUTLIER: Çok pahalı ürünler
UPDATE public.products
SET price = ROUND((25000 + random() * 25000)::numeric, 2)::float8
WHERE id IN (15, 87, 154, 488, 721);

-- OUTLIER: Çok yüksek stok
UPDATE public.products
SET stock = 4000 + (random() * 3000)::int
WHERE id IN (12, 90, 311, 555, 777);

-- Stoku bitmiş ürünler (%8)
UPDATE public.products
SET stock = 0
WHERE random() < 0.08;

-- Veri kalitesi problemi: Negatif stok
UPDATE public.products
SET stock = -5
WHERE id IN (45, 222, 901);

-- Veri kalitesi problemi: Fiyat 0
UPDATE public.products
SET price = 0
WHERE id IN (101, 202, 303);

-- Güncellenmemiş ürünler (%20)
UPDATE public.products
SET updated_date = created_date
WHERE random() < 0.20;

-- Bazı pasif ürünleri silinmiş olarak işaretle
UPDATE public.products
SET deleted_date = NOW() - ((random() * 120)::int || ' days')::interval
WHERE is_active = false
  AND deleted_date IS NULL
  AND random() < 0.40;