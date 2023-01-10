--of_verif_litlig
-- vérifie les lignes du litige éventuel s'il y a eu modif
-- les poids en litige doivent être saisis
-- ceci est utilisé à chaque demande de sauvegarde (of_sauve_ordre et of_sauve_litige)
-- AR 19/04/10 création après de multiples essais dans les triggers
-- BA 11/05/16 ajout du controle sur le poids du litige qui ne peut pas être superieur au poids net
CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_VERIF_LITLIG" (
    arg_lit_ref in GEO_LITIGE.LIT_REF%TYPE,
    res out number,
    msg out varchar2
)
AS
    ll_ind number;
    ll_count_pds number;
    ll_count_lcq number;
    ll_count_lca number;
    ls_mess_pds varchar2(50) :='~n - des poids de litige ';
    ls_mess_lca varchar2(50) :='~n - la cause';
    ls_mess_lcq varchar2(50) :='~n - l''action';
    ls_mess varchar2(50) := 'Il manque dans les lignes litige :';
    ls_mess_pds_sup varchar2(50) :='Le poids du litige ne peut être superieur au poids net.';
BEGIN
    res := 0;
    msg := '';

    declare
        cursor lignes is
            SELECT ll.lil_ref, ltg.num_version, ll.cli_pds_lit, ol.exp_pds_net, ll.lca_code, ll.lcq_code
            FROM geo_litlig ll
            JOIN geo_litige ltg on ltg.lit_ref = ll.lit_ref
            JOIN geo_ordlig ol on ll.orl_ref = ol.orl_ref
            WHERE ll.lit_ref = arg_lit_ref;
    begin
        for l in lignes loop

            iF l.num_version is null Then
                If  l.exp_pds_net is not null and l.cli_pds_lit is not null and   (abs(l.exp_pds_net) - abs(l.cli_pds_lit)) < - 1 Then
                    msg := 'erreur ' || ls_mess_pds_sup;
                    res := 0;
                    return;
                End IF;
            End IF;

            If l.num_version is null Then
                if l.cli_pds_lit is null then
                    l.cli_pds_lit := 0;
                end if;
                if l.cli_pds_lit = 0 then
                    ll_count_pds := ll_count_pds + 1;
                end if;
            End if;

            If l.lca_code is null Then
                l.lca_code :='';
            end if;
            if l.lca_code = ''      then
              ll_count_lca := ll_count_lca + 1;
            end if;

            If l.lcq_code is null Then
                l.lcq_code :='';
            end if;
            if l.lcq_code = ''      then
                ll_count_lcq := ll_count_lcq + 1;
            end if;
        end loop;

        if ll_count_pds <> 0  or ll_count_lcq <> 0 or ll_count_lca <> 0 then

            If ll_count_pds <> 0 Then
                msg := ls_mess || ls_mess_pds;
            end if;

            If ll_count_lca <> 0 Then
                msg := ls_mess || ls_mess_lca;
            end if;

            If ll_count_lcq <> 0 Then
                msg := ls_mess || ls_mess_lcq;
            end if;

            msg := 'erreur: ' || msg;
            res := 0;
            return;
        end if;

    EXCEPTION when OTHERS then
        msg := 'Erreur lors de la verification du litige ' || arg_lit_ref || ' : ' || SQLERRM;
        res := 0;
        rollback;
        return;
    end;

    res := 1;
END;
/

