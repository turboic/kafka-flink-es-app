package com.example.demo.clickhouse;

import com.alibaba.fastjson.JSON;
import com.example.demo.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ivi.opensource.flinkclickhousesink.ClickHouseSink;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseClusterSettings;
import ru.ivi.opensource.flinkclickhousesink.model.ClickHouseSinkConst;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaFlinkClickHouse {
    private static final Logger logger = LoggerFactory.getLogger(KafkaFlinkClickHouse.class);

    public static void main(String[] args) {
        try {
            StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment().setParallelism(1);
            environment.enableCheckpointing(1000);
            environment.enableChangelogStateBackend(true);
            environment.getCheckpointConfig().setCheckpointingMode(CheckpointConfig.DEFAULT_MODE);
            Map<String, String> globalParameters = new HashMap<>();

            logger.info("------------------ClickHouse cluster properties------------------");
            globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_HOSTS, Constant.CLICKHOUSE_HOSTS);
            globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_USER, Constant.CLICKHOUSE_USER);
            globalParameters.put(ClickHouseClusterSettings.CLICKHOUSE_PASSWORD, Constant.CLICKHOUSE_PASSWORD);

            logger.info("------------------sink common------------------");
            globalParameters.put(ClickHouseSinkConst.TIMEOUT_SEC, Constant.TIMEOUT_SEC);
            globalParameters.put(ClickHouseSinkConst.FAILED_RECORDS_PATH, Constant.FAILED_RECORDS_PATH);
            globalParameters.put(ClickHouseSinkConst.NUM_WRITERS, Constant.NUM_WRITERS);
            globalParameters.put(ClickHouseSinkConst.NUM_RETRIES, Constant.NUM_RETRIES);
            globalParameters.put(ClickHouseSinkConst.QUEUE_MAX_CAPACITY, Constant.QUEUE_MAX_CAPACITY);
            globalParameters.put(ClickHouseSinkConst.IGNORING_CLICKHOUSE_SENDING_EXCEPTION_ENABLED, Constant.IGNORING_CLICKHOUSE_SENDING_EXCEPTION_ENABLED);

            logger.info("------------------set global ParameterTool------------------");
            ParameterTool parameters = ParameterTool.fromMap(globalParameters);
            environment.getConfig().setGlobalJobParameters(parameters);


            logger.info("------------------create props for sink------------------");
            Properties props = new Properties();
            props.put(ClickHouseSinkConst.TARGET_TABLE_NAME, Constant.TARGET_TABLE_NAME);
            props.put(ClickHouseSinkConst.MAX_BUFFER_SIZE, Constant.MAX_BUFFER_SIZE);

            logger.info("------------------build chain------------------");

            KafkaSource<String> source = KafkaSource.<String>builder()
                    .setBootstrapServers(Constant.BOOTSTRAP_SERVERS)
                    .setTopics(Collections.singletonList(Constant.KAFKA_FLINK_CLICKHOUSE_TOPIC))
                    .setGroupId(Constant.KAFKA_FLINK_CLICKHOUSE_GROUP_ID)
                    .setStartingOffsets(OffsetsInitializer.earliest())
                    .setValueOnlyDeserializer(new SimpleStringSchema())
                    .setProperty(Constant.KEY_PARTITION_DISCOVERY_INTERVAL_MS, Constant.VALUE_PARTITION_DISCOVERY_INTERVAL_MS)
                    .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(StringDeserializer.class))
                    .build();

            DataStreamSource<String> dataStreamSource = environment.fromSource(source, WatermarkStrategy.noWatermarks(),
                    "Kafka Source");

            SingleOutputStreamOperator<FlinkClickHouse> dataStream = dataStreamSource.map((MapFunction<String, FlinkClickHouse>) value -> {
                FlinkClickHouse flinkClickHouse;
                if (StringUtils.isBlank(value)) {
                    logger.warn("消费kafka数据为空，使用默认值替代！");
                    flinkClickHouse = FlinkClickHouse.getDefault();
                }
                else {
                    try {
                        flinkClickHouse = JSON.parseObject(value, FlinkClickHouse.class);
                    }catch (Exception e) {
                        logger.error("json解析成对象FlinkClickHouse异常",e);
                        flinkClickHouse = FlinkClickHouse.getDefault();
                    }
                }
                return flinkClickHouse;
            });

            logger.info("convert FlinkClickHouse to ClickHouse table format");


            dataStream.map(YourEventConverter::toClickHouseInsertFormat)
                    .name("convert FlinkClickHouse to ClickHouse table format")
                    .addSink(new ClickHouseSink(props))
                    .name(Constant.TARGET_TABLE_NAME + " Flink ClickHouse sink");


            logger.info(" dataStream.print()");
            dataStream.print();
            environment.execute("Kafka Flink ClickHouse");

        } catch (Exception e) {
            logger.info("flink execute failure", e);
            throw new RuntimeException(e);
        }
    }
}
