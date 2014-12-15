package my.weixin.api.result;

import my.weixin.api.enums.WeixinMediaType;

/**
 * 微信上传结果
 * @author xzz
 */
public class WeixinUploadResult{

	private WeixinMediaType type;
	
	private String mediaID;
	
	private String createAt;

	public WeixinMediaType getType() {
		return type;
	}

	public void setType(WeixinMediaType type) {
		this.type = type;
	}

	public String getMediaID() {
		return mediaID;
	}

	public void setMediaID(String mediaID) {
		this.mediaID = mediaID;
	}

	public String getCreateAt() {
		return createAt;
	}

	public void setCreateAt(String createAt) {
		this.createAt = createAt;
	}
	
}
