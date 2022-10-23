package com.turboic.cloud.webmagic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class DemoPageProcessor implements PageProcessor {
    private static final Logger log = LoggerFactory.getLogger(DemoPageProcessor.class);

    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 部分二：定义如何抽取页面信息，并保存下来
        page.putField("author", page.getUrl().regex("https://www\\.douyin\\.com/(\\w+)/.*").toString());
        page.putField("title", page.getHtml().xpath("//tbody[@id='normalthread_11472604']/tr/th/span/a/text()").toString());
        page.putField("web", page.getHtml().xpath("//tbody[@id='normalthread_11472604']/tr/th/span/a/@href").toString());
        page.putField("author", page.getHtml().xpath("//td[@class='author']/cite/a/text()").toString());
        page.putField("strong", page.getHtml().xpath("//td[@class='nums']/strong/text()").toString());
        page.putField("em", page.getHtml().xpath("//td[@class='nums']/em/text()").toString());
        page.putField("lastpost", page.getHtml().xpath("//td[@class='lastpost']/em/a/text()").toString());
        page.putField("author", page.getHtml().xpath("//td[@class='author']/cite/a/text()").toString());
        if (page.getResultItems().get("title") == null) {
            //skip this page
            page.setSkip(true);
        }
        page.putField("type", page.getHtml().xpath("//th[@class='new']/em/a/text()").toString());

        // 部分三：从页面发现后续的url地址来抓取
        page.addTargetRequests(page.getHtml().links().regex("").all());
    }

    @Override
    public Site getSite() {
        return site;
    }
}