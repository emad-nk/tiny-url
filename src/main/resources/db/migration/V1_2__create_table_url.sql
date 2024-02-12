create table if not exists url
(
    id                             bigserial       not null constraint url_pk primary key,
    tiny_url_without_domain        varchar(50)     not null,
    original_url                   text            not null
);

create unique index if not exists tiny_url_without_domain_idx on url(tiny_url_without_domain);
create index if not exists original_url_idx on url(original_url);
