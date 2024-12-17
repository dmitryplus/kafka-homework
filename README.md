# kafka-homework

Запускаем zookeper, kafka, kafka-connect и postgres 

проверяем состояние

![2024-12-17_11-01.png](2024-12-17_11-01.png)


проверяем плагины коннекторов запросом на `http://localhost:8083/connector-plugins`

![2024-12-17_11-03.png](2024-12-17_11-03.png)

проверяем топики connect

![2024-12-17_11-05.png](2024-12-17_11-05.png)

создаем таблицу и заполняем данными, проверяем

![2024-12-17_11-14.png](2024-12-17_11-14.png)


создаем коннектор

`curl -X POST --data-binary "@clients.json" -H "Content-Type: application/json" http://localhost:8083/connectors | jq`

и проверяем его статус

`curl http://localhost:8083/connectors/clients-connector/status | jq`

![2024-12-17_11-15.png](2024-12-17_11-15.png)

читаем топик - все записи из бд попали в kafka

`docker exec kafka1 kafka-console-consumer --topic postgres.clients --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094 --from-beginning --property print.offset=true`

![2024-12-17_11-16.png](2024-12-17_11-16.png)