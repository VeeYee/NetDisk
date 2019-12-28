package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * 文件控制类，点击每个VBox，然后对文件进行相关操作
 */
public class FileControl {

    private AnchorPane scrollContent;
    private User user;
    private LinkedList<File> fileList;

    public FileControl(){
        MainControl mainControl = (MainControl) ControlCollections.controls.get(MainControl.class);
        this.scrollContent = mainControl.getScrollContent();
        this.user = mainControl.getUser();
        this.fileList = mainControl.getFileList();
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
                if(event.getButton() == MouseButton.PRIMARY && event.getClickCount()==2){
                    //双击打开文件
//                    openFile(fileName);
                }
            }
        });
    }


    /**
     * 以下是右键菜单的下载、删除、重命名功能
     */

    //下载文件的方法
    public void downloadFile(String fileName){
        //在执行下载之前先建立底层的socket连接
        try {
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            //用户选择文件下载路径 目录选择器
            DirectoryChooser dc = new DirectoryChooser();
            File saveDir = dc.showDialog(scrollContent.getScene().getWindow());
            if(!saveDir.exists()) saveDir.mkdir();
            //文件的下载路径
            String filePath = saveDir + "\\" + fileName;
            //向服务器发送下载消息
            Message downloadMessage = new Message(user, MessageType.DOWNLOAD,filePath+"   "+fileName,0);
            out.writeObject(downloadMessage);
            out.flush();
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
            System.out.println("之前"+fileList.size());
            for (int i=0; i<fileList.size(); i++){
                if(fileList.get(i).getName().equals(fileName)){
                    fileList.remove(fileList.get(i));
                    break;
                }
            }
            System.out.println("后面"+fileList.size());
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
}
