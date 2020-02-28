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

public class S3ConfigFetcher {

    private static final String BUCKET = "www.dashflight.net-config";

    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                                            .withRegion(Regions.US_EAST_2)
                                            .withCredentials(new DefaultAWSCredentialsProviderChain())
                                            .build();


    public static Properties getPropertiesForApplication(String applicationName) {
        return getPropertiesForApplication(applicationName, RuntimeEnvironment.getCurrentEnvironment());
    }

    public static Properties getPropertiesForApplication(String applicationName, RuntimeEnvironment env) {
        return getPropertiesForApplication(applicationName, env, null);
    }

    public static Properties getPropertiesForApplication(String applicationName, RuntimeEnvironment env, Map<String, Object> additionalProperties) {
        Properties result;

        String key = String.format("%s/%s.properties", applicationName, env.getName());

        try {
            S3Object configFile = s3Client.getObject(new GetObjectRequest(BUCKET, key));
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
