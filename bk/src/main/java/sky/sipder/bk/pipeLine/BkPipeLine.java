package sky.sipder.bk.pipeLine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import sky.sipder.bk.service.PoiServiceImpl;
import sky.sipder.bk.util.ImageDownloader;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class BkPipeLine implements Pipeline
{
	private Logger logger = LoggerFactory.getLogger(BkPipeLine.class);
	
	@Value("${path.source-txt}")
	private String sourceTxtPath;
	
	@Value("${path.target-dir}")
	private String targetDirPath;
	
	@Autowired
	private PoiServiceImpl poiService;
	
	@Override
	public void process(ResultItems resultItems, Task task)
	{
		ImageDownloader imageDownloader = new ImageDownloader();
		imageDownloader.initApacheHttpClient();
		File tarDir = new File(targetDirPath);
		
		logger.info("piplLine开始处理{}" , resultItems.get("url").toString());
		logger.info("页面标题是{}" , (resultItems.get("title") == null ? "空" : resultItems.get("title")));
		List<JSONObject> joList = resultItems.get("jsonList");
		List<String> imgList = resultItems.get("imgList");
		for(int i = 0 ; i < Integer.valueOf(resultItems.get("listSize").toString()) ; i++)
		{
			logger.info("-----------------------------------------------");
			logger.info("楼盘名:{}" , joList.get(i).getString("resblock_name"));
			logger.info("户型图片是:{}" , imgList.get(i).toString());
			logger.info("建筑面积:{}",joList.get(i).getString("build_area"));
			
			JSONArray ja = joList.get(i).getJSONArray("bind_building");
			
			StringBuilder sb; 
			String locat;
			if (ja.size() > 0)
			{
				sb = new StringBuilder();
				for (int j = 0; j < ja.size(); j++)
				{
					sb.append(ja.getJSONObject(j).getString("building_name"));
					sb.append("、");
				}
				locat = sb.substring(0, sb.length() - 1);
				logger.info("户型位于:{}", locat);
			}
			else
			{
				locat = "暂无在售楼栋";
				logger.info("户型暂无在售楼栋");
			}
			sb = new StringBuilder();
			sb.append(joList.get(i).getString("bedroom_count"));
			sb.append("室");
			sb.append(joList.get(i).getString("parlor_count"));
			sb.append("厅");
			sb.append(joList.get(i).getString("toilet_count"));
			sb.append("卫");
			
			String part = sb.toString();
			logger.info("户型构成:{}" , part);
			
			sb = new StringBuilder();
			sb.append(UUID.randomUUID().toString().replace("-", ""));
			sb.append("_");
			sb.append(joList.get(i).getString("resblock_name"));
			sb.append("_");
			sb.append(ja.size() > 0 ? ja.getJSONObject(0).getString("building_name") : "未知楼栋");
			double d = Double.valueOf(joList.get(i).getString("build_area"));
			sb.append((int) d);
			sb.append(".jpg");
			
			String fileName = sb.toString();
			File downloadFile = new File(tarDir, fileName);
			try
			{
				downloadFile.createNewFile();
				download(imgList.get(i).toString(), downloadFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}
			poiService.writeRow(joList.get(i).getString("resblock_name"), locat, joList.get(i).getString("build_area"), part, String.valueOf(resultItems.get("url").toString()), fileName, imgList.get(i).toString());
		}
		logger.info("piplLine处理{}完成" , resultItems.get("url").toString());
	}
	
	public void download(String targetUrl, File inFile)
	{
		logger.info("开始下载url:'{}'" , targetUrl);
		try
		{
			ImageDownloader imageDownloader = new ImageDownloader();
			imageDownloader.initApacheHttpClient();
			imageDownloader.fetchContent(targetUrl, inFile);
		} 
		catch (java.lang.IllegalArgumentException e) 
		{
			logger.error("参数错误，下载{}失败" , targetUrl);
		}
		catch (java.net.SocketTimeoutException e) 
		{
			logger.error("超时，下载{}失败" , targetUrl);
			inFile.delete();
		}
		catch (javax.net.ssl.SSLHandshakeException e) 
		{
			logger.error("远端服务器关闭连接， 下载{}失败" , targetUrl);
			inFile.delete();
		}
		catch (javax.net.ssl.SSLProtocolException e) 
		{
			logger.error("ssl原因，下载{}失败" , targetUrl);
			inFile.delete();
		}
		catch (java.net.SocketException e) 
		{
			logger.error("socket问题，下载{}失败" , targetUrl);
			inFile.delete();
		} 
		catch (ClientProtocolException e)
		{
			logger.error("Client问题，下载{}失败" , targetUrl);
			inFile.delete();
		} 
		catch (IOException e)
		{
			inFile.delete();
			if(e.getLocalizedMessage().contains("error code = 404"))
			{
				logger.error("404问题，下载{}失败" , targetUrl);
			}
			else
			{
				e.printStackTrace();
			}
		}
		catch (java.lang.IllegalStateException e) 
		{
			logger.error("ConnetionPool原因，下载{}失败" , targetUrl);
			inFile.delete();
		}
		catch (Exception e) 
		{
			logger.error("未知原因，下载" + targetUrl + "失败" , e);
			inFile.delete();
		}
		finally 
		{
			if(inFile.exists() && inFile.length() > 1024)
			{
				logger.info("下载{}成功" , targetUrl);
			}
			else
			{
				inFile.delete();
			}
		}
	}
}
