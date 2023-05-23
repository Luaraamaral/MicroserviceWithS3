package br.com.luara.files3.service;


import br.com.luara.files3.config.AWSConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BucketService {
    static final AmazonS3 amazonS3 = AWSConfig.crendenciaisS3();

    public Bucket criarBucket(String nomeDoBucket) {

        Bucket novoBucket = null;
        boolean verificaSeExisteBucket = amazonS3.doesBucketExistV2(nomeDoBucket);

        if (verificaSeExisteBucket) {
            System.out.println("Esse bucket já existe!");
        } else {
            try {
                novoBucket = amazonS3.createBucket(nomeDoBucket);
                if (verificarDono(novoBucket)) {
                    System.out.println("Esse bucket já possui dono(a)!");
                }
            } catch (AmazonS3Exception s3Exception) {
                System.out.println(s3Exception.getMessage());
            }
        }
        return novoBucket;

    }

    public String buscarBucket(String nomeDoBucket) {
        List<Bucket> listaDeBuckets = amazonS3.listBuckets();
        for (Bucket bucket : listaDeBuckets) {
            if (bucket.getName().equals(nomeDoBucket))
                System.out.println(nomeDoBucket);
            else {
               System.out.println("Não existe um bucket com este nome!");
            }
        }
        return nomeDoBucket;
    }

    public void listarOsBuckets() {
        List<Bucket> listaDeBuckets = amazonS3.listBuckets();
        for (Bucket buckets : listaDeBuckets)
            System.out.println(buckets.getName());
    }

    private boolean verificarDono(Bucket bucket) {
        try {
            var dono = bucket.getOwner().getDisplayName();
            return Boolean.parseBoolean(dono);
        } catch (AmazonS3Exception s3Exception) {
            return Boolean.parseBoolean((s3Exception.getMessage()));
        }
    }

}
