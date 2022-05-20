package fr.microtec.geo2.configuration;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class SmtpConfiguration {

    private final MailProperties mailProperties;

    public SmtpConfiguration(MailProperties properties) {
        this.mailProperties = properties;
    }

    @Bean
    public GreenMail greenMail() {
        ServerSetup setup = new ServerSetup(
                this.mailProperties.getPort(),
                this.mailProperties.getHost(),
                ServerSetup.PROTOCOL_SMTP
        );

        GreenMail greenMail = new GreenMail(setup);
        // greenMail.setUser(this.mailProperties.getUsername(), this.mailProperties.getPassword()).create();

        return greenMail;
    }

}
