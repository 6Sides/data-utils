package net.dashflight.data.email

/**
 * Creates EmailSpecifications for use by EmailClient implementations
 */
interface EmailSpecificationProvider {

    fun create(): EmailSpecification

}