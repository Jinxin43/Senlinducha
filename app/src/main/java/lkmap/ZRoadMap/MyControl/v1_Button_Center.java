package lkmap.ZRoadMap.MyControl;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class v1_Button_Center extends LinearLayout 
{
	public v1_Button_Center(Context context) {
		super(context,null);
	}
	public v1_Button_Center(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.v1_bk_button_center, this,true);
	}

	public Button GetButton()
	{
		return (Button)this.findViewById(R.id.btButton);
	}
	public void SetEnable(boolean enabled)
	{
		this.GetButton().setEnabled(enabled);
		TextView tv = (TextView)this.findViewById(R.id.tvText);
		if (!enabled)tv.setTextColor(Color.rgb(181, 181, 181));
		else tv.setTextColor(Color.rgb(0,0,0));
	}

	public void SetLargeText()
	{
		TextView tv = ((TextView)this.findViewById(R.id.tvText));
		((TextView)this.findViewById(R.id.tvText)).setTextAppearance(this.getContext(), android.R.style.TextAppearance_Large);
		tv.setTextColor(Color.BLACK);
	}

	public void SetImage(int imageResourId)
	{
		((ImageView)this.findViewById(R.id.ivImage)).setImageResource(imageResourId);
	}
	
	public void SetText(String Text)
	{
		((TextView)this.findViewById(R.id.tvText)).setText(Text);
		this.GetButton().setTag(Text);
	}
	
	public void SetFlatStyle(boolean flat)
	{
		this.GetButton().setBackgroundResource(R.drawable.buttonstyle_transparent_all);
	}
}
