CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_CONTROL_ORDRE_BAF" (
    arg_ord_ref in GEO_ORDRE.ORD_REF%type,
    arg_soc_code in GEO_SOCIETE.SOC_CODE%type,

    ind_baf out varchar2,
    ind_trp out varchar2,
    ind_prix out varchar2,
    ind_qte out varchar2,
    ind_autre out varchar2,
    ind_station out varchar2,
    ind_date out varchar2,
    desc_ctl out varchar2,
    pc_marge_brute out decimal,

    res out number,
    msg out varchar2
)
AS
    --ls_ind_trp varchar2(50) :='0';
    --ls_ind_prix varchar2(50) := '0';
    --ls_ind_qte varchar2(50) := '0';
    --ls_ind_autre varchar2(50) := '0';
    --ls_ind_date varchar2(50) := '0';
    --ls_ind_station varchar2(50) := '0';

    ldc_marge_nette decimal;
    ldc_marge_brute decimal;
    --ldc_pc_marge_brute decimal;

    ls_liv_dat varchar2(50);
    ls_liv_dat_tri varchar2(50);
    --ls_ind_baf varchar2(50);
    --ls_ind_trp varchar2(50);
    --ls_ind_prix varchar2(50);
    --ls_ind_qte varchar2(50);
    --ls_ind_autre varchar2(50);
    --ls_desc_ctl varchar2(50);
    --ls_ind_date varchar2(50);
    --ls_ind_station varchar2(50);
    ls_cen_code varchar2(50);
BEGIN
    res := 0;

    ind_baf := '0';
    ind_trp := '0';
    ind_prix := '0';
    ind_qte := '0';
    ind_autre := '0';
    ind_station := '0';
    ind_date := '0';

	f_calcul_marge(arg_ord_ref, res, msg);

    if msg is null or msg <> 'OK' then
        msg := '(A) %%% Problème sur le calcul de la marge';
        pc_marge_brute := 0;
    ELSE
        f_calcul_perequation(arg_ord_ref, arg_soc_code, res, msg);
        if msg <> 'OK' then
            msg := '(A) %%% Problème sur le calcul de la péréquation';
        Else
            f_verif_ordre_warning (arg_ord_ref, arg_soc_code, res, msg);
        End IF;

        select totvte-totrem+totres-totfrd-totach-tottrp-tottrs-totcrt-totfad,
               totvte-totrem+totres-totfrd-totach-tottrp-tottrs-totcrt-totfad-totmob,
               case when totvte > 0 THEN (totvte-totrem+totres-totfrd-totach-tottrp-tottrs-totcrt-totfad) / totvte ELSE 0 END
        INTO ldc_marge_brute, ldc_marge_nette, pc_marge_brute
        from GEO_ORDRE
        where ORD_REF = arg_ord_ref;

        pc_marge_brute := round(pc_marge_brute * 100, 2);
    End If;

    -- Initialise l'indicateur de couleur et message d'erreur
    If msg = 'OK' then
        -- VERT
        ind_baf := '0';
		desc_ctl := 'Ordre bon à facturer';
    ELSE
        desc_ctl := msg;

        if instr(msg, '%%%') <> 0 then
            -- ROUGE
            ind_baf := '2';
        ELSE
            -- ORANGE
            ind_baf := '1';
        END IF;

        if instr(msg, '(P) %%%') <> 0 then
            ind_prix := '2';
        Else
            if instr(msg,'(P)') <> 0 then
                ind_prix := '1';
            End If;
        End If;

        if instr(msg, '(A) %%%') <> 0 then
            ind_autre := '2';
        Else
            if instr(msg, '(A)') <> 0 then
                ind_autre := '1';
            End If;
        End If;

        if instr(msg, '(Q) %%%') <> 0 then
            ind_qte := '2';
        Else
            if instr(msg,'(Q)') <> 0 then
                ind_qte := '1';
            End If;
        End If;

        if instr(msg,'(T) %%%') <> 0 then
            ind_trp := '2';
        Else
            if instr(msg, '(T)') <> 0 then
                ind_trp := '1';
            End If;
        End If;


        if instr(msg, '(D) %%%') <> 0 then
            ind_date := '2';
        Else
            if instr(msg, '(D)') <> 0 then
                ind_date := '1';
            End If;
        End If;

        if instr(msg, '(S) %%%') <> 0 then
            ind_station := '2';
        Else
            if instr(msg, '(S)') <> 0 then
                ind_station := '1';
            End If;
        End If;
    End If;

	res := 1;
end;
