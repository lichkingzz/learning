package my.httpclient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InterruptedException,
			IOException {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(3000).setConnectTimeout(3000).build();
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
				.setDefaultRequestConfig(requestConfig).setMaxConnTotal(5)
				.build();
		httpclient.start();
		final CountDownLatch latch = new CountDownLatch(5);
		for (int i = 0; i < 5; i++) {
			httpclient.execute(new HttpGet("http://www.163.com"),
					new FutureCallback<HttpResponse>() {

						public void cancelled() {
							latch.countDown();
						}

						public void completed(HttpResponse response) {
							latch.countDown();
							System.out.println(response.getStatusLine());
						}

						public void failed(Exception ex) {
							latch.countDown();
						}

					});
		}
		latch.await();
		httpclient.close();
	}
}
