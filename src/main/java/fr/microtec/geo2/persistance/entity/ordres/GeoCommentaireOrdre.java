package fr.microtec.geo2.persistance.entity.ordres;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_comm_ordre")
@IdClass(GeoCommentaireOrdreKey.class)
@Entity
public class GeoCommentaireOrdre extends ModifiedEntity {
  
  @Id
	@Column(name = "comm_ord_ref")
	private String id;

	@Id
  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ord_ref",nullable = false)
	private GeoOrdre ordre;

	@Column(name = "comm_ordre")
	private String commentaires;
  
}
