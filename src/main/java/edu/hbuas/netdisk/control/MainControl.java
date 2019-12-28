package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.Transfer;
import edu.hbuas.netdisk.model.User;
import edu.hbuas.netdisk.utils.DateUtil;
import edu.hbuas.netdisk.utils.FileSizeUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class MainControl implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane scrollContent;

    //头像及用户名
    @FXML
    private ImageView images;
    @FXML
    private Label username;

    @FXML
    private ToggleButton myNetDiskBtn;
    @FXML
    private ToggleButton transferBtn;
    @FXML
    private ToggleButton uploadBtn;
    @FXML
    private ToggleButton newBtn;

    //文件搜索框
    @FXML
    private TextField searchText;

    //登陆进来的用户
    private User user;

    //存放所有文件的集合
    private LinkedList<File> fileList = new LinkedList<>();

    private FileControl fileControl;
    private TransferControl transferControl;

    public AnchorPane getScrollContent() {
        return scrollContent;
    }

    public User getUser() {
        return user;
    }

    public LinkedList<File> getFileList() {
        return fileList;
    }

    private void put(){
        ControlCollections.controls.put(getClass(),this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        put();
        scrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
        //获取登录的用户
        LoginControl loginControl = (LoginControl) ControlCollections.controls.get(LoginControl.class);
        this.user = loginControl.getUser();
        images.setImage(new Image(user.getImages()));
        username.setText(user.getUsername());

        this.transferControl = new TransferControl();

        //初始化界面时，加载所有的文件
        try {
            //建立Socket连接，将当前用户的文件列表从服务器读取过来
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            //封装更新界面的消息
            Message updateMessage = new Message(user,MessageType.UPDATE,null,0);
            out.writeObject(updateMessage);
            out.flush();
            //读取服务器回复的结果消息
            Message updateResult = (Message)in.readObject();
            this.fileList = updateResult.getFileList();
            //取到集合后再实例化文件控制器
            this.fileControl = new FileControl();
            myNetDisk();
            //关闭连接
            in.close();
            out.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //清除按钮的背景颜色
    private void clearBtnBackground(){
        myNetDiskBtn.setStyle("-fx-background-color: #eef0f6");
        transferBtn.setStyle("-fx-background-color:  #eef0f6");
        uploadBtn.setStyle("-fx-background-color: #eef0f6");
        newBtn.setStyle("-fx-background-color: #eef0f6");
    }

    //打开我的网盘
    @FXML
    public void myNetDisk(){
        clearBtnBackground();
        myNetDiskBtn.setStyle("-fx-background-color: rgba(96,204,255,0.7); -fx-background-radius: 20");
        fileControl.updateUI(fileList);
    }

    //打开传输列表
    @FXML
    public void transferList(){
        clearBtnBackground();
        transferBtn.setStyle("-fx-background-color: rgba(96,204,255,0.7); -fx-background-radius: 20");
        transferControl.loadRecord();
    }

    //上传文件的方法
    @FXML
    public void uploadFile(){
        clearBtnBackground();
        uploadBtn.setStyle("-fx-background-color: rgba(96,204,255,0.7); -fx-background-radius: 20");
        //建立短连接，上传或下载时才连接
        Socket client = null;
        ObjectOutputStream out = null;
        //1. 弹出文件选择器，让用户选择文件
        FileChooser fc = new FileChooser();
        File selectFile = fc.showOpenDialog(scrollPane.getScene().getWindow());
        if(selectFile!=null) {
            //2. 在执行上传之前先建立底层的socket连接
            try {
                client = new Socket("localhost",8123);
                out = new ObjectOutputStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //3. 上传之前先封装一个消息对象
            Message uploadMessage = new Message(user, MessageType.UPLOAD,
                    selectFile.getAbsolutePath()+"   "+selectFile.getName(), selectFile.length());
            //4. 向服务器传递上传文件的消息
            try {
                out.writeObject(uploadMessage);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //5. 消息对象发送给服务器后，就开始上传文件数据
            try {
                FileInputStream fileIn = new FileInputStream(selectFile);
                byte[] bs = new byte[1024];
                int length = -1;
                //通过字节流从本地磁盘读到内存，再通过通道流写给服务器
                while ((length = fileIn.read(bs)) != -1) {
                    out.write(bs, 0, length);
                    out.flush();
                }
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("文件上传成功");
                a.show();
                //更新界面，新增一个文件
                fileList.add(selectFile);
                myNetDisk();
                fileIn.close();
                out.close();
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("上传失败，未选择文件");
        }
    }

    //点击搜索按钮搜索
    @FXML
    public void searchFile1(){
        String fileName = searchText.getText();
        if (!fileName.equals("")){
            LinkedList<File> list = new LinkedList<>();
            for (int i=0;i<fileList.size();i++){
                if (fileList.get(i).getName().contains(fileName)){
                    list.add(fileList.get(i));
                }
            }
            fileControl.updateUI(list);
        }else {
            fileControl.updateUI(fileList);
        }
    }

    //回车搜索
    @FXML
    public void searchFile2(){
        searchText.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER){  //回车搜索
                    searchFile1();
                }
            }
        });
    }
}
