package com.dingtu.DTGIS.LDBG;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;

public class JiaoguiceshuEdit {

	private v1_FormTemplate mDialog = null;
	private String mMode = "new";

	private String mShi;
	private String mXian;
	private String mXiang;
	private String mCun;
	private String mLinban;
	private String mXiaoBan;
	private String mX;
	private String mY;

	public JiaoguiceshuEdit(String shi, String xian, String xiang, String xingzhegncun, String linban, String xiaoban,
			String x, String y) {
		mDialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		mDialog.SetOtherView(R.layout.xiaobanxuji_jiaoguiceshu_edit);
		mDialog.SetCaption(Tools.ToLocale("角规测树记录"));
		mDialog.ReSetSize(0.28f, -0.26f);
		mDialog.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("确定") + "  ,确定", pCallback);
		mShi = shi;
		mXian = xian;
		mXiang = xiang;
		mCun = xingzhegncun;
		mLinban = linban;
		mXiaoBan = xiaoban;
		mX = x;
		mY = y;
		initShuzhong(shi);
	}

	private void initShuzhong(String shi) {
		String szStr = "";
		if (!shi.equals("资源局")) {
			if (shi.length() < 3 && (!shi.endsWith("市"))) {
				shi = shi + "市";
			}
		}
		String sql = "select * from ShuZhong where CityName='" + shi + "' OR CityID='" + shi + "'";
		SQLiteDataReader DR = PubVar.m_DoEvent.m_ConfigDB.GetSQLiteDatabase().Query(sql);
		int i = 1;
		while (DR.Read()) {
			if (i == 1) {
				szStr += DR.GetString("ShuZhongCode");
			} else {
				szStr += "," + DR.GetString("ShuZhongCode");
			}
			i++;
		}

		ArrayAdapter<String> trhdAdapter = new ArrayAdapter<String>(mDialog.getContext(),
				android.R.layout.simple_spinner_item,
				// PubVar.m_DoEvent.m_DictDataDB.getEnumList(mProjectType,
				// "土壤厚度"));
				szStr.split(","));
		trhdAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) mDialog.findViewById(R.id.sp_shuzhong)).setAdapter(trhdAdapter);
	}

	private ICallback m_Callback = null;

	public void SetCallback(ICallback cb) {
		this.m_Callback = cb;
	}

	public void SetEditMode(String pMode, String pShuzhu, String D, String H, String G, int maxCode) {
		mMode = pMode;
		if (pMode == "edit") {
			Tools.SetSpinnerValueOnID(mDialog, R.id.sp_shuzhong, pShuzhu);
			Tools.SetTextViewValueOnID(mDialog, R.id.et_D, D);
			Tools.SetTextViewValueOnID(mDialog, R.id.et_H, H);
			// Tools.SetTextViewValueOnID(mDialog, R.id.et_G, G);
			if (G.equals("1")) {
				RadioButton rbButton = (RadioButton) mDialog.findViewById(R.id.rbCross);
				rbButton.setChecked(true);
			} else {
				RadioButton rbButton = (RadioButton) mDialog.findViewById(R.id.rbGraze);
				rbButton.setChecked(true);
			}
			Tools.SetTextViewValueOnID(mDialog, R.id.etCode, maxCode + "");
		} else {
			if (pShuzhu != null) {
				Tools.SetSpinnerValueOnID(mDialog, R.id.sp_shuzhong, pShuzhu);
			}
			Tools.SetTextViewValueOnID(mDialog, R.id.etCode, maxCode + "");
		}
	}

	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("确定")) {
				if (SaveCeshuDetail()) {
					if (m_Callback != null) {

						m_Callback.OnClick("新增", null);
					}
					mDialog.dismiss();
				} else {
					Tools.ShowToast(PubVar.m_DoEvent.m_Context, "角规测树记录保存失败");
				}
			}
		}
	};

	private boolean SaveCeshuDetail() {
		String strG = "0.5";
		RadioGroup rg = (RadioGroup) mDialog.findViewById(R.id.rg_GSelect);
		if (rg.getCheckedRadioButtonId() == R.id.rbCross) {
			strG = "1";
		}

		String strD = Tools.GetTextValueOnID(mDialog, R.id.et_D);
		if (strD == null || strD.isEmpty()) {
			Tools.ShowMessageBox("D为必填项");
			return false;
		}

		String strH = Tools.GetTextValueOnID(mDialog, R.id.et_H);
		if (strH == null || strH.isEmpty()) {
			Tools.ShowMessageBox("H为必填项");
			return false;
		}

		// String strG = Tools.GetTextValueOnID(mDialog, R.id.et_G);
		if (strG == null || strG.isEmpty()) {
			Tools.ShowMessageBox("G为必填项");
			return false;
		}

		String sql = "";

		String shuzhong = Tools.GetSpinnerValueOnID(mDialog, R.id.sp_shuzhong);

		if (mMode.equals("edit")) {
			sql = "update T_Jiaoguiceshu set D='" + strD + "', H='" + strH + "'" + ", G='" + strG + "',ShuZhong='"
					+ shuzhong + "' where Shi='" + mShi + "' and Xiang ='" + mXiang + "' and " + "Xian ='" + mXian
					+ "' and Linban = '" + mLinban + "' and XiaoBan=" + mXiaoBan + " and X='" + mX + "' and Y='" + mY
					+ "' and TreeID=" + Integer.parseInt(Tools.GetTextValueOnID(mDialog, R.id.etCode));
		} else {
			sql = "insert into T_Jiaoguiceshu (Shi,Xian,Xiang,Cun,Linban,XiaoBan,X,Y,TreeID,D,G,H,ShuZhong) values"
					+ "('%1$s','%2$s','%3$s','%4$s','%5$s', '%6$s','%7$s','%8$s',%9$d, '%10$s','%11$s','%12$s','%13$s')";

			sql = String.format(sql, mShi, mXian, mXiang, mCun, mLinban, mXiaoBan, mX, mY,
					Integer.parseInt(Tools.GetTextValueOnID(mDialog, R.id.etCode)), strD, strG, strH, shuzhong);
		}

		return PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql);
	}

	public void ShowDialog() {
		// 此处这样做的目的是为了计算控件的尺寸
		mDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
			}
		});
		mDialog.show();
	}
}
