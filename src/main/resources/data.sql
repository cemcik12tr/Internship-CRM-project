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

    -- Plain text password matching LoginRequest validation rules
    'Pass1234' AS password,

    (random() < 0.10) AS is_locked,
    (random() * 2)::int AS wrong_attempts,
    (random() * 4 + 1)::int AS title_id

FROM generate_series(1, 1000) gs;