package lkmap.ToolBar;

import java.util.UUID;

import com.dingtu.senlinducha.R;

import lkmap.Edit.Vertex;
import lkmap.Enum.lkVertexEditType;
import lkmap.MapControl.Tools;
import lkmap.MapControl.v1_Measure;
import android.view.View;
import android.view.View.OnClickListener;
import dingtu.ZRoadMap.PubVar;

public class v1_Agent_Vertex implements v1_IToolsBarCommand {

	private String Id = UUID.randomUUID().toString();
	@Override
	public void OnDispose() {
		//PubVar.m_MapControl.ClearOnPaint(this.Id);
	}
	
	@Override
	public void OnChange() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void OnPrepare() {
		if (this.m_Vertex==null) this.m_Vertex = new Vertex(PubVar.m_MapControl);
	}
	
	private Vertex m_Vertex = null;				//节点编辑工具
	
	private View m_View = null;
	public void SetView(View view)
	{
		this.m_View = view;
		this.m_View.findViewById(R.id.bt_movevertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_addvertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_delvertex).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_snap).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_undo).setOnClickListener(new ViewClick());
		this.m_View.findViewById(R.id.bt_redo).setOnClickListener(new ViewClick());
	}
	
    public class ViewClick implements OnClickListener
    {
    	@Override
    	public void onClick(View arg0)
    	{
    		String Tag = arg0.getTag().toString();
    		if (Tag.equals("移动节点"))
    		{
    			SetVertexMode(1);
    		}
    		if (Tag.equals("增加节点"))
    		{
    			SetVertexMode(2);
    		}
    		if (Tag.equals("删除节点"))
    		{
    			SetVertexMode(3);
    		}
    		if (Tag.equals("回退"))
    		{
    			PubVar.m_DoEvent.DoCommand(Tag);
    		}
    		if (Tag.equals("重做"))
    		{
    			PubVar.m_DoEvent.DoCommand(Tag);
    		}
    		if (Tag.equals("捕捉"))
    		{
//    			View btnSnap = m_View.findViewById(R.id.bt_snap);
//    			lkmap.Tools.Tools.SetToolsBarItemSelect(btnSnap,!m_Measure.m_Snap);
//    			m_Measure.m_Snap = !m_Measure.m_Snap;
    		}
    	}
    }

    //设置节点模式
    private void SetVertexMode(int mode)
    {
    	PubVar.m_MapControl.setActiveTools(Tools.MoveObject, this.m_Vertex, this.m_Vertex);
    	switch(mode)
    	{
	    	case 1:
	    		 this.m_Vertex.SetVertexEditType(lkVertexEditType.enMove);
	    		 break;
	    	case 2:
	    		 this.m_Vertex.SetVertexEditType(lkVertexEditType.enAdd);
	    		 break;
	    	case 3:
	    		 this.m_Vertex.SetVertexEditType(lkVertexEditType.enDelete);
	    		 break;
    	}
    }


}
