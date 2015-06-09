package com.gdc.nms.tester.client.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.gdc.nms.tester.common.Util;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SSHConnector {

    private static final String GLASSFISH_APPLICATIONS_PATH = "domains/domain1/applications/j2ee-apps/";

    /**
     * List of libraries, always be uploaded.
     */
    private static final List<String> IGNORE_LIBRARIES = Arrays.asList(new String[] { "common.jar", "server.jar",
            "model.jar" });

    /**
     * JSch instance
     */
    private JSch jSch;

    /**
     * Current session
     */
    private Session session;

    /**
     * Working dir, used as current directory in remote host
     */
    private String workingDirectory;

    public SSHConnector() {
        JSch.setConfig("StrictHostKeyChecking", "no");
        jSch = new JSch();
        workingDirectory = "/";
    }

    /**
     * Try open a SSH Connection
     * 
     * @param host
     * @param username
     * @param password
     * @param port
     * @throws JSchException
     */
    public void connect(String host, String username, String password, int port) throws JSchException {
        session = jSch.getSession(username, host, port);
        session.setPassword(password);
        session.connect();
    }

    @SuppressWarnings("unchecked")
    private <C extends Channel> C getChannel(Class<C> clazz) throws JSchException {
        String className = clazz.getSimpleName().toLowerCase();
        return (C) session.openChannel(className.substring(7));
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getWorkingDirectory() {
        if (!workingDirectory.endsWith("/") || workingDirectory.endsWith("\\")) {
            workingDirectory = workingDirectory.concat("/");
        }
        return workingDirectory;
    }

    /**
     * Complete working directory adding NMS path
     */
    @SuppressWarnings("unchecked")
    public void completeWorkingDirectory() throws SftpException {
        ChannelSftp channel = null;
        try {
            setWorkingDirectory(getWorkingDirectory().concat(GLASSFISH_APPLICATIONS_PATH));
            channel = getChannel(ChannelSftp.class);
            channel.setPty(true);
            channel.connect();
            Vector<LsEntry> ls = channel.ls(getWorkingDirectory());
            String nms = "";
            for (LsEntry entry : ls) {
                if (entry.getFilename().contains("nms")) {
                    nms = entry.getFilename();
                    break;
                }
            }
            if (nms.isEmpty()) {
                channel.disconnect();
                throw new SftpException(2, "nms directory not found.");
            }
            setWorkingDirectory(getWorkingDirectory().concat(nms).concat("/"));
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public File[] getFiles(File[] libraries) {
        if (libraries.length == 0) {
            return libraries;
        }
        String path = libraries[0].getParent().concat("/");
        ArrayList<File> files = new ArrayList<File>(Arrays.asList(libraries));
        ChannelSftp channel = null;
        try {
            channel = getChannel(ChannelSftp.class);
            channel.setPty(true);
            channel.connect();
            Vector<LsEntry> entries = channel.ls(getWorkingDirectory().concat("libraries"));
            for (LsEntry entry : entries) {
                File f = new File(path.concat(entry.getFilename()));
                if (!IGNORE_LIBRARIES.contains(entry.getFilename()) && files.contains(f)) {
                    files.remove(f);
                }
            }
        } catch (JSchException e) {
        } catch (SftpException e) {
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return files.toArray(new File[files.size()]);
    }

    public int uploadFiles(File[] files, String path) {
        int errors = 0;
        final CountDownLatch latch = new CountDownLatch(files.length);
        for (File file : files) {
            try {
                uploadFile(file, path, new SftpProgressMonitor() {
                    @Override
                    public void init(int op, String src, String dest, long max) {
                        // System.out.print("Uploading: " +
                        // dest.substring(dest.lastIndexOf('/') + 1));
                        // System.out.print(" [");
                    }

                    @Override
                    public void end() {
                        // System.out.print("] [OK]\n");
                        latch.countDown();
                    }

                    @Override
                    public boolean count(long count) {
                        // System.out.print(".");
                        return true;
                    }
                });
            } catch (Exception e) {
                latch.countDown();
                errors++;
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
        }

        return errors;
    }

    @SuppressWarnings("unchecked")
    public void uploadFile(File file, String path, SftpProgressMonitor monitor) throws SftpException,
            FileNotFoundException {
        path = (path == null) ? "" : path;
        ChannelSftp channel = null;
        try {
            channel = getChannel(ChannelSftp.class);
            channel.setPty(true);
            channel.connect();
            String dest = getWorkingDirectory();
            if (!path.isEmpty()) {
                boolean createPath = true;
                Vector<LsEntry> entries = channel.ls(getWorkingDirectory());
                for (LsEntry entry : entries) {

                    if (path.equals(entry.getFilename())) {
                        createPath = false;
                        break;
                    }
                }
                if (createPath) {
                    channel.mkdir(getWorkingDirectory().concat(path));
                }
                dest = dest.concat(path).concat("/");
            }
            channel.put(new FileInputStream(file), dest.concat(file.getName()), monitor, ChannelSftp.OVERWRITE);
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public boolean executeJar(String filename, String[] args) {
        ChannelExec channel = null;
        String cmdArgs = (args == null) ? "" : Arrays.toString(args).replaceAll("[\\[\\],]", "");
        StringBuffer command = new StringBuffer("java");

        command.append(' ')./*append("-Xdebug -agentlib:jdwp=transport=dt_socket,address=8888,server=y,suspend=n")
            .append(' ').*/append("-jar").append(' ').append('\"').append(getWorkingDirectory()).append(filename)
            .append('\"').append(' ').append(cmdArgs);
        ExecuteJar ex = new ExecuteJar(command.toString(), args[1], TimeUnit.MINUTES.toMillis(1));
        new Thread(ex).start();
        return ex.isRunning();
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    private class ExecuteJar implements Runnable {

        private long timeout;
        private String command;
        private CountDownLatch latch;
        private String mtpsAddress;
        private boolean running;

        public ExecuteJar(String command, String mtpsAddress, long timeout) {
            latch = new CountDownLatch(1);
            this.mtpsAddress = mtpsAddress;
            this.command = command;
            this.timeout = timeout;
            running = false;
        }

        @Override
        public void run() {
            long endTime = System.currentTimeMillis() + timeout;
            ChannelExec channel = null;
            try {
                channel = getChannel(ChannelExec.class);
                channel.setPty(true);
                 channel.setExtOutputStream(System.err);//lineas para imprimir en consola lo de jade
                 channel.setOutputStream(System.err);//lienas para imprimier en cola lo de jade
                channel.setCommand(command);
                channel.connect();
                while (!channel.isClosed()) {
                    if (Util.isBusyPort(mtpsAddress, 7778)) {
                        running = true;
                        System.out.println("->"+running);
                        break;
                    } else if (System.currentTimeMillis() >= endTime) {
                    	System.out.println("Timeout");
                        break;
                    }
                    Thread.sleep(500);
                }
            } catch (JSchException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }

        public boolean isRunning() {
            try {
            	System.out.println("Esperando");
                latch.await();
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
            System.out.println("<->"+running);
            return running;
        }
    }

}
