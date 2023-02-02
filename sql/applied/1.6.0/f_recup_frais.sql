CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_RECUP_FRAIS" (
    arg_var_code in GEO_VARIET.VAR_CODE%TYPE,
    arg_cat_code in varchar2,
    arg_sco_code in varchar2,
    arg_tvt_code in varchar2,
    arg_mode_culture in number,
    arg_origine in varchar2,
    res out number,
    msg out varchar2
)
AS
    ls_cat varchar2(50);
    ls_sco varchar2(50);
    ls_tvt varchar2(50);
    ls_tvt_code varchar2(50);
    ls_origine varchar2(50);
    ls_culture varchar2(50);
    lb_ya_cat integer := 0;
    lb_ya_sco integer := 0;
    lb_ya_tvt integer := 0;
    lb_ya_culture integer := 0;
    lb_ya_origine integer := 0;
    ld_frais_pu_mark number;
    ld_accompte number;
    ls_frais_unite_mark varchar2(50);
    ls_prerequation varchar2(50);
    ll_k_frais number;
    i number;

    CURSOR C_MARK (var_ref GEO_VARIET.VAR_CODE%type)
    IS
        select k_frais, choix_cat, sco_code, tvt_code, mode_culture, origine, frais_pu, frais_unite, accompte, perequation
        from geo_attrib_frais
        where var_code = var_ref
        and  valide = 'O';
BEGIN
    res := 0;
    msg := '';

    OPEN C_MARK(arg_var_code);
    LOOP
        fetch C_MARK INTO  ll_k_frais,  ls_cat,  ls_sco,  ls_tvt,  ls_culture,  ls_origine,  ld_frais_pu_mark,  ls_frais_unite_mark,  ld_accompte,  ls_prerequation;
        EXIT WHEN C_MARK%notfound;

        if instr(ls_cat,arg_cat_code) > 0 or ls_cat = '%' then lb_ya_cat := 1; end if;
        if instr(ls_sco,arg_sco_code) > 0 or ls_sco = '%' then lb_ya_sco := 1; end if;
        if instr(ls_tvt,arg_tvt_code) > 0 or ls_tvt = '%' then lb_ya_tvt := 1; end if;
        if instr(ls_culture,arg_mode_culture) > 0 or ls_culture = '%' then lb_ya_culture := 1; end if;
        if instr(ls_origine,arg_origine) > 0 or ls_origine = '%' then lb_ya_origine := 1; end if;

        if lb_ya_cat = 1 and lb_ya_sco = 1 and lb_ya_tvt = 1 and lb_ya_culture = 1 and lb_ya_origine = 1 then
            CLOSE C_MARK;

            res := ll_k_frais;
            return;
        end if;
    END LOOP;
    CLOSE C_MARK;

    res := 0;
    return;
END;
/

