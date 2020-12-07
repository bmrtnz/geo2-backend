package fr.microtec.geo2.persistance.entity.stock;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.*;

import fr.microtec.geo2.persistance.entity.Duplicable;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoClient;
import fr.microtec.geo2.persistance.entity.tiers.GeoPersonne;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.entity.tiers.GeoSociete;
import fr.microtec.geo2.persistance.entity.tiers.GeoTransporteur;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_ordre")
@DynamicInsert
@DynamicUpdate
@Entity
public class GeoOrdre extends ValidateAndModifiedEntity implements Duplicable<GeoOrdre> {

	@Id
	@Column(name = "ord_ref")
	@GeneratedValue(generator = "GeoOrdreGenerator")
	@GenericGenerator(name = "GeoOrdreGenerator", strategy = "fr.microtec.geo2.persistance.GeoSequenceGenerator", parameters = {
			@Parameter(name = "sequenceName", value = "seq_ord_num"),
			@Parameter(name = "mask", value = "FM0999999")
		})
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "soc_code")
	private GeoSociete societe;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sco_code")
	private GeoSecteur secteurCommercial;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trp_code", nullable = false)
	private GeoTransporteur transporteur;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cli_ref", nullable = false)
	private GeoClient client;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ordre")
	private List<GeoOrdreLigne> lignes;

	@NotNull
	@Column(name = "nordre", nullable = false, unique = true)
	private String numero;

	@Column(name = "ref_cli")
	private String referenceClient;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codeass")
	private GeoPersonne assistante;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_codecom")
	private GeoPersonne commercial;

	@Column(name = "depdatp")
	private LocalDate dateDepartPrevue;

	@Column(name = "livdatp")
	private LocalDate dateLivraisonPrevue;

	@Column(name = "vente_commission")
	private Boolean venteACommission;

	@Column(name = "flexp")
	private Boolean expedie;

	@Column(name = "flliv")
	private Boolean livre;

	@Column(name = "flbaf")
	private Boolean bonAFacturer;

	@Column(name = "flfac")
	private Boolean facture;

	@Column(name = "invoic_demat")
	private Boolean factureEDI;

	@Column(name = "instructions_logistique")
	private String instructionsLogistiques;

	public GeoOrdre duplicate() {
		GeoOrdre clone = new GeoOrdre();
		clone.societe = this.societe;
		clone.secteurCommercial = this.secteurCommercial;
		clone.transporteur = this.transporteur;
		clone.client = this.client;
		clone.referenceClient = this.referenceClient;
		clone.assistante = this.assistante;
		clone.commercial = this.commercial;
		
		return clone;
	}

}