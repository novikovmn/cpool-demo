package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DBDataUtil {

    /*---------------------------------------------------*/
    private static final DBDataUtil instance = new DBDataUtil();
    private DBDataUtil (){}

    public static DBDataUtil getInstance(){
        return instance;
    }
    /*---------------------------------------------------*/

    private static final String DB_CREDENTIONALS = "db.properties";
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_USER_KEY = "db.user";
    private static final String DB_PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "pool.size";

    private final Properties properties = new Properties();;
    private  String dbURL;
    private  String dbUser;
    private  String dbPassword;
    private  String poolSize;

    {
        loadProperties();
        initDBCredentionals();
    }

    private void initDBCredentionals(){
        dbURL = getValue(DB_URL_KEY);
        dbUser = getValue(DB_USER_KEY);
        dbPassword = getValue(DB_PASSWORD_KEY);
        poolSize = getValue(POOL_SIZE_KEY);
    }

    private void loadProperties(){
        InputStream inputStream = DBDataUtil.class.getClassLoader().getResourceAsStream(DB_CREDENTIONALS);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Can't load properties-file.");
        }
    }

    private String getValue(String key){
        return properties.getProperty(key);
    }

    public String getDbURL() {
        return dbURL;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getPoolSize() {
        return poolSize;
    }
}
