package com.example.demo.clickhouse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YourEventConverter {
    private static final Logger logger = LoggerFactory.getLogger(YourEventConverter.class);

    public static String toClickHouseInsertFormat(FlinkClickHouse flinkClickHouse) {
        logger.error("处理前:{}", flinkClickHouse.toString());
        String format = FlinkClickHouse.convertToCsv(flinkClickHouse);
        logger.error("处理后:{}", format);
        return format;
    }
}
