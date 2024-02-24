package fr.microtec.geo2.persistance.entity.traductions;

import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.produits.GeoVariete;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "geo_variet_desc_trad")
@IdClass(GeoVarieteTraductionId.class)
public class GeoVarieteTraduction extends ValidateAndModifiedEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "var_code")
    private GeoVariete variete;

    @Id
    @Column(name = "lan_code")
    private String langue;

    @Column(name = "var_desc")
    private String description;

    public String getId() {
        return this.getVariete().getId();
    }

}
