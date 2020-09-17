package fr.microtec.geo2.persistance.entity.stock;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GeoStockArticleAgeKey implements Serializable {

	protected String article;
	protected String age;

}
