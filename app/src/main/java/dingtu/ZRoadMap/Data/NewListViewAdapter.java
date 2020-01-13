package dingtu.ZRoadMap.Data;

import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;


public class NewListViewAdapter
{
	public NewListViewAdapter(){}
	ListView _lvList = null;
	int _selectIndex = -1;
	public void SetAdapterList(Context C,ListView lv,String[] StrList,int selectIndex,final ICallback callback)
	{
		_lvList=lv;_selectIndex = selectIndex;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(C,android.R.layout.simple_list_item_1,StrList);  
        lv.setAdapter(adapter);  
 
		lv.setOnItemClickListener(new OnItemClickListener() {
		   @Override
		    public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
		   {
			   {          
				   ListView lv = (ListView)parent;
				   for(int i=0;i<parent.getCount();i++)
				   {
					   if (parent.getChildAt(i)!=null)parent.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
				   }
				   view.setBackgroundColor(Color.RED);
				   callback.OnClick(((TextView)view).getText().toString(),position+"");
				}
		    }
		});
		
		lv.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				   for(int i=0;i<arg0.getCount();i++)
				   {
					   if (arg0.getChildAt(i)!=null)arg0.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
				   }
				   arg1.setBackgroundColor(Color.RED);
				   callback.OnClick(((TextView)arg1).getText().toString(),arg2+"");
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}});


		if (selectIndex>=0 && selectIndex<StrList.length)
			{
				lv.postDelayed(new Runnable() {
			        @Override
			        public void run() {
			        	_lvList.requestFocusFromTouch();
			        	_lvList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			        	_lvList.setSelected(true);
			        	_lvList.setSelection(_selectIndex);
			        	_lvList.setItemChecked(_selectIndex, true);

			        }
			    }, 500);

			}
		
		
	}



}

