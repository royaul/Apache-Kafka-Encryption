package com.Project;

import java.io.BufferedReader;
import java.io.FileReader;




public class Readfile{

    protected String line;
    protected BufferedReader reader;
    
    
    
    public void KafkaParagraph(){

       
        try {
            reader = new BufferedReader(new FileReader("/home/mommo/VScode/kafka-examples/kafka-example/Kafka.txt"));
    
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public static void main(String[] args) {

        

    }
      
    
}