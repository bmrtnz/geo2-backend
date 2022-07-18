-- AR création 17/04/09 reprise en fonction externe de w_stock2_reservation_globale.of_actualise_resa des 15 et 16/04/09
-- cette fonction devrait être l'unique  point de traitement des réservations
-- en sortie on renvoie le nbre de réservations effectuée ainsi que le solde fournisseur/article sous forme structure de long
-- AR 22/04/09 corrige bug oubli ll_nb_resa ++ sur resa normale (qte dispo suffisante)
-- AR 27/04/09 implémente return avec structure long
-- SL 23/05/13 Insere le type de palette dans les critèresll_nb_stock
--			Il faut décrémenter en fonction du type de palette si palette != null

CREATE OR REPLACE PROCEDURE GEO_ADMIN.F_RESA_UNE_LIGNE (
    arg_fou_code IN varchar2,
    arg_prop_code IN varchar2,
    arg_art_ref IN varchar2,
    arg_username IN varchar2,
    arg_qte_resa IN number,
    arg_ord_ref IN varchar2,
    arg_orl_ref IN varchar2,
    arg_desc IN varchar2,
    arg_pal_code IN varchar2,
	res OUT NUMBER,
	msg OUT varchar2,
    nb_resa OUT number,
    nb_dispo OUT number
)
AS
    -- s_multi_long ll_return
    ll_rc number;
    ll_ind number := 1;
    ll_nb_resa number := 0;
    ll_nb_stock number;
    ll_dispo number;
    ll_qte_one number;
    ll_qte_reliquat number := arg_qte_resa;
    ls_rc varchar2(50);
    type tabString is table of varchar2(50);
    ls_sto_ref tabString := tabString();
    ls_age varchar2(50);
    ls_sto_desc varchar2(50);
    ls_sto_statut varchar2(50);
    ls_pal_code varchar2(50);
    ll_qtt_ini number;
    ll_qtt_res number;
    ls_sql varchar2(500);
    type tabNumber is table of number;
    ll_qtt tabNumber := tabNumber();
    -- long ll_i
    co SYS_REFCURSOR;
BEGIN
	res := 1;
	msg := '';

    -- on récupère les stocks correspondants à fourni et article dans une datastore (triés par âge descendant)
    -- Si pal_code est non null on l'inclut dans les critères

    if arg_pal_code is null then
        ls_sql := 'select sto_ref, qte_ini, qte_res, age, sto_desc, sto_statut, pal_code from geo_stock where fou_code = ''' || arg_fou_code || ''' and prop_code = ''' || arg_prop_code || ''' and art_ref = ''' || arg_art_ref || ''' order by age desc';
        select count(*) into ll_nb_stock from geo_stock where fou_code = arg_fou_code and prop_code = arg_prop_code and art_ref = arg_art_ref;
    else
        ls_sql := 'select sto_ref, qte_ini, qte_res, age, sto_desc, sto_statut, pal_code from geo_stock where fou_code = ''' || arg_fou_code || ''' and prop_code = ''' || arg_prop_code || ''' and art_ref = ''' || arg_art_ref || ''' and pal_code = ''' || arg_pal_code || ''' order by age desc';
        select count(*) into ll_nb_stock from geo_stock where fou_code = arg_fou_code and prop_code = arg_prop_code and art_ref = arg_art_ref and pal_code = arg_pal_code;
    end if;

    -- On compte le nonbre de stocks disponibles

    -- ll_qte_reliquat	:= arg_qte_resa;
    -- ll_ind := 1;
    -- ll_nb_resa := 0;

    if arg_username <> '' then

        open co for ls_sql;
        loop
            ls_sto_ref.extend(1);
            ll_qtt.extend(1);
            fetch co into ls_sto_ref(ll_ind), ll_qtt_ini, ll_qtt_res, ls_age, ls_sto_desc, ls_sto_statut, ls_pal_code;
            exit when co%notfound;

            if trim(ls_sto_desc) = trim(arg_username) then
                ll_dispo := ll_qtt_ini - ll_qtt_res;
                if ll_qte_reliquat > ll_dispo then
                    -- le disponible est insuffisant
                    ll_nb_resa := ll_nb_resa + 1;
                    ll_qtt(ll_nb_resa) := ll_dispo;
                    -- il y aura un reliquat donc la boucle normale (plus bas) sera utilisée
                    ll_qte_reliquat	:= ll_qte_reliquat - ll_dispo;
                    ll_dispo	:= 0;
                else
                    -- le disponible est suffisant, on prend la qté à reservée et on sort de la boucle (fin)
                    ll_nb_resa := ll_nb_resa + 1;
                    ll_qtt(ll_nb_resa) := ll_qte_reliquat;
                    -- il n'a aps de reliquat don la boucle normale (plus bas) ne sera pas utilisée (ll_qte_reliquat = 0)
                    ll_dispo	:= ll_dispo - ll_qte_reliquat;
                    ll_qte_reliquat	:= 0;
                    exit;
                end if;
                exit;
            end if;

        ll_ind := ll_ind + 1;
        end loop;
        close co;

    end if;

    -- traitement normal (sans option ou reliquat sur option)
    if ll_qte_reliquat > 0 then
        ll_ind := 1;

        open co for ls_sql;
        loop
            ls_sto_ref.extend(1);
            ll_qtt.extend(1);
            fetch co into ls_sto_ref(ll_ind), ll_qtt_ini, ll_qtt_res, ls_age, ls_sto_desc, ls_sto_statut, ls_pal_code;
            exit when co%notfound;

            ll_dispo := ll_qtt_ini - ll_qtt_res;
            if ll_qte_reliquat > ll_dispo then
                if ll_ind = ll_nb_stock then
                    --MessageBox("DEBUG", "ll_ind = ll_nb_stock, dernière chance")
                    -- c'est la dernière chance, on passe tout le reste sur ce stock, quitte à être négatif
                    ll_nb_resa := ll_nb_resa + 1;
                    ll_qtt(ll_nb_resa) := ll_qte_reliquat;
                    ll_dispo	:= ll_dispo - ll_qte_reliquat;
                    ll_qte_reliquat	:= 0;
                else
                    -- sinon on prend tout le dispo et on va faire un autre tour sur le stock suivant
                    --MessageBox("DEBUG", "ll_ind < ll_nb_stock, on prend tout le dispo et on va faire un autre tour sur le stock suivant")
                    ll_nb_resa := ll_nb_resa + 1;
                    ll_qtt(ll_nb_resa) := ll_dispo;
                    ll_qte_reliquat	:= ll_qte_reliquat - ll_dispo;
                    ll_dispo	:= 0;
                end if;
            else
                    -- le disponible est suffisant, on prend la qté à reservée et on sort de la boucle (fin)
                --MessageBox("DEBUG", "le disponible est suffisant, on prend la qté à reservée et on sort de la boucle (fin)")
                ll_nb_resa := ll_nb_resa + 1;
                ll_qtt(ll_nb_resa) := ll_qte_reliquat;
                -- il n'a aps de reliquat don la boucle normale (plus bas) ne sera pas utilisée (ll_qte_reliquat = 0)
                ll_dispo	:= ll_dispo - ll_qte_reliquat;
                ll_qte_reliquat	:= 0;
                exit;
            end if;

        ll_ind := ll_ind + 1;
        end loop;
        close co;
    end if;

    -- On fait les réservations dans la base
    FOR i IN ls_sto_ref.FIRST .. ls_sto_ref.LAST LOOP

        declare
            ll_stm_ref number;
        begin
            select seq_stm_num.nextval into ll_stm_ref from dual;
            ll_stm_ref	:= to_char(ll_stm_ref,'000000');

            -- voir trigger GEO_STOMVT_BEF_INS qui actualise aussi geo_stock ainsi que les champs manquant de stomvt

            insert into geo_stomvt (stm_ref, sto_ref, nom_utilisateur, mvt_type, mvt_qte, ord_ref, art_ref, orl_ref, stm_desc)
            values(ll_stm_ref, ls_sto_ref(i), arg_username, 'R', ll_qtt(i), arg_ord_ref, arg_art_ref, arg_orl_ref, arg_desc);
        end;

    END LOOP;
    commit;

    --MessageBox("DEBUG", "Retour = " + String(ll_nb_resa) + " - " + String(ll_dispo))

    nb_resa := ll_nb_resa;
    nb_dispo := ll_dispo;

    res := 1;
    msg := 'OK';

END F_RESA_UNE_LIGNE;
/

