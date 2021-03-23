package fr.microtec.geo2.persistance.entity.ordres;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_litige")
@Entity
public class GeoLitige extends ValidateAndModifiedEntity {
  
  @Id
	@Column(name = "lit_ref")
	private String id;

  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref_origine")
	private GeoOrdre ordreOrigine;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "litige")
	private List<GeoLitigeLigne> lignes;

  @Column(name = "lit_frais_annexes")
	private Float fraisAnnexes;

}
