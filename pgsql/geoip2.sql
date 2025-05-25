-- 创建网络表
DROP table if exists geoip2_network;
create table geoip2_network (
  network cidr not null,
  geoname_id int,
  registered_country_geoname_id int,
  represented_country_geoname_id int,
  is_anonymous_proxy bool,
  is_satellite_provider bool,
  postal_code text,
  latitude numeric,
  longitude numeric,
  accuracy_radius int,
  is_anycast bool
);

-- 导入网络数据
\copy geoip2_network from 'GeoLite2-City-Blocks-IPv4.csv' with (format csv, header);
\copy geoip2_network from 'GeoLite2-City-Blocks-IPv6.csv' with (format csv, header);

-- 查询IP示例
select '192.168.0.0/24'::cidr >>= '192.168.0.1'; -- true
select '192.168.0.0/24'::cidr >>= '192.168.1.1'; -- false
select * from geoip2_network where network >>= '214.0.0.0';

-- 创建索引
create index on geoip2_network using gist (network inet_ops);
select * from geoip2_network net where network >>= '214.0.0.0';

-- 创建位置表
DROP table if exists geoip2_location;
create table geoip2_location (
  geoname_id int not null,
  locale_code text not null,
  continent_code text,
  continent_name text,
  country_iso_code text,
  country_name text,
  subdivision_1_iso_code text,
  subdivision_1_name text,
  subdivision_2_iso_code text,
  subdivision_2_name text,
  city_name text,
  metro_code int,
  time_zone text,
  is_in_european_union bool not null,
  primary key (geoname_id, locale_code)
);

-- 导入位置数据
\copy geoip2_location from 'GeoLite2-City-Locations-en.csv' with (format csv, header);
\copy geoip2_location from 'GeoLite2-City-Locations-zh-CN.csv' with (format csv, header);

-- 查询位置信息
select latitude, longitude, accuracy_radius, continent_name, country_name, subdivision_1_name, city_name
from geoip2_network net
left join geoip2_location location on (
  net.geoname_id = location.geoname_id
  and location.locale_code = 'en'
)
where network >>= '214.0.0.0';

select latitude, longitude, accuracy_radius,
       location.continent_name as location_continent_name,
       location.country_name as location_country_name,
       location.subdivision_1_name as location_subdivision_1_name,
       location.city_name as location_city_name,
       registered_country.continent_name as registered_country_continent_name,
       registered_country.country_name as registered_country_country_name,
       represented_country.continent_name as represented_country_continent_name,
       represented_country.country_name as represented_country_country_name
from geoip2_network net
left join geoip2_location location on (
  net.geoname_id = location.geoname_id
  and location.locale_code = 'en'
)
left join geoip2_location registered_country on (
  net.registered_country_geoname_id = registered_country.geoname_id
  and registered_country.locale_code = 'en'
)
left join geoip2_location represented_country on (
  net.represented_country_geoname_id = represented_country.geoname_id
  and represented_country.locale_code = 'en'
)
where network >>= '214.0.0.0';

