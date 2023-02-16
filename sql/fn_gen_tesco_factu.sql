CREATE OR REPLACE PROCEDURE GEO_ADMIN.FN_GEN_TESCO_FACTU (
    arg_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    res out number,
    msg out varchar2
)
AS
    li_ret number;
    ls_cli_ref varchar2(50);
    ls_ord_ref_sa varchar2(50);
    ls_ord_ref_sa_n varchar2(50);
    ls_flbaf_sa varchar2(50);
    ls_nordre_sa varchar2(50);
    ls_flbaf_sa_n varchar2(50);
    ls_nordre_sa_n varchar2(50);
    ls_facture_avoir varchar2(50);
BEGIN
    res := 0;
    msg := '';

    select CLI_REF, FACTURE_AVOIR into ls_cli_ref ,ls_facture_avoir
    FROM GEO_ORDRE
    where ORD_REF =arg_ord_ref;

    If ls_cli_ref ='007396' Then
        case ls_facture_avoir
            when 'F' then

                select ORD_REF_SA,ORD_REF_SA_N into ls_ord_ref_sa,ls_ord_ref_sa_n
                FROM GEO_ORDRE_BUK_SA
                where ORD_REF_BUK = arg_ord_ref;

                If ls_ord_ref_sa is not null or ls_ord_ref_sa= ''  Then
                    select FLBAF, NORDRE into ls_flbaf_sa,ls_nordre_sa
                    from GEO_ORDRE
                    where ORD_REF =ls_ord_ref_sa;

                    IF ls_flbaf_sa = 'O' Then
                        msg := 'Erreur: ORDRE SA '|| ls_nordre_sa||' déjà bon à facturer ou facturer';
                        res := 0;
                        return;
                    ELSE
                        delete from GEO_ORDRE where ORD_REF =ls_ord_ref_sa;

                    End IF;
                End IF;
                --
                If ls_ord_ref_sa_n is not null or ls_ord_ref_sa_n= ''  Then
                    select FLBAF, NORDRE into ls_flbaf_sa_n,ls_nordre_sa_n
                    from GEO_ORDRE
                    where ORD_REF =ls_ord_ref_sa_n;

                    IF ls_flbaf_sa_n = 'O' Then
                        msg := 'Erreur: ORDRE SA NEUTRE '|| ls_nordre_sa_n||' déjà bon à facturer ou facturer';
                        res := 0;
                        return;
                    ELSE
                        delete from GEO_ORDRE where ORD_REF =ls_ord_ref_sa_n;
                    End IF;
                End IF;

                DELETE FROM GEO_ORDRE_BUK_SA where ORD_REF_BUK = arg_ord_ref;

                fn_gen_ordre_tesco_sa(arg_ord_ref, res, msg);
                If res <> 1 Then return; end if;

                fn_gen_ordre_neutre_tesco_sa(arg_ord_ref, res, msg);
                If res <> 1 Then return; end if;

            when 'A' then

                select ORD_REF_SA,ORD_REF_SA_N into ls_ord_ref_sa,ls_ord_ref_sa_n
                FROM GEO_AVOIR_BUK_SA
                where ORD_REF_BUK = arg_ord_ref;

                If ls_ord_ref_sa is not null or ls_ord_ref_sa= ''  Then
                    select FLBAF, NORDRE into ls_flbaf_sa,ls_nordre_sa
                    from GEO_ORDRE
                    where ORD_REF =ls_ord_ref_sa;

                    IF ls_flbaf_sa = 'O' Then
                        msg := 'Erreur: AVOIR SA '|| ls_nordre_sa||' déjà bon à facturer ou facturer';
                        res := 0;
                        return;
                    ELSE
                        delete from GEO_ORDRE where ORD_REF =ls_ord_ref_sa;
                    End IF;
                End IF;
                --
                If ls_ord_ref_sa_n is not null or ls_ord_ref_sa_n= ''  Then
                    select FLBAF, NORDRE into ls_flbaf_sa_n,ls_nordre_sa_n
                    from GEO_ORDRE
                    where ORD_REF =ls_ord_ref_sa_n;

                    IF ls_flbaf_sa_n = 'O' Then
                        msg := 'Erreur: AVOIR SA NEUTRE '|| ls_nordre_sa_n||' déjà bon à facturer ou facturer';
                        res := 0;
                        return;
                    ELSE
                        delete from GEO_ORDRE where ORD_REF =ls_ord_ref_sa_n;
                    End IF;
                End IF;

                DELETE FROM GEO_AVOIR_BUK_SA where ORD_REF_BUK = arg_ord_ref;

                fn_gen_avoir_tesco_sa(arg_ord_ref, res, msg);
                If res <> 1 Then return; end if;

                fn_gen_avoir_neutre_tesco_sa(arg_ord_ref, res, msg);
                If res <> 1 Then return; end if;


        End case;
    End If;

    commit;
    res := 1;
END;
/

