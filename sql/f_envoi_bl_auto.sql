CREATE OR REPLACE PROCEDURE F_ENVOI_BL_AUTO (
    gs_soc_code in varchar2,
    ls_sco_code in varchar2,
    arg_date_min in date,
    arg_date_max in date,
    arg_utilisateur in varchar2,
    res out number,
    msg out varchar2,
    array_ord_ref out p_str_tab_type
)
AS
    -- ll_nbj	= (long(em_jours.Text) - 1) * -1
    ls_date_min	varchar2(8) := to_char(arg_date_min,'yyyymmdd');
    ls_date_max varchar2(8) := to_char(arg_date_max,'yyyymmdd');

    ll_cde_nb_col number;
    ll_exp_nb_col number;
    ld_EXP_PDS_BRUT number;
    ld_EXP_PDS_NET number;
    ls_jour_exp number;
    ls_jour_liv number;
    ls_jour_sys number;
    ls_fou_code varchar2(50);
    ls_ord_ref varchar2(50);
    ls_nordre varchar2(50);

    ll_cde_nb_col_tot number;
    ll_exp_nb_col_tot number;
    ll_poids_tot number;
    ls_art_ref varchar2(50);
    ls_art_ref_old varchar2(50);
    ls_fou_code_old varchar2(50);
      ll_cde_nb_col_ctl number;
    ll_cde_nb_col_ctl_old number;
    ll_exp_nb_col_ctl number;
    ll_exp_nb_col_ctl_old number;
    ld_exp_pds_net_ctl number;
    ld_exp_pds_net_ctl_old number;
    lb_bloquer boolean;
    ll_nb_detail_non_cloturer number;
    li_nb_poids_zero number;
    lb_ord_bloq_poids boolean := False;

    CURSOR C_ORD_REF IS
    SELECT
        sum(GEO_ORDLIG.cde_nb_col),
        sum(GEO_ORDLIG.exp_nb_col),
        sum(GEO_ORDLIG.EXP_PDS_BRUT),
        sum(GEO_ORDLIG.EXP_PDS_NET),
        GEO_ORDRE.ord_ref,
        GEO_ORDRE.nordre,
        to_char( geo_ordlog.DATDEP_FOU_P , 'D'),
        to_char( geo_ordre.LIVDATP , 'D'),
        to_char( SYSDATE , 'D'),
        sum( case when GEO_ORDLOG.FLAG_EXPED_FOURNNI ='N' then 1 else 0 end )
    FROM
        GEO_ORDLOG,
        GEO_ORDRE,
        GEO_ORDLIG
    WHERE
        GEO_ORDLOG.ord_ref = GEO_ORDRE.ord_ref AND
        GEO_ORDRE.ord_ref = GEO_ORDLIG.ord_ref AND
        ( geo_ordlog.datdep_fou_p_yyyymmdd between ls_date_min and ls_date_max) AND
        geo_ordre.soc_code = gs_soc_code  AND
        geo_ordre.sco_code like ls_sco_code AND
        geo_ordre.cli_code not like 'PREORDRE%' AND
        geo_ordre.VERSION_DETAIL IS NULL
    group by
        GEO_ORDRE.ord_ref,
        GEO_ORDRE.nordre,
        geo_ordlog.DATDEP_FOU_P,
        geo_ordre.LIVDATP;

    ll_index number := 0;
begin

	res := 0;
	msg := '';
    array_ord_ref := p_str_tab_type();

    OPEN C_ORD_REF;
    FETCH C_ORD_REF INTO ll_cde_nb_col, ll_exp_nb_col, ld_EXP_PDS_BRUT, ld_EXP_PDS_NET, ls_ord_ref ,ls_nordre, ls_jour_exp, ls_jour_liv, ls_jour_sys,ll_nb_detail_non_cloturer;
    loop
        EXIT WHEN C_ORD_REF%notfound;

        if ll_cde_nb_col<> 0 and ld_EXP_PDS_BRUT <> 0 and ld_EXP_PDS_NET <> 0 and ll_cde_nb_col = ll_exp_nb_col and ll_nb_detail_non_cloturer = 0   then

            -- BAM le 20/04/16
            -- ne pas envoyer le détail si des lignes ont leurs poids égales à zéro
            select count(*)  into li_nb_poids_zero
            from GEO_ORDLIG
            where ORD_REF = ls_ord_ref and
                    (EXP_PDS_BRUT = 0 or EXP_PDS_BRUT  is null or  EXP_PDS_NET= 0 or EXP_PDS_NET is null) and
                    exp_nb_col <> 0;

            lb_bloquer := False;
            If li_nb_poids_zero >  0  Then

                DECLARE
                    CURSOR C_NB_COL IS
                    select A.ART_REF,
                            A.FOU_CODE,
                            A.CDE_NB_COL,
                            A.EXP_NB_COL,
                            A.EXP_PDS_NET
                    from   GEO_ORDLIG A
                    where  A.ORD_REF =ls_ord_ref and
                            exists (	select 1
                                        from GEO_ORDLIG B
                                        where 	B.ORD_REF = ls_ord_ref  			and
                                                    (B.EXP_PDS_BRUT = 0 or B.EXP_PDS_BRUT  is null or  B.EXP_PDS_NET= 0 or B.EXP_PDS_NET is null) and
                                                    B.exp_nb_col <> 0 AND
                                                    A.FOU_CODE = B.FOU_CODE		and
                                                    A.ART_REF = B.ART_REF)
                    ORDER BY   ART_REF,
                                FOU_CODE;
                begin

                    OPEN C_NB_COL;
                    FETCH C_NB_COL INTO ls_art_ref, ls_fou_code,ll_cde_nb_col,ll_exp_nb_col,ld_exp_pds_net;
                    ll_cde_nb_col_tot   := ll_cde_nb_col_tot + ll_cde_nb_col;
                    ll_exp_nb_col_tot 	 := ll_exp_nb_col_tot + ll_exp_nb_col;
                    ll_poids_tot			 := ll_poids_tot + ld_exp_pds_net;
                    ls_fou_code_old := ls_fou_code;
                    ls_art_ref_old := ls_art_ref;
                    loop
                        EXIT WHEN C_NB_COL%notfound;
                        ls_art_ref := '';
                        ls_fou_code :='';
                        ll_cde_nb_col := 0;
                        ll_exp_nb_col := 0;
                        ld_exp_pds_net := 0;

                        FETCH C_NB_COL INTO ls_art_ref, ls_fou_code,ll_cde_nb_col,ll_exp_nb_col,ld_exp_pds_net;
                        If (ls_fou_code_old <> ls_fou_code or ls_art_ref_old <> ls_art_ref)	Then
                            If ll_cde_nb_col_tot  = ll_exp_nb_col_tot  and ll_poids_tot > 0 Then
                                lb_bloquer := False;
                            Else
                                lb_bloquer := True;
                                exit;
                            End if;
                            ls_fou_code_old := ls_fou_code;
                            ls_art_ref_old := ls_art_ref;
                            ll_cde_nb_col_tot  :=ll_cde_nb_col;
                            ll_exp_nb_col_tot  := ll_exp_nb_col;
                            ll_poids_tot := ll_poids_tot + ld_exp_pds_net;
                        ELSE
                            ll_cde_nb_col_tot  := ll_cde_nb_col_tot + ll_cde_nb_col;
                            ll_exp_nb_col_tot  := ll_exp_nb_col_tot +  ll_exp_nb_col;
                            ll_poids_tot  := ll_poids_tot +  ld_exp_pds_net;
                        End If;
                    end loop;

                    CLOSE C_NB_COL;
                end;


            END IF;


            If 	lb_bloquer = False  Then

                -- cas particulier du secteur france. On envoi pas les details du vendredi si pas livré le samedi.
                if ls_sco_code = 'F' and ls_jour_exp = 5 AND ls_jour_sys = 5 then
                    if ls_jour_liv = 6 then
                        -- on est samedi on peut envoyer
                        ll_index := ll_index + 1;
                        array_ord_ref.extend();
                        array_ord_ref(ll_index) := ls_ord_ref;
                    end if;
                else
                    ll_index := ll_index + 1;
                    array_ord_ref.extend();
                    array_ord_ref(ll_index) := ls_ord_ref;
                end if;
            Else
                    lb_ord_bloq_poids := True;
            end if;

        End if;
        FETCH C_ORD_REF INTO ll_cde_nb_col, ll_exp_nb_col, ld_EXP_PDS_BRUT, ld_EXP_PDS_NET, ls_ord_ref ,ls_nordre, ls_jour_exp, ls_jour_liv, ls_jour_sys,ll_nb_detail_non_cloturer;
    end loop;

    CLOSE C_ORD_REF;

    DECLARE
        i number;
        flux varchar2(50) := 'DETAIl';
        co SYS_REFCURSOR;
	    ls_env_code varchar2(50);
    BEGIN
        for i in 1 .. array_ord_ref.count
        loop
            OF_GENERE_ENVOIS(array_ord_ref(i), flux, 'O', arg_utilisateur, 'N', res, msg, co, ls_env_code);
        end loop;
    EXCEPTION when others then
        msg := 'Echec de l''envoi automatique des BL : ' || SQLERRM;
        return;
    END;


    If lb_ord_bloq_poids = False Then
        msg := 'Envoi des détails terminé';
    Else
        msg := 'Envoi des détails terminé~rLes détails d''expedition avec poids non renseignés ne sont pas partis';
    End if;

	res := 1;

end;
/

