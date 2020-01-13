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
     * ��������. 
     */  
    private String hostName;  
  
    /** 
     * �˿ں� 
     */  
    private int serverPort;  
  
    /** 
     * �û���. 
     */  
    private String userName;  
  
    /** 
     * ����. 
     */  
    private String password;  
  
    /** 
     * FTP����. 
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
  
        // �ϴ�֮ǰ��ʼ��  
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
  
        // �ϴ����֮��ر�����  
        this.uploadAfterOperate(uploadProgressListener);  
    }  
    
    private boolean uploadingSingle(File localFile,  
            UploadProgressListener listener) throws IOException {  
        boolean flag = true;  
        // �������ȵķ�ʽ  
        // ����������  
//         InputStream inputStream = new FileInputStream(localFile);  
//         // �ϴ������ļ�  
//         flag = ftpClient.storeFile(localFile.getName(), inputStream);  
//         // �ر��ļ���  
//         inputStream.close();  
         
  
        // ���н��ȵķ�ʽ  
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
  
        // ��FTP����  
        try {  
            this.openConnect();  
            listener.onUploadProgress(UploadLayer.FTP_CONNECT_SUCCESSS, 0,  
                    null);  
        } catch (IOException e1) {  
            e1.printStackTrace();  
            listener.onUploadProgress(UploadLayer.FTP_CONNECT_FAIL, 0, null);  
            return;  
        }  
  
        // ����ģʽ  
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);  
        // FTP�´����ļ���  
        ftpClient.makeDirectory(remotePath);  
        // �ı�FTPĿ¼  
        ftpClient.changeWorkingDirectory(remotePath);  
        // �ϴ������ļ�  
  
    } 
    
    private void uploadAfterOperate(UploadProgressListener listener)  
            throws IOException {  
        this.closeConnect();  
        listener.onUploadProgress(UploadLayer.FTP_DISCONNECT_SUCCESS, 0, null);  
    } 
    
    public void openConnect() throws IOException {  
        // ����ת��  
//        ftpClient.setControlEncoding("UTF-8");
        
//        ftpClient.setConnectTimeout(8000);
        ftpClient.setControlEncoding("GBK");
        
        int reply; // ��������Ӧֵ  
        // ������������  
        ftpClient.connect(hostName, serverPort); 
        
        // ��ȡ��Ӧֵ  
        reply = ftpClient.getReplyCode();  
        if (!FTPReply.isPositiveCompletion(reply)) {  
            // �Ͽ�����  
            ftpClient.disconnect();  
            throw new IOException("connect fail: " + reply);  
        }  
        // ��¼��������  
        ftpClient.login(userName, password);  
        // ��ȡ��Ӧֵ  
        reply = ftpClient.getReplyCode();  
        if (!FTPReply.isPositiveCompletion(reply)) {  
            // �Ͽ�����  
            ftpClient.disconnect();  
            throw new IOException("connect fail: " + reply);  
        } else {  
            // ��ȡ��¼��Ϣ  
            FTPClientConfig config = new FTPClientConfig(ftpClient  
                    .getSystemType().split(" ")[0]);  
            config.setServerLanguageCode("zh");  
            ftpClient.configure(config);  
            // ʹ�ñ���ģʽ��ΪĬ��  
            ftpClient.enterLocalPassiveMode();  
            // �������ļ�֧��  
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);  
        }  
    }
    
    public void closeConnect() throws IOException {  
        if (ftpClient != null) {  
            // �˳�FTP  
            ftpClient.logout();  
            // �Ͽ�����  
            ftpClient.disconnect();  
        }  
    }
    
    public interface UploadProgressListener {  
        public void onUploadProgress(String currentStep, long uploadSize, File file);  
    }  

}
