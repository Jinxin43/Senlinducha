package dingtu.ZRoadMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import lkmap.Tools.Tools;

public class HashMapEx 
{
	private List<HashValueObject> m_HashMap = new ArrayList<HashValueObject>();
	
	//�õ���Ҫ��Map�ж�̬��ʾ����Ϣ
	public List<String> GetInMapShowMessageList()
	{
		List<String> InfoList = new ArrayList<String>();
		for(HashValueObject hvObj:this.m_HashMap)
		{
			if (hvObj.ShowOnMap)InfoList.add(hvObj.LabelText);
		}
		return InfoList;
	}
	
	//ȡ��ָ��Key��HashValueObjectʵ��
	public HashValueObject GetValueObject(String Key)
	{
		return this.GetValueObject(Key, false);
	}
	public HashValueObject GetValueObject(String Key,boolean CreateNew)
	{
		
		for(HashValueObject hvObj1:this.m_HashMap)
		{
			if (hvObj1.Key.equals(Key)) return hvObj1;
		}
		
		if (CreateNew)
		{
			HashValueObject hvObj = new HashValueObject();
			hvObj.Key = Key;
			this.m_HashMap.add(hvObj);
			return hvObj;
		}
		else return null;
	}
	
	//����HashValueObjectʵ��
	public void Add(String Key,HashValueObject hvObject)
	{
		for(HashValueObject hvObj1:this.m_HashMap)
		{
			if (hvObj1.Key.equals(Key)) {this.m_HashMap.remove(hvObj1);break;}
		}
		hvObject.Key = Key;
		this.m_HashMap.add(hvObject);
	}
	
	public void Delete(String Key)
	{
		for(HashValueObject hvObj1:this.m_HashMap)
		{
			if (hvObj1.Key.equals(Key)){ this.m_HashMap.remove(hvObj1);return;}
		}
	}

}
