package fr.microtec.geo2.persistance.entity.ordres;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class GeoOrdreBaf {

    @Id
    @Column(name = "ORD_REF")
    private String ordreRef;

    @Column(name = "NORDRE")
    private String numeroOrdre;

    @Column(name = "RAISOC")
    private String clientRaisonSocial;

    /*@Column(name = "RAISOC")
    private String clientRaisonSocial;

    @Column(name = "liv_dat")
    private

    @Column(name = "liv_dat_tri")
    private


    dw_tab_ordre_baf.object.client_raisoc[ll_row] = ls_client_raisoc
    dw_tab_ordre_baf.object.entrep_raisoc[ll_row] = ls_entrep_raisoc
    dw_tab_ordre_baf.object.transp_raisoc[ll_row] = 	ls_transp_raisoc
    dw_tab_ordre_baf.object.ref_cli[ll_row] = 	ls_ref_cli*/

}
