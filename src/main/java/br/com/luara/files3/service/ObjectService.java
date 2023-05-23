package br.com.luara.files3.service;

import br.com.luara.files3.config.AWSConfig;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Paths;

@Service
public class ObjectService {
    @Autowired
    private static AWSConfig awsConfig;

    static final AmazonS3 s3 = awsConfig.crendenciaisS3();

    public static void listaDeObjetos(String nomeDoBucket) {
        var resultado = s3.listObjectsV2(nomeDoBucket);
        var sumObjeto = resultado.getObjectSummaries();

        for (S3ObjectSummary elementos : sumObjeto) {
            System.out.println(elementos.getKey());
        }
    }

    public static void uploadObjetoComMetadado(String nomeDoBucket, String caminhoDoArquivo, String titulo,
                                               String descricao, String usuario, String descricaoUsuario) {
        var nomeArquivo = Paths.get(caminhoDoArquivo).getFileName().toString();

        if (!verificacaoTipoArquivo(caminhoDoArquivo)) {
            try {
                PutObjectRequest request = new PutObjectRequest(nomeDoBucket, nomeArquivo, new File(caminhoDoArquivo));
                ObjectMetadata metadado = new ObjectMetadata();
                metadado.setContentType("Imagem: "+caminhoDoArquivo.lastIndexOf(".") + 1);
                metadado.addUserMetadata(usuario, descricaoUsuario);
                metadado.addUserMetadata(titulo, descricao);
                request.setMetadata(metadado);
                s3.putObject(request);
            } catch (AmazonServiceException amazonServiceException) {
                System.err.println(amazonServiceException.getErrorMessage() + "\n" + amazonServiceException.getErrorCode());
            }

            System.out.println("Sucesso ao fazer upload! Sua imagem com metadado foi salva no S3 storage!");
        } else System.out.println("Esse arquivo não é uma imagem!");
    }

    public static boolean uploadObjeto(String nomeDoBucket, String caminhoDoArquivo) {
        caminhoDoArquivo = "C:\\" + caminhoDoArquivo;
        String nomeArquivo = Paths.get(caminhoDoArquivo).toString();
        if (!verificacaoTipoArquivo(caminhoDoArquivo)) {
            System.out.println("Esse arquivo não é uma imagem!");
            return false;
        }
        try {
           s3.putObject(nomeDoBucket, nomeArquivo, new File(caminhoDoArquivo));
        } catch (AmazonS3Exception amazonS3Exception) {
            System.out.println(amazonS3Exception.getMessage());
        }
        return true;
    }

    private static boolean verificacaoTipoArquivo(String caminhoDoArquivo) {
        String nomeDoArquivo = new File(caminhoDoArquivo).getName();

        if (nomeDoArquivo.contains(".png") || nomeDoArquivo.contains(".jpeg") || nomeDoArquivo.contains(".jpg")) {
            System.out.println("Seu arquivo é uma imagem, seu upload irá continuar!");
            return true;
        }
        int posicaoDepoisDoPonto = nomeDoArquivo.lastIndexOf(".");
        System.out.println("Seu arquivo não é uma imagem! Selecione uma imagem para continuar! " + nomeDoArquivo.substring(posicaoDepoisDoPonto + 1));
        System.out.println("Você pode fazer upload desses tipos de arquivo: png, jpg e jpeg.");
        return false;
    }


}
