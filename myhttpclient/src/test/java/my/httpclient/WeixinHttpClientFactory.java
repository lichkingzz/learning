package my.httpclient;

import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
public class WeixinHttpClientFactory implements FactoryBean<HttpAsyncClient> {

	@Override
	public HttpAsyncClient getObject() throws Exception {
		// TODO Auto-generated method stub
		return HttpAsyncClients.createDefault();
	}

	@Override
	public Class<HttpAsyncClient> getObjectType() {
		// TODO Auto-generated method stub
		return HttpAsyncClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
