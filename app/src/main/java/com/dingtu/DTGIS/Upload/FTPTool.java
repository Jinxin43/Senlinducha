package com.dingtu.DTGIS.Upload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply; 

public class FTPTool {
	/** 
     * 服务器名. 
     */  
    private String hostName;  
  
    /** 
     * 端口号 
     */  
    private int serverPort;  
  
    /** 
     * 用户名. 
     */  
    private String userName;  
  
    /** 
     * 密码. 
     */  
    private String password;  
  
    /** 
     * FTP连接. 
     */  
    private FTPClient ftpClient;  
    
    public FTPTool()
    {
    	this.hostName = "192.168.1.107";  
        this.serverPort = 21;  
        this.userName = "FTPUser";  
        this.password = "FTPUser";  
        this.ftpClient = new FTPClient();  
    }
    
    public void uploadMultiFile(LinkedList<File> fileList, String remotePath,  
            UploadProgressListener uploadProgressListener) throws IOException {  
  
        // 上传之前初始化  
        this.uploadBeforeOperate(remotePath, uploadProgressListener);  
  
        boolean flag;  
  
        for (File singleFile : fileList) {  
            flag = uploadingSingle(singleFile, uploadProgressListener);  
            if (flag) {  
                uploadProgressListener.onUploadProgress(UploadLayer.FTP_UPLOAD_SUCCESS, 0,  
                        singleFile);  
            } else {  
                uploadProgressListener.onUploadProgress(UploadLayer.FTP_UPLOAD_FAIL, 0,  
                        singleFile);  
            }  
        }  
  
        // 上传完成之后关闭连接  
        this.uploadAfterOperate(uploadProgressListener);  
    }  
    
    private boolean uploadingSingle(File localFile,  
            UploadProgressListener listener) throws IOException {  
        boolean flag = true;  
        // 不带进度的方式  
        // 创建输入流  
//         InputStream inputStream = new FileInputStream(localFile);  
//         // 上传单个文件  
//         flag = ftpClient.storeFile(localFile.getName(), inputStream);  
//         // 关闭文件流  
//         inputStream.close();  
         
  
        // 带有进度的方式  
        BufferedInputStream buffIn = new BufferedInputStream(  
                new FileInputStream(localFile));  
        ProgressInputStream progressInput = new ProgressInputStream(buffIn,  
                listener, localFile);  
        flag = ftpClient.storeFile(localFile.getName(), progressInput);  
        buffIn.close();
        
  
        return flag;  
    }  
    
    private void uploadBeforeOperate(String remotePath,  
            UploadProgressListener listener) throws IOException {  
  
        // 打开FTP服务  
        try {  
            this.openConnect();  
            listener.onUploadProgress(UploadLayer.FTP_CONNECT_SUCCESSS, 0,  
                    null);  
        } catch (IOException e1) {  
            e1.printStackTrace();  
            listener.onUploadProgress(UploadLayer.FTP_CONNECT_FAIL, 0, null);  
            return;  
        }  
  
        // 设置模式  
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);  
        // FTP下创建文件夹  
        ftpClient.makeDirectory(remotePath);  
        // 改变FTP目录  
        ftpClient.changeWorkingDirectory(remotePath);  
        // 上传单个文件  
  
    } 
    
    private void uploadAfterOperate(UploadProgressListener listener)  
            throws IOException {  
        this.closeConnect();  
        listener.onUploadProgress(UploadLayer.FTP_DISCONNECT_SUCCESS, 0, null);  
    } 
    
    public void openConnect() throws IOException {  
        // 中文转码  
//        ftpClient.setControlEncoding("UTF-8");
        
//        ftpClient.setConnectTimeout(8000);
        ftpClient.setControlEncoding("GBK");
        
        int reply; // 服务器响应值  
        // 连接至服务器  
        ftpClient.connect(hostName, serverPort); 
        
        // 获取响应值  
        reply = ftpClient.getReplyCode();  
        if (!FTPReply.isPositiveCompletion(reply)) {  
            // 断开连接  
            ftpClient.disconnect();  
            throw new IOException("connect fail: " + reply);  
        }  
        // 登录到服务器  
        ftpClient.login(userName, password);  
        // 获取响应值  
        reply = ftpClient.getReplyCode();  
        if (!FTPReply.isPositiveCompletion(reply)) {  
            // 断开连接  
            ftpClient.disconnect();  
            throw new IOException("connect fail: " + reply);  
        } else {  
            // 获取登录信息  
            FTPClientConfig config = new FTPClientConfig(ftpClient  
                    .getSystemType().split(" ")[0]);  
            config.setServerLanguageCode("zh");  
            ftpClient.configure(config);  
            // 使用被动模式设为默认  
            ftpClient.enterLocalPassiveMode();  
            // 二进制文件支持  
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);  
        }  
    }
    
    public void closeConnect() throws IOException {  
        if (ftpClient != null) {  
            // 退出FTP  
            ftpClient.logout();  
            // 断开连接  
            ftpClient.disconnect();  
        }  
    }
    
    public interface UploadProgressListener {  
        public void onUploadProgress(String currentStep, long uploadSize, File file);  
    }  

}
