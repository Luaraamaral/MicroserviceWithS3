package br.com.luara.files3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class AWSConfig {

    private static String accessKey = "AKIA25AX5FMLR7XEHAEX";
    private static String secretKey = "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";

    @Bean
    public static AmazonS3 s3() {

        BasicAWSCredentials credenciais = new BasicAWSCredentials(accessKey, secretKey);

        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.SA_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credenciais))
                .build();

    }


}
