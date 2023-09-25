package com.Project;


import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.simple.JSONObject;

public class Producer {

    private Properties props;

    public Producer() throws InterruptedException, ExecutionException{

        props = new Properties();
        props.put("bootstrap.servers", "localhost:19093");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("security.protocol", "SSL");
        props.put("ssl.truststore.location", "/home/mommo/Kafka_Project/vault_configs/node-1-keystore.jks");
        props.put("ssl.truststore.password", "password");
        props.put("ssl.truststore.type", "JKS");
        props.put("ssl.key.password", "password");
        props.put("ssl.keystore.location", "/home/mommo/Kafka_Project/Clients/client-keystore.jks");
        props.put("ssl.keystore.password", "password");
        props.put("ssl.keystore.type", "JKS");
        //Verify if prodcucer is connected to broker
        AdminClient client = AdminClient.create(props);
        Collection<org.apache.kafka.common.Node> nodes = client.describeCluster().nodes().get();
        System.out.println(nodes != null && nodes.size() > 0);
    }

    public void SendMessage() throws Exception{

        final KafkaProducer<String,String> producer= new KafkaProducer<String,String>(props);
        JSONObject envelope = new JSONObject();
        EncryptionHandler handler = new EncryptionHandler();

        Readfile file = new Readfile();
        file.KafkaParagraph();
      
        while((file.line = file.reader.readLine()) != null ){
            
            envelope = handler.RSA_encrypt(handler.AES_Gen(), handler.IV_Gen(), file.line, "consumer");

           //Send Data - Asynchronous
            producer.send(new ProducerRecord<String,String>("kafka-topic",  "key1", envelope.toJSONString()), new org.apache.kafka.clients.producer.Callback() {

                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {

                    if(exception != null){

                        throw new UnsupportedOperationException("Unimplemented method 'onCompletion'");
                    }
                }
            });
        }
        //Flush and close producer
        producer.flush();
        producer.close();
    }   
}
