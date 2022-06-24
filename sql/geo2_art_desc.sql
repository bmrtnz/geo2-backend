create or replace view geo2_art_desc
as
select
    g.art_ref,
    g.art_ref || ' ' || m.VAR_CODE || ' cat ' || c.CAT_CODE ||
    DECODE(n.CAM_CODE, null, '', ' cal ' || n.CAM_CODE) ||
    DECODE(e.COL_CODE, null, '', ' col ' || e.COL_CODE) ||
    case
        when e.PDNET_CLIENT > 0 then ' ' || to_char(e.PDNET_CLIENT) || 'kg'
        else ''
    end ||
    DECODE(e.COL_PREPESE, 'N', ' NT', '') ||
    case
        when m.MODE_CULTURE is not null and m.MODE_CULTURE <> 0 then ' ' || mc.LIBELLE
        else ''
    end ||
    case
        when not(m.ORI_CODE = '-' or m.ORI_CODE = 'F') then ' ori ' || o.ORI_DESC
        else ''
    end as art_desc_long_ref,
    m.VAR_CODE || ' cat ' || c.CAT_CODE ||
    DECODE(n.CAM_CODE, null, '', ' cal ' || n.CAM_CODE) ||
    DECODE(e.COL_CODE, null, '', ' col ' || e.COL_CODE) ||
    case
        when e.PDNET_CLIENT > 0 then ' ' || to_char(e.PDNET_CLIENT) || 'kg'
        else ''
        end ||
    DECODE(e.COL_PREPESE, 'N', ' NT', '') ||
    case
        when m.MODE_CULTURE is not null and m.MODE_CULTURE <> 0 then ' ' || mc.LIBELLE
        else ''
        end ||
    case
        when not(m.ORI_CODE = '-' or m.ORI_CODE = 'F') then ' ori ' || o.ORI_DESC
        else ''
        end as art_desc_long,
    g.art_ref || ' ' || COALESCE(g.ART_ALPHA, '') as art_desc_court_ref,
    COALESCE(g.ART_ALPHA, '') as art_desc_court,
    DECODE(instr(lower(mc.LIBELLE), 'bio'), 0, 'N', 'O') as art_bio
FROM AVI_ART_GESTION g
INNER JOIN AVI_ART_MAT_PREM m on m.REF_MAT_PREM = g.REF_MAT_PREM
INNER JOIN AVI_ART_CDC c on c.REF_CDC = g.REF_CDC
INNER JOIN AVI_ART_NORMALISATION n on n.REF_NORMALISATION = g.REF_NORMALISATION
INNER JOIN AVI_ART_EMBALLAGE e on e.REF_EMBALLAGE = g.REF_EMBALLAGE
INNER JOIN GEO_MODE_CULTURE mc on mc.REF = m.MODE_CULTURE
INNER JOIN geo_origine O ON o.ORI_CODE = m.ORI_CODE and o.ESP_CODE = m.ESP_CODE;
/
