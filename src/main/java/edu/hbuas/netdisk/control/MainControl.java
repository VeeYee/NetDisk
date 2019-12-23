package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.ClientFile;
import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
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

    private int row = 0;
    private int col = 0;

    private File files[] = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //获取登录的用户
        LoginControl loginControl = (LoginControl) ControlCollections.controls.get(LoginControl.class);
        this.user = loginControl.getUser();
        images.setImage(new Image(user.getImages()));
        username.setText(user.getUsername());

        //登录成功，初始化界面
        File file = new File("D:/NetDiskFile/" + user.getUsername());  //遍历用户目录下的文件
        files = file.listFiles();
        if(files!=null && files.length>0) {
            for (File f : files) {
                String suffix = f.getName().split("\\.")[1];
                ImageView fileImage = null;
                try {
                    fileImage = new ImageView(new Image("images/" + suffix + ".png"));
                }catch (Exception e){
                    fileImage = new ImageView(new Image("images/other.png"));
                }
                fileImage.setFitWidth(78);
                fileImage.setFitHeight(78);
                Label fileName = new Label(f.getName());
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(fileImage, fileName);
                if (col == 6) {
                    row++;  //行
                    col = 0;
                }
                gridPane.add(vBox, col++, row);
            }
        }
    }

    private boolean alert(String fileName){
//        System.out.println(fileName);
        fileName = fileName.split("'\'")[fileName.split("'\'").length-1];
        for(File f:files){
//            System.out.println(f.getName());
            if(f.getName().equals(fileName)){
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("文件已存在");
                a.show();
                return false;
            }
        }
        return true;
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

        if(!alert(selectFile.getPath())){
            return;
        }

        //2. 在执行上传之前先建立底层的socket连接
        try {
            client = new Socket("localhost",8123);
            out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3. 上传之前先封装一个消息对象
        Message uploadMessage = new Message();
        uploadMessage.setFrom(user.getUsername());
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

            //上传文件，更新界面
            String suffix = selectFile.getName().split("\\.")[1];
            ImageView fileImage = null;
            try {
                fileImage = new ImageView(new Image("images/" + suffix + ".png"));
            }catch (Exception e){
                fileImage = new ImageView(new Image("images/other.png"));
            }
//            ImageView fileImage = new ImageView(new Image("images/"+suffix+".png"));
            fileImage.setFitWidth(78);
            fileImage.setFitHeight(78);
            Label fileName = new Label(selectFile.getName());
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(fileImage,fileName);
            if(col == 6){
                row ++;  //行
                col = 0;
            }
            gridPane.add(vBox,col++,row);

            //短连接，上传完即关闭流
            out.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
