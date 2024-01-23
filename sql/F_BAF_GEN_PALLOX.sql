DROP PROCEDURE GEO_ADMIN.F_BAF_GEN_PALLOX;

CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_BAF_GEN_PALLOX (
    arg_ord_ref IN GEO_ORDRE.ORD_REF%TYPE,
    arg_soc_code IN GEO_SOCIETE.SOC_CODE%TYPE,
    res IN OUT number,
    msg IN OUT varchar2
)
AS
    ls_datversion varchar(10) := '17/09/2018';
    ll_nb_pallox number;
    ls_cli_ref GEO_CLIENT.CLI_REF%TYPE;
    ls_cli_ref_palox GEO_CLIENT.CLI_REF_PALOX%TYPE;
    ls_nordre_pere GEO_ORDRE.NORDRE%TYPE;
    nb_nb_palox_deja_cree number;
    ls_list_ord_ref GEO_ORDRE.ORD_REF_PALOX_LIST_FILS%TYPE;
    ls_list_orl_ref clob;
    ls_fou_code_old GEO_FOURNI.FOU_CODE%TYPE := 'kamoulox';
    ls_col_code_palox_old GEO_COLIS.COL_CODE%TYPE := 'cotcotcodec';

    ls_ret clob;
    ls_sql clob;
    ls_nordre_pallox clob;

    ls_consigne_palox_sa char(1);
    ls_consigne_palox_udc char(1);
    ls_consigne_palox_bws char(1);
    ls_ind_cons_pal char(1);

    C_BAF_GEN_PALLOX_LIST SYS_REFCURSOR;
    CURSOR C_BAF_GEN_PALOX (ref_ordre GEO_ORDRE.ORD_REF%type)
    IS
        select GEO_FOURNI.FOU_CODE, GEO_ARTICLE_COLIS.COL_CODE, ORL_REF, IND_CONSIGNE_PALOX_UDC , IND_CONSIGNE_PALOX_SA, IND_CONSIGNE_PALOX_BWS
        from GEO_ORDLIG, GEO_FOURNI,GEO_ARTICLE_COLIS
        where GEO_ORDLIG.ORD_REF = ref_ordre and
                GEO_ORDLIG.FOU_CODE =  GEO_FOURNI.FOU_CODE and
                GEO_ORDLIG.ART_REF = GEO_ARTICLE_COLIS.ART_REF and
                GEO_ARTICLE_COLIS.GEM_CODE = 'PALOX' and
                GEO_ARTICLE_COLIS.ESP_CODE <> 'EMBALL' and
                GEO_ORDLIG.EXP_NB_COL <> 0
        ORDER BY GEO_FOURNI.FOU_CODE, GEO_ARTICLE_COLIS.COL_CODE;
BEGIN
    -- correspond à f_baf_gen_pallox.pbl
    res := 0;
    msg := '';

    begin
    select T.IND_CONS_PAL into ls_ind_cons_pal 
    from  GEO_TYPORD T, GEO_ORDRE O
    where O.ORD_REF = arg_ord_ref and
          O.TYP_ORDRE = T.TYP_ORD;
          
          
    If ls_ind_cons_pal  ='N' Then
             res := 1;
             return;
    End If;
    exception when others then    
            msg := 'pas de type ordre défini';
            return;

    end;
    
    select count(*) into ll_nb_pallox
    from GEO_ORDLIG
    where GEO_ORDLIG.ORD_REF = arg_ord_ref and
        exists     (select 1
                   from         GEO_ARTICLE_COLIS
                   where      GEO_ORDLIG.ART_REF = GEO_ARTICLE_COLIS.ART_REF and
                           GEO_ARTICLE_COLIS.GEM_CODE ='PALOX' and
                           GEO_ARTICLE_COLIS.ESP_CODE <> 'EMBALL')     and
        exists  (select 1
                 from GEO_CLIENT , GEO_ORDRE
                 where GEO_ORDRE.ORD_REF =  GEO_ORDLIG.ORD_REF and
                         GEO_ORDRE.CLI_REF =  GEO_CLIENT.CLI_REF and
                         GEO_CLIENT.IND_CONS_PALOX ='O');

    -- Spécifité UDC ne faire que les stations qui font de la consigne palox ( ou kamoulox en cinq avec girafe)
    If (ll_nb_pallox > 0 and arg_soc_code ='UDC') Then
        select count(*) into ll_nb_pallox
        from GEO_ORDLIG L , GEO_FOURNI F
        where L.ORD_REF = arg_ord_ref and
                L.FOU_CODE = F.FOU_CODE and
                F.IND_CONSIGNE_PALOX_UDC = 'O' and
            exists  (select 1
                     from GEO_CLIENT , GEO_ORDRE
                     where GEO_ORDRE.ORD_REF =  L.ORD_REF and
                             GEO_ORDRE.CLI_REF =  GEO_CLIENT.CLI_REF and
                             GEO_CLIENT.IND_CONS_PALOX ='O');
    End If;

    -- Spécifité BWS ne faire que les stations qui font de la consigne palox ( ou kamoulox en cinq avec girafe)
    If (ll_nb_pallox > 0 and arg_soc_code ='BWS') Then
        select count(*) into ll_nb_pallox
        from GEO_ORDLIG L , GEO_FOURNI F
        where L.ORD_REF = arg_ord_ref and
                L.FOU_CODE = F.FOU_CODE and
                F.IND_CONSIGNE_PALOX_BWS = 'O' and
            exists  (select 1
                     from GEO_CLIENT , GEO_ORDRE
                     where GEO_ORDRE.ORD_REF =  L.ORD_REF and
                             GEO_ORDRE.CLI_REF =  GEO_CLIENT.CLI_REF and
                             GEO_CLIENT.IND_CONS_PALOX ='O');
    End If;

    if ll_nb_pallox > 0 and (arg_soc_code = 'SA' or arg_soc_code = 'UDC' or arg_soc_code = 'BWS') Then
        select GEO_CLIENT.CLI_REF, GEO_CLIENT.CLI_REF_PALOX,NORDRE
        into ls_cli_ref, ls_cli_ref_palox,ls_nordre_pere
        from GEO_ORDRE, GEO_CLIENT
        where GEO_ORDRE.ORD_REF = arg_ord_ref and
                GEO_ORDRE.CLI_REF = GEO_CLIENT.CLI_REF;
                
                
        select count(*) into nb_nb_palox_deja_cree
        from GEO_ORDRE
        where sysdate - to_date(ls_datversion,'dd/mm/yyyy') < 60  and
                CREDAT < to_date(ls_datversion,'dd/mm/yyyy')  and
                ORD_REF = arg_ord_ref;

        If nb_nb_palox_deja_cree  = 0 Then
            begin
                select count(*),ORD_REF_PALOX_LIST_FILS
                into nb_nb_palox_deja_cree, ls_list_ord_ref
                from GEO_ORDRE
                where GEO_ORDRE.ORD_REF = arg_ord_ref and
                    GEO_ORDRE.ORD_REF_PALOX_LIST_FILS IS  NOT NULL
                group by ORD_REF_PALOX_LIST_FILS;
            exception when others then
                nb_nb_palox_deja_cree := 0;
                ls_list_ord_ref := '';
            end;
        end if;

        If nb_nb_palox_deja_cree = 0 Then
            If ls_cli_ref_palox is null or ls_cli_ref_palox = '' Then
                msg := 'PALOX pour l''ordre "+ls_nordre_pere,"Création automatique impossible car il n''y a pas de client pallox défini~nla validation de l''ordre pour facturation est abandonnée';
                res := 2;
                return;

                /* TODO : openwithparm(w_rech_client_palox,ls_cli_ref_palox) -- Compliqué a faire d'un point de vue SQL
                If ls_cli_ref_palox_new = 'CANCEL'   THEN
                    messagebox("PALOX pour l'ordre "+ls_nordre_pere,"Création automatique impossible car il n'y a pas de client pallox défini~nla validation de l'ordre pour facturation est abandonnée")
                    return -1
                Else
                    update GEO_CLIENT
                    set CLI_REF_PALOX = ls_cli_ref_palox_new
                    where CLI_REF = ls_cli_ref;
                End If;*/
            end if;

            FOR r in C_BAF_GEN_PALOX(arg_ord_ref)
            LOOP
                ls_consigne_palox_sa := r.IND_CONSIGNE_PALOX_SA;
                ls_consigne_palox_udc := r.IND_CONSIGNE_PALOX_UDC;
                ls_consigne_palox_bws := r.IND_CONSIGNE_PALOX_BWS;

                If (arg_soc_code = 'SA' and r.IND_CONSIGNE_PALOX_SA = 'O') or (arg_soc_code = 'UDC' and r.IND_CONSIGNE_PALOX_UDC = 'O') or (arg_soc_code = 'BWS' and r.IND_CONSIGNE_PALOX_BWS ='O') Then
                    If r.fou_code <> ls_fou_code_old or r.col_code <> ls_col_code_palox_old Then
                        If ls_fou_code_old <> 'kamoulox' Then
                            F_CRE_ORD_PAL_PR_LIG_PAL_V2(arg_ord_ref, arg_soc_code, ls_list_orl_ref, res, msg, ls_ret);

                            If substr(msg, 1, 3) = '%%%' Then
                                msg := 'Erreur ordres pallox pour l''ordre ' || ls_nordre_pere || ' : ' || msg;
                                rollback;
                                return;
                            else
                                ls_nordre_pallox := ls_nordre_pallox || ls_ret || '~n';
                            end if;
                        end if;

                        ls_fou_code_old := r.FOU_CODE;
                        ls_col_code_palox_old := r.COL_CODE;
                        ls_list_orl_ref := '''' || r.ORL_REF || '''';
                    else
                        ls_list_orl_ref := ls_list_orl_ref || ',''' || r.ORL_REF || '''';
                    end if;
                end if;
            end loop;

            If (arg_soc_code = 'SA' and ls_consigne_palox_sa = 'O') or (arg_soc_code = 'UDC' and ls_consigne_palox_udc = 'O')
                   or (arg_soc_code = 'BWS' and ls_consigne_palox_bws = 'O') Then
                F_CRE_ORD_PAL_PR_LIG_PAL_V2(arg_ord_ref,arg_soc_code, ls_list_orl_ref, res, msg, ls_ret);

                If substr(msg, 1, 3) = '%%%' Then
                    msg := 'Erreur de création ordres pallox pour l''ordre ' || ls_nordre_pere || ' : ' || msg;
                    rollback;
                    return;
                else
                    ls_nordre_pallox := ls_nordre_pallox || ls_ret || '~n';
                end if;
            end if;

            -- ON FAIT ?
            If ls_nordre_pallox <> '' or not ls_nordre_pallox is null then
                msg := 'Ordres pallox créés  pour l''ordre ' || ls_nordre_pere || ' : ' || ls_nordre_pallox;
            end if;
        else
            If ls_list_ord_ref = '' Then
                msg := 'PALOX pour l''ordre ' || ls_nordre_pere || ' : Création automatique impossible car il a déjà un ordre PALOX pour cet ORDRE';
            Else
                ls_list_ord_ref := '''' || replace(ls_list_ord_ref, ';', ''',''')  || '''';
                ls_sql := 'select NORDRE from GEO_ORDRE where ORD_REF IN (' || ls_list_ord_ref  || ')';

                begin
                    OPEN C_BAF_GEN_PALLOX_LIST FOR to_char(ls_sql);
                    LOOP
                        FETCH C_BAF_GEN_PALLOX_LIST INTO ls_ret;
                        EXIT WHEN C_BAF_GEN_PALLOX_LIST%notfound;

                        ls_nordre_pallox := ls_nordre_pallox || ls_ret || '~n';
                    end loop;
					CLOSE C_BAF_GEN_PALLOX_LIST;
					msg := 'PALOX pour l''ordre ' || ls_nordre_pere || ' : Création automatique impossible car il a déjà un(des) ordre(s)= PALOX pour cet ORDRE~n' || ls_nordre_pallox;
                end;
            end if;
        end if;
    end if;

    res := 1;
end F_BAF_GEN_PALLOX;
/
