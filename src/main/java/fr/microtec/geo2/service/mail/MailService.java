package fr.microtec.geo2.service.mail;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;
    /*@Autowired
    private MustacheAutoConfiguration mustacheAutoConfiguration;*/

    @Autowired
    private Mustache.TemplateLoader mustacheTemplateLoader;
    @Autowired
    private Mustache.Compiler mustacheCompiler;

    public GeoMail.GeoMailBuilder newMail() {
        GeoMail.GeoMailBuilder builder = GeoMail.builder();

        this.setDefaults(builder);

        return builder;
    }

    public void send(GeoMail.GeoMailBuilder builder) {
        this.send(builder.build());
    }

    public void send(Consumer<GeoMail.GeoMailBuilder> consumer) {
        GeoMail.GeoMailBuilder builder = new GeoMail.GeoMailBuilder();
        this.setDefaults(builder);

        consumer.accept(builder);

        this.send(builder);
    }

    public void send(GeoMail geoMail) {
        try {
            boolean hasAttachment = !geoMail.getAttachments().isEmpty();

            MimeMessage mail = this.emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, hasAttachment);

            helper.setFrom(geoMail.getFrom());
            helper.setSubject(geoMail.getSubject());
            helper.setTo(geoMail.getTo().toArray(String[]::new));
            helper.setCc(geoMail.getCc().toArray(String[]::new));
            helper.setBcc(geoMail.getBcc().toArray(String[]::new));
            helper.setText(this.parseTemplate(geoMail.getTemplate(), geoMail.getTemplateContext()), true);

            if (hasAttachment) {
                for (Map.Entry<String, Path> entry : geoMail.getAttachments().entrySet()) {
                    helper.addAttachment(entry.getKey(), new FileSystemResource(entry.getValue()));
                }
            }

            this.emailSender.send(mail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseTemplate(String templateName, Map<String, Object> context) throws MessagingException {
        try {
            final Reader template = this.mustacheTemplateLoader.getTemplate(templateName);

            return this.mustacheCompiler
                    .compile(template)
                    .execute(context);
        } catch (Exception e) {
            throw new MessagingException(e.getMessage(), e);
        }
    }

    private void setDefaults(GeoMail.GeoMailBuilder mailBuilder) {
        mailBuilder
                .from("noreplay@bluewhale.fr")
                .template("simple")
                .addTemplateContext("layout", new Layout(this.mustacheCompiler));
    }

    static class Layout implements Mustache.Lambda {
        String body;

        private final Mustache.Compiler compiler;

        public Layout(Mustache.Compiler compiler) {
            this.compiler = compiler;
        }

        @Override
        public void execute(Template.Fragment fragment, Writer writer) throws IOException {
            body = fragment.execute();

            this.compiler.compile("{{>layout}}").execute(fragment.context(), writer);
        }
    }
}
