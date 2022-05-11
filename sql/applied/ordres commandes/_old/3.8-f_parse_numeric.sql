CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_PARSE_NUMERIC" (
    arg_string in varchar2,
    ls_out out varchar2
)
AS
    ll_size integer;
    ll_ind integer;
    ll_car number;
BEGIN
    ll_size := length(arg_string);

    if (ll_size is null) then
        ls_out := '';
        return;
    end if;

    for ll_ind in 1..ll_size  LOOP
        ll_car := ascii(substr(arg_string, ll_ind, 1));

        if ll_car > 47 and ll_car < 58 then
            ls_out := ls_out || substr(arg_string, ll_ind, 1);
        end if;
    end loop;
end;
