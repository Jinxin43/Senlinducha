package dingtu.ZRoadMap;

import java.util.UUID;

public class HashValueObject 
{
	public String Key = UUID.randomUUID().toString();   //名称
	public String LabelText = "";
	public String Value = "";  			//值
	public boolean ShowOnMap = false;    //是否在主界面内部是显示
	
	public String ID = UUID.randomUUID().toString();   //唯 一ID值

}
