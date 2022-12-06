CREATE OR REPLACE PROCEDURE "GEO_ADMIN".FN_CHG_DEV_ORDRE_RGP(
    arg_ord_ref_rgp varchar2,
    res OUT number,
    msg OUT varchar2
)
AS
    li_nb_devise number;
    ldc_trp_dev_taux_GBP number;
    ldc_trp_dev_taux_ori number;
    ld_vte_pu_orig number;
    ld_vte_pu_rgp number;

    ls_dev_code_rgp varchar2(50);
    ls_dev_code_ori varchar2(50);
    ls_orl_ref_grp varchar2(50);
    ls_dev_code_orig varchar2(50);

    cursor C_devise is
        select  R.ORL_REF_RGP,O_ORI.DEV_CODE,OL_ORI.VTE_PU
        FROM GEO_GEST_REGROUP R, GEO_ORDRE O_ORI,GEO_ORDLIG OL_ORI,GEO_ORDRE O_RGP,GEO_ORDLIG OL_RGP
        where R.ORD_REF_RGP = arg_ord_ref_rgp and
            R.ORD_REF_RGP = O_RGP.ORD_REF and
            R.ORD_REF_RGP = OL_RGP.ORD_REF and
            R.ORL_REF_RGP = OL_RGP.ORL_REF and
            R.ORD_REF_ORIG = O_ORI.ORD_REF and
            R.ORD_REF_ORIG = OL_ORI.ORD_REF and
            R.ORL_REF_ORIG = OL_ORI.ORL_REF and
            R.FOU_CODE_ORIG <> 'STEFLEMANS';
BEGIN
    res := 0;
    msg := '';

    select count(distinct O.DEV_CODE) into li_nb_devise
    from GEO_GEST_REGROUP G,GEO_ORDRE O
    where  	G.ORD_REF_RGP = arg_ord_ref_rgp and
                G.ORD_REF_ORIG = O.ORD_REF;

    If li_nb_devise <2 Then
        msg := 'OK';
        res := 1;
        return;
    End IF;


    select DEV_CODE into ls_dev_code_rgp
    from GEO_ORDRE O
    where  	ORD_REF = arg_ord_ref_rgp ;

    select dev_tx into ldc_trp_dev_taux_GBP
    from geo_devise_ref
    where dev_code = 'GBP' and
            dev_code_ref ='EUR';


    IF ls_dev_code_rgp ='EUR' Then
            ls_dev_code_rgp := 'GBP';

            UPDATE GEO_ORDRE
            SET DEV_TX = ldc_trp_dev_taux_GBP,
                DEV_CODE =ls_dev_code_rgp
            where ORD_REF =arg_ord_ref_rgp;
    ENd IF;

    for r in C_devise loop
        If r.ORL_REF_RGP = ls_dev_code_rgp Then
            ld_vte_pu_rgp := r.VTE_PU;
        ELSe
            If ldc_trp_dev_taux_GBP = 0 Then
                ldc_trp_dev_taux_GBP :=1;
            end if;
            ld_vte_pu_rgp := r.VTE_PU / ldc_trp_dev_taux_GBP;
        End IF;
        update GEO_ORDLIG set VTE_PU = ld_vte_pu_rgp where ORD_REF = arg_ord_ref_rgp and   ORL_REF= r.DEV_CODE;
    end loop;


    msg := 'OK';
    res := 1;

end;
/

