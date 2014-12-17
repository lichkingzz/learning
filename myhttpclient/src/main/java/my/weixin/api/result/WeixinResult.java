package my.weixin.api.result;

/**
 * 微信通用返回结果
 * @author xzz
 */
public class WeixinResult {

	private String errorcode;
	
	private String errmsg;

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
}
