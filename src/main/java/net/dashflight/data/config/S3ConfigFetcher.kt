package net.dashflight.data.config

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import java.io.IOException
import java.util.*

class S3ConfigFetcher : ConfigurationSource {
    override fun getConfig(applicationName: String?, env: RuntimeEnvironment?, additionalData: Map<String?, Any?>?): ConfigurationData<Properties> {
        return getPropertiesForApplication(applicationName, env, additionalData)
    }

    companion object {
        private const val BUCKET = "www.dashflight.net-config"
        private val s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(DefaultAWSCredentialsProviderChain())
                .build()

        private fun getPropertiesForApplication(applicationName: String?, env: RuntimeEnvironment?, additionalProperties: Map<String?, Any?>?): ConfigurationData<Properties> {
            val result: Properties
            val key = String.format("%s/%s.properties", applicationName, env?.name)
            result = try {
                val configFile = s3Client.getObject(GetObjectRequest(BUCKET, key))
                JavaPropsMapper().readValue(configFile.objectContent, Properties::class.java)
            } catch (e: IOException) {
                e.printStackTrace()
                Properties()
            }
            if (additionalProperties != null) {
                result.putAll(additionalProperties)
            }
            return PropertiesData(result)
        }
    }
}