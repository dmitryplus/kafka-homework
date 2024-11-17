package ru.otus;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;

public class MyProducer {
    public static void main(String[] args) throws InterruptedException {

        Logger log = LoggerFactory.getLogger(MyProducer.class);


        Map<String, Object> producerConfig = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092",
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactionId"
        );


        try {


            try (var producer1 = new KafkaProducer<String, String>(producerConfig)) {

                producer1.initTransactions();
                producer1.beginTransaction();

                for (int i = 0; i < 5; i++) {

                    log.info("send message id {} in topic1 with good transaction", i);
                    producer1.send(new ProducerRecord<>("topic1", "topic1 good transaction message-" + i));
                    producer1.send(new ProducerRecord<>("topic2", "topic2 good transaction message-" + i));

                }

                producer1.commitTransaction();
                log.info("send commit with good transaction for all topics");

            }

            try (var producer2 = new KafkaProducer<String, String>(producerConfig)) {

                producer2.initTransactions();
                producer2.beginTransaction();

                for (int i = 0; i < 2; i++) {

                    log.info("send message id {} in topics with bad transaction", i);
                    producer2.send(new ProducerRecord<>("topic1", "topic1 bad transaction message-" + i));
                    producer2.send(new ProducerRecord<>("topic2", "topic2 bad transaction message-" + i));
                }


                producer2.abortTransaction();
                log.info("send commit with abort transaction for all topics");

            }

        } catch (ProducerFencedException e) {
            throw new RuntimeException(e);
        }
    }

}
