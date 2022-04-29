package fr.microtec.geo2.service.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * GeoAllowAllPasswordEncoder allow any password.
 *
 * WARNING : ONLY FOR DEV MODE !!!
 */
public class GeoAllowAllPasswordEncoder implements PasswordEncoder {

    public static PasswordEncoder getInstance() {
        return INSTANCE;
    }
    private static final PasswordEncoder INSTANCE = new GeoAllowAllPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return true;
    }
}
