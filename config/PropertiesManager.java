package com.config;

import com.pega.util.DataUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesManager {
    Properties properties;

    public PropertiesManager(){
        this.properties = setPropertiesFromGlobalSettingsFile();
    }

    public String setValueFromProperty(String propertyName) throws IOException {
        if(!properties.getProperty(propertyName).isEmpty()){
            return properties.getProperty(propertyName);
        } else{
            return System.getProperty(propertyName);
        }
    }

    private Properties setPropertiesFromGlobalSettingsFile(){
        try {
            File globalSettingsFile = DataUtil.getGlobalSettingsFile();
            Properties properties = new Properties();
            properties.load(new FileInputStream(globalSettingsFile));
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
