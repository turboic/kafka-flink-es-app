package com.turboic.cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


/**
 * kafka-provider
 *
 * @author liebe
 */
@SpringBootApplication
public class FlinkKafkaDemoApplication
{
    private static final Logger log = LoggerFactory.getLogger(FlinkKafkaDemoApplication.class);
    public static void main( String[] args )
    {
        log.debug("spring-boot flink 数据写入kafka 的例子程序 程序启动");
        log.info("kafka 的服务提供者启动");
        new SpringApplicationBuilder(FlinkKafkaDemoApplication.class).run(args);
    }
}
