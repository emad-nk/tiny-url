create table if not exists url
(
    id                             varchar(100)    not null constraint url_pk primary key,
    sequence_id                    bigserial       not null,
    tiny_url_without_domain        varchar(50)     not null,
    original_url                   text            not null
);

create unique index if not exists sequence_id_idx on url(sequence_id);
create unique index if not exists tiny_url_without_domain_idx on url(tiny_url_without_domain);
create index if not exists original_url_idx on url(original_url);
