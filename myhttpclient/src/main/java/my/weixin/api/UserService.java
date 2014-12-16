package my.weixin.api;

import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinOpenIDList;
import my.weixin.api.pojo.WeixinUserInfo;

/**
 * �û��������
 * 
 * @author xzz
 */
public interface UserService {

	/**
	 * �����û���ע
	 * 
	 * @param openID �û�ID
	 * @param remark ��ע
	 * @throws WeixinException ҵ���쳣
	 */
	void updateRemark(String openID,String remark) throws WeixinException;
	
	/**
	 * ��ȡ�û���Ϣ
	 * 
	 * @param openID �û�ID
	 * @param lang ���� zh_CN ���壬zh_TW ���壬en Ӣ��
	 * @return �û���Ϣ
	 * @throws WeixinException ҵ���쳣
	 */
	WeixinUserInfo queryUserInfo(String openID,String lang)throws WeixinException;
	
    /**
     * ��ȡ��ע�û�ID�б���Ҫ��ε������
     * 
     * @param nextOpenID ���Ϊ�ձ�ʾ��ͷ��ʼ��ѯ����������һ�ν�����ص�nextopenid������ѯ
     * @return �û�ID�б�
     * @throws WeixinException
     */
    WeixinOpenIDList queryUserlist(String nextOpenID)throws WeixinException;
}
