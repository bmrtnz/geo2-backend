package fr.microtec.geo2.persistance.entity.stock;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoStockArticleAgeKey implements Serializable {

    protected String article;
    protected String age;

}
