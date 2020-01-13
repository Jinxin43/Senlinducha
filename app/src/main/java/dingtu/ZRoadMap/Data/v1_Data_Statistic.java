package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;

public class v1_Data_Statistic
{
	private v1_FormTemplate _Dialog = null; 
    public v1_Data_Statistic()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_data_statistic);
    	_Dialog.ReSetSize(0.5f,0.96f);
    	
    	_Dialog.SetCaption(Tools.ToLocale("����ͳ��"));
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,���Ա�", pCallback);
    	
    	_Dialog.findViewById(R.id.st_point_list).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.st_line_list).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.st_poly_list).setOnClickListener(new ViewClick());
    	
    	
    	//������֧��
    	Tools.ToLocale(_Dialog.findViewById(R.id.st_point_list));
    	Tools.ToLocale(_Dialog.findViewById(R.id.st_line_list));
    	Tools.ToLocale(_Dialog.findViewById(R.id.st_poly_list));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText2));
    	Tools.ToLocale(_Dialog.findViewById(R.id.tvLocaleText3));
    }
    
    
    class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.contains("����"))SetArrowImage(arg0,R.drawable.v1_leftarrow);
    		if (Tag.contains("չ��"))SetArrowImage(arg0,R.drawable.v1_downarrow);
    		
    		LinearLayout LL = null;
        	if (Tag.contains("��")){LL = (LinearLayout)_Dialog.findViewById(R.id.ll_point);FillData(lkGeoLayerType.enPoint);}
        	if (Tag.contains("��")){LL = (LinearLayout)_Dialog.findViewById(R.id.ll_line);FillData(lkGeoLayerType.enPolyline);}
        	if (Tag.contains("��")){LL = (LinearLayout)_Dialog.findViewById(R.id.ll_poly);FillData(lkGeoLayerType.enPolygon);}
        	if (Tag.contains("����"))
        	{
        		LayoutParams LP = (LayoutParams) LL.getLayoutParams();
        		LP.width = LayoutParams.FILL_PARENT;
        		LP.height = 0;
        		LL.setLayoutParams(LP);
        		arg0.setTag(Tag.replace("����", "չ��"));
        		return;
        	}
        	if (Tag.contains("չ��"))
        	{
        		LayoutParams LP = (LayoutParams) LL.getLayoutParams();
        		LP.width = LayoutParams.FILL_PARENT;
        		LP.height = LayoutParams.WRAP_CONTENT;
        		LL.setLayoutParams(LP);
        		arg0.setTag(Tag.replace("չ��", "����"));
        		return;
        	}
    	}
    }
    
    private void SetArrowImage(View arg0,int imgid)
    {
		Button bt = (Button)arg0;
		Drawable lock = PubVar.m_DoEvent.m_Context.getResources().getDrawable(imgid);
		lock.setBounds(0, 0, lock.getMinimumWidth(), lock.getMinimumHeight());
		bt.setCompoundDrawables(lock, null, null, null); //������ͼ��
    }

    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("���Ա�"))
	    	{
	    		//if (m_SelectItem!=null)
	    		{
	    			_Dialog.dismiss();
	    		}
	    	}
		}};

	/**
	 * ͳ������
	 * @param Type
	 */
	private void FillData(lkGeoLayerType layerType)
	{
		String HeaderType = "ͳ��_%1$sͼ��";
		int listid = 0;
		if (layerType==lkGeoLayerType.enPoint){HeaderType = String.format(HeaderType,"��");listid=R.id.point_list;}
		if (layerType==lkGeoLayerType.enPolyline){HeaderType = String.format(HeaderType,"��");listid=R.id.line_list;}
		if (layerType==lkGeoLayerType.enPolygon){HeaderType = String.format(HeaderType,"��");listid=R.id.poly_list;}
		
		//��ͳ�ƽ��
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_Dialog.findViewById(listid), HeaderType,new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) {
				_Dialog.GetButton("1").setEnabled(true);
			}});
    	
    	//��ȡ����ͳ����Ϣ
    	List<HashMap<String,Object>> dataList = this.StatisticData(layerType);
    	hvf.BindDataToListView(dataList);
	}
	
	/**
	 * ͳ������
	 * @param layerType
	 * @return
	 */
	private List<HashMap<String,Object>> StatisticData(lkGeoLayerType layerType)
	{
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	
    	List<v1_Layer> vLayerList = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList();
    	for(v1_Layer vLayer : vLayerList)
    	{
    		HashMap<String,Object> layResult = new HashMap<String,Object>();
    		Dataset pDataset = PubVar.m_Workspace.GetDatasetById(vLayer.GetLayerID());
    		if (pDataset.getType()==layerType && layerType==lkGeoLayerType.enPoint)
    		{
    			layResult.put("D1", vLayer.GetLayerAliasName());
    			layResult.put("D2", pDataset.GetAllObjectCount());
    			dataList.add(layResult);
    		}
    		
    		if (pDataset.getType()==layerType && (layerType==lkGeoLayerType.enPolyline || layerType==lkGeoLayerType.enPolygon))
    		{
    			//ͳ�Ƴ���
    			double AllLen = 0,AllArea = 0;
    			String SQL = "select sum(SYS_Length) as AllLen,sum(SYS_Area) as AllArea from "+pDataset.getDataTableName()+" where SYS_STATUS='0'";
    			SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
    			if (DR!=null) while(DR.Read())
    			{
    				AllLen = DR.GetDouble("AllLen");
    				AllArea = DR.GetDouble("AllArea");
    			}DR.Close();
    			
    			layResult.put("D1", vLayer.GetLayerAliasName());
    			layResult.put("D2", pDataset.GetAllObjectCount());
    			if (layerType==lkGeoLayerType.enPolyline)layResult.put("D3", Tools.ConvertToDigi(AllLen+"",3));
    			if (layerType==lkGeoLayerType.enPolygon)layResult.put("D3", Tools.ReSetArea(AllArea,false));
    			dataList.add(layResult);
    		}
    	}
    	return dataList;
	}
	
    /**
     * ���ص㣬�ߣ����ͳ����Ϣ�б���Ϣ����Ҫ��ͼ����������������
     */
    private void LoadStatisticInfo()
    {
    	int PointLayerCount=0,LineLayerCount=0,PolyLayerCount=0;
    	int PointObjects=0,LineObjects=0,PolyObjects=0;
    	DataSource pDataSource = PubVar.m_Workspace.GetDataSourceByEditing();
    	for(Dataset pDataset : pDataSource.getDatasets())
    	{
    		if (pDataset.getType()==lkGeoLayerType.enPoint)
    		{
    			PointLayerCount++;
    			PointObjects+=pDataset.GetAllObjectCount();
    		}
    		if (pDataset.getType()==lkGeoLayerType.enPolyline)
    		{
    			LineLayerCount++;
    			LineObjects+=pDataset.GetAllObjectCount();
    		}
    		if (pDataset.getType()==lkGeoLayerType.enPolygon)
    		{
    			PolyLayerCount++;
    			PolyObjects+=pDataset.GetAllObjectCount();
    		}
    	}
    	
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_point_layercount, Tools.ToLocale("ͼ����")+"="+PointLayerCount);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_line_layercount, Tools.ToLocale("ͼ����")+"="+LineLayerCount);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_poly_layercount, Tools.ToLocale("ͼ����")+"="+PolyLayerCount);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_point_datacount, Tools.ToLocale("������")+"="+PointObjects);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_line_datacount, Tools.ToLocale("������")+"="+LineObjects);
    	Tools.SetTextViewValueOnID(_Dialog, R.id.st_poly_datacount, Tools.ToLocale("������")+"="+PolyObjects);
    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
				LoadStatisticInfo();}}
    	);
    	_Dialog.show();
    }
}
