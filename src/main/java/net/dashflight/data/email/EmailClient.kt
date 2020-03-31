package net.dashflight.data.email

import net.dashflight.data.email.EmailClient.EmailSendException

/**
 * Interface used for clients that send emails.
 */
interface EmailClient {
    /**
     * Sends and email.
     *
     * @param specification The spec outlining the contents / metadata of the email
     *
     * @throws EmailSendException If the email couldn't be sent / failed to send.
     */
    @Throws(EmailSendException::class)
    fun send(specification: EmailSpecification?)
    class EmailSendException(message: String?) : Exception(message)
}