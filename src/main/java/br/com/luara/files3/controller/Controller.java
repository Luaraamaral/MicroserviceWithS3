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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

import static br.com.luara.files3.config.AWSConfig.s3;

@RestController
public class Controller {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private ObjectService objectService;

    @GetMapping(value = "/buckets")
    public ResponseEntity<?> listarBuckets() {
        try {
            List<Bucket> listaDeBuckets = s3().listBuckets();
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
            ListObjectsV2Result resultado = s3().listObjectsV2(nomeDoBucket);
            List<S3ObjectSummary> sumObjeto = resultado.getObjectSummaries();
            for (S3ObjectSummary objeto : sumObjeto) {
                System.out.println(objeto.getKey());
            }
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/upload/{nomeDoBucket}/{caminhoDoArquivo}")
    public ResponseEntity<?> uploadImagem(@PathVariable String nomeDoBucket, @PathVariable String caminhoDoArquivo) {
        try {
            objectService.uploadObjeto(nomeDoBucket, caminhoDoArquivo);
            var resposta = new File(caminhoDoArquivo).getName() + "\n" + nomeDoBucket;
            return new ResponseEntity<>(resposta, HttpStatus.OK);

        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(amazonServiceException.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/criarBucket/{nomeDoBucket}")
    public ResponseEntity<?> criarUmBucket(@PathVariable String nomeDoBucket) {
        Bucket novoBucket = null;

        if (!s3().doesBucketExistV2(nomeDoBucket)) {
            try {
                novoBucket = s3().createBucket(nomeDoBucket);
            } catch (AmazonServiceException amazonServiceException) {
                return new ResponseEntity<>(amazonServiceException.getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(novoBucket, HttpStatus.OK);
        }

        try {
            novoBucket = s3().createBucket(nomeDoBucket);
            return new ResponseEntity<>(novoBucket, HttpStatus.OK);
        } catch (AmazonServiceException amazonServiceException) {
            var mensagemDeErro = "Esse bucket já existe. " + amazonServiceException.getErrorMessage();
            return new ResponseEntity<>(mensagemDeErro, HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

}