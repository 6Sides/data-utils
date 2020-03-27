package net.dashflight.data.postgres;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.flywaydb.core.Flyway;

/**
 * Handles creating a flyway instance with the required configuration.
 *
 * Caches s3 files locally to reduce overhead.
 */
public class FlywayManager {

    private static final String BUCKET = "www.dashflight.net-config";

    private static final AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_2)
            .withCredentials(new DefaultAWSCredentialsProviderChain())
            .build();


    private final PostgresClient postgresClient;


    public FlywayManager() throws NoSuchAlgorithmException, IOException {
        this(new DashflightPostgresConnectionOptionProvider("flyway-migration"));
    }

    public FlywayManager(PostgresConnectionOptionProvider provider)
            throws IOException, NoSuchAlgorithmException {
        this(new PostgresClient(provider));
    }

    public FlywayManager(PostgresClient postgresClient) throws IOException, NoSuchAlgorithmException {
        if (!new File(Paths.get("flyway", "db").toString()).exists()) {
            Files.createDirectories(Paths.get("flyway", "db"));
        }

        ObjectListing listing = s3Client.listObjects(BUCKET, "flyway-migration-definitions/migrations.tar.gz");

        for (S3ObjectSummary obj : listing.getObjectSummaries()) {
            if (!obj.getKey().equals("flyway-migration-definitions/migrations.tar.gz")) {
                continue;
            }

            File sourceFile = new File(Paths.get("flyway", "migrations.tar.gz").toString());

            if (areFilesEqual(sourceFile, obj)) {
                FileUtils.cleanDirectory(Paths.get("flyway", "db").toFile());
            } else {
                writeObjectToFile(obj, sourceFile);
            }

            untarFile();
        }

        this.postgresClient = postgresClient;
    }

    private void untarFile() throws IOException {
        TarArchiveInputStream tarInput = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(
                Paths.get("flyway", "migrations.tar.gz").toString())));
        TarArchiveEntry currentEntry = tarInput.getNextTarEntry();
        BufferedReader br;

        while (currentEntry != null) {
            br = new BufferedReader(new InputStreamReader(tarInput)); // Read directly from tarInput

            File targetFile = Paths.get("flyway", "db", currentEntry.getName()).toFile();

            if (currentEntry.isDirectory()) {
                if (!targetFile.exists()) {
                    targetFile.mkdirs();
                }
            } else {
                // Create the file if it doesn't exist
                targetFile.createNewFile();

                try (FileWriter writer = new FileWriter(targetFile)) {
                    String line;

                    while ((line = br.readLine()) != null) {
                        writer.write(line);
                        writer.write("\n");
                    }
                }
            }

            currentEntry = tarInput.getNextTarEntry();
        }
    }


    private void writeObjectToFile(S3ObjectSummary obj, File targetFile) throws IOException {
        try (S3Object versionDef = s3Client.getObject(new GetObjectRequest(BUCKET, obj.getKey()))) {
            InputStream input = versionDef.getObjectContent();
            OutputStream outStream = new FileOutputStream(targetFile);

            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
    }

    private boolean areFilesEqual(File localFile, S3ObjectSummary s3Object) throws NoSuchAlgorithmException {
        if (!localFile.exists()) {
            return false;
        }

        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes;

        try {
            bytes = Files.readAllBytes(localFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String localFileHash = DatatypeConverter.printHexBinary(md.digest(bytes));

        boolean lengthsAreEqual = localFile.length() == s3Object.getSize();
        boolean hashesAreEqual = localFileHash.equals(s3Object.getETag().toUpperCase());

        return lengthsAreEqual && hashesAreEqual;
    }

    public Flyway getFlyway() {
        String location = "filesystem:" + new File(Paths.get("flyway", "db").toString()).getAbsolutePath();

        return Flyway.configure()
                .dataSource(postgresClient.getDataSource())
                .loadDefaultConfigurationFiles()
                .locations(location)
                .load();
    }
}
