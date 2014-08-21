package com.renny.db;

import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public abstract class AbstractFrontier {

	private Environment env;
	private static final String CLASS_CATALOG ="java_class_catalog";
	
	protected StoredClassCatalog javaCatalog;
	protected Database catalogdatabase;
	protected Database database;
	
	public AbstractFrontier (String homeDirectory) throws DatabaseException, FileNotFoundException {
		System.out.println("open environment in: " +homeDirectory);
		EnvironmentConfig envConfig =new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		env =new Environment(new File(homeDirectory), envConfig);
		
		DatabaseConfig dbConfig =new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);
		
		catalogdatabase =env.openDatabase(null, CLASS_CATALOG, dbConfig);
		javaCatalog =new StoredClassCatalog(catalogdatabase);
		database =env.openDatabase(null, "URL", dbConfig);
	}
	
	public void close() throws DatabaseException {
		database.close();
		javaCatalog.close();
		env.close();
	}
	
	protected abstract void put(Object key, Object value);
	
	protected abstract Object get(Object key);
	
	protected abstract Object delete(Object key);
	
}
