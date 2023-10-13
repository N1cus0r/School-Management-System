package com.example.schoolmanagementsystem.s3;

import org.apache.commons.io.FileUtils;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.IoUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FakeS3 implements S3Client {
    @Override
    public String serviceName() {
        return "fake";
    }

    @Override
    public void close() {
    }

    @Override
    public PutObjectResponse putObject(
            PutObjectRequest putObjectRequest,
            RequestBody requestBody
    ) throws AwsServiceException, SdkClientException {
        InputStream inputStream = requestBody.contentStreamProvider().newStream();
        try {
            byte[] bytes = IoUtils.toByteArray(inputStream);
            FileUtils.writeByteArrayToFile(
                    new File(
                            buildObjectsFullPath(
                                    putObjectRequest.bucket(),
                                    putObjectRequest.key())
                    ),
                    bytes
            );
            return PutObjectResponse.builder().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getObject(
            GetObjectRequest getObjectRequest
    ) throws AwsServiceException, SdkClientException {
        try {
            FileInputStream fileInputStream = new FileInputStream(
                    buildObjectsFullPath(
                            getObjectRequest.bucket(),
                            getObjectRequest.key())
            );
            return new ResponseInputStream<>(
                    GetObjectResponse.builder().build(),
                    fileInputStream
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DeleteObjectResponse deleteObject(
            DeleteObjectRequest deleteObjectRequest
    ) throws AwsServiceException, SdkClientException{
        String filePath = buildObjectsFullPath(
                deleteObjectRequest.bucket(),
                deleteObjectRequest.key());

        Path pathObj = Paths.get(filePath);

        if (Files.exists(pathObj) && Files.isRegularFile(pathObj)) {
            try {
                Files.delete(pathObj);

                return DeleteObjectResponse.builder()
                        .deleteMarker(true)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("File not found");
        }
    }

    private String buildObjectsFullPath(
            String bucketName,
            String key
    ) {
        ClassLoader classLoader = getClass().getClassLoader();
        File resourcesDirectory = new File(classLoader.getResource("").getFile());
        return resourcesDirectory + File.separator + bucketName + File.separator + key;
    }
}
