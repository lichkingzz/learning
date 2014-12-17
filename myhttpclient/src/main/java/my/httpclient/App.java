package my.httpclient;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import my.httpclient.utils.ConvertUtils;
import my.weixin.api.CommonService;
import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinGroup;
import my.weixin.api.result.WeixinGroupResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) throws InterruptedException,
			IOException, WeixinException {
//
//		System.out.println(System.getenv("WEIXIN_CONFIG"));
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"beans.xml");
		CommonService service = context.getBean(CommonService.class);
		String[] ips = service.getWeixinIpList();
		for (String string : ips) {
			System.out.println(string);
		}
		
		PropertyDescriptor[] des = BeanUtils.getPropertyDescriptors(WeixinGroupResult.class);
		for (int i = 0; i < des.length; i++) {
			System.out.println(des[i].getName());
		}
		
		
		
		Thread.sleep(5000);
		System.exit(0);
		
		// RequestConfig requestConfig = RequestConfig.custom()
		// .setSocketTimeout(3000).setConnectTimeout(3000).build();
		// CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
		// .setDefaultRequestConfig(requestConfig).setMaxConnTotal(5)
		// .build();
		// httpclient.start();
		// final CountDownLatch latch = new CountDownLatch(5);
		// for (int i = 0; i < 5; i++) {
		// httpclient.execute(new HttpGet("http://www.163.com"),
		// new FutureCallback<HttpResponse>() {
		//
		// public void cancelled() {
		// latch.countDown();
		// }
		//
		// public void completed(HttpResponse response) {
		// latch.countDown();
		// System.out.println(response.getStatusLine());
		// }
		//
		// public void failed(Exception ex) {
		// latch.countDown();
		// }
		//
		// });
		// }
		// latch.await();
		// httpclient.close();
	}
}
