package my.weixin.api;

import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinGroupMessage;
import my.weixin.api.pojo.WeixinMessage;
import my.weixin.api.result.WeixinGroupMsgResult;

/**
 * ΢����Ϣ���� {@link http
 * ://mp.weixin.qq.com/wiki/7/12a5a320ae96fecdf0e15cb06123de9f.html}
 * 
 * @author xzz
 */
public interface MessageService {

	/**
	 * ���Ϳͷ���Ϣ������û�����48Сʱ�ڷ��͹���Ϣ���������������ͨ���˽ӿڷ���
	 * 
	 * @param msg
	 *            ��Ϣ���ݣ��ͻ���openID������Ϣ��
	 * @return ���ͽ��
	 */
	void sendCustomServiceMsg(WeixinMessage msg) throws WeixinException;

	/**
	 * Ⱥ����Ϣ
	 * 
	 * @param msg
	 *            Ⱥ����Ϣ
	 * @return ���ͽ��
	 */
	WeixinGroupMsgResult sendGroupMsg(WeixinGroupMessage msg)
			throws WeixinException;
}
