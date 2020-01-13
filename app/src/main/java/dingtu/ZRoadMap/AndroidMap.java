package dingtu.ZRoadMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.Funtion.UpdateManager;
import com.dingtu.senlinducha.R;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.MapControl.MapControl;
import lkmap.MapControl.v1_MyGlass;
import lkmap.ZRoadMap.Compass.v1_Compass_Control;
import lkmap.ZRoadMap.DoEvent.DoEvent;
import lkmap.ZRoadMap.Menu.v1_MainMenu;

public class AndroidMap extends Activity {
	private MapControl m_MapControl = null;
	private DoEvent m_DoEvent = null;

	// ��Ļ�ķ���
	public static int m_SCREEN_ORIENTATION = 0;

	// ��Ļ�ĳߴ�
	public static int m_ScreeWidth = 0;
	public static int m_ScreeHeight = 0;

	String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
	List<String> mPermissionList = new ArrayList<>();
	private final int mRequestCode = 100;
	private boolean hasPermissionDismiss = false;
	private AlertDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			requestPermission();
		} else {
			initData();
		}


	}

	private void initData() {
		PubVar.m_DisplayMetrics = this.getResources().getDisplayMetrics();

		PubVar.m_SysDictionaryName = this.getResources().getString(R.string.app_name);

		// 1�����ϵͳĿ¼�ṹ
		HashMap<String, String> resultHM = lkmap.Tools.Tools.CheckSystemFile(this);

		if (resultHM.get("Result").equals("ϵͳ��Ŀ¼ȱʧ")) {

			// ������ѡ��洢�̵ĶԻ���
			v1_SelectSystemPath sst = new v1_SelectSystemPath(this);
			sst.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("�˳�")) {
						ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
						String PackName = getPackageName();
						am.killBackgroundProcesses(PackName);
						System.exit(0);
					}
					if (Str.equals("����Ŀ¼")) {
						if (CheckSystemInfo())
							LoadSystem();
					}

				}
			});
			sst.ShowDialog();
			return;
		}

		if (!(resultHM.get("Result").equals("OK"))) {
			lkmap.Tools.Tools.ShowMessageBox(this, resultHM.get("Result"), new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					String PackName = getPackageName();
					am.killBackgroundProcesses(PackName); // API Level����Ϊ8����ʹ��
					System.exit(0);
				}
			});
			return;
		}

		// ��ȡϵͳ·��
		PubVar.m_SysAbsolutePath = resultHM.get("Path");
		this.LoadSystem();

		if (PubVar.AutoUpate) {
			UpdateManager um = new UpdateManager(this);
			um.checkUpdate();
		}
	}


	//Ȩ������
	private void requestPermission() {
		mPermissionList.clear();
		for (int i = 0; i < permissions.length; i++) {
			if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
					PackageManager.PERMISSION_GRANTED) {
				mPermissionList.add(permissions[i]);//��ӻ�δ�����Ȩ�޵�mPermissionList��
			}
		}
		if (mPermissionList.size() > 0) {//��Ȩ��û��ͨ������Ҫ����
			ActivityCompat.requestPermissions(this, permissions, mRequestCode);
		} else {
			//Ȩ���Ѿ���ͨ���ˣ����Խ������������
			initData();
		}

	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		if (mRequestCode == requestCode) {
			for (int i = 0; i < grantResults.length; i++) {
				if (grantResults[i] == -1) {
					hasPermissionDismiss = true;
					break;
				}
			}
		}
		if (hasPermissionDismiss) {//�����û�б������Ȩ��
			showWaringDialog();
		} else {
			initData();
		}
	}


	private void showWaringDialog() {
		mDialog = new AlertDialog.Builder(this)
				.setTitle("���棡")
				.setMessage("��ǰ������->Ӧ��->Ȩ���д����Ȩ�ޣ��������޷��������У�")
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						mDialog.dismiss();
						finish();
					}
				})
				.setPositiveButton("����", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mDialog.dismiss();
						Uri packageURI = Uri.parse("package:" + getPackageName());
						Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
						startActivity(intent);
						finish();

					}
				}).show();
	}









	/**
	 * ���ϵͳ��Ϣ
	 * 
	 * @return
	 */
	private boolean CheckSystemInfo() {
		// ���ϵͳĿ¼�ṹ
		HashMap<String, String> resultHM = lkmap.Tools.Tools.CheckSystemFile(this);
		if (!(resultHM.get("Result").equals("OK")))
			return false;
		// ��ȡϵͳ·��
		PubVar.m_SysAbsolutePath = resultHM.get("Path");
		return true;
	}

	/**
	 * ϵͳ������֤ͨ������ʼ����ϵͳ
	 */
	private void LoadSystem() {
		m_SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		// ��������
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// setRequestedOrientation(m_SCREEN_ORIENTATION); //�ݰ�
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // ���
		setContentView(R.layout.v1_mainwindow);

		// ������Ϣ֪ͨ
		this.AddNotification();
		try {

			// ����MapControl����
			this.m_MapControl = new MapControl(this);

			PubVar.m_MapControl = this.m_MapControl;

			// ����MapControl�Ĵ�С
			RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.map);
			linearLayout.addView(m_MapControl, 0);

			RelativeLayout.LayoutParams para = new RelativeLayout.LayoutParams(0, 0);
			para.height = LayoutParams.FILL_PARENT;
			para.width = LayoutParams.FILL_PARENT;
			para.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			para.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			para.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			para.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			m_MapControl.setLayoutParams(para);

			// ��ʼ��ȫ���¼�������
			this.m_DoEvent = new DoEvent(this);
			// ѡ���Ļص�����
			this.m_MapControl._Select.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					m_DoEvent.DoCommand("SelectEndCallBack");
				}
			});

			// PubVar.m_DoEvent.m_GlassView = new v1_MyGlass(this);
			// linearLayout.addView(PubVar.m_DoEvent.m_GlassView,0);

			// ����ϵͳ����
			lkmap.Tools.Tools.AutoGetSystemLanguage();

			// ��ʼ��������
			lkmap.ToolBar.ToolBar tb = new lkmap.ToolBar.ToolBar(this);
			// tb.SetBindSubMenuItemPannel(this.findViewById(R.id.slidingDrawer1));
			tb.LoadBottomToolBar(this.findViewById(R.id.map));
			PubVar.m_DoEvent.m_MainBottomToolBar = tb;

			// ��Ϊ�������������GPS״̬��ʾ��
			this.m_DoEvent.m_GpsInfoManage.SetHeaderViewBar(this.findViewById(R.id.mainviewheaderbar));

			// ����״̬��ʾ��
			this.m_DoEvent.m_CGpsDataInStatus.SetStatusView((ImageView) this.findViewById(R.id.iv_status));

			// ��ʼ����������
			ImageView iv = (ImageView) this.findViewById(R.id.iv_scalebar);
			PubVar.m_DoEvent.m_ScaleBar.SetImageView(iv);

			// ��ʼ���������������磺�Ŵ���С��
			// lkmap.ToolBar.OtherToolBar otb = new
			// lkmap.ToolBar.OtherToolBar();
			// otb.LoadEditBar(findViewById(R.id.toolbar_edit)); //�༭����
			// otb.LoadZoomBar(findViewById(R.id.gd_zoombar_view));
			// //�Ŵ���С��������ȫ���������߹���
			// otb.LoadStrechToolBar_View(findViewById(R.id.strechtoolbar_view));
			// //��ͼ
			lkmap.ToolBar.EditToolbar m_EditToolbar = new lkmap.ToolBar.EditToolbar();
			m_EditToolbar.SetEditToolbar(findViewById(R.id.submenu_editbar)); // �༭������
			m_EditToolbar.ShowToolsItem("", false);

			PubVar.m_DoEvent.m_EditToolbar = m_EditToolbar;

			// ͼ�㰴ť
			findViewById(R.id.bt_layerManage).setOnClickListener(new ViewClick());

			// ָ����
			v1_Compass_Control _Compass = new v1_Compass_Control();
			_Compass.SetCompassView(findViewById(R.id.iv_compass));
			_Compass.SetCompassControl(findViewById(R.id.bt_hidecompass));

			// GPS����
			findViewById(R.id.bt_gps).setOnClickListener(new ViewClick());

			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {

					PubVar.m_DoEvent.DoCommand("����_ѡ��"); // ����
				}
			});

			// Ĭ��Ϊ��������
			// this.m_DoEvent.DoCommand("��������");

			RelativeLayout.LayoutParams npara = new RelativeLayout.LayoutParams(0, 0);

			if (PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale").Value.equals("С")) {
				npara.height = 170;
				npara.width = 170;
			} else if (PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale").Value.equals("��")) {
				npara.height = 400;
				npara.width = 400;
			} else {
				npara.height = 300;
				npara.width = 300;
			}

			npara.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			npara.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			this.m_DoEvent.m_GlassView = new v1_MyGlass(this);
			this.m_DoEvent.m_GlassView.setLayoutParams(npara);
			linearLayout.addView(this.m_DoEvent.m_GlassView);
			if (PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true")) {
				PubVar.m_DoEvent.m_GlassView.SetVisible(true);
			} else {
				PubVar.m_DoEvent.m_GlassView.SetVisible(false);
			}

		} catch (Error Err) {
			lkmap.Tools.Tools.ShowMessageBox(Err.getMessage());
		}
	}

	public void BindEvent(int viewID, ViewClick VC) {
		View v = this.findViewById(viewID);
		if (v != null)
			v.setOnClickListener(VC);
	}

	public ViewClick GetViewClick() {
		return new ViewClick();
	}

	/**
	 * ��Ӷ���֪ͨ
	 */
	public void AddNotification() {
		// ���֪ͨ������������
		// ���NotificationManagerʵ��
		String service = NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(service);
		// ʵ����Notification
		Notification n = new Notification();
		// ������ʾͼ��
		int icon = R.drawable.mainicon;
		// ������ʾ��Ϣ
		String tickerText = this.getResources().getString(R.string.app_name);
		// ��ʾʱ��
		long when = System.currentTimeMillis();

		n.icon = icon;
		n.tickerText = tickerText;
		n.when = when;
		// ��ʾ�ڡ����ڽ����С�
		n.flags = Notification.FLAG_ONGOING_EVENT;

		// ʵ����Intent
		Intent intent = new Intent(AndroidMap.this, AndroidMap.class);
		// ���PendingIntent
		PendingIntent pi = PendingIntent.getActivity(AndroidMap.this, 0, intent, 0);
		// �����¼���Ϣ����ʾ������������
//		n.setLatestEventInfo(AndroidMap.this, tickerText, "�����л���ϵͳ������", pi);
		// ����֪ͨ
//		nm.notify(23232323, n);

	}

	// ɾ������֪ͨ
	public static void ClearNotification(Context C) {
		String service = NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) C.getSystemService(service);
		nm.cancelAll();
	}

	boolean isShutter = false;

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			// if (Tag.equals("����"))
			// {
			// if(PubVar.m_DoEvent.AlwaysOpenProject())
			// {
			// PubVar.m_MapControl.setActiveTool(Tools.Shutter);
			// PubVar.m_DoEvent.m_EditToolbar.ClearButtonSelect();
			// lkmap.Tools.Tools.SetToolsBarItemSelect(arg0, true);
			// }
			//
			// return;
			// }
			m_DoEvent.DoCommand(Tag);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			exitApp();
			return false;
			// moveTaskToBack(false); //��ʾ������ť�󱣳�����״̬��������
			// return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			this.m_DoEvent.DoCommand("�����Ŵ�");
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			this.m_DoEvent.DoCommand("������С");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �˳�����
	 */
	private long exitTime = 0;

	private void exitApp() {
		// �ж�2�ε���¼�ʱ��
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			lkmap.Tools.Tools.ShowToast(this, "�ٴΰ����˳���" + PubVar.m_SysDictionaryName + "����");
			exitTime = System.currentTimeMillis();
		} else {
			PubVar.m_DoEvent.DoCommand("�˳�ϵͳ");

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
	}

	v1_MainMenu menuWindow;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// ʵ����SelectPicPopupWindow
		menu.add("menu");// ���봴��һ��
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	/**
	 * ����MENU
	 */
	public boolean onMenuOpened(int featureId, Menu menu) {

		// if (menuWindow == null) {
		// menuWindow = new v1_MainMenu(AndroidMap.this, null);
		// }
		//
		// if (menuWindow.isShowing())
		// menuWindow.dismiss();
		// else {
		// //��ʾ����
		// menuWindow.showAtLocation(AndroidMap.this.findViewById(R.id.mainmenu),
		// Gravity.BOTTOM, 0, 0); //����layout��PopupWindow����ʾ��λ��
		// menuWindow.startAnimate();
		// }
		return false;// ����Ϊtrue ����ʾϵͳmenu
	}

}