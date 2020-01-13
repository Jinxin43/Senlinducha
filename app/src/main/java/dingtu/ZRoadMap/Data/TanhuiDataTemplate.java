package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.HashMap;
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
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkFieldType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;

public class TanhuiDataTemplate
{
	private DataBindAlertDialog _DataDialog = null; 
	private v1_Layer  mCurrentLayer = null;
	private int mCurrentSysID = 0;
	private int maxCode = 0;
	private boolean isTanhui = false;
	private String xiaobanLayerName = "";
	private String xiaobanField = "TU_BAN";
	
	
    public TanhuiDataTemplate()
    {
    	//TODO��Ӧ���ж��Ƿ���̼�㹤��
    	 PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().CheckAndCreateTanhuiTable();
    	_DataDialog = new DataBindAlertDialog(PubVar.m_DoEvent.m_Context);
    	_DataDialog.SetOtherView(R.layout.d_tanhui_datatemplate);
    	_DataDialog.SetCaption(Tools.ToLocale("����"));
    	_DataDialog.ReSetSize(0.4f,0.97f);
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
							if(isTanhui && xiaobanLayerName != null && !xiaobanLayerName.isEmpty())
							{
								if(!SaveXiaoBanSum())
								{
									Tools.ShowMessageBox("����С��̼�������Ϣʧ��");
								}
								
								Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mCurrentLayer.GetLayerID());
								if (pDataset==null) return;
								if (mCurrentLayer.GetLayerTypeName().equals("��"))PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
								if (mCurrentLayer.GetLayerTypeName().equals("��"))PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
								if (mCurrentLayer.GetLayerTypeName().equals("��"))PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
								PubVar.m_Map.FastRefresh();
							}
						}});
					
					
				}
			}});
    	
    	Tools.ToLocale(_DataDialog.findViewById(R.id.tvLocaleText1));
    	Tools.ToLocale(_DataDialog.findViewById(R.id.tvLocaleText2));
    	_DataDialog.findViewById(R.id.ll_status).setVisibility(View.GONE);
    	
    	//�����ֶΣ��ֶ����ԣ�ɾ���ֶΰ�ť
    	_DataDialog.findViewById(R.id.pln_add).setOnClickListener(new ViewClick());
    	_DataDialog.findViewById(R.id.pln_edit).setOnClickListener(new ViewClick());
    	_DataDialog.findViewById(R.id.pln_delete).setOnClickListener(new ViewClick());
    	_DataDialog.findViewById(R.id.pln_export).setOnClickListener(new ViewClick());
    	

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
				public void OnClick(String Str, Object ExtraStr) {
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
    
    private void CreateTanhuiForm(v1_Layer vLayer,int sysID)
    {
    	if (vLayer==null) return;
    	mCurrentLayer=vLayer;
    	mCurrentSysID = sysID;
    	
    	for(v1_LayerField LF:vLayer.GetFieldList())
    	{
    		if(LF.GetFieldName().equals("���غ�"))
    		{
        		EditText ET = (EditText)_DataDialog.findViewById(R.id.evYangdihao);
        		
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    		}
    		
    		if(LF.GetFieldName().equals("С���"))
    		{
    			EditText ET = (EditText)_DataDialog.findViewById(R.id.evXiaobanhao);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    		}
    		
    		if(LF.GetFieldName().equals("��׼�غ�"))
    		{
        		EditText ET = (EditText)_DataDialog.findViewById(R.id.evBiaozhundihao);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    		}
    		
    		if(LF.GetFieldName().equals("ƽ����"))
    		{
        		EditText ET = (EditText)_DataDialog.findViewById(R.id.evhight);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    		}
    		
    		if(LF.GetFieldName().equals("����"))
    		{
        		EditText ET = (EditText)_DataDialog.findViewById(R.id.tvCount);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    		}
    		
    		if(LF.GetFieldName().equals("�������"))
    		{
        		EditText ET = (EditText)_DataDialog.findViewById(R.id.tvXuJiliang);
	    		this._FieldNameViewList.add(new FieldView(LF.GetFieldName(),LF.GetDataFieldName(),ET));
    		}
    	}
    	
    	_DataDialog.findViewById(R.id.allNomal).setVisibility(View.GONE);
    	
    	_DataDialog.findViewById(R.id.alltanhui).setVisibility(View.VISIBLE);
    	
    	loadMeiMuJianChiList();
    	
    }
    
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
    	
    	_DataDialog.findViewById(R.id.allNomal).setVisibility(View.VISIBLE);
    	_DataDialog.findViewById(R.id.alltanhui).setVisibility(View.GONE);
		
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
    				if (SYS_ID==-1)
    				{
    					OK = false;
    				}
    				else
    				{
    					mCurrentSysID = SYS_ID; 
    				}
    			}
        	}
        	
			if (this._Layer.GetLayerType()==lkGeoLayerType.enPolyline)
			{
				PubVar.m_DoEvent.m_GPSLine.AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSLine.SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID==-1)OK = false;
				else
				{
					mCurrentSysID = SYS_ID; 
				}
			}
			if (this._Layer.GetLayerType()==lkGeoLayerType.enPolygon)
			{
				PubVar.m_DoEvent.m_GPSPoly.getGPSLine().AddPoint(newGPSCoor);
				int SYS_ID = PubVar.m_DoEvent.m_GPSPoly.getGPSLine().SaveGeoToDb(false);
				this._BaseObject.SetSYS_ID(SYS_ID);
				if (SYS_ID==-1)OK = false;
				else
				{
					mCurrentSysID = SYS_ID; 
				}
			}
    	}
    	
    	//������������
    	this._BaseObject.RefreshViewValueToData();
    	if(Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao)!= null && !Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao).isEmpty())
    	{
    		PubVar.preYangdihao = Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao);
    	}
    	
		if (!this._BaseObject.SaveFeatureToDb())OK=false;
		
		
		
		if (!OK)
		{
			Tools.ShowMessageBox(this._DataDialog.getContext(),"���ݱ���ʧ�ܣ�");
			return;
		}
		//this._DataDialog.dismiss();
		if (this._returnCallback!=null) this._returnCallback.OnClick("OK", null);
    }
    
    private boolean SaveXiaoBanSum()
    {
    	boolean isOkay = true;
    	
    	if(xiaobanLayerName.isEmpty())
    	{
    		return false;
    	}
    	
    	String fieldBZDSL= PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName).GetDataFieldNameByFieldName("��׼������");
    	String empty= "";
    	if(fieldBZDSL== null || fieldBZDSL.isEmpty())
    	{
    		empty ="��׼������";
    		isOkay = false;
    	}
    	
    	String fieldBZDZS= PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName).GetDataFieldNameByFieldName("��׼������");
    	if(fieldBZDZS == null || fieldBZDZS.isEmpty())
    	{
    		empty +=",��׼������";
    		isOkay = false;
    	}
    	
    	String fieldBZDXJ =PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName).GetDataFieldNameByFieldName("��׼�����");
    	if(fieldBZDXJ== null || fieldBZDXJ.isEmpty())
    	{
    		empty +=",��׼�����";
    		isOkay = false;
    	}
    	
    	String fieldBZDBX = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName).GetDataFieldNameByFieldName("��׼�ر��");
    	if(fieldBZDBX== null || fieldBZDBX.isEmpty())
    	{
    		empty +=",��׼�ر��";
    		isOkay = false;
    	}
    	
    	if(!isOkay)
    	{
    		Tools.ShowMessageBox("��׼������С��û�С�"+empty+"��"+"�ֶΣ����С��ͼ�������ֶ�");
    		return isOkay;
    	}
    	
    	
    	
    	String sql = "Select * from "+mCurrentLayer.GetLayerID()+"_D where F7='"+xiaobanLayerName+"' and F2='"+Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao)+"'";
    		
    	SQLiteDataReader DR = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()).Query(sql);
    	Double sumXuji = 0d;
    	int sumZhuShu = 0;
    	int sumCount = 0;
    	String allBiaozhundi = "";
    	while (DR.Read()) 
    	{
    		sumXuji+=DR.GetDouble("F6");
    		sumZhuShu +=DR.GetInt32("F5");
    		allBiaozhundi += DR.GetString("F3")+"��";
    		sumCount++;
		}
    	DR.Close();
    	
    	sql = "update "+xiaobanLayerName+"_D set "+fieldBZDSL+
    			"='"+sumCount+"', "+fieldBZDZS+
    			"='"+sumZhuShu+"', "+fieldBZDXJ+
    			"='"+String.valueOf(sumXuji)+"', "+fieldBZDBX+
    			"='"+allBiaozhundi+"' where "+PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName).GetDataFieldNameByFieldName(xiaobanField)+
    			"= '"+Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao)+"'";
    	return new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()).ExcuteSQL(sql);
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
    	Dataset pDataset = PubVar.m_Workspace.GetDatasetById(this._Layer.GetLayerID());
    	if(this._Layer.GetLayerProjecType() != null && this._Layer.GetLayerProjecType().contains("̼��"))
    	{
    		isTanhui = true;
    		this.CreateTanhuiForm(this._Layer,SYS_ID);
    	}
    	else
    	{
    		this.CreateForm(this._Layer);
	    	
	    	this.SetShowGpsAveragePointOption();
	    	
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
        
        //��ȡ̼��ѡ�е�С����Ϣ
        if(isTanhui)
        {
        	if(Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao).isEmpty())
        	{
		    	int selectedID = 0;
		    	for (GeoLayer layer :PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) 
		    	{
					if(layer.getType() == lkGeoLayerType.enPolygon)
					{
						if(layer.getSelSelection().getCount() == 1)
						{
							selectedID = layer.getSelSelection().getGeometryIndexList().get(0);
							xiaobanLayerName =layer.getId();
						}
					}
				};
				
				if(!xiaobanLayerName.isEmpty())
				{
					DataSource dc = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName());
					String fieldXB = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(xiaobanLayerName).GetDataFieldNameByFieldName(xiaobanField);
					if(fieldXB != null && !fieldXB.isEmpty())
					{
						String Xiaobanhao = dc.QueryDataFieldValue(fieldXB, selectedID, xiaobanLayerName+"_D");
						Tools.SetTextViewValueOnID(_DataDialog, R.id.evXiaobanhao,Xiaobanhao);
						
						this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("С���", "F7", xiaobanLayerName,null));
					}
				}
        	}
        	else
        	{
        		DataSource dc = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName());
				String fieldXB = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID).GetDataFieldNameByFieldName("С���");
				if(fieldXB != null && !fieldXB.isEmpty())
				{
					xiaobanLayerName = dc.QueryDataFieldValue(fieldXB, mCurrentSysID, layerID+"_D");
					
					this._BaseObject.AddDataBindItem(new DataBindOfKeyValue("С���", "F7", xiaobanLayerName,null));
				}
        	}
        	
        	if(Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao)== null || Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao).isEmpty())
        	{
        		 Tools.SetTextViewValueOnID(_DataDialog, R.id.evYangdihao,PubVar.preYangdihao);
        	}
        }

    }
    
    private void loadMeiMuJianChiList()
    {
    	//��ͼ���б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_DataDialog.findViewById(R.id.in_listview), "ÿľ��߱�",tanhuiCallback);
    	
    	
    	//��߱���ֶ��б�
    	ArrayList<MeiMuJianChi> lyrFieldList = new ArrayList<MeiMuJianChi>();
    	
    	List<HashMap<String,Object>> jianChiList = new ArrayList<HashMap<String,Object>>();
    	String sql = "select * from "+mCurrentLayer.GetLayerID()+"_D where SYS_ID ="+mCurrentSysID;
    	
    	SQLiteDataReader DR = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()).Query(sql);
    	String jianchiValue = "";
    	if(DR.Read()) 
    	{
    		
			for (int i=PubVar.MinTanhuiIndex;i<PubVar.maxTanhuiIndex;i++) 
			{
				String fi = DR.GetString("F"+i);
				if(!fi.isEmpty())
				{
					jianchiValue += fi;
				}
				
			}
			
			String[] all = jianchiValue.split(";");
			for(String per:all)
			{
				try
				{
					HashMap<String,Object> hm = new HashMap<String,Object>();
					String[] properties = per.split(",");
					if(properties.length>1)
					{
						maxCode = maxCode>Integer.valueOf(properties[0])?maxCode:Integer.valueOf(properties[0]);
						hm.put("D1", properties[0]);
						hm.put("D2", properties[1]);
						hm.put("D3", properties[2]);
						hm.put("D4", properties[3]);
						hm.put("D5", properties[4]);
						jianChiList.add(hm);
					}
					else
					{
						maxCode = 0;
					}
					
				}
				catch(Exception e)
				{
					Tools.ShowToast(_DataDialog.getContext(), e.getMessage());
				}
			}
		}
    	DR.Close();
    	hvf.BindDataToListView(jianChiList);
    }
    
    public void loadTanhuiList()
    {
    	//��ͼ���б�
    	v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
    	hvf.SetHeaderListView(_DataDialog.findViewById(R.id.in_listview), "ÿľ��߱�",tanhuiCallback);
    	
    	List<HashMap<String,Object>> dataList = new ArrayList<HashMap<String,Object>>();
    	
    	
    	String sql = "select * from T_MeiMuJianChi where  YangDiHao ='"+Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao)+
    				"' and XiaoBanHao ='"+Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao)+"' and "+
    				"BiaoZhunDiHao = '"+Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao)+"'";
    	
    	SQLiteDataReader DR = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().Query(sql);
 
    	while(DR.Read())
    	{
    		HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", DR.GetString("JianChiCode"));
        	hm.put("D2", DR.GetString("ShuZhongCode"));
        	hm.put("D3", DR.GetString("XiongJing"));
        	hm.put("D4", DR.GetDouble("XuJiLiang"));
        	dataList.add(hm);
    	}
    	
    	Tools.SetTextViewValueOnID(_DataDialog, R.id.tvCount, String.valueOf(dataList.size()));
    	
    	hvf.BindDataToListView(dataList);
    	DR.Close();
    	
    	sql = "select sum(Xujiliang) as sumXuJi from T_MeiMuJianChi where  YangDiHao ='"+Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao)+
				"' and XiaoBanHao ='"+Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao)+"' and "+
				"BiaoZhunDiHao = '"+Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao)+"'";
    	DR = PubVar.m_DoEvent.m_ProjectDB.GetSQLiteDatabase().Query(sql);
    	if(DR.Read())
    	{
    		Tools.SetTextViewValueOnID(_DataDialog, R.id.tvXuJiliang, String.valueOf(DR.GetDouble("sumXuJi")));
    	}
    	DR.Close();
    	
    }
    
    private String selectCode = "";
    private HashMap<String, Object> selectObj = null;
  //��ť�¼�
    private ICallback tanhuiCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
			//ѡ���ֶ��б��Ļص�
	    	if (Str.equals("�б�ѡ��"))
	    	{
	    		selectObj = (HashMap<String,Object>)ExtraStr;
	    		selectCode = selectObj.get("D1").toString();
//	        	List<v1_LayerField> lyrFieldList = m_EditLayer.GetFieldList();
//	        	for(v1_LayerField Field:lyrFieldList)
//	        	{
//	        		if (Field.GetFieldName().equals(FieldName))m_SelectField=Field;
//	        	}
	    		SetButtonEnable(true);
	    	}
			
		}};
		
		//���ð�ť��״̬
	    private void SetButtonEnable(boolean enabled)
	    {
	    	_DataDialog.findViewById(R.id.pln_edit).setEnabled(enabled);
	    	_DataDialog.findViewById(R.id.pln_delete).setEnabled(enabled);
	    	((TextView)_DataDialog.findViewById(R.id.tv_edit)).setTextColor((enabled?Color.BLACK:Color.GRAY));
	    	((TextView)_DataDialog.findViewById(R.id.tv_delete)).setTextColor((enabled?Color.BLACK:Color.GRAY));
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
	    	if (StrCommand.equals("����"))
	    	{
	    		Save();
	    		
	    		TanHuiJianChiEdit thEdit = new TanHuiJianChiEdit();
	    		thEdit.SetCurrentLayer(_Layer);
	    		thEdit.SetSysID(mCurrentSysID);
	    		
	    		if(Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao).isEmpty())
	    		{
	    			Tools.ShowMessageBox("���غŲ���Ϊ��");
	    			return;
	    		}
	    		thEdit.SetYangDiHao(Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao));
	    		if(Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao).isEmpty())
	    		{
	    			Tools.ShowMessageBox("С��Ų���Ϊ��");
	    			return;
	    		}
	    		thEdit.SetXiaoBanHao(Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao));
	    		if(Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao).isEmpty())
	    		{
	    			Tools.ShowMessageBox("��׼�غŲ���Ϊ��");
	    			return;
	    		}
	    		thEdit.SetBiaoZhunDihao(Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao));
	    		
//	    		int code = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetMaxJianChiCode(Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao), 
//	    				Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao), Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao));
	    		
	    		
	    		thEdit.SetCode(maxCode+1);
	    		
//	    		plf.SetEditLayer(this.m_EditLayer);
	    		//plf.SetCallback(pCallback);  //�ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
	    		thEdit.SetCallback(pCallback);
	    		thEdit.ShowDialog();
	    		
	    		
	    	}
	    	
	    	if (StrCommand.equals("��������"))
	    	{
	    		String fileName = Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao)+"-"+Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao)+"-"+Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao)+".CSV";
	    		//exportOneTanhui(mCurrentLayer,mCurrentSysID,fileName);
	    		PubVar.m_DoEvent.exportOneTanhui(mCurrentLayer,mCurrentSysID,fileName);
	    	}
	    	
	    	if(StrCommand.equals("�޸�"))
	    	{
	    		if(!this.selectCode.isEmpty())
	    		{
	    			TanHuiJianChiEdit thEdit = new TanHuiJianChiEdit();
	    			thEdit.SetCurrentLayer(_Layer);
		    		thEdit.SetSysID(mCurrentSysID);
		    		
		    		thEdit.SetCode(Integer.valueOf(selectObj.get("D1").toString()));
		    		thEdit.SetEditMode("edit",selectObj.get("D2").toString(),selectObj.get("D4").toString(),selectObj.get("D5").toString());
		    		
		    		
		    		if(Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao).isEmpty())
		    		{
		    			Tools.ShowMessageBox("���غŲ���Ϊ��");
		    			return;
		    		}
		    		thEdit.SetYangDiHao(Tools.GetTextValueOnID(_DataDialog, R.id.evYangdihao));
		    		if(Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao).isEmpty())
		    		{
		    			Tools.ShowMessageBox("С��Ų���Ϊ��");
		    			return;
		    		}
		    		thEdit.SetXiaoBanHao(Tools.GetTextValueOnID(_DataDialog, R.id.evXiaobanhao));
		    		if(Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao).isEmpty())
		    		{
		    			Tools.ShowMessageBox("��׼�غŲ���Ϊ��");
		    			return;
		    		}
		    		thEdit.SetBiaoZhunDihao(Tools.GetTextValueOnID(_DataDialog, R.id.evBiaozhundihao));
		    		
		    		
		    		
//		    		plf.SetEditLayer(this.m_EditLayer);
		    		//plf.SetCallback(pCallback);  //�ֶβ�����ɺ�Ļص� ���ص���־���ֶ�
		    		thEdit.SetCallback(pCallback);
		    		thEdit.ShowDialog();
	    		}
	    	}
	    	
	    	if(StrCommand.equals("ɾ���ֶ�"))
	    	{
	    		Tools.ShowYesNoMessage(_DataDialog.getContext(), Tools.ToLocale("�Ƿ�ɾ�����")+"��"+selectCode+"������ľ������ݣ�", new ICallback(){
	    			
	    			@Override
					public void OnClick(String Str, Object ExtraStr) {
	    			
	    				if(Str.endsWith("YES"))
	    				{
	    					String sql = "select * from "+mCurrentLayer.GetDataTableName()+" where SYS_ID ="+mCurrentSysID;
	    		        	SQLiteDataReader DR = new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()).Query(sql);
	    		        	String jianchiValue = "";
	    		        	String updateSql = "";
	    		        	int zhushu = 0;
	    		        	Double xuji = 0d;
	    		        	
	    		        	
	    		        	int index = 0; 
	    		        	boolean isFind = false;
	    		        	if(DR.Read()) 
	    		        	{
	    		        		for (int i=PubVar.MinTanhuiIndex;i<PubVar.maxTanhuiIndex;i++ ) 
	    	        			{
	    	        				jianchiValue = DR.GetString("F"+i);
	    	        				if(jianchiValue.isEmpty())
	    	        				{
	    	        					continue;
	    	        				}
	    	        				
	    	        				
	    	        				String[] all = jianchiValue.split(";");
	    	        				
	    	        				String newString = "";
	    	        				
	    	            			for(String per:all)
	    	            			{
	    	            				String[] properties = per.split(",");
	    	            				
	    	        					if(properties[0].equals(selectCode))
	    	        					{
	    	        						isFind = true;
	    	        						index = i;
	    	        					}
	    	        					else
	    	        					{
	    	        						zhushu++;
	    	        						maxCode= zhushu;
	    	        						xuji+=Double.valueOf(properties[4]);
	    	        						if(!isFind)
	    	        						{
	    	        							newString += per+";";
	    	        						}
	    	        						else 
	    	        						{
	    	        							newString +=  zhushu+",";
	    	        							newString +=properties[1]+","+properties[2]+","+properties[3]+","+properties[4]+";";
	    	        									
	    									}
	    	            					
	    	        					}
	    	            			}
	    	            			
	    	            			if(isFind)
	    	    	        		{
	    	    	        			updateSql += " F"+i +"= '"+newString+"', ";
	    	    	        		}
	    	            			
	    	            			
	    	        			}
	    		        	}
	    		        	DR.Close();
	    		        	if(!updateSql.isEmpty())
	    		        	{
	    		        		String updateSumSql = "update "+mCurrentLayer.GetDataTableName() +" set"+updateSql+" F5="+zhushu+", F6="+ xuji +" where SYS_ID = "+mCurrentSysID;
	    		        		if(new DataSource(PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()).ExcuteSQL(updateSumSql))
	    		        		{
	    		        			SetEditInfo(mCurrentLayer.GetLayerID(), mCurrentSysID);
	    		        		}
	    		        	}
	    		        	
	    		        	
	    				}
	    			}
	    		});
	    		
	    	}
	    	
	    	
	    }
	    
	    private ICallback pCallback = new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
				SetEditInfo(mCurrentLayer.GetLayerID(), mCurrentSysID);
			}
	    };
	    	
    
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
    
    
    class MeiMuJianChi
    {
    	public int code;
    	public String shuzhong;
    	public float xiongjing;
    	public float xujiliang;
    	public String yangdihao;
    	
    	public MeiMuJianChi()
    	{}
    	
    	public String getID()
    	{
    		return yangdihao+code;
    	}
    	public float GetXujiliang()
    	{
    		return 0f;
    	}
    	
    
    }
    
   
}

