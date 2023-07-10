CREATE OR REPLACE FUNCTION GEO_ADMIN.GET_FLUX(arg_ord_ref in varchar2, arg_fou_code in varchar2)
-- indique si  ordre contient des demi-palettes
RETURN varchar2 IS result varchar2(5);

BEGIN

 If arg_fou_code = 'STEFLEMANS' THEN
    result := 'BUK';
    return result;
 end if;

 begin
    select distinct 'BUK' into result
    FROM GEO_ORDRE O
    where O.ORD_REF =arg_ord_ref  and
    exists
        ( select 1
           from     GEO_ORDLIG  L
           where   O.ORD_REF        =  L.ORD_REF      and
                       L.CDE_NB_COL    <  L.PAL_NB_COL and
                       L.FOU_CODE = arg_fou_code);
  exception when others then
        result := 'SA';
   end;




return result;
END;
/

