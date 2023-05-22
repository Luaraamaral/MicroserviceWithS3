package br.com.luara.files3.service;

import br.com.luara.files3.config.AWSConfig;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObjectService {
    @Autowired
    private static AWSConfig awsConfig;

    public static void listaDeObjetos(String nomeDoBucket){
        AmazonS3 s3 = awsConfig.crendenciaisS3();
        var resultado = s3.listObjectsV2(nomeDoBucket);
        var sumObjeto = resultado.getObjectSummaries();

        for (S3ObjectSummary elementos: sumObjeto) {
            System.out.println(elementos.getKey());
        }
    }
}
