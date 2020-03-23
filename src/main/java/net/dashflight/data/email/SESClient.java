package net.dashflight.data.email;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.MailFromDomainNotVerifiedException;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.MessageRejectedException;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class SESClient {

    private static final String DEFAULT_FROM = "no-reply";

    private static final AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                                                            .withRegion(Regions.US_EAST_1).build();

    private final String from;


    public SESClient() {
        this(DEFAULT_FROM);
    }

    /**
     * Constructs a client to send email from SES.
     *
     * @param from The part before @dashflight.net. E.g. specifying `no-reply` will send the email from `no-reply@dashflight.net`
     */
    public SESClient(String from) {
        this.from = from;
    }


    public void sendEmail(EmailSpecification specification) throws MessageRejectedException, MailFromDomainNotVerifiedException {

        String recipient = specification.getRecipient();
        String subject = specification.getSubject();
        String body = specification.getBody();


        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(recipient))
                .withMessage(new Message()
                        .withSubject(new Content().withCharset("UTF-8").withData(subject))
                        .withBody(new Body()
                                .withHtml(new Content().withCharset("UTF-8").withData(body))))
                .withSource(String.format("%s@dashflight.net", this.from));

        client.sendEmail(request);
    }

}
