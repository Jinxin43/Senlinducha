package com.dingtu.Funtion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.dingtu.senlinducha.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import dingtu.ZRoadMap.PubVar;

public class UpdateManager {

	/* ������ */
	private static final int DOWNLOAD = 1;
	/* ���ؽ��� */
	private static final int DOWNLOAD_FINISH = 2;
	/* ���������XML��Ϣ */
	HashMap<String, String> mHashMap;
	/* ���ر���·�� */
	private String mSavePath;
	/* ��¼���������� */
	private int progress;
	/* �Ƿ�ȡ������ */
	private boolean cancelUpdate = false;
	
	private String codeVersion="";
	private String detail="";

	private Context mContext;
	/* ���½����� */
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	String url = "http://www.xadingtu.com/ducha/version.xml";

	private Handler httpHandler;

	{
		httpHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					showUpdateDialog();
					break;
				case 2:
					 Toast.makeText(mContext, R.string.soft_update_no,
					 Toast.LENGTH_SHORT).show();
					 break;
				case 0:
//					 Toast.makeText(mContext, "�޷����ӵ�����������",
//					 Toast.LENGTH_SHORT).show();
					 break;
				default:
					break;

				}
			}
		};
	}

	private void showUpdateDialog()
	{
		
		AlertDialog.Builder updateDialog = new AlertDialog.Builder(mContext);
		updateDialog.setTitle("ϵͳ����");
		String messageDetails = "�汾�ţ�"+ codeVersion+"  \n";
		if(detail != null)
		{
			for(String n:detail.split(";"))
			{
				messageDetails +="\n"+n;
			}
		}
		
		updateDialog.setMessage(messageDetails+"\n ");
		updateDialog.setNegativeButton("ȡ��", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		});
		updateDialog.setPositiveButton("����", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showNoticeDialog();
				
			}
			
		});
		updateDialog.show();
		
	
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ��������
			case DOWNLOAD:
				// ���ý�����λ��
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
				installApk();
				break;
			default:
				break;
			}
		}
	};

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	/**
	 * ����������
	 */
	public void checkUpdate() {
		
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				int result = isUpdate();
				httpHandler.sendEmptyMessage(result);
				Looper.loop();
			}
		}.start();
	}

	/**
	 * �������Ƿ��и��°汾
	 *
	 * @return
	 */
	private int isUpdate() {
		// ��ȡ��ǰ����汾
		int versionCode = getVersionCode(mContext);
		// ��version.xml�ŵ������ϣ�Ȼ���ȡ�ļ���Ϣ
		Log.e("versionCode", versionCode + "");

		try {
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			// �������糬ʱ����
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);
			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// InputStream inStream = entity.getContent();
				// ����XML�ļ��� ����XML�ļ��Ƚ�С�����ʹ��DOM��ʽ���н���

				ParseXmlService service = new ParseXmlService();
				try {
					mHashMap = service.parseXml(entity.getContent());

				} catch (Exception e) {
					e.printStackTrace();
					return 0;
				}
				if (null != mHashMap) {
					int serviceCode = Integer.valueOf(mHashMap.get("version"));
					// �汾�ж�
					if (serviceCode > versionCode) {
						
						codeVersion = mHashMap.get("codeVersion");
						detail = mHashMap.get("detail");
						return 1;
						
					}
					else
					{
						return 2;
//						Toast.makeText(PubVar.m_DoEvent.m_Context, "�Ѿ������°汾", Toast.LENGTH_LONG).show();
					}
				}
			} else {
				Log.e("http", "HttpEntity is null");
				return 0;
			}

			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

	}

	/**
	 * ��ȡ����汾��
	 *
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
			versionCode = context.getPackageManager().getPackageInfo("com.dingtu.senlinducha", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.d("versionCode", e.getMessage());
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * ��ʾ������¶Ի���
	 */
	private void showNoticeDialog() {
//		// ����Ի���
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setTitle(R.string.soft_update_title);
//		builder.setMessage(R.string.soft_update_info);
//		// ����
//		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				// ��ʾ���ضԻ���
//				showDownloadDialog();
//			}
//		});
//		// �Ժ����
//		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//		Dialog noticeDialog = builder.create();
//		noticeDialog.show();
		
		showDownloadDialog();
	}

	/**
	 * ��ʾ������ضԻ���
	 */
	private void showDownloadDialog() {
		// ����������ضԻ���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.dialog_softwareupdate, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setView(v);
		// ȡ������
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// ����ȡ��״̬
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		downloadApk();
	}

	private void downloadApk() {
		// �������߳��������
		new downloadApkThread().start();
	}

	private class downloadApkThread extends Thread {
		@Override
		public void run() {
			try {
				// �ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					// ��ô洢����·��
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// �ж��ļ�Ŀ¼�Ƿ����
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do {
						int numread = is.read(buf);
						count += numread;
						// ���������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);// ���ȡ����ֹͣ����.
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	};

	/**
	 * ��װAPK�ļ�
	 */
	private void installApk() {
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists()) {
			return;
		}
		Uri data;
		Intent i = new Intent(Intent.ACTION_VIEW);

		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
		// data = FileProvider.getUriForFile(mContext,
		// "com.dingtu.forestrytools.fileprovider", apkfile);
		// i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		// } else {
		data = Uri.fromFile(apkfile);
		// }
		i.setDataAndType(data, "application/vnd.android.package-archive");
		mContext.startActivity(i);

		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
