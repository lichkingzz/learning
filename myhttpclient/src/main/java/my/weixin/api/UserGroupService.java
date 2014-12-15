package my.weixin.api;

import my.weixin.api.exception.WeixinException;
import my.weixin.api.pojo.WeixinGroup;

/**
 * 用户分组服务
 * 
 * @author xzz
 */
public interface UserGroupService {

	/**
	 * 创建用户分组
	 * 
	 * @param group
	 *            用户分组
	 * @return 创建结果
	 */
	void createGroup(WeixinGroup group) throws WeixinException;

	/**
	 * 查询所有用户分组
	 * 
	 * @return 用户分组列表
	 */
	WeixinGroup[] queryAllGroups() throws WeixinException;

	/**
	 * 查询用户所在的分组
	 * 
	 * @param openID
	 *            用户ID
	 * @return 用户分组ID
	 */
	String queryGroupIDByOpenID(String openID) throws WeixinException;

	/**
	 * 修改用户分组信息
	 * 
	 * @param group 用户分组
	 */
	void updateGroup(WeixinGroup group) throws WeixinException;
	
	/**
	 * 移动用户到某个用户分组下
	 * 
	 * @param openID 用户ID
	 * @param groupID 分组ID
	 */
	void moveUserToGroup(String openID,String groupID) throws WeixinException;
}
