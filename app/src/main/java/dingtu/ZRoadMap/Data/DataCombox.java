package dingtu.ZRoadMap.Data;

import java.util.ArrayList;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Tools.Tools;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class DataCombox extends LinearLayout 
{
		private EditText tv;
		private ImageButton ibt;
		private Context C;
	    public DataCombox(Context context, AttributeSet attrs) 
	    {
			super(context, attrs);
			C=context;
			// TODO Auto-generated constructor stub
			this.setOrientation(HORIZONTAL);
	        tv = new EditText(context);
	        LinearLayout.LayoutParams LP1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT);
	        LP1.weight=2000;
	        addView(tv, LP1);

	        ibt = new ImageButton(context);
	        //ib.setImageDrawable(R.drawable.l_down_arrow_combox);
	        LinearLayout.LayoutParams LP2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT);
	        LP2.leftMargin=-6;
	        LP2.weight=1;
	        addView(ibt, LP2);
	        ibt.setImageDrawable(getResources().getDrawable(R.drawable.l_down_arrow_combox));
	        ibt.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					ShowOptionMessageBox();
				}});
		}
	    
	    private ListAdapter _Adapter = null;
	    public void setAdapter(ListAdapter adapter)
	    {
	    	_Adapter = adapter;
	    }
	    
	    public void setText(String v)
	    {
	    	tv.setText(v);
	    }
	    public String getText()
	    {
	    	return tv.getText().toString();
	    }
	    private String _Title = "选项";
	    public void setPrompt(String prt) 
	    {
	    	_Title = prt;
	    }

	    private boolean _MultiSelect = false;
	    public void SetMultiSelectMode(boolean _Mode)
	    {
	    	_MultiSelect = _Mode;
	    }
	    
	    //设置数据输入模式
	    public void SetNumberDecimalMode()
	    {
	    	tv.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
	    	tv.setSingleLine();
	    }
	    
	    //显示选项对话框 
	    private boolean[] _SelectItemBoolean = null;
	    private void ShowOptionMessageBox()
	    {
			// 创建AlertDialog        
			AlertDialog.Builder menuDialog = new AlertDialog.Builder(C); 
			//menuDialog.setIcon(R.drawable.fullscreen);
			menuDialog.setTitle(_Title);
			//menuDialog.setView(listView);
			List<String> StrList = new ArrayList<String>();
			if (_Adapter!=null)
			{
				_SelectItemBoolean = new boolean[_Adapter.getCount()];
				for(int i=0;i<_Adapter.getCount();i++)
				{
					StrList.add(String.valueOf(_Adapter.getItem(i)));
					_SelectItemBoolean[i]=false;
				}
			}

			if (!_MultiSelect)
			{
				menuDialog.setItems(lkmap.Tools.Tools.StrListToArray(StrList), new android.content.DialogInterface.OnClickListener(){
	
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						tv.setText(String.valueOf(_Adapter.getItem(arg1)));
						SetEditTextFocus();
						arg0.dismiss();
					}});
			} else
			{
				menuDialog.setMultiChoiceItems(lkmap.Tools.Tools.StrListToArray(StrList),_SelectItemBoolean,new android.content.DialogInterface.OnMultiChoiceClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1,
							boolean arg2) {
						_SelectItemBoolean[arg1]=arg2;
						
					}});
				menuDialog.setPositiveButton("确定", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						List<String> retStr = new ArrayList<String>();
						for(int i=0;i<_SelectItemBoolean.length;i++)
						{
							if (_SelectItemBoolean[i])retStr.add(String.valueOf(_Adapter.getItem(i)));
						}
						tv.setText(lkmap.Tools.Tools.StrListToStr(retStr));
						SetEditTextFocus();
					}});
			}

			menuDialog.setNegativeButton("取消",  new  DialogInterface.OnClickListener()
	        {
	            @Override
	             public   void  onClick(DialogInterface dialog,  int which)
	            {
	                dialog.dismiss();
	            }
	        });

			menuDialog.show();
	    }
	    
	    private void SetEditTextFocus()
	    {
	    	//tv.setFocusable(true);tv.setSelected(true);tv.setSelection(tv.getText().length());
	    }
}
