package my.weixin.api.pojo;

import my.weixin.api.enums.MsgType;

/**
 * 微信消息
 * 
 * @author xzz
 */
public class WeixinMessage {

	/**
	 * 客户openID
	 */
	private String touser;
	
	/**
	 * 消息类型
	 */
	private MsgType msgType;

	public String getTouser() {
		return touser;
	}

	public void setTouser(String touser) {
		this.touser = touser;
	}

	public MsgType getMsgType() {
		return msgType;
	}

	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}
	
}
