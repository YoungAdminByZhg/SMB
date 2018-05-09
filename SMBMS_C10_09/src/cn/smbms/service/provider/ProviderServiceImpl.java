package cn.smbms.service.provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.provider.ProviderDao;
import cn.smbms.pojo.Provider;

@Service
public class ProviderServiceImpl implements ProviderService {
	@Resource
	private ProviderDao providerDao;
	
	@Override
	public List<Provider> getProviderList(String proCode, String proName,
			int currentPageNo, int pageSize) {
		Connection connection = null;
		List<Provider> providerList = null;
		System.out.println("proCode ---- > " + proCode);
		System.out.println("proName ---- > " + proName);
		System.out.println("currentPageNo ---- > " + currentPageNo);
		System.out.println("pageSize ---- > " + pageSize);
		
		try {
			connection = BaseDao.getConnection();
			providerList = providerDao.getProviderList(connection, proCode, proName, currentPageNo, pageSize); 
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return providerList;
	}

	@Override
	public int getProviderCount(String proName,String proCode) {
		Connection connection = null;
		int count = 0;
		System.out.println("proName ---- > " + proName);
		try {
			connection = BaseDao.getConnection();
			count = providerDao.getProviderCount(connection, proName,proCode);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return count;
	}

	@Override
	public boolean addProvider(Provider provider) {
		boolean flag = false;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);//开启JDBC事务管理
			int updateRows = providerDao.addProvider(connection,provider);
			connection.commit();
			if(updateRows > 0){
				flag = true;
				System.out.println("add success!");
			}else{
				System.out.println("add failed!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.out.println("rollback==================");
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}

	@Override
	public Provider getProviderById(String id) {
		Connection connection = null;
		Provider provider = null;
		try {
			connection = BaseDao.getConnection();
			provider = providerDao.getProviderById(connection, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			BaseDao.closeResource(connection, null, null);
		}
		return provider;
	}

	@Override
	public boolean updateProvider(Provider provider) {
		boolean flag = false;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);
			int updateRows = providerDao.updateProvider(connection, provider);
			connection.commit();
			if(updateRows > 0){
				flag = true;
				System.out.println("modify success!");
			}else{
				System.out.println("modify failed!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			try {
				System.out.println("rollback==================");
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally{
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
}
