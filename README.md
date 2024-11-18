# kafka-homework

## Текст задания

Разработка приложения Kafka Streams:

    Запустить Kafka
    Создать топик events
    Разработать приложение, которое подсчитывает количество событий с одинаковыми key в рамках сессии 5 минут
    Для проверки отправлять сообщения, используя console producer.


## Домашнее задание к уроку 13


Запускаем кафку и создаем топик, проверяем черед kafdrop
![2024-11-17_17-17.png](2024-11-17_17-17.png)

![2024-11-17_17-18.png](2024-11-17_17-18.png)

![2024-11-17_17-18_1.png](2024-11-17_17-18_1.png)


Создаем основу приложения для постоянной работы stream (`java/ru/otus/Main.java`), используем следующую конструкцию

```java
        try {

            log.info("App Started");

            kafkaStreams.start();

            latch.await();

            log.info("Shutting down now");


        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
```

Создаем основной поток

```java

        var builder = new StreamsBuilder();

        KStream<String, String> originalStream = builder.stream("events", Consumed.with(stringSerde, stringSerde));

```


Выводим все входящие сообщения в консоль с меткой `From my apps`

```java

    originalStream.print(Printed.<String, String>toSysOut().withLabel("From my apps"));


```



Переводим поток в оконную таблицу и считаем количество событий с одинаковыми key в рамках сессии 1 минуты (для наглядности)

```java

var newTable = originalStream
        .groupByKey()
        .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))
        .count();


```

Переводим таблицу обратно в поток и выводим в консоль с новой меткой `Count for 1 minute:`

```java

newTable.toStream().print(Printed.<Windowed<String>, Long>toSysOut().withLabel("Count for 1 minute:"));


```

Запускаем приложение и начинаем отправлять сообщения в топик консольным продюсером 

`docker exec -it 9349fc28df88 /usr/bin/kafka-console-producer --bootstrap-server localhost:29092 --topic events --property "parse.key=true" --property "key.separator=:"`

Через минуту после начала отправки сообщений в консоли выводится результат

![2024-11-18_18-46.png](2024-11-18_18-46.png)