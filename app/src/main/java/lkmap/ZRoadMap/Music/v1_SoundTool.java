package lkmap.ZRoadMap.Music;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class v1_SoundTool 
{
	private SoundPool m_SoundPool = null;
	private HashMap<Integer,Integer> m_PlayList = new HashMap<Integer,Integer>();
	
	public v1_SoundTool(Context context)
	{
		this.m_SoundPool = new SoundPool(1,AudioManager.STREAM_SYSTEM,5);
		int id = this.m_SoundPool.load(context,R.raw.mic_gpsconnect, 1);   //GPS��
		this.m_PlayList.put(1, id);
		
		id = this.m_SoundPool.load(context,R.raw.mic_satellitelose, 1);   //���Ƕ�ʧ
		this.m_PlayList.put(2, id);
		
		id = this.m_SoundPool.load(context,R.raw.mic_gpsfix, 1);    //��λ��
		this.m_PlayList.put(3, id);
		
		id = this.m_SoundPool.load(context,R.raw.mic_gpsfixlose, 1);   //ʧȥ��λ��
		this.m_PlayList.put(4, id);
		
		id = this.m_SoundPool.load(context,R.raw.mic_gpspoint, 1);    //GPS�������
		this.m_PlayList.put(5, id);
	}
	public void PlaySound(int soundId)
	{
		this.m_SoundPool.play(this.m_PlayList.get(soundId),1, 1, 0, 0, 1);
	}
}
