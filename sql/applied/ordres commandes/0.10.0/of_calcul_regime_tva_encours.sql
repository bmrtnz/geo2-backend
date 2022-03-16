-- AR 12/10/12 création
-- SL 03/01/13 Généralisation du traitement du régime TVA.
--
-- verification des codes pays des fournisseurs pour savoir s'il s'agit d'une opération commerciale "triangulaire" ou du cas
-- particulier de l'Espagne.
-- Retourne chaine vide si OK, message erreur sinon
--
-- Si destination france ou logistique part de France retourne chaine vide et str_regime_tva = str_regime_tva_defaut
--
-- Opération triangulaire :
--                                           Marchandise en provenance et a destination d'un pays CEE hors France
--                                           mention spéciale facture : « Application de l’article 141 de la directive 2006/112/CE du Conseil, du 28 novembre 2006 »
--                                           str_regime_tva = 'T'
-- TVA Locale : Pour l'Espagne
--                                           Marchandise a destination de l'Espagne et provenance entité transitaire Blue Whale en Espagne 
--           BWPRESTALGES, BWPRESSANCHE, BWPRESTRANSI, BWPRESTANSTT, BWPRESTVALEN
--           Les entités fournisseur suceptibles d'utiliser ce regime doivent avoir comme regime TVA 'L'
--                                           str_regime_tva = 'L'
-- Par defaut
--                                           str_regime_tva = str_regime_tva_defaut
-- Melange de regime TVA
--                                           Retourne message d'erreur
--                                           str_regime_tva = ''

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_CALCUL_REGIME_TVA_ENCOURS" (
    arg_ord_ref GEO_ORDRE.ord_ref%type,
    str_regime_tva_defaut varchar2,
	res out number,
    msg out varchar2
) AS
    ls_CEE varchar2(50);
    ls_CEE_client varchar2(50);
    ls_pay_code_client varchar2(50);
    ls_pay_code_fourn varchar2(50);
    ls_pay_code_log varchar2(50);
    ls_CEE_log varchar2(50);
    ls_tvr_code varchar2(50);
    ls_tvr_code varchar2(50);
    ls_surch_type varchar2(50);
    ls_errorMsg varchar2(50);
    ls_ordLog varchar2(50);
    ls_sql varchar2(50);
    ls_sql_fourni varchar2(50) := '';

    ll_tvr_count number;
    ll_count_Fr number;
    ll_count_CEE number;
    ll_count_Transitaire_Esp number;
    ll_count_Transitaire_GB number;
    ll_count_Other number;
    ll_count_total number;
    ll_row number;
    str_regime_tva varchar2(50) := '';
    ls_errorMsg varchar2(50) := '';
    cursor cur_ols is
        select fou_code
        from geo_ordlig
        where ord_ref = arg_ord_ref;
begin

    msg := '';
    res := 0;

    -- Test si le client est en France
    select o.pay_code, p.cee
    into ls_pay_code_client, ls_CEE_client
    from geo_ordre o, geo_pays p
    where o.ord_ref = arg_ord_ref
    and o.pay_code = p.pay_code;

    if ls_pay_code_client = 'FR' then
        -- Regime TVA par defaut
        str_regime_tva := str_regime_tva_defaut;
        res := 1;
        return;
    end if;

    for r in cur_ols
    loop
        ls_sql_fourni := ls_sql_fourni || '''' || r.fou_code || ''',';
    end loop;

    ls_sql_fourni := substr(ls_sql_fourni,1,length(ls_sql_fourni)-1);

    -- ls_sql := 'select distinct F.PAY_CODE, F.TVR_CODE, P.CEE ';
    -- ls_sql := ls_sql || ' from geo_fourni F, geo_pays P ';
    -- ls_sql := ls_sql || ' where F.FOU_CODE in ('+ls_sql_fourni+') ';
    -- ls_sql := ls_sql || ' and F.PAY_CODE = P.PAY_CODE ';

    ll_count_Fr := 0;
    ll_count_Transitaire_Esp := 0;
    ll_count_Transitaire_GB := 0;
    ll_count_Other := 0;
    ll_count_total := 0;
    ll_count_CEE := 0;

    -- compte les origines des pays fournisseurs
    declare
        cursor C1 is
            select distinct F.PAY_CODE, F.TVR_CODE, P.CEE
            from geo_fourni F, geo_pays P
            where F.FOU_CODE in (ls_sql_fourni)
            and F.PAY_CODE = P.PAY_CODE;
    begin
        for f in C1 
        loop
            ll_count_total := ll_count_total + 1;

            select G.PAY_CODE, P.CEE
            into ls_pay_code_log, ls_CEE_log
            from geo_pays P, GEO_ORDLOG L, GEO_GROUPA G
            where l.ord_ref = arg_ord_ref
            and G.PAY_CODE = P.PAY_CODE
            and L.GRP_CODE = G.GRP_CODE;

            /* Traitement écarté le lieu de groupage ne compte pas
            -- si existe groupage on se base sur pays du groupeur
            if ls_pay_code_log <> '' then
                ls_pay_code_fourn = ls_pay_code_log
                ls_CEE = ls_CEE_log
            end if
            */

            if f.CEE = 'O' then
                ll_count_CEE := ll_count_CEE + 1;
            end if;

            case f.pay_code
                when 'FR' then
                    ll_count_Fr := ll_count_Fr + 1;
                when 'ES' then
                    ll_count_Other := ll_count_Other + 1;
                    if f.tvr_code = 'L'  then
                        ll_count_Transitaire_Esp := ll_count_Transitaire_Esp + 1;
                    end if;
                when 'GB' then
                    ll_count_Other := ll_count_Other + 1;
                    if f.tvr_code = 'G'  then
                        ll_count_Transitaire_GB := ll_count_Transitaire_GB + 1;
                    end if;
                else
                    ll_count_Other := ll_count_Other + 1;
            end case;
        end loop;
    end;

    -- Client en Espagne
    if ls_pay_code_client = 'ES' then
        if ll_count_Transitaire_Esp = ll_count_total then
            -- Client en espagne et provenance seulement des entités transitaires -> TVA locale
            str_regime_tva := 'L';
        elsif ll_count_Fr = ll_count_total then
            -- Origine France -> regime par defaut
            str_regime_tva := str_regime_tva_defaut;
        elsif ll_count_CEE = ll_count_total and ll_count_Transitaire_Esp = 0 and ll_count_Fr = 0 then
            -- Origine CEE hors france et autre que entite transitaire-> Operation triangulaire
            str_regime_tva := 'T';
        elsif ll_count_Other = ll_count_total and ll_count_CEE = 0 then
            -- Origine hors CEE -> Export regime stantdart
            str_regime_tva := str_regime_tva_defaut;
        else
            -- Provenances de pays différents
            msg := 'Incompatibilité des fournisseurs, les regimes de TVA sont différents. L''ordre ne peu pas être validé.';
            str_regime_tva := '';
            return;
        end if;
        res := 1;
        return;
    end if;

    -- Client en GB
    if ls_pay_code_client = 'GB' then
        if ll_count_Transitaire_GB = ll_count_total then
            -- Client en espagne et provenance seulement des entités transitaires -> TVA locale
            str_regime_tva := 'G';
        elsif ll_count_Fr = ll_count_total then
            -- Origine France -> regime par defaut
            str_regime_tva := str_regime_tva_defaut;
        elsif ll_count_CEE = ll_count_total and ll_count_Transitaire_GB = 0 and ll_count_Fr = 0 then
            -- Origine CEE hors france et autre que entite transitaire-> Operation triangulaire
            str_regime_tva := 'T';
        elsif ll_count_Other = ll_count_total and ll_count_CEE = 0 then
            -- Origine hors CEE -> Export regime stantdart
            str_regime_tva := str_regime_tva_defaut;
        else
            -- Provenances de pays différents
            msg := 'Incompatibilité des fournisseurs, les regimes de TVA sont différents. L''ordre ne peu pas être validé.';
            str_regime_tva := '';
            return;
        end if;
        res := 1;                  
        return;
    end if;

    -- Client en CEE
    if ls_CEE_client = 'O' then
        if ll_count_Other = ll_count_total and ll_count_CEE = ll_count_total then
            -- Origine CEE hors France
            str_regime_tva := 'T';
        elsif ll_count_Fr = ll_count_total then
            -- Origine France
            str_regime_tva := str_regime_tva_defaut;
        elsif ll_count_Other = ll_count_total and ll_count_CEE = 0 then
            -- origne hors CEE
            str_regime_tva := str_regime_tva_defaut;
        else
            msg := 'Incompatibilité des fournisseurs, les regimes de TVA sont différents. L''ordre ne peu pas être validé.';
            str_regime_tva := '';
            return;
        end if;
        res := 1;
        return;
    end if;

    -- Client hors CEE
    str_regime_tva := str_regime_tva_defaut;

    msg := 'OK';
    res := 1;
    return;

end;