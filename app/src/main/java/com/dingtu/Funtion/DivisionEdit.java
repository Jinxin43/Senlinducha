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
    	DialogView.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+","+Tools.ToLocale("确定")+"  ,确定", btnCallback);
	}
	
	
	private ICallback btnCallback = new ICallback() 
	{
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				
	    		if(Str.equals("确定"))
	    		{
	    			if(mLevel.equals("村"))
	    			{
	    				String cunCode = Tools.GetTextValueOnID(DialogView, R.id.et_cunCode);
	    				String cunName = Tools.GetTextValueOnID(DialogView, R.id.et_cunName);
	    				String townCode = Tools.GetTextValueOnID(DialogView, R.id.et_townCode);
	    				if(cunName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("村名称不能为空！");
	    					return;
	    				}
	    				if(cunCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("村代码不能为空！");
	    					return;
	    				}
	    				if(cunCode.length() != 12)
	    				{
	    					Tools.ShowMessageBox("村代码长度为12位！");
	    					return;
	    				}
	    				if(!cunCode.substring(0,9).equals(townCode))
	    				{
	    					Tools.ShowMessageBox("村代码的前9位必须与乡代码一致！");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("村", townCode, cunName, cunCode))
	    					{
	    						Tools.ShowMessageBox("村信息添加成功！");
	    						mCallback.OnClick("村", null);
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(cunCode, cunName))
							{
								Tools.ShowMessageBox("村信息更新成功！");
								mCallback.OnClick("村", null);
	    						DialogView.dismiss();
							}
						}
	    			}
	    			
	    			if(mLevel.equals("乡"))
	    			{
	    				String townCode = Tools.GetTextValueOnID(DialogView, R.id.et_townCode);
	    				String townName = Tools.GetTextValueOnID(DialogView, R.id.et_townName);
	    				String countyCode = Tools.GetTextValueOnID(DialogView, R.id.et_countyCode);
	    				if(townName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("乡名称不能为空！");
	    					return;
	    				}
	    				if(townCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("乡代码不能为空！");
	    					return;
	    				}
	    				if(townCode.length() != 9)
	    				{
	    					Tools.ShowMessageBox("乡代码长度为9位！");
	    					return;
	    				}
	    				if(!townCode.substring(0,6).equals(countyCode))
	    				{
	    					Tools.ShowMessageBox("乡代码的前6位必须与县代码一致！");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("乡", countyCode, townName, townCode))
	    					{
	    						Tools.ShowMessageBox("乡信息添加成功！");
	    						mCallback.OnClick("乡", null);
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(townCode, townName))
							{
								Tools.ShowMessageBox("乡信息更新成功！");
								mCallback.OnClick("乡", null);
	    						DialogView.dismiss();
							}
						}
	    			}
	    			
	    			if(mLevel.equals("县"))
	    			{
	    				String countyCode = Tools.GetTextValueOnID(DialogView, R.id.et_countyCode);
	    				String countyName = Tools.GetTextValueOnID(DialogView, R.id.et_countyName);
	    				String cityCode = Tools.GetTextValueOnID(DialogView, R.id.et_cityCode);
	    				if(countyName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("县名称不能为空！");
	    					return;
	    				}
	    				if(countyCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("县代码不能为空！");
	    					return;
	    				}
	    				if(countyCode.length() != 6)
	    				{
	    					Tools.ShowMessageBox("县代码长度为6位！");
	    					return;
	    				}
	    				if(!countyCode.substring(0,4).equals(cityCode))
	    				{
	    					Tools.ShowMessageBox("县代码的前4位必须与市代码一致！");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("县", cityCode, countyName, countyCode))
	    					{
	    						Tools.ShowMessageBox("县信息添加成功！");
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(countyCode, countyName))
							{
								Tools.ShowMessageBox("县信息更新成功！");
	    						DialogView.dismiss();
							}
						}
	    			}
	    			
	    			if(mLevel.equals("市"))
	    			{
	    				String cityCode = Tools.GetTextValueOnID(DialogView, R.id.et_cityCode);
	    				String cityName = Tools.GetTextValueOnID(DialogView, R.id.et_cityName);
	    				String provinceCode = Tools.GetTextValueOnID(DialogView, R.id.et_provinceCode);
	    				if(cityName.isEmpty())
	    				{
	    					Tools.ShowMessageBox("市名称不能为空！");
	    					return;
	    				}
	    				if(cityCode.isEmpty())
	    				{
	    					Tools.ShowMessageBox("市代码不能为空！");
	    					return;
	    				}
	    				if(cityCode.length() != 4)
	    				{
	    					Tools.ShowMessageBox("市代码长度为4位！");
	    					return;
	    				}
	    				if(!cityCode.substring(0,2).equals(provinceCode))
	    				{
	    					Tools.ShowMessageBox("市代码的前2位必须与省代码一致！");
	    					return;
	    				}
	    				
	    				if(mIsAdd)
	    				{
	    					if(mDictDB.AddXZQH("市", provinceCode, cityName, cityCode))
	    					{
	    						Tools.ShowMessageBox("市信息添加成功！");
	    						DialogView.dismiss();
	    					}
	    					
	    				}
	    				else 
	    				{
							if(mDictDB.updateXZQH(cityCode, cityName))
							{
								Tools.ShowMessageBox("市信息更新成功！");
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
		if(level.equals("村"))
		{
			DialogView.findViewById(R.id.et_cunName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_cunCode).setEnabled(true);
			}
		}
		if(level.equals("乡"))
		{
			DialogView.findViewById(R.id.ll_cun).setVisibility(View.GONE);
			DialogView.findViewById(R.id.et_townName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_townCode).setEnabled(true);
			}
		}
		if(level.equals("县"))
		{
			DialogView.findViewById(R.id.ll_cun).setVisibility(View.GONE);
			DialogView.findViewById(R.id.ll_town).setVisibility(View.GONE);
			DialogView.findViewById(R.id.et_countyName).setEnabled(true);
			if(isAdd)
			{
				DialogView.findViewById(R.id.et_countyCode).setEnabled(true);
			}
		}
		if(level.equals("市"))
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
		if(level.equals("省"))
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
