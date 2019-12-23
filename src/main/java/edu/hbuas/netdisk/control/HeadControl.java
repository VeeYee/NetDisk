package edu.hbuas.netdisk.control;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HeadControl implements Initializable {
    @FXML
    private GridPane pane;

    @FXML
    private ImageView head1;
    @FXML
    private ImageView head2;
    @FXML
    private ImageView head3;
    @FXML
    private ImageView head4;

    @FXML
    private ImageView head5;
    @FXML
    private ImageView head6;
    @FXML
    private ImageView head7;
    @FXML
    private ImageView head8;

    @FXML
    private ImageView head9;
    @FXML
    private ImageView head10;
    @FXML
    private ImageView head11;
    @FXML
    private ImageView head12;

    @FXML
    private ImageView head13;
    @FXML
    private ImageView head14;
    @FXML
    private ImageView head15;
    @FXML
    private ImageView head16;

    //选中的头像  默认头像
    private Image SelectImage = new Image("head/default.jpg");
    private String url = "head/default.jpg";  //图片的路径

    public Image getSelectImage() {
        return SelectImage;
    }

    public String getUrl() {
        return url;
    }

    private void put(){
        ControlCollections.controls.put(getClass(),this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        put();

        selectImage(head1);
        selectImage(head2);
        selectImage(head3);
        selectImage(head4);
        selectImage(head5);
        selectImage(head6);
        selectImage(head7);
        selectImage(head8);
        selectImage(head9);
        selectImage(head10);
        selectImage(head11);
        selectImage(head12);
        selectImage(head13);
        selectImage(head14);
        selectImage(head15);
        selectImage(head16);

    }

    //图片事件的处理
    private void selectImage(ImageView head){
        head.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                SelectImage = head.getImage();
                url = (String) head.getUserData();
                pane.getScene().getWindow().hide();
            }
        });
    }
}
