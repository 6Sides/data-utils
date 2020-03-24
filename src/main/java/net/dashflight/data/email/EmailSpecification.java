package net.dashflight.data.email;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailSpecification {

    /**
     * The value in the `from` field of the email
     */
    private String from;

    /**
     * The email address of the recipient
     */
    private List<String> recipients;

    /**
     * Subject line of the email
     */
    private String subject;

    /**
     * Contents of the email. Can be plain text or formatted as html
     */
    private String body;

}
