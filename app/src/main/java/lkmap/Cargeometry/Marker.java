package lkmap.Cargeometry;

import java.util.UUID;

import lkmap.Enum.lkMarkerType;

public class Marker 
{
	//Marker的ID值 
	private String _ID = UUID.randomUUID().toString();
	public String GetID(){return _ID;}
	public void SetID(String id){this._ID = id;}
	
	//Marker的类型
	private lkMarkerType _MarkerType = lkMarkerType.enPointMarker;
	public lkMarkerType GetMarkerType()
	{
		return _MarkerType;
	}
	public void SetMarkerType(lkMarkerType markerType)
	{
		this._MarkerType = markerType;
	}
	
	//Tag的额外数据项
	private String _Tag = "";
	public void SetTag(String tag){this._Tag = tag;}
	public String GetTag(){return this._Tag;}
	
	public void Draw(){}
}
