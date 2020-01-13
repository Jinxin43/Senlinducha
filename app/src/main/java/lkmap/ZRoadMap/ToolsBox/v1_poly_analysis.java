package lkmap.ZRoadMap.ToolsBox;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GeoLayers;
import lkmap.Spatial.SpatialAnalysisTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class v1_poly_analysis
{
	private v1_FormTemplate _Dialog = null; 

    public v1_poly_analysis()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_poly_analysis);
    	_Dialog.ReSetSize(0.7f,0.90f);
    	
    	//���ñ���
    	_Dialog.SetCaption(Tools.ToLocale("���ص�����"));
    	_Dialog.SetButtonInfo("2,"+R.drawable.v1_cat+",����  ,����", pCallback);
    	_Dialog.SetButtonInfo("1,"+R.drawable.m_option+","+Tools.ToLocale("����")+" ,����", pCallback);
    	_Dialog.findViewById(R.id.pln_recal).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {pCallback.OnClick("���¼���", "");}});
			
    }
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("����"))
	    	{
	    		v1_poly_analysis_set pas = new v1_poly_analysis_set();
	    		pas.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						pCallback.OnClick("���¼���", null);
					}});
	    		pas.ShowDialog();
	    	}
	    	
	    	if (Str.equals("����"))
	    	{
				v1_poly_analysis_saveresult dss = new v1_poly_analysis_saveresult(_Dialog.getContext());
				dss.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) 
					{
						HashMap<String,String> savePara = (HashMap<String,String>)ExtraStr;
						String txtPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/���ݵ���";
						String txtName = txtPath+"/"+savePara.get("����")+".txt";
						if (!Tools.ExistFile(txtPath))(new File(txtPath)).mkdirs();
						
						List<String> saveResultList = new ArrayList<String>();
						saveResultList.add("����ʱ�䣺"+Tools.GetSystemDate());
						saveResultList.add("���˵����"+savePara.get("˵��"));
						saveResultList.add("ѡ�������"+Tools.GetTextValueOnID(_Dialog, R.id.tvresult));
						saveResultList.add("ͼ������     ͳ����Ŀ     ���     ����");
						for(HashMap<String,Object> hm:m_AnalysisResultList)
						{
							String resultStr = hm.get("D1")+"     "+hm.get("D2")+"     "+hm.get("D3")+"     "+hm.get("D4");
							saveResultList.add(resultStr);
						}
						try
						{
		    				FileWriter filenew= new FileWriter(txtName);
		    				BufferedWriter bw= new BufferedWriter(filenew);
						    bw.write(Tools.JoinT("\r\n", saveResultList));
						    bw.close();
						    Tools.ShowToast(_Dialog.getContext(), "������ɹ����棡\n�洢��"+txtName);
						    return;
						}
						catch(Exception e){}

						Tools.ShowMessageBox(_Dialog.getContext(), "�޷���������������ע�����ļ����ƣ�");return;

					}});
				dss.ShowDialog();
	    	}
	    	if (Str.equals("���¼���"))
	    	{
	    		Tools.OpenDialog("���ڽ��������...", new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						StartAnalysisPoly();
					}});
	    	}
		}};
		
	//��������ò�����
	private v1_UserConfigDB_PolyAnalysisOption m_PAO = null;
	
	//���������
	private List<HashMap<String,Object>> m_AnalysisResultList = new ArrayList<HashMap<String,Object>>();
	
    //������
    private void StartAnalysisPoly()
    {
    	//1����ȡ�������������Ϣ����ʽ�����v1_UserConfigDB_PolyAnalysisOption
    	if (this.m_PAO==null) this.m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
    	List<HashMap<String,Object>> OptList = this.m_PAO.GetPolyAnalysisOption();
    	
    	//2����ȡ��Ҫ���������
    	List<HashMap<String,Object>> polyDatasetList = new ArrayList<HashMap<String,Object>>();
    	for(HashMap<String,Object> Opt:OptList)
    	{
    		String LayerId = Opt.get("LayerId")+"";
    		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
    		if (pDataset==null)continue;
    		
    		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
    		if (pLayer==null)pLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer().GetLayerByID(pDataset.getId());
    		
    		List<String> FieldList = (List<String>)Opt.get("FieldNameList");
    		
    		//�ж�ͳ���ֶ��Ƿ���Ч
    		int FieldCount = FieldList.size();
    		for(int i=FieldCount-1;i>=0;i--)
    		{
    			boolean Have = false;
	    		for(v1_LayerField LF :pLayer.GetFieldList())
	    		{
	    			if (LF.GetDataFieldName().equals(FieldList.get(i)))Have = true;
	    		}
	    		if (!Have)FieldList.remove(i);
    		}
    		if (FieldList.size()==0)FieldList.add("SYS_ID");   //û��ͳ���ֶΣ�Ĭ����SYS_ID����ͳ��
    		HashMap<String,Object> hmObj = new HashMap<String,Object>();
    		hmObj.put("Dataset", pDataset);
    		hmObj.put("FieldNameList", FieldList);
    		polyDatasetList.add(hmObj);
    	}
    	
    	//���Ϊ0����ʾ��Ҫ���з�������
    	if (polyDatasetList.size()==0)
    	{
    		Tools.ShowYesNoMessage(_Dialog.getContext(), "û������ͳ�Ʒ���ѡ��Ƿ���Ҫ���ã�", new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES")){pCallback.OnClick("����", "");return;}
					else {_Dialog.dismiss();}
				}});
    	}
    	
    	//3����ȡ��ǰѡ�е���
    	Polygon _SelectPoly = null;
    	GeoLayers GeoLayerList = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll);
    	for(GeoLayer pGeoLayer:GeoLayerList.getList())
    	{
    		if (pGeoLayer.getType()==lkGeoLayerType.enPolygon) 
			{
				if (pGeoLayer.getSelSelection().getCount()==1)
				{
					int idx = pGeoLayer.getSelSelection().getGeometryIndexList().get(0);
					_SelectPoly = (Polygon)pGeoLayer.getDataset().GetGeometry(idx);
				}
			}
    	}
    	
    	//4����ʾ��ѡ��������
    	double SelectPolyArea = _SelectPoly.getArea(true);  
    	Tools.SetTextViewValueOnID(_Dialog, R.id.tvresult, Tools.ReSetArea(SelectPolyArea,true));
    	
    	//�������б�
    	this.m_AnalysisResultList = new ArrayList<HashMap<String,Object>>();
    	for(HashMap<String,Object> pDatasetInfo:polyDatasetList)
    	{
    		Dataset pDataset = (Dataset)pDatasetInfo.get("Dataset");
    		List<String> FieldList = (List<String>)pDatasetInfo.get("FieldNameList");
    		
    		//����ָ����㣬���ظ�ʽ��result["SYSID"],result["Area"]
    		HashMap<String,Object> result = this.CalPolyLayer(pDataset, _SelectPoly);
    		if (result.size()==0)continue;
    		List<String> SYSIDList = new ArrayList<String>();
    		for(String id:result.keySet())SYSIDList.add(id);
    		
    		//����������Ҫ�Ǹ�������ѡ���е��ֶ��н��з������
    		HashMap<String,Object> STTypeList = new HashMap<String,Object>();
    		String SQL = "select (%1$s) as STType,SYS_ID from %2$s where SYS_ID in (%3$s)";
    		SQL = String.format(SQL, Tools.JoinT("||','||", FieldList),pDataset.getDataTableName(),Tools.JoinT(",", SYSIDList));
    		SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
    		if (DR!=null)while(DR.Read())
    		{
    			String STType = DR.GetString("STType");  //���磺���أ�ˮ��
    			String SYSID = DR.GetString("SYS_ID");
    			double B = Double.parseDouble(result.get(SYSID)+"");  //�������
    			
    			if (STTypeList.containsKey(STType)) //�ۼ�
    			{
					double A = Double.parseDouble(STTypeList.get(STType)+"");
					STTypeList.put(STType, A+B);
    			}
    			else
    			{
    				STTypeList.put(STType,B);
    			}
    		}DR.Close();
    		
    		//�����ཻ���
    		if (STTypeList.keySet().size()>0)
    		{
    			for(String key:STTypeList.keySet())
    			{
    				double A = Double.parseDouble(STTypeList.get(key)+"");
        			HashMap<String,Object> hm = new HashMap<String,Object>();
        			hm.put("D1", pDataset.getBindGeoLayer().GetAliasName());  //ͼ������
        			hm.put("D2", key);			  							  //ͳ����Ŀ
        			hm.put("D3", Tools.ReSetArea(A, false));			  	  //���
        			hm.put("D4", Tools.ConvertToDigi(A/SelectPolyArea*100+"",2)+"%");
        			this.m_AnalysisResultList.add(hm);
    			}
    		}
    		
    		
    	}
    	
    	//�󶨷������
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(R.id.prj_list), "����ӷ������");
    	hvf.BindDataToListView(this.m_AnalysisResultList);
    }
    
    /**
     * ����ָ������ָ��ͼ����ཻ���
     * @param pDataset
     * @param APoly
     * @return
     */
    private HashMap<String,Object> CalPolyLayer(Dataset pDataset,Polygon pSelectPoly)
    {
    	HashMap<String,Object> result = new HashMap<String,Object>();
    	GeoLayer pGeoLayer = pDataset.getBindGeoLayer();
		int ShowCount = pGeoLayer.getShowSelection().getCount();
		for(int i=0;i<ShowCount;i++)
		{
			int idx = pGeoLayer.getShowSelection().getGeometryIndexList().get(i);
			Polygon Poly2 = (Polygon)pGeoLayer.getDataset().GetGeometry(idx);
			if (Poly2.getStatus()==lkGeometryStatus.enDelete) continue;
			if (pSelectPoly.equals(Poly2))continue;   //�������ж�

			//��Ӿ����Ƿ��ཻ
			if (!pSelectPoly.getEnvelope().Intersect(Poly2.getEnvelope())) continue;
			
			//�������
			HashMap<String,Object> IntersectResult = SpatialAnalysisTools.Poly_IntersectArea(pSelectPoly, Poly2);
			
			
//				//�����ã������괮д����ʱ�ļ���
//				List<String> TempList = new ArrayList<String>();
//				TempList.add("�����"+Allarea);
//
//				TempList.add("================�������괮==============================");
//				Poly_Intersect pi = new Poly_Intersect();
//				List<List<Coordinate>> SubPolyList = pi.Poly_Intersect(_SelectPoly, Poly2);
//				for(int m=0;m<SubPolyList.size();m++)
//				{
//					TempList.add("================����"+(m+1)+"==============================");
//					for(Coordinate Coor1:SubPolyList.get(m))
//					{
//						TempList.add(Coor1.ToString());
//					}
//				}
//				
//				
//				TempList.add("=================��1 ���괮=============================");
//				for(Coordinate Coor1:_SelectPoly.GetPartAt(0).getVertexList())
//				{
//					TempList.add(Coor1.ToString());
//				}
//				TempList.add("=================��2 ���괮=============================");
//				for(Coordinate Coor1:Poly2.GetPartAt(0).getVertexList())
//				{
//					TempList.add(Coor1.ToString());
//				}
//				
//				try
//				{
//    				FileWriter filenew= new FileWriter(PubVar.m_SysAbsolutePath+"//test.txt");
//
//				   BufferedWriter bw= new BufferedWriter(filenew);
//
//				    bw.write(Tools.JoinT("\r\n", TempList));
//
//				    bw.close();
//				}
//				catch(Exception e)
//				{
//					
//				}

		
			//�����ཻ���
			double Allarea = Double.parseDouble(IntersectResult.get("Area")+"");
			if (Allarea>0)
			{
				result.put(Poly2.getSysId()+"",Allarea);
			}
		}
		
		return result;
    }
    

    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				pCallback.OnClick("���¼���","");}}
    	);
    	_Dialog.show();
    }

}
