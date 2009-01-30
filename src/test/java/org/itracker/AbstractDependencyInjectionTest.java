package org.itracker;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RunWith(JUnit4ClassRunner.class)
public abstract class AbstractDependencyInjectionTest extends
		AbstractDependencyInjectionSpringContextTests {

	private static final Logger log = Logger
			.getLogger(AbstractDependencyInjectionSpringContextTests.class);
	private DataSource dataSource;
	private LocalSessionFactoryBean sessionFactoryBean;
	public ClassLoader classLoader;
	public IDataSet dataSet;
	private SessionFactory sessionFactory;

	protected AbstractDependencyInjectionTest() {
		classLoader = getClass().getClassLoader();
	}

	@Override
	public void onSetUp() throws Exception {

		sessionFactory = (SessionFactory) applicationContext
				.getBean("sessionFactory");
		Session session = sessionFactory.openSession();
		TransactionSynchronizationManager.bindResource(sessionFactory,
				new SessionHolder(session));

		dataSet = getDataSet();
		DatabaseConnection dbConnection = null;
		try {
			dbConnection = new DatabaseConnection(getDataSource().getConnection());
			dbConnection.getConfig().setProperty(
					DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
					new HsqldbDataTypeFactory());

			if (dataSet != null) {
                            DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
				}

			if (!dbConnection.getConnection().getAutoCommit()) {
				dbConnection.getConnection().commit();
			}
		} catch (Exception e) {
			log.error("onSetUp: failed to set up datasets", e);
			throw e;
		} finally {
			if (null != dbConnection) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log.warn("onSetUp: failed to close connection", e);
				}
			}
		}
	}

	@Override
	public void onTearDown() throws Exception {
		DatabaseConnection dbConnection = null;
		try {
			dbConnection = new DatabaseConnection(getDataSource()
					.getConnection());

			if (dataSet != null) {
					DatabaseOperation.DELETE_ALL.execute(dbConnection, dataSet);
				}

			if (!dbConnection.getConnection().getAutoCommit()) {
				dbConnection.getConnection().commit();
			}

		} catch (Exception e) {
			log.error("onTearDown: failed to tear down datasets", e);
			throw e;
		} finally {
			TransactionSynchronizationManager.unbindResource(sessionFactory);
			sessionFactoryBean.destroy();

			if (null != dbConnection) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					log.warn("onTearDown: failed to close connection", e);
				}
			}
		}
	}

	private IDataSet getDataSet() throws Exception {
		final String[] aDataSet = getDataSetFiles();
                final IDataSet[] dataSets = new IDataSet[aDataSet.length];

		for (int i = 0; i < aDataSet.length; i++) {
			dataSets[i] = new XmlDataSet(classLoader
					.getResourceAsStream(aDataSet[i]));
		}
		return new CompositeDataSet(dataSets);
	}

	/**
	 * must make sure, that the order is correct, so no constraints will be
	 * violated.
	 * 
	 * @return
	 */
	protected abstract String[] getDataSetFiles();

	public LocalSessionFactoryBean getSessionFactoryBean() {
		return sessionFactoryBean;
	}

	public void setSessionFactoryBean(LocalSessionFactoryBean sessionFactoryBean) {
		this.sessionFactoryBean = sessionFactoryBean;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Before
	public final void callSetup() throws Exception {
		super.setUp();
	}

	@After
	public final void callTeardown() throws Exception {
		super.tearDown();
	}

}
