package com.Project;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.simple.JSONObject;






//Purpose of class is utilizing a combination of two cryptographic algorithms(RSA and AES)

public class KeyProvider {

    private Keys keys = new Keys();
    private IvParameterSpec ivSpec;
    private KeyGenerator keyGenerator;
    private SecretKey AES_key;
    private HashMap<String,Object> containerMap;
    private JSONObject container;

    // Encrypt messages (RSA )
    public JSONObject RSA_encrypt(SecretKey Symkey, IvParameterSpec iv, String message,String alias) throws Exception{

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keys.retrievePublicKey(alias));

        //Retrieve message and IV and encrypt

        byte[] AES_encrypt = cipher.doFinal(Symkey.getEncoded());
        byte[] IV_encrypt = cipher.doFinal(ivSpec.getIV());

        containerMap = new HashMap<String,Object>();

        String ciphertext = AES_encrypt(message,Symkey,iv);
        String keyStr = encode(AES_encrypt);
        String keyIV = encode(IV_encrypt);
        
        containerMap.put("key", keyStr);
        containerMap.put("IV", keyIV);
        containerMap.put("ciphertext", ciphertext);
        containerMap.put("signature", messageSign(message, alias));
        
        container = new JSONObject(containerMap);
        
        return container;
    }

    //Decrypt message (RSA)
    public String RSA_decrypt(JSONObject json,String alias) throws Exception{

        byte[] encryptedKey = decode((String)json.get("key"));
        byte[] encryptedIV = decode((String)json.get("IV"));
        byte[] encryptedMessage = decode((String)json.get("ciphertext"));
        //byte[] encryptedSignature = decode((String)json.get("signature"));

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, keys.retrievePrivateKey(alias));

        byte[] decryptKey = cipher.doFinal(encryptedKey);
        byte[] decryptIV = cipher.doFinal(encryptedIV);

        SecretKey newkey = new SecretKeySpec(decryptKey,0,decryptKey.length,"AES");
        String result = AES_decrypt( encode(encryptedMessage),newkey, new IvParameterSpec(decryptIV) );

        if (messageVerify(result, (String)json.get("signature"), alias)) {
            System.out.println("The signer is verified");
        }
        else{
            System.out.println("Access denied");
        }

        return new String(AES_decrypt( encode(encryptedMessage),newkey, new IvParameterSpec(decryptIV) ));
    }

    //Generate AES key
    public SecretKey AES_Gen() throws Exception{
        //Create a secret key
        keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        AES_key = keyGenerator.generateKey();
        return AES_key;
    }


    //Encrypt utilizing AES
    public String AES_encrypt(String message, SecretKey key, IvParameterSpec iv) throws Exception{

        //Create a cipher object and initialize it with key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        // Encrypt the message
        byte[] encrypted = cipher.doFinal(message.getBytes());

        //Convert to Base64
        return encode(encrypted);
    }

     //Decrypt utilizing AES
    public String AES_decrypt(String message, SecretKey key ,IvParameterSpec iv)throws Exception{


        //Create a cipher object and initialize it with key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key,iv);
        
        // Decrypy the message
        byte[] decrypted = cipher.doFinal(decode(message));
        
        String finish = new String(decrypted);
        //System.out.println(finish);

        return finish;
    }

    //Generate random initialization vector 
    public IvParameterSpec IV_Gen(){

        // Create an initialization vector
        byte[] iv = new byte[16]; // 128-bit IV
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        ivSpec = new IvParameterSpec(iv);
        
        return ivSpec;
    }


    // Sign messages
    public String messageSign(String message, String alias) throws Exception{

        
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keys.retrievePrivateKey(alias));
        signature.update(message.getBytes(StandardCharsets.UTF_8));

        byte[] sign = signature.sign();

        return encode(sign);
    }

    //Verify messages
    public boolean messageVerify(String message, String sign ,String alias)throws Exception{

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(keys.retrievePublicKey(alias));
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        byte[] signBytes = decode(sign);

        return signature.verify(signBytes);
    }




    private String encode(byte[] data) { 
        return Base64.getEncoder().encodeToString(data);
    }





    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

    
    public static void main(String[] args)throws Exception{

        //Create KeyProvider
        KeyProvider geneKey = new KeyProvider();

        String message = "Hello World!";

        //Encrypt message 

        JSONObject test = new JSONObject();

        test = geneKey.RSA_encrypt(geneKey.AES_Gen(), geneKey.IV_Gen(), message,"producer");
        
        //Make sure the code is beautifully written

        /*System.out.println("Key: " + test.get("key"));   
        System.out.println("IV: " + test.get("IV"));  
        System.out.println("text: " + test.get("ciphertext"));
        System.out.println("signature: " + test.get("signature"));
        
        System.out.println("---------------------------------------------");

        System.out.println(geneKey.RSA_decrypt(test, "producer"));
*/

  }



    
}
