package com.Project;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import java.security.cert.CertificateException;

public class Keys extends Object{

    PublicKey publickey;
    PrivateKey privateKey;

    public PublicKey retrievePublicKey(String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException{

        String keystorePath = "/home/mommo/Kafka_Project/Clients/client-keystore.jks";
        String password = "password";

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(new FileInputStream(keystorePath), password.toCharArray());

        if (keystore.containsAlias(alias)) {

            publickey =  keystore.getCertificate(alias).getPublicKey();
            //System.out.println("Public key: " + publickey.toString());

        } else {
            System.out.println("Alias not found in keystore.");
        }

        return publickey;

    }

    public PrivateKey retrievePrivateKey(String alias) throws Exception{

        String keystorePath = "/home/mommo/Kafka_Project/Clients/client-keystore.jks";
        String password = "password";

        KeyStore keyStore = KeyStore.getInstance("JKS");
        FileInputStream is = new FileInputStream(keystorePath);
        keyStore.load(is, password.toCharArray());
        is.close();

        
        Key key = keyStore.getKey(alias, password.toCharArray());
        if (key instanceof PrivateKey) {
            // Use the private key
             privateKey = (PrivateKey) key;
            //System.out.println("Private key, This is from Keys class: " + privateKey.toString());
        } else {
            throw new Exception("Private key not found for alias: " + alias);
        }
        return privateKey;

    }


    public PublicKey getPublicKey(){

        return publickey;

    }
        




    public static void main(String[] args) throws Exception {

       // Keys key = new Keys();
    
  
    }

    
}
