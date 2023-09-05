CREATE OR REPLACE PROCEDURE "GEO_ADMIN".OF_CONTROLE_QTE_ART(
    arg_edi_ordre IN number,
    arg_cam_code IN GEO_ORDRE.CAM_CODE%TYPE,
    res OUT number,
    msg OUT varchar2
)
AS
    ls_gtin_art_cli varchar2(50);
    ls_controle_sel varchar2(50);
    ls_controle_qte varchar2(50);
    ls_gtin_client varchar2(50);
    ls_art_ref_client varchar2(50);
    ll_count_check number;
    ll_edi_lig number;
    ll_qte_art_cde number;
    ll_qte_valide number;
    ls_controle_qte_titre clob := 'Les quantités validées supérieures à celles commandées ~r~n';
    cursor C_ARTICLE is
        select distinct edi_lig
        from geo_stock_art_edi_bassin
        where edi_ord = arg_edi_ordre
        and cam_code = arg_cam_code
        and (fou_code  is not null or fou_code <> '')
        order by edi_lig;
BEGIN
    msg := '';
    res := 0;

    begin
        open C_ARTICLE;
        fetch C_ARTICLE into ll_edi_lig;
        loop

            begin
                select count(*), sum(qte_valide)
                into ll_count_check, ll_qte_valide
                from geo_stock_art_edi_bassin
                where edi_ord = arg_edi_ordre
                and cam_code = arg_cam_code
                and edi_lig = ll_edi_lig
                and choix = 'O'
                and (fou_code is not null or fou_code <> '');

                select ean_prod_client, code_interne_prod_client, quantite_colis
                into ls_gtin_client, ls_art_ref_client, ll_qte_art_cde
                from geo_edi_ligne
                where ref_edi_ligne = ll_edi_lig;
                if ls_gtin_client is not null then
                    ls_gtin_art_cli := ls_gtin_client;
                else
                    ls_gtin_art_cli := ls_art_ref_client;
                end if;

                if ll_qte_valide <> ll_qte_art_cde  then
                    ls_controle_qte := ls_controle_qte || '- GTIN/Article: ' || ls_gtin_art_cli || ' Qté cde: '  || to_char(ll_qte_art_cde) || ' / Qté validée: ' || to_char(ll_qte_valide) || '~r~n';
                end if;
            exception when others then
                null;
            end;

            fetch C_ARTICLE into ll_edi_lig;
            EXIT WHEN C_ARTICLE%notfound;
        end loop;
        close C_ARTICLE;
    exception when others then
        close C_ARTICLE;
        res := 0;
        msg := '%%%ERREUR of_controle_qte_art / edi_ord: ' || to_char(arg_edi_ordre);
        return;
    end;

    if ls_controle_qte is not null then
        ls_controle_qte := ls_controle_qte_titre || ls_controle_qte;
    end if;

    res := 1;
end OF_CONTROLE_QTE_ART;
/

