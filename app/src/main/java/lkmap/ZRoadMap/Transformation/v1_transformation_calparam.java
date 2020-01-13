package lkmap.ZRoadMap.Transformation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.UserConfigDB;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Cargeometry.Coordinate;
import lkmap.CoordinateSystem.CoorParamTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_Project_New_CheckPoint_Adpter;

public class v1_transformation_calparam
{
	private v1_FormTemplate _Dialog = null; 
    public v1_transformation_calparam()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_transformation_calparam);
    	_Dialog.SetCaption("����������");
    	_Dialog.ReSetSize(0.8f, 0.76f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+" ,ȷ��", pCallback);

    	_Dialog.findViewById(R.id.bt_add).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("���ӿ��Ƶ�", "");}});
    	
       	_Dialog.findViewById(R.id.bt_edit).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("�༭���Ƶ�", "");}});
       	
       	_Dialog.findViewById(R.id.bt_delete).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("ɾ�����Ƶ�", "");}});
       	
       	_Dialog.findViewById(R.id.bt_cal).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("������Ƶ�", "");}});
       	
       	_Dialog.findViewById(R.id.bt_save).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {pCallback.OnClick("�������", "");}});

    	((ListView)_Dialog.findViewById(R.id.listView1)).setOnItemClickListener(new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{
			ListView lv = (ListView)arg0;
			v1_Project_New_CheckPoint_Adpter hva = (v1_Project_New_CheckPoint_Adpter)lv.getAdapter();
			hva.SetSelectItemIndex(arg2);
			hva.notifyDataSetInvalidated();//���������Ѿ��䶯
			m_SelectItem = (HashMap<String,Object>)hva.getItem(arg2);
			_Dialog.findViewById(R.id.bt_edit).setEnabled(true);
			_Dialog.findViewById(R.id.bt_delete).setEnabled(true);
		}});
    	
    	
    	UserConfigDB db = new UserConfigDB();
    	UpdateCheckPoint(db.getTransformPoint());
    }
    
    
    //��ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			if (Str.equals("ȷ��"))
			{
	    		if (m_CalParamType.equals("�����Ĳ���"))
	    		{
	    			if (!CheckControlPointNumber(true))return;
	    			final HashMap<String,Object> param = CalFourParam();
	    			if (param==null){Tools.ShowMessageBox("�޷����������");return;}
	    			String Message = GetResultMessage();
	    			lkmap.Tools.Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ĳ���������ɣ��Ƿ�ʹ�øò�����\n\n"+Message,new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (m_Callback!=null)m_Callback.OnClick("�Ĳ���", param);
							_Dialog.dismiss();
						}});
	    		}
			}
			
	    	if (Str.equals("���ӿ��Ƶ�"))
	    	{
	    		v1_transformation_addmatchpoint pnca = new v1_transformation_addmatchpoint();
	    		pnca.SetMatchPointType(m_CalParamType);
	    		pnca.SetUseGPS(m_UseGPS);
	    		pnca.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						UpdateCheckPoint((HashMap<String,Object>)ExtraStr);
					}});
	    		pnca.ShowDialog();
	    	}

	    	if (Str.equals("�༭���Ƶ�"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
		    		v1_transformation_addmatchpoint pnca = new v1_transformation_addmatchpoint();
		    		pnca.SetMatchPointType(m_CalParamType);
		    		pnca.SetEditCheckPoint(m_SelectItem);
		    		pnca.SetUseGPS(m_UseGPS);
		    		pnca.SetCallback(new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							UpdateCheckPoint((HashMap<String,Object>)ExtraStr);
						}});
		    		pnca.ShowDialog();
	    		}
	    	}
	    	if (Str.equals("ɾ�����Ƶ�"))
	    	{
	    		if (m_SelectItem!=null)
	    		{
	    			String MessageStr = "�Ƿ�ɾ�����µ�ԣ�\r\n���=��"+m_SelectItem.get("DH")+"��";
	    			Tools.ShowYesNoMessage(_Dialog.getContext(), MessageStr, new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							m_CheckPointList.remove(m_SelectItem);
							RefreshCheckPointList();
						}});
	    		}
	    	}
	    	if (Str.equals("������Ƶ�"))
	    	{
	    		if (!CheckControlPointNumber(true))return;
	    		if (m_CalParamType.equals("�����Ĳ���"))
	    		{
	    			String Message = GetResultMessage();
	    			lkmap.Tools.Tools.ShowMessageBox("�Ĳ���������ɣ��������£�\n"+Message);
	    		}
	    	}
	    	
	    	if (Str.equals("�������"))
	    	{
	    		if (m_CalParamType.equals("�����Ĳ���"))
	    		{
	    			if (!CheckControlPointNumber(true))return;
	    			final HashMap<String,Object> param = CalFourParam();
	    			param.put("DH", "");
	    			param.put("P1", param.get("DX"));param.put("P2", param.get("DY"));
	    			param.put("P3", param.get("R"));param.put("P4", param.get("K"));
	    			
	    			String Message = GetResultMessage();
	    			lkmap.Tools.Tools.ShowYesNoMessage(_Dialog.getContext(), "�Ƿ񱣴����²�����\n\n"+Message,new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							
							v1_transformation_paramanage_add tpa = new v1_transformation_paramanage_add();
							tpa.SetCallParamType("�Ĳ���");
							tpa.SetEditParam(param);
							tpa.ShowDialog();
						}});
	    		}
	    	}
		}};
		
	//�����Ƶ��Ƿ����Ҫ��
	private boolean CheckControlPointNumber(boolean showMessage)
	{
		int CalPointCount = 0;
		for(HashMap<String,Object> hm:this.m_CheckPointList)
		{
			if (Boolean.parseBoolean(hm.get("Select")+""))CalPointCount++;
		}
		if (CalPointCount==0 )
		{
			if (showMessage)Tools.ShowMessageBox("���ڽ�����Ƶ�����Ϊ��0�����޷����������");
			return false;
		}
		return true;
	}
		
	/**
	 * ���ɽ����Ϣ
	 * @return
	 */
	private String GetResultMessage()
	{
		HashMap<String,Object> param = CalFourParam();
		String Message = "Xƽ��(��)��"+param.get("DX")+"\nYƽ��(��)��"+param.get("DY")+"\n��ת(��)��"+param.get("R")+"\n�߶ȣ�"+param.get("K");
		if (Double.parseDouble(param.get("DX").toString())==0 && 
			Double.parseDouble(param.get("DY").toString())==0 &&
			Double.parseDouble(param.get("R").toString())==0 && 
			Double.parseDouble(param.get("K").toString())==1)
		{
			Message+="\n\nע�⣺�˲����쳣�����飡";
		}
		return Message;
	}
	
	/**
	 * ����ת������
	 * @return
	 */
	private HashMap<String,Object> CalFourParam()
	{
		List<Coordinate> CoorList = new ArrayList<Coordinate>();
		for(HashMap<String,Object> cPoint:m_CheckPointList)
		{
			if (!Boolean.parseBoolean(cPoint.get("Select")+""))continue;
			Coordinate Coor1 = new Coordinate(Double.parseDouble(cPoint.get("X1")+""),Double.parseDouble(cPoint.get("Y1")+""));
			Coordinate Coor2 = new Coordinate(Double.parseDouble(cPoint.get("X2")+""),Double.parseDouble(cPoint.get("Y2")+""));
			CoorList.add(Coor1);CoorList.add(Coor2);
		}
		return CoorParamTools.CalFourPara(CoorList);
	}

	//�򿪹��̺�Ļص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	//����������ͣ������������������Ĳ����������߲���
	private String m_CalParamType = "";
	public void SetCallParamType(String calParamType)
	{
		this.m_CalParamType = calParamType;
	}
	
	//�Ƿ����ʹ��GPS��ȡ��λ
	private boolean m_UseGPS = true;
	public void SetUseGPS(boolean useGPS)
	{
		this.m_UseGPS = useGPS;
	}
	
	//ǰѡ�еĿ��Ƶ�
	private HashMap<String,Object> m_SelectItem = null;
	
	//У׼���б�
	List<HashMap<String,Object>> m_CheckPointList = new ArrayList<HashMap<String,Object>>();
	
	//����У׼����Ϣ�����checkPointInfo����DH�ؼ� �֣���ʾΪ���£������������ʾ����
	private void UpdateCheckPoint(HashMap<String,Object> checkPointInfo)
	{
		if (!checkPointInfo.containsKey("DH"))
		{
			checkPointInfo.put("Select",true);
			this.m_CheckPointList.add(checkPointInfo);
		} 

		this.RefreshCheckPointList();
	}
	
	//ˢ��У׼���б�
	private void RefreshCheckPointList()
	{
//		this.m_CheckPointList.clear();
//		
//		HashMap<String,Object> h1 = new HashMap<String,Object>();
//		h1.put("X1", 3342166.84023232323);h1.put("Y1", 441596.337);
//		h1.put("X2", 3342120.512);h1.put("Y2", 441553.409);h1.put("Select", true);
//
//		HashMap<String,Object> h2 = new HashMap<String,Object>();
//		h2.put("X1", 3316936.647);h2.put("Y1", 424906.947);
//		h2.put("X2", 3316889.969);h2.put("Y2", 424864.015);h2.put("Select", true);
//		
//		HashMap<String,Object> h3 = new HashMap<String,Object>();
//		h3.put("X1", 3338625.302);h3.put("Y1", 415534.526);
//		h3.put("X2", 3338578.777);h3.put("Y2", 415491.366);
//		h3.put("Select", true);
//		
//		HashMap<String,Object> h4 = new HashMap<String,Object>();
//		h4.put("X1", 3356163.501);h4.put("Y1", 392948.358);
//		h4.put("X2", 3356117.009);h4.put("Y2", 392904.880);h4.put("Select", true);
//		
//		HashMap<String,Object> h5 = new HashMap<String,Object>();
		
//		h5.put("X1", 3326868.132);h5.put("Y1", 396444.949);
//		h5.put("X2", 3326821.375);h5.put("Y2", 396401.680);h5.put("Select", true);
//		
//		this.m_CheckPointList.add(h1);this.m_CheckPointList.add(h2);
//		this.m_CheckPointList.add(h3);this.m_CheckPointList.add(h4);
//		this.m_CheckPointList.add(h5);
		
		//���µ��
		for(int i=0;i<this.m_CheckPointList.size();i++)
		{
			this.m_CheckPointList.get(i).put("DH", i+1);
		}
		//ˢ���б�
        v1_Project_New_CheckPoint_Adpter adapter = new v1_Project_New_CheckPoint_Adpter(_Dialog.getContext(),m_CheckPointList, 
											       R.layout.v1_bk_transformation_calparam_list, new String[] {"DH", "X1",  "Y1","X2","Y2","Select"}, 
											       new int[] {R.id.tvDH, R.id.tvSX1, R.id.tvSY1,R.id.tvRX1, R.id.tvRY1,R.id.cbSelect});  
		(((ListView)_Dialog.findViewById(R.id.listView1))).setAdapter(adapter);
		
		_Dialog.findViewById(R.id.bt_edit).setEnabled(false);
		_Dialog.findViewById(R.id.bt_delete).setEnabled(false);
	}

    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
		    	if (m_CalParamType.equals("�����Ĳ���"))_Dialog.SetCaption("�Ĳ���������");
				RefreshCheckPointList();
				}}
    	);
    	_Dialog.show();
    }
}
