package edu.hbuas.netdisk.dao;

import edu.hbuas.netdisk.model.User;

public interface UserDAO {
    boolean registerUser(User user);
    User login(String username,String password);
}
