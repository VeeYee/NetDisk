package edu.hbuas.netdisk.model;

import java.io.Serializable;

public class User implements Serializable {

  private String username;
  private String password;
  private String sex;
  private String age;
  private String tel;
  private String email;
  private String images;

  public User() {
  }

  public User(String username, String password, String sex, String age, String tel, String email, String images) {
    this.username = username;
    this.password = password;
    this.sex = sex;
    this.age = age;
    this.tel = tel;
    this.email = email;
    this.images = images;
  }

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }


  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }


  public String getTel() {
    return tel;
  }

  public void setTel(String tel) {
    this.tel = tel;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getImages() {
    return images;
  }

  public void setImages(String images) {
    this.images = images;
  }
}
