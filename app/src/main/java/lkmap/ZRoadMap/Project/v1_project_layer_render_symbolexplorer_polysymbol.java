package lkmap.ZRoadMap.Project;

import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Symbol.PolySymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;

public class v1_project_layer_render_symbolexplorer_polysymbol
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_render_symbolexplorer_polysymbol()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_symbolexplorer_polysymbol);
    	//_Dialog.ReSetSize(1f, -1f);
    	_Dialog.SetCaption("�༭�����");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.m_save+","+Tools.ToLocale("��� ")+" ,���", pCallback);
    	//this.SetButtonEnable(false);
    }
    
    /**
     * ���ð�ť�Ŀ�����
     * @param enable
     */
    private void SetButtonEnable(boolean enabled)
    {
    	_Dialog.GetButton("1").setEnabled(enabled);
    	_Dialog.GetButton("2").setEnabled(enabled);
    }
    
    //�ϲ���ť�¼�
    private ICallback pCallback = new ICallback(){
		@Override
		public void OnClick(String Str, Object ExtraStr)
		{
	    	if (Str.equals("ȷ��"))
	    	{
	    		if (m_Callback!=null)
    			{
		    		m_Callback.OnClick("���ſ�", m_SelectSymbolObject);
    			}
	    		_Dialog.dismiss();
	    	}
	    	if (Str.equals("���"))
	    	{
	    		
	    	}
		}};
		
	
	//�ص�
	private ICallback m_Callback = null;
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
		
	//����
	private PolySymbol m_Symbol = new PolySymbol();
	public void SetSymbol(String Base64Str)
	{
		this.m_Symbol.CreateByBase64(Base64Str);
		this.m_SelectSymbolObject = new v1_SymbolObject();
		this.m_SelectSymbolObject.SymbolBase64Str = Base64Str;
		this.m_SelectSymbolObject.SymbolFigure = this.m_Symbol.ToFigureBitmap(64, 48);
	}
	
	private v1_SymbolObject m_SelectSymbolObject = null;
	
		
    /**
     * ���ط�����Ϣ���б�
     */
    private void LoadSymbolInfo()
    {
      	//����ɫ
    	v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_pcolor);
    	ESD.getEditTextView().setEnabled(false);
    	ESD.getEditTextView().setFocusable(false);
    	ESD.getEditTextView().setBackgroundColor(this.m_Symbol.getPStyle().getColor());
    	ESD.getEditTextView().setTag(this.m_Symbol.getPStyle().getColor());
    	ESD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
	    		v1_project_layer_render_colorpicker plrc = new v1_project_layer_render_colorpicker();
	    		plrc.SetICallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_EditSpinnerDialog ESDColor = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_pcolor);
						ESDColor.getEditTextView().setBackgroundColor(Color.parseColor(ExtraStr.toString()));
						ESDColor.getEditTextView().setTag(Color.parseColor(ExtraStr.toString()));
						CreatePreview();  //����Ԥ��ͼ
					}});
	    		plrc.ShowDialog();
				
			}});
    	
      	//������ɫ
    	v1_EditSpinnerDialog ESD1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_lcolor);
    	ESD1.getEditTextView().setEnabled(false);
    	ESD.getEditTextView().setFocusable(false);
    	ESD1.getEditTextView().setBackgroundColor(this.m_Symbol.getLStyle().getColor());
    	ESD1.getEditTextView().setTag(this.m_Symbol.getLStyle().getColor());
    	ESD1.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
	    		v1_project_layer_render_colorpicker plrc = new v1_project_layer_render_colorpicker();
	    		plrc.SetICallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						v1_EditSpinnerDialog ESDColor = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_lcolor);
						ESDColor.getEditTextView().setBackgroundColor(Color.parseColor(ExtraStr.toString()));
						ESDColor.getEditTextView().setTag(Color.parseColor(ExtraStr.toString()));
						CreatePreview();  //����Ԥ��ͼ
					}});
	    		plrc.ShowDialog();
				
			}});
    	
    	//���߿��
    	EditText ESD_Size = (EditText)_Dialog.findViewById(R.id.et_size);
    	ESD_Size.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	ESD_Size.setText(this.m_Symbol.getLStyle().getStrokeWidth()+"");
    	ESD_Size.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) 
			{
				CreatePreview();  //����Ԥ��ͼ
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,int arg2, int arg3) {}
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}});
    	
    	this.CreatePreview();
    }
    
    /**
     * ����ʾ��ͼ
     */
    private void CreatePreview()
    {
    	//����ɫ
    	v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_pcolor);
    	int pcolor = Integer.parseInt(ESD.getEditTextView().getTag()+"");
    	
    	//������ɫ
    	v1_EditSpinnerDialog ESD1 = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_lcolor);
    	int lcolor = Integer.parseInt(ESD1.getEditTextView().getTag()+"");
    	
    	//���߿��
    	EditText ESD_Size = (EditText)_Dialog.findViewById(R.id.et_size);
    	String sizeStr = ESD_Size.getText()+"";if (sizeStr.equals(""))sizeStr="0";
    	float size = Float.parseFloat(sizeStr);
    	
    	this.m_Symbol.CreateByBase64(Tools.ColorToHexStr(pcolor)+","+Tools.ColorToHexStr(lcolor)+","+size);
		this.m_SelectSymbolObject.SymbolBase64Str = this.m_Symbol.ToBase64();
		this.m_SelectSymbolObject.SymbolFigure = this.m_Symbol.ToFigureBitmap(64, 48);
		
    	Bitmap viewMap = this.m_SelectSymbolObject.SymbolFigure;
    	ImageView IV = (ImageView)_Dialog.findViewById(R.id.ivPreview);
    	IV.setImageBitmap(viewMap);
    }

    public void ShowDialog()
    {
    	//�˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) {LoadSymbolInfo();}});
    	_Dialog.show();
    }
    
}
