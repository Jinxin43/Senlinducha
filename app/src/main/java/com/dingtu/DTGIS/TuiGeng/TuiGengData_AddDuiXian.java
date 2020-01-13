package com.dingtu.DTGIS.TuiGeng;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.TuiGengDB;
import com.dingtu.DTGIS.TuiGeng.TuiGengData.TextFilter;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class TuiGengData_AddDuiXian 
{
	private v1_FormTemplate dialogView;
	private String mLayerID;
	private String mObjID;
	private String mCishu;
	private int dxIndex = -1;
	private BigDecimal mMMDJ = new BigDecimal("0");
	private String mNianDu="";
	private String mZhuhangju;
	private String mMianji;
	
	private v1_FormTemplate listView;
	
	public TuiGengData_AddDuiXian(boolean isAdd,String layerID,int objID,String cishu,String niandu,String danjia,String zhuhangju)
	{
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.tuigeng_duixianlist_add);
		dialogView.ReSetSize(0.6f,-1f);
		dialogView.SetCaption("兑现情况");
		dialogView.SetButtonInfo("1,"+R.drawable.v1_ok+",确定  ,确定", pCallback);
		
		mLayerID = layerID;
		mObjID = objID+"";
		mCishu = cishu;
		
		if(isAdd)
		{
			initSelectList();
		}
		
		((EditText)dialogView.findViewById(R.id.et_zcbzbz)).addTextChangedListener(new DXJEChanged());
		((EditText)dialogView.findViewById(R.id.et_zmf)).addTextChangedListener(new DXJEChanged());
		
		dialogView.findViewById(R.id.bt_cal).setOnClickListener(new ViewClick());
		mNianDu = niandu;
		mZhuhangju = zhuhangju;
		try
		{
			mMMDJ = new BigDecimal(danjia);
		}
		catch(Exception ex)
		{}
	}
	
   private void initSelectList()
   {
	   	TuiGengDB db = new TuiGengDB();
		HashMap<String, String> hMap = db.getFenhuData(mLayerID, mObjID);
		String humings = hMap.get("户名s");
		String dileis = hMap.get("地类s");
		String mianjis = hMap.get("面积s");
		String linzhongs = hMap.get("林种s");
		String shuzhus = hMap.get("树种s");
		String miaomus = hMap.get("苗木s");
		String zhongzis = hMap.get("种子s");
		String xiangzhen = hMap.get("乡镇s");
		String cun = hMap.get("建制村s");
		String xiaoban = hMap.get("小班s");
		
		if(humings== null || humings.isEmpty())
		{
			Tools.ShowMessageBox("请先录入分户信息！");
			return;
		}
		
		HashMap<String, String> dMap = db.getDuixianData(mLayerID, mObjID);
		String strDXs = dMap.get("D户名");
		List<String> dxHuMings = new ArrayList<String>();
		boolean isDX = false;
		if(strDXs != null && strDXs.length()>0)
		{
			String[] dxs= dMap.get("D户名").split(";");
			for(String dx:dxs)
			{
				if(dx.contains(mCishu+"."))
				{
					isDX = true;
					dxHuMings = Tools.StrArrayToList(dx.replace(mCishu+".", "").split(","));
				}
			}
		}
		
		ArrayList<HashMap<String,Object>> mList = new ArrayList<HashMap<String,Object>>();
		
		String[] arrayHM = humings.split(",");
		String[] arrayDL = dileis.split(",");
		String[] arrayMJ = mianjis.split(",");
		String[] arraySZ = shuzhus.split(",");
		
		String[] arrayLZ = linzhongs.split(",");
		String[] arrayMM = miaomus.split(",");
		String[] arrayZZ = zhongzis.split(",");
		for(int i=0;i<arrayHM.length;i++)
		{
			if(!dxHuMings.contains(arrayHM[i]))
			{
				HashMap<String,Object> hm = new HashMap<String,Object>();
				try
				{
					hm.put("xiangzhen",xiangzhen);
					hm.put("cun", cun);
					hm.put("xiaoban", xiaoban);
					hm.put("huming", arrayHM[i]);
					hm.put("dilei", arrayDL[i]);
					hm.put("mianji", arrayMJ[i]);
					hm.put("linzhong", arrayLZ[i]);
					hm.put("shuzhong", arraySZ[i]);
					hm.put("miaomu", arrayMM[i]);
					hm.put("zhongzi", arrayZZ[i]);
					mList.add(hm);
				}
				catch(Exception ex)
				{
					Tools.ShowMessageBox(ex.getMessage());
				}
			}
			
		}
		
		if(mList.size() == 0)
		{
			Tools.ShowMessageBox("所有分户都已兑现！");
			return;
		}
		
		
		showNeedFenhuList(mList);
   }
   
   public class ViewClick implements OnClickListener
   {
   	@Override
   	public void onClick(View arg0)
   	{
   		String Tag = arg0.getTag().toString();
   		if (Tag.equals("计算苗木费"))
   		{
   			if(mZhuhangju != null && mZhuhangju.length()>0)
   			{
   				try
   				{
   					
   					String[] zhjs=mZhuhangju.split("\\*");
   					if(zhjs.length == 2)
   					{
   						double h = Double.parseDouble(zhjs[0]);
   						double l = Double.parseDouble(zhjs[1]);
   						double fk = h*l;
   						double mj = Double.parseDouble(mMianji);
   						int count = (int)((mj*666.7)/fk);
   						
   						BigDecimal zmf = mMMDJ.multiply(new BigDecimal(count+""));
   			   		   	Tools.SetTextViewValueOnID(dialogView, R.id.et_zmf, zmf.toString());
   					}
   					
   		   			
   				}
   				catch(Exception ex)
   				{
   					Tools.ShowMessageBox(ex.getMessage());
   				}
   			}
   			else
   			{
   				Tools.ShowMessageBox("株行距未填写！");
   			}
   			
   		}
   		
   	}
   }
   
   	 class DXJEChanged implements TextWatcher 
	 {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		{
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) 
		{
			calcJinE();
			
		}

		@Override
		public void afterTextChanged(Editable s) 
		{
			
		}
	 
	 }
   
     private void calcJinE()
	 {
		 double bzbz = 0;
		 double zlmj = 0;
		 double bzdx = 0;
		 double zmf = 0;
		 double dxje = 0;
				 
		
		 try
		 {
			 zlmj = Double.parseDouble(Tools.GetTextValueOnID(dialogView, R.id.tv_mj));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 bzbz = Double.parseDouble(Tools.GetTextValueOnID(dialogView, R.id.et_zcbzbz));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 zmf = Double.parseDouble(Tools.GetTextValueOnID(dialogView, R.id.et_zmf));
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 try
		 {
			 bzdx = zlmj*bzbz;
			 Tools.SetTextViewValueOnID(dialogView, R.id.et_zcbz, bzdx+"");
			 
			 dxje =zmf+bzdx;
			 
			 Tools.SetTextViewValueOnID(dialogView, R.id.et_dxje, dxje+"");
		 }
		 catch(Exception ex)
		 {
			 
		 }
		 
		 
	 }
   
   private void showNeedFenhuList(ArrayList<HashMap<String,Object>> list)
	{
	   listView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
	   listView.SetOtherView(R.layout.tuigeng_duixian_needduixianlist);
	   listView.ReSetSize(0.5f,0.5f);
	   listView.SetCaption("未兑现分户");
		
		TuiGengFenhuAdapter fenhuAdapter = new TuiGengFenhuAdapter(dialogView.getContext(),
				list,
				R.layout.tuigeng_duixian_needduixianlist_item,
				new String[] {"xiangzhen","cun","xiaoban","huming","dilei","mianji","linzhong","shuzhong","miaomu","zhongzi"},
				new int[] {R.id.tv_xz,R.id.tv_jzc,R.id.tv_xbh,R.id.tv_hm, R.id.tv_dl, R.id.tv_mj,R.id.tv_lz,R.id.tv_sz});

		
		(((ListView)listView.findViewById(R.id.lvListNeedHu))).setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2,long arg3) {
				ListView lvList = (ListView)arg0;
				TuiGengFenhuAdapter la = (TuiGengFenhuAdapter)lvList.getAdapter();
				la.SetSelectItemIndex(arg2);
				la.notifyDataSetChanged();
				
	    		TuiGengData_AddFenHu addFenhu = new TuiGengData_AddFenHu();
				addFenhu.SetCallback(pCallback);
				HashMap<String,Object> seletItem = (HashMap<String,Object>)la.getItem(arg2);
				setDuixianEditInfo(-1,
										seletItem.get("xiangzhen")+"",
										seletItem.get("cun")+"",
										seletItem.get("xiaoban")+"",
										seletItem.get("huming")+"",
										seletItem.get("dilei")+"",
										seletItem.get("linzhong")+"",
										seletItem.get("shuzhong")+"",
										seletItem.get("miaomu")+"",
										seletItem.get("mianji")+"",
										mNianDu,
										"",
										"",
										"",
										"0",
										"是",
										"");
				
				listView.dismiss();
				
			}});
		
		(((ListView)listView.findViewById(R.id.lvListNeedHu))).setAdapter(fenhuAdapter);
		
		ShowSelectFenhu();
	}
   
   public void setDuixianEditInfo(int index,String xiangzhen,String cun,String xiaoban,String huming,String dilei,String linzhong,String shuzhong,String miaomu,String fenhuMJ,String niandu,String dxje,String buzhu,String biaozhun,String zhongmiao,String duixian,String beizhu)
	{	
		dxIndex= index;
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_xz, xiangzhen);
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_jzc, cun);
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_xbh, xiaoban);
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_hm, huming);
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_dl, dilei);
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_mj, fenhuMJ);
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_sz, shuzhong);
		Tools.SetTextViewValueOnID(dialogView, R.id.tv_lz, linzhong);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_dxnd, niandu);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_dxje, dxje);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_zmf, zhongmiao);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_zcbz, buzhu);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_zcbzbz, biaozhun);
		Tools.SetTextViewValueOnID(dialogView, R.id.et_bz, beizhu);
		mMianji = fenhuMJ;
		
		
		String sfdx = "是,否";
		ArrayAdapter<String> lzAdapter = new ArrayAdapter<String>(dialogView.getContext(),
				android.R.layout.simple_spinner_item,
				sfdx.split(","));
		lzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		((Spinner)dialogView.findViewById(R.id.sp_sfdx)).setAdapter(lzAdapter);
		Tools.SetSpinnerValueOnID(dialogView, R.id.sp_sfdx, duixian);
		
		ShowDialog();
		
	}
   
   
   public void ShowSelectFenhu()
   {
   	
	   listView.setOnShowListener(new OnShowListener()
	   {
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
			}
		});
	   listView.show();
   	
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
			String huming = Tools.GetTextValueOnID(dialogView, R.id.tv_hm);
			String dilei = Tools.GetTextValueOnID(dialogView, R.id.tv_dl);
			String fenhuMJ = Tools.GetTextValueOnID(dialogView, R.id.tv_mj);
			String shuzhong = Tools.GetTextValueOnID(dialogView, R.id.tv_sz);
			String linzhong = Tools.GetTextValueOnID(dialogView, R.id.tv_lz);
			String niandu = Tools.GetTextValueOnID(dialogView, R.id.et_dxnd);
			String dxje = Tools.GetTextValueOnID(dialogView, R.id.et_dxje);
			String zhongmiao = Tools.GetTextValueOnID(dialogView, R.id.et_zmf);
			String buzhu = Tools.GetTextValueOnID(dialogView, R.id.et_zcbz);
			String biaozhun = Tools.GetTextValueOnID(dialogView, R.id.et_zcbzbz);
			String beizhu = Tools.GetTextValueOnID(dialogView, R.id.et_bz);
			String sfdx = Tools.GetSpinnerValueOnID(dialogView, R.id.sp_sfdx);
			if(niandu.isEmpty()||dxje.isEmpty()||zhongmiao.isEmpty()||buzhu.isEmpty()||biaozhun.isEmpty())
			{
				Tools.ShowMessageBox("年度|兑现金额|种苗费|政策补助兑现|政策补助标准 必须填写！");
				return;
			}
			else
			{
				HashMap<String,Object> hm = new HashMap<String,Object>();
			
				hm.put("isSelect", false);
				hm.put("xiangcun", Tools.GetTextValueOnID(dialogView, R.id.tv_xz)+"-"+Tools.GetTextValueOnID(dialogView, R.id.tv_jzc));
				hm.put("xiaoban", Tools.GetTextValueOnID(dialogView, R.id.tv_xbh));
				hm.put("huming", huming);
				hm.put("dilei", dilei);
				hm.put("mianji", fenhuMJ);
				hm.put("shuzhong", shuzhong);
				hm.put("niandu", niandu);
				hm.put("linzhong", linzhong);
				hm.put("dxje", dxje);
				hm.put("zhongmiao", zhongmiao);
				hm.put("buzhu", buzhu);
				hm.put("biaozhun", biaozhun);
				hm.put("dxIndex", dxIndex);
				if(beizhu == null||beizhu.isEmpty())
				{
					beizhu = " ";
				}
				
				hm.put("beizhu", beizhu);
				hm.put("sfdx", sfdx);
				
				if(mCallback != null)
				{
					if(dxIndex>-1)
					{
						mCallback.OnClick("编辑兑现", hm);
					}
					else
					{
						mCallback.OnClick("新增兑现", hm);
					}
					
					dialogView.dismiss();
				}
				
			}
			
		}
	};
	
	public void ShowDialog()
    {
    	
		dialogView.setOnShowListener(new OnShowListener()
		{
			@Override
			public void onShow(DialogInterface dialog) 
			{
				
			}}
    	);
		dialogView.show();
    	
    }
}
