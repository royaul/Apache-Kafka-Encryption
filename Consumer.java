package com.Project;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Consumer {

    private Properties props; 


    public Consumer() throws Exception{

         //Set configuration properties for consumer 
         props = new Properties();

         props.put("bootstrap.servers", "localhost:19093");
         props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
         props.put("security.protocol", "SSL");
         props.put("ssl.truststore.location", "/home/mommo/Kafka_Project/vault_configs/node-1-keystore.jks");
         props.put("ssl.truststore.password", "password");
         props.put("ssl.truststore.type", "JKS");
         props.put("ssl.key.password", "password");
         props.put("ssl.keystore.location", "/home/mommo/Kafka_Project/Clients/client-keystore.jks");
         props.put("ssl.keystore.password", "password");
         props.put("ssl.keystore.type", "JKS");
         props.put("group.id", "kafka-group");
        
         RecieveMessages();
        
    }

    public void RecieveMessages() throws Exception{
        //Create new Kafka consumer 
        KafkaConsumer <String,String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("kafka-topic"));
        KeyProvider provider = new KeyProvider();
        

        try{ 

            while (true) {
        
                ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String,String> record : records) {
                
                    String jsonString = record.value();

                    // Deserialize JSON string to JSON object
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(jsonString);

                    System.out.println(provider.RSA_decrypt(json, "producer"));
        


                }
            }
        }finally{
                consumer.close();
            }
    }

    public static void main(String[] args) throws Exception{
        
        Consumer consumer = new Consumer();
        
        consumer.RecieveMessages();
        
        

    }


    
}
