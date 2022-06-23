package fr.microtec.geo2.persistance.entity.stock;

import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_stock_consolid")
@Entity
@DynamicUpdate
public class GeoStockConsolide extends ModifiedEntity {

	@Id
	@Column(name = "art_ref")
	private String id;

	@Column(name = "commentaire")
	private String commentaire;

}
