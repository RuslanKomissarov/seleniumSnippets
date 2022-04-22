package com.config;

import com.pega.Configuration;
import com.pega.util.DataUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {

    Configuration config;

    public ConfigManager(Configuration config){
        this.config = config;
    }

    public String setCredential(String propertyName){
        if(!config.getCredential(propertyName).isEmpty()){
            return config.getCredential(propertyName);
        } else {
            return System.getProperty(propertyName);
        }
    }
}
