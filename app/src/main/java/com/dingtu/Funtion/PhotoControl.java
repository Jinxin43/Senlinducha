package com.dingtu.Funtion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dingtu.senlinducha.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import dingtu.ZRoadMap.HashValueObject;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.PhotoCamera;
import lkmap.Cargeometry.Coordinate;
import lkmap.Map.StaticObject;
import lkmap.Tools.Tools;

public class PhotoControl 
{
//	private Activity mOwnActivity = null;
	private View  mOwnView = null;
	private String mPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/Photo";
	private String mSmallPhotoPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/SmallPhoto";
	private String tempPhotoName;
	private List<String> mPhotoNameList = new ArrayList<String>();
	private int mObjID;
	public static ICallback _Callback = null;//��PhotoCamera�ص�ʹ��
	private String mX="";
	private String mY="";
	private String mXian = "��(��ҵ��)";
	private String mXiang ="��(�ֳ�)";
	private String mCun="��(Ӫ����)";
	private String mLinBan = "�ְ�";
	private String mXiaoban = "С��";
	private boolean needWarterMark;
	private boolean needXiaoBan;
	private boolean needTuBan = false;
	private String mWaterMarkValue;
	private ICallback mCallback;
	
	public PhotoControl(int objID,List<String> photoNames,boolean addWaterMark,boolean isAddXiaoban,View ownView)
	{
		initPhotoControl(objID,photoNames,addWaterMark,isAddXiaoban,ownView);
	}
	
	public PhotoControl(int objID,List<String> photoNames,boolean addWaterMark,View ownView)
	{
		initPhotoControl(objID,photoNames,addWaterMark,true,ownView);
	}
	
	public void setNeedTuban(boolean isNeedTuban)
	{
		needTuBan = isNeedTuban;
	}
	
	public void setWaterWaterMark(boolean isNeedTuban)
	{
		needWarterMark = isNeedTuban;
	}
	
	public void setCallback(ICallback callback)
	{
		mCallback = callback;
	}
	
	
	private void initPhotoControl(int objID, List<String> photoNames, boolean addWaterMark, boolean isAddXiaoban,View ownView) 
	{
		mObjID = objID;
		needXiaoBan = isAddXiaoban;
//		mOwnActivity = (Activity)PubVar.m_DoEvent.m_Context;
		mPhotoNameList = photoNames;
		needWarterMark = addWaterMark;
		mOwnView = ownView;
		
		_Callback = new ICallback()
		{
			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
				Log.v("tag", "���������գ����ڻص�������");
				PhotoCallbackResult(Str,ExtraStr.toString());
			}
		};
		
		((Button)mOwnView.findViewById(R.id.bt_addPhoto)).setOnClickListener(new ViewClick());
		((Button)mOwnView.findViewById(R.id.bt_deletePhoto)).setOnClickListener(new ViewClick());
		ShowPhotos();
		
	}

	public void SetXiaoBanInfo(String x,String y,String value)
	{
		if(x != null)
		{
			mX = x;
		}
		if(y != null)
		{
			mY = y;
		}
		
		mXiaoban = value;
		
	}
	
	public void SetXiaoBanInfo(String xian,String xiang,String cun,String linban,String xiaoban,String x,String y)
	{
		if(x != null)
		{
			mX = x;
		}
		if(y != null)
		{
			mY = y;
		}
		
		if(xian != null && xian.length()>0)
		{
			mXian = xian;
		}
		
		if(xiang != null && xiang.length()>0)
		{
			mXiang = xiang;
		}
		
		if(cun != null && cun.length()>0)
		{
			mCun = cun;
		}
		
		if(linban != null && linban.length()>0)
		{
			mLinBan = linban;
		}
		
		if(xiaoban != null && xiaoban.length()>0)
		{
			mXiaoban = xiaoban;
		}
	}
	
	public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if(Tag.equals("����"))
    		{
	    		OpenCamera();
    		}
    		if(Tag.equals("ɾ����Ƭ"))
    		{
    			deletePhotos();
    		}
    	}
    }
	
	private void OpenCamera() 
	{
		if (!lkmap.Tools.Tools.ExistFile(mPhotoPath))
		{
			(new File(mPhotoPath)).mkdirs();
		}
		
		if (!lkmap.Tools.Tools.ExistFile(mSmallPhotoPath))
		{
			(new File(mSmallPhotoPath)).mkdirs();
		}
    	tempPhotoName = mPhotoPath+"/TempPhoto.jpg";
		Intent inet = new Intent(PubVar.m_DoEvent.m_Context,PhotoCamera.class); 
		inet.putExtra("PhotoPath",mPhotoPath);
		inet.putExtra("TempPhoto","TempPhoto.jpg");
		PubVar.m_DoEvent.m_Context.startActivity(inet);
	}
	
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
	
	public void deletePhotos()
    {
    	GridView gridView = (GridView)mOwnView.findViewById(R.id.gvList);
    	ListAdapter adapter = gridView.getAdapter();
    	
    	List<String> delPhotos = new ArrayList<String>();
    	
    	for(int i= 0;i<adapter.getCount();i++)
    	{
    		HashMap<String, Object> map = (HashMap<String, Object>)adapter.getItem(i);
    		View view = gridView.getChildAt(i);
    		CheckBox checkBox = (CheckBox)view.findViewById(R.id.cb_select);
    		if(checkBox.isChecked())
    		{
    			String fileName = map.get("image")+"";
    			File file=new File(fileName);
    			if(file.exists())
    			{
    				if(file.delete())
    				{
    					
    				}
    				else
    				{
    					//Tools.ShowMessageBox(map.get("text")+"ɾ��ʧ�ܣ�");
    				}
    			}
//    			File file2=new File(fileName.replace("Small", ""));
//    			if(file.exists())
//    			{
//    				if(file.delete())
//    				{
//    					
//    				}
//    				else
//    				{
//    					//Tools.ShowMessageBox(map.get("text")+"ɾ��ʧ�ܣ�");
//    				}
//    			}
    			
    			for(String f:mPhotoNameList)
				{
					if(f.equals(map.get("text")+""))
					{
						delPhotos.add(f);
					}
				}
    		}
    	}
    	
    	for(String f:delPhotos)
    	{
    		mPhotoNameList.remove(f);
    	}
    	
    	//this.mBaseObject.SetSYS_PHOTO(Tools.StrListToStr(mPhotoNameList));
    	ShowPhotos();
    	if(mCallback != null)
        {
        	mCallback.OnClick("����", null);
        }
    	
    }
	
	public List<String> getPhotoList()
	{
		return mPhotoNameList;
	}
	
	
	ArrayList<HashMap<String, Object>> data_list;
	//ImageListAdapter sim_adapter;
    public void ShowPhotos()
    {
		String ShowText = "��Ƭ��������"+this.mPhotoNameList.size()+"����";
		((TextView)mOwnView.findViewById(R.id.photo_info)).setText(ShowText);
	    GridView gridView = (GridView)mOwnView.findViewById(R.id.gvList);
	    String [] from ={"image","text","check"};
        int [] to = {R.id.iv_image,R.id.tv_info,R.id.cb_select};
        data_list = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<mPhotoNameList.size();i++)
        {
        	HashMap<String, Object> map = new HashMap<String, Object>();
            //map.put("image", mSmallPhotoPath+"/"+mPhotoNameList.get(i));
        	map.put("image", mPhotoNameList.get(i).replace("/Photo", "/SmallPhoto"));
            map.put("text", mPhotoNameList.get(i));
            map.put("check", false);
            data_list.add(map);
        }
        //sim_adapter = new ImageListAdapter(mOwnActivity, data_list, R.layout.photolistitem, from, to);
        SimpleAdapter sim_adapter = new SimpleAdapter(mOwnView.getContext(), data_list, R.layout.photolistitem, from, to);
        //����������
        gridView.setAdapter(sim_adapter);
        gridView.setOnItemClickListener(new ItemClickListener());
        gridView.invalidate();
        
    }
    
    class ItemClickListener implements OnItemClickListener  
    {  
		  public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened   
		                                    View arg1,//The view within the AdapterView that was clicked  
		                                    int arg2,//The position of the view in the adapter  
		                                    long arg3//The row id of the item that was clicked  
		                                    ) 
		  {  
		      //�ڱ�����arg2=arg3  
		      HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);  
		      //��ʾ��ѡItem��ItemText  

		    String path = item.get("image")+"";
		    String truePath = path.replace("Small", "");
            File file= new File(truePath);
            Intent it =new Intent(Intent.ACTION_VIEW);
            Uri mUri = Uri.parse("file://"+file.getPath());
            it.setDataAndType(mUri, "image/*");
            mOwnView.getContext().startActivity(it);
		  }  
        
    } 
    
     private static Handler handler=new Handler();
	 protected void PhotoCallbackResult(String Str,String requestCode) 
	    { 
		 
		 Log.v("tag", "�ص�У���룺"+requestCode);
     	if (!requestCode.equals("1"))
  		{
	        	return;
  		}
	    
     	if (!Tools.ExistFile(tempPhotoName))
     	{
			return;
		}
     	
     	final ProgressDialog pDialog = new ProgressDialog(mOwnView.getContext());
    	
        // ���ý�������񣬷��ΪԲ�Σ���ת��
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        pDialog.setIcon(R.drawable.v1_messageinfo);
        
        // ����ProgressDialog ����
        pDialog.setTitle(Tools.ToLocale("��ʾ"));
       
        // ����ProgressDialog ��ʾ��Ϣ
        pDialog.setMessage("���ڴ�����Ƭ������Ϣ......");

        // ����ProgressDialog �Ľ������Ƿ���ȷ
        pDialog.setIndeterminate(false);
       
        // ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
        pDialog.setCancelable(false);
        pDialog.show();
        
     	try
	        {
	        		new Thread(new Runnable() {                    
	                    @Override
	                    public void run() 
	                    {
	                    	
	                    	Log.v("tag", "��ʼУ����Ƭ�е�������Ϣ");
	        		    	//��Ƭ��Exif��Ϣ
	        				HashMap<String,String> exifInfo = new HashMap<String,String>();
	        			    if (PubVar.m_Photo_LockGPS)  //�Ƿ���Ҫ����Ƭ�д洢GPS��Ϣ
	        			    {
	        			    	//�ж�GPS�Ƿ�λ
	        			    	if (!PubVar.m_GPSLocate.AlwaysFix()) 
	        			    	{
	        			    			//����Ƭ��û����ȡ��������Ϣ���Ƿ��������գ�
	        			    	}
	        			    	else 
	        			    	{
	        			    		String[] Coor = PubVar.m_GPSLocate.getJWGPSCoordinate().split(",");
	        			    		String[] GPSDateTime = PubVar.m_GPSLocate.getGPSDateForPhotoFormat();
	        			    		exifInfo.put("GPSLongitudeRef", "E");
	        			    		exifInfo.put("GPSLongitude", Tools.ConvertToSexagesimal(Coor[0]));
	        			    		exifInfo.put("GPSLatitudeRef", "N");
	        			    		exifInfo.put("GPSLatitude", Tools.ConvertToSexagesimal(Coor[1]));
	        			    		exifInfo.put("GPSTimeStamp",GPSDateTime[1]);
	        			    		exifInfo.put("GPSDateStamp",GPSDateTime[0]);
	        		    		    Log.v("tag", "��ʼ����GPS��Ϣ1�����ȣ�"+exifInfo.get("GPSLongitude")+"��γ�ȣ�"+exifInfo.get("GPSLatitude")+"��");
	        		    		    Log.v("tag", "��ʼ����GPS��Ϣ2�����ڣ�"+exifInfo.get("GPSDateStamp")+"��ʱ�䣺"+exifInfo.get("GPSTimeStamp")+"��");
	        			    	}
	        			    }
	        		
	        			    String dt = Tools.GetSystemDate();
	        			    try
	        			    {
	        			    	 ExifInterface exif2 = new ExifInterface(tempPhotoName);
	 	        		    	
	 	        		    	//��ȡ��Ƭ����ת�Ƕ�
	 	        		    	int orientation = exif2.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
	 	        		    	
	 	        		    	//��Ƭ����
	 	        		    	dt = exif2.getAttribute(ExifInterface.TAG_DATETIME);
	 	        		    	
	 	        			    //������Ƭ����ת���� 
	 	        			    if (exifInfo.size()>0)
	 	        			    {
	 	        			    	exifInfo.put("Orientation", orientation+"");
	 	        			    	exifInfo.put("DateTime", dt);
	 	        			    	exifInfo.put("DateTimeDigitized", dt);    //���ֻ�ʱ�䣬ע��д��
	 	        			    	exifInfo.put("DateTimeOriginal", dt);    //ԭʼʱ�䣬ע��д��
	 	        			    	
	 	        				    //����Exif��д���� 
	 	        				    if (exifInfo.size()>1)
	 	        				    	SaveExifInfo(exif2,exifInfo);
	 	        			    }
	        			    }
	        			    catch(Exception ex)
	        			    {
	        			    	ex.printStackTrace();
	        			    	pDialog.cancel();
	        			    }
	        			    
	        			    try
	        			    {
	        			    	//�����ļ�����
		        			    Log.v("tag", "�Զ�������Ƭ�ļ���������");
		        				int idx = 0;
		        				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); 
		        		    	simpleDateFormat.format(new java.util.Date());
		        				String PhotoFileName = "E"+mX.replace(".", "")+"N"+mY.replace(".", "")+simpleDateFormat.format(new java.util.Date())+""+idx;
		        			    while(lkmap.Tools.Tools.ExistFileEx(mPhotoPath,PhotoFileName))
		        			    {
		        			    	idx++;
		        			    	PhotoFileName = "E"+mX.replace(".", "")+"N"+mY.replace(".", "")+simpleDateFormat.format(new java.util.Date())+"_"+(idx<10?"00"+idx:(idx<100?"0"+idx:idx));
		        			    }
		        			    Log.v("tag", "��ʼ������Ƭ������");
		        			    PhotoFileName=PhotoFileName+".jpg";
		        		        
		        		        File f1 = new File(tempPhotoName.toString());
		        		       
		        		        if(f1.exists())
		        		        {
		        		        	
		        		        	BitmapFactory.Options options = new BitmapFactory.Options();
        			    	        //options.inTempStorage = new byte[1000*1024];
        			    	        //options.inPreferredConfig = Bitmap.Config.RGB_565;
        			    	        options.inPurgeable = true;
        			    	        options.inMutable = true;
        			    	        options.inInputShareable = true;
        			    	        //options.inSampleSize = 2;
        			    	        FileInputStream iSteam = new FileInputStream(tempPhotoName);
        			    	        Bitmap bitmap = BitmapFactory.decodeStream(iSteam, null, options);
        			    	        iSteam.close();
        			    	        iSteam = null;
        			    	        Log.v("tag", "��ȡ��Ƭ������");
        		
        			    	        int w = bitmap.getWidth();
        			    	        int h = bitmap.getHeight(); 
        			    	        String strCamerTime = "����ʱ�䣺"+dt;
        			    	        Canvas canvasTemp = new Canvas(bitmap);
        			    	       
        			    	        Paint p = new Paint();
        			    	        String familyName = "����";
        			    	        Typeface font = Typeface.create(familyName,Typeface.BOLD);
        			    	        p.setColor(Color.RED);
        			    	        p.setTypeface(font);
        			    	        p.setTextSize(30);
        			    	        canvasTemp.drawText(strCamerTime,8,h-150,p);
        			    	        
		        		        	if(needWarterMark)
		        			        {	
		        			        	
	        			    	        if(needTuBan)
	        			    	        {
	        			    	        	canvasTemp.drawText("ͼ�ߺţ�"+mXiaoban,8,h-200,p);
	        			    	        }
	        			    	        else
	        			    	        {
	        			    	        	String strXiaoban;
		        			    	        if(needXiaoBan)
		        			    	        {
		        			    	           strXiaoban = "����С�ࣺ"+mXian+"_"+mXiang+"_"+mCun+"_"+mLinBan+"_"+mXiaoban; 
		        			    	        }
		        			    	        else
		        			    	        {
//		        			    	        	strXiaoban = "����壺"+mXian+"_"+mXiang+"_"+mCun;
		        			    	        	strXiaoban = mWaterMarkValue;
		        			    	        }
		        			    	        
		        			    	        if(strXiaoban != null)
		        			    	        {
		        			    	        	canvasTemp.drawText(strXiaoban,8,h-200,p);
		        			    	        }
	        			    	        }
	        			    	        
	        			    	        
	        			    	        
	        			    	        canvasTemp.save();
	        			    	        
	        			    	        canvasTemp.restore(); 
	        			    	       
		        			        }
//		        		        	else
//		        		        	{
//		        		        		FileInputStream iSteam = new FileInputStream(tempPhotoName);
//	        			    	        Bitmap bitmap = BitmapFactory.decodeStream(iSteam);
//	        			    	        File smallF = new File(mSmallPhotoPath+"/"+PhotoFileName);
//	        			    	        FileOutputStream f = new FileOutputStream(smallF);
//        			    	        	Bitmap b = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/10, bitmap.getHeight()/10, false);
//        			    	        	b.compress(Bitmap.CompressFormat.JPEG, 100, f);
//        			    	        	
//		        		        		f1.renameTo(new File(mPhotoPath+"/"+PhotoFileName));
//		        		        		f1 = null;
//		        		        	}
		        		        	
		        		        	 String mbwz = "Ŀ��λ�ã�";
	        			    	        if(mX != null && mY != null && mX.length()>0 && mY.length()>0)
	        			    	        {
	        			    	        	
	        			    	        	Coordinate coord = StaticObject.soProjectSystem.XYToWGS84(Double.valueOf(mX),Double.valueOf(mY),0);
	        			    	        	String jd = Tools.GetDDMMSS(coord.getX());
	        			    	        	String wd = Tools.GetDDMMSS(coord.getY());
	        			    	        	mbwz = "Ŀ��λ�ã�N"+wd+",E"+jd;
	        			    	        	
	        			    	        	Log.v("tag", "���Ŀ��λ�á�����");
	        			    	        }
	        			    	        canvasTemp.drawText(mbwz,8,h-100,p);
	        			    	        
	        			    	        String pswz = "����λ�ã�δ��λ";
	        			    	        if(PubVar.m_GPSLocate.AlwaysFix())
	        			    	        {
	        			    	        	String[] Coor = PubVar.m_GPSLocate.getJWGPSCoordinate().split(",");
	        			    	        	String jd = Tools.GetDDMMSS(Tools.ConvertToDouble(Coor[0]));
	        			    	        	String wd = Tools.GetDDMMSS(Tools.ConvertToDouble(Coor[1]));
	        			    	        	pswz = "����λ�ã�N"+wd+",E"+jd;
	        			    	        	
	        			    	        	Log.v("tag", "�������λ�á�����");
	        			    	        }
	        			    	        canvasTemp.drawText(pswz,8,h-50,p);
	        			    	        
	        			    	        canvasTemp.save();
	        			    	        canvasTemp.restore();
	        			    	       
	        			    	        FileOutputStream fos = null;
	        			    	        FileOutputStream fos2 = null;
	        			    	        try 
	        			    	        {
	        			    	        	File f = new File(mPhotoPath+"/"+PhotoFileName);
	        			    	        	fos = new FileOutputStream(f);
	        			    	        	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
	        			    	        	File smallF = new File(mSmallPhotoPath+"/"+PhotoFileName);
	        			    	        	fos2 = new FileOutputStream(smallF);
	        			    	        	Bitmap b = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/10, bitmap.getHeight()/10, false);
	        			    	        	b.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
	        			    	        	
	        			    	        } catch (FileNotFoundException e) 
	        			    	        {
	        			    	        	e.printStackTrace();
	        			    	        } 
	        			    	        finally 
	        			    	        {
	        			    	            if (fos != null) {
	        			    	                try 
	        			    	                {
	        			    	                    fos.flush();
	        			    	                    fos.close();
	        			    	                    fos = null;
	        			    	                    
	        			    	                    canvasTemp = null;
	        			    	                    bitmap.recycle();
	        				    	        	    bitmap = null;
	        			    	                } 
	        			    	                catch (Exception e) 
	        			    	                {
	        			    	                	e.printStackTrace();
	        			    	                }
	        			    	            }
	        			    	            
	        			    	            if (fos2 != null) {
	        			    	                try 
	        			    	                {
	        			    	                	fos2.flush();
	        			    	                	fos2.close();
	        			    	                	fos2 = null;
	        			    	                   
	        			    	                } 
	        			    	                catch (Exception e) 
	        			    	                {
	        			    	                	e.printStackTrace();
	        			    	                }
	        			    	            }
	        			    	        }
		        		     			f1.delete();
		        			        	Log.v("tag", "��Ƭ����ɹ�������");
		        			        		
		        		        	mPhotoNameList.add(0,mPhotoPath+"/"+PhotoFileName);
		        		        	System.gc();
		        		        }
		        		        
		        		        
	        			    }
	        			    catch(Exception ex)
	        			    {
	        			    	pDialog.cancel();
	        			    }
	                      
	                        handler.post(new Runnable() {                    
	                            @Override
	                            public void run() 
	                            {
	                            	ShowPhotos();
	                            	pDialog.cancel();
	                            	if(mCallback != null)
	                                {
	                                	mCallback.OnClick("����", null);
	                                }
	                            }
	                        }); 
	                    }
	                }).start();
	        		
	        		
	    	       
		  }
		  catch(Exception e)
		  {
			  Tools.ShowMessageBox(e.getMessage());
			  pDialog.cancel();
		  }
	    }
	 
	    
}
