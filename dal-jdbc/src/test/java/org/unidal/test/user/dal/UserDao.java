package org.unidal.test.user.dal;

import org.unidal.dal.jdbc.DalException;
import org.unidal.dal.jdbc.QueryEngine;
import org.unidal.dal.jdbc.Readset;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.annotation.Named;

@Named
public class UserDao {
   @Inject
	private QueryEngine m_queryEngine;

	public User createLocal() {
		User user = new User();

		return user;
	}

	public boolean delete(User user) throws DalException {
		int rows = m_queryEngine.deleteSingle(UserEntity.DELETE_BY_PK, user);

		return rows > 0;
	}

	public User findByPK(int userId, Readset<User> readset) throws DalException {
		User proto = new User();

		proto.setKeyUserId(userId);

		User user = m_queryEngine.querySingle(UserEntity.FIND_BY_PK, proto,
				readset);

		return user;
	}

	public User findWithSubObjectsByPK(int userId, Readset<User> readset) throws DalException {
		User proto = new User();
		
		proto.setKeyUserId(userId);
		
		User user = m_queryEngine.querySingle(UserEntity.FIND_WITH_SUBOBJECTS_BY_PK, proto,
				readset);
		
		return user;
	}
	
	public boolean insert(User user) throws DalException {
		int rows = m_queryEngine.insertSingle(UserEntity.INSERT, user);

		return rows > 0;
	}
}
