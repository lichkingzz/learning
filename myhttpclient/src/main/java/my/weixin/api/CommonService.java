package my.weixin.api;

import java.io.File;

import my.weixin.api.enums.WeixinMediaType;
import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinArticle;
import my.weixin.api.result.WeixinUploadResult;

/**
 * ΢�Ż���֧�ֽӿ� {@link http
 * ://mp.weixin.qq.com/wiki/11/0e4b294685f817b95cbed85ba5e82b8f.html}
 * 
 * @author xzz
 */
public interface CommonService {

	/**
	 * ��ȡ�ӿڷ���Token��ÿ�η��ʵ�ʱ����Ҫ�������token
	 * 
	 * @return token�ֶ�
	 */
	String getAccessToken();

	/**
	 * ��ȡ΢�ŷ�������IP�б�
	 * 
	 * @return IP�б�
	 */
	String[] getWeixinIpList();

	/**
	 * �ϴ��ļ���΢�ŷ�����
	 * 
	 * @param type
	 *            �ϴ��ļ�����
	 * @param uploadFile
	 *            ��Ҫ�ϴ����ļ�
	 * @return �ϴ����
	 */
	WeixinUploadResult upload(WeixinMediaType type, File uploadFile)
			throws WeixinException;

	/**
	 * �ϴ��������͵���Ϣ
	 * 
	 * @param ariticles
	 *            �����б�
	 * @return �ϴ����
	 */
	WeixinUploadResult uploadArticles(WeixinArticle[] ariticles)
			throws WeixinException;

	/**
	 * ��΢�ŷ����������ļ�
	 * 
	 * @param mediaID
	 *            �ļ�ID
	 * @param saveFile
	 *            �����ļ��ı���λ��
	 * @return ���ؽ��
	 */
	void download(String mediaID, File saveFile) throws WeixinException;

}
