package ru.tyurin.filesync.server.db;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import ru.tyurin.filesync.server.db.tables.UserEntity;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 9:41 AM
 */
public class UserProvider {

	private EntityProvider provider;

	public UserProvider() {
		this.provider = EntityProvider.getInstance();
	}

	public UserEntity addUser(UserEntity user) {
		return provider.save(user);
	}

	public UserEntity findById(int id) {
		return provider.findById(UserEntity.class, id);
	}

	public UserEntity findByLoginAndPassword(String login, String password) {
		Session session = provider.getSession();
		Criteria criteria = session.createCriteria(UserEntity.class);
		criteria.add(Restrictions.eq("login", login))
				.add(Restrictions.eq("password", password));
		return (UserEntity) criteria.uniqueResult();
	}

	public UserEntity createUser(String login, String password) {
		UserEntity user = new UserEntity();
		user.setLogin(login);
		user.setPassword(password);
		return provider.save(user);
	}

	public void updateUser(UserEntity user) {
		provider.update(user);
	}


}
