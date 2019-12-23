package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.dao.UserDAO;
import edu.hbuas.netdisk.model.User;
import edu.hbuas.netdisk.view.Main;
import edu.hbuas.netdisk.view.Register;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginControl implements Initializable {

    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    @FXML
    public Button loginBtn;
    @FXML
    public Button registerBtn;

    private UserDAO dao;
    private User user;   //登录的用户

    public User getUser() {
        return user;
    }

    public void put(){
        ControlCollections.controls.put(getClass(),this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        put();
        dao = new UserDAO();
    }

    //点击登录按钮时，查询用户信息
    @FXML
    public void login(){
        String usernameInput = username.getText().trim();
        String passwordInput = password.getText().trim();
        try {
            user = dao.login(usernameInput,passwordInput);
            if (user==null){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("登陆失败，用户名或密码错误！");
                a.show();
            }else{
                //跳转到主页面，并隐藏当前登录页面
                Main main = new Main();
                main.start(new Stage());
                loginBtn.getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //点击注册按钮，跳转到注册页面
    @FXML
    public void register(){
        Register register = new Register();
        register.start(new Stage());
        registerBtn.getScene().getWindow().hide();
    }
}
