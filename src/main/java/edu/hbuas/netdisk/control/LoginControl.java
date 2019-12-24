package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
    }

    //点击登录按钮时，查询用户信息
    @FXML
    public void login(){
        String usernameInput = username.getText().trim();
        String passwordInput = password.getText().trim();
        try {
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            //封装用户登录的消息
            Message loginMessage = new Message();
            user = new User(usernameInput,passwordInput);
            loginMessage.setFrom(user);
            loginMessage.setType(MessageType.LOGIN);
            //写给服务器登录消息
            out.writeObject(loginMessage);
            out.flush();
            //读取服务器返回的消息
            Message loginResult = (Message) in.readObject();
            user = loginResult.getFrom();
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
            //关闭连接
            out.close();
            in.close();
            client.close();
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
