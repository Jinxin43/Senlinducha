package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Symbol.LineSymbol;
import lkmap.Symbol.PointSymbol;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_ImageSpinnerDialog;
import lkmap.ZRoadMap.Project.v1_project_layer.ViewClick;

public class v1_project_layer_render_symbolexplorer_linesymbol
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_render_symbolexplorer_linesymbol()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_symbolexplorer_linesymbol);
    	//_Dialog.ReSetSize(1f, 0.96f);
    	_Dialog.SetCaption("编辑线符号");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.m_save+","+Tools.ToLocale("另存 ")+" ,另存", pCallback);
    	
    	_Dialog.findViewById(R.id.btUp).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.btDown).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.btAdd).setOnClickListener(new ViewClick());
    	_Dialog.findViewById(R.id.btDel).setOnClickListener(new ViewClick());
    	
    	//this.SetButtonEnable(false);
    	
    }
    
    class ViewClick implements View.OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		pCallback.OnClick(Tag, null);
    	}
    }
    
    /**
     * 设置按钮的可用性
     * @param enable
     */
    private void SetButtonEnable(boolean enabled)
    {
    	//_Dialog.GetButton("1").setEnabled(enabled);
    	//_Dialog.GetButton("2").setEnabled(enabled);
		
		//将当前选中的层次信息提取并显示出来
		//样式
    	((v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_symbol)).setEnabled(enabled);
    	
      	//边线颜色
    	((v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color)).setEnabled(enabled);

    	//大小
    	((EditText)_Dialog.findViewById(R.id.et_size)).setEnabled(enabled);
    }
    
    //上部按钮事件
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("确定"))
	    	{
	    		if (m_Callback!=null)
    			{
	    			CreatePreview();
	    			m_SelectSymbolObject.SymbolBase64Str = m_Symbol.ToBase64();
	    			m_SelectSymbolObject.SymbolFigure = m_Symbol.ToFigureBitmap(200,40);
		    		m_Callback.OnClick("符号库", m_SelectSymbolObject);
    			}
	    		_Dialog.dismiss();
	    	}
	    	if (Str.equals("另存"))
	    	{
	    		
	    	}
	    	
	    	if (Str.equals("向上") || Str.equals("向下"))
	    	{
	    		if (m_SelectSymbolObject==null) return;
	    		int idx = Integer.parseInt(m_SelectSymbolObject.SymbolName);
	    		//最上、最下了
	    		if (idx==0 && Str.toString().equals("向上")){Tools.ShowToast(_Dialog.getContext(), "已经在最上层！"); return;}
	    		if (idx==m_SymbolObjectLevelList.size()-1 && Str.toString().equals("向下")){Tools.ShowToast(_Dialog.getContext(), "已经在最下层！"); return;}
	    		if (Str.toString().equals("向上"))idx--;
	    		if (Str.toString().equals("向下"))idx++;
	    		m_SymbolObjectLevelList.remove(m_SelectSymbolObject);
	    		m_SymbolObjectLevelList.add(idx, m_SelectSymbolObject);
	    		LoadSymbolLevel(idx);
	    	}

	    	if (Str.equals("新增"))
	    	{
	    		v1_SymbolObject SO = new v1_SymbolObject();
	    		SO.SymbolBase64Str = "#000000,2";
	    		LineSymbol LS = new LineSymbol();
	    		LS.CreateByBase64(SO.SymbolBase64Str);
	    		SO.SymbolFigure = LS.ToFigureBitmap(200,40);
	    		m_SymbolObjectLevelList.add(SO);
	    		LoadSymbolLevel(m_SymbolObjectLevelList.size()-1);
	    	}
	    	if (Str.equals("删除"))
	    	{
	    		if (m_HeaderListViewDataItemList==null) return;
	    		for(int i=m_HeaderListViewDataItemList.size()-1;i>=0;i--)
	    		{
	    			if (Boolean.parseBoolean(m_HeaderListViewDataItemList.get(i).get("D1")+""))
	    			{
	    				m_SymbolObjectLevelList.remove(i);
	    			}
	    		}
	    		LoadSymbolLevel(m_SymbolObjectLevelList.size()-1);
	    	}
	    	
	    	//选中层次后
	    	if (Str.equals("列表选项"))
	    	{
	    		m_SelectSymbolObject = (v1_SymbolObject)((HashMap<String,Object>)ExtraStr).get("SymbolObject");
	    		
	    		//将当前选中的层次信息提取并显示出来
	    		//样式
	        	v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_symbol);
	        	List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
	        	SOList.add(m_SelectSymbolObject);
	        	ISD.SetImageItemList(SOList);
	        	
	          	//边线颜色
	        	String color = m_SelectSymbolObject.SymbolBase64Str.split(",")[0];
	        	v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color);
	        	ESD.getEditTextView().setBackgroundColor(Color.parseColor(color));
	        	ESD.getEditTextView().setTag(Color.parseColor(color));
	        	
	        	//大小
	        	String size = m_SelectSymbolObject.SymbolBase64Str.split(",")[1];
	        	EditText ESD_Size = (EditText)_Dialog.findViewById(R.id.et_size);
	        	ESD_Size.setText(size);
	        	
	        	SetButtonEnable(true);
	    	}
		}};
		
	
	//回调
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//符号
	private LineSymbol m_Symbol = new LineSymbol();
	private List<v1_SymbolObject> m_SymbolObjectLevelList = new ArrayList<v1_SymbolObject>();
	
	public void SetSymbol(String Base64Str)
	{
		this.m_Symbol.CreateByBase64(Base64Str);
		String[] symList = Base64Str.split("@");
		for(String sym:symList)
		{
			LineSymbol LS = new LineSymbol();
			LS.CreateByBase64(sym);
			v1_SymbolObject SO = new v1_SymbolObject();
			SO.SymbolBase64Str = sym;
			SO.SymbolFigure = LS.ToFigureBitmap(200, 40);
			SO.SymbolName = this.m_SymbolObjectLevelList.size()+"";
			this.m_SymbolObjectLevelList.add(SO);
		}
	}
	
	private v1_SymbolObject m_SelectSymbolObject = null;
	
		
    /**
     * 加载符号信息到列表
     */
    private void LoadSymbolInfo()
    {
    	//符号样式
    	v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_symbol);
    	v1_SymbolObject SO = new v1_SymbolObject();
    	SO.SymbolBase64Str = this.m_Symbol.ToBase64();
    	SO.SymbolFigure = this.m_Symbol.ToFigureBitmap(200, 40);
    	
    	List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
    	SOList.add(SO);
    	ISD.SetImageItemList(SOList);
    	ISD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
		    	//加载默认简单样式
		    	for(int i=0;i<=2;i++)
		    	{
		    		LineSymbol PS = new LineSymbol();
		    		PS.CreateByBase64(CreateSymbol("L"+i));
		    		v1_SymbolObject SO1 = new v1_SymbolObject();
		    		SO1.SymbolBase64Str = PS.ToBase64();
		    		SO1.SymbolFigure = PS.ToFigureBitmap(200, 40);
		    		SO1.SymbolName = "L"+i;
		    		SOList.add(SO1);
		    	}
	    		v1_project_layer_render_symbolexplorer_linesymbol_custom plrs = new v1_project_layer_render_symbolexplorer_linesymbol_custom();
	    		plrs.SetSymbolObjectList(SOList);
	    		plrs.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_SelectSymbolObject.SymbolBase64Str = ((v1_SymbolObject)ExtraStr).SymbolBase64Str;
						m_SelectSymbolObject.SymbolFigure  = ((v1_SymbolObject)ExtraStr).SymbolFigure;
				    	List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
				    	SOList.add(m_SelectSymbolObject);
				    	v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_symbol);
				    	ISD.SetImageItemList(SOList);
				    	RefreshLevel();CreatePreview();  //创建预览图
					}});
	    		plrs.ShowDialog();
			}});
    	
      	//边线颜色
    	v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color);
    	ESD.getEditTextView().setEnabled(false);
    	ESD.getEditTextView().setBackgroundColor(Color.GRAY);
    	ESD.getEditTextView().setTag(Color.GRAY);
    	ESD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
	    		v1_project_layer_render_colorpicker plrc = new v1_project_layer_render_colorpicker();
	    		plrc.SetICallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_EditSpinnerDialog ESDColor = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color);
						ESDColor.getEditTextView().setBackgroundColor(Color.parseColor(ExtraStr.toString()));
						ESDColor.getEditTextView().setTag(Color.parseColor(ExtraStr.toString()));
						RefreshLevel();CreatePreview();  //创建预览图
					}});
	    		plrc.ShowDialog();
				
			}});
    	
    	
    	//边线宽度
    	EditText ESD_Size = (EditText)_Dialog.findViewById(R.id.et_size);
    	ESD_Size.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	ESD_Size.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) 
			{
				RefreshLevel();CreatePreview();  //创建预览图
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}});

    	
    	//层次列表
    	this.LoadSymbolLevel(0);

    	this.CreatePreview();
    }
    
    private List<HashMap<String,Object>> m_HeaderListViewDataItemList = null;
    private v1_HeaderListViewFactory m_HVF = null;
    /**
     * 加载符号层次列表,selectIndex表示当前选中的层次
     */
    private void LoadSymbolLevel(int selectIndex)
    {
    	//绑定符号列表
    	if (this.m_HVF==null)
    	{
	    	this.m_HVF = new v1_HeaderListViewFactory();
	    	this.m_HVF.SetHeaderListView(_Dialog.findViewById(R.id.symbol_list), "线符号定义列表",pCallback);
	    	this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String,Object>>();
    	}
    	this.m_HeaderListViewDataItemList.clear();
    	int idx = 0;
    	for(v1_SymbolObject SO:this.m_SymbolObjectLevelList)
    	{
    		SO.SymbolName = idx+"";idx++;  //整理索引
        	HashMap<String,Object> hm = new HashMap<String,Object>();
        	hm.put("D1", false);  					//可选择
        	hm.put("D2", SO.SymbolFigure);  		//符号
        	hm.put("SymbolObject", SO);				//整个符号SymbolObject
        	this.m_HeaderListViewDataItemList.add(hm);
    	}
    	this.m_HVF.BindDataToListView(m_HeaderListViewDataItemList,pCallback);
    	
    	//不可用图层按钮，如编辑之类的
    	this.SetButtonEnable(false);
    	
    	this.m_SelectSymbolObject=null;
    	
    	if (selectIndex>=0 && selectIndex<this.m_HeaderListViewDataItemList.size())
    		this.m_HVF.SetSelectItemIndex(selectIndex, pCallback);
    }
    
    
    //刷新指定的层次
    private void RefreshLevel()
    {
    	if (this.m_SelectSymbolObject==null)return;
    	
    	//颜色
    	v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color);
    	int color = Integer.parseInt(ESD.getEditTextView().getTag()+"");
    	
    	//宽度
    	EditText ESD_Size = (EditText)_Dialog.findViewById(R.id.et_size);
    	String sizeStr = ESD_Size.getText()+"";if (sizeStr.equals(""))sizeStr="1";
    	float width = Float.parseFloat(sizeStr);
    	
    	//构建新符号
    	String NewSymbolStr = Tools.ColorToHexStr(color)+","+width;
    	String[] SymInfo = this.m_SelectSymbolObject.SymbolBase64Str.split(",");
    	if (SymInfo.length==3)NewSymbolStr+=","+SymInfo[2];
    	this.m_SelectSymbolObject.SymbolBase64Str = NewSymbolStr;
    	this.m_Symbol.CreateByBase64(this.m_SelectSymbolObject.SymbolBase64Str);
    	this.m_SelectSymbolObject.SymbolFigure = this.m_Symbol.ToFigureBitmap(200,40);
    	
    	//更新层次列表
    	for(HashMap<String,Object> hm:this.m_HeaderListViewDataItemList)
    	{
    		v1_SymbolObject SO = (v1_SymbolObject)hm.get("SymbolObject");
    		if (SO.SymbolName.equals(this.m_SelectSymbolObject.SymbolName))
    		{
    	    	hm.put("D2", this.m_SelectSymbolObject.SymbolFigure);  		//符号
    	    	hm.put("SymbolObject", this.m_SelectSymbolObject);				//整个符号SymbolObject
    		}
    	}
    	this.m_HVF.notifyDataSetInvalidated();
    }
    
    /**
     * 创建示例图
     */
    private void CreatePreview()
    {
    	List<String> SymbolInfoList = new ArrayList<String>();
    	for(v1_SymbolObject SO:this.m_SymbolObjectLevelList)
    	{
    		SymbolInfoList.add(SO.SymbolBase64Str);
    	}
    	this.m_Symbol.CreateByBase64(Tools.JoinT("@", SymbolInfoList));
    	Bitmap viewMap = this.m_Symbol.ToFigureBitmap(200,40);
    	ImageView IV = (ImageView)_Dialog.findViewById(R.id.ivpreview);
    	IV.setImageBitmap(viewMap);
    }
    
    /**
     * 创建默认符号，颜色，宽度，样式
     * @return
     */
    private String CreateSymbol(String SymbolID)
    {
    	int color = Color.BLACK;

		if (SymbolID.equals("L0"))   //实线
		{
			return Tools.ColorToHexStr(color)+",2";
		}
		if (SymbolID.equals("L1"))   //虚线
		{
			return Tools.ColorToHexStr(color)+",2,10*5";
		}
		if (SymbolID.equals("L2"))   //虚线
		{
			return Tools.ColorToHexStr(color)+",2,10*2";
		}
		return Tools.ColorToHexStr(color)+",2";
    }

    public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadSymbolInfo();}});
    	_Dialog.show();
    }
    

}
