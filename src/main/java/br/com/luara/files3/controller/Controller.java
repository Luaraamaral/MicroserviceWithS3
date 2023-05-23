package br.com.luara.files3.controller;


import br.com.luara.files3.service.BucketService;
import br.com.luara.files3.service.ObjectService;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static br.com.luara.files3.config.AWSConfig.crendenciaisS3;

@RestController
public class Controller {

    @Autowired
    private BucketService bucketService;

    @GetMapping(value = "/buckets")
    public ResponseEntity<?> listarBuckets() {
        try {
            List<Bucket> listaDeBuckets = crendenciaisS3().listBuckets();
            return new ResponseEntity<>(listaDeBuckets, HttpStatus.OK);
        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping(value = "/bucket/{nomeDoBucket}")
    public ResponseEntity<?> buscarBucketPeloNome(@PathVariable String nomeDoBucket) {
        try {
            var bucket = bucketService.buscarBucket(nomeDoBucket);
            return new ResponseEntity<>(bucket, HttpStatus.OK);
        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/objetos/{nomeDoBucket}")
    public ResponseEntity<?> listarObjetosDoBucket(@PathVariable String nomeDoBucket) {
        try {
            ListObjectsV2Result resultado = crendenciaisS3().listObjectsV2(nomeDoBucket);
            List<S3ObjectSummary> sumObjeto = resultado.getObjectSummaries();
            for (S3ObjectSummary objeto : sumObjeto) {
                System.out.println(objeto.getKey());
            }
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

}