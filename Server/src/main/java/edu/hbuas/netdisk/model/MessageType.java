package edu.hbuas.netdisk.model;

import java.io.Serializable;

//封装一个网盘消息类型
public enum MessageType implements Serializable {
    LOGIN,
    REGISTER,
    UPLOAD,
    DOWNLOAD,
    UPDATE,
    DELETE,
    RENAME,
    TRANSFER,
    DELETERECORD
}
