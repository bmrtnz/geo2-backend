package fr.microtec.geo2.service.fs;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

/**
 * This service is used for access to maddog2 NAS file system.
 */
@Service
@Secured("ROLE_USER")
public class Maddog2FileSystemService extends FileSystemService {

    /**
     * PATH_KEY use by client, it's use to order files.
     */
    public enum PATH_KEY {
        CLIENTS("/Geo2_doc/tiers/clients/"),
        FOURNISSEURS("/Geo2_doc/tiers/fournisseurs/"),
        TRANSPORTEURS("/Geo2_doc/tiers/transporteurs/"),
        LIEUX_PASSAGE_A_QUAI("/Geo2_doc/tiers/lieuxpassageaquai/"),
        ARTICLES("/Geo2_doc/articles/"),
        ORDRES("/Geo2_doc/ordres/"),
        GEO_IMG("/geo_img/"),
        GEO_DOC("/geo_doc/"),
        GEO_FACTURE("/geo_factures/"),
        GEO_RETOUR_PALOX("/geo_retour_palox/"),
        GEO_CQ("/geo_cq/"),
        GEO_CQ_PHOTOS("/cqpho/"),
        GEO_CQ_PDF("/cqpdf/"),
        GEO_IMPORT_GB("/geo_import_gb/"),
        LITIGES("/Geo2_doc/litiges/");

        private String path;

        // private String urlPath;
        PATH_KEY(String basePath/* , String urlPath */) {
            this.path = basePath;
            /* this.urlPath = urlPath; */ }

        public static PATH_KEY from(String s) {
            for (PATH_KEY pathKey : PATH_KEY.values()) {
                if (pathKey.name().equalsIgnoreCase(s))
                    return pathKey;
            }

            throw new IllegalArgumentException();
        }
    }

    public Maddog2FileSystemService(@Value("${geo2.maddog2.path}") String basePath) {
        super();
        this.basePath = basePath;
    }

    /**
     * Build base relative path for given path_key and id.
     */
    public String buildKeyIdPath(String key, String id) {
        return PATH_KEY.from(key).path + id + "/";
    }

    /**
     * Get path of document from given path in pathKey.
     * Download args :
     * true : get full path for access to file
     * false: get relative path for download via FileManagerController
     */
    public Path getDocument(PATH_KEY pathKey, String path, boolean download) {
        Path file = this._getBasePath(pathKey.path).resolve(path);

        if (!Files.exists(file)) {
            throw new FileSystemNotFoundException();
        }

        return download ? file : Path.of(path);
    }

    /**
     * Get Path of label file from given name, with extension or not.
     */
    public Path getEtiquette(String filename) {
        boolean withExtension = filename.matches(HAVE_EXTENSION_REGEX);
        String globPattern = "glob:**/" + filename + (withExtension ? "" : ".{pdf,jpg}");
        List<Path> files = this.list(PATH_KEY.GEO_IMG.path, FileSystems.getDefault().getPathMatcher(globPattern));
        Path downloadFile;

        // If as multiple file, pdf is priority
        if (files.size() > 1) {
            boolean asPdf = files.stream().anyMatch(f -> f.toString().endsWith(".pdf"));

            if (asPdf) {
                downloadFile = files.stream().filter(f -> f.toString().endsWith(".pdf")).findFirst().get();
            } else {
                downloadFile = files.get(0);
            }
        } else if (files.size() == 1) {
            downloadFile = files.get(0);
        } else {
            throw new FileSystemNotFoundException();
        }

        return downloadFile;
    }

}
