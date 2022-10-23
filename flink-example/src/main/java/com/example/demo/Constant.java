package com.example.demo;

public class Constant {
    public static final String KAFKA_TOPIC = "demo";

    public static final String KAFKA_FLINK_CLICKHOUSE_TOPIC = "clickhouse";

    public static final String KAFKA_FLINK_CLICKHOUSE_GROUP_ID = "clickhouse";

    public static final String BOOTSTRAP_SERVERS = "10.10.10.99:9092";
    public static final String GROUP_ID = "logs";
    public static final String username = "";
    public static final String password = "";
    public static final String host = "10.10.10.99";
    public static final int port = 9200;


    public static final String KEY_PARTITION_DISCOVERY_INTERVAL_MS = "partition.discovery.interval.ms";


    public static final String VALUE_PARTITION_DISCOVERY_INTERVAL_MS = "10000";

    public static final String CLICKHOUSE_HOSTS = "http://10.10.10.99:8123";

    public static final String CLICKHOUSE_USER = "default";

    public static final String CLICKHOUSE_PASSWORD = "clickhouse";


    public static final String TIMEOUT_SEC = "2";


    public static final String FAILED_RECORDS_PATH = "/home";


    public static final String NUM_WRITERS = "2";

    public static final String NUM_RETRIES = "2";

    public static final String QUEUE_MAX_CAPACITY = "2";

    public static final String IGNORING_CLICKHOUSE_SENDING_EXCEPTION_ENABLED = "false";


    public static final String TARGET_TABLE_NAME = "default.test";

    public static final String MAX_BUFFER_SIZE = "10000";


}
