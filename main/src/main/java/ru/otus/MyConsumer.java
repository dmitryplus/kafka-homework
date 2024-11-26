package ru.otus;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class MyConsumer {


    public static void consumerFromTopic() {

        Logger log = LoggerFactory.getLogger(MyConsumer.class);


        Map<String, Object> consumerConfig = Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092",
                ConsumerConfig.GROUP_ID_CONFIG, "some-java-consumer",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 2000,
                ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed"
        );


        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerConfig);

        consumer.subscribe(Arrays.asList("topic1", "topic2"));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records)
                    log.info("consume message {} with key {} ", record.value(), record.key());
            }
        } finally {
            consumer.close();
        }

    }

}
