package fr.microtec.geo2.persistance.security;

import fr.microtec.geo2.persistance.entity.GeoUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class Geo2SecurityRoles {

	/**
	 * Geo2 roles
	 */
	public enum Role { ROLE_ADMIN, ROLE_GEO_TIERS, ROLE_GEO_PRODUIT }

	/**
	 * Get geo2 roles for a user.
	 *
	 * @param geoUser Logged user
	 * @return List of authorities
	 */
	public static List<GrantedAuthority> authoritiesFor(GeoUser geoUser) {
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

		if (geoUser.isGeoTiers()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(Role.ROLE_GEO_TIERS.toString()));
		}
		if (geoUser.isGeoProduit()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(Role.ROLE_GEO_PRODUIT.toString()));
		}

		return grantedAuthorities;
	}

}
