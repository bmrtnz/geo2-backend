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

@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "geo_user")
@Entity
public class GeoUtilisateur extends ValidateAndModifiedEntity implements UserDetails {

	@Id
	@Column(name = "nom_utilisateur")
	private String nomUtilisateur;

	@Column
	private String email;

	@Column(name = "mot_de_passe")
	private String motDePasse;

	@Column(name = "nom_interne")
	private String nomInterne;

	@Column(name = "geo_tiers")
	private Boolean accessGeoTiers;

	@Column(name = "geo_produit")
	private Boolean accessGeoProduct;

	@Column(name = "geo_ordre")
	private Boolean accessGeoOrdre;

	@Column(name = "geo_facture")
	private Boolean accessGeoFacture;

	@Column
	private String perimetre;

	@Convert(converter = BooleanIntegerConverter.class)
	@Column(name = "flag_limiter_secteur")
	private Boolean limitationSecteur;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sco_code")
	private GeoSecteur secteurCommercial;

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
