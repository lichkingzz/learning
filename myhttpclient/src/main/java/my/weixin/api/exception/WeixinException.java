package my.weixin.api.exception;

/**
 * 微信业务异常
 * 
 * @author xzz
 */
public class WeixinException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8769689069270609539L;
	
	public WeixinException(){
		
	}
	
	public WeixinException(String errorcode,String errmsg){
		super("errcode:" + errorcode + ",errmsg:" + errmsg);
		this.errcode = errorcode;
		this.errmsg = errmsg;
	}

	private String errcode;

	private String errmsg;

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	
}
