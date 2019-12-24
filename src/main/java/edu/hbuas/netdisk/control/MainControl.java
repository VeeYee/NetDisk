package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.User;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class MainControl implements Initializable {

    @FXML
    private ImageView images;
    @FXML
    private Label username;
    @FXML
    private GridPane gridPane;

    @FXML
    private Button uploadBtn;
    @FXML
    private Button downloadBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button newBtn;
    @FXML
    private Button search;

    private User user;

    //网格的行与列
    private int row = 0;
    private int col = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //获取登录的用户
        LoginControl loginControl = (LoginControl) ControlCollections.controls.get(LoginControl.class);
        this.user = loginControl.getUser();
        images.setImage(new Image(user.getImages()));
        username.setText(user.getUsername());

        //建立Socket连接，将当前用户的文件列表从服务器读取过来
        Socket client = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            client = new Socket("localhost",8123);
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //封装更新界面的消息
        Message updateMessage = new Message();
        updateMessage.setFrom(user);
        updateMessage.setType(MessageType.UPDATE);
        try {
            out.writeObject(updateMessage);
            out.flush();

            //读取服务器回复的结果消息
            Message updateResult = (Message)in.readObject();
            Set<File> fileList = updateResult.getFileList();
            updateUI(fileList);
            //关闭连接
            in.close();
            out.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //更新界面的方法
    public void updateUI(Set<File> fileList){
        if(fileList!=null && fileList.size()>0) {
            for (File f : fileList) {
                String suffix = f.getName().split("\\.")[f.getName().split("\\.").length-1];
                //图片
                ImageView fileImage = null;
                try {
                    fileImage = new ImageView(new Image("images/" + suffix + ".png"));
                }catch (Exception e){
                    fileImage = new ImageView(new Image("images/other.png"));
                }
                fileImage.setFitWidth(78);
                fileImage.setFitHeight(78);
                //文件名
                Label fileName = new Label(f.getName());
                //垂直布局
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(fileImage, fileName);

                if (col == 6) {
                    row++;  //行
                    col = 0;
                }
                gridPane.add(vBox, col++, row);
                click(vBox);  //点击事件
            }
        }
    }

//    private boolean alert(String fileName){
////        System.out.println(fileName);
//        fileName = fileName.split("'\'")[fileName.split("'\'").length-1];
//        for(File f:files){
////            System.out.println(f.getName());
//            if(f.getName().equals(fileName)){
//                Alert a = new Alert(Alert.AlertType.INFORMATION);
//                a.setContentText("文件已存在");
//                a.show();
//                return false;
//            }
//        }
//        return true;
//    }

    public void click(VBox vBox){
        vBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
//                if(event.getButton()== MouseButton.PRIMARY){
//                    //鼠标左击事件
//                }
//                if(event.getButton()==MouseButton.SECONDARY){
//                    //鼠标右击事件
//                }
//                if(event.getClickCount()==2){
//                    //双击事件
//                }
                System.out.println(vBox.getChildren().get(1));
//                vBox.setFocusable(true);
//                vBox.requestFocus();
//                vBox.requestFocusFromTouch();
                vBox.setStyle("-fx-background-color: #8cd3ec");
                vBox.setFocusTraversable(true);
//                System.out.println(vBox.isFocused());
//                if(vBox.isFocused()) {
//                    vBox.setStyle("-fx-background-color: white");
//                    vBox.setFocusTraversable(false);
//                }
//                System.out.println(vBox.getChildren().get(1));
//                for(int i=0; i<vBox.getChildren().size(); i++){
//                    System.out.println((Label)vBox.getChildren().get(i));
//                }
//                System.out.println(gridPane.getColumnConstraints());
            }
        });
    }

    //点击上传文件按钮
    @FXML
    public void uploadFile(){
        //建立短连接，上传或下载时才连接
        Socket client = null;
        ObjectOutputStream out = null;

        //1. 弹出文件选择器，让用户选择文件
        FileChooser fc = new FileChooser();
        File selectFile = fc.showOpenDialog(uploadBtn.getScene().getWindow());

//        if(!alert(selectFile.getPath())){
//            return;
//        }

        //2. 在执行上传之前先建立底层的socket连接
        try {
            client = new Socket("localhost",8123);
            out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3. 上传之前先封装一个消息对象
        Message uploadMessage = new Message();
        uploadMessage.setFrom(user);
        uploadMessage.setType(MessageType.UPLOAD);
        uploadMessage.setFileName(selectFile.getName());
        uploadMessage.setFileLength(selectFile.length());

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
            while((length = fileIn.read(bs))!=-1) {
                out.write(bs,0,length);
                out.flush();
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("文件上传成功");
            a.show();

            Set<File> file = new HashSet<>();
            file.add(selectFile);
            updateUI(file);

            //短连接，上传完即关闭流
            fileIn.close();
            out.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
