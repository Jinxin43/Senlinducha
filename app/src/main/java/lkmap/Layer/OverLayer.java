package lkmap.Layer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lkmap.Cargeometry.Marker;

public class OverLayer 
{
	//ͼ������
	private String _Name = UUID.randomUUID().toString();
	public void SetName(String name)
	{
		this._Name = name;
	}
	public String GetName()
	{
		return this._Name;
	}
	
	//����б�
	private List<Marker> _MarkerList = new ArrayList<Marker>();
	public List<Marker> GetMarkerList()
	{
		return _MarkerList;
	}
	
	//���ӱ��
	public void AddMarker(Marker _Marker)
	{
		this._MarkerList.add(_Marker);
	}
	
	//ɾ�����
	public void RemoveMarker(Marker _Marker)
	{
		this.RemoveMarkerById(_Marker.GetID());
	}
	public void RemoveMarkerById(String _MarkerId)
	{
		int MarkerCount = this._MarkerList.size();
		for(int i=MarkerCount-1;i>=0;i--)
		{
			if (_MarkerList.get(i).GetID().equals(_MarkerId))_MarkerList.remove(i);
		}
	}
	
	public Marker GetMarker(String _MarkerID)
	{
		for(Marker m:_MarkerList)
		{
			if (m.GetID().equals(_MarkerID)) return m;
		}
		return null;
	}
	//ɾ�����еı��
	public void RemoveAllMarker()
	{
		this._MarkerList.clear();
	}
	
	//ˢ����ʾ
	public void Refresh()
	{
		for(Marker marker : this._MarkerList)
		{
			marker.Draw();
		}
	}
}
