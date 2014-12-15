package my.weixin.api;

import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinGroupMessage;
import my.weixin.api.pojo.WeixinMessage;
import my.weixin.api.result.WeixinGroupMsgResult;

/**
 * 微信消息服务 {@link http
 * ://mp.weixin.qq.com/wiki/7/12a5a320ae96fecdf0e15cb06123de9f.html}
 * 
 * @author xzz
 */
public interface MessageService {

	/**
	 * 发送客服消息，如果用户曾经48小时内发送过消息到服务器，则可以通过此接口返回
	 * 
	 * @param msg
	 *            消息内容，客户的openID放在消息中
	 * @return 发送结果
	 */
	void sendCustomServiceMsg(WeixinMessage msg) throws WeixinException;

	/**
	 * 群发消息
	 * 
	 * @param msg
	 *            群发消息
	 * @return 发送结果
	 */
	WeixinGroupMsgResult sendGroupMsg(WeixinGroupMessage msg)
			throws WeixinException;
}
