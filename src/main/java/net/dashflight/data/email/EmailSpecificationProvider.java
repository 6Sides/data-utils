package net.dashflight.data.email;

/**
 *  Creates EmailSpecifications for use by EmailClient implementations
 */
public interface EmailSpecificationProvider {

    EmailSpecification create();

}
