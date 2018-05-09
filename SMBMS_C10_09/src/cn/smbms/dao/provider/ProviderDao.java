package cn.smbms.dao.provider;

import java.sql.Connection;
import java.util.List;

import cn.smbms.pojo.Provider;

public interface ProviderDao{
	/**
	 * 通过条件查询-providerList
	 * @param connection
	 * @param proCode
	 * @param proName
	 * @param currentPageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public List<Provider> getProviderList(Connection connection,String proCode,String proName,int currentPageNo, int pageSize)throws Exception;
	/**
	 * 通过条件查询-供应商表记录数
	 * @param connection
	 * @param userName
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public int getProviderCount(Connection connection,String proName,String proCode)throws Exception;
	
	/**
	 * 添加供应商
	 * @param connection
	 * @param provider
	 * @return
	 */
	public int addProvider(Connection connection,Provider provider) throws Exception;
	
	/**
	 * 根据id获取供应商信息
	 * @param connection
	 * @param id
	 * @return
	 */
	public Provider getProviderById(Connection connection,String id) throws Exception;
	
	/**
	 * 修改供应商
	 * @param connection
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public int updateProvider(Connection connection,Provider provider) throws Exception;
}
