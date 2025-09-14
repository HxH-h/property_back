package com.propertysystem.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    @Value("${amazons.accessKeyId}")
    public String accessKeyId;

    @Value("${amazons.accessKeySecret}")
    public String accessKeySecret;

    @Value("${amazons.endpoint}")
    public String endpoint;

    @Bean
    public AmazonS3 amazonS3(){
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, accessKeySecret);
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCredentials));
        builder.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,"auto"));
        return builder.build();
    }
}
