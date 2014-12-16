package my.httpclient;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Hello world!
 *
 */
@Configuration
@ComponentScan
public class App {
	public static void main(String[] args) throws InterruptedException,
			IOException {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		HttpAsyncClient client = context.getBean(HttpAsyncClient.class);
		client.execute(new HttpGet("http://www.163.com"), new FutureCallback<HttpResponse>() {
			@Override
			public void cancelled() {
				
			}

			@Override
			public void completed(HttpResponse rsp) {
			}

			@Override
			public void failed(Exception ex) {
				
			}
		});
//		RequestConfig requestConfig = RequestConfig.custom()
//				.setSocketTimeout(3000).setConnectTimeout(3000).build();
//		CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
//				.setDefaultRequestConfig(requestConfig).setMaxConnTotal(5)
//				.build();
//		httpclient.start();
//		final CountDownLatch latch = new CountDownLatch(5);
//		for (int i = 0; i < 5; i++) {
//			httpclient.execute(new HttpGet("http://www.163.com"),
//					new FutureCallback<HttpResponse>() {
//
//						public void cancelled() {
//							latch.countDown();
//						}
//
//						public void completed(HttpResponse response) {
//							latch.countDown();
//							System.out.println(response.getStatusLine());
//						}
//
//						public void failed(Exception ex) {
//							latch.countDown();
//						}
//
//					});
//		}
//		latch.await();
//		httpclient.close();
	}
}
