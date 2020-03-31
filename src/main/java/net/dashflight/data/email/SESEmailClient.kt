package net.dashflight.data.email

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.*
import net.dashflight.data.email.EmailClient.EmailSendException

/**
 * Sends emails from AWS SES
 */
class SESEmailClient : EmailClient {
    @Throws(EmailSendException::class)
    override fun send(specification: EmailSpecification?) {
        val from = specification?.from
        val recipients = specification?.recipients
        val subject = specification?.subject
        val body = specification?.body

        val request = SendEmailRequest()
                .withDestination(getDestination(recipients))
                .withMessage(Message()
                        .withSubject(getSubject(subject))
                        .withBody(getBody(body)))
                .withSource(from)
        try {
            sesClient.sendEmail(request)
        } catch (e: MessageRejectedException) {
            throw EmailSendException(e.message)
        } catch (e: MailFromDomainNotVerifiedException) {
            throw EmailSendException(e.message)
        } catch (e: ConfigurationSetDoesNotExistException) {
            throw EmailSendException(e.message)
        }
    }

    private fun getDestination(addresses: List<String>?): Destination {
        return Destination().withToAddresses(addresses!!)
    }

    private fun getSubject(subject: String?): Content {
        return Content().withCharset("UTF-8").withData(subject!!)
    }

    private fun getBody(body: String?): Body {
        return Body().withHtml(Content().withCharset("UTF-8").withData(body!!))
    }

    companion object {
        private val sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build()
    }
}