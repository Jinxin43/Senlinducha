package com.dingtu.DTGIS.LDBG;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DictXZQH;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.DataBindOfKeyValue;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkFieldType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class LDBGDataObject extends v1_CGpsDataObject 
{
	private String shenCode = "";
	private String dishiCode = "";
	private String xianCode ="";
	private String xiangCode ="";
	private String cunCode ="";
	private String linyejuCode ="";
	private String linchangCode ="";
	
	public LDBGDataObject()
	{
		super();
	}
	@Override
	public void ReadDataAndBindToView(String Where)
	{
    	ReadDataAndBindToView(Where,"","");
	}
	
	@Override
	public void ReadDataAndBindToView(String Where, String layerID,String layerType)
	{
        List<String> FeatureList = new ArrayList<String>();
        SQLiteDataReader DR = this.m_Dataset.getDataSource().Query("select * from "+this.m_Dataset.getDataTableName()+" where " + Where);
        if (DR==null) return;
        if (DR.Read()) 
        {
        	String[] FieldNameList = DR.GetFieldNameList();
        	for(String FieldName : FieldNameList)
        	{
        		if (FieldName.equals("SYS_ID")) this.SYS_ID = Integer.parseInt(DR.GetString(FieldName));
        		if (FieldName.equals("SYS_OID")) this.SYS_OID = DR.GetString(FieldName);
        		//if (FieldName.equals("SYS_LABEL")) this.SYS_LABEL = DR.GetString(FieldName);
        		if (FieldName.equals("SYS_DATE")) this.SYS_DATE = DR.GetString(FieldName);
        		if (FieldName.equals("SYS_PHOTO"))
        		{
        			this.SYS_PHOTO = DR.GetString(FieldName);
        		}
        			
        		if (FieldName.equals("SYS_GEO"))continue;  //��ȡͼ��
        		
        		String FValue = DR.GetString(FieldName);
        		if (FValue==null)FValue="";
        		FeatureList.add(FieldName+","+FValue);
        	}
    		this.SetFeatureList(FeatureList,layerID,layerType);
        }DR.Close();
        this.RefreshDataToView();    //������ˢ�µ��ؼ���
	}
	
	@Override
	 public void SetFeatureList(List<String> FeatureList,String layerID,String layerType)
	    {
	    	for(int i=0;i<FeatureList.size();i++)
	    	{
	    		String[] fv = FeatureList.get(i).split(",");
	    		String FieldName = fv[0];
	    		String FieldValue = (fv.length!=2?"":fv[1]);
	    		
	    		if(layerType != null && layerType.length()>0)
	    		{
	    			List<v1_LayerField> _FieldList  = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID).GetFieldList();
	    			for(v1_LayerField field:_FieldList)
	    			{
	    				if(field.GetDataFieldName().equals(FieldName))
	    				{
	    					if(field.GetFieldEnumCode() != null && field.GetFieldEnumCode().length()>0)
	    					{
	    						//FieldValue = PubVar.m_DoEvent.m_DictDataDB.getEnumItenWithCode("��ҵ", layerType, field.GetFieldEnumCode(),FieldValue);
	    						List<String> enumList = PubVar.m_DoEvent.m_DictDataDB.getEnumList(layerType, field.GetFieldEnumCode());
	    						if(enumList != null)
	    						{
	    							String value = "("+FieldValue+")";
	    							for(String strValue:enumList)
	    							{
	    								if(strValue.contains(value))
	    								{
	    									FieldValue = strValue;
	    									break;
	    								}
	    							}
	    							
//	    							String value = enumList.get(FieldValue);
//	    							if(value != null)
//	    							{
//	    								FieldValue = "("+FieldValue+")"+value;
//	    							}
	    						}
	    						
	    					}
	    					
	    					if(field.GetFieldType() == lkFieldType.enFloat)
	    					{
	    						if(FieldValue.toUpperCase().contains("E"))
	    						{
	    							try
	    							{
	    								BigDecimal bd = new BigDecimal(FieldValue);
	    								FieldValue = bd.toPlainString();
	    							}
	    							catch(Exception ex)
	    							{
	    							}
	    						}
	    					}
	    					continue;
	    					
	    				}
	    				
	    			}
	    		}
	    		
	    			
	    		SetDataBindItemValue(FieldName,FieldValue);
//	    		SetDataBindItemValue(FieldName,FieldValue);
	    		//this.DataBindList.get(i).Value=FeatureList.get(i);
	    	}
	    }
	
	private DictXZQH xzqhDB = null;
	private String getXZQHname(String value,String key)
	{
		if(xzqhDB == null)
		{
			xzqhDB = new DictXZQH();
		}
		return xzqhDB.getNameByCode(value,key);
	}
	
	private String getXZQHCode(String name,String level,String parCode)
	{
		if(xzqhDB == null)
		{
			xzqhDB = new DictXZQH();
		}
		return xzqhDB.getCodeByName(name, level, parCode);
	}
	
	private String mXianCode = "";
	private String mXiangCode = "";

	public void SetDataBindItemValue2(String key,String value)
	{
		for(DataBindOfKeyValue DKV:this.DataBindList)
		{
			if (DKV.DataKey.equals(key))
			{
				
				DKV.Value=value;
				if(DKV.Key.equals("ʡ"))
				{
					DKV.Value=getXZQHname(value, "ʡ");
					return;
				}
				
//				if(DKV.Key.equals("����"))
//				{
//					DKV.Value=getXZQHname(value, "����");
//					return;
//				}
				
				if(DKV.Key.equals("��"))
				{
					String xName = getXZQHname(value, "��");
					if(xName.length()>0)
					{
						mXianCode = value;
						DKV.Value = xName;
					}
					else
					{
						DKV.Value = value;
					}
					return;
				}
				
				if(DKV.Key.equals("��ҵ��"))
				{
					if(value.equals("000000"))
					{
						DKV.Value= value;
					}
					else
					{
						
						String xName = getXZQHname(value, "��");
						if(xName.length()>0)
						{
							mXianCode = value;
							DKV.Value = xName;
						}
						else
						{
							DKV.Value = value;
						}
						return;
					}
				}
				
				if(DKV.Key.equals("��"))
				{
					String xName = getXZQHname(mXianCode+value, "��");
					if(xName.length()>0)
					{
						mXiangCode = mXianCode+value;
						DKV.Value = xName;
					}
					else
					{
						DKV.Value = value;
					}
					return;
				}
				
				if(DKV.Key.equals("�ֳ�"))
				{
					if(value.equals("000"))
					{
						DKV.Value= value;
					}
					else
					{
						String xName = getXZQHname(mXianCode+value, "��");
						if(xName.length()>0)
						{
							mXiangCode = xName+value;
							DKV.Value = xName;
						}
						else
						{
							DKV.Value = value;
						}
						return;
					}
				}
				
				if(DKV.Key.equals("��"))
				{
					String cunName=getXZQHname(mXiangCode+value, "��");
					if(cunName.length()>0)
					{
						DKV.Value = cunName;
					}
					else
					{
						DKV.Value=value;
					}
					return;
				}
			}
		}
	}
	
//	@Override
//	public void RefreshViewValueToData()
//	{
//		for(DataBindOfKeyValue KV:DataBindList)
//		{
//			KV.Value = Tools.GetViewValue(KV.ViewControl);
//			
//			
//			
////			if (KV.ViewControl!=null)
////			{
////				if(KV.Key.equals("ʡ")||KV.Key.equals("��")||KV.Key.equals("��ҵ��")||KV.Key.equals("��")||KV.Key.equals("�ֳ�")||KV.Key.equals("��"))
////				{
////					//TODO������������Ϣ��ʱ�����޸�
////				}
////				else
////				{
////					KV.Value = Tools.GetViewValue(KV.ViewControl);
////				}
////				
////			}
//		}
//	}
}
