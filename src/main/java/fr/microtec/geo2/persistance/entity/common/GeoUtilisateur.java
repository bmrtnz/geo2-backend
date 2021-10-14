package fr.microtec.geo2.persistance.entity.common;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.persistance.security.Geo2SecurityRoles;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_user")
@Entity
public class GeoUtilisateur extends ValidateAndModifiedEntity implements UserDetails {

	@Id
	@Column(name = "nom_utilisateur", insertable = false, updatable = false)
	private String nomUtilisateur;

	@Column(insertable = false, updatable = false)
	private String email;

	@Column(name = "mot_de_passe", insertable = false, updatable = false)
	private String motDePasse;

	@Column(name = "nom_interne", insertable = false, updatable = false)
	private String nomInterne;

	@Column(name = "geo_tiers", insertable = false, updatable = false)
	private Boolean accessGeoTiers;

	@Column(name = "geo_produit", insertable = false, updatable = false)
	private Boolean accessGeoProduct;

	@Column(name = "geo_ordre", insertable = false, updatable = false)
	private Boolean accessGeoOrdre;

	@Column(name = "geo_facture", insertable = false, updatable = false)
	private Boolean accessGeoFacture;

	@Column(name = "admin_client", insertable = false, updatable = false)
	private Boolean adminClient;

	@Column(insertable = false, updatable = false)
	private String perimetre;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "flag_limiter_secteur", insertable = false, updatable = false)
	private Boolean limitationSecteur;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sco_code", insertable = false, updatable = false)
	private GeoSecteur secteurCommercial;

	@Lob
	@Column(name = "config_tuiles_ordres", columnDefinition = "BLOB")
	private HashMap<String, Object> configTuilesOrdres;

	@Lob
	@Column(name = "config_tabs_ordres", columnDefinition = "CLOB")
	private HashMap<String, Object> configTabsOrdres;

	@Override
	public String getUsername() {
		return this.getNomUtilisateur();
	}

	@Override
	public String getPassword() {
		return this.getMotDePasse();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Geo2SecurityRoles.authoritiesFor(this);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.getValide();
	}
}
