CREATE OR REPLACE PROCEDURE F_CALCUL_FOB_V2(
    is_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ld_TOTVTE number;
    ld_FRET number;
    ld_fob number;
    ld_FRET_TOTAL number;
    ls_esp_code GEO_ORDLIG.ESP_CODE%TYPE;
    ld_tot_vte_esp number;
    ld_tot_rem_esp number;
    ld_tot_rep_esp number;
    li_nb_fob number;

    CURSOR C_FOB (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select
                O.TOTVTE - case when O.TOTREM is not null then O.TOTREM else 0 end ,
                CASE WHEN F.DEV_TX <> 0 THEN F.MONTANT * F.DEV_TX ELSE 0 END AS FRET_EUR
        from
            geo_ordre O,
            (select ORD_REF, MONTANT, DEV_TX FROM geo_ordfra where ORD_REF =  ref_ordre AND FRA_CODE = 'FRET') F
        where
                O.ord_ref = ref_ordre and
                F.ORD_REF (+)= O.ORD_REF;
    CURSOR C_fob_mont (ref_ordre GEO_ORDRE.ORD_REF%TYPE)
    IS
        select OL.ESP_CODE,
               sum(OL.TOTVTE),
               SUM(OL.TOTREM),
               round(sum(OL.TOTVTE)/O.TOTVTE,2)
        from GEO_ORDLIG OL ,
             GEO_ORDRE O
        where O.ORD_REF = ref_ordre and
                O.ORD_REF = OL.ORD_REF
        group by OL.ESP_CODE, O.TOTVTE ;
BEGIN
    -- correspond à f_calcul_fob_v2.pbl
    msg := '';
    res := 0;

    OPEN C_fob (is_ord_ref);
    FETCH C_fob into ld_TOTVTE, ld_FRET;
    if ld_TOTVTE is null then ld_TOTVTE := 0; end if;

    ld_fob := ld_TOTVTE - (ld_TOTVTE * 1 / 100);

    LOOP
        FETCH C_fob into ld_TOTVTE, ld_FRET;
        EXIT WHEN C_fob%notfound;

        if ld_FRET is null then ld_FRET := 0; end if;

        ld_fob := ld_fob - ld_FRET;
        if ld_fob <= 0 then
            msg := 'Erreur calcul du FOB, Valeur incohérente :' || to_char(ld_fob);
            return;
        end if;
        ld_FRET_TOTAL := ld_FRET_TOTAL + ld_FRET;
    end loop;
    CLOSE C_fob;

    commit;

    update geo_ordre set TOTFOB = ld_fob where ord_ref = is_ord_ref;

    OPEN C_fob_mont (is_ord_ref);
    LOOP
        FETCH C_fob_mont into ls_esp_code, ld_tot_vte_esp, ld_tot_rem_esp, ld_tot_rep_esp;
        EXIT WHEN C_fob_mont%notfound;

        ld_fob := (ld_tot_vte_esp - ld_tot_rem_esp) - (ld_tot_vte_esp - ld_tot_rem_esp) / 100 - (ld_FRET_TOTAL * ld_tot_rep_esp);
        -- TODO : En attente pour la table GEO_ORDFOB
        /*select count(*) into li_nb_fob
        from GEO_ORDFOB
        where 	ESP_CODE 	= ls_esp_code and
                ORD_REF 	= is_ord_ref;

        if li_nb_fob = 0 then
            insert into GEO_ORDFOB(ESP_CODE,ORD_REF,MONTANT_FOB) VALUES(ls_esp_code, arg_ord_ref, ld_fob);
        ELSE
            UPDATE GEO_ORDFOB SET MONTANT_FOB = ld_fob where ESP_CODE = ls_esp_code and ORD_REF = is_ord_ref;
        End IF;*/
    end loop;
    CLOSE C_fob_mont;

    res := 1;
end F_CALCUL_FOB_V2;
