# NetDisk
网盘项目

1. 修改加载数据库配置文件的路径为绝对路径

  （ConnectionPool.java里面  读取配置文件的地方）
  
    properties.load(new FileInputStream("D:\\Zidea\\NetDisk\\Server\\src\\main\\resources\\datasource.properties"));

2. 手动在D盘创建空文件夹:  NetDiskFile


3. 服务端入口：Server.java        客户端入口：Login.java


## 已实现的功能
1. 登录，注册（注册时请选择头像，不选会有一点问题...）
2. 点击我的网盘，显示用户上传到网盘的所有文件。
3. 右键点击每个文件，弹出菜单，可以对文件进行下载、删除、重命名操作。
4. 文件下载，选择一个本地文件夹，即可下载。（本地文件夹若已存在该文件，则会提示下载成功，并且覆盖原来已存在的此文件）
5. 文件删除，服务器将从磁盘删除此文件，删除后将不会在网盘中显示，已下载到本地的不会被删除。
6. 文件重命名，输入框只显示文件名，只可修改文件名，不允许修改后缀。
7. 点击传输列表，显示用户上传和下载的所有记录。
8. 传输记录的时间在当天将显示详细上传时间，昨天上传将显示昨天，本周内将显示星期几，本周之外将显示年月日。
9. 传输记录支持删除。
10. 通过传输记录可以打开下载到本地的文件，也可以通过获取用户上传文件的路径，打开用户已上传的文件。若文件移动，打开失败，提示"文件不存在或已被移动"。
11. 点击文件上传，从本地选择一个文件上传并显示在网盘中。
12. 搜索功能，从我的网盘中的所有文件中搜索指定文件，支持回车搜索，或者点击按钮搜索。
13. 另外界面支持滚动！！

## 项目实现效果
![image](https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%9B%BE%E7%89%87&hs=2&pn=0&spn=0&di=175560&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cl=2&lm=-1&cs=2534506313%2C1688529724&os=1097436471%2C408122739&simid=3354786982%2C133358663&adpicid=0&lpn=0&ln=30&fr=ala&fm=&sme=&cg=&bdtype=0&oriquery=%E5%9B%BE%E7%89%87&objurl=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fp7rtwg_z%26e3Bkwthj_z%26e3Bv54AzdH3Ftrw1AzdH3Fwn_89_0c_a8naaaaa8m98bm8d8nmm0cmbanmbm_3r2_z%26e3Bip4s&gsm=1&islist=&querylist=)
