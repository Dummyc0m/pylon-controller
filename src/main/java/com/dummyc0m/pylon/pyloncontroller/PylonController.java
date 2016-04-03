package com.dummyc0m.pylon.pyloncontroller;

import com.dummyc0m.pylon.pyloncore.ConfigFile;
import com.dummyc0m.pylon.pyloncore.controller.ControllerConfig;

import java.io.File;

/**
 * Created by Dummyc0m on 3/23/16.
 */
public class PylonController {
    public static void main(String[] args) {
        new PylonController().launch(args);
    }

    private String identifier;
    private String name;
    private PylonSQLSource sqlSource;

    private void launch(String[] args) {
        ConfigFile<ControllerConfig> configFile = new ConfigFile<>(new File(System.getProperty("user.dir")), "pylon.json", ControllerConfig.class);
        ControllerConfig config = configFile.getConfig();
        this.identifier = config.getIdentifier();
        this.name = config.getName();
        Thread.currentThread().setName(name + " Main");
        sqlSource = new PylonSQLSource(config.getType(), config.getUrl(), config.getUsername(), config.getPassword());


    }
}
