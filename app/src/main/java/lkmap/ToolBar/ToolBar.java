package lkmap.ToolBar;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dingtu.senlinducha.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Layer.GeoLayer;
import lkmap.Tools.Tools;

public class ToolBar {
	private View m_View = null; // R.id.mainbottomtoolbar

	public ToolBar(Context mainContext) {
	}

	private int[] m_ToolbarItemId = { R.id.bt_select, R.id.bt_feature, R.id.bt_measure, R.id.bt_pan, R.id.bt_zoomin,
			R.id.bt_zoomout, R.id.bt_fullscreen, R.id.bt_gpslocate, R.id.bt_Copy, R.id.bt_query, R.id.bt_navigate,
			R.id.bt_tools, R.id.bt_save, R.id.bt_swipe };
//	private int[] m_ToolbarItemId_Poly = { R.id.bt_poly_drawline, R.id.bt_poly_coor, R.id.bt_poly_gps,
//			R.id.bt_poly_create, R.id.bt_poly_changeflip, R.id.bt_dc_jckp, R.id.bt_dc_wfwg };
	
	private int[] m_ToolbarItemId_Poly = { R.id.bt_poly_drawline, R.id.bt_poly_coor, R.id.bt_poly_gps,
			R.id.bt_poly_create, R.id.bt_poly_changeflip, R.id.bt_dc_wfwg };
	private int[] m_ToolbarItemId_Line = { R.id.bt_line_drawline, R.id.bt_line_coor, R.id.bt_line_gps,
			R.id.bt_line_create, R.id.bt_line_changeflip };
	private int[] m_ToolbarItemId_Point = { R.id.bt_point_draw, R.id.bt_point_coor, R.id.bt_point_gps };
	private int[] m_ToolbarItemId_Common = { R.id.bt_delete_op, R.id.bt_undo_op, R.id.bt_redo_op };

	private int[] m_ToolbarItemId_Vetcor = { R.id.bt_poly_drawline };

	/**
	 * 初始化工具条
	 * 
	 * @param view，R.id.mainbottomtoolbar
	 */
	public void LoadBottomToolBar(View view) {
		this.m_View = view;

		// 初始化工具条件数据
		for (int btId : m_ToolbarItemId)
			this.m_View.findViewById(btId).setOnClickListener(new ViewClick());
		for (int btId : m_ToolbarItemId_Point)
			this.m_View.findViewById(btId).setOnClickListener(new ViewClick());
		for (int btId : m_ToolbarItemId_Line)
			this.m_View.findViewById(btId).setOnClickListener(new ViewClick());
		for (int btId : m_ToolbarItemId_Poly)
			this.m_View.findViewById(btId).setOnClickListener(new ViewClick());
		for (int btId : m_ToolbarItemId_Common) {
			this.m_View.findViewById(btId).setOnClickListener(new ViewClick());
		}
		this.LoadBottomToolBarByType("全部", false);

	}

	/**
	 * 根据工具条的类型加载
	 * 
	 * @param barType
	 */
	public void LoadBottomToolBarByType(String barType, boolean visible) {
		int visibleId = View.GONE;
		if (visible)
			visibleId = View.VISIBLE;

		for (int btId : m_ToolbarItemId_Common) {
			this.m_View.findViewById(btId).setVisibility(visibleId);
		}

		if (barType.equals("点") || barType.equals("全部")) {
			for (int btId : m_ToolbarItemId_Point) {
				this.m_View.findViewById(btId).setVisibility(visibleId);
			}
		}
		if (barType.equals("线") || barType.equals("全部")) {
			for (int btId : m_ToolbarItemId_Line) {
				this.m_View.findViewById(btId).setVisibility(visibleId);
			}
		}
		if (barType.equals("面") || barType.equals("全部")) {
			for (int btId : m_ToolbarItemId_Poly) {
				this.m_View.findViewById(btId).setVisibility(visibleId);
			}
		}
		if (barType.equals("Vector") || barType.equals("全部")) {
			for (int btId : m_ToolbarItemId_Vetcor) {
				this.m_View.findViewById(btId).setVisibility(visibleId);
			}
		}
	}

	public class ViewClick implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String Tag = arg0.getTag().toString();
			String[] ChangeSelectTag = { "选择", "面_手绘", "面_gps", "线_手绘", "线_gps", "点_手绘", "测量", "手势缩放", "单击缩小", "单击放大",
					"卷帘" };
			for (String SelectTag : ChangeSelectTag) {
				if (Tag.equals(SelectTag)) {
					PubVar.m_DoEvent.m_EditToolbar.ClearButtonSelect();
					// Tools.SetToolsBarItemSelect(PubVar.m_DoEvent.m_SwipeBar,false);
					SetToolsBarItemSelect(arg0.getId(), null);
				}
			}
			PubVar.m_DoEvent.DoCommand(Tag);
		}
	}

	private int[] m_SelectToolsBarItemIdList = { R.id.bt_select, R.id.bt_pan, R.id.bt_poly_drawline, R.id.bt_poly_gps,
			R.id.bt_line_drawline, R.id.bt_line_gps, R.id.bt_point_draw, R.id.bt_measure, R.id.bt_swipe };

	/**
	 * 设置被选中的按钮Id
	 * 
	 * @param SelectBarItemId
	 * @param ClearBarItemId
	 */
	public void SetToolsBarItemSelect(int SelectBarItemId, int[] ClearBarItemId) {
		if (ClearBarItemId != null) {
			for (int barItemId : ClearBarItemId) {
				Tools.SetToolsBarItemSelect(this.m_View.findViewById(barItemId), false);
			}
		} else {
			for (int barItemId : m_SelectToolsBarItemIdList) {
				Tools.SetToolsBarItemSelect(this.m_View.findViewById(barItemId), false);
			}
		}

		if (SelectBarItemId != -1) {
			for (int barItemId : m_SelectToolsBarItemIdList) {
				if (SelectBarItemId == barItemId) {
					Tools.SetToolsBarItemSelect(this.m_View.findViewById(SelectBarItemId), true);
					break;
				}

			}

		}
	}

	/**
	 * 清除工具条的选择状态
	 */
	public void ClearButtonSelect() {
		this.SetToolsBarItemSelect(-1, null);
	}

	/**
	 * 更新工具条上选择按钮的上面的被选择实体数量值
	 */
	public void UpdateShowSelectCount() {
		int Count = Tools.GetSelectObjectsCount();
//		ShwoWFWFButton();
		Button btSelect = (Button) m_View.findViewById(R.id.bt_select);
		TextView tv = (TextView) m_View.findViewById(R.id.tv_selectcount);
		tv.setText(Count + "");
		RelativeLayout.LayoutParams LP = (RelativeLayout.LayoutParams) tv.getLayoutParams();
		LP.setMargins(btSelect.getWidth() + Tools.DPToPix(4), Tools.DPToPix(4), 0, 0);
		if (Count > 0) {
			tv.setVisibility(View.VISIBLE);
		} else {
			tv.setVisibility(View.GONE);
		}

	}

	@SuppressLint("NewApi")
	public void ShwoWFWFButton() {
		List<GeoLayer> pGeoLayerList = PubVar.m_MapControl.getMap().getGeoLayers(lkGeoLayersType.enVectorEditingData)
				.getList();

		boolean allWF = true;
		boolean noSelect = true;

		for (GeoLayer pGeoLayer : pGeoLayerList) {
			if (pGeoLayer.getSelSelection().getCount() > 0) {
				if (PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId()).GetLayerProjecType()
						.equals(ForestryLayerType.DuChaYanZheng)) {

					noSelect = false;
					try {
						
						String dataField = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer()
								.GetLayerByID(pGeoLayer.getId()).GetDataFieldNameByFieldName("违法违规");
						if (dataField.length() > 0) {

							SQLiteDataReader reader = pGeoLayer.getDataset().getDataSource().Query("Select " + dataField
									+ " from " + pGeoLayer.getDataset().getDataTableName() + " where SYS_ID in ("
									+ Tools.JoinIntT(",", pGeoLayer.getSelSelection().getGeometryIndexList()) + ")");
							while (reader.Read()) {
								if (reader.GetInt32(dataField) != 1) {
									allWF = false;
									break;
								}

							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					
//					if(PubVar.isZhuijiaing )
//					{
//						if(PubVar.zhuijiaCheckCard != null && PubVar.zhuijiaCheckCard.mLayerId.equals(pGeoLayer.getId()))
//						{
//							String[] ids = PubVar.zhuijiaCheckCard.mIds.split(",");
//							List<String> allId = Tools.StrArrayToList(ids);
//							
//							
//							for(Integer id:pGeoLayer.getSelSelection().getGeometryIndexList())
//							{
//								allId.remove(id+"");
//							}
//							
//							if(pGeoLayer.getSelSelection().getGeometryIndexList().size()== 0)
//							{
//								Tools.ShowMessageBox("必须选择图斑！");
//								return;
//							}
//							else
//							{
//								
////								if(pGeoLayer.getSelSelection().getCount()>ids.length)
////								{
//								 List<Integer> sortedSelectedIndex = pGeoLayer.getSelSelection().getGeometryIndexList();
//								 Collections.sort(sortedSelectedIndex,new Comparator<Integer>(){
//
//										@Override
//										public int compare(Integer o1, Integer o2) {
//											return o1.compareTo(o2);
//										}
//										
//									});
//								 
//								 String idString = pGeoLayer.getSelSelection().getGeometryIndexList().get(0) + "";
//									for (int i = 1; i < pGeoLayer.getSelSelection().getGeometryIndexList().size(); i++) {
//										idString += "," + pGeoLayer.getSelSelection().getGeometryIndexList().get(i);
//									}
//									
//									final String idss = idString;
//									
//								 if(allId.size()>0)
//								 {
//									 Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "您确定要减少图斑?", new ICallback(){
//
//											@Override
//											public void OnClick(String Str, Object ExtraStr) {
//												if(Str.equals("YES"))
//												{
//													PubVar.zhuijiaCheckCard.updateNewIds(idss);
//												}
//											}
//											
//										});
//								 }
//								 else
//								 {
//									 Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "是否立即将新选择的图斑追加到正在编辑的调查卡片?", new ICallback(){
//
//											@Override
//											public void OnClick(String Str, Object ExtraStr) {
//												if(Str.equals("YES"))
//												{
//													PubVar.zhuijiaCheckCard.updateNewIds(idss);
//												}
//											}
//											
//										}); 
//								 }
//									
//								
//									
////								}
//							}
//							
//							
//							
//						}
//					}
					

				}

			}
		}

		PubVar.allSelectWF = allWF;

		if (noSelect) {
			PubVar.allSelectWF = false;
		}
		
		

		Button szwf = ((Button) m_View.findViewById(R.id.bt_dc_wfwg));
		if (PubVar.allSelectWF)// 都是违法，改变按钮
		{

			Drawable topDrawable = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.icon_ducha_weifa);
			topDrawable.setBounds(0, 0, topDrawable.getIntrinsicWidth(), topDrawable.getIntrinsicHeight());
			szwf.setCompoundDrawables(null, topDrawable, null, null);
		} else {
			Drawable topDrawable = PubVar.m_DoEvent.m_Context.getResources().getDrawable(R.drawable.icon_ducha_weifa2);
			topDrawable.setBounds(0, 0, topDrawable.getIntrinsicWidth(), topDrawable.getIntrinsicHeight());
			szwf.setCompoundDrawables(null, topDrawable, null, null);

		}

		szwf.invalidate();

		PubVar.m_MapControl.invalidate();
	}
}
