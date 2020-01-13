package dingtu.ZRoadMap.Data;

import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import dingtu.ZRoadMap.AndroidMap;
import dingtu.ZRoadMap.PubVar;
import lkmap.Tools.Tools;

public class v1_FormTemplate extends Dialog {
	public Context C = null;
	private ICallback _returnCallback = null; // �ص�����

	public void SetCallback(ICallback returnCallback) {
		_returnCallback = returnCallback;
	}

	public void HideSoftInputMode() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // ��������ʱ�������뷨
	}

	/**
	 * ���ع�����
	 */
	public void HideToolBar() {
		this.findViewById(R.id.rl_toolbar).setVisibility(View.GONE);
	}

	/**
	 * ���ñ����ı�
	 * 
	 * @param CaptionText
	 */
	public void SetCaption(String CaptionText) {
		// TextView tv = (TextView)this.findViewById(R.id.headerbar);
		// tv.setText(" "+CaptionText);

		Button button = (Button) this.findViewById(R.id.formtemp_quit);
		button.setText("" + CaptionText);
	}

	/**
	 * ��ȡָ��ID�İ�ť����
	 * 
	 * @param btnID
	 * @return
	 */
	public Button GetButton(String btnID) {
		int buttonID = -1;
		if (btnID.equals("1"))
			buttonID = R.id.formtemp_4;
		if (btnID.equals("2"))
			buttonID = R.id.formtemp_3;
		if (btnID.equals("3"))
			buttonID = R.id.formtemp_2;
		if (btnID.equals("4"))
			buttonID = R.id.formtemp_1;

		return (Button) this.findViewById(buttonID);
	}

	/**
	 * ���ð�ť��Ϣ
	 * 
	 * @param buttonInfo
	 *            ˳���,ͼ����Դ,Text,Tag
	 * @param ICallback
	 *            �����Ļص��¼�
	 */
	public void SetButtonInfo(String buttonInfo, final ICallback callBack) {
		int buttonID = R.id.formtemp_1;

		String[] btInfo = buttonInfo.split(",");
		if (btInfo[0].equals("1"))
			buttonID = R.id.formtemp_4;
		if (btInfo[0].equals("2"))
			buttonID = R.id.formtemp_3;
		if (btInfo[0].equals("3"))
			buttonID = R.id.formtemp_2;
		if (btInfo[0].equals("4"))
			buttonID = R.id.formtemp_1;
		Button v = (Button) this.findViewById(buttonID);

		// ���ð�ťͼƬ��Դ
		int imageID = Integer.parseInt(btInfo[1]);
		if (imageID != -1) {
			v.setVisibility(View.VISIBLE);
			Drawable drawable = C.getResources().getDrawable(imageID);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());// ��һ������Ҫ��,���򲻻���ʾ.
			v.setCompoundDrawables(drawable, null, null, null);
		}

		// ���ð�ťText,Tag
		v.setText(btInfo[2]);
		v.setTag(btInfo[3]);

		// ���¼�
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Button bt = (Button) v;
				if (callBack != null)
					callBack.OnClick(bt.getTag().toString(), "");
			}
		});

		setButtonFocusChanged(v);
	}

	/**
	 * ���������ť���е���ɫ����
	 */
	public final static float[] BT_SELECTED = new float[] { 1.5f, 0, 0, 0, 0, 1.6f, 1.5f, 0, 0, 0, 1.8f, 0, 1.3f, 0, 0,
			0, 0, 0, 1.3f, 0 };

	/**
	 * ��ť�ָ�ԭ״����ɫ����
	 */
	public final static float[] BT_NOT_SELECTED = new float[] { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
			0 };

	public final static OnFocusChangeListener buttonOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				v.invalidate();
			} else {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				v.invalidate();
			}
		}
	};

	/**
	 * ��ť��������Ч��
	 */
	public final static OnTouchListener buttonOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
				v.setBackgroundDrawable(v.getBackground());
				v.invalidate();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// v.getBackground().setColorFilter(new
				// ColorMatrixColorFilter(BT_NOT_SELECTED));
				// v.setBackgroundDrawable(v.getBackground());
				// v.setBackground(v.getBackground());
				v.getBackground().clearColorFilter();
				v.invalidate();
			}
			return false;
		}
	};

	/**
	 * ����ͼƬ��ť��ȡ����ı�״̬
	 * 
	 * @param inImageButton
	 */
	public final static void setButtonFocusChanged(View inView) {
		inView.setOnTouchListener(buttonOnTouchListener);
		// inView.setOnFocusChangeListener(buttonOnFocusChangeListener);
	}

	public v1_FormTemplate(Context context) {
		super(context);

		// ����������
		lkmap.Tools.Tools.SetLocale(context);

		this.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface arg0) {
			}
		});

		this.C = context;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setCancelable(false); // ����Ӧ���˼�
		this.HideSoftInputMode();
		this.setContentView(R.layout.l_dialogtemplate);

		// ���˳���ť
		Button v = (Button) this.findViewById(R.id.formtemp_quit);
		v.setOnClickListener(new ViewClick());
		v.setText(Tools.ToLocale(v.getText() + "") + " ");
		setButtonFocusChanged(v);
	}

	class ViewClick implements View.OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			v1_FormTemplate.this.DoCommand(Tag);
		}
	}

	// ��ť�¼�
	public void DoCommand(String StrCommand) {
		if (StrCommand.equals("�˳�")) {
			Button v = (Button) v1_FormTemplate.this.findViewById(R.id.formtemp_quit);
			if (v.getText().equals("������������")) {
				Tools.ShowYesNoMessage(v1_FormTemplate.this.getContext(), "ȷ���˳���", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if (Str.equals("YES")) {
							if (v1_FormTemplate.this._returnCallback != null)
								v1_FormTemplate.this._returnCallback.OnClick("�˳�", "");
							v1_FormTemplate.this.dismiss();
						}

					}
				});
			} else {
				if (this._returnCallback != null)
					this._returnCallback.OnClick("�˳�", "");
				this.dismiss();
			}
		}
	}

	// ���������ߴ�
	public void SetOtherView(int ViewID) {
		LayoutInflater inflater = (LayoutInflater) this.C.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View vPopupWindow = inflater.inflate(ViewID, null, false);
		this.SetOtherView(vPopupWindow);
	}

	public void SetOtherView(View view) {
		LinearLayout LY = (LinearLayout) this.findViewById(R.id.databindalertdialoglayout);
		LY.addView(view, LY.getLayoutParams());
	}

	// �������óߴ�
	public void ReSetSize(float WScale, float HScale) {
		ReSetSize(WScale, HScale, 0, 0);
	}

	public void ReSetSize(float WScale, float HScale, int XOffset, int YOffset) {
		WindowManager.LayoutParams p = this.getWindow().getAttributes();
		WindowManager m = ((Activity) PubVar.m_DoEvent.m_Context).getWindowManager();

		// ��ȡ�Ի���ǰ�Ĳ���ֵ ���ɸ��ݲ�ͬ�ֱ��ʵ�������ʾ�ĸ����
		Display d = m.getDefaultDisplay(); // Ϊ��ȡ��Ļ����
		p.x = XOffset; // ����λ�� Ĭ��Ϊ����
		p.y = YOffset; // ����λ�� Ĭ��Ϊ����
		if (AndroidMap.m_SCREEN_ORIENTATION == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			if (HScale != 0)
				p.height = (int) (d.getHeight() * HScale); // �߶�����Ϊ��Ļ��0.6
			if (WScale != 0)
				p.width = (int) (d.getWidth() * WScale); // �������Ϊ��Ļ��0.95
		} else {
			p.width = (int) (d.getWidth() * WScale); // �������Ϊ��Ļ
			if (HScale > 0)
				p.height = (int) (d.getHeight() * HScale); // �߶�����Ϊ��Ļ
		}
		this.getWindow().setAttributes((android.view.WindowManager.LayoutParams) p);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			DoCommand("�˳�");
			return false;
			// moveTaskToBack(false); //��ʾ������ť�󱣳�����״̬��������
			// return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
