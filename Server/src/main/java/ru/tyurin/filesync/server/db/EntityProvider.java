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

	public static final String POSTGRESQL = "postgresql";
	public static final String H2 = "h2";

	private static EntityProvider provider;

	private EntityManagerFactory emf;
	private EntityManager em;

	private static EntityProvider createPgSQLProvider(String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) throws Exception {
		String url = String.format("jdbc:postgresql://%s:%d/%s", dbHost, dbPort, dbName);
		return new EntityProvider(POSTGRESQL, url, dbUser, dbPassword);
	}

	private static EntityProvider createH2Provider(String path, String dbUser, String dbPassword) throws Exception {
		String url = String.format("jdbc:h2:%s", path);
		return new EntityProvider(H2, url, dbUser, dbPassword);
	}

	private EntityProvider(String dbType, String url, String dbUser, String dbPassword) throws Exception {
		emf = getEntityManagerFactory(dbType, url, dbUser, dbPassword);
		em = emf.createEntityManager();
	}

	public static EntityProvider createInstance(String db, String dbHost, int dbPort, String dbName, String dbUser, String dbPassword) throws Exception {
		switch (db) {
			case POSTGRESQL:
				provider = createPgSQLProvider(dbHost, dbPort, dbName, dbUser, dbPassword);
				break;
			case H2:
				provider = createH2Provider(dbName, dbUser, dbPassword);
				break;
			default:
				throw new Exception(String.format("Database %s not supported", db));
		}
		return provider;
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

	public <E> E update(E entity) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		entity = em.merge(entity);
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
		provider.em.close();
//		provider.emf.close();
		provider = null;
		System.out.println("provider close");
	}

	private EntityManagerFactory getEntityManagerFactory(String dbType, String url, String dbUser, String dbPassword) throws Exception {
		Map<String, String> prop = new HashMap<>();
		prop.put("javax.persistence.jdbc.url", url);
		prop.put("javax.persistence.jdbc.user", dbUser);
		prop.put("javax.persistence.jdbc.password", dbPassword);
		DriverManager.setLoginTimeout(3);
		EntityManagerFactory factory = null;
		try {
			factory = Persistence.createEntityManagerFactory(dbType, prop);
		} catch (JDBCConnectionException e) {
			throw new Exception(e.getMessage(), e);
		}
		return factory;
	}

}
