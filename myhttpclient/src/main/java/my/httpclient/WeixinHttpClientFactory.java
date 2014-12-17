package my.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class WeixinHttpClientFactory implements FactoryBean<HttpAsyncClient> {

	private int socketTimeout = 10000;

	private int connectTimeout = 5000;

	@Override
	public HttpAsyncClient getObject() throws Exception {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout).build();
		CloseableHttpAsyncClient client = HttpAsyncClients.custom().setDefaultRequestConfig(requestConfig)
				.build();
		client.start();
		return client;
	}

	@Override
	public Class<HttpAsyncClient> getObjectType() {
		return HttpAsyncClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

}
