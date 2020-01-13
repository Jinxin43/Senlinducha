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

	// 屏幕的方向
	public static int m_SCREEN_ORIENTATION = 0;

	// 屏幕的尺寸
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

		// 1、检查系统目录结构
		HashMap<String, String> resultHM = lkmap.Tools.Tools.CheckSystemFile(this);

		if (resultHM.get("Result").equals("系统主目录缺失")) {

			// 弹跳出选择存储盘的对话框
			v1_SelectSystemPath sst = new v1_SelectSystemPath(this);
			sst.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("退出")) {
						ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
						String PackName = getPackageName();
						am.killBackgroundProcesses(PackName);
						System.exit(0);
					}
					if (Str.equals("工作目录")) {
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
					am.killBackgroundProcesses(PackName); // API Level至少为8才能使用
					System.exit(0);
				}
			});
			return;
		}

		// 获取系统路径
		PubVar.m_SysAbsolutePath = resultHM.get("Path");
		this.LoadSystem();

		if (PubVar.AutoUpate) {
			UpdateManager um = new UpdateManager(this);
			um.checkUpdate();
		}
	}


	//权限申请
	private void requestPermission() {
		mPermissionList.clear();
		for (int i = 0; i < permissions.length; i++) {
			if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
					PackageManager.PERMISSION_GRANTED) {
				mPermissionList.add(permissions[i]);//添加还未授予的权限到mPermissionList中
			}
		}
		if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
			ActivityCompat.requestPermissions(this, permissions, mRequestCode);
		} else {
			//权限已经都通过了，可以将程序继续打开了
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
		if (hasPermissionDismiss) {//如果有没有被允许的权限
			showWaringDialog();
		} else {
			initData();
		}
	}


	private void showWaringDialog() {
		mDialog = new AlertDialog.Builder(this)
				.setTitle("警告！")
				.setMessage("请前往设置->应用->权限中打开相关权限，否则功能无法正常运行！")
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						mDialog.dismiss();
						finish();
					}
				})
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {
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
	 * 检查系统信息
	 * 
	 * @return
	 */
	private boolean CheckSystemInfo() {
		// 检查系统目录结构
		HashMap<String, String> resultHM = lkmap.Tools.Tools.CheckSystemFile(this);
		if (!(resultHM.get("Result").equals("OK")))
			return false;
		// 获取系统路径
		PubVar.m_SysAbsolutePath = resultHM.get("Path");
		return true;
	}

	/**
	 * 系统数据验证通过，开始加载系统
	 */
	private void LoadSystem() {
		m_SCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		// 永不待机
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// setRequestedOrientation(m_SCREEN_ORIENTATION); //纵版
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 横版
		setContentView(R.layout.v1_mainwindow);

		// 增加消息通知
		this.AddNotification();
		try {

			// 创建MapControl对象
			this.m_MapControl = new MapControl(this);

			PubVar.m_MapControl = this.m_MapControl;

			// 设置MapControl的大小
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

			// 初始化全局事件处理类
			this.m_DoEvent = new DoEvent(this);
			// 选择后的回调函数
			this.m_MapControl._Select.SetCallback(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					m_DoEvent.DoCommand("SelectEndCallBack");
				}
			});

			// PubVar.m_DoEvent.m_GlassView = new v1_MyGlass(this);
			// linearLayout.addView(PubVar.m_DoEvent.m_GlassView,0);

			// 设置系统语言
			lkmap.Tools.Tools.AutoGetSystemLanguage();

			// 初始化工具条
			lkmap.ToolBar.ToolBar tb = new lkmap.ToolBar.ToolBar(this);
			// tb.SetBindSubMenuItemPannel(this.findViewById(R.id.slidingDrawer1));
			tb.LoadBottomToolBar(this.findViewById(R.id.map));
			PubVar.m_DoEvent.m_MainBottomToolBar = tb;

			// 此为主界面最上面的GPS状态显示条
			this.m_DoEvent.m_GpsInfoManage.SetHeaderViewBar(this.findViewById(R.id.mainviewheaderbar));

			// 数据状态显示类
			this.m_DoEvent.m_CGpsDataInStatus.SetStatusView((ImageView) this.findViewById(R.id.iv_status));

			// 初始化比例尺条
			ImageView iv = (ImageView) this.findViewById(R.id.iv_scalebar);
			PubVar.m_DoEvent.m_ScaleBar.SetImageView(iv);

			// 初始化其它工具条，如：放大缩小框
			// lkmap.ToolBar.OtherToolBar otb = new
			// lkmap.ToolBar.OtherToolBar();
			// otb.LoadEditBar(findViewById(R.id.toolbar_edit)); //编辑工具
			// otb.LoadZoomBar(findViewById(R.id.gd_zoombar_view));
			// //放大、缩小、移屏、全屏及比例尺工具
			// otb.LoadStrechToolBar_View(findViewById(R.id.strechtoolbar_view));
			// //视图
			lkmap.ToolBar.EditToolbar m_EditToolbar = new lkmap.ToolBar.EditToolbar();
			m_EditToolbar.SetEditToolbar(findViewById(R.id.submenu_editbar)); // 编辑工具条
			m_EditToolbar.ShowToolsItem("", false);

			PubVar.m_DoEvent.m_EditToolbar = m_EditToolbar;

			// 图层按钮
			findViewById(R.id.bt_layerManage).setOnClickListener(new ViewClick());

			// 指北针
			v1_Compass_Control _Compass = new v1_Compass_Control();
			_Compass.SetCompassView(findViewById(R.id.iv_compass));
			_Compass.SetCompassControl(findViewById(R.id.bt_hidecompass));

			// GPS设置
			findViewById(R.id.bt_gps).setOnClickListener(new ViewClick());

			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {

					PubVar.m_DoEvent.DoCommand("工程_选择"); // 加载
				}
			});

			// 默认为手势缩放
			// this.m_DoEvent.DoCommand("手势缩放");

			RelativeLayout.LayoutParams npara = new RelativeLayout.LayoutParams(0, 0);

			if (PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale").Value.equals("小")) {
				npara.height = 170;
				npara.width = 170;
			} else if (PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass_Scale").Value.equals("大")) {
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
	 * 添加顶部通知
	 */
	public void AddNotification() {
		// 添加通知到顶部任务栏
		// 获得NotificationManager实例
		String service = NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) getSystemService(service);
		// 实例化Notification
		Notification n = new Notification();
		// 设置显示图标
		int icon = R.drawable.mainicon;
		// 设置提示信息
		String tickerText = this.getResources().getString(R.string.app_name);
		// 显示时间
		long when = System.currentTimeMillis();

		n.icon = icon;
		n.tickerText = tickerText;
		n.when = when;
		// 显示在“正在进行中”
		n.flags = Notification.FLAG_ONGOING_EVENT;

		// 实例化Intent
		Intent intent = new Intent(AndroidMap.this, AndroidMap.class);
		// 获得PendingIntent
		PendingIntent pi = PendingIntent.getActivity(AndroidMap.this, 0, intent, 0);
		// 设置事件信息，显示在拉开的里面
//		n.setLatestEventInfo(AndroidMap.this, tickerText, "单击切换到系统主界面", pi);
		// 发出通知
//		nm.notify(23232323, n);

	}

	// 删除顶部通知
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
			// if (Tag.equals("卷帘"))
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
			// moveTaskToBack(false); //表示按回退钮后保持现有状态，不回退
			// return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			this.m_DoEvent.DoCommand("单击放大");
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			this.m_DoEvent.DoCommand("单击缩小");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 退出程序
	 */
	private long exitTime = 0;

	private void exitApp() {
		// 判断2次点击事件时间
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			lkmap.Tools.Tools.ShowToast(this, "再次按键退出【" + PubVar.m_SysDictionaryName + "】！");
			exitTime = System.currentTimeMillis();
		} else {
			PubVar.m_DoEvent.DoCommand("退出系统");

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
		// 实例化SelectPicPopupWindow
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	/**
	 * 拦截MENU
	 */
	public boolean onMenuOpened(int featureId, Menu menu) {

		// if (menuWindow == null) {
		// menuWindow = new v1_MainMenu(AndroidMap.this, null);
		// }
		//
		// if (menuWindow.isShowing())
		// menuWindow.dismiss();
		// else {
		// //显示窗口
		// menuWindow.showAtLocation(AndroidMap.this.findViewById(R.id.mainmenu),
		// Gravity.BOTTOM, 0, 0); //设置layout在PopupWindow中显示的位置
		// menuWindow.startAnimate();
		// }
		return false;// 返回为true 则显示系统menu
	}

}