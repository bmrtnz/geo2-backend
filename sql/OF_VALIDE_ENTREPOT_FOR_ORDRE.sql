DROP PROCEDURE GEO_ADMIN.OF_VALIDE_ENTREPOT_FOR_ORDRE;

CREATE OR REPLACE PROCEDURE GEO_ADMIN."OF_VALIDE_ENTREPOT_FOR_ORDRE" (
 code_entrepot in varchar2,
 res out number,
 msg out varchar2
)
AS
  ls_cli_code varchar2(20);
  ls_cli_ref varchar2(6);
  ll_nbj_litige_limite number;
  ld_dat_older_litige date;
  ll_nbj_litige number;
  ls_nordre_litige varchar2(6);
  ls_client_valide varchar2(1);
  ls_vente_commission varchar2(1);
  ldate_older date;
  ll_delai_baf number;
  ll_rc number;
  ls_nordre varchar2(6);
  ls_nordre_txt varchar2(200);

  cursor cur_delaibaf is
  SELECT  O.VENTE_COMMISSION,
      MIN(O.LIVDATP),
      C.DELAI_BAF
  FROM
      GEO_ORDRE O,
      GEO_CLIENT C
  WHERE
      C.CLI_REF = ls_cli_ref AND
      O.FLBAF = 'N' AND
      O.CLI_REF = C.CLI_REF  AND
      O.TOTVTE > 0  and
      not exists(select 1
                    from GEO_ORDRE_BUK_SA
                    where GEO_ORDRE_BUK_SA.ORD_REF_BUK = O.ORD_REF)
  GROUP BY
      O.VENTE_COMMISSION,
      C.DELAI_BAF,
      C.VALIDE
  ORDER BY O.VENTE_COMMISSION;

  cursor cur_nordre is
  SELECT O.NORDRE
             FROM     GEO_ORDRE O,
                        GEO_CLIENT C
            WHERE C.CLI_REF = ls_cli_ref                    AND
                O.FLBAF = 'N'                                     AND
                O.CLI_REF = C.CLI_REF                          AND
                O.TOTVTE > 0                                     AND
                TO_CHAR(O.LIVDATP, 'yyyy/mm/dd') = TO_CHAR( ldate_older,  'yyyy/mm/dd');
BEGIN
  -- Vérifie si la création de l'ordre pour l'entrepot est autorisé

  msg := 'ok';
  res := 0;

  -- PREORDRE
  select C.CLI_CODE into ls_cli_code
  from GEO_CLIENT C,GEO_ENTREP E
  where E.CEN_REF = code_entrepot and E.CLI_REF = C.CLI_REF;

  if substr(ls_cli_code, 1, 8) = 'PREORDRE' Then
     res := 1;
     return;
    end if;

    -- LITIGE non clôturés
    begin
    select distinct C.CLI_REF, C.NBJ_LITIGE_LIM, CL.DAT_OLDER_LITIGE
  into ls_cli_ref, ll_nbj_litige_limite, ld_dat_older_litige
  from GEO_CLIENT C ,GEO_CLIENT_CTL CL
  where C.CLI_REF = CL.CLI_REF and exists (SELECT 1 FROM GEO_ENTREP E WHERE E.CEN_REF = code_entrepot and C.CLI_REF = E.CLI_REF);

  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      ld_dat_older_litige := null; -- permet de passer à la suite.
  end;

  if ld_dat_older_litige is not null Then
      ll_nbj_litige := trunc(systimestamp) - trunc(ld_dat_older_litige);

    if ll_nbj_litige_limite is not null and ll_nbj_litige_limite > 0 Then
          If    ll_nbj_litige > ll_nbj_litige_limite Then
            begin
              select NORDRE    into ls_nordre_litige
              from GEO_ORDRE, GEO_LITIGE
              where GEO_ORDRE.CLI_REF = ls_cli_ref AND
                    GEO_LITIGE.ORD_REF_ORIGINE = GEO_ORDRE.ORD_REF and
                    GEO_LITIGE.LIT_DATE_CREATION =ld_dat_older_litige;

              msg := 'L''ordre ' || ls_nordre_litige || ' a un litige non cloturé à plus de ' || ll_nbj_litige_limite || ' jours pour ce client.\r\nCréation de l''ordre annulée.';
              res := 0;
              return;
               EXCEPTION
    WHEN NO_DATA_FOUND THEN  NULL;
            end;
          End If;
      End If;
  end If;

  -- CLIENT toujours valide
  select distinct    C.CLI_REF, C.VALIDE
  into ls_cli_ref, ls_client_valide
  from GEO_CLIENT C
  WHERE exists (SELECT 1 FROM GEO_ENTREP E WHERE E.CEN_REF = code_entrepot and C.CLI_REF = E.CLI_REF);

  if ls_client_valide = 'N' Then
      msg := 'Le client n''est plus valide. Création de l''ordre annulée.';
      res := 0;
      return;
  End If;

  -- Contrôles des BAF
  open cur_delaibaf;

  LOOP
    fetch cur_delaibaf into ls_vente_commission, ldate_older, ll_delai_baf;
    EXIT WHEN cur_delaibaf%NOTFOUND;

    ll_rc := trunc(systimestamp) - trunc(ldate_older);
      -- delai supplementaire de 20 jours pour les ventes à la commission
      if ls_vente_commission = 'O' then
        ll_delai_baf := ll_delai_baf + 8;
    end if;

      if ll_rc > ll_delai_baf then
        open cur_nordre;
            loop
              fetch cur_nordre into ls_nordre;
        EXIT WHEN cur_nordre%NOTFOUND;

              If ls_nordre is null Then
                ls_nordre := '';
          end if;
            ls_nordre_txt := ls_nordre_txt || ls_nordre || ' ';
                fetch  cur_nordre into ls_nordre;
            end loop;
            close cur_nordre;

      msg := 'Il existe l''ordre (' || trim(ls_nordre_txt) || ') de plus de ' || ll_delai_baf || ' jours qui n''est pas passé ''Bon à facturer'' pour ce client.\r\nCréation de l''ordre annulée.';
      res := 0;
      return;
      end if;

  fetch cur_delaibaf into ls_vente_commission, ldate_older, ll_delai_baf;
  END LOOP;
  close cur_delaibaf;

  res := 1;
  msg := 'OK';

EXCEPTION
    WHEN NO_DATA_FOUND THEN null;
     msg := 'not found.';
     res := 0;
     return;
end;--------------------------
-- \3.1-f_calcul_qte.sql
--------------------------
/
