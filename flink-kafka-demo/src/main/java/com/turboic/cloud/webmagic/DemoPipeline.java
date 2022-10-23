package com.turboic.cloud.webmagic;

import com.alibaba.fastjson.JSON;
import com.turboic.cloud.service.FlinkKafkaDemoService;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class DemoPipeline implements Pipeline {

    private static final Logger log = LoggerFactory.getLogger(DemoPipeline.class);

    @Autowired
    private FlinkKafkaDemoService flinkKafkaDemoService;


    @Override
    public void process(ResultItems resultItems, Task task) {
        log.info("接收处理结果");
        String name = resultItems.get("name");
        Map<String, Object> mapItems = resultItems.getAll();
        if (mapItems != null) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<String,Object> entry : mapItems.entrySet()) {
                if (!StringUtils.isEmpty(entry.getValue())) {
                    map.put("time", DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
                    map.put(entry.getKey(),entry.getValue());
                    map.put("version", "1.0");
                    map.put("tool", "spring-boot->kafka->flink->elasticsearch");
                }
            }
            if (map.size() > 0) {
                flinkKafkaDemoService.product(JSON.toJSONString(map));
            }
        }
    }
}