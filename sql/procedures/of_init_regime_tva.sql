-- Dans le cas où l'ordre est marqué 'Bon à facturer' il ne faut plus forcer le régime de TVA
-- L'utilisateur abilité doit pouvoir forcer sa valeur

CREATE OR REPLACE PROCEDURE OF_INIT_REGIME_TVA (
    is_cur_ord_ref in GEO_ORDRE.ORD_REF%TYPE,
    is_tvr_code_entrepot varchar2,
    res out number,
    msg out varchar2
)
AS
    ls_rc varchar2(50);
    ls_regime_tva varchar2(50);
    ls_flbaf varchar2(50);
    ls_tvr_code varchar2(50);
begin

	res := 0;
	msg := '';

    -- Récupère l'état de l'ordre pour 'bon a facturer'

    select flbaf, tvr_code
    into ls_flbaf, ls_tvr_code
    from geo_ordre
    where ord_ref = is_cur_ord_ref;

    -- si ls_flbaf = 'O' il ne faut plus forcer sa valeur, l'utilisateur doit pouvoir changer le régime
    if ls_flbaf <> 'O' then
        -- BAM le 04/02/18 C'est la tva entrepot qui pilote
        f_calcul_regime_tva(is_cur_ord_ref,is_tvr_code_entrepot, ls_regime_tva,ls_rc);
    else
        ls_regime_tva := ls_tvr_code;
    end if;

    case ls_regime_tva
        when 'T' then
            -- regime tva T
            msg := msg || 'Opération triangulaire, un régime TVA spécial est appliqué';
            f_set_regime_tva(is_cur_ord_ref, 'T', res, msg);
            --idw_ordre_b.setitem( 1, 'tvr_code', 'T Triangulaire')
        when  'X' then
            -- regime tva X
            msg := msg || 'Opération ''Cross Trade'', un régime TVA spécial est appliqué';
            f_set_regime_tva(is_cur_ord_ref, 'X', res, msg);
            --idw_ordre_b.setitem( 1, 'tvr_code', 'X Cross Trade')
        when  'L' then
            -- regime tva X
            msg := msg || 'TVA locale, un régime TVA spécial est appliqué';
            f_set_regime_tva(is_cur_ord_ref, 'L', res, msg);
            --idw_ordre_b.setitem( 1, 'tvr_code', 'L TVA locale')
        when  'G' then
            -- regime tva X
            msg := msg || 'TVA locale, un régime TVA spécial est appliqué';
            f_set_regime_tva(is_cur_ord_ref, 'G', res, msg);
            --idw_ordre_b.setitem( 1, 'tvr_code', 'L TVA locale')
        when '' then
            -- Melange de regime TVA
            msg := msg || ls_rc;
            --idw_ordre_b.setitem( 1, 'tvr_code', '')
            --MessageBox('Error', 'Incompatibilité des fournisseurs, les regimes de TVA sont différents')
        else
            -- nothing special
            f_set_regime_tva(is_cur_ord_ref, ls_regime_tva, res, msg);

            -- if ls_regime_tva = 'E' then idw_ordre_b.setitem( 1, 'tvr_code', 'E Export')
            -- if ls_regime_tva = 'F' then idw_ordre_b.setitem( 1, 'tvr_code', 'F Franchise')
            -- if ls_regime_tva = 'N' then idw_ordre_b.setitem( 1, 'tvr_code', 'N France')
            -- if ls_regime_tva = 'C' then idw_ordre_b.setitem( 1, 'tvr_code', 'C Communaute CEE')
            --
    end case;

	res := 1;
end;
/

