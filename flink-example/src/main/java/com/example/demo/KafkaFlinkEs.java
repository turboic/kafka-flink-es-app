package com.example.demo;

import com.example.demo.sinks.MyElasticsearchSink;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class KafkaFlinkEs {
    private static final Logger logger = LoggerFactory.getLogger(KafkaFlinkEs.class);

    public static void main(String[] args) {

        try {

            logger.info("1、create flink runtime environment");
            logger.info("1、创建flink的运行环境");
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
            env.enableCheckpointing(1000);
            env.enableChangelogStateBackend(true);
            env.getCheckpointConfig().setCheckpointingMode(CheckpointConfig.DEFAULT_MODE);

            logger.info("2、create kafkaSource for flink");
            logger.info("1、创建kafka的数据源到flink");

            KafkaSource<String> source = KafkaSource.<String>builder()
                    .setBootstrapServers(Constant.BOOTSTRAP_SERVERS)
                    .setTopics(Collections.singletonList(Constant.KAFKA_TOPIC))
                    .setGroupId(Constant.GROUP_ID)
                    .setStartingOffsets(OffsetsInitializer.earliest())
                    .setValueOnlyDeserializer(new SimpleStringSchema())
                    .setProperty(Constant.KEY_PARTITION_DISCOVERY_INTERVAL_MS, Constant.VALUE_PARTITION_DISCOVERY_INTERVAL_MS)
                    .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class))
                    .build();


            logger.info("3、consume kafka topic {}", Constant.KAFKA_TOPIC);
            logger.info("3、kafka的消费主题 {}", Constant.KAFKA_TOPIC);

            logger.info("4、addSource ，build data Collections");
            logger.info("4、构建数据源集合");
            DataStreamSource<String> dataStreamSource = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

            logger.info("5、dataStreamSource print");
            logger.info("5、数据流打印");
            dataStreamSource.print();

            logger.info("6、dataStreamSource printToErr");
            logger.info("6、数据异常流打印");
            dataStreamSource.printToErr();

            logger.info("7、data Flow to Elasticsearch");
            logger.info("7、数据写入到es中");

            dataStreamSource.addSink(new MyElasticsearchSink());

            logger.info("8、env execute");
            env.execute("kafkaToFlink");
            org.apache.lucene.index.CorruptIndexException d;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("flink execute failed "+e);
            logger.error("flink execute failed", e);
        }
    }
}
