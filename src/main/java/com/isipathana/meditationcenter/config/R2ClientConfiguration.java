package com.isipathana.meditationcenter.config;

import com.isipathana.meditationcenter.client.r2.R2FileManagerClient;
import com.isipathana.meditationcenter.client.r2.R2PresignerClient;
import com.isipathana.meditationcenter.config.properties.R2ClientProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configuration for Cloudflare R2 (S3-compatible) object storage.
 * <p>
 * Creates the necessary beans for R2 file management:
 * - S3Client for uploading/downloading/deleting files
 * - S3Presigner for generating presigned URLs
 * - R2FileManagerClient wrapper for file operations
 * - R2PresignerClient wrapper for URL generation
 * <p>
 * Only activated when r2.enabled=true in application.properties.
 */
@Configuration
@EnableConfigurationProperties(R2ClientProperties.class)
@ConditionalOnProperty(value = "r2.enabled", havingValue = "true")
@RequiredArgsConstructor
public class R2ClientConfiguration {

    private final R2ClientProperties properties;
    private final Logger logger = LoggerFactory.getLogger(R2ClientConfiguration.class);

    /**
     * Creates the S3Client bean configured for Cloudflare R2.
     * <p>
     * Uses AWS SDK S3Client with R2 endpoint and credentials.
     */
    @Bean
    public S3Client s3Client() {
        logger.info("Configuring S3Client for Cloudflare R2: endpoint={}", properties.endpoint());

        return S3Client.builder()
                .region(Region.of("auto")) // R2 uses "auto" region
                .endpointOverride(URI.create(properties.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                properties.accessKeyId(),
                                properties.secretAccessKey()
                        )
                ))
                .build();
    }

    /**
     * Creates the S3Presigner bean configured for Cloudflare R2.
     * <p>
     * Used to generate presigned URLs for temporary access to objects.
     */
    @Bean
    public S3Presigner s3Presigner() {
        logger.info("Configuring S3Presigner for Cloudflare R2");

        return S3Presigner.builder()
                .region(Region.of("auto"))
                .endpointOverride(URI.create(properties.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                properties.accessKeyId(),
                                properties.secretAccessKey()
                        )
                ))
                .build();
    }

    /**
     * Creates the R2FileManagerClient bean for file upload/download/delete operations.
     */
    @Bean
    public R2FileManagerClient r2FileManagerClient(S3Client s3Client) {
        logger.info("Creating R2FileManagerClient bean");
        return new R2FileManagerClient(s3Client, logger);
    }

    /**
     * Creates the R2PresignerClient bean for generating presigned URLs.
     */
    @Bean
    public R2PresignerClient r2PresignerClient(S3Presigner s3Presigner) {
        logger.info("Creating R2PresignerClient bean");
        return new R2PresignerClient(s3Presigner, logger);
    }
}
