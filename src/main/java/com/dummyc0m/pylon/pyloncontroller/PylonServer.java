package com.dummyc0m.pylon.pyloncontroller;

import com.google.common.collect.ImmutableList;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Dummyc0m on 3/23/16.
 */
public class PylonServer {
    private String identifier;
    private String[] startCommand;
    private File workingDirectory;
    private int logKeepLength;
    private Process process;
    private OutputStream outputStream;
    private InputStream inputStream;
    private ArrayBlockingQueue<String> logBuffer;
    private volatile boolean isAlive;

    private volatile long totalMemory;
    private volatile long freeMemory;
    private volatile File logFile;
    private volatile PipedInputStream listenIn;
    private volatile PipedOutputStream output;

    public PylonServer(String identifier, File workingDirectory, String[] startCommand, int logKeepLength) {
        this.identifier = identifier;
        this.workingDirectory = workingDirectory;
        this.startCommand = startCommand;
        this.logKeepLength = logKeepLength;
    }

    public void start() throws IOException {
        logBuffer = new ArrayBlockingQueue<>(logKeepLength);
        ProcessBuilder processBuilder = new ProcessBuilder();
        process = processBuilder.directory(workingDirectory)
                .command(startCommand)
                .start();
        outputStream = process.getOutputStream();
        inputStream = process.getInputStream();
        output = new PipedOutputStream();
        listenIn = new PipedInputStream(output);
        isAlive = true;
        write("pylon.getLogFile");
        Thread ioThread = new Thread(identifier + " In") {
            @Override
            public void run() {
                Charset utf8 = Charset.forName("UTF-8");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while (isAlive) {
                    try {
                        if(bufferedReader.ready()) {
                            line = bufferedReader.readLine();
                            if(line.startsWith("pylon.log-")) {
                                line = line.substring(10);
                                if(!logBuffer.offer(line)) {
                                    logBuffer.poll();
                                    logBuffer.offer(line);
                                }
                                byte[] bytes = (line + "\n").getBytes(utf8);
                                if(listenIn.available() + bytes.length < 900) {
                                    output.write(bytes);
                                    output.flush();
                                }
                            } else if(line.startsWith("pylon.totalMemory-")) {
                                line = line.substring(18);
                                totalMemory = Long.parseLong(line);
                            } else if(line.startsWith("pylon.freeMemory-")) {
                                line = line.substring(17);
                                freeMemory = Long.parseLong(line);
                            } else if(line.equals("pylon.stop")) {
                                isAlive = false;
                            } else if(line.startsWith("pylon.logFile-")) {
                                line = line.substring(14);
                                logFile = new File(line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(20L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        ioThread.start();
    }

    public void write(String s) throws IOException {
        outputStream.write((s + "\n").getBytes(Charset.forName("UTF-8")));
        outputStream.flush();
    }


    public void destroyForcibly() {
        process.destroyForcibly();
        isAlive = false;
    }

    public void kill() throws IOException {
        write("pylon.kill");
    }

    public void shutdownGracefully() throws IOException {
        write("stop");
    }

    public List<String> getLogs() {
        return ImmutableList.copyOf(logBuffer.iterator());
    }

    public Iterator<String> getLog() {
        return logBuffer.iterator();
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public File getLogFile() {
        return logFile;
    }

    public synchronized InputStream getInputStream() throws IOException {
        return listenIn;
    }
}
