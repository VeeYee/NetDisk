package edu.hbuas.netdisk.dao;

import edu.hbuas.netdisk.model.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class UserDAOImpl implements UserDAO{

    /**
     * 用户注册，向数据库插入一条数据
     * @param user
     * @return
     */
    @Override
    public boolean registerUser(User user) {
        boolean flag = false;
        QueryRunner runner = new QueryRunner(ConnectionPool.getDataSource());
        try {
            int count = runner.update("insert into user values(?,?,?,?,?,?,?)",
                    user.getUsername(),user.getPassword(),user.getSex(),user.getAge(),user.getTel(),user.getEmail(),user.getImages());
            flag = count>0?true:false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 用户登录，判断用户名密码是否正确
     * @param username
     * @param password
     * @return
     */
    @Override
    public User login(String username, String password) {
        User user = null;
        ResultSetHandler<User> bh = new BeanHandler<>(User.class);
        QueryRunner runner = new QueryRunner(ConnectionPool.getDataSource());
        try {
            user = runner.query("select * from USER where username=? and password=?",bh,username,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
