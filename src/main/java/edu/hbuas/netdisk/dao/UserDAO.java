package edu.hbuas.netdisk.dao;

import edu.hbuas.netdisk.model.User;

import java.sql.*;

public class UserDAO {

    //注册时，向数据库user表中插入一条数据
    public boolean registerUser(User user) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/netdisk","root","root");
        PreparedStatement pre = con.prepareStatement("insert into user values(?,?,?,?,?,?,?)");
        pre.setString(1,user.getUsername());
        pre.setString(2,user.getPassword());
        pre.setString(3,user.getSex());
        pre.setString(4,user.getAge());
        pre.setString(5,user.getTel());
        pre.setString(6,user.getEmail());
        pre.setString(7,user.getImages());
        int inserted = pre.executeUpdate();
        return inserted>0?true:false;
    }

    //登录时，在数据库查找用户是否存在
    public User login(String username,String password) throws Exception {
        User user = null;
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/netdisk","root","root");
        PreparedStatement pre = con.prepareStatement("select * from user where USERNAME=? and PASSWORD=?");
        pre.setString(1,username);
        pre.setString(2,password);
        ResultSet rs = pre.executeQuery();
        if(rs.next()){
            user = new User();
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setSex(rs.getString("sex"));
            user.setAge(rs.getString("age"));
            user.setTel(rs.getString("tel"));
            user.setEmail(rs.getString("email"));
            user.setImages(rs.getString("images"));
        }
        return user;
    }
}
