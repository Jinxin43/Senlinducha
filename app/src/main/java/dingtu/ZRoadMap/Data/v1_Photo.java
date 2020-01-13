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

/*��������Ĳ�����
 * 1��ObjectName����ʾ����
 * 2��ObjectUUID����Ƭ��Ψһ��ʶ
 * 3��ObjectSubPath����Ƭ����·��
 * 4��ObjectNameList��������Ƭ�б�
 * 
 * ���ز������µ���Ƭ�б�
 */
public class v1_Photo
{
	public static ICallback _Callback = null;   //���պ�Ļص�
	
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
				Log.v("tag", "���������գ����ڻص�������");
				PhotoCallbackResult(Str,ExtraStr.toString());
			}};
			
		//������֧��
		Tools.ToLocale(_DataDialog.findViewById(R.id.photo_camera));
		Tools.ToLocale(_DataDialog.findViewById(R.id.photo_exit));
		Tools.ToLocale(_DataDialog.findViewById(R.id.photo_erase));
	}
	
	//�˳���Ļص������ڸ��°�ť����Ƭ����
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
	
	String _ObjectUUID = "";			   //��Ƭ����ʵ���UUID
	String _ObjectName = "";               //��Ƭ����ʵ��ı�ʶ����
	String _PhotoPath = "";				   //ͼƬ���ڵ�·��

	String _TempPhotoName =null;		   //��ʱͼƬ����

    List<String> _PhotoNameList = new ArrayList<String>();    //ͼƬ�����б�
    Gallery _mGallery = null;			   //ͼƬ�б���
    int _SelectedIndex = -1;    		   //��ѡ��ͼƬ������
    
    //������Ƭ�������Ĳ���
    //ObjectName:��ʶ���ƣ�ObjectUUID:ʵ���UUID,ObjectNameList:������Ƭ�б�
    public void SetPhotoPara(String ObjectName,String ObjectUUID,String ObjectNameList)
    {
    	this._PhotoPath=PubVar.m_SysAbsolutePath+"/Photo";
    	this._ObjectName  = ObjectName;   //����
    	this._ObjectUUID = ObjectUUID;    //UUID

	    //���Ŀ¼�����û���򴴽�
	    if (!lkmap.Tools.Tools.ExistFile(this._PhotoPath))
	    {
	    	(new File(this._PhotoPath)).mkdirs();
	    }
	    
	    //Ĭ��ͼƬ�б�
	    if (ObjectNameList!=null)
	    {
		    if (ObjectNameList!="")
		    {
		    	String[] aa = ObjectNameList.split(",");
		    	for(String PN :aa)if (!PN.trim().equals(""))this._PhotoNameList.add(this._PhotoPath+"/"+Tools.GetFileName(PN));
		    }
	    }
	    Log.v("tag", "��Ƭ�б�"+ObjectNameList);
    }
    
    public void OnStart() 
    {
		RelativeLayout linearLayout = (RelativeLayout)_DataDialog.findViewById(R.id.mainphoto);
		linearLayout.bringChildToFront(_DataDialog.findViewById(R.id.rl_photo_info));
		
 
        //���������
    	_DataDialog.findViewById(R.id.photo_camera).setOnClickListener(new View.OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
				OpenPhoto();
			}
        });
        
        //�˳�
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
        
        //ɾ��ѡ����Ƭ
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
				
				 //�·�����ͨ��Intent����ϵͳ��ͼƬ�鿴���Ĺؼ�����
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
	    
	    //���û����Ƭ�����Զ��������
	    if (_PhotoNameList.size()==0) OpenPhoto();
    }

    //�����
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
		String ShowText = Tools.ToLocale("���ƣ�")+this._ObjectName+"��"+Tools.ToLocale("������")+this._PhotoNameList.size()+" "+Tools.ToLocale("��");
		((TextView)_DataDialog.findViewById(R.id.photo_info)).setText(ShowText);
	    this._mGallery.setAdapter(new ImageAdapter(_DataDialog.getContext(),this._PhotoNameList));
    }
    
    //���������ķ�������int requestCode, int resultCode, Intent data
    protected void PhotoCallbackResult(String Str,String requestCode) 
    { 
        try
        {
        	Log.v("tag", "�ص�У���룺"+requestCode);
        	if (!requestCode.equals("1")) return;

        		Log.v("tag", "��ʼУ����Ƭ�е�������Ϣ");
        		if (!Tools.ExistFile(this._TempPhotoName))return;
        		
        		//��Ƭ��Exif��Ϣ
        		HashMap<String,String> exifInfo = new HashMap<String,String>();
        		
    		    if (PubVar.m_Photo_LockGPS)  //�Ƿ���Ҫ����Ƭ�д洢GPS��Ϣ
    		    {
    		    	try
    		    	{
    		    		//�ж�GPS�Ƿ�λ
        		    	if (!PubVar.m_GPSLocate.AlwaysFix()) 
        		    	{
//        		    		Tools.ShowYesNoMessage(_DataDialog.getContext(), "����Ƭ��û����ȡ��������Ϣ���Ƿ��������գ�", 
//        		    				new ICallback(){
//    									@Override
//    									public void OnClick(String Str, Object ExtraStr) 
//    									{
//    										if (Str.equals("YES"))	{File f=new File(_TempPhotoName);f.delete();OpenPhoto();}
//    									}});
//    						return;
        		    	}
        		    	else   //GPS�Ѷ�λ��д��������Ϣ
        		    	{
        		    		String[] Coor = PubVar.m_GPSLocate.getJWGPSCoordinate().split(",");
        		    		String[] GPSDateTime = PubVar.m_GPSLocate.getGPSDateForPhotoFormat();
        		    		exifInfo.put("GPSLongitudeRef", "E");
        		    		exifInfo.put("GPSLongitude", Tools.ConvertToSexagesimal(Coor[0]));
        		    		exifInfo.put("GPSLatitudeRef", "N");
        		    		exifInfo.put("GPSLatitude", Tools.ConvertToSexagesimal(Coor[1]));
        		    		exifInfo.put("GPSTimeStamp",GPSDateTime[1]);
        		    		exifInfo.put("GPSDateStamp",GPSDateTime[0]);
//        		    		Tools.ShowMessageBox("��ʼ����GPS��Ϣ1�����ȣ�"+exifInfo.get("GPSLongitude")+"��γ�ȣ�"+exifInfo.get("GPSLatitude")+"��\r\n"+
//        		    												"��ʼ����GPS��Ϣ2�����ڣ�"+exifInfo.get("GPSDateStamp")+"��ʱ�䣺"+exifInfo.get("GPSTimeStamp")+"��");
        	    		    Log.v("tag", "��ʼ����GPS��Ϣ1�����ȣ�"+exifInfo.get("GPSLongitude")+"��γ�ȣ�"+exifInfo.get("GPSLatitude")+"��");
        	    		    Log.v("tag", "��ʼ����GPS��Ϣ2�����ڣ�"+exifInfo.get("GPSDateStamp")+"��ʱ�䣺"+exifInfo.get("GPSTimeStamp")+"��");
        		    	}
        		    	
        		    	 //������Ƭ����ת���� 
            		    if (exifInfo.size()>0)
            		    {
            		    	ExifInterface exif2 = new ExifInterface(this._TempPhotoName);
            		    	
            		    	//��ȡ��Ƭ����ת�Ƕ�
            		    	int orientation = exif2.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            		    	
            		    	//��Ƭ����
            		    	String dt = exif2.getAttribute(ExifInterface.TAG_DATETIME);
            		    	
            		    	exifInfo.put("Orientation", orientation+"");
            		    	exifInfo.put("DateTime", dt);
            		    	exifInfo.put("DateTimeDigitized", dt);    //���ֻ�ʱ�䣬ע��д��
            		    	exifInfo.put("DateTimeOriginal", dt);    //ԭʼʱ�䣬ע��д��
            		    	
                		    //����Exif��д���� 
                		    if (exifInfo.size()>1)this.SaveExifInfo(exif2,exifInfo);
            		    }
    		    	}
    		    	catch(Exception ex)
    		    	{
    		    		
    		    	}
//    		    	
    		    }
    		    

    		    //�����ļ�����
    		    Log.v("tag", "�Զ�������Ƭ�ļ���������");
        		int idx = 1;
        		String PhotoFileName = this._ObjectUUID+"_00"+idx;
    		    while(lkmap.Tools.Tools.ExistFileEx(this._PhotoPath,PhotoFileName))
    		    {
    		    	idx++;
    		    	PhotoFileName = this._ObjectUUID+"_"+(idx<10?"00"+idx:(idx<100?"0"+idx:idx));
    		    }
    		    Log.v("tag", "��ʼ������Ƭ������");
    		    PhotoFileName=this._PhotoPath+"/"+PhotoFileName+".jpg";
    	        File f = new File(PhotoFileName);
    	        File f1 = new File(this._TempPhotoName.toString());
        		if (f1.exists())if (f1.renameTo(f)){this._PhotoNameList.add(0,PhotoFileName);StartLookImage();}
        		Log.v("tag", "��Ƭ����ɹ�������");
	  }
	  catch(Exception e)
	  {
		  Tools.ShowMessageBox(e.getMessage());
	  }
    }
    
    
    /**
     * ��дExif��Ϣ
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
//		    exif2.setAttribute(ExifInterface.TAG_DATETIME, dt);  		 //����ʱ��
//		    exif2.setAttribute("DateTimeDigitized", dt);    //���ֻ�ʱ�䣬ע��д��
//		    exif2.setAttribute("DateTimeOriginal", dt);    //ԭʼʱ�䣬ע��д��
//		    
//		    exif2.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, "2013:01:01");			//GPS����
//		    exif2.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, "5/1,12/1,23/1");    //GPSʱ�䣬ע��д��
//		   
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,"E"); 
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,"112/1,39/1,42/1");   //����
//		    
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,"N");
//		    exif2.setAttribute(ExifInterface.TAG_GPS_LATITUDE,"46/1,23/1,51/1");    //γ��
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
//     * ��תͼ��
//     */
//    private boolean RotateImage(int orientation)
//    {
//    	try
//    	{
//			//�ж���Ƭ�Ƿ���ת�� 
//			int degree = 0;   //��Ƭ����ת�Ƕ�
//		    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) degree = 90;
//		    if (orientation == ExifInterface.ORIENTATION_ROTATE_180)degree = 180;
//		    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) degree = 270;
//		    
//			if (degree==0) return false;
//			
//			Bitmap bitmap = BitmapFactory.decodeFile(this._TempPhotoName);//����Path��ȡ��ԴͼƬ  
//			
//			//����ķ�����Ҫ�����ǰ�ͼƬתһ���Ƕ�
//			Matrix m = new Matrix();
//			int width = bitmap.getWidth();  
//			int height = bitmap.getHeight();  
//			m.setRotate(degree); // ��תangle��  
//			
//     		Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height,  m, true);// ��������ͼƬ  
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
    
    
    //ͼƬ����
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
            		//��ȡ��Ƭ����ת�Ƕ�
            		ExifInterface exif2 = new ExifInterface(F);
    		    	orientation = exif2.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            	}
            	catch(Exception e){}
            	

        		BitmapFactory.Options options = new BitmapFactory.Options();
        		options.inSampleSize = 8;
                Bitmap bitmap = BitmapFactory.decodeFile(F,options);
                
                //�Ƿ���Ҫ��ת
    			int degree = 0;   //��Ƭ����ת�Ƕ�
    		    if (orientation == ExifInterface.ORIENTATION_ROTATE_90) degree = 90;
    		    if (orientation == ExifInterface.ORIENTATION_ROTATE_180)degree = 180;
    		    if (orientation == ExifInterface.ORIENTATION_ROTATE_270) degree = 270;
    		    
    			if (degree!=0) 
    			{
        			//����ķ�����Ҫ�����ǰ�ͼƬתһ���Ƕ�
        			Matrix m = new Matrix();
        			int width = bitmap.getWidth();  
        			int height = bitmap.getHeight();  
        			m.setRotate(degree); // ��תangle��  
        			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,  m, true);// ��������ͼƬ  
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

            //�����趨layout�Ŀ��
            v.setLayoutParams(new Gallery.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
            return v;
        }
    }
           
}


       
