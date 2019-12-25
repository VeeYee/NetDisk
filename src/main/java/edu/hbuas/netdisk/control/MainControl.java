package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
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
        Message updateMessage = new Message(user,MessageType.UPDATE,null,0);
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

    //更新界面的方法，用于登录时初始化界面以及上传文件更新界面
    public void updateUI(Set<File> fileList){
        if(fileList!=null && fileList.size()>0) {
            for (File f : fileList) {
                String suffix = f.getName().split("\\.")[f.getName().split("\\.").length-1];
                //存放文件图片
                ImageView fileImage = null;
                try {
                    fileImage = new ImageView(new Image("images/" + suffix + ".png"));
                }catch (Exception e){
                    fileImage = new ImageView(new Image("images/other.png"));
                }
                fileImage.setFitWidth(78);
                fileImage.setFitHeight(78);
                //存放文件名
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

    //上传文件的方法
    @FXML
    public void uploadFile(){
        //建立短连接，上传或下载时才连接
        Socket client = null;
        ObjectOutputStream out = null;

        //1. 弹出文件选择器，让用户选择文件
        FileChooser fc = new FileChooser();
        File selectFile = fc.showOpenDialog(uploadBtn.getScene().getWindow());

        //2. 在执行上传之前先建立底层的socket连接
        try {
            client = new Socket("localhost",8123);
            out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //3. 上传之前先封装一个消息对象
        Message uploadMessage = new Message(user,MessageType.UPLOAD,selectFile.getName(),selectFile.length());
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
            while((length = fileIn.read(bs))!=-1) {
                out.write(bs,0,length);
                out.flush();
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("文件上传成功");
            a.show();
            //更新界面，新增一个文件
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

    /**
     * 每个文件的点击事件，点击获取文件信息，弹出菜单，进行其他操作
     */
    public void click(VBox vBox){
        vBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //右键单击，弹出菜单
                if(event.getButton() == MouseButton.SECONDARY && event.getClickCount()==1){
                    ContextMenu rightMenu = new ContextMenu();
                    MenuItem item1 = new MenuItem("下载");
                    MenuItem item2 = new MenuItem("删除");
                    MenuItem item3 = new MenuItem("重命名");
                    rightMenu.getItems().addAll(item1,item2,item3);
                    rightMenu.show(vBox, Side.BOTTOM,50,-50);
                    //点击下载，调用下载文件的方法
                    item1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String fileName = ((Label)vBox.getChildren().get(1)).getText();
                            downloadFile(fileName);
                        }
                    });

                }
//                //获取需要删除的文件名
//                String fileName = ((Label)vBox.getChildren().get(1)).getText();
//                try {
//                    Socket client = new Socket("localhost",8123);
//                    ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
//                    //向服务器发送下载文件的消息
//                    Message downloadMessage = new Message();
//                    downloadMessage.setFrom(user);
//                    downloadMessage.setFileName(fileName);
//                    downloadMessage.setType(MessageType.DOWNLOAD);
//                    out.writeObject(downloadMessage);
//                    out.flush();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                vBox.setStyle("-fx-background-color: #8cd3ec");
//                vBox.setFocusTraversable(true);
            }
        });
    }


    //下载文件的方法
    public void downloadFile(String fileName){
        //在执行下载之前先建立底层的socket连接
        try {
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            //向服务器发送下载消息
            Message downloadMessage = new Message(user,MessageType.DOWNLOAD,fileName,0);
            out.writeObject(downloadMessage);
            out.flush();
            //用户选择文件下载路径 目录选择器
            DirectoryChooser dc = new DirectoryChooser();
            File saveDir = dc.showDialog(uploadBtn.getScene().getWindow());
            if(!saveDir.exists()) saveDir.mkdir();
            //通过通道流读取文件信息，再通过字节流从内存写向磁盘
            FileOutputStream fileOut = new FileOutputStream(saveDir + "/" + fileName);
            byte[] bs = new byte[1024];
            int length = -1;
            while ((length = in.read(bs)) != -1) {
                fileOut.write(bs,0,length);
                fileOut.flush();
            }
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("文件下载成功");
            a.show();
            //关闭流
            fileOut.close();
            out.close();
            in.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
