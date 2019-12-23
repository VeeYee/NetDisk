package edu.hbuas.netdisk.control;

import java.util.HashMap;
import java.util.Map;

public class ControlCollections {

    //控制器集合，将所有的控制器放入到同一个空间，用于控制器间传递数据
    public static Map<Class,Object> controls = new HashMap<Class, Object>();
}
