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
    override fun send(specification: EmailSpecification) {
        val (from, recipients, subject, body) = specification

        val request = SendEmailRequest().apply {
            this.source = from
            this.destination = getDestination(recipients)

            message = Message().apply {
                this.subject = getSubject(subject)
                this.body = getBody(body)
            }
        }

        try {
            sesClient.sendEmail(request)
        } catch (e: AmazonSimpleEmailServiceException) {
            throw EmailSendException(e.message)
        }
    }

    private fun getDestination(addresses: List<String>): Destination {
        return Destination().withToAddresses(addresses)
    }

    private fun getSubject(subject: String): Content {
        return Content().withCharset("UTF-8").withData(subject)
    }

    private fun getBody(body: String): Body {
        return Body().withHtml(Content().withCharset("UTF-8").withData(body))
    }


    companion object {
        private val sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build()
    }
}