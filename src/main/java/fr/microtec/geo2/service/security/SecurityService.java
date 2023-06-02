package fr.microtec.geo2.service.security;

import fr.microtec.geo2.persistance.entity.common.GeoUtilisateur;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.text.Normalizer;
import java.time.Duration;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    private Duration timeout;

    public SecurityService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.timeout = Duration.ofHours(2);
    }

    /**
     * Authenticate user.
     */
    public GeoUtilisateur login(String login, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(normalizeLogin(login),
                password);
        GeoUtilisateur user = null;

        try {
            Authentication auth = this.authenticationManager.authenticate(authReq);

            user = (GeoUtilisateur) auth.getPrincipal();

            // Set in context
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(auth);

            // Set context in session
            if (request != null) {
                RequestContextHolder.currentRequestAttributes().getSessionId();
                HttpSession session = request.getSession(true);
                session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
                session.setMaxInactiveInterval((int) timeout.toSeconds());
            }
        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            String msg = "Unknown error";

            if (BadCredentialsException.class.equals(ex.getClass())) {
                msg = "Bad credential on LDAP server";
            } else if (UsernameNotFoundException.class.equals(ex.getClass())) {
                msg = "Can't find user in database";
            }

            throw new SecurityException(msg, ex);
        }

        return user;
    }

    /**
     * Get current connected user.
     */
    public GeoUtilisateur getUser() {
        return (GeoUtilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Normalize login for search in ldap/database (no accent, uppercase).
     *
     * @param login User login
     * @return Normalized login
     */
    public static String normalizeLogin(String login) {
        return Normalizer
                .normalize(login, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toUpperCase();
    }
}
