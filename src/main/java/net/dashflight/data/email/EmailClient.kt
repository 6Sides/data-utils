package net.dashflight.data.email;

/**
 * Interface used for clients that send emails.
 */
public interface EmailClient {

    /**
     * Sends and email.
     *
     * @param specification The spec outlining the contents / metadata of the email
     *
     * @throws EmailSendException If the email couldn't be sent / failed to send.
     */
    void send(EmailSpecification specification) throws EmailSendException;


    class EmailSendException extends Exception {
        public EmailSendException(String message) {
            super(message);
        }
    }
}
