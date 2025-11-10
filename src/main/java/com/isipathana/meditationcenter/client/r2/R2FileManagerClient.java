package com.isipathana.meditationcenter.client.r2;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Client for managing file operations on Cloudflare R2 (S3-compatible) storage.
 * <p>
 * Provides methods for uploading and deleting files and folders.
 * Uses AWS SDK S3Client under the hood.
 *
 * @author Claude Code
 */
public class R2FileManagerClient {

    private final S3Client s3Client;
    private final Logger logger;

    public R2FileManagerClient(S3Client s3Client, Logger logger) {
        this.s3Client = s3Client;
        this.logger = logger;
    }

    /**
     * Uploads a file from local filesystem to R2.
     *
     * @param bucketName The name of the R2 bucket
     * @param key        The object key (path) in R2
     * @param filePath   The local file path to upload
     * @return true if upload successful, false otherwise
     */
    public boolean uploadFile(String bucketName, String key, Path filePath) {
        try {
            logger.info("Uploading file to R2: bucket={}, key={}, path={}", bucketName, key, filePath);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(filePath));

            logger.info("Successfully uploaded file to R2: bucket={}, key={}", bucketName, key);
            return true;

        } catch (S3Exception e) {
            logger.error("Failed to upload file to R2: bucket={}, key={}, error={}", bucketName, key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Uploads a Spring MultipartFile to R2.
     * <p>
     * This is the primary method used by REST controllers handling file uploads.
     *
     * @param bucketName The name of the R2 bucket
     * @param key        The object key (path) in R2
     * @param file       The multipart file from HTTP request
     * @return true if upload successful, false otherwise
     */
    public boolean uploadFile(String bucketName, String key, MultipartFile file) {
        try {
            logger.info("Uploading multipart file to R2: bucket={}, key={}, filename={}, size={}",
                    bucketName, key, file.getOriginalFilename(), file.getSize());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            logger.info("Successfully uploaded multipart file to R2: bucket={}, key={}", bucketName, key);
            return true;

        } catch (IOException e) {
            logger.error("Failed to read multipart file: bucket={}, key={}, error={}", bucketName, key, e.getMessage(), e);
            return false;
        } catch (S3Exception e) {
            logger.error("Failed to upload multipart file to R2: bucket={}, key={}, error={}", bucketName, key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Deletes a single file from R2.
     *
     * @param bucketName The name of the R2 bucket
     * @param key        The object key (path) to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteFile(String bucketName, String key) {
        try {
            logger.info("Deleting file from R2: bucket={}, key={}", bucketName, key);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            logger.info("Successfully deleted file from R2: bucket={}, key={}", bucketName, key);
            return true;

        } catch (S3Exception e) {
            logger.error("Failed to delete file from R2: bucket={}, key={}, error={}", bucketName, key, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Deletes all files under a folder (prefix) in R2.
     * <p>
     * Uses list-then-delete approach since R2/S3 doesn't support recursive delete.
     *
     * @param bucketName The name of the R2 bucket
     * @param folderKey  The folder prefix (should end with /)
     * @return true if all deletions successful, false if any failed
     */
    public boolean deleteFolder(String bucketName, String folderKey) {
        try {
            logger.info("Deleting folder from R2: bucket={}, folderKey={}", bucketName, folderKey);

            // List all objects under the folder
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folderKey)
                    .build();

            ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

            if (listResponse.contents().isEmpty()) {
                logger.info("No objects found in folder: bucket={}, folderKey={}", bucketName, folderKey);
                return true;
            }

            // Build list of object identifiers to delete
            List<ObjectIdentifier> objectsToDelete = new ArrayList<>();
            for (S3Object s3Object : listResponse.contents()) {
                objectsToDelete.add(ObjectIdentifier.builder()
                        .key(s3Object.key())
                        .build());
            }

            // Delete all objects in batch
            Delete delete = Delete.builder()
                    .objects(objectsToDelete)
                    .build();

            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(delete)
                    .build();

            DeleteObjectsResponse deleteResponse = s3Client.deleteObjects(deleteRequest);

            logger.info("Successfully deleted {} objects from folder: bucket={}, folderKey={}",
                    deleteResponse.deleted().size(), bucketName, folderKey);

            // Check for errors
            if (!deleteResponse.errors().isEmpty()) {
                logger.error("Some objects failed to delete: {}", deleteResponse.errors());
                return false;
            }

            return true;

        } catch (S3Exception e) {
            logger.error("Failed to delete folder from R2: bucket={}, folderKey={}, error={}",
                    bucketName, folderKey, e.getMessage(), e);
            return false;
        }
    }
}
