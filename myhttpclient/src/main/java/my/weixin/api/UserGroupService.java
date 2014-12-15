package my.weixin.api;

import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinGroup;

/**
 * �û��������
 * 
 * @author xzz
 */
public interface UserGroupService {

	/**
	 * �����û�����
	 * 
	 * @param group
	 *            �û�����
	 * @return �������
	 */
	void createGroup(WeixinGroup group) throws WeixinException;

	/**
	 * ��ѯ�����û�����
	 * 
	 * @return �û������б�
	 */
	WeixinGroup[] queryAllGroups() throws WeixinException;

	/**
	 * ��ѯ�û����ڵķ���
	 * 
	 * @param openID
	 *            �û�ID
	 * @return �û�����ID
	 */
	String queryGroupIDByOpenID(String openID) throws WeixinException;

	/**
	 * �޸��û�������Ϣ
	 * 
	 * @param group �û�����
	 */
	void updateGroup(WeixinGroup group) throws WeixinException;
	
	/**
	 * �ƶ��û���ĳ���û�������
	 * 
	 * @param openID �û�ID
	 * @param groupID ����ID
	 */
	void moveUserToGroup(String openID,String groupID) throws WeixinException;
}
