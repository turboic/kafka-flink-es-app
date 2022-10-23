1、启动kafka中的zookeeper

/home/demo/kafka_2.12-3.2.0/bin/zookeeper-server-start.sh /home/demo/kafka_2.12-3.2.0/config/zookeeper.properties

2、启动kafka
/home/demo/kafka_2.12-3.2.0/bin/kafka-server-start.sh /home/demo/kafka_2.12-3.2.0/config/server.properties


3、创建kafka的测试主题demo
/home/demo/kafka_2.12-3.2.0/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic demo


4、kafka的控制台生产者
/home/demo/kafka_2.12-3.2.0/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic demo




测试数据
{"name":"spark流任务学习，接收kafka数据","app":"latest","issue":"es最新的docker版本不支持认证","time":"2022-08-08 11:35:02","reason":"今日隔离"}

