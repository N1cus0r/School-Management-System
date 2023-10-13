package com.example.schoolmanagementsystem.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3.bucket")
@Getter
@Setter
public class S3Bucket {
    private String name;
    public final String PROFILE_IMAGE_PATH = "profile-images/%s/%s";
}
