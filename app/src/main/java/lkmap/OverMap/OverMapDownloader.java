package lkmap.OverMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import dingtu.ZRoadMap.PubVar;

public class OverMapDownloader
{
	public OverMapDownloader()
	{
		//��ʼ������ͼƬ���߳�
		for(int i=1;i<=m_DownloadThreadCount;i++)
		{
			OverMapDownload omd = new OverMapDownload();
			omd.setCallbackHandler(this.myHander);
			this.m_DownloadThreadList.add(omd);
		}
	}
	
	//��ͼ�����ؽ�����
	private int m_DownloadThreadCount = 5;
	//�������ݵ��߳��б�
	private List<OverMapDownload> m_DownloadThreadList = new ArrayList<OverMapDownload>();

	//��OverMap������
	private OverMap _OverMap = null;
	public void setBindGoogleMap(OverMap gm)
	{
		this._OverMap = gm;
	}
	

	
	//�����ļ��б�
	private List<OverMapTile> m_DownLoadFileList = new ArrayList<OverMapTile>();

	//���������ļ��б�
	public void setUpLoadFileList(List<OverMapTile> _DownloadFileList)
	{
		this.m_DownLoadFileList = _DownloadFileList;
	}
	

	//�����߳�
	private Timer _SaveTimer = null;
	private OverMapSaveCache _GoogleMapSaveCache = null;	
	TimerTask _saveTask = new TimerTask()
	{  
       public void run() 
       {  
	   		if (_GoogleMapSaveCache==null)
	   		{
	   			_GoogleMapSaveCache=new OverMapSaveCache();
	   			_GoogleMapSaveCache.SetCacheFilePath(_OverMap.GetOverMapPath());
	   		}
	   		_GoogleMapSaveCache.run();
       }  
	};

	
	//��ʼ�ϴ�
	public void StartUpLoad()
	{
		if (this._SaveTimer==null)
		{
			this._SaveTimer=new Timer();
			this._SaveTimer.schedule(_saveTask, 0, 1000*2);
		}

		//�����ϴ����߳����������ļ����䣬ƽ������
		int FileIndex = 0;
		boolean exit=true;
		do 
		{
			for(int i=0;i<this.m_DownloadThreadList.size();i++)
			{
				if (FileIndex<=this.m_DownLoadFileList.size()-1)
				{
					this.m_DownloadThreadList.get(i).AddDownLoadFile(this.m_DownLoadFileList.get(FileIndex));
					FileIndex++;
				} 
				else
				{
					exit=false;break;
				}
			}
		}while(exit);
		
		for(int i=0;i<this.m_DownloadThreadList.size();i++)
		{
			Thread t = new Thread(this.m_DownloadThreadList.get(i));
			t.start();
		}
	}
	
	//���滺��ͼƬ
	//private GoogleMapSaveCache _GoogleMapSaveCache = new GoogleMapSaveCache();
	//������ɺ�Ļص�
	Handler myHander = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what==1)  //����UI
			{
				Bundle data = msg.getData();
				OverMapTile tile = (OverMapTile)data.getSerializable("Tile");
				byte[] ImageByte = data.getByteArray("ImageByte");
				_OverMap.ShowImage(tile, ImageByte);
				PubVar.m_Map.FastRefresh();
				//_GoogleMapSaveCache.Save(_GoogleMap.getSQLiteDB(),_GoogleMap.getLocalGoogleMapPath(), dbFileName, _GoogleMap.getGoogleMapType()+"", ImageByte);
			}
			super.handleMessage(msg);
		}
	};

}
