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


создаем debezium коннектор

`curl -X POST --data-binary "@customers-cdc.json" -H "Content-Type: application/json" http://localhost:8083/connectors | jq`

и проверяем его статус

`curl http://localhost:8083/connectors/customers-cdc-connector/status | jq`

![2024-12-18_10-09.png](2024-12-18_10-09.png)

запускаем слушателя kafka и добавляем записи с таблицу

`docker exec kafka1 kafka-console-consumer --topic postgres.cdc.public.customers --bootstrap-server kafka1:19092,kafka2:19093,kafka3:19094 --from-beginning --property print.offset=true`

![2024-12-18_11-47.png](2024-12-18_11-47.png)