package net.dashflight.data.email

import com.google.inject.Inject
import net.dashflight.data.email.EmailClient.EmailSendException

class SendEmailService @Inject constructor(private val emailClient: EmailClient, private val provider: EmailSpecificationProvider) {
    @Throws(EmailSendException::class)
    fun send() {
        val specification = provider.create()
        emailClient.send(specification)
    }

}