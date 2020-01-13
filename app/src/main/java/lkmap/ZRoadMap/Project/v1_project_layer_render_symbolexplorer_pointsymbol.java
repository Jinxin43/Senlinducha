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
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Symbol.PointSymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Config.v1_SymbolObject;
import lkmap.ZRoadMap.MyControl.v1_EditSpinnerDialog;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.MyControl.v1_ImageSpinnerDialog;

public class v1_project_layer_render_symbolexplorer_pointsymbol
{
	private v1_FormTemplate _Dialog = null; 
    public v1_project_layer_render_symbolexplorer_pointsymbol()
    {
    	_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.v1_project_layer_render_symbolexplorer_pointsymbol);
    	//_Dialog.ReSetSize(1f, -1f);
    	_Dialog.SetCaption("�༭�����");
    	_Dialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", pCallback);
    	_Dialog.SetButtonInfo("2,"+R.drawable.m_save+","+Tools.ToLocale("��� ")+" ,���", pCallback);

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
	    			m_SelectSymbolObject.SymbolBase64Str = m_Symbol.ToBase64();
	    			m_SelectSymbolObject.SymbolFigure = m_Symbol.ToFigureBitmap(64,40);
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
	private PointSymbol m_Symbol = new PointSymbol();
	public void SetSymbol(String Base64Str)
	{
		this.m_Symbol.CreateByBase64(Base64Str);
		this.m_SelectSymbolObject = new v1_SymbolObject();
		this.m_SelectSymbolObject.SymbolBase64Str = Base64Str;
		this.m_SelectSymbolObject.SymbolFigure = this.m_Symbol.ToFigureBitmap(this.m_Symbol.getIcon().getWidth(),this.m_Symbol.getIcon().getHeight());
	}
	
	private v1_SymbolObject m_SelectSymbolObject = null;
	
		
    /**
     * ���ط�����Ϣ���б�
     */
    private void LoadSymbolInfo()
    {
    	//������ʽ
    	v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_symbol);
    	v1_SymbolObject SO = new v1_SymbolObject();
    	SO.SymbolBase64Str = this.m_Symbol.ToBase64();
    	SO.SymbolFigure = this.m_Symbol.ToFigureBitmap(64,40);
    	
    	List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
    	SOList.add(SO);
    	ISD.SetImageItemList(SOList);
    	ISD.SetCallback(new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) {
				List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
		    	//����Ĭ�ϼ���ʽ
		    	for(int i=1;i<=3;i++)
		    	{
		    		PointSymbol PS = new PointSymbol();
		    		PS.setIcon(CreateSymbol("S"+i, Color.GRAY, 24));
		    		v1_SymbolObject SO1 = new v1_SymbolObject();
		    		SO1.SymbolBase64Str = PS.ToBase64();
		    		SO1.SymbolFigure = PS.ToFigureBitmap(24,24);
		    		SO1.SymbolName = "S"+i;
		    		SOList.add(SO1);
		    	}
	    		v1_project_layer_render_symbolexplorer_pointsymbol_custom plrs = new v1_project_layer_render_symbolexplorer_pointsymbol_custom();
	    		plrs.SetSymbolObjectList(SOList);
	    		plrs.SetCallback(new ICallback(){
					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_SelectSymbolObject = (v1_SymbolObject)ExtraStr;
				    	List<v1_SymbolObject> SOList = new ArrayList<v1_SymbolObject>();
				    	SOList.add(m_SelectSymbolObject);
				    	v1_ImageSpinnerDialog ISD = (v1_ImageSpinnerDialog)_Dialog.findViewById(R.id.isd_symbol);
				    	ISD.SetImageItemList(SOList);
				    	//�Զ�����ʽ����ɫ����
						((v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color)).setEnabled(true);
						CreatePreview();  //����Ԥ��ͼ
					}});
	    		plrs.ShowDialog();
			}});
    	
      	//������ɫ
    	v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color);
    	ESD.setEnabled(false);
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
						CreatePreview();  //����Ԥ��ͼ
					}});
	    		plrc.ShowDialog();
				
			}});
    	
    	//���Ŵ�С
    	EditText ESD_Size = (EditText)_Dialog.findViewById(R.id.et_size);
    	ESD_Size.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	ESD_Size.setText(this.m_SelectSymbolObject.SymbolFigure.getWidth()+"");
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
    	//��ɫ
    	v1_EditSpinnerDialog ESD = (v1_EditSpinnerDialog)_Dialog.findViewById(R.id.sp_color);
    	int color = Integer.parseInt(ESD.getEditTextView().getTag()+"");
    	
    	//��С
    	EditText ESD_Size = (EditText)_Dialog.findViewById(R.id.et_size);
    	String sizeStr = ESD_Size.getText()+"";if(sizeStr.equals(""))sizeStr="1";
    	int size = Integer.parseInt(sizeStr);
    	
    	Bitmap viewMap = null;
    	if (!this.m_SelectSymbolObject.SymbolName.equals(""))
    	{
    		viewMap = this.CreateSymbol(this.m_SelectSymbolObject.SymbolName, color, size);
    	} 
    	else
    	{
    		viewMap = Bitmap.createBitmap(size,size,Config.ARGB_8888);
    		Canvas g = new Canvas(viewMap);
    		//g.drawBitmap(this.m_SelectSymbolObject.SymbolFigure, 0, 0, null);
    		g.drawBitmap(this.m_SelectSymbolObject.SymbolFigure, new Rect(0,0,this.m_SelectSymbolObject.SymbolFigure.getWidth(),this.m_SelectSymbolObject.SymbolFigure.getHeight()),
    					new Rect(0,0,size,size), null);
    	}
    	ImageView IV = (ImageView)_Dialog.findViewById(R.id.ivPreview);
    	IV.setImageBitmap(viewMap);
		this.m_Symbol.setIcon(viewMap);
    }
    
    /**
     * ���������
     * @return
     */
    private Bitmap CreateSymbol(String SymbolID,int SymbolColor,int SymbolSize)
    {
    	Paint paint = new Paint();
    	paint.setColor(SymbolColor);
		Bitmap bp = Bitmap.createBitmap(SymbolSize,SymbolSize,Config.ARGB_8888);
		Canvas g = new Canvas(bp);
		if (SymbolID.equals("S1"))   //Բ��
		{
			paint.setStyle(Style.FILL);
			g.drawCircle((float)SymbolSize/2f, (float)SymbolSize/2f, (float)SymbolSize/2f, paint);
		}
		if (SymbolID.equals("S2"))   //������
		{
			paint.setStyle(Style.FILL);
			g.drawRect(0, 0, SymbolSize, SymbolSize, paint);
		}
		if (SymbolID.equals("S3"))   //������
		{
			paint.setStyle(Style.FILL);
			Path path = new Path();
			path.moveTo(0, SymbolSize);
			path.lineTo(SymbolSize, SymbolSize);
			path.lineTo(SymbolSize/2f, 0);
			path.lineTo(0, SymbolSize);
			g.drawPath(path, paint);
		}
		return bp;
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
