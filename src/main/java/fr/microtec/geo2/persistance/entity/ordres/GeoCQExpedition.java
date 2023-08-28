package fr.microtec.geo2.persistance.entity.ordres;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ModifiedEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_cqexp")
@Entity
public class GeoCQExpedition extends ModifiedEntity implements Serializable {

  @Id
  @Column(name = "cql_ref")
  private String id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cql_ref", insertable = false, updatable = false)
  private GeoCQLigne ligne;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "orl_ref")
  private GeoOrdreLigne ordreLigne;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_type_pal")
  private Boolean typePaletteOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_etat_pal")
  private Boolean etatPaletteOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_pal_pcf")
  private Boolean PCFOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_type_col")
  private Boolean typeColisOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_nbr_col")
  private Boolean nombreColisOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_fiche_pal")
  private Boolean fichePaletteOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_etiq_col")
  private Boolean etiquetteColisOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_lis_etiq_c")
  private Boolean lisibiliteEtiquetteColisOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_type_bel")
  private Boolean typeBoxEndLabelOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_lis_bel")
  private Boolean lisibiliteBoxEndLabelOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_type_sac")
  private Boolean typeSacOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_variete")
  private Boolean varieteOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_nbr_fruit")
  private Boolean nombreFruitsOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_type_etiq_sac")
  private Boolean typeEtiquetteSacOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_lis_etiq_s")
  private Boolean lisibiliteEtiquetteOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_nbr_uc_col")
  private Boolean nombreUCColisOK;

  @Convert(converter = BooleanIntegerConverter.class)
  @Column(name = "ok_homo_col")
  private Boolean homogeneiteColisOK;

}
