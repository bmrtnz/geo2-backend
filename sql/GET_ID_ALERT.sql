CREATE OR REPLACE FUNCTION GEO_ADMIN.GET_ID_ALERT(arg_sco_code in varchar2)
RETURN NUMBER IS result NUMBER;

BEGIN
begin
select  K_ALERT into result
from ( select *
from (
select  1 "TRI", A.SCO_CODE,A.K_ALERT
from GEO_ALERT A
where A.VALIDE = 'O' and
         sysdate > A.DAT_DEBUT and
         (A.DAT_FIN > sysdate or A.DAT_FIN IS NULL)  and
          A.COD_TYP_ALERT = 'W' and
          (A.SCO_CODE =arg_sco_code OR  A.SCO_CODE IS NULL)
UNION
select  2 "TRI" , A.SCO_CODE,A.K_ALERT
from GEO_ALERT A
where A.VALIDE = 'O' and
         sysdate > A.DAT_DEBUT and
         (A.DAT_FIN > sysdate or A.DAT_FIN IS NULL)  and
          A.COD_TYP_ALERT = 'S' and
          (A.SCO_CODE = arg_sco_code OR  A.SCO_CODE IS NULL)
UNION
select  3 "TRI"  , A.SCO_CODE,A.K_ALERT
from GEO_ALERT A
where A.VALIDE = 'O' and
         sysdate > A.DAT_DEBUT and
         (A.DAT_FIN > sysdate or A.DAT_FIN IS NULL)  and
          A.COD_TYP_ALERT = 'I' and
          (A.SCO_CODE =arg_sco_code OR  A.SCO_CODE IS NULL)  )  G_ALERT
         ORDER BY TRI, SCO_CODE) G_RETURN
             where ROWNUM = 1 ;
 exception when others then
    result :=0;
   end;
return result;
END;
/

