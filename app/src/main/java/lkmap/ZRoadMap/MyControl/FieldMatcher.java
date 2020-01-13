package lkmap.ZRoadMap.MyControl;

import java.util.List;

import com.dingtu.senlinducha.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import dingtu.ZRoadMap.PubVar;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_LayerField;


public class FieldMatcher extends LinearLayout 
{
	
	private v1_LayerField mField;
	
	public FieldMatcher(Context context) 
	{
		super(context);
		LayoutInflater.from(context).inflate(R.layout.fieldmatcher, this,true);
	}
	
	public FieldMatcher(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.fieldmatcher, this,true);
    }
	
	public void setMatcherValue(v1_LayerField tofield,List<String> fieldsName,List<String> fromFields)
	{
		mField = tofield;
		((EditText)this.findViewById(R.id.btnSrcField)).setText(mField.GetFieldShortName()+"("+mField.GetFieldName()+" "+mField.GetFieldTypeName()+")");
		//DO it on trigger class
		//fromFields.add(0,"");
		ArrayAdapter<String> fromAdapter = new ArrayAdapter<String>(this.getContext(),
				android.R.layout.simple_spinner_item,
				fromFields);
		fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner)this.findViewById(R.id.spFromField)).setAdapter(fromAdapter);
		int i = 0;
		for(String f:fieldsName)
		{
			if(f.equals(mField.GetFieldShortName()))
			{
				((Spinner)this.findViewById(R.id.spFromField)).setSelection(i+1);
				break;
			}
			else
			{
				if(f.equals(mField.GetFieldName()))
				{
					((Spinner)this.findViewById(R.id.spFromField)).setSelection(i+1);
					break;
				}
			}
			i++;
		}
	}
	
	public v1_LayerField getSrcField()
	{
		return mField;
	}
	
	public int getSelectionIndex()
	{
		return ((Spinner)this.findViewById(R.id.spFromField)).getSelectedItemPosition();
	}
	

}
