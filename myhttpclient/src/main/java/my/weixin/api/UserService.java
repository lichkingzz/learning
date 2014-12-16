package my.weixin.api;

import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinOpenIDList;
import my.weixin.api.pojo.WeixinUserInfo;

/**
 * 用户管理服务
 * 
 * @author xzz
 */
public interface UserService {

	/**
	 * 更新用户备注
	 * 
	 * @param openID 用户ID
	 * @param remark 备注
	 * @throws WeixinException 业务异常
	 */
	void updateRemark(String openID,String remark) throws WeixinException;
	
	/**
	 * 获取用户信息
	 * 
	 * @param openID 用户ID
	 * @param lang 语言 zh_CN 简体，zh_TW 繁体，en 英语
	 * @return 用户信息
	 * @throws WeixinException 业务异常
	 */
	WeixinUserInfo queryUserInfo(String openID,String lang)throws WeixinException;
	
    /**
     * 获取关注用户ID列表，需要多次调用完成
     * 
     * @param nextOpenID 如果为空表示从头开始查询，否则传入上一次结果返回的nextopenid继续查询
     * @return 用户ID列表
     * @throws WeixinException
     */
    WeixinOpenIDList queryUserlist(String nextOpenID)throws WeixinException;
}
