package sky.sipder.bk.config;

import org.springframework.context.annotation.Configuration;

import us.codecraft.webmagic.Site;

@Configuration
public class SkyConfig
{
	public static final Site SITE = Site.me()
			.setRetryTimes(3)
			.setRetrySleepTime(1500)
			.setCycleRetryTimes(10)
			.setSleepTime(1500)
			.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
}
