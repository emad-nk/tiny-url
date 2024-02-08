create table if not exists url
(
    id                  bigserial       not null constraint url_pk primary key,
    base62              varchar(50)     not null,
    original_url        text            not null
);

create unique index if not exists base62_idx on url(base62);
create index if not exists original_url_idx on url(original_url);
