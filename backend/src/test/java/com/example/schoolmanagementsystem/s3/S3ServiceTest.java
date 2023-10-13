package com.example.schoolmanagementsystem.s3;

import com.example.schoolmanagementsystem.AbstractServiceTest;
import com.example.schoolmanagementsystem.exception.RequestValidationError;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = S3Service.class)
class S3ServiceTest extends AbstractServiceTest {
    @MockBean
    private S3Client s3Client;

    @Autowired
    private S3Service s3Service;


    @Test
    void puObject()  throws IOException {
        String bucketName = FAKER.lorem().word();
        String key = FAKER.lorem().word();
        byte[] bytes = FAKER.lorem().sentence().getBytes();

        s3Service.putObject(bucketName, key, bytes);

        ArgumentCaptor<PutObjectRequest> putObjectRequestArgumentCaptor =
                ArgumentCaptor.forClass(PutObjectRequest.class);

        ArgumentCaptor<RequestBody> requestBodyArgumentCaptor =
                ArgumentCaptor.forClass(RequestBody.class);

        verify(s3Client).putObject(
                putObjectRequestArgumentCaptor.capture(),
                requestBodyArgumentCaptor.capture()
        );

        PutObjectRequest putObjectRequestArgumentCaptorValue =
                putObjectRequestArgumentCaptor.getValue();

        assertThat(putObjectRequestArgumentCaptorValue.bucket())
                .isEqualTo(bucketName);
        assertThat(putObjectRequestArgumentCaptorValue.key())
                .isEqualTo(key);

        RequestBody requestBodyArgumentCaptorValue =
                requestBodyArgumentCaptor.getValue();

        assertThat(
                requestBodyArgumentCaptorValue.contentStreamProvider().newStream().readAllBytes()
        ).isEqualTo(
                RequestBody.fromBytes(bytes).contentStreamProvider().newStream().readAllBytes()
        );
    }

    @Test
    void getObject() throws IOException{
        String bucketName = FAKER.lorem().word();
        String key = FAKER.lorem().word();
        byte[] bytes = FAKER.lorem().sentence().getBytes();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> response =
                mock(ResponseInputStream.class);

        when(s3Client.getObject(eq(getObjectRequest)))
                .thenReturn(response);

        when(response.readAllBytes())
                .thenReturn(bytes);

        byte[] resultBytes = s3Service.getObject(bucketName, key);

        assertThat(resultBytes)
                .isEqualTo(bytes);
    }

    @Test
    void getObjectThrowsError() throws IOException{
        String bucketName = FAKER.lorem().word();
        String key = FAKER.lorem().word();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> response =
                mock(ResponseInputStream.class);

        when(s3Client.getObject(eq(getObjectRequest)))
                .thenReturn(response);

        when(response.readAllBytes())
                .thenThrow(new IOException("Cannot read bytes"));

        assertThatThrownBy(() -> s3Service.getObject(bucketName, key))
                .isInstanceOf(RequestValidationError.class)
                .hasMessage("Unable to retrieve file");
    }

    @Test
    void deleteObject() {
        String bucketName = FAKER.lorem().word();
        String key = FAKER.lorem().word();

        s3Service.deleteObject(bucketName, key);

        ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestArgumentCaptor =
                ArgumentCaptor.forClass(DeleteObjectRequest.class);

        verify(s3Client).deleteObject(deleteObjectRequestArgumentCaptor.capture());

        DeleteObjectRequest deleteObjectRequestArgumentCaptorValue =
                deleteObjectRequestArgumentCaptor.getValue();

        assertThat(deleteObjectRequestArgumentCaptorValue.bucket())
                .isEqualTo(bucketName);
        assertThat(deleteObjectRequestArgumentCaptorValue.key())
                .isEqualTo(key);
    }
}