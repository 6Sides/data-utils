package net.dashflight.data.email;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.ConfigurationSetDoesNotExistException;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.MailFromDomainNotVerifiedException;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.util.List;

/**
 * Sends emails from AWS SES
 */
public class SESEmailClient implements EmailClient {

    private static final AmazonSimpleEmailService sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                                                                .withRegion(Regions.US_EAST_1).build();


    @Override
    public void send(EmailSpecification specification) throws EmailSendException {
        String from = specification.getFrom();
        List<String> recipients = specification.getRecipients();
        String subject = specification.getSubject();
        String body = specification.getBody();

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(getDestination(recipients))
                .withMessage(new Message()
                        .withSubject(getSubject(subject))
                        .withBody(getBody(body)))
                .withSource(from);

        try {
            sesClient.sendEmail(request);
        } catch (MessageRejectedException | MailFromDomainNotVerifiedException | ConfigurationSetDoesNotExistException e) {
            throw new EmailSendException(e.getMessage());
        }
    }

    private Destination getDestination(List<String> addresses) {
        return new Destination().withToAddresses(addresses);
    }

    private Content getSubject(String subject) {
        return new Content().withCharset("UTF-8").withData(subject);
    }

    private Body getBody(String body) {
        return new Body().withHtml(new Content().withCharset("UTF-8").withData(body));
    }
}
