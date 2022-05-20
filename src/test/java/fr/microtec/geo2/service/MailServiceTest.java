package fr.microtec.geo2.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import fr.microtec.geo2.Geo2Application;
import fr.microtec.geo2.configuration.SmtpConfiguration;
import fr.microtec.geo2.persistance.entity.ordres.GeoOrdre;
import fr.microtec.geo2.service.mail.MailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest/*(classes = {Geo2Application.class, SmtpConfiguration.class})*/
public class MailServiceTest {

    private static final String SUBJECT = "Dummy Subject";
    private static final String TO = "to@test.fr";

    @Autowired
    private MailService mailService;
    @Autowired
    private GreenMail greenMail;

    @BeforeAll
    public void startGreenMail() {
        this.greenMail.start();
    }

    @BeforeEach
    public void resetGreenMail() {
        this.greenMail.reset();
    }

    @AfterAll
    public void stopGreenMail() {
        this.greenMail.stop();
    }

    @Test
    public void testSendMailTest() {
        String expectedBody = "Short message";
        GreenMailUtil.sendTextEmailTest(TO, "from@test.fr", "Subject", expectedBody);
        String body = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);

        assertEquals(expectedBody, body);
    }

    @Test
    public void testSendMailWithTemplate() {
        GeoOrdre ordre = new GeoOrdre();
        ordre.setId("000000000100000000");
        String expectedBody = "<html>\n<body>\n" +
                String.format("<h1>%s</h1>\n", ordre.getId()) +
                "</body>\n</html>";

        this.mailService.send(mail -> mail
                .addTo("to@test.fr")
                .subject(SUBJECT)
                .template("test-simple")
                .addTemplateContext("ordre", ordre)
        );

        String body = GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]);
        assertEquals(StringUtils.trimAllWhitespace(expectedBody), StringUtils.trimAllWhitespace(body));
    }

    @Test
    public void testSendMailWithTemplateLayout() throws MessagingException {
        GeoOrdre ordre = new GeoOrdre();
        ordre.setId("000000000100000000");
        String expectedBody = "<html>\n<head></head>\n<body>\n" +
                String.format("<h1>%s</h1>\n", ordre.getId()) +
                "</body>\n</html>";

        this.mailService.send(mail -> mail
                    .addTo("to@test.fr")
                    .subject(SUBJECT)
                    .template("test-layout")
                    .addTemplateContext("ordre", ordre)
        );

        MimeMessage message = greenMail.getReceivedMessages()[0];
        String body = GreenMailUtil.getBody(message);

        assertTrue(Arrays.stream(message.getAllRecipients()).anyMatch(e -> e.toString().equals("to@test.fr")));
        assertEquals(SUBJECT, message.getSubject());
        assertEquals(StringUtils.trimAllWhitespace(expectedBody), StringUtils.trimAllWhitespace(body));
    }

    // TODO Test attachments

}
