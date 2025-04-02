DROP table if exists game_users;
CREATE table game_users (
    user_id int,
    created date,
    country varchar
);

COPY game_users FROM '/tmp/game_users.csv' DELIMITER ',' CSV HEADER;

DROP table if exists game_actions;
CREATE table game_actions (
    user_id int,
    action varchar,
    action_date date
);

COPY game_actions FROM '/tmp/game_actions.csv' DELIMITER ',' CSV HEADER;

DROP table if exists game_purchases;
CREATE table game_purchases (
    user_id int,
    purch_date date,
    amount decimal
);

COPY game_purchases FROM '/tmp/game_purchases.csv' DELIMITER ',' CSV HEADER;

DROP table if exists exp_assignment;
CREATE table exp_assignment (
    exp_name varchar,
    user_id int,
    exp_date date,
    variant varchar
);

COPY exp_assignment FROM '/tmp/exp_assignment.csv' DELIMITER ',' CSV HEADER;


SELECT a.variant,
       count(a.user_id) as total_cohorted,
       count(b.user_id) as completions,
       count(b.user_id) * 1.0 / count(a.user_id) as pct_completed
FROM exp_assignment a
LEFT JOIN game_actions b on a.user_id = b.user_id
and b.action = 'onboarding complete'
WHERE a.exp_name = 'Onboarding'
GROUP BY 1;

SELECT variant,
       count(user_id) as total_cohorted,
       avg(amount) as mean_amount,
       stddev(amount) as stddev_amount
FROM (
    SELECT a.variant, a.user_id,
        sum(coalesce(b.amount,0)) as amount
    FROM exp_assignment a
    LEFT JOIN game_purchases b on a.user_id = b.user_id
    WHERE a.exp_name = 'Onboarding'
    GROUP BY 1,2
) a
GROUP BY 1;

SELECT variant,count(user_id) as total_cohorted,
       avg(amount) as mean_amount,
       stddev(amount) as stddev_amount
FROM (
    SELECT a.variant, a.user_id,
        sum(coalesce(b.amount,0)) as amount
    FROM exp_assignment a
    LEFT JOIN game_purchases b on a.user_id = b.user_id
    JOIN game_actions c on a.user_id = c.user_id
    and c.action = 'onboarding complete'
    WHERE a.exp_name = 'Onboarding'
    GROUP BY 1,2
) a GROUP BY 1;

SELECT a.variant,
    count(distinct a.user_id) as total_cohorted,
    count(distinct b.user_id) as purchasers,
    count(distinct b.user_id) * 1.0 / count(distinct a.user_id) as pct_purchased
FROM exp_assignment a
LEFT JOIN game_purchases b on a.user_id = b.user_id
JOIN game_actions c on a.user_id = c.user_id
and c.action = 'onboarding complete'
WHERE a.exp_name = 'Onboarding'
GROUP BY 1;

SELECT variant,
    count(user_id) as total_cohorted,
    avg(amount) as mean_amount,
    stddev(amount) as stddev_amount
FROM (
    SELECT a.variant, a.user_id,
        sum(coalesce(b.amount,0)) as amount
    FROM exp_assignment a
    LEFT JOIN game_purchases b on a.user_id = b.user_id 
    and b.purch_date <= a.exp_date + interval '7 days'
    WHERE a.exp_name = 'Onboarding'
    GROUP BY 1,2
) a GROUP BY 1;


SELECT 
case when a.created between '2020-01-13' and '2020-01-26' then 'pre'
     when a.created between '2020-01-27' and '2020-02-09' then 'post'
     end as variant,
    count(distinct a.user_id) as cohorted,
    count(distinct b.user_id) as opted_in,
    count(distinct b.user_id) * 1.0 / count(distinct a.user_id) as pct_optin,
    count(distinct a.created) as days
FROM game_users a
LEFT JOIN game_actions b on a.user_id = b.user_id 
and b.action = 'email_optin'
WHERE a.created between '2020-01-13' and '2020-02-09'
GROUP BY 1;

SELECT a.country,
    count(distinct a.user_id) as total_cohorted,
    count(distinct b.user_id) as purchasers,
    count(distinct b.user_id) * 1.0 / count(distinct a.user_id) as pct_purchased
FROM game_users a
LEFT JOIN game_purchases b on a.user_id = b.user_id
WHERE a.country in ('United States','Canada')
GROUP BY 1;

