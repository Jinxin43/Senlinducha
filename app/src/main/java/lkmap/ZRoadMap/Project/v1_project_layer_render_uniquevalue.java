package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Layer.GeoLayer;
import lkmap.Render.UniqueValueRender;
import lkmap.Symbol.ISymbol;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_ImageSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class v1_project_layer_render_uniquevalue
{
	private v1_FormTemplate _Dialog = null; 
	
    public v1_project_layer_render_uniquevalue()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_uniquevalue);
    	_Dialog.ReSetSize(PubVar.m_WindowScaleW, 0.96f);
    	
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	
    	//ɾ������ȡֵ���Զ��尴ť
    	_Dialog.findViewById(R.id.pln_del).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pln_queryvalue).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.pln_customvalue).setOnClickListener(new ViewClick());
    	
    	//ѡ���ֶ����
    	v1_SpinnerDialog vsd = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_fieldlist);
    	vsd.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				v1_project_layer_render_uniquevalue_selectfield pnl = new v1_project_layer_render_uniquevalue_selectfield();
				pnl.SetEditLayer(m_EditLayer);
				pnl.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_UniqueFieldList = (List<v1_LayerField>)ExtraStr;
						List<String> FieldNameList = new ArrayList<String>();
						for(v1_LayerField LF:m_UniqueFieldList)FieldNameList.add(LF.GetFieldName());
						v1_DataBind.SetBindListSpinner(_Dialog, "", new String[]{Tools.JoinT(",", FieldNameList)}, R.id.sp_fieldlist);
						FillUniqueValueList(m_UniqueFieldList);
					}});
				pnl.ShowDialog();
			}});
    	
    	
    }
    
    //���ð�ť��״̬
    private void SetButtonEnable(boolean enabled)
    {
//    	_Dialog.findViewById(R.id.pln_edit).setEnabled(enabled);
//    	_Dialog.findViewById(R.id.pln_delete).setEnabled(enabled);
//    	((TextView)_Dialog.findViewById(R.id.tv_edit)).setTextColor((enabled?Color.BLACK:Color.GRAY));
//    	((TextView)_Dialog.findViewById(R.id.tv_delete)).setTextColor((enabled?Color.BLACK:Color.GRAY));
    }

    
    class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		DoCommand(Tag);
    	}
    }
    //��ť�¼�
    private void DoCommand(String StrCommand)
    {
    	if (StrCommand.equals("ɾ��"))
    	{
    		int DeleteCount = 0;
    		int ItemCount = this.m_ValueSymbolList.size();
    		for(int i=ItemCount-1;i>=0;i--)
    		{
    			HashMap<String,Object> hm = this.m_ValueSymbolList.get(i);
    			if (Boolean.parseBoolean(hm.get("D1")+""))
    			{
    				DeleteCount++;
    				this.m_ValueSymbolList.remove(i);
    			}
    		}
    		if (DeleteCount==0)
    		{
    			Tools.ShowToast(_Dialog.getContext(), "�����б��ڹ�ѡ��Ҫɾ������Ŀ��");
    			return;
    		} else this.m_HVF.notifyDataSetInvalidated();
    		
    	}
    	if (StrCommand.equals("��ȡֵ"))
    	{
    		if (m_UniqueFieldList.size()==0)
    		{
    			Tools.ShowToast(_Dialog.getContext(), "��ѡ���ֶ���ϣ�");
    			return;
    		}
    		FillUniqueValueList(m_UniqueFieldList);
    	}
    	if (StrCommand.equals("�Զ���"))
    	{
    		v1_project_layer_render_uniquevalue_customvalue ruc = new v1_project_layer_render_uniquevalue_customvalue();
    		ruc.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
					int W = 64,H=40;
					v1_SymbolObject SO = new v1_SymbolObject();
		    		if (m_EditLayer.GetLayerType()==lkGeoLayerType.enPoint)
		    		{
		    			PointSymbol PS = new PointSymbol();
		    			SO.SymbolBase64Str = PS.ToBase64();
		    			SO.SymbolFigure = PS.ToFigureBitmap(W, H);
		    		}
		    		if (m_EditLayer.GetLayerType()==lkGeoLayerType.enPolyline)
		    		{
		    			LineSymbol PS = new LineSymbol();
		    			SO.SymbolBase64Str = PS.ToBase64();
		    			SO.SymbolFigure = PS.ToFigureBitmap(W*2, H);
		    		}
		    		if (m_EditLayer.GetLayerType()==lkGeoLayerType.enPolygon)
		    		{
		    			PolySymbol PS = new PolySymbol();
		    			SO.SymbolBase64Str = PS.ToBase64();
		    			SO.SymbolFigure = PS.ToFigureBitmap(W, H);
		    		}
		        	HashMap<String,Object> hm = new HashMap<String,Object>();
		        	hm.put("D1", false);
		        	hm.put("D2", ExtraStr+"");
		        	hm.put("D3", SO.SymbolFigure);
		        	hm.put("SymbolObject",SO);
		        	hm.put("Index",m_ValueSymbolList.size());
		        	m_ValueSymbolList.add(hm);
		        	m_HVF.notifyDataSetInvalidated();
				}});
    		ruc.ShowDialog();
    	}
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (SaveLayerInfo())
	    		{
	    			if (m_Callback!=null)m_Callback.OnClick("��ֵ��Ⱦ", null);
	    			_Dialog.dismiss();
	    		}
	    	}

	    	//ѡ��Ψһֵ�����б��Ļص�
	    	if (Str.equals("�б�ѡ��"))
	    	{
	    		final HashMap<String,Object> m_SelectItem = (HashMap<String,Object>)ExtraStr;
	    		v1_project_layer_render_symbolexplorer rs = new v1_project_layer_render_symbolexplorer();
	    		rs.SetDefaultSymbolObject((v1_SymbolObject)m_SelectItem.get("SymbolObject"));
	    		rs.SetGeoLayerType(m_EditLayer.GetLayerType());
	    		rs.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_SymbolObject SO1 = (v1_SymbolObject)ExtraStr;
						m_ValueSymbolList.remove(m_SelectItem);
			        	HashMap<String,Object> newHM = new HashMap<String,Object>();
			        	newHM.put("D1", m_SelectItem.get("D1"));
			        	newHM.put("D2", m_SelectItem.get("D2"));
			        	newHM.put("D3", SO1.SymbolFigure);
			        	newHM.put("SymbolObject",SO1);
			        	newHM.put("Index", m_SelectItem.get("Index"));
			        	m_ValueSymbolList.add(Integer.parseInt(m_SelectItem.get("Index")+""), newHM);
						m_HVF.notifyDataSetInvalidated();
					}});
	    		rs.ShowDialog();
	    	}
			
		}};

	//�ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}

	//ѡ������ֶ�����б�
	private List<v1_LayerField> m_UniqueFieldList = new ArrayList<v1_LayerField>();
	
	
	private v1_Layer m_EditLayer = null;
	/**
	 * ���õ�ǰ���ڱ༭��ͼ��
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr) 
	{
		this.m_EditLayer = lyr;
		this._Dialog.SetCaption("��"+lyr.GetLayerAliasName()+"����ֵ����");
	}
	
	/**
	 * ͨ���ֶ��б��ѯΨһֵ�б�
	 * @param uniqueFieldList
	 */
	private void FillUniqueValueList(List<v1_LayerField> uniqueFieldList)
	{
		List<String> ValueList = new ArrayList<String>();
		List<v1_SymbolObject> SymbolList = new ArrayList<v1_SymbolObject>();
		List<String> DataFieldList = new ArrayList<String>();
		for(v1_LayerField LF:uniqueFieldList)DataFieldList.add(LF.GetDataFieldName());
		if (DataFieldList.size()>0)
		{
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(this.m_EditLayer.GetLayerID());
			String SQL = "select distinct (%1$s) from %2$s where SYS_STATUS='0'";
			SQL = String.format(SQL, Tools.JoinT("||','||", DataFieldList),pDataset.getDataTableName());
			SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
			if (DR!=null)
			{
				while(DR.Read())
				{
					ValueList.add(DR.GetString(0));
				}DR.Close();
			}
			
			//��������
			if (ValueList.size()>0)
			{
				int symbolIdx = 0;
				List<v1_SymbolObject> symbolList = PubVar.m_DoEvent.m_ConfigDB.GetSymbolExplorer().GetSymbolObjectList(null, pDataset.getType());
				for(int i=0;i<ValueList.size();i++)
				{
//					if (symbolIdx<symbolList.size())SymbolList.add(symbolList.get(i));
					if (symbolIdx<symbolList.size())
					{
						SymbolList.add(symbolList.get(symbolIdx));
						symbolIdx++;
					}
					else 
					{
						symbolIdx = 0;
						continue;
					}
					
				}
			}
		}
		//�󶨵��б�
		this.LoadUniqueValueInfo(ValueList, SymbolList);
		
		
	}
	
	/**
	 * ����ͼ���ֵ��Ⱦ��Ϣ
	 * @return
	 */
	private boolean SaveLayerInfo()
	{
		//��֤�ֶ����
		if (this.m_UniqueFieldList.size()==0)
		{
			Tools.ShowMessageBox(this._Dialog.getContext(), "��ѡ���ֶ���ϣ�");
			return false;
		}
		
		//��ֵ֤��
		if (this.m_ValueSymbolList.size()==0)
		{
			Tools.ShowMessageBox(this._Dialog.getContext(), "����д���ֵ�����б�");
			return false;
		}
		
		//����ͼ���ֵ��Ⱦ
		List<String> FieldList = new ArrayList<String>();
		for(v1_LayerField LF:this.m_UniqueFieldList)FieldList.add(LF.GetDataFieldName());
		
		List<String> ValueList = new ArrayList<String>();
		List<String> SymbolList = new ArrayList<String>();
		for(HashMap<String,Object> hm:this.m_ValueSymbolList)
		{
			ValueList.add(hm.get("D2")+"");
			SymbolList.add(((v1_SymbolObject)hm.get("SymbolObject")).SymbolBase64Str);
		}
		this.m_EditLayer.GetUniqueSymbolInfoList().put("UniqueValueField", FieldList);
		this.m_EditLayer.GetUniqueSymbolInfoList().put("UniqueValueList", ValueList);
		this.m_EditLayer.GetUniqueSymbolInfoList().put("UniqueSymbolList", SymbolList);
		
		
		v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_defaultsymbol);
		this.m_EditLayer.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", ISD.GetSelectSymbolObject().SymbolBase64Str);
		return true;
	}
	
	
	private v1_HeaderListViewFactory m_HVF = null;
	private List<HashMap<String,Object>> m_ValueSymbolList = null;
    /**
     * ��Ψһֵ������б�
     */
    private void LoadUniqueValueInfo(List<String> ValueList,List<v1_SymbolObject> SymbolList)
    {
    	//��ͼ���б�
    	if (this.m_HVF==null)
    	{
    		this.m_HVF = new v1_HeaderListViewFactory();
    		this.m_HVF.SetHeaderListView(_Dialog.findViewById(R.id.in_listview), "��ֵ��Ⱦ",pCallback);
    		this.m_ValueSymbolList = new ArrayList<HashMap<String,Object>>();
    	}
		
    	this.m_ValueSymbolList.clear();
    	
		//��Ψһֵ������б�
    	for(int i=0;i<ValueList.size();i++)
    	{
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", false);
        	hm.put("D2", ValueList.get(i));
//        	hm.put("D3", SymbolList.get(i).SymbolFigure);
//        	hm.put("SymbolObject",SymbolList.get(i));
        	hm.put("D3", SymbolList.get(i%SymbolList.size()).SymbolFigure);
        	hm.put("SymbolObject",SymbolList.get(i%SymbolList.size()));
        	
        	hm.put("Index",i);
        	this.m_ValueSymbolList.add(hm);
    	}
    	this.m_HVF.BindDataToListView(this.m_ValueSymbolList,pCallback);
    }
    
    /**
     * ����ͼ���Ψһֵ������Ϣ
     */
    private void LoadDefaultSet()
    {
    	int W = 64,H=40;if (this.m_EditLayer.GetLayerType()==lkGeoLayerType.enPolyline)W=100;
    	
    	//Ψһֵ�ֶ����
    	List<String> uniqueValueFieldList = (List<String>)this.m_EditLayer.GetUniqueSymbolInfoList().get("UniqueValueField");
    	List<String> FieldNameList = new ArrayList<String>();
    	for(String DataFieldName:uniqueValueFieldList)   //�������ֶ���ת��Ϊ��������
    	{
	    	for(v1_LayerField LF:this.m_EditLayer.GetFieldList())
	    	{
	    		if (LF.GetDataFieldName().equals(DataFieldName))
    			{
    				this.m_UniqueFieldList.add(LF);
    				FieldNameList.add(LF.GetFieldName());
    			}
	    	}
    	}
    	v1_DataBind.SetBindListSpinner(_Dialog, "", new String[]{Tools.JoinT(",", FieldNameList)}, R.id.sp_fieldlist);
    	
    	//Ψһֵ�������б�
    	List<String> uniqueValueList = (List<String>)this.m_EditLayer.GetUniqueSymbolInfoList().get("UniqueValueList");
    	List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
    	List<String> pSymbolStrList = (List<String>)this.m_EditLayer.GetUniqueSymbolInfoList().get("UniqueSymbolList");
    	for(String pSymbolStr :pSymbolStrList)
    	{
    		v1_SymbolObject SO = new v1_SymbolObject();
    		if (this.m_EditLayer.GetLayerType()==lkGeoLayerType.enPoint)
    		{
    			PointSymbol PS = new PointSymbol();
    			PS.CreateByBase64(pSymbolStr);
    			SO.SymbolBase64Str = PS.ToBase64();
    			SO.SymbolFigure = PS.ToFigureBitmap(W, H);
    		}
    		if (this.m_EditLayer.GetLayerType()==lkGeoLayerType.enPolyline)
    		{
    			LineSymbol PS = new LineSymbol();
    			PS.CreateByBase64(pSymbolStr);
    			SO.SymbolBase64Str = PS.ToBase64();
    			SO.SymbolFigure = PS.ToFigureBitmap(W, H);
    		}
    		if (this.m_EditLayer.GetLayerType()==lkGeoLayerType.enPolygon)
    		{
    			PolySymbol PS = new PolySymbol();
    			PS.CreateByBase64(pSymbolStr);
    			SO.SymbolBase64Str = PS.ToBase64();
    			SO.SymbolFigure = PS.ToFigureBitmap(W, H);
    		}
    		
    		SOList.add(SO);
    	}
    	this.LoadUniqueValueInfo(uniqueValueList, SOList);
    	
    	//Ĭ��ֵ����
    	String DefaultSymbolBase64Str = this.m_EditLayer.GetUniqueSymbolInfoList().get("UniqueDefaultSymbol")+"";
    	v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_defaultsymbol);
    	List<v1_SymbolObject> DefaultSOList = new ArrayList<v1_SymbolObject>();
    	v1_SymbolObject DefaultSO = new v1_SymbolObject();
    	
		if (this.m_EditLayer.GetLayerType()==lkGeoLayerType.enPoint)
		{
			PointSymbol PS = new PointSymbol();
			if (!DefaultSymbolBase64Str.equals(""))PS.CreateByBase64(DefaultSymbolBase64Str);
			DefaultSO.SymbolBase64Str = PS.ToBase64();
			DefaultSO.SymbolFigure = PS.ToFigureBitmap(W, H);
		}
		if (this.m_EditLayer.GetLayerType()==lkGeoLayerType.enPolyline)
		{
			LineSymbol PS = new LineSymbol();
			if (!DefaultSymbolBase64Str.equals(""))PS.CreateByBase64(DefaultSymbolBase64Str);
			DefaultSO.SymbolBase64Str = PS.ToBase64();
			DefaultSO.SymbolFigure = PS.ToFigureBitmap(W*2, H);
		}
		if (this.m_EditLayer.GetLayerType()==lkGeoLayerType.enPolygon)
		{
			PolySymbol PS = new PolySymbol();
			if (!DefaultSymbolBase64Str.equals(""))PS.CreateByBase64(DefaultSymbolBase64Str);
			DefaultSO.SymbolBase64Str = PS.ToBase64();
			DefaultSO.SymbolFigure = PS.ToFigureBitmap(W, H);
		}
    	DefaultSOList.add(DefaultSO);
    	ISD.SetImageItemList(DefaultSOList);
    	ISD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
	    		v1_project_layer_render_symbolexplorer plrs = new v1_project_layer_render_symbolexplorer();
	    		plrs.SetGeoLayerType(m_EditLayer.GetLayerType());
	    		plrs.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
			    	   	v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_defaultsymbol);
			    	   	List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
			    	   	SOList.add((v1_SymbolObject)ExtraStr);
			        	ISD.SetImageItemList(SOList);
					}});
	    		v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_defaultsymbol);
	    		if (ISD.GetSelectSymbolObject()!=null)
	    		{
	    			plrs.SetDefaultSymbolObject(ISD.GetSelectSymbolObject());
	    		}
	    		plrs.ShowDialog();
			}});    	
    }
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadDefaultSet();}});
    	_Dialog.show();
    }
    

}
