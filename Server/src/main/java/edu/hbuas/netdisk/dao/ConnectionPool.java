package edu.hbuas.netdisk.dao;

import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

//连接池帮助类
public class ConnectionPool {

    private static DataSource dataSource;
    private static Connection connection;

    static {
        Properties properties = new Properties();
        try {
            //读取配置文件
            properties.load(new FileInputStream("D:\\Zidea\\NetDisk\\Server\\src\\main\\resources\\datasource.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取连接
    public static Connection getConnection(){
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    //数据源
    public static DataSource getDataSource(){
        return dataSource;
    }

}
