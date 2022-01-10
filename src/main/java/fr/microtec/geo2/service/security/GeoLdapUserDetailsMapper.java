package fr.microtec.geo2.service.security;

import lombok.Getter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GeoLdapUserDetailsMapper extends LdapUserDetailsMapper {

    @Getter
    private final GeoUserDetailsService geo2UserDetailsService;

    public GeoLdapUserDetailsMapper(GeoUserDetailsService geo2UserDetailsService) {
        this.geo2UserDetailsService = geo2UserDetailsService;
    }

    /**
     * Load user detail from database after LDAP authentication.
     */
    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        return this.geo2UserDetailsService.loadUserByUsername(username);
    }
}
