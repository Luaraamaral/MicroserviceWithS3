package br.com.luara.files3.controller;


import br.com.luara.files3.service.BucketService;
import br.com.luara.files3.service.ObjectService;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

import static br.com.luara.files3.config.AWSConfig.s3;

@RestController
@CrossOrigin("*")
public class Controller {
    @Autowired
    private BucketService bucketService;
    @Autowired
    private ObjectService objectService;

    @Operation(summary = "Listar todos os buckets")
    @GetMapping(value = "/buckets")
    public ResponseEntity<?> listarBuckets() {
        try {
            List<Bucket> listaDeBuckets = s3().listBuckets();
            return new ResponseEntity<>(listaDeBuckets, HttpStatus.OK);
        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @Operation(summary = "Buscar um bucket pelo nome")
    @GetMapping(value = "/bucket/{nomeDoBucket}")
    public ResponseEntity<?> buscarBucketPeloNome(@PathVariable String nomeDoBucket) {
        try {
            var bucket = bucketService.buscarBucket(nomeDoBucket);
            return new ResponseEntity<>(bucket, HttpStatus.OK);
        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Listar todos os objetos do buckets")
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

    @Operation(summary = "Criar um bucket")
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

    @Operation(summary = "Fazer upload de arquivo no bucket")
    @PutMapping(value = "/upload/{nomeDoBucket}/{caminhoDoArquivo}")
    public ResponseEntity<?> uploadArquivo(@PathVariable String nomeDoBucket, @PathVariable String caminhoDoArquivo) {
        try {
            objectService.uploadObjeto(nomeDoBucket, caminhoDoArquivo);
            var resposta = new File(caminhoDoArquivo).getName() + "\n" + nomeDoBucket;
            return new ResponseEntity<>(resposta, HttpStatus.OK);

        } catch (AmazonServiceException amazonServiceException) {
            return new ResponseEntity<>(amazonServiceException.getErrorCode(), HttpStatus.BAD_REQUEST);
        }
    }

}