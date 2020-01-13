package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lkmap.Dataset.DataSource;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.drm.DrmRights;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;

public class TanHuiJianChiEdit 
{
	private v1_FormTemplate _Dialog = null; 
	
	private String mode = "new";
	
	
	public void SetEditMode(String pMode,String pShuzhu,String pXiongjing,String pXujiliang)
	{
		mode = pMode;
		if(mode=="edit")
		{
			Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_shuzhong,pShuzhu);
			Tools.SetTextViewValueOnID(_Dialog, R.id.et_xiongjing, pXiongjing);
			Tools.SetTextViewValueOnID(_Dialog, R.id.evXujiliang, pXujiliang);
		}
	}
	
	private v1_Layer  mCurrentLayer = null;
	public void SetCurrentLayer(v1_Layer layer)
	{
		mCurrentLayer = layer;
	}
	
	private int mCurrentSysID = 0;
	public void SetSysID(int sysID)
	{
		mCurrentSysID = sysID;
	}
	
	private String mXiaoBanHao = null;
	public void SetXiaoBanHao(String xiaobanhao)
	{
		mXiaoBanHao= xiaobanhao;
	}
	
	private String mYangDiHao = null;
	public void SetYangDiHao(String pYangDiHao)
	{
		mYangDiHao = pYangDiHao;
	}
	
	private String mBiaoZhunDiHao = null;
	public void SetBiaoZhunDihao(String pBiaoZhunDiHao)
	{
		mBiaoZhunDiHao = pBiaoZhunDiHao;
	}
	
	private String mXiongJing = null;
	public TanHuiJianChiEdit()
	{
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.tanhuijianchiedit);
    	_Dialog.SetCaption(Tools.ToLocale("每木检尺登记"));
    	//_Dialog.ReSetSize(1f,-1f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	
    	BindShuZhong();
    	EditText  etXiongJing = (EditText)_Dialog.findViewById(R.id.et_xiongjing);
    	
    	etXiongJing.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) 
			{
				CalcXujiliang();  //创建预览图
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}
		});
    	
    	Spinner shuzhong = (Spinner)_Dialog.findViewById(R.id.sp_shuzhong);
    	shuzhong.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
    			{
		    		@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
					{
		    			CalcXujiliang();
					}
		
					@Override
					public void onNothingSelected(AdapterView<?> arg0) {}
		    			});
	}
	
	private String mShuZhong = "";
	private String mShuZhongCode = "";
	private int zhushu = 0;
	
	private void CalcXujiliang()
	{
		EditText  etXiongJing = (EditText)_Dialog.findViewById(R.id.et_xiongjing);
		try
		{
			mXiongJing = etXiongJing.getText().toString();
			if(mXiongJing.isEmpty())
			{
				return;
			}
			
			double cmXiongJing = Double.parseDouble(mXiongJing);
		 
			int mmXiongijng =(int)(Math.ceil(cmXiongJing)*10);
			mShuZhongCode= ((Spinner)_Dialog.findViewById(R.id.sp_shuzhong)).getSelectedItem().toString();
			
			String pCaijishi = PubVar.m_DoEvent.m_ConfigDB.QueryCaijishi(mShuZhongCode, "6109");
			int caijishi = Integer.parseInt(pCaijishi);
			int Caiji = PubVar.m_DoEvent.m_ConfigDB.QuaryCaiji(mmXiongijng,caijishi);
			double dCaiji = Double.parseDouble(Caiji+"")/1000;
			String strCaiji = Caiji+"";
			Tools.SetTextViewValueOnID(_Dialog, R.id.evXujiliang, String.valueOf(dCaiji));
		}
		catch(Exception ex)
		{
			Tools.ShowToast(PubVar.m_DoEvent.m_Context, ex.getMessage());
			Tools.SetTextViewValueOnID(_Dialog, R.id.evXujiliang, "");
		}
	}
	
	private String GetXujiliang()
	{
		return Tools.GetTextValueOnID(_Dialog, R.id.evXujiliang);
	}
	
	//绑定树种列表
	private void BindShuZhong()
	{
		List<String> ankangShuzhong =  PubVar.m_DoEvent.m_ConfigDB.ReadShuZhong("6109");
		v1_DataBind.SetBindListSpinner(_Dialog,"树种类别",ankangShuzhong,R.id.sp_shuzhong);
	}
	
	 private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("确定"))
	    	{
	    		if (SaveJianChiDetail())
				{
	    			if (m_Callback!=null)
    				{
    					m_Callback.OnClick("新增", null);
    				}
					_Dialog.dismiss();
				}
	    		else
	    		{
	    			Tools.ShowToast(PubVar.m_DoEvent.m_Context, "保存每木检尺表失败");
	    		}
	    	}
		}
	};
	
	
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	private boolean SaveFieldDetailInfo()
	{
		if(GetXujiliang().isEmpty())
		{
			return false;
		}
		
		String sql="";
		if(mode == "edit")
		{
			sql = "update T_MeiMuJianChi set ShuZhongCode='"+mShuZhongCode+"', XiongJing='"+mXiongJing+"'"+", XuJiLiang="+GetXujiliang()
					+" where YangDiHao='"+mYangDiHao+"' and "+
					"XiaoBanhao ='"+mXiaoBanHao+"' and BiaoZhunDiHao = '"+mBiaoZhunDiHao+"' and JianChiCode="+mCode;
		}
		else
		{
			sql = "insert into T_MeiMuJianChi (JianChiID,YangDiHao,BiaoZhunDiHao,XiaoBanHao,JianChiCode,ShuZhongCode,Shuzhong,XiongJing,XuJiLiang) values"+
						"('%1$s','%2$s','%3$s','%4$s','%5$s', '%6$s','%7$s','%8$s', '%9$s')";
			   		  		
			sql = String.format(sql,mYangDiHao+mXiaoBanHao+mBiaoZhunDiHao+mCode,mYangDiHao,mBiaoZhunDiHao,mXiaoBanHao,
								mCode,mShuZhongCode,"",mXiongJing,GetXujiliang());
		}
		
		return PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().ExcuteSQL(sql);
	}
	
	private boolean SaveJianChiDetail()
	{
		if(GetXujiliang().isEmpty())
		{
			return false;
		}
		
    	String sql = "select * from "+mCurrentLayer.GetDataTableName()+" where SYS_ID ="+mCurrentSysID;
    	SQLiteDataReader DR = getDataSourceDB().Query(sql);
    	String jianchiValue = "";
    	String updateSql = "";
    	Double xuji = 0d;
    	
    	if(DR.Read()) 
    	{
    		if(mode == "edit")
    		{
    			for (int i=PubVar.MinTanhuiIndex;i<PubVar.maxTanhuiIndex;i++) 
    			{
    				jianchiValue = DR.GetString("F"+i);
    				if(jianchiValue.isEmpty())
    				{
    					continue;
    				}
    				
    				String[] all = jianchiValue.split(";");
    				boolean isFind = false;
    				String newString = "";
        			for(String per:all)
        			{
        				
        				try
        				{
        					HashMap<String,Object> hm = new HashMap<String,Object>();
        					String[] properties = per.split(",");
        					if(properties[0].equals(String.valueOf(mCode)))
        					{
        						isFind = true;
        						xuji=DR.GetDouble("F6");
        						xuji= xuji-Double.valueOf(properties[4])+Double.valueOf(GetXujiliang());
        						
            					newString += properties[0]+","+mShuZhongCode+","+PubVar.m_DoEvent.m_ConfigDB.QueryShuzhong(mShuZhongCode, "6109")+","+mXiongJing+","+GetXujiliang()+";";
            					
        					}
        					else
        					{
        						
            					newString += per+";";
        					}
        					
        				}
        				catch(Exception e)
        				{
        					Tools.ShowToast(_Dialog.getContext(), e.getMessage());
        				}
        			}
        			
	        			if(isFind)
	        			{
	        				updateSql = "update "+mCurrentLayer.GetDataTableName()+" set F"+i+" = '"+newString +"',F6="+xuji+" where SYS_ID = "+mCurrentSysID;
	        				break;
	        			}
    				}
    			  
    			}
	    		else 
	    		{
	    			int zhushu= DR.GetInt32("F5");
					String newString = mCode+","+mShuZhongCode+","+PubVar.m_DoEvent.m_ConfigDB.QueryShuzhong(mShuZhongCode, "6109")+","+mXiongJing+","+GetXujiliang()+";";
					for (int i=PubVar.MinTanhuiIndex;i<PubVar.maxTanhuiIndex;i++ ) 
	    			{
	    				String currentF = DR.GetString("F"+i);
	    				String nextF = DR.GetString("F"+(i+1));
	    				xuji = DR.GetDouble("F6")+Double.valueOf(GetXujiliang());
	    				if(currentF.isEmpty())
	    				{
	    					updateSql = "update "+mCurrentLayer.GetDataTableName()+" set F"+i+" = '"+currentF+newString +"',F5="+(zhushu+1)+", F6="+xuji+" where SYS_ID = "+mCurrentSysID;;
	    					break;
	    				}
	    				else if (nextF.isEmpty()) 
	    				{
	    					if(currentF.length()+newString.length()<=224)
	    					{
	    						updateSql = "update "+mCurrentLayer.GetDataTableName()+" set F"+i+" = '"+currentF+newString +"',F5="+(zhushu+1)+", F6="+xuji+" where SYS_ID = "+mCurrentSysID;;
	    						break;
	    					}
	    					
						}
	    			}
					
				}
    		}
    	
    	DR.Close();
    	
    	if(!updateSql.isEmpty())
    	{
    		return getDataSourceDB().ExcuteSQL(updateSql);
    	}
    	
    	return false;
	}

	
	private int mCode = 1;
	public void SetCode(int code)
	{
		mCode = code;
		Tools.SetTextViewValueOnID(_Dialog, R.id.evCode, String.valueOf(code));
	}
	
	public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {}});
    	_Dialog.show();
    }
	
	public DataSource getDataSourceDB()
	{
		DataSource pDataSource = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName());
		return pDataSource;
	}
}
