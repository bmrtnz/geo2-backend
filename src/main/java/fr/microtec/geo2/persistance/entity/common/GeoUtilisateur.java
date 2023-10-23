package fr.microtec.geo2.persistance.entity.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import fr.microtec.geo2.persistance.converter.BooleanIntegerConverter;
import fr.microtec.geo2.persistance.entity.ValidateAndModifiedEntity;
import fr.microtec.geo2.persistance.entity.tiers.GeoPersonne;
import fr.microtec.geo2.persistance.entity.tiers.GeoRole;
import fr.microtec.geo2.persistance.entity.tiers.GeoSecteur;
import fr.microtec.geo2.service.security.GeoSecurityRoles;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.val;

@Getter
@Setter
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

    @Column(name = "acces_cde_edi")
    private Boolean accessCommandeEdi;

    @Column(name = "admin_client", insertable = false, updatable = false)
    private Boolean adminClient;

    @Column(insertable = false, updatable = false)
    private String perimetre;

    @Column(name = "geo_client")
    private Character geoClient;

    @Convert(converter = BooleanIntegerConverter.class)
    @Column(name = "flag_limiter_secteur", insertable = false, updatable = false)
    private Boolean limitationSecteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sco_code", insertable = false, updatable = false)
    private GeoSecteur secteurCommercial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_codecom")
    private GeoPersonne assistante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "per_codeass")
    private GeoPersonne commercial;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumnOrFormula(column = @JoinColumn(name = "nom_utilisateur", referencedColumnName = "per_username", insertable = false, updatable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "valide", value = "'O'"))
    private GeoPersonne personne;

    @Column(name = "profile_client", insertable = false, updatable = false)
    private String profileClient;

    @Column(name = "ind_visu_incot_log")
    private Boolean indicateurVisualisationIncotermFournisseur;

    @Column(name = "periode")
    private String periode;

    @Column(name = "flag_comment_stock")
    @Convert(converter = BooleanIntegerConverter.class)
    private Boolean commentaireStock;

    @Column(name = "filtre_rech_stock_edi")
    private Character filtreRechercheStockEdi;

    @Column(name = "ind_report_ach")
    private Boolean reportPrixAchat;

    @Column(name = "ind_report_vte")
    private Boolean reportPrixVente;

    @Column(name = "ind_report_prop")
    private Boolean reportProprietaire;

    @Column(name = "ind_report_exp")
    private Boolean reportExpediteur;

    @Column(name = "ind_report_pal")
    private Boolean reportTypePalette;

    @Column(name = "ind_bar_def_ht")
    private Boolean barreDefilementHaut;

    @Column(name = "ind_bar_def_bs")
    private Boolean barreDefilementBas;

    @Column(name = "ind_plandp_dif_exp")
    private Boolean diffSurExpedition;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nomUtilisateur")
    private List<GeoParamUserClientRestriction> restrictions;

    @Lob
    @Column(name = "config_tuiles_ordres", columnDefinition = "BLOB")
    private HashMap<String, Object> configTuilesOrdres;

    @Lob
    @Column(name = "config_tabs_ordres", columnDefinition = "BLOB")
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
        return GeoSecurityRoles.authoritiesFor(this);
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

    public boolean isAdmin() {
        return this.getProfileClient() == "ADMIN";
    }

    /**
     * Get personne by role
     */
    public GeoPersonne getPersonneByRole() {
        val personne = Optional.ofNullable(this.getPersonne());
        if (personne.isPresent()) {
            val role = Optional.ofNullable(personne.get().getRole());
            if (role.isPresent()) {
                if (role.get().equals(GeoRole.ASSISTANT))
                    return this.getAssistante();
                if (role.get().equals(GeoRole.COMMERCIAL))
                    return this.getCommercial();
                return personne.get();
            }
        }
        return null;
    }

    /**
     * Get personne user by role
     */
    public Optional<GeoUtilisateur> getUtilisateurByRole() {
        try {
            return Optional.ofNullable(this.getPersonneByRole().getUtilisateur());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
