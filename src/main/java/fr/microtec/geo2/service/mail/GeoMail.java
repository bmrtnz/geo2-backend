package fr.microtec.geo2.service.mail;

import lombok.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class GeoMail {

    private String from;
    private String subject;
    @Singular("addTo")
    private List<String> to;
    @Singular("addCc")
    private List<String> cc;
    @Singular("addBcc")
    private List<String> bcc;
    private String template;
    @Singular("addTemplateContext")
    private Map<String, Object> templateContext;
    @Singular("addAttachment")
    private Map<String, Path> attachments;

}
