package net.dashflight.data.email;

import com.google.inject.Inject;
import net.dashflight.data.email.EmailClient.EmailSendException;


public class SendEmailService {

    private EmailClient emailClient;
    private EmailSpecificationProvider provider;

    @Inject
    public SendEmailService(EmailClient emailClient, EmailSpecificationProvider provider) {
        this.emailClient = emailClient;
        this.provider = provider;
    }


    public void send() throws EmailSendException {
        EmailSpecification specification = provider.create();
        emailClient.send(specification);
    }

}
