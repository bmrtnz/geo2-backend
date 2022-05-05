CREATE OR REPLACE PROCEDURE OF_VERIF_PALETTE (
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    is_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    is_cur_cen_code IN varchar2,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_cen_gest_code GEO_ENTREP.GEST_CODE%TYPE;
    ls_sco_code GEO_ORDRE.SCO_CODE%TYPE;
    ls_typ_ordre GEO_ORDRE.TYP_ORDRE%TYPE;
    ls_pal_code_ref GEO_ENTREP.PAL_CODE%TYPE;
    ls_dim_code_ref GEO_PALETT.DIM_CODE%TYPE;
    ls_tcl_code GEO_CLIENT.TCL_CODE%TYPE;
    lb_chep_lpr boolean;
    ls_substitut boolean;

    ls_pal_code_lig GEO_PALETT.PAL_CODE%TYPE;
    ls_dim_code_lig GEO_PALETT.DIM_CODE%TYPE;
    ls_gest_code_lig GEO_PALETT.GEST_CODE%TYPE;
    ll_count number;

    cursor CT (ref_ordre GEO_ORDRE.ORD_REF%type)
        IS
        select L.PAL_CODE, P.DIM_CODE, P.GEST_CODE
        from geo_ordlig L, geo_espece E, geo_article A, geo_palett P
        where      L.ord_ref = ref_ordre
          and         L.art_ref = A.art_ref
          and         A.esp_code = E.esp_code
          and        E.gen_code = 'F'
          and         P.PAL_CODE = L.PAL_CODE;
BEGIN
    -- correspond à of_verif_palette.pbl
    res := 0;
    msg := '';

    select TYP_ORDRE into ls_typ_ordre from GEO_ORDRE
    where ORD_REF = is_ord_ref;

    select E.gest_code,O.sco_code,E.pal_code,P.dim_code,C.tcl_code
    into ls_cen_gest_code, ls_sco_code, ls_pal_code_ref, ls_dim_code_ref, ls_tcl_code
    from  geo_entrep E, geo_ordre O, geo_palett P, geo_client C
    where 	E.cen_ref = O.cen_ref
      and 		O.ord_ref = is_ord_ref
      and 		P.pal_code (+)= E.pal_code
      and 		O.CLI_REF = C.CLI_REF;

    -- LLEF Accepter aussi les palettes LPR si palette par défaut est du CHEP
    if ls_cen_gest_code = 'CHEP' then
        lb_chep_lpr := TRUE;
    end if;
    -- Fin LLEF

    if substr(is_cur_cen_code, 1, 8) = 'PREORDRE' OR (is_soc_code = 'BWS'  and ls_sco_code <> 'GB') OR ls_sco_code='PAL' OR ls_dim_code_ref ='-' or
        is_soc_code = 'QUP'  or is_soc_code = 'IMP' or ls_sco_code = 'IND' OR ls_typ_ordre ='RGP' then
        return;
    end if;

    open CT (is_ord_ref);
    ll_count := 0;

    loop
        fetch CT into ls_pal_code_lig, ls_dim_code_lig, ls_gest_code_lig;
        EXIT WHEN CT%notfound;

        -- deb llef
        If (lb_chep_lpr = TRUE and ls_gest_code_lig = 'LPR' and ls_dim_code_lig = ls_dim_code_ref) Then
            ls_substitut := true;
        else
            If (lb_chep_lpr = TRUE and ls_gest_code_lig = 'LPR' and ls_dim_code_lig <> ls_dim_code_ref) Then
                msg := 'la palette ' || ls_pal_code_lig || ' n''est pas autorisée , la palette ' || ls_pal_code_lig || ' est une palette LPR dans une dimension différente de la CHEP définie pour cet entrepot ';
                return;
            end if;
        end if;
        -- fin llef

        If ls_pal_code_lig <> ls_pal_code_ref and ls_substitut = false and ls_pal_code_ref is not null and ls_pal_code_ref <> '-' and ls_pal_code_ref <> '' Then
            If (ls_sco_code ='F' and (ls_tcl_code  = 'GROSSC' or ls_tcl_code  = 'GROSMA')) Then
                msg := 'la palette ' || ls_pal_code_lig || 'n''est pas celle par défaut , la palette ' || ls_pal_code_ref || 'est définie pour cet entrepot';
                return;
            Else
                msg := 'la palette ' || ls_pal_code_lig || ' n''est pas autorisée , la palette ' || ls_pal_code_ref || ' est définie uniquement pour cet entrepot ';
                return;
            End If;
        End If;

        If ls_pal_code_lig = '-' or ls_pal_code_lig is null Then
            If (ls_sco_code = 'F' and (ls_tcl_code = 'GROSSC' or ls_tcl_code  = 'GROSMA')) Then
                msg := 'un type de palette ' || ls_pal_code_lig || ' n''est pas renseignée';
                return;
            else
                msg := 'un type de palette ' || ls_pal_code_lig || 'n''est pas renseignée~rDemande annulée';
                return;
            End If;
        End If;
    end loop;
    close CT;

    res := 1;
end OF_VERIF_PALETTE;
/
