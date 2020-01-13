package lkmap.ToolBar;

import java.util.UUID;

import com.dingtu.senlinducha.R;

import lkmap.Cargeometry.Coordinate;
import lkmap.MapControl.Tools;
import lkmap.MapControl.v1_Measure;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;

public class v1_Agent_Measure implements v1_IToolsBarCommand {

	private String Id = UUID.randomUUID().toString();
	@Override
	public void OnDispose() {
		PubVar.m_MapControl.ClearOnPaint(this.Id);
		this.m_Measure.Clear();
	}
	
	@Override
	public void OnChange() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void OnPrepare() {
		if (this.m_Measure==null)this.m_Measure = new v1_Measure();
		PubVar.m_MapControl.AddOnPaint(this.Id, this.m_Measure);
	}
	
	
	private v1_Measure m_Measure = null;
	
	private View m_View = null;
	public void SetView(View view)
	{
		this.m_View = view;
		if (this.m_Measure==null)this.m_Measure = new v1_Measure();
		this.m_View.findViewById(R.id.bt_line_measure).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_poly_measure).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_snap).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_clear).setOnClickListener(new ViewClick());
	}
	
    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.equals("测线"))
    		{
    			SetMode(1);
    		}
    		if (Tag.equals("测面"))
    		{
    			SetMode(2);
    		}
    		if (Tag.equals("捕捉"))
    		{
    			View btnSnap = m_View.findViewById(R.id.bt_snap);
    			lkmap.Tools.Tools.SetToolsBarItemSelect(btnSnap,!m_Measure.m_Snap);
    			m_Measure.m_Snap = !m_Measure.m_Snap;
    		}
    		if (Tag.equals("清空"))
    		{
    			if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
    			m_Measure.Clear();
    		}
    	}
    }

    //设置测量模式
    private void SetMode(int mode)
    {
    	View btnLine = this.m_View.findViewById(R.id.bt_line_measure);
    	View btnPoly = this.m_View.findViewById(R.id.bt_poly_measure);
    	lkmap.Tools.Tools.SetToolsBarItemSelect(btnLine, false);
    	lkmap.Tools.Tools.SetToolsBarItemSelect(btnPoly, false);
		if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
		this.m_Measure.SetMode(mode);
		PubVar.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_Measure, this.m_Measure);
		PubVar.m_MapControl.invalidate();
		if (mode==1)lkmap.Tools.Tools.SetToolsBarItemSelect(btnLine, true);
		if (mode==2)lkmap.Tools.Tools.SetToolsBarItemSelect(btnPoly, true);
    }

    public void SetZHMode()
    {
		if (!PubVar.m_DoEvent.AlwaysOpenProject())return;
		this.m_Measure.SetMode(0);
		PubVar.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_Measure, this.m_Measure);
		PubVar.m_MapControl.invalidate();
    }
    
    public Coordinate getFirstPoint()
    {
    	return this.m_Measure.getFisrtCoor();
    }
}
