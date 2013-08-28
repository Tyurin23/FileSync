package ru.tyurin.filesync.server.db;


import org.hibernate.Session;
import org.hibernate.exception.JDBCConnectionException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

/**
 * User: tyurin
 * Date: 8/27/13
 * Time: 10:44 AM
 */
public class EntityProvider {

	private static EntityProvider provider;

	private EntityManagerFactory emf;
	private EntityManager em;

	private static EntityProvider createPgSQLProvider(String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) throws Exception {
		return new EntityProvider(org.postgresql.Driver.class, dbHost, dbPort, dbName, dbUser, dbPassword);
	}

	private EntityProvider(Class driver, String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) throws Exception {
		emf = getEntityManagerFactory(driver, dbHost, dbPort, dbName, dbUser, dbPassword);
		em = emf.createEntityManager();
	}

	public static EntityProvider createInstance(String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) throws Exception {
		return provider = createPgSQLProvider(dbHost, dbPort, dbName, dbUser, dbPassword);
	}

	public static EntityProvider getInstance() {
		return provider;
	}

	public <E> E save(E entity) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(entity);
		tx.commit();
		return entity;
	}

	public <E> E findById(Class<E> entity, Object id) {
		return em.find(entity, id);
	}

	public Session getSession() {
		return em.unwrap(Session.class);
	}

	public EntityManager getEntityManager() {
		return em;
	}

	public static void close() {
		getInstance().em.close();
		getInstance().emf.close();
		provider = null;
	}

	private EntityManagerFactory getEntityManagerFactory(Class driver, String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) throws Exception {
		Map<String, String> prop = new HashMap<>();
		DriverManager.setLoginTimeout(3);
		prop.put("javax.persistence.jdbc.driver", driver.getCanonicalName());
		prop.put("javax.persistence.jdbc.url", String.format("jdbc:postgresql://%s:%d/%s", dbHost, dbPort, dbName));
		prop.put("javax.persistence.jdbc.user", dbUser);
		prop.put("javax.persistence.jdbc.password", dbPassword);
		prop.put("javax.persistence.lock.timeout", "2000");
		prop.put("javax.persistence.query.timeout", "1000");
		EntityManagerFactory factory = null;
		try {
			factory = Persistence.createEntityManagerFactory("FSync", prop);
		} catch (JDBCConnectionException e) {
			throw new Exception(e.getMessage(), e);
		}
		return factory;
	}
}
