package fr.microtec.geo2.persistance.entity.produits;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "geo2_art_desc")
public class Geo2ArticleDescription {

    @Id
    @Column(name = "art_ref")
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "art_ref")
    private GeoArticle article;

    @Column(name = "art_desc_long")
    private String descriptionLongue;

    @Column(name = "art_desc_long_ref")
    private String descriptionReferenceLongue;

    @Column(name = "art_desc_court")
    private String descriptionCourte;

    @Column(name = "art_desc_court_ref")
    private String descriptionReferenceCourte;

    @Column(name = "art_bio")
    private Boolean bio;

}
