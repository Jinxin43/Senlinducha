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
    	_Dialog.SetCaption(Tools.ToLocale("字段属性"));
    	_Dialog.ReSetSize(0.5f,-1f);
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	
    	//绑定字段类型列表
    	v1_DataBind.SetBindListSpinner(_Dialog, "字段类型", Tools.StrArrayToList(new String[]{"字符串","整型","浮点型","布尔型","日期型"}), R.id.sp_type);
    	
    	//绑定字段精度列表
    	v1_DataBind.SetBindListSpinner(_Dialog, "字段精度", Tools.StrArrayToList(new String[]{"0","1","2","3","4","5","6"}), R.id.sp_decimal);
    	
    	//字段值域列表
    	v1_SpinnerDialog vsd = (v1_SpinnerDialog)_Dialog.findViewById(R.id.sp_valuelist);
    	vsd.SetCallback(pCallback);vsd.setTag("");
    	
    	//多语言支持
    	int[] localeText = new int[]{R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.tvLocaleText4,R.id.tvLocaleText5,R.id.tvLocaleText6,R.id.tvLocaleText7,R.id.cb_inputmode};
    	for(int id :localeText)
    	{
    		Tools.ToLocale(_Dialog.findViewById(id));
    	}
    }
    
    
    //上部按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (SaveFieldDetailInfo())
    			{
	    			if (m_Callback!=null) m_Callback.OnClick("字段", m_EditField);
    				_Dialog.dismiss();
    			}
	    	}
	    	
	    	//选择数据字段对话框
	    	if (Str.equals("SpinnerCallback"))   
	    	{
	    		//v1_DataDictionary dd = new v1_DataDictionary();
	    		String layerType = m_EditLayer.GetLayerProjecType();
	    		if(layerType.isEmpty())
	    		{
	    			layerType = "自定义工程";
	    		}
	    		
	    		if(layerType.equals("自定义图层"))
	    		{
	    			layerType = "自定义工程";
	    		}
	    		DialogDictSelector dd = new DialogDictSelector("林业",layerType,Tools.GetSpinnerValueOnID(_Dialog, R.id.sp_valuelist));
	    		dd.SetCallback(pCallback);
	    		dd.ShowDialog();
	    	}
	    	
	    	//数据字典选择完成后的回调
	    	if (Str.equals("数据字典"))
	    	{
	    		if (ExtraStr==null)  //置空
	    		{
					v1_DataBind.SetBindListSpinner(_Dialog, "值域", new ArrayList<String>(), R.id.sp_valuelist);
					_Dialog.findViewById(R.id.sp_valuelist).setTag("");
	    		} 
	    		else
	    		{
		    		//HashMap(ZDBM,ZDList)
	    			HashMap<String,Object> dataDicObject = (HashMap<String,Object>)ExtraStr;
	    			String[] items = new String[]{Tools.StrListToStr((List<String>)(dataDicObject.get("ZDList")))};
					v1_DataBind.SetBindListSpinner(_Dialog, "值域", Tools.StrArrayToList(items), R.id.sp_valuelist);
					_Dialog.findViewById(R.id.sp_valuelist).setTag(dataDicObject.get("ZDBM"));
	    		}
	    	}
		}};


	//字段操作完成后的回调
	private ICallback m_Callback = null;
	/**
	 * 设置字段操作完成后的回调
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	//当前字段，如果有默认值，为编辑状态
	private lkEditMode m_EditMode = lkEditMode.enNew;
	private v1_LayerField m_EditField = null;
	/**
	 * 设置当前正在编辑的字段类
	 * @param lf
	 */
	public void SetEditField(v1_LayerField lf)
	{
		this.m_EditField = lf;
	}
	
	//当前字段所属的图层
	private v1_Layer m_EditLayer = null;
	/**
	 * 设置当前字段所属的图层类
	 * @param lyr
	 */
	public void SetEditLayer(v1_Layer lyr)
	{
		this.m_EditLayer = lyr;
	}
	
	/**
	 * 保存字段的详细信息
	 * @return
	 */
	private boolean SaveFieldDetailInfo()
	{
		//获取字段值
		String FieldName = Tools.GetTextValueOnID(_Dialog,R.id.et_name);  //字段名称
		String FieldType = Tools.GetSpinnerValueOnID(_Dialog,R.id.sp_type);  //字段类型
		String FieldSize = Tools.GetTextValueOnID(_Dialog,R.id.et_size);  //字段大小
		String FieldDecimal = Tools.GetSpinnerValueOnID(_Dialog,R.id.sp_decimal);  //字段精度
		String FieldValue = _Dialog.findViewById(R.id.sp_valuelist).getTag().toString();  //字段值域
		boolean FieldValueInput = Tools.GetCheckBoxValueOnID(_Dialog,R.id.cb_inputmode);  //字段值域是否可输入
		
		//验证字段信息
		List<String> ErrorInfoList = new ArrayList<String>();
		if (FieldName.equals(""))ErrorInfoList.add("【字段名称】不能为空值！");
		for(v1_LayerField lf : this.m_EditLayer.GetFieldList())
		{
			if (lf.GetFieldName().equals(FieldName) && (!lf.GetFieldID().equals(this.m_EditField.GetFieldID())))ErrorInfoList.add("【字段名称】重复！");
		}
		if (FieldSize.equals(""))ErrorInfoList.add("【字段大小】不能为空值！");
		else if (Integer.parseInt(FieldSize)>255)ErrorInfoList.add("【字段大小】应小于255！");
		
		//显示错误信息
		if (ErrorInfoList.size()>0)
		{
			String ErrorInfo = Tools.JoinT("\r\n", ErrorInfoList);
			Tools.ShowMessageBox(_Dialog.getContext(), ErrorInfo);return false;
		}
		
		//为字段实体赋值
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
     * 加载字段信息
     */
    private void LoadFieldDetailInfo()
    {
    	//判读是否有默认字段，有则为编辑状态
    	if (this.m_EditField!=null)
    	{
    		this.m_EditMode = lkEditMode.enEdit;
    		
    		Tools.SetTextViewValueOnID(_Dialog,R.id.et_name,this.m_EditField.GetFieldName());  //字段名称
    		Tools.SetSpinnerValueOnID(_Dialog, R.id.sp_type, this.m_EditField.GetFieldTypeName());  //字段类型
    		Tools.SetTextViewValueOnID(_Dialog,R.id.et_size,this.m_EditField.GetFieldSize()+"");  //字段大小
    		Tools.SetSpinnerValueOnID(_Dialog,R.id.sp_decimal,this.m_EditField.GetFieldDecimal()+"");  //字段精度
    		
    		_Dialog.findViewById(R.id.sp_valuelist).setTag(this.m_EditField.GetFieldEnumCode());  //字段值域
    		
    		//String EnumStr = Tools.JoinT(",", this.m_EditField.GetFieldEnumList());
    		v1_DataBind.SetBindListSpinner(_Dialog, "",Tools.StrArrayToList(new String[]{this.m_EditField.GetFieldEnumCode()}) , R.id.sp_valuelist);
    		
    		Tools.SetCheckValueOnID(_Dialog,R.id.cb_inputmode,this.m_EditField.GetFieldEnumEdit());  //字段值域是否可输入
    		
    	} else
    	{
    		//新增字段
    		this.m_EditField = new v1_LayerField();
    		
    		//自动获取对应空闲数据字段
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
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadFieldDetailInfo();}});
    	_Dialog.show();
    }
    

}
