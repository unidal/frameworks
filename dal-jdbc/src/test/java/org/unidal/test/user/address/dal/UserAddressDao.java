package org.unidal.test.user.address.dal;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.Readset;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named
public class UserAddressDao {
   @Inject
	private QueryEngine m_queryEngine;

	public UserAddress createLocal() {
		UserAddress userAddress = new UserAddress();

		return userAddress;
	}

	public boolean delete(UserAddress userAddress) throws DalException {
		int rows = m_queryEngine.deleteSingle(UserAddressEntity.DELETE_BY_PK,
				userAddress);

		return rows > 0;
	}

	public boolean deleteAllByUserId(int userId) throws DalException {
		UserAddress proto = createLocal();

		proto.setKeyUserId(userId);

		int rows = m_queryEngine.deleteSingle(
				UserAddressEntity.DELETE_ALL_BY_USER_ID, proto);

		return rows > 0;
	}

	public boolean insert(UserAddress userAddress) throws DalException {
		int rows = m_queryEngine.insertSingle(UserAddressEntity.INSERT,
				userAddress);

		return rows > 0;
	}

	public UserAddress findByPK(int userId, Readset<UserAddress> readset)
			throws DalException {
		UserAddress proto = new UserAddress();

		proto.setUserId(userId);

		UserAddress user = m_queryEngine.querySingle(
				UserAddressEntity.FIND_BY_PK, proto, readset);

		return user;
	}
}
