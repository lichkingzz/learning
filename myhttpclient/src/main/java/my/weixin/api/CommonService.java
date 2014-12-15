package my.weixin.api;

import java.io.File;

import my.weixin.api.enums.WeixinMediaType;
import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinArticle;
import my.weixin.api.result.WeixinUploadResult;

/**
 * 微信基础支持接口 {@link http
 * ://mp.weixin.qq.com/wiki/11/0e4b294685f817b95cbed85ba5e82b8f.html}
 * 
 * @author xzz
 */
public interface CommonService {

	/**
	 * 获取接口访问Token，每次访问的时候都需要输入添加token
	 * 
	 * @return token字段
	 */
	String getAccessToken();

	/**
	 * 获取微信服务器的IP列表
	 * 
	 * @return IP列表
	 */
	String[] getWeixinIpList();

	/**
	 * 上传文件到微信服务器
	 * 
	 * @param type
	 *            上传文件类型
	 * @param uploadFile
	 *            需要上传的文件
	 * @return 上传结果
	 */
	WeixinUploadResult upload(WeixinMediaType type, File uploadFile)
			throws WeixinException;

	/**
	 * 上传文章类型的消息
	 * 
	 * @param ariticles
	 *            文章列表
	 * @return 上传结果
	 */
	WeixinUploadResult uploadArticles(WeixinArticle[] ariticles)
			throws WeixinException;

	/**
	 * 从微信服务器下载文件
	 * 
	 * @param mediaID
	 *            文件ID
	 * @param saveFile
	 *            下载文件的保存位置
	 * @return 下载结果
	 */
	void download(String mediaID, File saveFile) throws WeixinException;

}
