package sky.sipder.bk.processor;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import sky.sipder.bk.config.SkyConfig;
import sky.sipder.bk.util.Utf8ToCn;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

@Component

public class BkProcessor implements PageProcessor 
{
	private Logger logger = LoggerFactory.getLogger(BkProcessor.class);

	@Override
	public void process(Page page)
	{
		String url = page.getUrl().toString();
		logger.info("processor开始处理{}" , url);
		page.putField("url", url);
		List<JSONObject> jsonList = new ArrayList<>();
		List<String> imgList = new ArrayList<>();
		Document d = Jsoup.parse(page.getHtml().get());
		Elements lis = d.getElementsByClass("slides-list");
		for(Element li : lis)
		{
			JSONObject jo = JSONObject.parseObject(Utf8ToCn.unicodeToCn(li.attr("data-origin")));
			jsonList.add(jo);
			Element pic = li.getElementsByTag("img").first();
			imgList.add(pic.attr("src"));
		}
		page.putField("jsonList", jsonList);
		page.putField("imgList", imgList);
		page.putField("listSize", lis.size());
		String title = d.getElementsByTag("title").first().html();
		page.putField("title", title);
		logger.info("processor处理{}完成" , url);
	}

	@Override
	public Site getSite()
	{
		return SkyConfig.SITE;
	}
	
}
