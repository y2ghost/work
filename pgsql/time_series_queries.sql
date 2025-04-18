-- date类型操作示例
DROP table if exists date_dim;
CREATE table date_dim
as
SELECT gen_date::date ,
    to_char(gen_date,'yyyymmdd')::int as date_key,
    date_part('day',gen_date)::int as day_of_month,
    date_part('doy',gen_date)::int as day_of_year,
    date_part('dow',gen_date)::int as day_of_week,
    trim(to_char(gen_date, 'Day')) as day_name,
    trim(to_char(gen_date, 'Dy')) as day_short_name,
    date_part('week',gen_date)::int as week_number,
    to_char(gen_date,'W')::int as week_of_month,
    date_trunc('week',gen_date)::date as week,
    date_part('month',gen_date)::int as month_number,
    trim(to_char(gen_date, 'Month')) as month_name,
    trim(to_char(gen_date, 'Mon')) as month_short_name,
    date_trunc('month',gen_date)::date as first_day_of_month,
    (date_trunc('month',gen_date) + interval '1 month' - interval '1 day')::date as last_day_of_month,
    date_part('quarter',gen_date)::int as quarter_number,
    trim('Q' || date_part('quarter',gen_date)::int) as quarter_name,
    date_trunc('quarter',gen_date)::date as first_day_of_quarter,
    (date_trunc('quarter',gen_date) + interval '3 months' - interval '1 day')::date as last_day_of_quarter,
    date_part('year',gen_date)::int as year,
    date_part('decade',gen_date)::int * 10 as decade,
    date_part('century',gen_date)::int as centurys
FROM generate_series('1770-01-01'::date, '2030-12-31'::date, '1 day') as gen_date;

-- 零售信息表
DROP table if exists retail_sales;
CREATE table retail_sales (
    sales_month date,
    naics_code varchar,
    kind_of_business varchar,
    reason_for_null varchar,
    sales decimal
);

COPY retail_sales
FROM '/tmp/us_retail_sales.csv'
DELIMITER ','
CSV HEADER;

-- 统计零售和食物时序分析趋势图
SELECT sales_month,sales
FROM retail_sales
WHERE kind_of_business = 'Retail and food services sales, total'
ORDER BY 1;

-- 按年统计
SELECT date_part('year',sales_month) as sales_year, sum(sales) as sales
FROM retail_sales
WHERE kind_of_business = 'Retail and food services sales, total'
GROUP BY 1
ORDER BY 1;

-- 多类别按年统计
SELECT date_part('year',sales_month) as sales_year, kind_of_business, sum(sales) as sales
FROM retail_sales
WHERE kind_of_business in ('Book stores','Sporting goods stores','Hobby, toy, and game stores')
GROUP BY 1,2
ORDER BY 1,2;

SELECT sales_month, kind_of_business, sales
FROM retail_sales
WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
ORDER BY 1,2;

SELECT date_part('year',sales_month) as sales_year, kind_of_business, sum(sales) as sales
FROM retail_sales
WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
GROUP BY 1,2;

SELECT date_part('year',sales_month) as sales_year,
    sum(case when kind_of_business = 'Women''s clothing stores' then sales end) as womens_sales,
    sum(case when kind_of_business = 'Men''s clothing stores' then sales end) as mens_sales
FROM retail_sales
WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
GROUP BY 1
ORDER BY 1;

SELECT sales_year,
    womens_sales - mens_sales as womens_minus_mens,
    mens_sales - womens_sales as mens_minus_womens
FROM (
    SELECT date_part('year',sales_month) as sales_year,
        sum(case when kind_of_business = 'Women''s clothing stores' then sales end) as womens_sales,
        sum(case when kind_of_business = 'Men''s clothing stores' then sales end) as mens_sales
        FROM retail_sales
        WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
        and sales_month <= '2019-12-01'
        GROUP BY 1
) a ORDER BY 1;

SELECT date_part('year', sales_month) as sales_year,
    sum(case when kind_of_business = 'Women''s clothing stores' then sales end)
    - sum(case when kind_of_business = 'Men''s clothing stores' then sales end) as womens_minus_mens
FROM retail_sales
WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
and sales_month <= '2019-12-01'
GROUP BY 1
ORDER BY 1;

SELECT sales_year, womens_sales / mens_sales as womens_times_of_mens
FROM (
    SELECT date_part('year',sales_month) as sales_year,
        sum(case when kind_of_business = 'Women''s clothing stores' then sales end) as womens_sales,
        sum(case when kind_of_business = 'Men''s clothing stores' then sales end) as mens_sales
        FROM retail_sales
        WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
        and sales_month <= '2019-12-01'
        GROUP BY 1
) a ORDER BY 1;

SELECT sales_year,(womens_sales / mens_sales - 1) * 100 as womens_pct_of_mens
FROM (
    SELECT date_part('year',sales_month) as sales_year,
        sum(case when kind_of_business = 'Women''s clothing stores' then sales end) as womens_sales,
        sum(case when kind_of_business = 'Men''s clothing stores' then sales end) as mens_sales
        FROM retail_sales
        WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
        and sales_month <= '2019-12-01'
        GROUP BY 1
) a ORDER BY 1 ;

-- 总比率计算
SELECT sales_month, kind_of_business, sales * 100 / total_sales as pct_total_sales
FROM (
    SELECT a.sales_month,
        a.kind_of_business,
        a.sales,
        sum(b.sales) as total_sales
        FROM retail_sales a
        JOIN retail_sales b on a.sales_month = b.sales_month
        and b.kind_of_business in ('Men''s clothing stores' ,'Women''s clothing stores')
        WHERE a.kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
        GROUP BY 1,2,3
) aa ORDER BY 1,2;

SELECT sales_month, kind_of_business, sales,
    sum(sales) over (partition by sales_month) as total_sales,
    sales * 100 / sum(sales) over (partition by sales_month) as pct_total
FROM retail_sales 
WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
ORDER BY 1;

SELECT sales_month,kind_of_business,sales * 100 / yearly_sales as pct_yearly
FROM (
    SELECT a.sales_month,
        a.kind_of_business,
        a.sales,
        sum(b.sales) as yearly_sales
        FROM retail_sales a
        JOIN retail_sales b on date_part('year',a.sales_month) = date_part('year',b.sales_month)
        and a.kind_of_business = b.kind_of_business
        and b.kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
        WHERE a.kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
        GROUP BY 1,2,3
) aa ORDER BY 1,2;

SELECT sales_month, kind_of_business, sales,
    sum(sales) over (partition by date_part('year',sales_month), kind_of_business) as yearly_sales,
    sales * 100 / sum(sales) over (partition by date_part('year',sales_month), kind_of_business) as pct_yearly
FROM retail_sales 
WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')
ORDER BY 1,2;

SELECT sales_year, sales ,first_value(sales) over (order by sales_year) as index_sales
FROM (
    SELECT date_part('year',sales_month) as sales_year,sum(sales) as sales
    FROM retail_sales
    WHERE kind_of_business = 'Women''s clothing stores'
    GROUP BY 1
) a;

SELECT sales_year, sales,(sales / index_sales - 1) * 100 as pct_from_index
FROM (
    SELECT date_part('year',aa.sales_month) as sales_year,
        bb.index_sales,
        sum(aa.sales) as sales
        FROM retail_sales aa
        JOIN (
            SELECT first_year, sum(a.sales) as index_sales
            FROM retail_sales a JOIN (
                SELECT min(date_part('year',sales_month)) as first_year
                FROM retail_sales
                WHERE kind_of_business = 'Women''s clothing stores') b
            on date_part('year',a.sales_month) = b.first_year
            WHERE a.kind_of_business = 'Women''s clothing stores'
            GROUP BY 1
        ) bb on 1 = 1
        WHERE aa.kind_of_business = 'Women''s clothing stores'
        GROUP BY 1,2
) aaa;

SELECT sales_year, kind_of_business, sales,
    (sales / first_value(sales) over (partition by kind_of_business order by sales_year) - 1) * 100 as pct_from_index
FROM (
    SELECT date_part('year',sales_month) as sales_year,
        kind_of_business,
        sum(sales) as sales
        FROM retail_sales
        WHERE kind_of_business in ('Men''s clothing stores','Women''s clothing stores')  and sales_month <= '2019-12-31'
GROUP BY 1,2) a;

-- 计算滚动时间窗
SELECT a.sales_month,a.sales,b.sales_month as rolling_sales_month,b.sales as rolling_sales
FROM retail_sales a
JOIN retail_sales b on a.kind_of_business = b.kind_of_business
 and b.sales_month between a.sales_month - interval '11 months'
 and a.sales_month
 and b.kind_of_business = 'Women''s clothing stores'
WHERE a.kind_of_business = 'Women''s clothing stores'
and a.sales_month = '2019-12-01';

SELECT a.sales_month, a.sales, avg(b.sales) as moving_avg,count(b.sales) as records_count
FROM retail_sales a
JOIN retail_sales b on a.kind_of_business = b.kind_of_business
 and b.sales_month between a.sales_month - interval '11 months'
 and a.sales_month
 and b.kind_of_business = 'Women''s clothing stores'
WHERE a.kind_of_business = 'Women''s clothing stores'
and a.sales_month >= '1993-01-01'
GROUP BY 1,2
ORDER BY 1;

SELECT sales_month,
    avg(sales) over (order by sales_month rows between 11 preceding and current row) as moving_avg,
    count(sales) over (order by sales_month rows between 11 preceding and current row) as records_count
FROM retail_sales
WHERE kind_of_business = 'Women''s clothing stores';

SELECT a.gen_date, b.sales_month, b.sales
FROM date_dim a
JOIN (
    SELECT sales_month, sales
    FROM retail_sales
    WHERE kind_of_business = 'Women''s clothing stores'
    and date_part('month',sales_month) in (1,7)
) b on b.sales_month between a.gen_date - interval '11 months' and a.gen_date
WHERE a.gen_date = a.first_day_of_month and a.gen_date between '1993-01-01' and '2020-12-01'
ORDER BY 1,2;

SELECT a.gen_date, avg(b.sales) as moving_avg, count(b.sales) as records
FROM date_dim a
JOIN (
    SELECT sales_month, sales
    FROM retail_sales
    WHERE kind_of_business = 'Women''s clothing stores' and date_part('month',sales_month) in (1,7)
) b on b.sales_month between a.gen_date - interval '11 months' and a.gen_date
WHERE a.gen_date = a.first_day_of_month and a.gen_date between '1993-01-01' and '2020-12-01'
GROUP BY 1
ORDER BY 1;

SELECT a.sales_month, avg(b.sales) as moving_avg
FROM (
    SELECT distinct sales_month
    FROM retail_sales
    WHERE sales_month between '1993-01-01' and '2020-12-01'
) a JOIN retail_sales b
ON b.sales_month between a.sales_month - interval '11 months' and a.sales_month
and b.kind_of_business = 'Women''s clothing stores' 
GROUP BY 1;

-- 计算累积值
SELECT sales_month, sales,
    sum(sales) over (partition by date_part('year',sales_month) order by sales_month) as sales_ytd
FROM retail_sales
WHERE kind_of_business = 'Women''s clothing stores';

SELECT a.sales_month, a.sales, sum(b.sales) as sales_ytd
FROM retail_sales a
JOIN retail_sales b
ON date_part('year',a.sales_month) = date_part('year',b.sales_month)
 and b.sales_month <= a.sales_month
 and b.kind_of_business = 'Women''s clothing stores'
WHERE a.kind_of_business = 'Women''s clothing stores'
GROUP BY 1,2;

-- 季节性分析
SELECT kind_of_business, sales_month, sales,
    lag(sales_month) over (partition by kind_of_business order by sales_month) as prev_month,
    lag(sales) over (partition by kind_of_business order by sales_month) as prev_month_sales
FROM retail_sales
WHERE kind_of_business = 'Book stores';

SELECT kind_of_business, sales_month, sales,
    (sales / lag(sales) over (partition by kind_of_business order by sales_month) - 1) * 100 as pct_growth_from_previous
FROM retail_sales
WHERE kind_of_business = 'Book stores';

SELECT sales_year, yearly_sales,
    lag(yearly_sales) over (order by sales_year) as prev_year_sales,
    (yearly_sales / lag(yearly_sales) over (order by sales_year) -1) * 100 as pct_growth_from_previous
FROM (
    SELECT date_part('year',sales_month) as sales_year,
        sum(sales) as yearly_sales
        FROM retail_sales
        WHERE kind_of_business = 'Book stores'
        GROUP BY 1) a;

-- 同期对比
SELECT sales_month, date_part('month',sales_month)
FROM retail_sales
WHERE kind_of_business = 'Book stores';

SELECT sales_month,sales,
    lag(sales_month) over (partition by date_part('month',sales_month) order by sales_month) as prev_year_month,
    lag(sales) over (partition by date_part('month',sales_month) order by sales_month) as prev_year_sales
FROM retail_sales
WHERE kind_of_business = 'Book stores';

SELECT sales_month, sales,
    sales - lag(sales) over (partition by date_part('month',sales_month) order by sales_month) as absolute_diff,
    (sales / lag(sales) over (partition by date_part('month',sales_month) order by sales_month) - 1) * 100 as pct_diff
FROM retail_sales
WHERE kind_of_business = 'Book stores';

SELECT date_part('month',sales_month) as month_number,
    to_char(sales_month,'Month') as month_name,
    max(case when date_part('year',sales_month) = 1992 then sales end) as sales_1992,
    max(case when date_part('year',sales_month) = 1993 then sales end) as sales_1993,
    max(case when date_part('year',sales_month) = 1994 then sales end) as sales_1994
FROM retail_sales
WHERE kind_of_business = 'Book stores' and sales_month between '1992-01-01' and '1994-12-01'
GROUP BY 1,2;

SELECT sales_month, sales,
    lag(sales,1) over (partition by date_part('month',sales_month) order by sales_month) as prev_sales_1,
    lag(sales,2) over (partition by date_part('month',sales_month) order by sales_month) as prev_sales_2,
    lag(sales,3) over (partition by date_part('month',sales_month) order by sales_month) as prev_sales_3
FROM retail_sales
WHERE kind_of_business = 'Book stores';

SELECT sales_month, sales,
    sales / avg(sales) over (partition by date_part('month',sales_month) order by sales_month rows between 3 preceding and 1 preceding) as pct_of_prev_3
FROM retail_sales
WHERE kind_of_business = 'Book stores';

