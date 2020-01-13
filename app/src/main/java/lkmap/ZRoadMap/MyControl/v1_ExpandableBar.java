package lkmap.ZRoadMap.MyControl;

import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import lkmap.Tools.Tools;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import dingtu.ZRoadMap.Data.ICallback;

public class v1_ExpandableBar extends LinearLayout 
{
	private Context m_Context = null;
	public v1_ExpandableBar(Context context) {
		super(context);
		this.m_Context = context;
		// TODO Auto-generated constructor stub
		//this.Init();
	}
	public v1_ExpandableBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.m_Context = context;
		// TODO Auto-generated constructor stub
		//this.Init();
	}
	
	/**
	 * 创建头部条
	 * @param headerCaptoin
	 */
	public void CreateBarHeader(String headerCaptoin)
	{
		LayoutInflater inflater = (LayoutInflater) this.m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View barHeader = inflater.inflate(R.layout.v1_expandablebar_header, null); //layout定义是: View layout
		LayoutParams LP = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		barHeader.setLayoutParams(LP);
		this.addView(barHeader);
		
		//标题 
		((TextView)barHeader.findViewById(R.id.tv_header)).setText(headerCaptoin);
		
		//头部条点击，展开或关才
		this.m_HeaderBar = (LinearLayout)barHeader.findViewById(R.id.ll_headermore);
		this.m_HeaderBar.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch(arg1.getAction() & MotionEvent.ACTION_MASK)
				{
					case MotionEvent.ACTION_DOWN:
						//arg0.setBackgroundColor(Color.BLUE);
						break;
					case MotionEvent.ACTION_UP:
						//展开
						if (arg0.findViewById(R.id.image1).getVisibility()==View.VISIBLE)
						{
							arg0.findViewById(R.id.image1).setVisibility(View.GONE);
							arg0.findViewById(R.id.image2).setVisibility(View.VISIBLE);
							m_ItemListBar.setVisibility(View.VISIBLE);
						}
						else  //收起
						{
							arg0.findViewById(R.id.image1).setVisibility(View.VISIBLE);
							arg0.findViewById(R.id.image2).setVisibility(View.GONE);
							m_ItemListBar.setVisibility(View.GONE);
						}
						//arg0.setBackgroundColor(Color.TRANSPARENT);
						break;							
				}
				return true;
			}});
		this.m_ItemListBar = (LinearLayout)barHeader.findViewById(R.id.ll_list);

	}

//	private void Init()
//	{
//
//		this.CreateBarHeader("头部条");
//		
//		this.SetItemList();
//	}
	
	//头部条
	private LinearLayout m_HeaderBar = null;

	//数据显示框
	private LinearLayout m_ItemListBar = null;
	
	private ICallback m_Callback = null;
	/**
	 * 设置选中列表项后的回调
	 * @param cb
	 */
	public void SetCallback(ICallback cb){this.m_Callback = cb;}
	
	private float m_OffsetX = 0,m_OffsetY = 0;
	
	/**
	 * 设置显示数据
	 */
	public void SetItemList(List<HashMap<String,Object>> itemList)
	{
		for(HashMap<String,Object> item:itemList)
		{
			LayoutInflater inflater = (LayoutInflater) this.m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View itemView = inflater.inflate(R.layout.v1_expandablebar_item_layerlist, null); 
			itemView.setTag(item);
			((TextView)itemView.findViewById(R.id.tv_d1)).setText(item.get("D1").toString());
			((TextView)itemView.findViewById(R.id.tv_d2)).setText(item.get("D2").toString());
			
			LayoutParams LP = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,Tools.DPToPix(48));
			itemView.setLayoutParams(LP);
			this.m_ItemListBar.addView(itemView);
			itemView.setLongClickable(true); 
			itemView.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					switch(arg1.getAction() & MotionEvent.ACTION_MASK)
					{
						case MotionEvent.ACTION_DOWN:
							arg0.setBackgroundColor(Color.BLUE);
							m_OffsetX = arg1.getX();
							m_OffsetY = arg1.getY();
							break;
						case MotionEvent.ACTION_MOVE:
							if (Math.abs(m_OffsetY-arg1.getY())>3)
							arg0.setBackgroundColor(Color.TRANSPARENT);
							break;
						case MotionEvent.ACTION_UP:
							arg0.setBackgroundColor(Color.TRANSPARENT);
							if (m_Callback!=null)m_Callback.OnClick("选项", arg0.getTag());
							break;							
					}
					return true;
				}});
		}
	}

}
