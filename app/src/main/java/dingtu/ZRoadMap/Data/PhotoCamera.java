package dingtu.ZRoadMap.Data;

import java.io.File;

import com.dingtu.Funtion.PhotoControl;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

public class PhotoCamera extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String PhotoPath = this.getIntent().getStringExtra("PhotoPath");
		String TempPhoto = this.getIntent().getStringExtra("TempPhoto");

		File out = new File(PhotoPath, TempPhoto);
		Uri _TName = Uri.fromFile(out);

		Intent tt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		tt.putExtra(MediaStore.EXTRA_OUTPUT, _TName);
		startActivityForResult(tt, 1);
	}

	// ȫ������ص�������Ҫ
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("tag", "���������գ���PhotoCamera���ڻص�������");

		if (PhotoControl._Callback != null) {
			PhotoControl._Callback.OnClick("ok", requestCode + "");
		}

		// lkmap.Tools.Tools.CloseActivity(this);
		if (v1_Photo._Callback != null)
			v1_Photo._Callback.OnClick("OK", requestCode + "");

		// �˴����ܻ����window leak����
		// �����������androidMainfest.xml�м���android:configChanges="orientation|keyboardHidden|navigation"
		this.finish();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
	}

}
