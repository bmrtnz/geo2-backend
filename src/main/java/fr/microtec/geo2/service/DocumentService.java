package fr.microtec.geo2.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import fr.microtec.geo2.common.CustomUtils;
import io.leangen.graphql.execution.ResolutionEnvironment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.controller.FsDocumentType;
import fr.microtec.geo2.persistance.entity.document.GeoAsCMR;
import fr.microtec.geo2.persistance.entity.document.GeoAsCQDoc;
import fr.microtec.geo2.persistance.entity.document.GeoAsCQTechnique;
import fr.microtec.geo2.persistance.entity.document.GeoAsDocument;
import fr.microtec.geo2.persistance.entity.document.GeoAsEtiquette;
import fr.microtec.geo2.persistance.entity.document.GeoAsFacture;
import fr.microtec.geo2.persistance.entity.document.GeoBaseDocument;
import fr.microtec.geo2.persistance.entity.document.GeoDocument;
import fr.microtec.geo2.persistance.entity.produits.GeoArticle;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DocumentService {

    private final Maddog2FileSystemService maddog2FileSystemService;

    public DocumentService(Maddog2FileSystemService maddog2FileSystemService) {
        this.maddog2FileSystemService = maddog2FileSystemService;
    }

    public <T extends GeoBaseDocument> Optional<T> loadDocuments(Optional<T> optionalWithDocument, ResolutionEnvironment env) {
        optionalWithDocument.ifPresent(doc -> this.loadDocuments(doc, env));

        return optionalWithDocument;
    }

    public <T extends GeoBaseDocument> List<T> loadDocuments(List<T> listWithDocument, ResolutionEnvironment env) {
        return listWithDocument
                .stream()
                .map(doc -> this.loadDocuments(doc, env))
                .collect(Collectors.toList());
    }

    public <T extends GeoBaseDocument> RelayPage<T> loadDocuments(RelayPage<T> pageWithDocument, ResolutionEnvironment env) {
        pageWithDocument.getEdges().forEach(edge -> this.loadDocuments(edge.getNode(), env));

        return pageWithDocument;
    }

    public <T extends GeoBaseDocument> T loadDocuments(T entityAsDocument, ResolutionEnvironment env) {
        List<Class<?>> findDocumentClass = this.findDocumentClass(entityAsDocument);
        for (Class<?> documentClass : findDocumentClass) {
            this.loadDocumentWithClass(documentClass, entityAsDocument, env);
        }

        if (entityAsDocument instanceof GeoArticle) {
            if (((GeoArticle) entityAsDocument).getNormalisation().getEtiquetteUc() != null) {
                this.loadDocumentWithClass(
                        GeoAsEtiquette.class,
                        ((GeoArticle) entityAsDocument).getNormalisation().getEtiquetteUc(),
                        env
                );
            }
            if (((GeoArticle) entityAsDocument).getNormalisation().getEtiquetteEvenementielle() != null) {
                this.loadDocumentWithClass(
                        GeoAsEtiquette.class,
                        ((GeoArticle) entityAsDocument).getNormalisation().getEtiquetteEvenementielle(),
                        env
                );
            }
            if (((GeoArticle) entityAsDocument).getNormalisation().getEtiquetteColis() != null) {
                this.loadDocumentWithClass(
                        GeoAsEtiquette.class,
                        ((GeoArticle) entityAsDocument).getNormalisation().getEtiquetteColis(),
                        env
                );
            }
            if (((GeoArticle) entityAsDocument).getNormalisation().getStickeur() != null) {
                this.loadDocumentWithClass(
                        GeoAsEtiquette.class,
                        ((GeoArticle) entityAsDocument).getNormalisation().getStickeur(),
                        env
                );
            }
        }

        return entityAsDocument;
    }

    private void loadDocumentWithClass(Class<?> clazz, GeoBaseDocument entityAsDocument, ResolutionEnvironment env) {
        boolean requestDocument = CustomUtils.parseSelectFromEnv(env).contains(this.getDocumentProperty(clazz));
        System.out.println("Document property contains document : " + (requestDocument ? "True" : "False"));
        if (!requestDocument) {
            return;
        }

        GeoDocument document = new GeoDocument();
        boolean isEtiquette = entityAsDocument instanceof GeoAsEtiquette;

        try {
            Path doc = this.getDocument(clazz, entityAsDocument);
            String filename = isEtiquette ? doc.getFileName().toString() : doc.toString();

            log.info(
                    "Load {} '{}'",
                    isEtiquette ? "etiquette" : "document",
                    this.getDocumentName(clazz, entityAsDocument));

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
            UriComponents uriComponents = uriBuilder
                    .path("/file-manager")
                    .path("/")
                    .path(this.getDocumentType(clazz, entityAsDocument).getKey())
                    .path("/")
                    .path(Base64.getEncoder().encodeToString(filename.getBytes())).build();

            // Set document type (image as img else iframe)
            String mimeType = Files.probeContentType(doc);
            boolean isImg = (mimeType != null && "image".equals(mimeType.split("/")[0]));

            document.setIsPresent(true);
            document.setUri(uriComponents.toUriString());
            document.setType(isImg ? "img" : "iframe");
        } catch (Exception e) {
            document.setIsPresent(false);
        }

        this.setDocument(clazz, entityAsDocument, document);
    }

    private static final List<Class<? extends GeoBaseDocument>> managedClasses = Arrays.asList(
            GeoAsDocument.class, GeoAsFacture.class, GeoAsCMR.class, GeoAsCQTechnique.class, GeoAsCQDoc.class);

    private List<Class<?>> findDocumentClass(GeoBaseDocument entity) {
        return managedClasses.stream()
                .filter(clazz -> clazz.isAssignableFrom(entity.getClass()))
                .collect(Collectors.toList());
    }

    private Path getDocument(Class<?> clazz, GeoBaseDocument entity) {
        Path path;

        if (GeoAsFacture.class.equals(clazz)) {
            path = ((GeoAsFacture) entity).getDocumentFactureWithMaddogService(this.maddog2FileSystemService);
        } else if (GeoAsDocument.class.isAssignableFrom(clazz)) { // Handle GeoAsEtiquette
            path = ((GeoAsDocument) entity).getDocumentWithMaddogService(this.maddog2FileSystemService);
        } else if (GeoAsCMR.class.equals(clazz)) {
            path = ((GeoAsCMR) entity).getDocumentCRMWithMaddogService(this.maddog2FileSystemService);
        } else if (GeoAsCQTechnique.class.equals(clazz)) {
            path = ((GeoAsCQTechnique) entity).getCqTechniqueWithMaddogService(this.maddog2FileSystemService);
        } else if (GeoAsCQDoc.class.equals(clazz)) {
            path = ((GeoAsCQDoc) entity).getCqDocWithMaddogService(this.maddog2FileSystemService);
        } else {
            throw new RuntimeException(
                    String.format("DocumentService can't load document on entity %s, please map this new document type",
                            clazz.getSimpleName()));
        }

        return path;
    }

    private String getDocumentName(Class<?> clazz, GeoBaseDocument entity) {
        String name;

        if (GeoAsFacture.class.equals(clazz)) {
            name = ((GeoAsFacture) entity).getDocumentFactureName();
        } else if (GeoAsDocument.class.isAssignableFrom(clazz)) { // Handle GeoAsEtiquette
            name = ((GeoAsDocument) entity).getDocumentName();
        } else if (GeoAsCMR.class.equals(clazz)) {
            name = ((GeoAsCMR) entity).getDocumentCMRName();
        } else if (GeoAsCQTechnique.class.equals(clazz)) {
            name = ((GeoAsCQTechnique) entity).getCqTechniqueName();
        } else if (GeoAsCQDoc.class.equals(clazz)) {
            name = ((GeoAsCQDoc) entity).getCqDocPath();
        } else {
            throw new RuntimeException(
                    String.format("DocumentService can't load document on entity %s, please map this new document type",
                            clazz.getSimpleName()));
        }

        return name;
    }

    private void setDocument(Class<?> clazz, GeoBaseDocument entity, GeoDocument doc) {
        if (GeoAsFacture.class.equals(clazz)) {
            ((GeoAsFacture) entity).setDocumentFacture(doc);
        } else if (GeoAsDocument.class.isAssignableFrom(clazz)) { // Handle GeoAsEtiquette
            ((GeoAsDocument) entity).setDocument(doc);
        } else if (GeoAsCMR.class.equals(clazz)) {
            ((GeoAsCMR) entity).setDocumentCMR(doc);
        } else if (GeoAsCQTechnique.class.equals(clazz)) {
            ((GeoAsCQTechnique) entity).setCqTechnique(doc);
        } else if (GeoAsCQDoc.class.equals(clazz)) {
            ((GeoAsCQDoc) entity).setCqDoc(doc);
        } else {
            throw new RuntimeException(
                    String.format("DocumentService can't set document on entity %s, please map this new document type",
                            clazz.getSimpleName()));
        }
    }

    private FsDocumentType getDocumentType(Class<?> clazz, GeoBaseDocument entity) {
        Maddog2FileSystemService.PATH_KEY key;

        if (GeoAsFacture.class.equals(clazz)) {
            key = ((GeoAsFacture) entity).getDocumentFacturePathKey();
        } else if (GeoAsDocument.class.isAssignableFrom(clazz)) { // Handle GeoAsEtiquette
            key = ((GeoAsDocument) entity).getDocumentPathKey();
        } else if (GeoAsCMR.class.equals(clazz)) {
            key = ((GeoAsCMR) entity).getDocumentCMRPathKey();
        } else if (GeoAsCQTechnique.class.equals(clazz)) {
            key = ((GeoAsCQTechnique) entity).getCqTechniquePathKey();
        } else if (GeoAsCQDoc.class.equals(clazz)) {
            key = ((GeoAsCQDoc) entity).getCqDocPathKey();
        } else {
            throw new RuntimeException(String.format(
                    "DocumentService can't load document type on entity %s, please map this new document type",
                    clazz.getSimpleName()));
        }

        return FsDocumentType.fromPathKey(key);
    }

    private String getDocumentProperty(Class<?> clazz) {
        String name;

        if (GeoAsFacture.class.equals(clazz)) {
            name = "documentFactureName";
        } else if (GeoAsDocument.class.isAssignableFrom(clazz)) {
            name = "documentName";
        } else if (GeoAsCMR.class.equals(clazz)) {
            name = "documentCMRName";
        } else if (GeoAsCQTechnique.class.equals(clazz)) {
            name = "cqTechniqueName";
        } else if (GeoAsCQDoc.class.equals(clazz)) {
            name = "cqDocPath";
        } else {
            throw new RuntimeException(
                String.format("DocumentService can't load document on entity %s, please map this new document type",
                    clazz.getSimpleName()));
        }

        return name;
    }

}
