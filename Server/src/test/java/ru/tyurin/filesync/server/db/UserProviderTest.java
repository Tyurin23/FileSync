package ru.tyurin.filesync.server.db;

import org.testng.annotations.*;
import ru.tyurin.filesync.server.db.tables.UserEntity;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * User: tyurin
 * Date: 8/27/13
 * Time: 9:10 AM
 */
public class UserProviderTest {

	final String DB_HOST = "localhost";
	final int DB_PORT = 5432;
	final String DB_NAME = "fsync";
	final String DB_USER = "fsync";
	final String DB_PASSWORD = "fsync";

	@BeforeClass
	public void setUp() throws Exception {
		EntityProvider.createInstance(DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD);
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {


	}

	@AfterMethod
	public void tearDownMethod() throws Exception {

	}

	@AfterClass
	public void tearDown() throws Exception {

	}

	@Test
	public void testUserProvider() throws Exception {
		final String LOGIN = "login";
		final String PASSWORD = "password";
		UserProvider provider = new UserProvider();
		UserEntity user = new UserEntity();
		user.setLogin(LOGIN);
		user.setPassword(PASSWORD);
		UserEntity savedUser = provider.addUser(user);
		assertNotNull(savedUser);
		assertEquals(LOGIN, savedUser.getLogin());
		assertEquals(PASSWORD, savedUser.getPassword());
		assertNotNull(savedUser.getId());

		UserEntity fUser = provider.findById(savedUser.getId());
		assertNotNull(fUser);
		assertEquals(LOGIN, savedUser.getLogin());
		assertEquals(PASSWORD, savedUser.getPassword());
		assertNotNull(savedUser.getId());
		assertEquals(savedUser.getId(), fUser.getId());

	}
}
