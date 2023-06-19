CREATE OR REPLACE PROCEDURE "GEO_ADMIN".REFRESH_STOCK_HEBDO(
    res IN OUT number,
    msg IN OUT clob
)
AS
    ls_rc clob;
BEGIN
    msg := '';
    res := 0;

    -- actualisation de la table geo_stock_hebdo
	-- AR 19/05/09 création d'abord avec une procédure, puis avec execute immédiate (voir en bas pour commentaire)

    begin
        ls_rc := 'TRUNCATE TABLE GEO_ADMIN.GEO_STOCK_HEBDO DROP STORAGE';
        EXECUTE IMMEDIATE to_char(ls_rc);
    exception when others then
        msg := 'Erreur de Mise à jour, Le stock est en cours de mise à jour, l''actualisation n''est pas effective';
        return;
    end;

    begin
        ls_rc := 'insert into geo_stock_hebdo (select L.art_ref, round(sum(L.exp_nb_col) / 6,0) ' ||
                'from geo_ordre O, geo_ordlig L where L.ord_ref = O.ord_ref	 ' ||
                'and to_char(O.depdatp,''yymmdd'') between to_char(sysdate - 7,''yymmdd'') and to_char(sysdate,''yymmdd'') ' ||
                'and O.FACTURE_AVOIR <> ''A'' group by L.art_ref)';
        EXECUTE IMMEDIATE to_char(ls_rc);
    end;

    res := 1;

end REFRESH_STOCK_HEBDO;
/

