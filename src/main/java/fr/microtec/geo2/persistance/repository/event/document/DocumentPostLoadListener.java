package fr.microtec.geo2.persistance.repository.event.document;

import fr.microtec.geo2.controller.FsCommand;
import fr.microtec.geo2.controller.FsDocumentType;
import fr.microtec.geo2.persistance.repository.event.Geo2LoadEventListener;
import fr.microtec.geo2.persistance.repository.event.document.GeoAsDocument;
import fr.microtec.geo2.persistance.repository.event.document.GeoAsEtiquette;
import fr.microtec.geo2.persistance.repository.event.document.GeoDocument;
import fr.microtec.geo2.service.fs.Maddog2FileSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Component
@Slf4j
public class DocumentPostLoadListener implements Geo2LoadEventListener<GeoAsDocument> {

    private ObjectFactory<Maddog2FileSystemService> fileSystemServiceFactory;

    @Autowired
    public void setFileSystemService(ObjectFactory<Maddog2FileSystemService> factory) {
        this.fileSystemServiceFactory = factory;
    }

    @Override
    public void onLoad(GeoAsDocument entity) {
        GeoDocument document = new GeoDocument();
        boolean isEtiquette = entity instanceof GeoAsEtiquette;

        Maddog2FileSystemService fileSystemService = this.fileSystemServiceFactory.getObject();
        try {
            // File
            Path doc = entity.getDocumentWithMaddogService(fileSystemService);
            String filename = isEtiquette ? doc.getFileName().toString() : doc.toString();

            // Uri
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance();
            UriComponents uriComponents = uriBuilder
                .path("/file-manager")
                .path("/")
                .path(this.getDocumentType(entity).getKey())
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
            entity.getDocumentName(),
            document.getIsPresent() ? "Found" : "Not Found");

        if (document.getIsPresent())
            entity.setDocument(document);
    }

    private FsDocumentType getDocumentType(GeoAsDocument entity) {
        boolean isEtiquette = entity instanceof GeoAsEtiquette;

        if (isEtiquette) return FsDocumentType.ETIQUETTE;

        return entity.getDocumentPathKey().equals(Maddog2FileSystemService.PATH_KEY.GEO_IMG)
            ? FsDocumentType.IMAGE
            : FsDocumentType.DOCUMENT;
    }

}
