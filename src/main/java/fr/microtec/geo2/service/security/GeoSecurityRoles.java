package fr.microtec.geo2.service.security;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class GeoSecurityRoles {

	/**
	 * Geo2 roles
	 */
	public enum Role { ROLE_ADMIN, ROLE_USER, ROLE_GEO_TIERS, ROLE_GEO_PRODUIT }

	/**
	 * Get geo2 roles for a user.
	 *
	 * @param geoUtilisateur Logged user
	 * @return List of authorities.
	 */
	public static List<GrantedAuthority> authoritiesFor(GeoUtilisateur geoUtilisateur) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(new SimpleGrantedAuthority(Role.ROLE_USER.toString()));

		if (geoUtilisateur.getAccessGeoTiers()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(Role.ROLE_GEO_TIERS.toString()));
		}
		if (geoUtilisateur.getAccessGeoProduct()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(Role.ROLE_GEO_PRODUIT.toString()));
		}

		return grantedAuthorities;
	}

}
