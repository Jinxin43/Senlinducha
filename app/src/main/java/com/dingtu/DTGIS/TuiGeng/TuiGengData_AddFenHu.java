package com.dingtu.DTGIS.TuiGeng;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class TuiGengData_AddFenHu 
{
	private v1_FormTemplate dialogView;
	private String mLayerID;
	private String mObjID;
	private BigDecimal mXBMianji;
	private BigDecimal mFHMianji;
	private String mZHJ = "";
	//fhIndex>-1��ʾ�Ǳ༭
	private int fhIndex = -1;
	private CheckBox cbPinkunHu;
	
	public TuiGengData_AddFenHu()
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.tuigengdata_sheji_fenhuedit);
		dialogView.ReSetSize(0.6f,-1f);
		dialogView.SetCaption("С��ֻ����");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",ȷ��  ,ȷ��", pCallback);
		cbPinkunHu = ((CheckBox)dialogView.findViewById(R.id.cb_pinkunhu));
		dialogView.findViewById(R.id.bt_calarea).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) 
			{
				if(mFHMianji != null)
				{
					Tools.SetTextViewValueOnID(dialogView, R.id.et_mj, mFHMianji.toString());
				}
				else
				{
					Tools.ShowMessageBox("����ҵ���û����д��");
				}
				
			}
	
		});
		
		dialogView.findViewById(R.id.bt_calmm).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				calcMiaoMu();
			}
		});
	}
	
	private void calcMiaoMu()
	{
		String mianji = Tools.GetTextValueOnID(dialogView, R.id.et_mj);
		double mj = 0;
		
		if(mianji != null && mianji.length()>0)
		{
			try
			{
				mj = Double.parseDouble(mianji);
			}
			catch(Exception ex)
			{
				Tools.ShowMessageBox("�ֻ������ʽ����ȷ��");
			}
			
		}
		else
		{
			Tools.ShowMessageBox("�ֻ��������Ϊ�գ�");
		}
		
		String[] zhjs=mZHJ.split("\\*");
		try
		{
			if(zhjs.length == 2)
			{
				double h =  Double.parseDouble(zhjs[0]);
				double l = Double.parseDouble(zhjs[1]);
				double fk = h*l;
				int count = (int)((mj*666.7)/fk);
				
				Tools.SetTextViewValueOnID(dialogView, R.id.et_mm, count+"");
			}
			else
			{
				Tools.ShowMessageBox("���о��ʽ����ȷ��");
			}
		}
		catch(Exception ex)
		{
			Tools.ShowMessageBox("���о��ʽ����ȷ��");
		}
		
	}
	
	private ICallback mCallback = null;
	public void SetCallback(ICallback cb)
	{
		this.mCallback = cb;
	}
	 
	private ICallback pCallback = new ICallback()
	{
		
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			String huming = Tools.GetTextValueOnID(dialogView, R.id.et_hm);
			String mianji = Tools.GetTextValueOnID(dialogView, R.id.et_mj);
			String miaomu = Tools.GetTextValueOnID(dialogView, R.id.et_mm);
			String zhongzi = Tools.GetTextValueOnID(dialogView, R.id.et_zzzz);
			String dilei = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_dl);
			String linzhong = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_zllz);
			String shuzhong = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_zlsz);
			if(huming.isEmpty()||mianji.isEmpty()||miaomu.isEmpty()||zhongzi.isEmpty()||dilei.isEmpty()||linzhong.isEmpty()||shuzhong.isEmpty())
			{
				Tools.ShowMessageBox("������Ϣ������д������");
				return;
			}
			else
			{
				HashMap<String,Object> hm = new HashMap<String,Object>();
				hm.put("isSelect", false);
				hm.put("huming", huming);
				hm.put("mianji", mianji);
				hm.put("miaomu", miaomu);
				hm.put("zhongzi", zhongzi);
				hm.put("dilei", dilei);
				hm.put("linzhong", linzhong);
				hm.put("shuzhong", shuzhong);
				hm.put("pinkunhu", cbPinkunHu.isChecked()?"��":"��");
				if(mCallback != null)
				{
					if(fhIndex>-1)
					{
						mCallback.OnClick("�༭�ֻ�", hm);
					}
					else
					{
						mCallback.OnClick("�����ֻ�", hm);
					}
					
					dialogView.dismiss();
				}
				
			}
			
		}
	};
	
	

	
	public void setEditInfo(int index,String huming,String dilei,String linzhong,String shuzhong,String miaomu,String zhongzi,String fenhuMJ,BigDecimal shengyuMJ,String zhj,String sfPKH)
	{	
		fhIndex= index;
		mFHMianji = shengyuMJ;
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_sum,"δ�ֻ������"+shengyuMJ+"");
		
		String zllz ="��̬��,������";
		//List<String> strLinZhongs = PubVar.m_DoEvent.m_DictDataDB.getEnumItem("��ҵ", "�˸�����", "��������");
		ArrayAdapter<String> lzAdapter = new ArrayAdapter<String>(dialogView.getContext(),
				android.R.layout.simple_spinner_item,
				zllz.split(","));
		lzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		((Spinner)dialogView.findViewById(R.id.sp_zllz)).setAdapter(lzAdapter);
		Tools.SetSpinnerValueOnID(dialogView, R.id.sp_zllz, linzhong);
		
		String strdl ="25���Ϸǻ���ũ���µ�,����ɳ������,��ҪˮԴ��15��-25���Ƹ���,�˸���,��ɽ�ĵ�,��Ե�,����";
		//List<String> strDiLeis = PubVar.m_DoEvent.m_DictDataDB.getEnumItem("��ҵ", "�˸�����", "����");
		ArrayAdapter<String> zllzAdapter = new ArrayAdapter<String>(dialogView.getContext(),
				android.R.layout.simple_spinner_item,
				strdl.split(","));
		zllzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		((Spinner)dialogView.findViewById(R.id.sp_dl)).setAdapter(zllzAdapter);
		Tools.SetSpinnerValueOnID(dialogView,R.id.sp_dl, dilei);
		
		final v1_SpinnerDialog sp = (v1_SpinnerDialog)dialogView.findViewById(R.id.sp_zlsz);
		sp.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				TuiGengData_ZLSZ zhjDialog = new TuiGengData_ZLSZ(Tools.GetSpinnerValueOnID(dialogView, R.id.sp_zlsz));
				zhjDialog.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_DataBind.SetBindListSpinner(PubVar.m_DoEvent.m_Context, "��������", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), sp);
						
					}});
				zhjDialog.ShowDialog();
			}});
		v1_DataBind.SetBindListSpinner(dialogView.getContext(), "", Tools.StrArrayToList(new String[]{shuzhong}), sp);
		
		if(sfPKH.equals("��"))
		{
			cbPinkunHu.setChecked(true);
		}
		
		Tools.SetTextViewValueOnID(dialogView, R.id.et_hm,huming);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_mj,fenhuMJ);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_zzzz,zhongzi);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_mm,miaomu);
		
		
		mZHJ = zhj;
	}
	
	public void ShowDialog()
    {
    	
		dialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
			}}
    	);
		dialogView.show();
    	
    }
}
