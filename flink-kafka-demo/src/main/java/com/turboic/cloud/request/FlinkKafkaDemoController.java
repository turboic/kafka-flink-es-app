package com.turboic.cloud.request;

import com.turboic.cloud.config.Swagger2Controller;
import com.turboic.cloud.service.FlinkKafkaDemoService;
import com.turboic.cloud.webmagic.DemoPageProcessor;
import com.turboic.cloud.webmagic.DemoPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;

/**
 * @author liebe
 */

@RestController
@RequestMapping("/flink")
@Swagger2Controller(value = "kafka-flink的Controller")
public class FlinkKafkaDemoController {
    private static final Logger log = LoggerFactory.getLogger(FlinkKafkaDemoController.class);
    private final FlinkKafkaDemoService flinkKafkaDemoService;

    @Autowired
    public FlinkKafkaDemoController(FlinkKafkaDemoService flinkKafkaDemoService) {

        this.flinkKafkaDemoService = flinkKafkaDemoService;
    }


    /**
     * 异步发送
     *
     * @return
     */
    @PostMapping("send")
    public String send(String msg) {
        log.info("发送消息内容:{}",msg);
        flinkKafkaDemoService.product(msg);
        return "kafka数据发送完成";
    }
    @PostMapping("spider")
    public String spider(String url) {
        log.info("启动爬虫程序：{}", url);
        Spider.create(demoPageProcessor)
                .addUrl(url)
                .addPipeline(demoPipeline)
                .thread(5)
                .run();
        return "spider task running ...";
    }
    @Autowired
    private DemoPipeline demoPipeline;

    @Autowired
    private DemoPageProcessor demoPageProcessor;
}
