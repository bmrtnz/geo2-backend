package fr.microtec.geo2.service;

import fr.microtec.geo2.configuration.graphql.RelayPage;
import fr.microtec.geo2.controller.FsDocumentType;
import fr.microtec.geo2.persistance.entity.document.GeoAsDocument;
import fr.microtec.geo2.persistance.entity.document.GeoAsEtiquette;
import fr.microtec.geo2.persistance.entity.document.GeoDocument;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentService {

    private final Maddog2FileSystemService maddog2FileSystemService;

    public DocumentService(Maddog2FileSystemService maddog2FileSystemService) {
        this.maddog2FileSystemService = maddog2FileSystemService;
    }

    public <T extends GeoAsDocument> Optional<T> loadDocument(Optional<T> optionalWithDocument) {
        optionalWithDocument.ifPresent(this::loadDocument);

        return optionalWithDocument;
    }

    public <T extends GeoAsDocument> List<T> loadDocument(List<T> listWithDocument) {
        return listWithDocument
            .stream()
            .map(this::loadDocument)
            .collect(Collectors.toList());
    }

    public <T extends GeoAsDocument> RelayPage<T> loadDocument(RelayPage<T> pageWithDocument) {
        pageWithDocument.getEdges().forEach(edge -> this.loadDocument(edge.getNode()));

        return pageWithDocument;
    }

    public <T extends GeoAsDocument> T loadDocument(T entityAsDocument) {
        GeoDocument document = new GeoDocument();
        boolean isEtiquette = entityAsDocument instanceof GeoAsEtiquette;

        try {
            // File
            Path doc = entityAsDocument.getDocumentWithMaddogService(this.maddog2FileSystemService);
            String filename = isEtiquette ? doc.getFileName().toString() : doc.toString();

            // Uri
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
            UriComponents uriComponents = uriBuilder
                .path("/file-manager")
                .path("/")
                .path(this.getDocumentType(entityAsDocument).getKey())
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

        log.info(
            "Search {} '{}' : {}",
            isEtiquette ? "etiquette" : "document",
            entityAsDocument.getDocumentName(),
            document.getIsPresent() ? "Found" : "Not Found");

        entityAsDocument.setDocument(document);

        return entityAsDocument;
    }

    private FsDocumentType getDocumentType(GeoAsDocument entity) {
        return FsDocumentType.fromPathKey(entity.getDocumentPathKey());
    }

}
