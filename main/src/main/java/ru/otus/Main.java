package ru.otus;

public class Main {

    public static void main(String[] args) throws InterruptedException {


        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss.SSS");


        MyProducer.main(args);
        MyConsumer.consumerFromTopic();
    }
}