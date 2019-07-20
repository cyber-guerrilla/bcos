package com.cyber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created on 2019/1/24-17:04
 * abstract 设置其不能被实例化
 * @author lihb/l786112323@gmail.com
 */
public abstract class Bootstrap {
    private static String[] info = new String[]{"appHome","java.library.path"};
    static{
        System.setProperty("appHome",System.getProperty("user.dir"));
        System.setProperty("appName",new File(System.getProperty("user.dir")).getName());

    }
    protected static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    static{ // 使得日志输出时，已经加载了logger
        for(String i:info)
            logger.debug("{}={}",i,System.getProperty(i));
    }
}
