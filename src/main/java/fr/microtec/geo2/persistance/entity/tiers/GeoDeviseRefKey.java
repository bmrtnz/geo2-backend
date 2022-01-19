package fr.microtec.geo2.persistance.entity.tiers;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoDeviseRefKey implements Serializable {

	protected String id;
	protected String devise;

}