package com.dingtu.Funtion;

import java.util.logging.Level;

import com.dingtu.DTGIS.DataService.DictDataDB;
import com.dingtu.DTGIS.DataService.DictXZQH;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class DivisionEdit 
{
	private v1_FormTemplate DialogView = null;
	private String mLevel = "";
	private DictXZQH mDictDB = new DictXZQH();
	private boolean mIsAdd = false;
	private ICallback mCallback = null;
	
	public DivisionEdit()
	{
		DialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	DialogView.SetOtherView(R.layout.divisionedit);
    	DialogView.ReSetSize(0.46f,0.67f);
    	DialogView.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", btnCallback);
	}
	
	
	private ICallback btnCallback = new ICallback() 
	{
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				
	    		if(Str.equals("ȷ��"))
	    		{
	    			if(mLevel.equals("��"))
	    			{
	    				String cunCode = Tools.GetTextValueOnID(DialogView, R.id.et_cunCode);
	    				String cunName = Tools.GetTextValueOnID(DialogView, R.id.et_cunName);
	    				String townCode = Tools.GetTextValueOnID(DialogView, R.id.et_townCode);
	    				if(cunName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("�����Ʋ���Ϊ�գ�");
	    					return;
	    				}
	    				if(cunCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("����벻��Ϊ�գ�");
	    					return;
	    				}
	    				if(cunCode.length() != 12)
	    				{
	    					Tools.ShowMessageBox("����볤��Ϊ12λ��");
	    					return;
	    				}
	    				if(!cunCode.substring(0,9).equals(townCode))
	    				{
	    					Tools.ShowMessageBox("������ǰ9λ�����������һ�£�");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("��", townCode, cunName, cunCode))
	    					{
	    						Tools.ShowMessageBox("����Ϣ��ӳɹ���");
	    						mCallback.OnClick("��", null);
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(cunCode, cunName))
							{
								Tools.ShowMessageBox("����Ϣ���³ɹ���");
								mCallback.OnClick("��", null);
	    						DialogView.dismiss();
							}
						}
	    			}
	    			
	    			if(mLevel.equals("��"))
	    			{
	    				String townCode = Tools.GetTextValueOnID(DialogView, R.id.et_townCode);
	    				String townName = Tools.GetTextValueOnID(DialogView, R.id.et_townName);
	    				String countyCode = Tools.GetTextValueOnID(DialogView, R.id.et_countyCode);
	    				if(townName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("�����Ʋ���Ϊ�գ�");
	    					return;
	    				}
	    				if(townCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("����벻��Ϊ�գ�");
	    					return;
	    				}
	    				if(townCode.length() != 9)
	    				{
	    					Tools.ShowMessageBox("����볤��Ϊ9λ��");
	    					return;
	    				}
	    				if(!townCode.substring(0,6).equals(countyCode))
	    				{
	    					Tools.ShowMessageBox("������ǰ6λ�������ش���һ�£�");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("��", countyCode, townName, townCode))
	    					{
	    						Tools.ShowMessageBox("����Ϣ��ӳɹ���");
	    						mCallback.OnClick("��", null);
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(townCode, townName))
							{
								Tools.ShowMessageBox("����Ϣ���³ɹ���");
								mCallback.OnClick("��", null);
	    						DialogView.dismiss();
							}
						}
	    			}
	    			
	    			if(mLevel.equals("��"))
	    			{
	    				String countyCode = Tools.GetTextValueOnID(DialogView, R.id.et_countyCode);
	    				String countyName = Tools.GetTextValueOnID(DialogView, R.id.et_countyName);
	    				String cityCode = Tools.GetTextValueOnID(DialogView, R.id.et_cityCode);
	    				if(countyName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("�����Ʋ���Ϊ�գ�");
	    					return;
	    				}
	    				if(countyCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("�ش��벻��Ϊ�գ�");
	    					return;
	    				}
	    				if(countyCode.length() != 6)
	    				{
	    					Tools.ShowMessageBox("�ش��볤��Ϊ6λ��");
	    					return;
	    				}
	    				if(!countyCode.substring(0,4).equals(cityCode))
	    				{
	    					Tools.ShowMessageBox("�ش����ǰ4λ�������д���һ�£�");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("��", cityCode, countyName, countyCode))
	    					{
	    						Tools.ShowMessageBox("����Ϣ��ӳɹ���");
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(countyCode, countyName))
							{
								Tools.ShowMessageBox("����Ϣ���³ɹ���");
	    						DialogView.dismiss();
							}
						}
	    			}
	    			
	    			if(mLevel.equals("��"))
	    			{
	    				String cityCode = Tools.GetTextValueOnID(DialogView, R.id.et_cityCode);
	    				String cityName = Tools.GetTextValueOnID(DialogView, R.id.et_cityName);
	    				String provinceCode = Tools.GetTextValueOnID(DialogView, R.id.et_provinceCode);
	    				if(cityName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("�����Ʋ���Ϊ�գ�");
	    					return;
	    				}
	    				if(cityCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("�д��벻��Ϊ�գ�");
	    					return;
	    				}
	    				if(cityCode.length() != 4)
	    				{
	    					Tools.ShowMessageBox("�д��볤��Ϊ4λ��");
	    					return;
	    				}
	    				if(!cityCode.substring(0,2).equals(provinceCode))
	    				{
	    					Tools.ShowMessageBox("�д����ǰ2λ������ʡ����һ�£�");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("��", provinceCode, cityName, cityCode))
	    					{
	    						Tools.ShowMessageBox("����Ϣ��ӳɹ���");
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(cityCode, cityName))
							{
								Tools.ShowMessageBox("����Ϣ���³ɹ���");
	    						DialogView.dismiss();
							}
						}
	    			}
	    		}
			}
	};
	
	public void setInfo(Boolean isAdd,String level,String provinceName,String provinceCode,String cityName,String cityCode,String countyName,String countyCode,String townName,String townCode,String cunName,String cunCode)
	{
		mIsAdd = isAdd;
		mLevel = level;
		Tools.SetTextViewValueOnID(DialogView, R.id.et_provinceName, provinceName);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_provinceCode, provinceCode);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_cityName, cityName);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_cityCode, cityCode);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_countyName, countyName);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_countyCode, countyCode);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_townName, townName);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_townCode, townCode);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_cunName, cunName);
		Tools.SetTextViewValueOnID(DialogView, R.id.et_cunCode, cunCode);
		if(level.equals("��"))
		{
			DialogView.findViewById(R.id.et_cunName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_cunCode).setEnabled(true);
			}
		}
		if(level.equals("��"))
		{
			DialogView.findViewById(R.id.ll_cun).setVisibility(View.GONE);
			DialogView.findViewById(R.id.et_townName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_townCode).setEnabled(true);
			}
		}
		if(level.equals("��"))
		{
			DialogView.findViewById(R.id.ll_cun).setVisibility(View.GONE);
			DialogView.findViewById(R.id.ll_town).setVisibility(View.GONE);
			DialogView.findViewById(R.id.et_countyName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_countyCode).setEnabled(true);
			}
		}
		if(level.equals("��"))
		{
			DialogView.findViewById(R.id.ll_cun).setVisibility(View.GONE);
			DialogView.findViewById(R.id.ll_town).setVisibility(View.GONE);
			DialogView.findViewById(R.id.ll_county).setVisibility(View.GONE);
			DialogView.findViewById(R.id.et_cityName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_cityCode).setEnabled(true);
			}
		}
		if(level.equals("ʡ"))
		{
			DialogView.findViewById(R.id.ll_cun).setVisibility(View.GONE);
			DialogView.findViewById(R.id.ll_town).setVisibility(View.GONE);
			DialogView.findViewById(R.id.ll_county).setVisibility(View.GONE);
			DialogView.findViewById(R.id.ll_city).setVisibility(View.GONE);
			DialogView.findViewById(R.id.et_provinceName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_provinceCode).setEnabled(true);
			}
		}
		
		
	}
	
	public void ShowDialog()
	{
		DialogView.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
			}
				
    	});
    		
    	DialogView.show();
	}
	
	public void setCallback(ICallback callback)
	{
		mCallback = callback;
	}
}
