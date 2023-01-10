CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_LITIGE_CTL_CLIENT_INSERT" (
    gs_soc_code in varchar2,
    gs_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_lit_ref in GEO_LITIGE.LIT_REF%TYPE,
    res out number,
    msg out varchar2
)
AS
    ls_ref_client varchar2(50);
    ld_older_litige timestamp;
    ld_date_creat timestamp;
    li_nblitligt45 number := 0;
    li_indice number;
    ls_fou_code varchar2(50);
    ls_pay_code varchar2(50);
BEGIN
    res := 0;
    msg := '';

    If gs_soc_code = 'SA' then

        select GEO_CLIENT_CTL.DAT_OLDER_LITIGE, GEO_CLIENT.CLI_REF
        into ld_older_litige,ls_ref_client
        from GEO_ORDRE,  GEO_CLIENT, GEO_CLIENT_CTL
        where GEO_ORDRE.ORD_REF = gs_ord_ref  			and
        GEO_ORDRE.CLI_REF= GEO_CLIENT.CLI_REF 			and
        GEO_ORDRE.SOC_CODE =GEO_CLIENT.SOC_CODE 	and
        GEO_CLIENT.SOC_CODE = 'SA'								and
        GEO_CLIENT.CLI_REF=  GEO_CLIENT_CTL.CLI_REF;

        declare
            cursor lignes is
                SELECT ll.lil_ref, ll.lca_code, ll.tyt_code, ll.tie_code
                FROM geo_litlig ll
                WHERE ll.lit_ref = arg_lit_ref;
        begin
            for l in lignes loop
                IF l.lca_code = 'T45' then li_nblitligt45 := li_nblitligt45 + 1; end if;

                IF l.tyt_code = 'F' then

                    select PAY_CODE
                    into ls_pay_code
                    FROM GEO_FOURNI
                    where FOU_CODE = l.tie_code;

                    If ls_pay_code <> 'FR' Then
                        res := 0;
                        return;
                    end if;

                End If;

            end loop;
        EXCEPTION when OTHERS then
            msg := 'Echec lors du traitement du litige ' || arg_lit_ref || ' : ' || SQLERRM;
            res := 0;
            return;
        end;

        declare
            l_count number;
        begin
            SELECT count(lil_ref)
            INTO l_count
            FROM geo_litlig
            WHERE lit_ref = arg_lit_ref;
            If li_nblitligt45 = l_count  Then
                res := 0;
                return;
            end if;
        end;

        declare
            ld_date_creat timestamp;
        begin
            SELECT lit_date_creation
            INTO ld_date_creat
            FROM geo_litige
            WHERE lit_ref = arg_lit_ref;

            If  ld_older_litige is null  Then
                delete GEO_CLIENT_CTL
                WHERE 	CLI_REF = ls_ref_client;

                insert into  GEO_CLIENT_CTL(CLI_REF,DAT_OLDER_LITIGE) VALUES (ls_ref_client,ld_date_creat);

                commit;
            End If;
        end;
    end if;

    res := 1;
END;
/

