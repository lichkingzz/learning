package my.weixin.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import my.httpclient.utils.ConvertUtils;
import my.weixin.api.exception.WeixinException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ΢�Ż����������
 * 
 * @author xzz
 */
public class WeixinBaseService {

	private final static Logger logger = LogManager
			.getLogger(WeixinBaseService.class);

	@Autowired
	private HttpAsyncClient httpClient;

	@Autowired
	private Environment env;

	// token��������ʱ��
	private long lastTokenUpdateTime = 0;

	private String accesstoken = "";

	private int expireAt = 0;

	// ���ڼ���5������Ϊ����
	private static int interval = 300 * 1000;

	@Value("${weixin.appID}")
	private String appID = null;

	@Value("${weixin.appSecret}")
	private String appSecret = null;

	@Value("${weixin.url.get_access_token}")
	private String getAccessTokenURL = null;

	protected ObjectMapper mapper = new ObjectMapper();

	/**
	 * ��ȡ�ӿڷ���Token��ÿ�η��ʵ�ʱ����Ҫ�������token
	 * 
	 * @return token�ֶ�
	 */
	protected String getAccessToken() throws WeixinException {
		// ����Ѿ����ˣ��͸��°�
		if (System.currentTimeMillis() - expireAt - interval > lastTokenUpdateTime) {
			// ��ֹ���߳�ͬʱ����
			synchronized (accesstoken) {
				if (System.currentTimeMillis() - expireAt - interval > lastTokenUpdateTime) {
					Map<String, Object> result = readResultToMap(new HttpGet(
							getAccessTokenURL
									+ "?grant_type=client_credential&appid="
									+ appID + "&secret=" + appSecret), true);
					checkHasError(result);
					accesstoken = result.get("access_token").toString();
					expireAt = (Integer) result.get("expires_in") * 1000;
					if (logger.isDebugEnabled()) {
						logger.debug("get accessToken:" + accesstoken
								+ ",expireAt:" + expireAt);
					}
				}
			}
		}
		return accesstoken;
	}

	/**
	 * ���ʲ��ҽ����ת����Map
	 * 
	 * @param req
	 *            http����
	 * @param ignoreToken
	 *            �Ƿ����token
	 * @return ���
	 * @throws WeixinException
	 */
	protected Map<String, Object> readResultToMap(HttpUriRequest req,
			boolean ignoreToken) throws WeixinException {
		InputStream in = null;
		try {
			if (ignoreToken) {
				in = executeAsStream(req);
			} else {
				if (req instanceof HttpGet) {
					HttpGet get = ((HttpGet) req);
					String base = get.getURI().toString();
					if(base.indexOf("?") != -1){
						base += "&access_token=" + URLEncoder.encode(getAccessToken(),"utf-8");
					}else{
						base += "?access_token=" + URLEncoder.encode(getAccessToken(),"utf-8");
					}
					URI uri = new URI(base);
					if(logger.isDebugEnabled()){
						logger.debug("query uri:" + uri);
					}
					get.setURI(uri);
					in = executeAsStream(req);
				}
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> result = ConvertUtils.readJson(in, Map.class);
			checkHasError(result);
			return result;
		} catch (Exception e) {
			logger.error("read result to map fail:", e);
			throw new WeixinException("SYSTEM_ERROR", "SYSTEM_ERROR");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error("close stream fail:", e);
				}
			}
		}
	}

	/**
	 * ������Ƿ�����쳣�������׳��쳣
	 * 
	 * @param result
	 *            ���ؽ��
	 * @throws WeixinException
	 *             �׳�ҵ���쳣
	 */
	protected void checkHasError(Object result) throws WeixinException {
		if (result instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> temp = (Map<String, Object>) result;
			if (temp.get("errcode") != null) {
				throw new WeixinException(temp.get("errcode").toString(), temp
						.get("errmsg").toString());
			}
		}

	}

	protected InputStream executeAsStream(HttpUriRequest req)
			throws IllegalStateException, IOException, InterruptedException,
			ExecutionException {
		Future<HttpResponse> future = getHttpClient().execute(req, null);
		HttpResponse response = future.get();
		return response.getEntity().getContent();
	}

	protected HttpAsyncClient getHttpClient() {
		return httpClient;
	}

	protected String getAppID() {
		return appID;
	}

	protected String getAppSecret() {
		return appSecret;
	}

}
