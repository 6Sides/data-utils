package net.dashflight.data;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public abstract class ConfigurableDataSource {

    private static final String BUCKET = "www.dashflight.net-config";

    private final RuntimeEnvironment ENVIRONMENT;

    /**
     * The name of the application is the s3 object it will look for to configure itself.
     */
    private final String APPLICATION_NAME;


    public ConfigurableDataSource(String applicationName) {
        this(applicationName, RuntimeEnvironment.getCurrentEnvironment());
    }

    public ConfigurableDataSource(String applicationName, RuntimeEnvironment env) {
        this(applicationName, env, null);
    }

    public ConfigurableDataSource(String applicationName, RuntimeEnvironment env, Map<String, Object> properties) {
        if (env == null) {
            throw new IllegalArgumentException("RuntimeEnvironment must be non null");
        }

        this.APPLICATION_NAME = applicationName;
        this.ENVIRONMENT = env;

        Properties props = this.fetchProperties(properties);
        new ValueInjector().inject(this, props);
    }

    private Properties fetchProperties(Map<String, Object> additionalProperties) {
        Properties result;

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .withRegion(Regions.US_EAST_2)
                .build();

        String key = String.format("%s/%s.properties", APPLICATION_NAME, ENVIRONMENT.getName());
        S3Object configFile = s3Client.getObject(new GetObjectRequest(BUCKET, key));

        try {
            result = new JavaPropsMapper().readValue(configFile.getObjectContent(), Properties.class);
        } catch (IOException e) {
            e.printStackTrace();
            result = new Properties();
        }

        if (additionalProperties != null) {
            result.putAll(additionalProperties);
        }

        return result;
    }
}
