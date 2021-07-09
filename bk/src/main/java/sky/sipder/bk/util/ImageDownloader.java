package sky.sipder.bk.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

public class ImageDownloader
{
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31";

	private static Logger logger = Logger.getLogger(ImageDownloader.class);

	private static final int TIMEOUT_SECONDS = 30;

	private static final int POOL_SIZE = 3;

	private static CloseableHttpClient httpclient;

	public static void mainnnnn(String[] args) throws ClientProtocolException, IOException
	{

		ImageDownloader imageDownloader = new ImageDownloader();
		imageDownloader.initApacheHttpClient();

		String imageUrl = "https://mmtp1.com/fulitu/zipai/630/02.jpg";
		imageDownloader.fetchContent(imageUrl , new File("I:\\temp\\ERo\\kk.jpg"));

		
		imageDownloader.destroyApacheHttpClient();
	}

	public void initApacheHttpClient()
	{
		// Create global request configuration
		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(TIMEOUT_SECONDS * 1000)
				.setConnectTimeout(TIMEOUT_SECONDS * 1000).build();

		// Create an HttpClient with the given custom dependencies and
		// configuration.
		httpclient = HttpClients.custom().setUserAgent(USER_AGENT).setMaxConnTotal(POOL_SIZE)
				.setMaxConnPerRoute(POOL_SIZE).setDefaultRequestConfig(defaultRequestConfig).build();
	}

	private void destroyApacheHttpClient()
	{
		try
		{
			httpclient.close();
		} 
		catch (IOException e)
		{
			logger.error("httpclient close fail", e);
		}
	}

	public void fetchContent(String imageUrl , File outputFile) throws ClientProtocolException, IOException
	{

		HttpGet httpget = new HttpGet(imageUrl);
		//httpget.setHeader("Referer", "http://www.google.com");

		CloseableHttpResponse response = httpclient.execute(httpget);

		try
		{
			HttpEntity entity = response.getEntity();

			if (response.getStatusLine().getStatusCode() >= 400)
			{
				throw new IOException("Got bad response, error code = " + response.getStatusLine().getStatusCode()
						+ " imageUrl: " + imageUrl);
			}
			if (entity != null)
			{
				InputStream input = entity.getContent();
				OutputStream output = new FileOutputStream(outputFile);
				IOUtils.copy(input, output);
				output.flush();
			}
		} finally
		{
			response.close();

		}

	}
}
