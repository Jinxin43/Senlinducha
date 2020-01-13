package dingtu.ZRoadMap.Data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import lkmap.Tools.Tools;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import android.media.ExifInterface;

/*调用相机的参数：
 * 1、ObjectName：显示名称
 * 2、ObjectUUID：照片的唯一标识
 * 3、ObjectSubPath：照片的子路径
 * 4、ObjectNameList：已有照片列表
 * 
 * 返回参数：新的照片列表
 */
public class v1_Photo
{
	public static ICallback _Callback = null;   //拍照后的回调
	
	private DataBindAlertDialog _DataDialog = null;
	public v1_Photo()
	{
    	_DataDialog = new DataBindAlertDialog(PubVar.m_DoEvent.m_Context);
    	_DataDialog.setContentView(R.layout.v1_photo);
    	_DataDialog.ReSetSize(0f,0f);
    	_Callback = new ICallback(){
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				Log.v("tag", "相机完成拍照，正在回调。。。");
				PhotoCallbackResult(Str,ExtraStr.toString());
			}};
			
		//多语言支持
		Tools.ToLocale(_DataDialog.findViewById(R.id.photo_camera));
		Tools.ToLocale(_DataDialog.findViewById(R.id.photo_exit));
		Tools.ToLocale(_DataDialog.findViewById(R.id.photo_erase));
	}
	
	//退出后的回调，用于更新按钮的相片数量
	private ICallback _quitCallback = null;
	public void SetQuitCallback(ICallback quitCallback)
	{
		this._quitCallback = quitCallback;
	}
	
    public void ShowDialog()
    {
    	_DataDialog.show();
    	this.OnStart();
    }
	
	String _ObjectUUID = "";			   //照片所属实体的UUID
	String _ObjectName = "";               //照片所属实体的标识名称
	String _PhotoPath = "";				   //图片所在的路径

	String _TempPhotoName =null;		   //临时图片名称

    List<String> _PhotoNameList = new ArrayList<String>();    //图片名称列表
    Gallery _mGallery = null;			   //图片列表画廊
    int _SelectedIndex = -1;    		   //被选择图片的索引
    
    //设置相片管理器的参数
    //ObjectName:标识名称，ObjectUUID:实体的UUID,ObjectNameList:已有照片列表
    public void SetPhotoPara(String ObjectName,String ObjectUUID,String ObjectNameList)
    {
    	this._PhotoPath=PubVar.m_SysAbsolutePath+"/Photo";
    	this._ObjectName  = ObjectName;   //名称
    	this._ObjectUUID = ObjectUUID;    //UUID

	    //检查目录，如果没有则创建
	    if (!lkmap.Tools.Tools.ExistFile(this._PhotoPath))
	    {
	    	(new File(this._PhotoPath)).mkdirs();
	    }
	    
	    //默认图片列表
	    if (ObjectNameList!=null)
	    {
		    if (ObjectNameList!="")
		    {
		    	String[] aa = ObjectNameList.split(",");
		    	for(String PN :aa)if (!PN.trim().equals(""))this._PhotoNameList.add(this._PhotoPath+"/"+Tools.GetFileName(PN));
		    }
	    }
	    Log.v("tag", "相片列表："+ObjectNameList);
    }
    
    public void OnStart() 
    {
		RelativeLayout linearLayout = (RelativeLayout)_DataDialog.findViewById(R.id.mainphoto);
		linearLayout.bringChildToFront(_DataDialog.findViewById(R.id.rl_photo_info));
		
 
        //启动照相机
    	_DataDialog.findViewById(R.id.photo_camera).setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				OpenPhoto();
			}
        });
        
        //退出
    	_DataDialog.findViewById(R.id.photo_exit).setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v)
			{
				List<String> returnPhotoNameList = new ArrayList<String>();
				for(String FilePathAndName :v1_Photo.this._PhotoNameList)
				{
					if (!FilePathAndName.trim().equals(""))	returnPhotoNameList.add(_PhotoPath+"/"+Tools.GetFileName(FilePathAndName));
				}
				if (_quitCallback!=null) _quitCallback.OnClick(Tools.StrListToStr(returnPhotoNameList), "0");
				_DataDialog.dismiss();
			}
        });
        
        //删除选中相片
    	_DataDialog.findViewById(R.id.photo_erase).setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				if (_SelectedIndex!=-1)
				{
					if (_SelectedIndex<0 || _SelectedIndex >=_PhotoNameList.size()) return;
					lkmap.Tools.Tools.DeletePhoto(_DataDialog.getContext(),_PhotoNameList.get(_SelectedIndex),new ICallback(){

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							// TODO Auto-generated method stub
							if (Str.equals("OK"))
								{
									_PhotoNameList.remove(_SelectedIndex);
									StartLookImage();
								}
						}});
					//
					return;
				}
			}
        });
        

	    this._mGallery =(Gallery) _DataDialog.findViewById(R.id.photo_view);   
	    this.StartLookImage();

	    this._mGallery.setOnItemClickListener(new Gallery.OnItemClickListener()
	    {   
	       @Override  
	       public void onItemClick(AdapterView<?> parent, View v, int position,long id) 
	       {   
				if (position==-1)return;
				File file = new File(_PhotoNameList.get(position));
				
				 //下方是是通过Intent调用系统的图片查看器的关键代码
				 Intent intent = new Intent();
				 intent.setAction(android.content.Intent.ACTION_VIEW);
				 intent.setDataAndType(Uri.fromFile(file), "image/*");
				 PubVar.m_DoEvent.m_Context.startActivity(intent);
	       }
	       });
	    
	    this._mGallery.setOnItemSelectedListener(new Gallery.OnItemSelectedListener()
	    {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) 
			{
				_SelectedIndex = arg2;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
	    });
	    
	    //如果没有相片，而自动启动相机
	    if (_PhotoNameList.size()==0) OpenPhoto();
    }

    //打开相机
    private void OpenPhoto() 
	{
    	_TempPhotoName = _PhotoPath+"/TempPhoto.jpg";
		Intent inet = new Intent(PubVar.m_DoEvent.m_Context,PhotoCamera.class); 
		inet.putExtra("PhotoPath",_PhotoPath);
		inet.putExtra("TempPhoto","TempPhoto.jpg");
		PubVar.m_DoEvent.m_Context.startActivity(inet);
	}
    
    public void StartLookImage()
    {
		//this._PhotoNameList = this.getPhotoList();
		String ShowText = Tools.ToLocale("名称：")+this._ObjectName+"，"+Tools.ToLocale("数量：")+this._PhotoNameList.size()+" "+Tools.ToLocale("张");
		((TextView)_DataDialog.findViewById(R.id.photo_info)).setText(ShowText);
	    this._mGallery.setAdapter(new ImageAdapter(_DataDialog.getContext(),this._PhotoNameList));
    }
    
    //启动相机后的返回数据int requestCode, int resultCode, Intent data
    protected void PhotoCallbackResult(String Str,String requestCode) 
    { 
        try
        {
        	Log.v("tag", "回调校验码："+requestCode);
        	if (!requestCode.equals("1")) return;

        		Log.v("tag", "开始校验相片中的坐标信息");
        		if (!Tools.ExistFile(this._TempPhotoName))return;
        		
        		//相片的Exif信息
        		HashMap<String,String> exifInfo = new HashMap<String,String>();
        		
    		    if (PubVar.m_Photo_LockGPS)  //是否需要在相片中存储GPS信息
    		    {
    		    	try
    		    	{
    		    		//判断GPS是否定位
        		    	if (!PubVar.m_GPSLocate.AlwaysFix()) 
        		    	{
//        		    		Tools.ShowYesNoMessage(_DataDialog.getContext(), "在相片中没有提取到坐标信息，是否重新拍照？", 
//        		    				new ICallback(){
//    									@Override
//    									public void OnClick(String Str, Object ExtraStr) 
//    									{
//    										if (Str.equals("YES"))	{File f=new File(_TempPhotoName);f.delete();OpenPhoto();}
//    									}});
//    						return;
        		    	}
        		    	else   //GPS已定位，写入坐标信息
        		    	{
        		    		String[] Coor = PubVar.m_GPSLocate.getJWGPSCoordinate().split(",");
        		    		String[] GPSDateTime = PubVar.m_GPSLocate.getGPSDateForPhotoFormat();
        		    		exifInfo.put("GPSLongitudeRef", "E");
        		    		exifInfo.put("GPSLongitude", Tools.ConvertToSexagesimal(Coor[0]));
        		    		exifInfo.put("GPSLatitudeRef", "N");
        		    		exifInfo.put("GPSLatitude", Tools.ConvertToSexagesimal(Coor[1]));
        		    		exifInfo.put("GPSTimeStamp",GPSDateTime[1]);
        		    		exifInfo.put("GPSDateStamp",GPSDateTime[0]);
//        		    		Tools.ShowMessageBox("开始构造GPS信息1【经度："+exifInfo.get("GPSLongitude")+"，纬度："+exifInfo.get("GPSLatitude")+"】\r\n"+
//        		    												"开始构造GPS信息2【日期："+exifInfo.get("GPSDateStamp")+"，时间："+exifInfo.get("GPSTimeStamp")+"】");
        	    		    Log.v("tag", "开始构造GPS信息1【经度："+exifInfo.get("GPSLongitude")+"，纬度："+exifInfo.get("GPSLatitude")+"】");
        	    		    Log.v("tag", "开始构造GPS信息2【日期："+exifInfo.get("GPSDateStamp")+"，时间："+exifInfo.get("GPSTimeStamp")+"】");
        		    	}
        		    	
        		    	 //处理相片的旋转问题 
            		    if (exifInfo.size()>0)
            		    {
            		    	ExifInterface exif2 = new ExifInterface(this._TempPhotoName);
            		    	
            		    	//读取相片的旋转角度
            		    	int orientation = exif2.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            		    	
            		    	//相片日期
            		    	String dt = exif2.getAttribute(ExifInterface.TAG_DATETIME);
            		    	
            		    	exifInfo.put("Orientation", orientation+"");
            		    	exifInfo.put("DateTime", dt);
            		    	exifInfo.put("DateTimeDigitized", dt);    //数字化时间，注意写法
            		    	exifInfo.put("DateTimeOriginal", dt);    //原始时间，注意写法
            		    	
                		    //处理Exif重写问题 
                		    if (exifInfo.size()>1)this.SaveExifInfo(exif2,exifInfo);
            		    }
    		    	}
    		    	catch(Exception ex)
    		    	{
    		    		
    		    	}
//    		    	
    		    }
    		    

    		    //处理文件名称
    		    Log.v("tag", "自动生成相片文件名。。。");
        		int idx = 1;
        		String PhotoFileName = this._ObjectUUID+"_00"+idx;
    		    while(lkmap.Tools.Tools.ExistFileEx(this._PhotoPath,PhotoFileName))
    		    {
    		    	idx++;
    		    	PhotoFileName = this._ObjectUUID+"_"+(idx<10?"00"+idx:(idx<100?"0"+idx:idx));
    		    }
    		    Log.v("tag", "开始保存相片。。。");
    		    PhotoFileName=this._PhotoPath+"/"+PhotoFileName+".jpg";
    	        File f = new File(PhotoFileName);
    	        File f1 = new File(this._TempPhotoName.toString());
        		if (f1.exists())if (f1.renameTo(f)){this._PhotoNameList.add(0,PhotoFileName);StartLookImage();}
        		Log.v("tag", "相片保存成功。。。");
	  }
	  catch(Exception e)
	  {
		  Tools.ShowMessageBox(e.getMessage());
	  }
    }
    
    
    /**
     * 重写Exif信息
     * @param exifInfo,Key,Value
     * @return
     */
    private boolean SaveExifInfo(ExifInterface exif2,HashMap<String,String> exifInfo)
    {
		try 
		{
			for (Iterator iter = exifInfo.keySet().iterator(); iter.hasNext();) 
			{      
				Object key = iter.next();     
				Object val = exifInfo.get(key);      
				System.out.println("key:"+key);      
				System.out.println("value:"+val); 
				exif2.setAttribute(key.toString(),val.toString()); 
			}
			exif2.saveAttributes();

//			ExifInterface exif2 = new ExifInterface(this._TempPhotoName);
//		    String dt = exif2.getAttribute(ExifInterface.TAG_DATETIME);
//	
//		    exif2.setAttribute(ExifInterface.TAG_DATETIME, dt);  		 //拍摄时间
//		    exif2.setAttribute("DateTimeDigitized", dt);    //数字化时间，注意写法
//		    exif2.setAttribute("DateTimeOriginal", dt);    //原始时间，注意写法
//		    
//		    exif2.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, "2013:01:01");			//GPS日期
//		    exif2.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, "5/1,12/1,23/1");    //GPS时间，注意写法
//		   
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,"E"); 
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,"112/1,39/1,42/1");   //经度
//		    
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,"N");
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LATITUDE,"46/1,23/1,51/1");    //纬度
//	    
//			exif2.saveAttributes();
			return true;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
    }
    
//    /**
//     * 旋转图像
//     */
//    private boolean RotateImage(int orientation)
//    {
//    	try
//    	{
//			//判断相片是否旋转过 
//			int degree = 0;   //相片的旋转角度
//		    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) degree = 90;
//		    if (orientation == ExifInterface.ORIENTATION_ROTATE_180)degree = 180;
//		    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) degree = 270;
//		    
//			if (degree==0) return false;
//			
//			Bitmap bitmap = BitmapFactory.decodeFile(this._TempPhotoName);//根据Path读取资源图片  
//			
//			//下面的方法主要作用是把图片转一个角度
//			Matrix m = new Matrix();
//			int width = bitmap.getWidth();  
//			int height = bitmap.getHeight();  
//			m.setRotate(degree); // 旋转angle度  
//			
//     		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height,  m, true);// 从新生成图片  
//			FileOutputStream out = new FileOutputStream(this._TempPhotoName);
//			bitmap2.compress(Bitmap.CompressFormat.JPEG, 80, out);
//			out.close();
//			
//     		if(!bitmap.isRecycled()){bitmap.recycle();bitmap=null;}
//			if(!bitmap2.isRecycled()){bitmap2.recycle();bitmap2=null;}
//			return true;
//		} catch (IOException e)  {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//		}
//        return false;
//    }
    
    
    //图片绑定器
    public class ImageAdapter extends BaseAdapter 
    {
        private Context m_Context;   
        int mGalleryItemBackground;   
        private List<String> photoFileList = null;

        public List<Bitmap> bitmapList= new ArrayList<Bitmap>();
        
        public ImageAdapter(Context context,List<String> li)
        {    
            this.m_Context = context;   
            this.photoFileList = li ;   
            TypedArray a = _DataDialog.getContext().obtainStyledAttributes(R.styleable.Gallery);   
            mGalleryItemBackground = a.getResourceId(R.styleable.Gallery_android_galleryItemBackground,0);   
            a.recycle();   
            
            for(String F :this.photoFileList )
            {
            	int orientation = 0;
            	try
            	{
            		//读取相片的旋转角度
            		ExifInterface exif2 = new ExifInterface(F);
    		    	orientation = exif2.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            	}
            	catch(Exception e){}
            	

        		BitmapFactory.Options options = new BitmapFactory.Options();
        		options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(F,options);
                
                //是否需要旋转
    			int degree = 0;   //相片的旋转角度
    		    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) degree = 90;
    		    if (orientation == ExifInterface.ORIENTATION_ROTATE_180)degree = 180;
    		    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) degree = 270;
    		    
    			if (degree!=0) 
    			{
        			//下面的方法主要作用是把图片转一个角度
        			Matrix m = new Matrix();
        			int width = bitmap.getWidth();  
        			int height = bitmap.getHeight();  
        			m.setRotate(degree); // 旋转angle度  
        			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,  m, true);// 从新生成图片  
    			}
    			
    			bitmapList.add(bitmap);
            }
        }   
           
        @Override  
        public int getCount() {   
           
            return this.photoFileList.size();
        }   
      
        @Override  
        public Object getItem(int position) {   
             
            return position;   
        }   
           
        @Override  
        public long getItemId(int position) {   
               
            return position;   
        }

        @Override  
        public View getView(int position, View converView, ViewGroup parent) 
        {   
  
            ImageView v = new ImageView(this.m_Context);  
            v.setBackgroundColor(0xFF000000);
            v.setImageBitmap(bitmapList.get(position));

            //重新设定layout的宽高
            v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            return v;
        }
    }
           
}


       
