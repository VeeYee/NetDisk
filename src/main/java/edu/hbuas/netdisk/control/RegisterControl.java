package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.User;
import edu.hbuas.netdisk.utils.Toast;
import edu.hbuas.netdisk.view.Head;
import edu.hbuas.netdisk.view.Login;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class RegisterControl implements Initializable {

    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    @FXML
    public PasswordField confirmPassword;
    @FXML
    public RadioButton male;
    @FXML
    public RadioButton female;
    @FXML
    public DatePicker age;
    @FXML
    public ImageView myImage;
    @FXML
    public Button selectBtn;
    @FXML
    public TextField tel;
    @FXML
    public TextField email;
    @FXML
    public Button registerBtn;
    @FXML
    public Button loginBtn;

    private HeadControl headControl;
    private Image selectImage;
    private String url;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        age.setValue(LocalDate.now()); //生日默认是当前时间
    }

    //点击选择头像
    @FXML
    public void select(){
        Stage stage = new Stage();
        Head head = new Head();
        stage.setX(1100);
        stage.setY(100);
        head.start(stage);

        //监听选择头像窗口的隐藏
        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                headControl = (HeadControl) ControlCollections.controls.get(HeadControl.class);
                selectImage = headControl.getSelectImage();
                myImage.setImage(selectImage);
            }
        });
    }

    //点击注册按钮，提交用户填写的数据
    @FXML
    public void register() {
        //获取用户输入的数据
        String usernameInput = username.getText().trim();
        String passwordInput = password.getText().trim();
        String confirmPasswordInput = confirmPassword.getText().trim();
        String sexInput = male.isSelected()?"男":"女";
        String ageInput = age.getValue().toString();
        headControl = (HeadControl) ControlCollections.controls.get(HeadControl.class);
        this.url = headControl.getUrl();
        String imageInput = url;
        String telInput = tel.getText().trim();
        String emailInput = email.getText().trim();
        //表单验证
        Toast toast = new Toast(registerBtn.getScene().getWindow());
        Toast.Level level = Toast.Level.values()[0]; //提示级别
        if(usernameInput.equals("")){
            toast.show(level, 1000, "用户名不能为空！");
            username.requestFocus();
        }else if(passwordInput.equals("")){
            toast.show(level, 1000, "密码不能为空！");
            password.requestFocus();
        }else if(confirmPasswordInput.equals("")){
            toast.show(level, 1000, "请再次输入密码！");
            confirmPassword.requestFocus();
        }else if(!passwordInput.equals(confirmPasswordInput)){
            toast.show(level, 1000, "两次密码不一致！");
            confirmPassword.requestFocus();
        }else {
            try {
                Socket client = new Socket("localhost", 8123);
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                //封装用户注册的消息
                Message registerMessage = new Message();
                User user = new User(usernameInput, passwordInput, sexInput, ageInput, telInput, emailInput,imageInput);
                registerMessage.setFrom(user);
                registerMessage.setType(MessageType.REGISTER);
                //写给服务器注册消息
                out.writeObject(registerMessage);
                out.flush();

                //读取服务器返回的消息
                Message registerResult = (Message) in.readObject();
                user = registerResult.getFrom();
                if (user==null){
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("注册失败，该用户名已存在！");
                    a.show();
                }else{
                    //跳转到主页面，并隐藏当前登录页面
                    Login login = new Login();
                    login.start(new Stage());
                    registerBtn.getScene().getWindow().hide();
                }
                //关闭连接
                out.close();
                in.close();
                client.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //点击登录按钮，跳转到登录页面
    @FXML
    public void login(){
        Login login = new Login();
        login.start(new Stage());
        loginBtn.getScene().getWindow().hide();
    }
}
