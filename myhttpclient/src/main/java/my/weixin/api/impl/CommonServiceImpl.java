package my.weixin.api.impl;

import java.io.File;
import java.util.List;
import java.util.Map;

import my.weixin.api.CommonService;
import my.weixin.api.enums.WeixinMediaType;
import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinArticle;
import my.weixin.api.result.WeixinUploadResult;

import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl extends WeixinBaseService implements
		CommonService {

	@Value("${weixin.url.get_ip_list}")
	private String getCallbackIpUrl;

	@Override
	public String[] getWeixinIpList() throws WeixinException {
		Map<String, Object> result = readResultToMap(new HttpGet(
				getCallbackIpUrl), false);
		@SuppressWarnings("rawtypes")
		List ipList = (List) result.get("ip_list");
		if (ipList == null || ipList.size() == 0) {
			return null;
		}
		String[] ipArray = new String[ipList.size()];
		for (int i = 0; i < ipArray.length; i++) {
			ipArray[i] = ipList.get(i).toString();
		}
		return ipArray;
	}

	@Override
	public WeixinUploadResult upload(WeixinMediaType type, File uploadFile)
			throws WeixinException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WeixinUploadResult uploadArticles(WeixinArticle[] ariticles)
			throws WeixinException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void download(String mediaID, File saveFile) throws WeixinException {
		// TODO Auto-generated method stub

	}

}
