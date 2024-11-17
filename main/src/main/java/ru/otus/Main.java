package ru.otus;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class Main {

    public static void main(String[] args) throws Exception {

        Logger log = LoggerFactory.getLogger("appl");

        Serde<String> stringSerde = Serdes.String();
        var builder = new StreamsBuilder();

        var upperCasedStream = builder
                .stream("events", Consumed.with(stringSerde, stringSerde))
                .mapValues(it -> it.toUpperCase());


        upperCasedStream.to("events_new", Produced.with(stringSerde, stringSerde));
        upperCasedStream.print(Printed.<String, String>toSysOut().withLabel("From my apps"));


        Map<String, Object> streamsConfig = Map.of(
                StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092",
                StreamsConfig.APPLICATION_ID_CONFIG, "appId1"
        );


        log.info("{}", builder.build().describe());

        var kafkaStreams = new KafkaStreams(builder.build(), new StreamsConfig(streamsConfig));

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                kafkaStreams.close();
                latch.countDown();
            }
        });


        try {

            log.info("App Started");

            kafkaStreams.start();

            latch.await();

            log.info("Shutting down now");


        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);

    }
}