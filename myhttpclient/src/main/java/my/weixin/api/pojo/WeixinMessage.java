package my.weixin.api.pojo;

import my.weixin.api.enums.MsgType;

/**
 * ΢����Ϣ
 * 
 * @author xzz
 */
public class WeixinMessage {

	/**
	 * �ͻ�openID
	 */
	private String touser;
	
	/**
	 * ��Ϣ����
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
