package edu.hbuas.netdisk.model;

import java.io.Serializable;

public class Transfer implements Serializable {

  private long id;
  private String username;
  private String filename;
  private long fileLength;
  private String filepath;
  private String time;
  private String operating;

  public Transfer() {
  }

  public Transfer(long id, String username, String filename, long fileLength, String filepath, String time, String operating) {
    this.id = id;
    this.username = username;
    this.filename = filename;
    this.fileLength = fileLength;
    this.filepath = filepath;
    this.time = time;
    this.operating = operating;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }


  public long getFileLength() {
    return fileLength;
  }

  public void setFileLength(long fileLength) {
    this.fileLength = fileLength;
  }


  public String getFilepath() {
    return filepath;
  }

  public void setFilepath(String filepath) {
    this.filepath = filepath;
  }


  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }


  public String getOperating() {
    return operating;
  }

  public void setOperating(String operating) {
    this.operating = operating;
  }

}
