package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.Transfer;
import edu.hbuas.netdisk.model.User;
import edu.hbuas.netdisk.utils.DateFormatUtil;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    private Button myNetDiskBtn;
    @FXML
    private Button transferBtn;
    @FXML
    private Button uploadBtn;
    @FXML
    private Button newBtn;

    //文件搜索框
    @FXML
    private TextField searchText;

    //登陆进来的用户
    private User user;

    //存放所有文件的集合
    private LinkedList<File> fileList = new LinkedList<>();
    //存放所有传输记录的集合
    private LinkedList<Transfer> recordList = new LinkedList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        scrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
        //获取登录的用户
        LoginControl loginControl = (LoginControl) ControlCollections.controls.get(LoginControl.class);
        this.user = loginControl.getUser();
        images.setImage(new Image(user.getImages()));
        username.setText(user.getUsername());

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
            fileList = updateResult.getFileList();
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

    //更新主界面的方法，用于登录时初始化界面以及上传文件时更新界面
    public void updateUI(LinkedList<File> fileList){
        double x = 0;
        double y = 0;
        scrollContent.getChildren().clear();
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
                TextField fileName = new TextField(f.getName());
                fileName.setStyle("-fx-background-color: white; -fx-alignment: center");
                fileName.setEditable(false);
                //垂直布局
                VBox vBox = new VBox();
                vBox.setPrefSize(110,120);
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(fileImage, fileName);
                vBox.setLayoutX(x);
                vBox.setLayoutY(y);
                if(x < 660){
                    x += 110;
                }else{
                    x=0;
                    y += 120;
                }
                scrollContent.getChildren().add(vBox);
                click(vBox);  //点击事件
            }
        }
    }

    //更新传输列表的方法，用于初始化时以及上传或下载文件时更新传输列表
    public void updateRecord(LinkedList<Transfer> recordList) {
        double y = 0;
        scrollContent.getChildren().clear();
        if(recordList!=null && recordList.size()>0){
            for(Transfer t : recordList){
                //文件图片
                String suffix = t.getFilename().split("\\.")[t.getFilename().split("\\.").length-1];
                ImageView fileImage = null;
                try {
                    fileImage = new ImageView(new Image("images/" + suffix + ".png"));
                }catch (Exception e){
                    fileImage = new ImageView(new Image("images/other.png"));
                }
                fileImage.setFitWidth(61);
                fileImage.setFitHeight(65);
                //文件名以及文件大小
                VBox v = new VBox(5);
                v.setAlignment(Pos.TOP_LEFT);
                v.setPadding(new Insets(7,0,5,10));
                v.setPrefSize(336,66);
                Label fileName = new Label(t.getFilename());
                FileSizeUtil f = new FileSizeUtil();
                Label fileLength = new Label(f.formatFileSize(t.getFileLength()));  //转换文件大小
                fileLength.setStyle("-fx-text-fill: #7c7a7a");
                v.getChildren().addAll(fileName,fileLength);

                //时间、操作类型以及按钮
                HBox h = new HBox(30);
                h.setAlignment(Pos.CENTER);
                h.setPrefSize(397,66);
                DateFormatUtil d = new DateFormatUtil();
                Label time = new Label(d.dateFormatUtil(t.getTime()));
                time.setPrefWidth(100);
                time.setStyle("-fx-text-fill: #7c7a7a");
                Label type = new Label(t.getOperating()+"完成");
                type.setStyle("-fx-text-fill: #7c7a7a");
                Button openBtn = new Button("打开");
                openBtn.setStyle("-fx-background-color: #e7e7e7");
                Button deleteBtn = new Button("删除");
                deleteBtn.setStyle("-fx-background-color: #e7e7e7");
                deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        deleteRecord(t.getId());
                    }
                });
                h.getChildren().addAll(time,type,openBtn,deleteBtn);
                //存放每条完整的传输记录
                HBox hBox = new HBox();
                hBox.setPrefSize(788,66);
                hBox.getChildren().addAll(fileImage,v,h);
                hBox.setLayoutY(y);
                y += 66;
                scrollContent.getChildren().add(hBox);
            }
        }
    }

    //删除一条传输记录
    private void deleteRecord(long id){
        try {
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            Message deleteRecordMessage = new Message(user,MessageType.DELETERECORD,"",id);
            out.writeObject(deleteRecordMessage);
            for (int i=0; i<recordList.size(); i++){
                if(recordList.get(i).getId() == id){
                    recordList.remove(recordList.get(i));
                }
            }
            updateRecord(recordList);
            Message result = (Message)in.readObject();
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            if(result.getFileName().equals("true")){
                a.setContentText("记录删除成功");
            }else {
                a.setContentText("删除失败，请检查网络连接！");
            }
            a.show();
            out.flush();
            out.close();
            in.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打开我的网盘
    @FXML
    public void myNetDisk(){
        clearBtnBackground();
        myNetDiskBtn.setStyle("-fx-background-color: rgba(95,203,255,0.74)");
        updateUI(fileList);
    }

    //打开传输列表
    @FXML
    public void transferList(){
        clearBtnBackground();
        transferBtn.setStyle("-fx-background-color: rgba(95,203,255,0.74)");
        //初始化时读取所有的传输记录
        try {
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            Message transferMessage = new Message(user,MessageType.TRANSFER,"",0);
            out.writeObject(transferMessage);
            out.flush();

            Message transferResult = (Message)in.readObject();
            recordList = transferResult.getRecordList();
            updateRecord(recordList);
            //关闭连接
            in.close();
            out.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //上传文件的方法
    @FXML
    public void uploadFile(){
        clearBtnBackground();
        uploadBtn.setStyle("-fx-background-color: rgba(95,203,255,0.74)");
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
            Message uploadMessage = new Message(user, MessageType.UPLOAD, selectFile.getName(), selectFile.length());
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

    /**
     * 每个文件的点击事件，点击获取文件信息，弹出菜单，进行其他操作
     */
    public void click(VBox vBox){
        vBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //获取点击的文件名
                String fileName = ((TextField)vBox.getChildren().get(1)).getText();
                if(event.getButton() == MouseButton.SECONDARY && event.getClickCount()==1){
                    //右键单击，弹出菜单
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
                            downloadFile(fileName);
                        }
                    });
                    //点击删除，调用删除文件的方法
                    item2.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            deleteFile(fileName);
                        }
                    });
                    //点击重命名，调用重命名文件的方法
                    item3.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            TextField fileName = ((TextField)vBox.getChildren().get(1));
                            renameFile(fileName);
                        }
                    });
                }
                if(event.getButton()==MouseButton.PRIMARY && event.getClickCount()==2){
                    //双击打开文件
//                    openFile(fileName);
                }
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
            File saveDir = dc.showDialog(scrollPane.getScene().getWindow());
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

    //删除文件的方法
    public void deleteFile(String fileName){
        try {
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            Message deleteMessage = new Message(user,MessageType.DELETE,fileName,0);
            out.writeObject(deleteMessage);
            out.flush();
            //删除成功后更新UI界面
            for (int i=0; i<fileList.size(); i++){
                if(fileList.get(i).getName().contains(fileName)){
                    fileList.remove(fileList.get(i));
                }
            }
            updateUI(fileList);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("文件删除成功");
            a.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //重命名文件的方法
    public void renameFile(TextField editName){
        editName.setEditable(true);
        String fullName = editName.getText();   //文件全名
        String suffix = fullName.substring(fullName.lastIndexOf("."));  //后缀
        String before = fullName.substring(0,fullName.lastIndexOf("."));  //文件名
        //只显示文件名，不允许修改后缀
        editName.setText(before);
        editName.requestFocus();
        editName.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //按下回车键确定修改
                if(event.getCode() == KeyCode.ENTER) {
                    String after = editName.getText();
                    if (after.equals("")) {
                        Alert a = new Alert(Alert.AlertType.INFORMATION);
                        a.setContentText("文件名不能为空");
                        a.show();
                    } else {
                        editName.setEditable(false);  //修改后不可编辑
                        editName.setText(after + suffix);
                        try {
                            Socket client = new Socket("localhost", 8123);
                            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                            String result = fullName + "   " + after + suffix;
                            Message renameMessage = new Message(user, MessageType.RENAME, result, 0);
                            out.writeObject(renameMessage);
                            out.flush();
                            Alert a = new Alert(Alert.AlertType.INFORMATION);
                            a.setContentText("文件重命名成功");
                            a.show();
                            //关闭流
                            out.close();
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //更新文件列表，搜索时能搜到更新后的文件
                        for (int i=0; i<fileList.size(); i++){
                            if(fileList.get(i).getName().equals(fullName)){
                                fileList.remove(fileList.get(i));
                                fileList.add(new File(after+suffix));
                                break;
                            }
                        }
                    }
                }
            }
        });
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
            updateUI(list);
        }else {
            updateUI(fileList);
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
