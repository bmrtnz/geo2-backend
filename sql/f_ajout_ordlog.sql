-- f_ajout_ordlog

CREATE OR REPLACE PROCEDURE "GEO_ADMIN"."F_AJOUT_ORDLOG" (
    arg_orx_ref GEO_ORDLOG.orx_ref%type,
    arg_typ_passage GEO_ORDLOG.typ_fou%type,
    arg_ch_passage GEO_ORDLOG.grp_code%type,
	res out number,
    msg out varchar2
) AS
    ls_ord_ref varchar2(50);
    ls_new_sequence varchar2(50);
    ldt_depdatp timestamp;
    ldt_datliv_grp timestamp;
    ls_inc_code varchar2(50);
begin

    msg := '';
    res := 0;

    select olo.ord_ref, olo.datliv_grp, o.inc_code
    into ls_ord_ref, ldt_datliv_grp, ls_inc_code
    from geo_ordlog olo
    left join geo_ordre o on o.ord_ref = olo.ord_ref
    where olo.orx_ref = arg_orx_ref
    order by olo.datdep_fou_p, olo.fou_code;

    -- Selectionne "datdep_grp_p" du precedent ORDLOG
    SELECT previous_date
    INTO ldt_depdatp
    FROM (
            SELECT
            olo.orx_ref,
            olo.datdep_grp_p,
            LAG(olo.datdep_grp_p) OVER (ORDER BY olo.datdep_fou_p, olo.fou_code) AS previous_date
        FROM
            geo_ordlog olo
        LEFT JOIN geo_ordre o ON
            o.ord_ref = olo.ord_ref
        WHERE
            olo.ord_ref = ls_ord_ref
        ORDER BY
            olo.datdep_fou_p,
            olo.fou_code
    )
    WHERE orx_ref = arg_orx_ref;
    
    if arg_ch_passage is not null then 
        select F_SEQ_ORX_SEQ into ls_new_sequence from dual;
        begin
            insert into geo_ordlog 
                (orx_ref, ord_ref, fou_code, datdep_fou_p, typ_fou, datliv_grp, typ_grp, grp_code, orx_rat, incot_fourn)
                values (ls_new_sequence, ls_ord_ref, arg_ch_passage, ldt_depdatp, arg_typ_passage, ldt_datliv_grp, '', '','',ls_inc_code);
        exception when others then
            msg := 'pb sur insert logistique ' || arg_ch_passage || ' ' || SQLERRM;
            return;
        end;

        declare
            CURSOR C_ORX_LOG is
                select ORX_REF
                from geo_ordlog 
                where ORD_REF = ls_ord_ref and grp_code = arg_ch_passage;
        begin
            for r in C_ORX_LOG
            loop
                UPDATE geo_ordlog
                SET ORX_RAT = ls_new_sequence
                WHERE ORX_REF = r.orx_ref and ORD_REF = ls_ord_ref;
            end loop;
        end;
        commit;
    end if;

    msg := 'OK';
    res := 1;
    return;

end;