package com.dummyc0m.pylon.pyloncontroller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by Dummyc0m on 3/24/16.
 */
public class PylonServerTest {
    private PylonServer instance;

    @Before
    public void setUp() throws Exception {
        File pylonDir = new File(System.getProperty("user.dir"));
        File pylonFile = new File(pylonDir, "pylonmc.jar");
        String[] commandArgs = new String[] {
                System.getProperty("java.home") + "/bin/java",
                "-Xmx1G",
                "-jar",
                "" + pylonFile.getAbsolutePath() + "",
                "minecraft_server.1.9.jar"
        };
//        String[] commandArgs = new String[] {System.getProperty("java.home") + "/bin/java"};
        System.out.println();

        instance = new PylonServer("ServerTest", new File(System.getProperty("user.dir") + File.separator + "run"), commandArgs, 20);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void start() throws Exception {
        instance.start();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(instance.getInputStream()));
        instance.write("stop");
        while (instance.isAlive()) {
            if (bReader.ready()) {
                String line = bReader.readLine();
                System.out.println(line);
//                System.out.println(instance.getFreeMemory());
//                System.out.println(instance.getTotalMemory());
            }
            Thread.sleep(75L);
        }
        while (bReader.ready()) {
            String line = bReader.readLine();
            System.out.println(line);
        }
    }
}