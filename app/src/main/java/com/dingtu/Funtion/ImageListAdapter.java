package com.dingtu.Funtion;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class ImageListAdapter extends BaseAdapter {

	//当前选中的项目索引
	private List<HashMap<String,Object>> m_DataList = null;
	private int m_LayoutId = 0;
	private String[] m_ObjField ;
	private int[] m_ViewId;
	private LayoutInflater mInflater = null;
	
	public ImageListAdapter(Context context,List<HashMap<String,Object>> list,int layoutid,String[] objField,int[] viewid)
	{
		if (this.mInflater==null)
			this.mInflater = LayoutInflater.from(context);
		this.m_DataList = list;
		this.m_LayoutId = layoutid;
		this.m_ObjField = objField;
		this.m_ViewId = viewid;
	}
	
	@Override
	public int getCount() {
		return this.m_DataList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.m_DataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if (convertView==null)
		{
			convertView = mInflater.inflate(this.m_LayoutId, null); 
		}
		    
		final HashMap<String,Object> obj = (HashMap<String,Object>)this.getItem(position);
		for(int i=0;i<this.m_ViewId.length;i++)
		{
			//显示可用列
			View v = convertView.findViewById(this.m_ViewId[i]);

		    //分情况赋值
	    	String VType = v.getClass().getName();
	    	if (VType.equals("android.widget.TextView"))
	    	{
			    TextView tv = (TextView) v;
			    tv.setText(obj.get(this.m_ObjField[i]).toString()); 
	    	}
	    	
	    	if (VType.equals("android.widget.CheckBox"))
	    	{
	    		CheckBox cb = (CheckBox) v;
	    		cb.setChecked(Boolean.parseBoolean(obj.get(this.m_ObjField[i]).toString()));
	    	}
	    	
	    	
	    	if (VType.equals("android.widget.ImageView"))
	    	{
	    		ImageView iv = (ImageView)v;
	    		if (i<this.m_ObjField.length)
	    		{
	    			if (obj.get(this.m_ObjField[i])!=null)
	    				try
	    				{
	    					Bitmap bitmap = revitionImageSize(obj.get(this.m_ObjField[i])+"");
	    					iv.setImageBitmap(bitmap);
	    				}
	    				catch(Exception ex)
	    				{
	    					
	    				}
	    				
	    		}
	    	}

		}
		
		return convertView;
	}
	
	public Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
//		options.inJustDecodeBounds = true;
//		BitmapFactory.decodeStream(in, null, options);
//		in.close();
		int i = 4;
		Bitmap bitmap = null;
//		while (true) 
//		{
//			if ((options.outWidth >> i <= 512)
//					&& (options.outHeight >> i <= 512)) 
//			{
//				in = new BufferedInputStream(
//						new FileInputStream(new File(path)));
//				options.inSampleSize = (int) Math.pow(2.0D, i);
//				options.inJustDecodeBounds = false;
//				bitmap = BitmapFactory.decodeStream(in, null, options);
//				break;
//			}
//			i += 1;
//		}
		options.inSampleSize = (int) Math.pow(2.0D, i);
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeStream(in, null, options);
		in.close();
		return bitmap;
	}

}
