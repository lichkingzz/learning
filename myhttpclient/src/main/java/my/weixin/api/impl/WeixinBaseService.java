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
 * 微信基础服务基类
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

	// token的最后更新时间
	private long lastTokenUpdateTime = 0;

	private String accesstoken = "";

	private int expireAt = 0;

	// 过期减少5分钟作为缓冲
	private static int interval = 300 * 1000;

	@Value("${weixin.appID}")
	private String appID = null;

	@Value("${weixin.appSecret}")
	private String appSecret = null;

	@Value("${weixin.url.get_access_token}")
	private String getAccessTokenURL = null;

	protected ObjectMapper mapper = new ObjectMapper();

	/**
	 * 获取接口访问Token，每次访问的时候都需要输入添加token
	 * 
	 * @return token字段
	 */
	protected String getAccessToken() throws WeixinException {
		// 如果已经超了，就更新吧
		if (System.currentTimeMillis() - expireAt - interval > lastTokenUpdateTime) {
			// 防止多线程同时更新
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
	 * 访问并且将结果转化成Map
	 * 
	 * @param req
	 *            http请求
	 * @param ignoreToken
	 *            是否忽略token
	 * @return 结果
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
	 * 检查结果是否包含异常，是则抛出异常
	 * 
	 * @param result
	 *            返回结果
	 * @throws WeixinException
	 *             抛出业务异常
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
