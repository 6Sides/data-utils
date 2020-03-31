package net.dashflight.data.email

/**
 * The value in the `from` field of the email
 */

/**
 * The email address of the recipient
 */

/**
 * Subject line of the email
 */

/**
 * Contents of the email. Can be plain text or formatted as html
 */
data class EmailSpecification(val from: String, val recipients: List<String>, val subject: String, val body: String)