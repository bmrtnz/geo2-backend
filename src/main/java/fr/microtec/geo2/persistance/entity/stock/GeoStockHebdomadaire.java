package fr.microtec.geo2.persistance.entity.stock;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Table(name = "geo_hebdo")
@Entity
public class GeoStockHebdomadaire {

	@Id
	@Column(name = "art_ref")
	private String id;

	@Column(name = "qte_hebdo")
	private Float quantiteHebdomadaire;

}
