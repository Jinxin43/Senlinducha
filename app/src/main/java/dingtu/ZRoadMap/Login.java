package dingtu.ZRoadMap;

import java.util.List;

import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import dingtu.ZRoadMap.LockPatternView.Cell;
import dingtu.ZRoadMap.LockPatternView.DisplayMode;
import lkmap.Tools.Tools;

public class Login extends Activity implements LockPatternView.OnPatternListener {
	private static final String TAG = "LockActivity";

	public static final String LOCK = "lock";
	public static final String LOCK_KEY = "lock_key";

	private static final int STEP_1 = 1; // 手势未设置
	private static final int STEP_2 = 2; // 第一次设置手势完成
	private static final int STEP_3 = 3; // 第二次密码输入错误
	private static final int STEP_4 = 4; // 手势未验证
	private static final int STEP_5 = 5; // 密码被清空
	private static final int STEP_6 = 6; // 修改密码

	private List<Cell> lockPattern;
	private LockPatternView lockPatternView;
	private List<Cell> choosePattern;

	private TextView tvTooltips;
	private AuthorizeTools at = null;
	private boolean hasSetPW = true;

	private int step;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			String version = android.os.Build.MODEL;
			String release = android.os.Build.VERSION.RELEASE;
			PubVar.Version = appInfo.metaData.getString("version");
			if (PubVar.Version.equals("TGHL")) {
				setContentView(R.layout.tghllogin);
			} else if (PubVar.Version.equals("WPZF")) {
				setContentView(R.layout.login_wpzf);
			} else {
				// setContentView(R.layout.loginershi);
				setContentView(R.layout.login);
			}

			at = new AuthorizeTools(this);
			AuthorizeTools_UserInfo userInfo = at.GetUserInfo();

			if (userInfo == null) {
				userInfo = new AuthorizeTools_UserInfo();
				userInfo.SYS_UserType = "临时用户";
			}

			PubVar.softCode = userInfo.SYS_SoftCode;
			if (userInfo.SYS_UserType != "正式用户") {
				// this.getWindow().findViewById(R.id.tvsoftname).setVisibility(View.VISIBLE);
				 this.getWindow().findViewById(R.id.tvsoftname).setVisibility(View.GONE);
			} else {
				TextView tvView = (TextView) this.getWindow().findViewById(R.id.tvsoftname);
				tvView.setVisibility(View.VISIBLE);
				Tools.SetTextViewValueOnID(this, R.id.tvsoftname, "使用单位:" + userInfo.OT_UserName);

			}

			ImageButton btnQuite = (ImageButton) this.findViewById(R.id.btquit);
			btnQuite.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					String PackName = getPackageName();
					am.killBackgroundProcesses(PackName); // API Level至少为8才能使用
					System.exit(0);
				};
			});
		} catch (Exception ex) {

		}

		try {
			SharedPreferences preferences = getSharedPreferences(LOCK, MODE_PRIVATE);
			String patternString = preferences.getString(LOCK_KEY, null);

			lockPatternView = (LockPatternView) findViewById(R.id.iv_lockpattern);
			tvTooltips = (TextView) findViewById(R.id.tvchecktips);

			lockPatternView.setOnPatternListener(this);

			if (patternString == null || patternString.isEmpty()) {
				hasSetPW = false;
			}

			if (hasSetPW) {
				step = STEP_4;
				tvTooltips.setVisibility(View.VISIBLE);
				tvTooltips.setText("请输入手势密码!");
				lockPattern = LockPatternView.stringToPattern(patternString);
			} else {
				tvTooltips.setVisibility(View.VISIBLE);
				tvTooltips.setText("请设置手势密码!");
				step = STEP_1;
			}

		} catch (Exception e) {
			Tools.ShowMessageBox(this, e.getMessage(), null);
		}

	}

	@Override
	public void onPatternStart() {
		Log.d(TAG, "onPatternStart");
	}

	@Override
	public void onPatternCleared() {
		Log.d(TAG, "onPatternCleared");
	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {
		Log.d(TAG, "onPatternCellAdded");
		Log.e(TAG, LockPatternView.patternToString(pattern));
		// Toast.makeText(this, LockPatternView.patternToString(pattern),
		// Toast.LENGTH_LONG).show();
	}

	int clearPW = 0;

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		Log.d(TAG, "onPatternDetected");

		tvTooltips.setVisibility(View.VISIBLE);

		if (hasSetPW) {
			// 修改密码
			if (pattern.size() == 1 && pattern.get(0).getRow() == 1 && pattern.get(0).getColumn() == 1) {
				clearPW++;

				if (clearPW == 8) {
					clearPW = 0;
					step = STEP_1;
					hasSetPW = false;
					tvTooltips.setText("请重新设置密码!");
				} else {
					if (clearPW >= 4) {
						tvTooltips.setText("还需点击" + (8 - clearPW) + "次可以重置密码！");
					} else {
						tvTooltips.setText("");
					}

				}

				lockPatternView.clearPattern();
			} else// 设置密码
			{
				if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
					Toast.makeText(this, R.string.lockpattern_recording_incorrect_too_short, Toast.LENGTH_LONG).show();
					lockPatternView.setDisplayMode(DisplayMode.Wrong);
					tvTooltips.setText("手势密码太短!");
					lockPatternView.clearPattern();

				} else {
					if (pattern.equals(lockPattern)) {
						tvTooltips.setText("密码验证成功!");
						finish();
						Intent intent = new Intent(this, AndroidMap.class);
						startActivity(intent);
					} else {
						tvTooltips.setText("手势密码错误,请重新输入!");
						lockPatternView.clearPattern();
					}
				}
			}
		} else {
			if (pattern.size() >= LockPatternView.MIN_LOCK_PATTERN_SIZE) {
				if (step == STEP_1) {
					choosePattern = pattern;
					step = STEP_2;
					tvTooltips.setText("请再次输入手势密码!");
					lockPatternView.clearPattern();
					return;
				} else {
					if (choosePattern.equals(pattern)) {
						tvTooltips.setText("两次密码输入一致，正在保存!");
						step = STEP_4;
						SharedPreferences preferences = getSharedPreferences(Login.LOCK, MODE_PRIVATE);
						preferences.edit().putString(Login.LOCK_KEY, LockPatternView.patternToString(choosePattern))
								.commit();
						lockPatternView.clearPattern();
						finish();
						Intent intent = new Intent(this, AndroidMap.class);
						startActivity(intent);
					} else {
						tvTooltips.setText("两次密码输入一致，请重新输入!");
						lockPatternView.clearPattern();
					}
				}
			}

		}

	}

}
