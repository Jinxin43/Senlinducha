package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.graphics.Color;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.CoordinateSystem.Project_Web;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkFieldType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.Project.v1_project_layer;

public class v1_Data_Template
{

	private DataBindAlertDialog _DataDialog = null; 
    public v1_Data_Template()
    {
    	_DataDialog = new DataBindAlertDialog(PubVar.m_DoEvent.m_Context);
    	_DataDialog.SetOtherView(R.layout.v1_data_template);
    	_DataDialog.SetCaption(Tools.ToLocale("����"));
    	_DataDialog.ReSetSize(0.4f,0);
    	//_DataDialog.ReSetSize(0.8f, 0);
    	_DataDialog.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				if (Str.equals("����"))
				{
					lkmap.Tools.Tools.OpenDialog("���ڱ�������...",new ICallback(){
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							Save();
						}});
				}
			}});
    	
    	Tools.ToLocale(_DataDialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_DataDialog.findViewById(R.id.tvLocaleText2));
    	_DataDialog.findViewById(R.id.ll_status).setVisibility(View.GONE);

    }
    
    private ICallback _returnCallback = null;
    public void SetCallback(ICallback cb){this._returnCallback=cb;}
    
    //�����Ƿ�ɼ�ƽ����ѡ�����ɽ���
    private v1_CGps_AveragePoint _MyAveragePoint = null;
    private void SetShowGpsAveragePointOption()
    {
    	if (this._CalAveragePoint)
    	{
    		if (this._Layer==null)return;
        	_DataDialog.findViewById(R.id.ll_status).setVisibility(View.VISIBLE);
        	_DataDialog.findViewById(R.id.bt_restart).setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_DataDialog.findViewById(R.id.sp_pointcount);
					_MyAveragePoint.Start(Integer.parseInt(esd1.getText()));
				}});
        	
        	//��ʼ���ؼ�
        	v1_EditSpinnerDialog esd1 = (v1_EditSpinnerDialog)_DataDialog.findViewById(R.id.sp_pointcount);
        	esd1.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
        	esd1.SetSelectItemList(Tools.StrArrayToList(new String[]{"3","4","5","10","20","30"}));
        	esd1.getEditTextView().setEnabled(true);
        	
        	//Ĭ��ֵ
        	int gpsPointCount = 1;
        	if (this._Layer.GetLayerType()==lkGeoLayerType.enPoint)
        	{
        		String PointCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_PointCount").Value+"";
        		esd1.setText(PointCount);
        		if (Tools.IsInteger(PointCount))gpsPointCount = Integer.parseInt(PointCount);
        	} else
        	{
        		String VertexCount = PubVar.m_HashMap.GetValueObject("Tag_System_GPS_VertexCount").Value+"";
        		esd1.setText(VertexCount);
        		if (Tools.IsInteger(VertexCount))gpsPointCount = Integer.parseInt(VertexCount);
        	}
        	this._MyAveragePoint = new v1_CGps_AveragePoint();
        	this._MyAveragePoint.Start(gpsPointCount);
        	this._MyAveragePoint.SetCallback(new ICallback(){
				@Override
				public void OnClick(String Str, Object ExtraStr) 
				{
					if (Str.equals("�ɼ�״̬"))
					{
						Tools.SetTextViewValueOnID(_DataDialog, R.id.et_gpspointcount, (ExtraStr+"").split(",")[0]+"  ");
					}
				}});
         
        	
    	}
    }
    
    

    
    //�Ƿ���Ҫ�ɼ�ƽ����
    private boolean _CalAveragePoint = false;
    /**
     * �Ƿ���Ҫ�ɼ�ƽ����
     * @param _calAveragePoint
     */
    public void SetCalAveragePoint(boolean _calAveragePoint)
    {
    	this._CalAveragePoint = _calAveragePoint;
    	this.SetShowGpsAveragePointOption();
    }
    
    /**
     * ����ͼ�㶯̬��������Ϣ
     * @param vLayer
     */
    private void CreateForm(v1_Layer vLayer)
    {
    	
    	if (vLayer==null) return;
    	
    	//�����ǩ�ı�����󳤶ȣ�Ϊ������׼��
    	int LabelMaxLen = 0;
    	for(v1_LayerField LF:vLayer.GetFieldList())
    	{
    		if (Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName()))>LabelMaxLen) LabelMaxLen = Tools.CalStrLength(Tools.ToLocale(LF.GetFieldName()));
    	}
    	
    	//����ͼ����ֶ�������Ϣ����̬�������Ա�
    	LinearLayout LL = (LinearLayout)_DataDialog.findViewById(R.id.baselist);
    	for(v1_LayerField LF:vLayer.GetFieldList())
    	{
    		//������ǩ
    		LinearLayout SubLL = this.CreateFormRowHeader(LL, Tools.ToLocale(LF.GetFieldName()), LabelMaxLen);
    		
    		//��������
    		if (LF.GetFieldType()==lkFieldType.enString)  //�ı�
    		{
    			if (LF.GetFieldEnumCode().equals(""))
    			{
		    		EditText ET = new EditText(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		ET.setLayoutParams(LPET);
		    		SubLL.addView(ET);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    			} else
    			{
    				v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(es.getWidth(),es.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		es.setLayoutParams(LPET);
		    		es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(),LF.GetFieldEnumCode()));
		    		es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
		    		SubLL.addView(es);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
    			}
    		}
    		
       		if (LF.GetFieldType()==lkFieldType.enFloat)   //��������
    		{
       			if (LF.GetFieldEnumCode().equals(""))
       			{
		    		EditText ET = new EditText(LL.getContext());
		    		ET.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		ET.setLayoutParams(LPET);
		    		SubLL.addView(ET);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
       			} else
       			{
    				v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(es.getWidth(),es.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		es.setLayoutParams(LPET);
		    		es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
		    		es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(),LF.GetFieldEnumCode()));
		    		es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
		    		SubLL.addView(es);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
       			}
    		}
       		if (LF.GetFieldType()==lkFieldType.enInt)   //����
    		{
       			if (LF.GetFieldEnumCode().equals(""))
       			{
		    		EditText ET = new EditText(LL.getContext());
		    		ET.setInputType(InputType.TYPE_CLASS_NUMBER);
		    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		ET.setLayoutParams(LPET);
		    		SubLL.addView(ET);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
       			} else
       			{
    				v1_EditSpinnerDialog es = new v1_EditSpinnerDialog(LL.getContext());
		    		LayoutParams LPET = new LayoutParams(es.getWidth(),es.getHeight());
		    		LPET.width = LayoutParams.FILL_PARENT;
		    		LPET.height = LayoutParams.WRAP_CONTENT;
		    		es.setLayoutParams(LPET);
		    		es.getEditTextView().setInputType(InputType.TYPE_CLASS_NUMBER);
		    		es.SetSelectItemList(LF.GetFieldEnumList(vLayer.GetLayerProjecType(),LF.GetFieldEnumCode()));
		    		es.getEditTextView().setEnabled(LF.GetFieldEnumEdit());
		    		SubLL.addView(es);
		    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),es));
       			}
    		}
       		if (LF.GetFieldType()==lkFieldType.enBoolean)  //������
    		{
	    		Spinner SP = new Spinner(LL.getContext());
	    		v1_DataBind.SetBindListSpinner(_DataDialog.getContext(), "�Ƿ�", Tools.StrArrayToList(new String[]{"��","��"}), SP);
	    		LayoutParams LPET = new LayoutParams(SP.getWidth(),SP.getHeight());
	    		LPET.width = LayoutParams.FILL_PARENT;
	    		LPET.height = LayoutParams.WRAP_CONTENT;
	    		SP.setLayoutParams(LPET);
	    		SubLL.addView(SP);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),SP));
    		}
       		
       		if (LF.GetFieldType()==lkFieldType.enDateTime)  //����
       		{
       			final v1_SpinnerDialog SP = new v1_SpinnerDialog(LL.getContext());
       			SP.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_Data_Template_DateTime dtd = new v1_Data_Template_DateTime();
						dtd.SetCallabck(new ICallback(){
							@Override
							public void OnClick(String Str, Object ExtraStr) {
								v1_DataBind.SetBindListSpinner(_DataDialog.getContext(), "����", Tools.StrArrayToList(new String[]{ExtraStr.toString()}), SP);
							}});
						dtd.ShowDialog();
						
					}});
	    		LayoutParams LPET = new LayoutParams(SP.getWidth(),SP.getHeight());
	    		LPET.width = LayoutParams.FILL_PARENT;
	    		LPET.height = LayoutParams.WRAP_CONTENT;
	    		SP.setLayoutParams(LPET);
	    		SubLL.addView(SP);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),SP));
       		}
    		
    		LL.addView(SubLL);
    	}
    	
    	//�ڴ˼���ɼ����ݵ�״̬��Ϣ���������꣬�ߵĳ��ȣ���������
    	List<String> objInnerFeatureList = new ArrayList<String>();
    	if (vLayer.GetLayerType()==lkGeoLayerType.enPoint)
    	{
    		objInnerFeatureList.add(Tools.ToLocale("����"));
    	}
    	if (vLayer.GetLayerType()==lkGeoLayerType.enPolyline)
    	{
    		objInnerFeatureList.add(Tools.ToLocale("����"));
    	}
    	if (vLayer.GetLayerType()==lkGeoLayerType.enPolygon)
    	{
    		objInnerFeatureList.add(Tools.ToLocale("���"));
    	}
    	LinearLayout LO = (LinearLayout)_DataDialog.findViewById(R.id.otherlist);
    	for(String FL:objInnerFeatureList)
    	{
    		LinearLayout SubLL = this.CreateFormRowHeader(LO, FL, LabelMaxLen);
    		EditText ET = new EditText(LO.getContext());
    		ET.setEnabled(false);
    		LayoutParams LPET = new LayoutParams(ET.getWidth(),ET.getHeight());
    		LPET.width = LayoutParams.FILL_PARENT;
    		LPET.height = LayoutParams.WRAP_CONTENT;
    		ET.setLayoutParams(LPET);
    		SubLL.addView(ET);
    		LO.addView(SubLL);
    		
    		this._FieldInnerFeauterViewList.add(new FieldView(FL,"",ET));
    	}
		
    }
    
    /**
     * ���������е�ͷ�ı���ǩ
     * @param LL
     * @param Text
     * @param LabelMaxLen
     * @return
     */
    private LinearLayout CreateFormRowHeader(LinearLayout LL,String Text,int LabelMaxLen)
    {
		LinearLayout SubLL = new LinearLayout(LL.getContext());
		LayoutParams LP = new LayoutParams(SubLL.getWidth(),SubLL.getHeight());
		LP.width = LayoutParams.FILL_PARENT;
		LP.height = LayoutParams.WRAP_CONTENT;
		SubLL.setGravity(Gravity.CENTER);
		SubLL.setLayoutParams(LP);
		
		//��ǩ�ı�
		TextView TV = new TextView(LL.getContext());
		TV.setText(Tools.PadLeft(Text,LabelMaxLen)+"��");
		TV.setTextColor(Color.BLACK);
		TV.setTextAppearance(LL.getContext(), android.R.attr.textAppearanceMedium);
		SubLL.addView(TV);
		return SubLL;
    }
    
    public void ShowDialog()
    {
    	_DataDialog.show();
    }
    
    //����ʵ���ڲ����Ե��ֶ���ͼ�б�
    private List<FieldView> _FieldInnerFeauterViewList = new ArrayList<FieldView>();
    
    
    //�ֶ��������ֶ���ͼ�б����ڱ������ݣ���ʽ���ֶ�����,�ֶ���ͼ
    private List<FieldView> _FieldNameViewList = new ArrayList<FieldView>();
    
    /**
     * �Զ��屣�����
     */
    private void Save()
    {
    	boolean OK = true;
    	
    	//����ͼ������
    	if (this._CalAveragePoint)
    	{
    		//��ȡ��ǰGPS��λ
			Coordinate newGPSCoor = this._MyAveragePoint.CalGpsPoint();
			if (newGPSCoor==null){Tools.ShowMessageBox(_DataDialog.getContext(), "û�л�ȡ��Ч��GPS��λ�������ԣ�");return;}
        	if (this._BaseObject.GetSYS_ID()==-1)  //��������������
        	{
    			//�����ʹ洢
    			if (this._Layer.GetLayerType()==lkGeoLayerType.enPoint)
    			{
    				int SYS_ID = PubVar.m_DoEvent.m_GPSPoint.SaveGeoToDb(newGPSCoor, "GPS��λ");
    				this._BaseObject.SetSYS_ID(SYS_ID);
    				if (SYS_ID==-1)OK = false;
    			}
        	}
        	
			if (this._Layer.GetLayerType()==lkGeoLayerType.enPolyline)
			{
				PubVar.m_DoEvent.m_GPSLine.AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSLine.SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID==-1)OK = false;
			}
			if (this._Layer.GetLayerType()==lkGeoLayerType.enPolygon)
			{
				PubVar.m_DoEvent.m_GPSPoly.getGPSLine().AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSPoly.getGPSLine().SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID==-1)OK = false;
			}
    	}
    	
    	//������������
    	this._BaseObject.RefreshViewValueToData();
		if (!this._BaseObject.SaveFeatureToDb())OK=false;
		if (!OK)
		{
			Tools.ShowMessageBox(this._DataDialog.getContext(),"���ݱ���ʧ�ܣ�");
			return;
		}
		this._DataDialog.dismiss();
		if (this._returnCallback!=null) this._returnCallback.OnClick("OK", null);
    }
    
    //ʵ������
	private v1_CGpsDataObject _BaseObject = null;
	
    //��ǰͼ��
    private v1_Layer _Layer = null;
    
    /**
     * ����ʵ��༭��Ϣ�����SYS_ID=-1��ʾ����������Ϊ�޸�
     * @param layerID
     * @param SYS_ID
     */
    public void SetEditInfo(String layerID,int SYS_ID)
    {
    	this._Layer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID);
    	this.CreateForm(this._Layer);
    	this.SetShowGpsAveragePointOption();
    	Dataset pDataset = PubVar.m_Workspace.GetDatasetById(this._Layer.GetLayerID());
    	//��ʾ�༭״̬
    	if (SYS_ID!=-1)
    	{
	    	//ʵ���ڲ�������Ϣ
	    	Geometry pGeometry = pDataset.GetGeometry(SYS_ID);
	    	if (pGeometry==null)
	    	{
	    		List<String> SYSIDList = new ArrayList<String>();
	    		SYSIDList.add(SYS_ID+"");
	    		List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYSIDList);
	    		if (pGeometryList.size()!=0)pGeometry = pGeometryList.get(0);
	    	}
	    	if (pGeometry!=null)
	    	{
		    	if (this._Layer.GetLayerType()==lkGeoLayerType.enPoint)
		    	{
		    		TextView TV = (TextView)this._FieldInnerFeauterViewList.get(0).FieldView;
		    		Coordinate Coor = ((Point)pGeometry).getCoordinate();
		    		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		    		if (CS.GetName().equals("WGS-84����"))
		    		{
		    			Coordinate BLCoor = Project_Web.Web_XYToBL(Coor.getX(),Coor.getY());
		    			TV.setText(Tools.ConvertToDigi(BLCoor.getX()+"",6)+","+Tools.ConvertToDigi(BLCoor.getY()+"",6)+","+Tools.ConvertToDigi(Coor.getZ()+"",2));
		    		}else
		    		{
		    			TV.setText(Coor.ToString()+","+Tools.ConvertToDigi(Coor.getZ()+"",2));
		    		}
		    	}
		    	if (this._Layer.GetLayerType()==lkGeoLayerType.enPolyline)
		    	{
		    		TextView TV = (TextView)this._FieldInnerFeauterViewList.get(0).FieldView;
		    		TV.setText(Tools.ReSetDistance(((Polyline)pGeometry).getLength(true),true));
		    	}
		    	if (this._Layer.GetLayerType()==lkGeoLayerType.enPolygon)
		    	{
		    		TextView TV = (TextView)this._FieldInnerFeauterViewList.get(0).FieldView;
		    		TV.setText(Tools.ReSetArea(((Polygon)pGeometry).getArea(true),true));
		    	}
	    	}
    	}
    	
        //��ʼ��ʵ�岢���ð���Ŀ
        this._BaseObject = new v1_CGpsDataObject();
        this._BaseObject.SetDataset(pDataset);
        //if (this._Layer.GetIfLabel()) this._BaseObject.SetLabelFieldName(this._Layer.GetLabelField());	   //���ñ�ע�ֶ�����
       
        this._BaseObject.SetSYS_ID(SYS_ID);   //����SYS_ID
        
        for(FieldView FV:this._FieldNameViewList)
        {
        	this._BaseObject.AddDataBindItem(new DataBindOfKeyValue(FV.FieldName, FV.DataFieldName,"", FV.FieldView));	
        }

        this._BaseObject.ReadDataAndBindToView("SYS_ID="+SYS_ID);           //��ȡ���ݲ�����״̬
        this._DataDialog.SetGpsBasePointObject(this._BaseObject);
        this._DataDialog.UpdateDialogShowInfo();			 //���½�����ʾ

    }
    
    private class FieldView
    {
    	public FieldView(String fieldName,String dataFieldName,View view)
    	{
    		this.FieldName = fieldName;
    		this.DataFieldName = dataFieldName;
    		this.FieldView = view;
    	}
    	public String FieldName = "";
    	public String DataFieldName = "";
    	public View FieldView = null;
    }
}
