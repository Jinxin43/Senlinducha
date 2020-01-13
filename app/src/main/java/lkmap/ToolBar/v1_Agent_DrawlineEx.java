package lkmap.ToolBar;

import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsLine;

public class v1_Agent_DrawlineEx 
{
//	public static DrawlineEx GetDrawLineEx()
//	{
//		if (m_DrawlineEx==null)m_DrawlineEx = new DrawlineEx();
//		PubVar.m_MapControl.AddOnPaint(m_Id, m_DrawlineEx);
//		return m_DrawlineEx;
//	}
	
	public static v1_CGpsLine GetDrawLine_Poly()
	{
		return PubVar.m_DoEvent.m_GPSPoly.getGPSLine();
	}
	public static v1_CGpsLine GetDrawLine_Line()
	{
		return PubVar.m_DoEvent.m_GPSLine;
	}
}
