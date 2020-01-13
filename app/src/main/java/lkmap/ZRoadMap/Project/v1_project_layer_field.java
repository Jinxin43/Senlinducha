package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataDictionary.DialogDictSelector;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkEditMode;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.DataDictionary.v1_DataDictionary;
import lkmap.ZRoadMap.MyControl.v1_SpinnerDialog;

public class v1_project_layer_field
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_field()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_field);
    	_Dialog.SetCaption(Tools.ToLocale("�ֶ�����"));
    	_Dialog.ReSetSize(0.5f,-1f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	
    	//���ֶ������б�
    	v1_DataBind.SetBindListSpinner(_Dialog, "�ֶ�����", Tools.StrArrayToList(new String[]{"�ַ���","����","������","������","������"}), R.id.sp_type);
    	
    	//���ֶξ����б�
    	v1_DataBind.SetBindListSpinner(_Dialog, "�ֶξ���", Tools.StrArrayToList(new String[]{"0","1","2","3","4","5","6"}), R.id.sp_decimal);
    	
    	//�ֶ�ֵ���б�
    	v1_SpinnerDialog vsd = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_valuelist);
    	vsd.SetCallback(pCallback);vsd.setTag("");
    	
    	//������֧��
    	int[] localeText = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.tvLocaleText4,R.id.tvLocaleText5,R.id.tvLocaleText6,R.id.tvLocaleText7,R.id.cb_inputmode};
    	for(int id :localeText)
    	{
    		Tools.ToLocale(_Dialog.findViewById(id));
    	}
    }
    
    
    //�ϲ���ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (SaveFieldDetailInfo())
    			{
	    			if (m_Callback!=null) m_Callback.OnClick("�ֶ�", m_EditField);
    				_Dialog.dismiss();
    			}
	    	}
	    	
	    	//ѡ�������ֶζԻ���
	    	if (Str.equals("SpinnerCallback"))   
	    	{
	    		//v1_DataDictionary dd = new v1_DataDictionary();
	    		String layerType = m_EditLayer.GetLayerProjecType();
	    		if(layerType.isEmpty())
	    		{
	    			layerType = "�Զ��幤��";
	    		}
	    		
	    		if(layerType.equals("�Զ���ͼ��"))
	    		{
	    			layerType = "�Զ��幤��";
	    		}
	    		DialogDictSelector dd = new DialogDictSelector("��ҵ",layerType,Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_valuelist));
	    		dd.SetCallback(pCallback);
	    		dd.ShowDialog();
	    	}
	    	
	    	//�����ֵ�ѡ����ɺ�Ļص�
	    	if (Str.equals("�����ֵ�"))
	    	{
	    		if (ExtraStr==null)  //�ÿ�
	    		{
					v1_DataBind.SetBindListSpinner(_Dialog, "ֵ��", new ArrayList<String>(), R.id.sp_valuelist);
					_Dialog.findViewById(R.id.sp_valuelist).setTag("");
	    		} 
	    		else
	    		{
		    		//HashMap(ZDBM,ZDList)
	    			HashMap<String,Object> dataDicObject = (HashMap<String,Object>)ExtraStr;
	    			String[] items = new String[]{Tools.StrListToStr((List<String>)(dataDicObject.get("ZDList")))};
					v1_DataBind.SetBindListSpinner(_Dialog, "ֵ��", Tools.StrArrayToList(items), R.id.sp_valuelist);
					_Dialog.findViewById(R.id.sp_valuelist).setTag(dataDicObject.get("ZDBM"));
	    		}
	    	}
		}};


	//�ֶβ�����ɺ�Ļص�
	private ICallback m_Callback = null;
	/**
	 * �����ֶβ�����ɺ�Ļص�
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	//��ǰ�ֶΣ������Ĭ��ֵ��Ϊ�༭״̬
	private lkEditMode m_EditMode = lkEditMode.enNew;
	private v1_LayerField m_EditField = null;
	/**
	 * ���õ�ǰ���ڱ༭���ֶ���
	 * @param lf
	 */
	public void SetEditField(v1_LayerField lf)
	{
		this.m_EditField = lf;
	}
	
	//��ǰ�ֶ�������ͼ��
	private v1_Layer m_EditLayer = null;
	/**
	 * ���õ�ǰ�ֶ�������ͼ����
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr)
	{
		this.m_EditLayer = lyr;
	}
	
	/**
	 * �����ֶε���ϸ��Ϣ
	 * @return
	 */
	private boolean SaveFieldDetailInfo()
	{
		//��ȡ�ֶ�ֵ
		String FieldName = Tools.GetTextValueOnID(_Dialog,R.id.et_name);  //�ֶ�����
		String FieldType = Tools.GetSpinnerValueOnID(_Dialog,R.id.sp_type);  //�ֶ�����
		String FieldSize = Tools.GetTextValueOnID(_Dialog,R.id.et_size);  //�ֶδ�С
		String FieldDecimal = Tools.GetSpinnerValueOnID(_Dialog,R.id.sp_decimal);  //�ֶξ���
		String FieldValue = _Dialog.findViewById(R.id.sp_valuelist).getTag().toString();  //�ֶ�ֵ��
		boolean FieldValueInput = Tools.GetCheckBoxValueOnID(_Dialog,R.id.cb_inputmode);  //�ֶ�ֵ���Ƿ������
		
		//��֤�ֶ���Ϣ
		List<String> ErrorInfoList = new ArrayList<String>();
		if (FieldName.equals(""))ErrorInfoList.add("���ֶ����ơ�����Ϊ��ֵ��");
		for(v1_LayerField lf : this.m_EditLayer.GetFieldList())
		{
			if (lf.GetFieldName().equals(FieldName) && (!lf.GetFieldID().equals(this.m_EditField.GetFieldID())))ErrorInfoList.add("���ֶ����ơ��ظ���");
		}
		if (FieldSize.equals(""))ErrorInfoList.add("���ֶδ�С������Ϊ��ֵ��");
		else if (Integer.parseInt(FieldSize)>255)ErrorInfoList.add("���ֶδ�С��ӦС��255��");
		
		//��ʾ������Ϣ
		if (ErrorInfoList.size()>0)
		{
			String ErrorInfo = Tools.JoinT("\r\n", ErrorInfoList);
			Tools.ShowMessageBox(_Dialog.getContext(), ErrorInfo);return false;
		}
		
		//Ϊ�ֶ�ʵ�帳ֵ
		this.m_EditField.SetFieldName(FieldName);
		this.m_EditField.SetFieldTypeName(FieldType);
		this.m_EditField.SetFieldSize(Integer.parseInt(FieldSize));
		this.m_EditField.SetFieldDecimal(Integer.parseInt(FieldDecimal));
		this.m_EditField.SetFieldEnumCode(FieldValue);
		this.m_EditField.SetFieldEnumEdit(FieldValueInput);
		
		if (this.m_EditMode==lkEditMode.enNew)
		{
			this.m_EditLayer.GetFieldList().add(this.m_EditField);
		}
		return true;
	}
		
    /**
     * �����ֶ���Ϣ
     */
    private void LoadFieldDetailInfo()
    {
    	//�ж��Ƿ���Ĭ���ֶΣ�����Ϊ�༭״̬
    	if (this.m_EditField!=null)
    	{
    		this.m_EditMode = lkEditMode.enEdit;
    		
    		Tools.SetTextViewValueOnID(_Dialog,R.id.et_name,this.m_EditField.GetFieldName());  //�ֶ�����
    		Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, this.m_EditField.GetFieldTypeName());  //�ֶ�����
    		Tools.SetTextViewValueOnID(_Dialog,R.id.et_size,this.m_EditField.GetFieldSize()+"");  //�ֶδ�С
    		Tools.SetSpinnerValueOnID(_Dialog,R.id.sp_decimal,this.m_EditField.GetFieldDecimal()+"");  //�ֶξ���
    		
    		_Dialog.findViewById(R.id.sp_valuelist).setTag(this.m_EditField.GetFieldEnumCode());  //�ֶ�ֵ��
    		
    		//String EnumStr = Tools.JoinT(",", this.m_EditField.GetFieldEnumList());
    		v1_DataBind.SetBindListSpinner(_Dialog, "",Tools.StrArrayToList(new String[]{this.m_EditField.GetFieldEnumCode()}) , R.id.sp_valuelist);
    		
    		Tools.SetCheckValueOnID(_Dialog,R.id.cb_inputmode,this.m_EditField.GetFieldEnumEdit());  //�ֶ�ֵ���Ƿ������
    		
    	} else
    	{
    		//�����ֶ�
    		this.m_EditField = new v1_LayerField();
    		
    		//�Զ���ȡ��Ӧ���������ֶ�
    		for(int i=1;i<=225;i++)
    		{
    			boolean haveField = false;
	    		for(v1_LayerField LF:this.m_EditLayer.GetFieldList())
	    		{
	    			if (("F"+i).equals(LF.GetDataFieldName())){haveField=true;break;}
	    		}
	    		if (!haveField) {this.m_EditField.SetDataFieldName("F"+i);break;}
    		}
    	}
    }
    
    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadFieldDetailInfo();}});
    	_Dialog.show();
    }
    

}
