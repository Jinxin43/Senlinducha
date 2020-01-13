package lkmap.ZRoadMap.MyControl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.Spinner;
import dingtu.ZRoadMap.Data.ICallback;

public class v1_SpinnerDialog extends Spinner {

	public v1_SpinnerDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public v1_SpinnerDialog(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public v1_SpinnerDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}
	
	public v1_SpinnerDialog(Context context,int mode) {
		super(context,mode);
		// TODO Auto-generated constructor stub

	}
	
	private ICallback _Callback = null;
	public void SetCallback(ICallback cb){this._Callback = cb;}
	@Override
	public boolean performClick() 
	{
		if (_Callback!=null) _Callback.OnClick("SpinnerCallback", null);
		return false;
	}

}
