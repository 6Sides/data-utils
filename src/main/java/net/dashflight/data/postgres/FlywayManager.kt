package net.dashflight.data.postgres

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3ObjectSummary
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.FileUtils
import org.flywaydb.core.Flyway
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.xml.bind.DatatypeConverter

/**
 * Handles creating a flyway instance with the required configuration.
 *
 * Caches s3 files locally to reduce overhead.
 */
class FlywayManager(postgresClient: PostgresClient) {
    private val postgresClient: PostgresClient

    @JvmOverloads
    constructor(provider: PostgresConnectionOptionProvider = DashflightPostgresConnectionOptionProvider("flyway-migration")) : this(PostgresClient(provider)) {
    }

    @Throws(IOException::class)
    private fun untarFile() {
        val tarInput = TarArchiveInputStream(GzipCompressorInputStream(FileInputStream(
                Paths.get("flyway", "migrations.tar.gz").toString())))
        var currentEntry = tarInput.nextTarEntry
        var br: BufferedReader
        while (currentEntry != null) {
            br = BufferedReader(InputStreamReader(tarInput)) // Read directly from tarInput
            val targetFile = Paths.get("flyway", "db", currentEntry.name).toFile()
            if (currentEntry.isDirectory) {
                if (!targetFile.exists()) {
                    targetFile.mkdirs()
                }
            } else {
                // Create the file if it doesn't exist
                targetFile.createNewFile()
                FileWriter(targetFile).use { writer ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        writer.write(line)
                        writer.write("\n")
                    }
                }
            }
            currentEntry = tarInput.nextTarEntry
        }
    }

    @Throws(IOException::class)
    private fun writeObjectToFile(obj: S3ObjectSummary, targetFile: File) {
        s3Client.getObject(GetObjectRequest(BUCKET, obj.key)).use { versionDef ->
            val input: InputStream = versionDef.objectContent
            val outStream: OutputStream = FileOutputStream(targetFile)
            val buffer = ByteArray(8 * 1024)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                outStream.write(buffer, 0, bytesRead)
            }
        }
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun areFilesEqual(localFile: File, s3Object: S3ObjectSummary): Boolean {
        if (!localFile.exists()) {
            return false
        }
        val md = MessageDigest.getInstance("MD5")
        val bytes: ByteArray
        bytes = try {
            Files.readAllBytes(localFile.toPath())
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        val localFileHash = DatatypeConverter.printHexBinary(md.digest(bytes))
        val lengthsAreEqual = localFile.length() == s3Object.size
        val hashesAreEqual = localFileHash == s3Object.eTag.toUpperCase()
        return lengthsAreEqual && hashesAreEqual
    }

    val flyway: Flyway
        get() {
            val location = "filesystem:" + File(Paths.get("flyway", "db").toString()).absolutePath
            return Flyway.configure()
                    .dataSource(postgresClient.dataSource)
                    .loadDefaultConfigurationFiles()
                    .locations(location)
                    .load()
        }

    companion object {
        private const val BUCKET = "www.dashflight.net-config"
        private val s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_2)
                .withCredentials(DefaultAWSCredentialsProviderChain())
                .build()
    }

    init {
        if (!File(Paths.get("flyway", "db").toString()).exists()) {
            Files.createDirectories(Paths.get("flyway", "db"))
        }
        val listing = s3Client.listObjects(BUCKET, "flyway-migration-definitions/migrations.tar.gz")
        for (obj in listing.objectSummaries) {
            if (obj.key != "flyway-migration-definitions/migrations.tar.gz") {
                continue
            }
            val sourceFile = File(Paths.get("flyway", "migrations.tar.gz").toString())
            if (areFilesEqual(sourceFile, obj)) {
                FileUtils.cleanDirectory(Paths.get("flyway", "db").toFile())
            } else {
                writeObjectToFile(obj, sourceFile)
            }
            untarFile()
        }
        this.postgresClient = postgresClient
    }
}