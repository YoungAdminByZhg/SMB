package cn.smbms.dao.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mysql.jdbc.StringUtils;

import cn.smbms.dao.BaseDao;
import cn.smbms.pojo.Provider;

@Repository
public class ProviderDaoImpl implements ProviderDao {

	@Override
	public List<Provider> getProviderList(Connection connection,String proCode, String proName, int currentPageNo, int pageSize)
			throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<Provider> providerList = new ArrayList<Provider>();
		if (connection != null) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT * FROM smbms_provider where 1=1 ");
			List<Object> list = new ArrayList<Object>();
			if (!StringUtils.isNullOrEmpty(proCode)) {
				sql.append(" and proCode like ?");
				list.add("%" + proCode + "%");
			}
			if (!StringUtils.isNullOrEmpty(proName)) {
				sql.append(" and proName like ?");
				list.add("%" + proName + "%");
			}
			sql.append(" order by creationDate DESC limit ?,?");
			currentPageNo = (currentPageNo - 1) * pageSize;
			list.add(currentPageNo);
			list.add(pageSize);

			Object[] params = list.toArray();
			System.out.println("sql ----> " + sql.toString());
			rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);

			while (rs.next()) {
				Provider provider = new Provider();
				provider.setId(rs.getInt("id"));
				provider.setProCode(rs.getString("proCode"));
				provider.setProName(rs.getString("proName"));
				provider.setProDesc(rs.getString("proDesc"));
				provider.setProContact(rs.getString("proContact"));
				provider.setProPhone(rs.getString("proPhone"));
				provider.setProAddress(rs.getString("proAddress"));
				provider.setProFax(rs.getString("proFax"));
				provider.setCreatedBy(rs.getInt("createdBy"));
				provider.setCreationDate(rs.getDate("creationDate"));
				provider.setModifyBy(rs.getInt("modifyBy"));
				provider.setModifyDate(rs.getDate("modifyDate"));
				provider.setCompanyLicPicPath(rs.getString("companyLicPicPath"));
				provider.setOrgCodePicPath(rs.getString("orgCodePicPath"));
				providerList.add(provider);
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return providerList;
	}

	@Override
	public int getProviderCount(Connection connection, String proName,
			String proCode) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int count = 0;
		if (connection != null) {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT count(*) as count FROM smbms_provider where 1=1 ");
			List<Object> list = new ArrayList<Object>();
			if (!StringUtils.isNullOrEmpty(proName)) {
				sql.append(" and proName like ?");
				list.add("%" + proName + "%");
			}
			if (!StringUtils.isNullOrEmpty(proCode)) {
				sql.append(" and proCode like ?");
				list.add("%" + proCode + "%");
			}
			Object[] params = list.toArray();
			System.out.println("sql ----> " + sql.toString());
			rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
			if (rs.next()) {
				count = rs.getInt("count");
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return count;
	}

	@Override
	public int addProvider(Connection connection, Provider provider) throws Exception {
		PreparedStatement pstm = null;
		int updateRows = 0;
		if (connection != null) {
			String sql = "INSERT INTO smbms_provider (proCode, proName, proDesc, proContact, proPhone, proAddress, "
					+ "proFax, createdBy, creationDate,companyLicPicPath,orgCodePicPath) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			Object[] params = { provider.getProCode(), provider.getProName(),
					provider.getProDesc(), provider.getProContact(), provider.getProPhone(),
					provider.getProAddress(), provider.getProFax(), provider.getCreatedBy(),
					provider.getCreationDate(),provider.getCompanyLicPicPath(),provider.getOrgCodePicPath() };
			updateRows = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
		}
		return updateRows;
	}

	@Override
	public Provider getProviderById(Connection connection, String id) throws Exception {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		Provider provider = null;
		if(connection != null){
			String sql = "SELECT * FROM smbms_provider where id=?;";
			Object[] params = {id};
			rs = BaseDao.execute(connection, pstm, rs, sql, params);
			if(rs.next()){
				provider = new Provider(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getInt(9), rs.getDate(10), rs.getDate(11), rs.getInt(12),rs.getString(13),rs.getString(14));
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return provider;
	}

	@Override
	public int updateProvider(Connection connection,Provider provider)
			throws Exception {
		PreparedStatement pstm = null;
		int updateRows = 0;
		if(connection != null){
			String sql = "UPDATE smbms_provider SET proCode=?, proName=?, proDesc=?, "
					+ "proContact=?, proPhone=?, proAddress=?, proFax=?,modifyDate=?,"
					+ " modifyBy=? WHERE (id=?)";
			Object[] params = {provider.getProCode(),provider.getProName(),provider.getProDesc(),
					provider.getProContact(),provider.getProPhone(),provider.getProAddress(),provider.getProFax(),
					provider.getModifyDate(),provider.getModifyBy(),provider.getId()};
			updateRows = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
		}
		return updateRows;
	}
}
