-- AR 20/10/09 corrige bug (monstrueux) de l'absence d'initialisation du ord_ref_origine
-- cett fonction est lancée par cb_litige_cree
-- quelquefois dw_litige.postinsertrow n'était pas exécuté
-- met le trigger dw_litige.postinsertrow en commentaire pour éviter toute confusion
CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."OF_CHRONO_LITIGE" (
    is_cur_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    arg_username IN GEO_USER.nom_utilisateur%type,
    res out number,
    msg out varchar2,
    is_cur_lit_ref out GEO_LITIGE.LIT_REF%TYPE
)
AS
    ll_lit_ref GEO_LITIGE.LIT_REF%TYPE;
    ldt_x timestamp;
    o_depdatp timestamp;
    o_ref_cli varchar2(50);
BEGIN
    res := 0;
    msg := '';

    select seq_lit_num.nextval into ll_lit_ref from dual;
	is_cur_lit_ref := ll_lit_ref;
    --is_cur_lit_ref	:= to_char(ll_lit_ref,'000000');

    select depdatp, ref_cli
    into o_depdatp, o_ref_cli
    from geo_ordre
    where ord_ref = is_cur_ord_ref;

    begin
        insert into geo_litige (
            lit_ref,
            ord_ref_origine, -- ajout AR 20/10/09
            fl_fourni_clos,
            lit_frais_annexes,
            lit_date_creation,
            lit_date_origine,
            lit_date_resolution,
            fl_encours,
            ref_cli,
            num_version,
            mod_user
        )
        values (
            is_cur_lit_ref,
            is_cur_ord_ref,
            'N',
            0,
            sysdate,
            -- to_date(to_char(o_depdatp,'dd/mm/yy')),
            o_depdatp,
            sysdate,
            'O',
            o_ref_cli,
            2,
            arg_username
        );
    exception when others then
        msg := 'Echec de création du litige: ' || SQLERRM;
        res := 0;
        return;
    end;

    of_sauve_litige(is_cur_lit_ref,res,msg);
    commit;
    if res = 0 then return; end if;

    res := 1;
END;
/
