package sky.sipder.bk.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import sky.sipder.bk.pipeLine.BkPipeLine;
import sky.sipder.bk.processor.BkProcessor;
import sky.sipder.bk.service.PoiServiceImpl;
import us.codecraft.webmagic.Spider;

@Component
public class SpiderTask implements ApplicationRunner
{
	private Logger logger = LoggerFactory.getLogger(SpiderTask.class);
	
	@Value("${path.source-txt}")
	private String sourceTxtPath;
	
	@Value("${path.target-dir}")
	private String targetDirPath;
	
	@Autowired
	private BkProcessor bkProcessor;
	
	@Autowired
	private BkPipeLine bkPipeLine;
	
	@Autowired
	private PoiServiceImpl poiService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception
	{
		logger.info("start");
		if(!checkPath())
		{
			logger.info("error");
			return ;
		}
		String[] targetUrl = genTarUrls();
		poiService.initWorkBook();
		Spider.create(bkProcessor).addUrl(targetUrl).addPipeline(bkPipeLine).thread(1).run();
		poiService.output();
		logger.info("end");
	}

	private boolean checkPath()
	{
		File srcTxt = new File(sourceTxtPath);
		if((!srcTxt.exists()) || (!srcTxt.isFile()))
		{
			logger.info("参数path.source-txt不合法,值为{}" , sourceTxtPath);
			return false;
		}
		File tarDir = new File(targetDirPath);
		if((!tarDir.exists()) || (!tarDir.isDirectory()))
		{
			logger.info("参数path.target-dir不合法,值为{}" , targetDirPath);
			return false;
		}
		return true;
	}
	
	private String[] genTarUrls() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(new File(sourceTxtPath)));
		List<String> urlList = new ArrayList<String>();
		String line = null;
		logger.info("开始载入url");
		while((line = br.readLine()) != null)
		{
			urlList.add(line);
			logger.info(line);
		}
		br.close();
		logger.info("总计{}个URL被载入" , urlList.size());
		String[] urls = new String[urlList.size()];
		urlList.toArray(urls);
		return urls;
	}

}
