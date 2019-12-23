package edu.hbuas.netdisk.dao;

import edu.hbuas.netdisk.model.ClientFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Set;

public class FileDAO {

    public boolean upload(ClientFile clientFile) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/netdisk","root","root");
        PreparedStatement pre = con.prepareStatement("insert into file values(0,?,?,?)");
        pre.setString(1,clientFile.getUsername());
        pre.setString(2,clientFile.getFileName());
        pre.setLong(3,clientFile.getFileLength());
        int inserted = pre.executeUpdate();
        return inserted>0?true:false;
    }

    //通过用户名找到该用户上传的所有文件
    public Set<ClientFile> getFileList(String username) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/netdisk","root","root");
        Set<ClientFile> fileList = null;
        PreparedStatement pre = con.prepareStatement("select * from file where username=?");
        ResultSet rs = pre.executeQuery();
        while (rs.next()){
            ClientFile f = new ClientFile(rs.getString("username"),rs.getString("fileName"),rs.getLong("fileLength"));
            fileList.add(f);
        }
        return fileList;
    }
}
