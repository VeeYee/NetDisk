package edu.hbuas.netdisk.control;

import edu.hbuas.netdisk.model.Message;
import edu.hbuas.netdisk.model.MessageType;
import edu.hbuas.netdisk.model.Transfer;
import edu.hbuas.netdisk.model.User;
import edu.hbuas.netdisk.utils.DateUtil;
import edu.hbuas.netdisk.utils.FileSizeUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;

/**
 * 传输列表的控制类，可以获取所有的传输记录
 * 支持删除、以及打开下载到本地的文件
 */
public class TransferControl{

    private AnchorPane scrollContent;
    private User user;

    //存放所有传输记录的集合
    private LinkedList<Transfer> recordList = new LinkedList<>();

    public TransferControl() {
        MainControl mainControl = (MainControl) ControlCollections.controls.get(MainControl.class);
        this.scrollContent = mainControl.getScrollContent();
        this.user = mainControl.getUser();
    }

    //加载所有传输记录
    public void loadRecord(){
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
                HBox h = new HBox(22);
                h.setAlignment(Pos.CENTER);
                h.setPrefSize(397,66);
                DateUtil d = new DateUtil();
                Label time = new Label(d.getDate(t.getTime()));
                time.setPrefWidth(90);
                time.setStyle("-fx-text-fill: #7c7a7a");
                Label type = new Label(t.getOperating()+"完成");
                type.setStyle("-fx-text-fill: #7c7a7a");

                //打开文件按钮
                ImageView openPic = new ImageView(new Image("images/open.png"));
                Button openBtn = new Button("",openPic);
                openBtn.setStyle("-fx-background-color: white");
                openBtn.setPrefSize(30,30);
                openBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        openFile(t.getFilepath());
                    }
                });

                //删除按钮
                ImageView deletePic = new ImageView(new Image("images/delete.png"));
                Button deleteBtn = new Button("",deletePic);
                deleteBtn.setStyle("-fx-background-color: white");
                deleteBtn.setPrefSize(30,30);
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

    //打开本地文件
    private void openFile(String filePath){
        System.out.println("点击了打开文件的方法");
        String path=filePath.replace("\\", "\\\\");
        File file=new File(path);
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        if (file.exists()) {
            @SuppressWarnings("unused")
            Process p;
            try {
                p = Runtime.getRuntime().exec("cmd /C " + path);
            } catch (Exception e) {
                a.setTitle("失败");
                a.setContentText("无法打开文件");
                a.show();
            }
        }else{
            a.setTitle("失败");
            a.setContentText("文件不存在或已被移动");
            a.show();
        }
    }

    //删除一条传输记录
    private void deleteRecord(long id){
        try {
            Socket client = new Socket("localhost",8123);
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            Message deleteRecordMessage = new Message(user, MessageType.DELETERECORD,"",id);
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
}
