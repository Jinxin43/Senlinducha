package lkmap.ZRoadMap.Project;

import java.util.HashMap;

import com.dingtu.DTGIS.DataService.ProjectDB;
import com.dingtu.senlinducha.R;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.Tools.Tools;

public class ImageEnhance {

	private v1_FormTemplate _Dialog = null; 
	private SeekBar brightBar;
	private SeekBar contrastBar;
	private int brigthValue = 127;
	private int contrastValue = 127;
	private Boolean isEffect = false; 
	private ImageView iView;
	private ProjectDB db;
	
	public ImageEnhance()
	{
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_Dialog.SetOtherView(R.layout.imagequality);
    	_Dialog.ReSetSize(0.75f, 0.79f);
    	
    	//初始显示及按钮
    	_Dialog.SetCaption("底图调节");
    	_Dialog.SetButtonInfo("1,"+R.drawable.icon_title_comfirm+",确定  ,确定", pCallback);
    	brightBar = (SeekBar)_Dialog.findViewById(R.id.sb_light);
    	contrastBar = (SeekBar)_Dialog.findViewById(R.id.sb_Contrast);
    	db = new ProjectDB();
    	
    	_Dialog.findViewById(R.id.bt_ClearEnhance).setOnClickListener(new OnClickListener() {
			
    		@Override
			public void onClick(View v) {
				brigthValue = 127;
				contrastValue = 127;
				brightBar.setProgress(brigthValue);
				contrastBar.setProgress(contrastValue);
				
				isEffect = false;
				updateImageEffect();
			}
		});
    	
    	_Dialog.findViewById(R.id.bt_CleanChange).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				getEffectFromDB();
				brightBar.setProgress(brigthValue);
				contrastBar.setProgress(contrastValue);
				
			}
		});
    	
    	getEffectFromDB();
    	
    	brightBar.setProgress(brigthValue);
	    contrastBar.setProgress(contrastValue);
    	
    	brightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				isEffect = true;
				brigthValue = progress;  
				updateImageEffect();
			}
		});
    	
    	contrastBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
				isEffect = true;
				contrastValue = progress;
				updateImageEffect();
			}
		});
    	
    	_Dialog.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				if(db != null)
				{
					db.Close();
				}
			}
		});
    	
	}
	
	 private ICallback pCallback = new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr)
			{
		    	if (Str.equals("确定"))
		    	{
		    		HashMap<String , Object> hashMap = new HashMap<String, Object>();
		    		hashMap.put("isEffect", isEffect);
		    		hashMap.put("bright", brigthValue);
		    		hashMap.put("contrast", contrastValue);
		    		if(db.updateImageEffect(hashMap))
		    		{
		    			PubVar.imageEffect =hashMap; 
		    			_Dialog.dismiss();
		    		}
		    		
		    	}
			}};
	
	public void ShowDialog()
    {
    	//此处这样做的目的是为了计算控件的尺寸
    	_Dialog.setOnShowListener(new OnShowListener(){
			@Override
			public void onShow(DialogInterface dialog) 
			{
		        iView = (ImageView)_Dialog.findViewById(R.id.iv_ImageEnhance);
		        updateImageEffect();
			}}
    	);
    	_Dialog.show();
    }
	
	
	private void updateImageEffect()
	{
		int brightness = brigthValue - 127; 
		float contrast = (float) ((contrastValue+128) / 256.0); 
	    if(PubVar.OriginalMap == null)
	    {
	    	PubVar.OriginalMap = Bitmap.createBitmap(PubVar.m_Map.bp);
	    }
		
	   
		Bitmap bmp = Bitmap.createBitmap(PubVar.OriginalMap.getWidth(), PubVar.OriginalMap.getHeight(),  
                     Config.ARGB_8888); 
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bmp);
       
        if(isEffect)
	    {
        	 ColorMatrix bMatrix = new ColorMatrix();  
             bMatrix.set(new float[] {contrast, 0, 0, 0, brightness,
              		0, contrast,0, 0, brightness,
              		0, 0, contrast, 0, brightness, 
              		0, 0, 0, 1, 0 });
              
             paint.setColorFilter(new ColorMatrixColorFilter(bMatrix)); 
	    }
	    canvas.drawBitmap(PubVar.OriginalMap, 0, 0, paint);  
	    iView.setImageBitmap(bmp);
	}
	
	private void getEffectFromDB()
	{
    	HashMap<String, Object> result = db.getImagetEffect();
    	PubVar.imageEffect = result;
    	isEffect = (Boolean)result.get("isEffect");
    	if(isEffect)
    	{
    		try{
    			brigthValue = Integer.parseInt(result.get("bright")+"");
    			contrastValue = Integer.parseInt(result.get("contrast")+"");
    		}
    		catch(Exception ex)
    		{
    			
    		}
    	
    	}
    	else
    	{
    		brigthValue = 127;
    		contrastValue = 127;
    	}
	}
}
