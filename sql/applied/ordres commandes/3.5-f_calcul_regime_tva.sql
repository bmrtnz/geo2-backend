-- AB (17/03/22) : requete commentée qui sert pas (ligne 60)
CREATE OR REPLACE PROCEDURE GEO_ADMIN."F_CALCUL_REGIME_TVA" (
    arg_ord_ref in GEO_ORDLIG.ORD_REF%TYPE,
    str_regime_tva_defaut in varchar2,
    str_regime_tva in out varchar2,
    msg out varchar2
)
AS
    ls_CEE varchar2(50);
    ls_CEE_client varchar2(50);
    ls_pay_code_client varchar2(50);
    ls_pay_code_fourn varchar2(50);
    ls_pay_code_log varchar2(50);
    ls_CEE_log varchar2(50);
    ls_tvr_code varchar2(50);
    ls_fou_TVR varchar2(50);
    ll_tvr_count number;
    ll_count_Fr number := 0;
    ll_count_CEE number := 0;
    ll_count_Transitaire_Esp number := 0;
    ll_count_Transitaire_GB number := 0;
    ll_count_Other number := 0;
    ll_count_total number := 0;
    ls_surch_type varchar2(50);
    ls_errorMsg varchar2(50);
    ls_ordLog varchar2(50);
    CURSOR C1 (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select F.PAY_CODE, F.TVR_CODE, P.CEE
        from geo_ordlig O, geo_fourni F, geo_pays P
        where O.ord_ref = ref_ordre
          and O.FOU_CODE = F.FOU_CODE
          and F.PAY_CODE = P.PAY_CODE;
BEGIN
    str_regime_tva := '';
    msg := '';
    -- Test si le client est en France
    select o.pay_code, p.cee into ls_pay_code_client, ls_CEE_client
    from geo_ordre o, geo_pays p
    where o.ord_ref = arg_ord_ref
      and o.pay_code = p.pay_code;
    if ls_pay_code_client = 'FR' then
        -- Regime TVA par defaut
        str_regime_tva := str_regime_tva_defaut;
        return;
    end if;
    -- compte les origines des pays fournisseurs
    OPEN C1(arg_ord_ref);
    LOOP
        fetch C1 into ls_pay_code_fourn, ls_fou_TVR, ls_CEE;
        EXIT WHEN C1%notfound;
        ll_count_total := ll_count_total + 1;
        /*  select G.PAY_CODE, P.CEE into ls_pay_code_log, ls_CEE_log
            from geo_pays P, GEO_ORDLOG L, GEO_GROUPA G
            where l.ord_ref = arg_ord_ref
            and G.PAY_CODE = P.PAY_CODE
            and L.GRP_CODE = G.GRP_CODE;*/
        if ls_CEE = 'O' then
            ll_count_CEE := ll_count_CEE + 1;
        end if;
        CASE ls_pay_code_fourn
            WHEN 'FR' THEN
                ll_count_Fr := ll_count_Fr + 1;
            WHEN 'ES' THEN
                ll_count_Other := ll_count_Other + 1;
                if ls_fou_TVR = 'L'  then
                    ll_count_Transitaire_Esp := ll_count_Transitaire_Esp + 1;
                end if;
            WHEN 'GB' THEN
                ll_count_Other := ll_count_Other + 1;
                if ls_fou_TVR = 'G' then
                    ll_count_Transitaire_GB := ll_count_Transitaire_GB + 1;
                end if;
            ELSE
                ll_count_Other := ll_count_Other + 1;
        END CASE;
    END LOOP;
    CLOSE C1;
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
        end if;
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
        end if;
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
        end if;
        return;
    end if;
    -- Client hors CEE
    str_regime_tva := str_regime_tva_defaut;
END;