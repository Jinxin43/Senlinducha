package dingtu.ZRoadMap.Data;

import android.view.View;

public class DataBindOfKeyValue 
{
	public String Key = "";
	public String Value = "";
	public View ViewControl = null;
	public String DataKey = "";

	public DataBindOfKeyValue (String key,String dataKey,String value,View v)
	{
		this.DataKey = dataKey;
		this.Key=key;this.Value=value;ViewControl=v;
	}
	public DataBindOfKeyValue (String key,String value,View v)
	{
		this.DataKey = key;
		this.Key=key;this.Value=value;ViewControl=v;
	}
	public DataBindOfKeyValue (String key,View v)
	{
		this.DataKey = key;
		this.Key=key;this.Value="";ViewControl=v;
	}
}
