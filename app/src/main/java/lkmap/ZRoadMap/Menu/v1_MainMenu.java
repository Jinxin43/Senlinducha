package lkmap.ZRoadMap.Menu;
import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;
import dingtu.ZRoadMap.PubVar;
public class v1_MainMenu extends PopupWindow
{

	private View mMenuView;

	public v1_MainMenu(Activity context,OnClickListener itemsOnClick) 
	{
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mMenuView = inflater.inflate(R.layout.v1_menu_mainmenu, null);

		//����SelectPicPopupWindow��View
		this.setContentView(mMenuView);
		//����SelectPicPopupWindow��������Ŀ�
		this.setWidth(LayoutParams.FILL_PARENT);
		//����SelectPicPopupWindow��������ĸ�
		this.setHeight(LayoutParams.WRAP_CONTENT);
		//����SelectPicPopupWindow��������ɵ��
		this.setFocusable(true);
		//����SelectPicPopupWindow�������嶯��Ч��
		//this.mMenuView.setAnimation(R.style.PopupAnimation);
		//ʵ����һ��ColorDrawable��ɫΪ��͸��
		ColorDrawable dw = new ColorDrawable(0xaa000000);
		//����SelectPicPopupWindow��������ı���
		this.setBackgroundDrawable(dw);
//			//mMenuView���OnTouchListener�����жϻ�ȡ����λ�������ѡ������������ٵ�����
		this.mMenuView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){dismiss();}
				}				
				return true;
			}
		});
		
		/** 1.����ٴε��MENU���޷�Ӧ����   
	     *  2.sub_view��PopupWindow����View 
	     */  
		this.mMenuView.setFocusableInTouchMode(true);  
		this.mMenuView.setOnKeyListener(new OnKeyListener() {  
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
	            if ((keyCode == KeyEvent.KEYCODE_MENU)&&(isShowing())) {  
	            	dismiss();// ����д��ģ��menu��PopupWindow�˳�����  
	                return true;  
	            }  
	            return false;  
			}  
	    });  
		
		//��ʼ��ť�¼�
		this.LoadButtonEvent();
	}
	
	private void LoadButtonEvent()
	{
		this.mMenuView.findViewById(R.id.mm_polyAnalysis).setOnClickListener(ButtonClickEvent);
		this.mMenuView.findViewById(R.id.mm_Delete).setOnClickListener(ButtonClickEvent);
		this.mMenuView.findViewById(R.id.mm_Move).setOnClickListener(ButtonClickEvent);
		this.mMenuView.findViewById(R.id.mm_vertexEdit).setOnClickListener(ButtonClickEvent);
		this.mMenuView.findViewById(R.id.mm_Undo).setOnClickListener(ButtonClickEvent);
		this.mMenuView.findViewById(R.id.mm_Redo).setOnClickListener(ButtonClickEvent);
	}

	private OnClickListener ButtonClickEvent = new OnClickListener(){

		@Override
		public void onClick(View v) 
		{
			//PubVar.m_DoEvent.m_DataEdit.DoCommand(v.getTag().toString());
			dismiss();
		}};
		
		
	public void startAnimate()
	{
		TranslateAnimation animation =new TranslateAnimation(Animation.RELATIVE_TO_SELF,  
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,  
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,  
                0.0f); 
				animation.setDuration(500);  
				this.mMenuView.setAnimation(animation);
				animation.startNow();
	}
}
