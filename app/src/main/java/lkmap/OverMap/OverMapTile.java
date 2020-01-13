package lkmap.OverMap;

import java.io.Serializable;

import android.graphics.Bitmap;

/**
 * ���ӵ�ͼ����Ƭ��
 * @author lmgk
 *
 */
public class OverMapTile  implements Serializable
{
	public int Row = 0;
	public int Col = 0;
	public int Level = 0;
	
	private String TileName = "";  //��ʽ��col@row@level
	
	public String TableName = "";   //��Ƭ���ĸ��ڱ���
	
	public String Url = "";   //��Ƭ�����ص�ַ
	public String CachePath = "";   //����·��

	
	/**
	 * ������Ƭ�����ƣ���ʽ��col@row@level
	 * @param RowColLevel
	 */
	public void SetTileName(String RowColLevel,String WhichTableName)
	{
    	String[] tiInfo = RowColLevel.split("@");
    	this.Col = Integer.valueOf(tiInfo[0]);
    	this.Row = Integer.valueOf(tiInfo[1]);
    	this.Level = Integer.valueOf(tiInfo[2]);
    	
    	this.TileName = RowColLevel; 
    	this.TableName = WhichTableName;
	}
	
	/**
	 * ��ȡ��Ƭ������
	 * @return
	 */
	public String GetTileName(){return this.TileName;}	

	public Bitmap TileBitmap = null;

}
