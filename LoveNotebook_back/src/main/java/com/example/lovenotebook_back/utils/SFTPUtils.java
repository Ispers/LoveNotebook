package com.example.lovenotebook_back.utils;

import com.jcraft.jsch.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Vector;

public class SFTPUtils {


    private Session sshSession;

    private ChannelSftp sftp;

    /**
     * 连接sftp服务器
     *
     * @param host
     * @param port
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public ChannelSftp connect(String host, int port, String username, String password) throws Exception {
        JSch jsch = new JSch();
        sshSession = jsch.getSession(username, host, port);

        sshSession.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        sshSession.connect();

        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;

        return sftp;
    }

    /**
     * 连接sftp服务器
     *
     * @param host
     * @param port
     * @param username
     * @param privateKey
     * @param passphrase
     * @return
     * @throws Exception
     */
    public ChannelSftp connect(String host, int port, String username, String privateKey, String passphrase) throws Exception {
        JSch jsch = new JSch();

        //设置密钥和密码
        if (!StringUtils.isEmpty(privateKey)) {
            if (!StringUtils.isEmpty(passphrase)) {
                //设置带口令的密钥
                jsch.addIdentity(privateKey, passphrase);
            } else {
                //设置不带口令的密钥
                jsch.addIdentity(privateKey);
            }
        }
        sshSession = jsch.getSession(username, host, port);

        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        sshSession.connect();

        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;

        return sftp;
    }

    public void portForwardingL(int lport, String rhost, int rport) throws Exception {
        int assinged_port = sshSession.setPortForwardingL(lport, rhost, rport);
        System.out.println("localhost:" + assinged_port + " -> " + rhost + ":" + rport);
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (sftp != null) sftp.disconnect();
        if (sshSession != null) sshSession.disconnect();
    }

    /**
     * 上传文件
     *
     * @param directory  上传的目录
     * @param uploadFile 要上传的文件
     */
    public void upload(String directory, String uploadFile) throws Exception {
        sftp.cd(directory);
        File file = new File(uploadFile);
        sftp.put(new FileInputStream(file), file.getName());
    }

    public void upload(String directory, File file) throws Exception {
        sftp.cd(directory);
        sftp.put(new FileInputStream(file), file.getName());
        System.out.println("upload file " + file.getAbsolutePath() + " to host " + sshSession.getHost());
    }

    /**
     * 利用流上传文件 fileName
     *
     * @author sun0316
     * @date 2023/5/17 14:31
     */
    public void uploadFileInputStream(MultipartFile file, String directory, String fileName) throws Exception {
        sftp.cd(directory);
        sftp.put(file.getInputStream(), fileName);
    }

    public void uploadDir(File src, String dst) throws Exception {
        if (!exist(dst)) {
            sftp.mkdir(dst);
        }
        if (src.isFile()) {
            upload(dst, src);
        } else {
            for (File file : src.listFiles()) {
                if (file.isDirectory()) {
                    uploadDir(file, dst + "/" + file.getName());
                }
                upload(dst, file);
            }
        }
    }

    /**
     * 目录是否查找
     *
     * @param path
     * @return
     * @throws SftpException
     */
    public boolean exist(String path) throws SftpException {
        String pwd = sftp.pwd();
        try {
            sftp.cd(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            } else {
                throw e;
            }
        } finally {
            sftp.cd(pwd);
        }

        return true;
    }

    /**
     * 下载文件
     *
     * @param directory
     * @param downloadFile
     * @param saveFile
     * @throws Exception
     */
    public void download(String directory, String downloadFile, String saveFile) throws Exception {
        sftp.cd(directory);
        File file = new File(saveFile);
        sftp.get(downloadFile, new FileOutputStream(file));
    }

    /**
     * 下载文件
     *
     * @param directory
     * @param downloadFile
     * @param saveFile
     * @throws Exception
     */
    public void download(String directory, String downloadFile, File saveFile) throws Exception {
        sftp.cd(directory);
        sftp.get(downloadFile, new FileOutputStream(saveFile));
        System.out.println("download file " + directory + "/" + downloadFile + " from host " + sshSession.getHost());
    }

    /**
     * 下载文件
     *
     * @param src
     * @param dst
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void downloadDir(String src, File dst) throws Exception {
        try {
            sftp.cd(src);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        dst.mkdirs();

        Vector<ChannelSftp.LsEntry> files = sftp.ls(src);
        for (ChannelSftp.LsEntry lsEntry : files) {
            if (lsEntry.getFilename().equals(".") || lsEntry.getFilename().equals("..")) {
                continue;
            }
            if (lsEntry.getLongname().startsWith("d")) {
                downloadDir(src + "/" + lsEntry.getFilename(), new File(dst, lsEntry.getFilename()));
            } else {
                download(src, lsEntry.getFilename(), new File(dst, lsEntry.getFilename()));
            }

        }
    }

    /**
     * 删除文件
     *
     * @param directory
     * @param deleteFile
     * @throws SftpException
     */
    public void delete(String directory, String deleteFile) throws SftpException {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }

    /**
     * 列出目录下的文件
     *
     * @param directory
     * @return
     * @throws SftpException
     */
    public Vector listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    public Session getSshSession() {
        return sshSession;
    }

    public ChannelSftp getSftp() {
        return sftp;
    }
}
