package lkmap.ZRoadMap.DoEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.dingtu.DTGIS.DataDictionary.DataDicitonary;
import com.dingtu.DTGIS.DataService.DictDataDB;
import com.dingtu.DTGIS.GPS.GPSExporter;
import com.dingtu.DTGIS.Upload.UploadLayer;
import com.dingtu.Funtion.CopyObject;
import com.dingtu.Funtion.ProjectBackup;
import com.dingtu.Funtion.UpdateManager;
import com.dingtu.SLDuCha.CheckCard;
import com.dingtu.SLDuCha.DuChaSetting;
import com.dingtu.SLDuCha.TuBanYanZheng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import dingtu.ZRoadMap.AndroidMap;
import dingtu.ZRoadMap.AuthorizeTools;
import dingtu.ZRoadMap.AuthorizeTools_UserInfo;
import dingtu.ZRoadMap.GpsDetail;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.VectorDataFeature;
import dingtu.ZRoadMap.Data.v1_About;
import dingtu.ZRoadMap.Data.v1_CGpsDataInStatus;
import dingtu.ZRoadMap.Data.v1_CGpsInfoManage;
import dingtu.ZRoadMap.Data.v1_CGpsLine;
import dingtu.ZRoadMap.Data.v1_CGpsPoint;
import dingtu.ZRoadMap.Data.v1_CGpsPoly;
import dingtu.ZRoadMap.Data.v1_CGps_Data_InputCoor;
import dingtu.ZRoadMap.Data.v1_Data_Back_Feature;
import dingtu.ZRoadMap.Data.v1_Data_ClipScreen;
import dingtu.ZRoadMap.Data.v1_Data_MultiSelect;
import dingtu.ZRoadMap.Data.v1_Data_Query;
import dingtu.ZRoadMap.Data.v1_Data_Statistic;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Edit.DeleteAddObject;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkDataCollectType;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.GPS.GPSLocate;
import lkmap.GPS.GPSSocket;
import lkmap.Layer.GeoLayer;
import lkmap.Map.Param;
import lkmap.MapControl.MapControl;
import lkmap.MapControl.Tools;
import lkmap.MapControl.v1_MyGlass;
import lkmap.MapControl.v1_ScaleBar;
import lkmap.ToolBar.EditToolbar;
import lkmap.ToolBar.ToolBar;
import lkmap.ToolBar.v1_Agent_Measure;
import lkmap.UnRedo.IUnRedo;
import lkmap.ZRoadMap.Compass.v1_Compass;
import lkmap.ZRoadMap.Config.v1_ConfigDB;
import lkmap.ZRoadMap.Config.v1_SystemSet;
import lkmap.ZRoadMap.Config.v1_UserConfigDB;
import lkmap.ZRoadMap.DataExport.v1_data_export;
import lkmap.ZRoadMap.Music.v1_SoundTool;
import lkmap.ZRoadMap.Navigate.v1_navigate;
import lkmap.ZRoadMap.Project.LayerManger;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_ProjectDB;
import lkmap.ZRoadMap.Project.v1_project_layer_ex;
import lkmap.ZRoadMap.ToolsBox.v1_moretools;
import lkmap.ZRoadMap.ToolsBox.v1_poly_analysis;
import lkmap.ZRoadMap.Transformation.v1_transformation_plane_fourparam;

public class DoEvent {
	private MapControl m_MapControl = null;
	public AuthorizeTools m_AuthorizeTools = null; // 认证工具
	public int m_MainActivityID = -1; // 主界面的Activity值
	public Context m_Context = null; // 主界面的Context
	public View m_EditBar = null; // 编辑工具条
	public View m_SwipeBar = null; // 卷帘工具条
	public v1_MyGlass m_GlassView = null; // 放大镜
	public v1_ScaleBar m_ScaleBar = null; // 主界面上的比例尺条
	public ToolBar m_MainBottomToolBar = null; // 主底部工具条
	public EditToolbar m_EditToolbar = null; // 右侧编辑工具条
	public GPSLocate m_GPSLocate = null; // GPS通信类
	public v1_CGpsLine m_GPSLine = null; // 采集路线
	public v1_CGpsPoint m_GPSPoint = null; // 采集点位
	public v1_CGpsPoly m_GPSPoly = null; // 采集面
	public v1_Agent_Measure m_Agent_Measure = null; // 测量类
	public v1_ConfigDB m_ConfigDB = null; // 系统配置操作类
	public DictDataDB m_DictDataDB = null;
	public v1_UserConfigDB m_UserConfigDB = null; // 用户自定义配置操作类
	public v1_ProjectDB m_ProjectDB = null; // 工程配置操作类
	public DeleteAddObject m_Delete = null; // 删除实体
	// public v1_DataEdit m_DataEdit = null; //数据编辑类
	public GPSSocket m_GPSSocket = null; // 实时追踪类
	public v1_SoundTool m_SoundTool = null; // 系统声音类
	public v1_Compass m_Compass = null; // 指北针
	public v1_CGpsInfoManage m_GpsInfoManage = null; // GPS信息管理器
	public v1_CGpsDataInStatus m_CGpsDataInStatus = null; // 数据采集状态显示类
	public GPSExporter m_GpsExporter = null;
	public v1_navigate m_Navigate = null;

	public DoEvent_Project m_DoEvent_Project = null; // 子DoEent，与工程相关

	public DoEvent(Context _context) {
		this.m_MapControl = PubVar.m_MapControl;
		this.m_ScaleBar = new v1_ScaleBar();

		this.m_Context = _context;
		PubVar.m_DoEvent = this;

		// 系统声音类
		this.m_SoundTool = new v1_SoundTool(this.m_Context);

		// 启用认证功能
		this.m_AuthorizeTools = new AuthorizeTools(this.m_Context);

		this.m_GPSLine = new v1_CGpsLine();
		this.m_GPSPoint = new v1_CGpsPoint();
		this.m_GPSPoly = new v1_CGpsPoly();

		this.m_GpsInfoManage = new v1_CGpsInfoManage();
		this.m_CGpsDataInStatus = new v1_CGpsDataInStatus();

		// 配置库操作类
		this.m_ConfigDB = new v1_ConfigDB();
		m_DictDataDB = new DictDataDB();

		// 用户自定义配置库操作类
		this.m_UserConfigDB = new v1_UserConfigDB();

		this.m_UserConfigDB.LoadSystemConfig();

		// 工程库操作类
		this.m_ProjectDB = new v1_ProjectDB();
		this.m_DoEvent_Project = new DoEvent_Project(_context);

		// GPS控制类
		this.m_GPSLocate = new GPSLocate(this.m_MapControl);
		this.m_GPSLocate.m_Context = this.m_Context;
		PubVar.m_GPSLocate = this.m_GPSLocate;

		// 采集线，面类
		PubVar.m_GPSMap.SetGpsLine(this.m_GPSLine);
		PubVar.m_GPSMap.SetGpsPoly(this.m_GPSPoly);
		PubVar.m_GPSMap.SetGpsInfoManage(this.m_GpsInfoManage);

		// 数据编辑类
		// this.m_DataEdit = new v1_DataEdit();

		// 指北针
		this.m_Compass = new v1_Compass();
		this.m_Navigate = new v1_navigate();

	}

	/**
	 * 判断当前是否已经打开工程
	 * 
	 * @return
	 */
	public boolean AlwaysOpenProject() {
		return this.AlwaysOpenProject(true);
	}

	public boolean AlwaysOpenProject(boolean ShowMessage) {
		if (!this.m_ProjectDB.AlwaysOpenProject()) {
			if (ShowMessage)
				lkmap.Tools.Tools.ShowMessageBox(this.m_Context, lkmap.Tools.Tools.ToLocale("系统没有加载任何工程信息，无法完成操作！"));
		}
		return this.m_ProjectDB.AlwaysOpenProject();
	}

	// 执行各类操作
	@SuppressLint("NewApi")
	public void DoCommand(String CommandStr) {
		String[] CommandInfo = CommandStr.split("_");
		if (CommandInfo.length == 2) {
			// 分类型进入相关处理类，格式如：工程_选择
			if (CommandInfo[0].equals("工程")) {
				this.m_DoEvent_Project.DoCommand(CommandStr);
				return;
			}
		}

		if (CommandStr.equals("SelectEndCallBack")) {
			// this.m_DataEdit.IfShowEditToolbarBySelectEnd();
			// //跟据选中实体的情况，确定编辑工具条显示样式
			// int PointCount = lkmap.Tools.Tools.GetSelectObjectsCount(2, 0);
			// int LineCount = lkmap.Tools.Tools.GetSelectObjectsCount(2, 1);
			// int PolyCount = lkmap.Tools.Tools.GetSelectObjectsCount(2, 2);
			// if
			// ((PointCount+LineCount+PolyCount)==0)this.m_EditBar.setVisibility(View.GONE);
			// else this.m_EditBar.setVisibility(View.VISIBLE);
			// else
			// {
			// this.m_EditBar.setVisibility(View.VISIBLE);
			// this.m_EditBar.findViewById(R.id.tb_edit_vertex_add).setEnabled(true);
			// this.m_EditBar.findViewById(R.id.tb_edit_vertex_delete).setEnabled(true);
			// this.m_EditBar.findViewById(R.id.tb_edit_vertex_move).setEnabled(true);
			// if (PointCount>0 || (LineCount+PolyCount)>1)
			// {
			// this.m_EditBar.findViewById(R.id.tb_edit_vertex_add).setEnabled(false);
			// this.m_EditBar.findViewById(R.id.tb_edit_vertex_delete).setEnabled(false);
			// this.m_EditBar.findViewById(R.id.tb_edit_vertex_move).setEnabled(false);
			// }
			// }
		}

		if (CommandStr.equals("切换编辑图层")) {
			// 选"当前图层"
			if (!PubVar.m_DoEvent.AlwaysOpenProject())
				return;

			// 打开工程图层管理
			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					v1_project_layer_ex vpl = new v1_project_layer_ex();
					vpl.SetCallback(new ICallback() {
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (ExtraStr == null)
								return;
							v1_Layer player = (v1_Layer) ExtraStr;
							Dataset pDataset = PubVar.m_Workspace.GetDatasetById(player.GetLayerID());
							if (pDataset == null)
								return;
							// PubVar.m_Workspace.SetAllGeoLayerNoSelectable();
							PubVar.m_Map.ClearSelection();
							pDataset.getBindGeoLayer().setSelectable(true);
							if (player.GetLayerTypeName().equals("点"))
								PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("线"))
								PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("面"))
								PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
							m_MainBottomToolBar.LoadBottomToolBarByType("全部", false);
							m_MainBottomToolBar.LoadBottomToolBarByType(player.GetLayerTypeName(), true);
							m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "工具", false);
							// v1_Layer pLayer =
							// PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
							m_GpsInfoManage.SetCurrentLayerName(player);

						}
					});
					vpl.ShowDialog();
				}
			});
		}

		if (CommandStr.equals("图层")) {
			if (!this.AlwaysOpenProject())
				return;
			// 打开工程图层管理
			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					LayerManger vpl = new LayerManger();
					vpl.SetCallback(new ICallback() {
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (ExtraStr == null)
								return;
							v1_Layer player = (v1_Layer) ExtraStr;
							Dataset pDataset = PubVar.m_Workspace.GetDatasetById(player.GetLayerID());
							if (pDataset == null)
								return;
							// PubVar.m_Workspace.SetAllGeoLayerNoSelectable();
							PubVar.m_Map.ClearSelection();
							pDataset.getBindGeoLayer().setSelectable(true);
							// 处理界面

							if (player.GetLayerTypeName().equals("点"))
								PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("线"))
								PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("面"))
								PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
							m_MainBottomToolBar.LoadBottomToolBarByType("全部", false);
							if (Str.equals("Vector")) {
								m_MainBottomToolBar.LoadBottomToolBarByType("Vector", true);
								m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "工具", true);
							} else {
								m_MainBottomToolBar.LoadBottomToolBarByType(player.GetLayerTypeName(), true);
								m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "工具", false);
							}

							// v1_Layer pLayer =
							// PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
							m_GpsInfoManage.SetCurrentLayerName(player);

						}
					});
					vpl.ShowDialog();

				}
			});

			return;
		}

		if (CommandStr.equals("我的位置")) {
			if (!this.AlwaysOpenProject())
				return;
			if (!lkmap.Tools.Tools.ReadyGPS(true))
				return;

			Coordinate coordinate = PubVar.m_GPSLocate.getGPSCoordinate();
			if (coordinate != null) {
				this.m_MapControl._Pan.SetNewCenter(PubVar.m_GPSLocate.getGPSCoordinate());
			}

		}

		if (CommandStr.equals("点_坐标")) // 坐标绘点
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoint.CheckLayerValid())
				return;
			this.m_GPSPoint.AddPointByInputCoor();
		}

		if (CommandStr.equals("复制")) // 坐标绘点
		{
			CopyObject co = new CopyObject();
		}

		if (CommandStr.equals("点_手绘")) // 手动采点
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoint.CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPoint, this.m_GPSPoint, this.m_GPSPoint);
		}
		if (CommandStr.equals("点_gps")) // GPS采点
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoint.CheckLayerValid())
				return;
			this.m_GPSPoint.AddGPSPoint();
		}

		if (CommandStr.equals("线_手绘")) // 手动绘线
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSLine.CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_GPSLine, this.m_GPSLine);
			this.m_GPSLine.Start(lkDataCollectType.enManual);
		}
		if (CommandStr.equals("线_坐标")) // GPS定点绘线
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSLine.CheckLayerValid())
				return;
			v1_CGps_Data_InputCoor cdi = new v1_CGps_Data_InputCoor();
			cdi.SetDataset(this.m_GPSLine.GetDataset());
			cdi.ShowDialog();

			// this.m_MapControl.setActiveTools(Tools.AddPolyline,
			// this.m_GPSLine, this.m_GPSLine);
			// this.m_GPSLine.Start(lkDataCollectType.enGps_P);
			// if (lkmap.Tools.Tools.ReadyGPS(true))
			// {
			// //是否启用平均值采点
			// boolean averageEnable =
			// Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_AveragePointEnable").Value);
			// if
			// (!averageEnable)this.m_GPSLine.AddPoint(PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate());
			// else
			// {
			// this.m_GPSLine.AddAveragePoint();
			//
			//// v1_Data_Gps_AveragePoint dsap = new v1_Data_Gps_AveragePoint();
			//// dsap.SetDataType(lkGeoLayerType.enPolyline);
			//// dsap.SetCallback(new ICallback(){
			//// @Override
			//// public void OnClick(String Str, Object ExtraStr) {
			//// m_GPSLine.AddPoint((Coordinate)ExtraStr);
			//// }});
			//// dsap.ShowDialog();
			// }
			// }
		}
		if (CommandStr.equals("线_gps")) // GPS轨迹绘线
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSLine.CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_GPSLine, this.m_GPSLine);
			this.m_GPSLine.Start(lkDataCollectType.enGps_T);
		}
		if (CommandStr.equals("线_反向")) {
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSLine.CheckLayerValid())
				return;
			this.m_GPSLine.ChangeEditDirection();
		}

		if (CommandStr.equals("01_02_01_Feature")) // 正在采集线的属性
		{
			this.m_GPSLine.Edit();
		}

		if (CommandStr.equals("01_02_01_Undo")) // 回退一步采集线形
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSLine.Undo();
		}

		if (CommandStr.equals("01_02_01_Cancel")) // 取消当前采集线形
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSLine.Cancel();
		}

		if (CommandStr.equals("线_完成")) // 绘线，生成保存
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSLine.Stop();
		}

		if (CommandStr.equals("面_手绘")) // 手动绘面
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolygon, this.m_GPSPoly, this.m_GPSPoly);
			this.m_GPSPoly.getGPSLine().Start(lkDataCollectType.enManual);
		}

		if (CommandStr.equals("面_坐标")) // GPS定点绘面
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())
				return;
			v1_CGps_Data_InputCoor cdi = new v1_CGps_Data_InputCoor();
			cdi.SetDataset(this.m_GPSPoly.getGPSLine().GetDataset());
			cdi.ShowDialog();
			// if (!this.AlwaysOpenProject())return;
			// if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())return;
			// this.m_MapControl.setActiveTools(Tools.AddPolygon,
			// this.m_GPSPoly, this.m_GPSPoly);
			// this.m_GPSPoly.getGPSLine().Start(lkDataCollectType.enGps_P);
			//
			// if (lkmap.Tools.Tools.ReadyGPS(true))
			// {
			// //是否启用平均值采点
			// boolean averageEnable =
			// Boolean.parseBoolean(PubVar.m_HashMap.GetValueObject("Tag_System_GPS_AveragePointEnable").Value);
			// if
			// (!averageEnable)this.m_GPSPoly.getGPSLine().AddPoint(PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate());
			// else
			// {
			// this.m_GPSPoly.getGPSLine().AddAveragePoint();
			//// v1_Data_Gps_AveragePoint dsap = new v1_Data_Gps_AveragePoint();
			//// dsap.SetDataType(lkGeoLayerType.enPolyline);
			//// dsap.SetCallback(new ICallback(){
			//// @Override
			//// public void OnClick(String Str, Object ExtraStr) {
			//// m_GPSPoly.getGPSLine().AddPoint((Coordinate)ExtraStr);
			//// }});
			//// dsap.ShowDialog();
			// }
			//
			// //this.m_GPSPoly.getGPSLine().AddPoint(PubVar.m_DoEvent.m_GPSLocate.getGPSCoordinate());
			// }
		}
		if (CommandStr.equals("面_gps")) // GPS轨迹绘面
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolygon, this.m_GPSPoly, this.m_GPSPoly);
			this.m_GPSPoly.getGPSLine().Start(lkDataCollectType.enGps_T);
		}
		if (CommandStr.equals("面_反向")) {
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())
				return;
			this.m_GPSPoly.getGPSLine().ChangeEditDirection();
		}

		if (CommandStr.equals("01_03_01_Feature")) // 正在采集面的属性
		{
			this.m_GPSPoly.getGPSLine().Edit();
		}

		if (CommandStr.equals("01_03_01_Undo")) // 回退一步采集面
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSPoly.getGPSLine().Undo();
		}

		if (CommandStr.equals("01_03_01_Cancel")) // 取消当前采集面
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSPoly.getGPSLine().Cancel();
		}

		if (CommandStr.equals("面_完成")) // 绘面，生成保存
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSPoly.getGPSLine().Stop(lkGeoLayerType.enPolygon);
		}

		if (CommandStr.equals("测量")) {
			if (!this.AlwaysOpenProject())
				return;
			if (this.m_Agent_Measure == null) {
				this.m_Agent_Measure = new v1_Agent_Measure();
				this.m_Agent_Measure.OnPrepare();
			}
			this.m_Agent_Measure.SetZHMode();
		}

		if (CommandStr.equals("截图")) {
			if (!this.AlwaysOpenProject())
				return;
			Intent inet = new Intent(PubVar.m_DoEvent.m_Context, v1_Data_ClipScreen.class);
			PubVar.m_DoEvent.m_Context.startActivity(inet);
		}

		if (CommandStr.equals("校正参数")) {
			if (!this.AlwaysOpenProject())
				return;

			// 在此判断是否支持平面转换，也就是目标坐标系统为WGS-84坐标系，不支持
			String CoorSystemName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName();
			if (CoorSystemName.equals("WGS-84坐标")) {
				lkmap.Tools.Tools.ShowMessageBox("当前目标坐标系统为【" + CoorSystemName + "】，不支持转换参数！");
				return;
			}
			v1_transformation_plane_fourparam vtm = new v1_transformation_plane_fourparam();
			vtm.ShowDialog();
		}

		if (CommandStr.equals("导航")) {
			if (!this.AlwaysOpenProject())
				return;
			v1_navigate ng = PubVar.m_DoEvent.m_Navigate;
			ng.ShowDialog();
		}

		if (CommandStr.equals("更多工具")) {
			v1_moretools vtm = new v1_moretools();
			vtm.ShowDialog();
		}

		if (CommandStr.equals("面分析")) {
			if (!this.AlwaysOpenProject())
				return;

			int PolyCount = lkmap.Tools.Tools.GetSelectObjectsCount(-1, 2);
			if (PolyCount != 1) {
				lkmap.Tools.Tools.ShowMessageBox("请选择一个面状实体！");
				return;
			}
			// 打开面分析器
			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					v1_poly_analysis vpl = new v1_poly_analysis();
					vpl.ShowDialog();
				}
			});

			return;
		}

		if (CommandStr.equals("删除实体")) {
			if (!this.AlwaysOpenProject())
				return;

			// 在此判断是否有当前正在采集的线、面，如果有的话，先删除它们
			if (this.m_GPSPoly.getGPSLine().CheckIfStarting()) {
				lkmap.Tools.Tools.ShowYesNoMessage(this.m_Context, "是否删除当前正在采集面？", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_GPSPoly.getGPSLine().Cancel();
					}
				});
				return;
			}
			if (this.m_GPSLine.CheckIfStarting()) {
				lkmap.Tools.Tools.ShowYesNoMessage(this.m_Context, "是否删除当前正在采集线？", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_GPSLine.Cancel();
					}
				});
				return;
			}

			if (this.m_Delete == null)
				this.m_Delete = new DeleteAddObject();
			this.m_Delete.Delete();
		}

		if (CommandStr.equals("回退")) {
			if (!this.AlwaysOpenProject())
				return;

			// 先判断是否有当前正在采集的数据
			if (this.m_GPSPoly.getGPSLine().CheckIfStarting()) {
				if (this.m_GPSPoly.getGPSLine().Undo())
					return;
			}
			if (this.m_GPSLine.CheckIfStarting()) {
				if (this.m_GPSLine.Undo())
					return;
			}

			lkmap.Tools.Tools.OpenDialog("正在进行【回退】操作...", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (!IUnRedo.Undo()) {
						lkmap.Tools.Tools.ShowToast(m_Context, lkmap.Tools.Tools.ToLocale("无可【回退】操作记录！"));
					}
					DoCommand("SelectEndCallBack");
				}
			});

		}
		if (CommandStr.equals("重做")) {
			if (!this.AlwaysOpenProject())
				return;

			if (IUnRedo.Redo()) {
				return;
			}

			if (this.m_GPSPoly.getGPSLine().CheckIfStarting()) {
				if (this.m_GPSPoly.getGPSLine().Redo())
					return;
				return;
			}
			if (this.m_GPSLine.CheckIfStarting()) {
				if (this.m_GPSLine.Redo()) {

				} else {
					lkmap.Tools.Tools.ShowToast(m_Context, lkmap.Tools.Tools.ToLocale("无可【重做】操作记录！"));
				}
			}

		}

		if (CommandStr.equals("Edit_Move_Move")) {
			if (!this.AlwaysOpenProject())
				return;

			// 验证是否有选中的可移动实体
			int SelectObectCount = 0;
			for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {
				if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount() > 0) {
					SelectObectCount += pGeoLayer.getSelSelection().getCount();
				}
			}
			if (SelectObectCount > 0) {
				PubVar.m_MapControl.setActiveTool(Tools.MoveObject);
			} else {
				lkmap.Tools.Tools.ShowMessageBox(this.m_Context, "请在可编辑图层中选择需要移动的实体！");
				return;
			}

		}
		if (CommandStr.equals("Edit_Vertex_Move")) {
			if (!this.AlwaysOpenProject())
				return;
			PubVar.m_MapControl.setActiveTool(Tools.MoveVertex);
		}
		if (CommandStr.equals("Edit_Vertex_Delete")) {
			if (!this.AlwaysOpenProject())
				return;
			PubVar.m_MapControl.setActiveTool(Tools.DelVertex);
		}
		if (CommandStr.equals("Edit_Vertex_Add")) {
			if (!this.AlwaysOpenProject())
				return;
			PubVar.m_MapControl.setActiveTool(Tools.AddVertex);
		}

		if (CommandStr.equals("测试GPS")) {
			this.DoCommand("自动开启GPS");
			if (m_GPSLocate.GPS_OpenClose) {
				// 开启GPS设置对话框
				GpsDetail gps = new GpsDetail();
				gps.ShowDialog();
				// Intent inet = new Intent(m_Context,v1_GPSSet.class);
				// //startActivityForResult(intent1,MYREQUEST_CODE);
				// //启动这个intent跳转
				// m_Context.startActivity(inet);
			}
			return;
		}

		if (CommandStr.equals("自动开启GPS")) {
			// if (!AuthorizeTools.IfAuthorizePass())return;
			if (!m_GPSLocate.GPS_OpenClose) {
				m_GPSLocate.OpenGPS();
			}
			return;
		}
		if (CommandStr.equals("关闭GPS")) {
			if (m_GPSLocate.GPS_OpenClose) {
				m_GPSLocate.CloseGPS();
			}
			return;
		}

		if (CommandStr.equals("系统设置")) {
			if (!this.AlwaysOpenProject())
				return;
			v1_SystemSet ss = new v1_SystemSet();
			ss.ShowDialog();
			return;
		}

		if (CommandStr.equals("转换参数管理")) {
			// if (!this.AlwaysOpenProject()) return;
			// v1_transformation_paramanage tp = new
			// v1_transformation_paramanage();
			// tp.ShowDialog();
			if (!this.AlwaysOpenProject())
				return;
			UploadLayer uLayer = new UploadLayer();
			uLayer.ShowDialog();
			return;
		}

		// if (CommandStr.equals("加载"))
		// {
		// this.LoadMap();
		// this.m_MapControl.setActiveTool(Tools.FullScreenSize);
		// if
		// (!lkmap.Tools.Tools.RestoreViewExtend())this.m_MapControl.setActiveTool(Tools.FullScreen);
		// this.m_MapControl.setActiveTool(Tools.ZoomInOutPan);
		// //this.m_MapControl.setActiveTool(Tools.Select);
		// return;
		// }

		if (CommandStr.equals("查询数据")) {
			if (!this.AlwaysOpenProject())
				return;
			// 打开工程图层管理
			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					v1_Data_Query vdq = new v1_Data_Query();
					vdq.ShowDialog();
				}
			});
		}

		// if (CommandStr.equals("加载工程"))
		// {
		// this.v1_LoadMap();
		// }

		if (CommandStr.equals("手势缩放")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.ZoomInOutPan);
			return;
		}

		if (CommandStr.equals("卷帘")) {
			if (PubVar.m_DoEvent.AlwaysOpenProject()) {
				PubVar.m_MapControl.setActiveTool(Tools.Shutter);
				// PubVar.m_DoEvent.m_EditToolbar.ClearButtonSelect();
				// lkmap.Tools.Tools.SetToolsBarItemSelect(arg0, true);
			}

			return;
		}

		if (CommandStr.equals("放大")) {
			this.m_MapControl.setActiveTool(Tools.ZoomIn);
			return;
		}
		if (CommandStr.equals("单击放大")) {

			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.ZoomOut);
			this.m_MapControl.SetZoomIn();

			// this.m_MapControl.getMap().Refresh();
			return;
		}
		if (CommandStr.equals("缩小")) {
			this.m_MapControl.setActiveTool(Tools.ZoomOut);
			return;
		}
		if (CommandStr.equals("单击缩小")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.ZoomIn);
			this.m_MapControl.SetZoomOut();
			return;
		}
		if (CommandStr.equals("移屏")) {
			this.m_MapControl.setActiveTool(Tools.Pan);
			return;
		}
		if (CommandStr.equals("全屏")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.FullScreen);
			return;
		}

		if (CommandStr.equals("选择")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.Select);
			return;
		}
		if (CommandStr.equals("属性1")) {

			Param GeoLayerName = new Param();
			Param SYSID = new Param();
			if (!lkmap.Tools.Tools.GetSelectOneObjectInfo(GeoLayerName, SYSID)) {
				lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools.ToLocale("请选择需要查询的实体！"));
				return;
			}
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(GeoLayerName.getStringValue());
			if (pDataset.getDataSource().getEditing()) // 是否为正在编辑的数据源
			{
				if (pDataset.getType() == lkmap.Enum.lkGeoLayerType.enPoint) {
					m_GPSPoint.Edit(pDataset.getId(), SYSID.getInt());
				}
				if (pDataset.getType() == lkmap.Enum.lkGeoLayerType.enPolyline) {
					m_GPSLine.Edit(pDataset.getId(), SYSID.getInt());
				}
				if (pDataset.getType() == lkmap.Enum.lkGeoLayerType.enPolygon) {
					// m_GPSPoly.Edit(pDataset.getId(),SYSID.getInt());
					m_GPSPoly.Edit(pDataset.getId(), SYSID.getInt());

				}
			} else {

				if (PubVar.VectorBGEditable) {
					VectorDataFeature vdF = new VectorDataFeature(pDataset.getId(), SYSID.getInt());
				} else {
					v1_Data_Back_Feature v1dbf = new v1_Data_Back_Feature();
					v1dbf.ShowDialog();
				}

			}

			return;
		}
		if (CommandStr.equals("属性")) {
			if (!this.AlwaysOpenProject())
				return;
			
			
			
			// 提取需要显示的实体，如果有多个实体被选中，而给出相应提示栏进行选择
			int SelectObjectsCount = lkmap.Tools.Tools.GetSelectObjectsCount();
			if (SelectObjectsCount == 1)
				this.DoCommand("属性1");
			else if (SelectObjectsCount == 0)
				lkmap.Tools.Tools.ShowToast(this.m_Context, lkmap.Tools.Tools.ToLocale("请选择需要查询的实体！"));// lkmap.Tools.Tools.ShowMessageBox("请选择需要查询的实体！");
			else if(SelectObjectsCount>1)
			{
				boolean hasSelect = false;
				String layerId="";
				List<Integer> selectIndexs=new ArrayList<Integer>();
				for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {

					if (pGeoLayer.getSelSelection().getCount() > 0) {

						if (PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId()) == null) {
							continue;
						}
						if (PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId())
								.GetLayerProjecType().equals(ForestryLayerType.DuChaYanZheng)) {
							if (hasSelect) {
								lkmap.Tools.Tools.ShowMessageBox("所选图层不在一个督查图层，请检查后选择！");
								return;
							}
							layerId = pGeoLayer.getId();
							selectIndexs = pGeoLayer.getSelSelection().getGeometryIndexList();
							
							hasSelect = true;
						}
					}
				}
				
				if(hasSelect)
				{
										
					if(selectIndexs.size()>1)
					{
						v1_Layer layer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerId);
						String tuBanDF = layer.GetDataFieldNameByFieldName("图斑号");
						Dataset pDataset = PubVar.m_Workspace.GetDatasetById(layerId);
						Collections.sort(selectIndexs,new Comparator<Integer>(){

							@Override
							public int compare(Integer o1, Integer o2) {
								return o1.compareTo(o2);
							}
							
						});
						
						
						if(tuBanDF.length()>0)
						{
							SQLiteDataReader reader = pDataset.getDataSource().Query("select distinct "+tuBanDF+" from "+pDataset.getDataTableName()+" where SYS_ID in ("+lkmap.Tools.Tools.JoinIntT(",", selectIndexs)+")");
							if(reader.GetCount()>1)
							{
								lkmap.Tools.Tools.ShowMessageBox("所选图斑号不同，同一图斑号才能合并现地验证！");
								return;
							}
							else
							{
								new TuBanYanZheng(layerId,selectIndexs.get(0),selectIndexs);
							}
							reader.Close();
						}
						else
						{
							lkmap.Tools.Tools.ShowMessageBox("疑似图层没有图斑号字段！");
						}	
						
					}
					else
					{
						new TuBanYanZheng(layerId,selectIndexs.get(0),selectIndexs);
					}
					
				}
				else
				{
					v1_Data_MultiSelect dm = new v1_Data_MultiSelect();
					dm.ShowDialog();
				}
				
			}
//			if (SelectObjectsCount > 1) // 弹出多选实体的对话框，可在其中进行选择需要查询的实体
//			{
//				v1_Data_MultiSelect dm = new v1_Data_MultiSelect();
//				dm.ShowDialog();
//			}
			return;
		}
		
		

		if (CommandStr.equals("数据导出")) {
			if (!this.AlwaysOpenProject())
				return;
			AuthorizeTools_UserInfo UI = PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo();
			if (UI.SYS_UserType.equals("临时用户")) {
				lkmap.Tools.Tools.ShowMessageBox("临时用户不支持导出功能！");
				return;
			}
			try {
				v1_data_export vps = new v1_data_export();
				vps.ShowDialog();
			} catch (Exception ex) {

			}

		}

		if (CommandStr.equals("采集统计")) {
			if (!this.AlwaysOpenProject())
				return;
			v1_Data_Statistic ds = new v1_Data_Statistic();
			ds.ShowDialog();
			return;
		}

		if (CommandStr.equals("数据字典")) {
			// v1_DataDictionary_Manage dm = new v1_DataDictionary_Manage();
			// dm.ShowDialog();

			// DataDict ddDataDict = new DataDict();
			// ddDataDict.ShowDialog();

			DataDicitonary DD = new DataDicitonary();
			DD.ShowDialog();
		}

		if (CommandStr.equals("保存数据")) {
			if (!this.AlwaysOpenProject())
				return;
			lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("正在保存数据") + "...", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					List<String> ErrorList = new ArrayList<String>();
					for (v1_Layer pLayer : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
						Dataset pDataset = PubVar.m_Workspace.GetDatasetById(pLayer.GetLayerID());
						if (!pDataset.Purge())
							ErrorList.add(pDataset.getBindGeoLayer().GetAliasName());
					}
					if (ErrorList.size() > 0) {
						lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools
								.ToLocale("以下图层数据整理失败！\r\n" + lkmap.Tools.Tools.JoinT("\r\n", ErrorList)));
					} else {
						IUnRedo.ClearDo();
						SaveProjectWorkspace();
						lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools.ToLocale("数据保存成功！"));
					}
				}
			});
		}
		
		if (CommandStr.equals("督查设置")) {
			
			DuChaSetting ducha = new DuChaSetting();
			ducha.ShowDialog();
		
		}

		if (CommandStr.equals("督查_违法违规")) {
			boolean hasSelected = false;

			String setSql = " =1";
			if (PubVar.allSelectWF) {
				setSql = "=0";
			}

			for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {

				if (pGeoLayer.getSelSelection().getCount() > 0 && PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer()
						.GetLayerByID(pGeoLayer.getId()).GetLayerProjecType().equals(ForestryLayerType.DuChaYanZheng)) {
					hasSelected = true;

					final String strcheange = setSql;
					final List<Integer> allIds = pGeoLayer.getSelSelection().getGeometryIndexList();
					final String layerID = pGeoLayer.getId();
					String message = "是否将所选小班设置成违法？";
					if (PubVar.allSelectWF) {
						message = "是否取消所选小班设置的违法状态？";
					}
					lkmap.Tools.Tools.ShowYesNoMessage(m_Context, message, new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								Dataset pDataset = PubVar.m_Workspace.GetDatasetById(layerID);
								String filed = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID)
										.GetDataFieldNameByFieldName("违法违规");
								String idString = "";
								List<Geometry> selGeometry = new ArrayList<Geometry>();
								for (int index : allIds) {
									if (idString.length() == 0) {
										idString += index + "";
									} else {
										idString += "," + index;
									}

									selGeometry.add(pDataset.GetGeometry(index));
								}
								if (filed.length() > 0) {
									String sql = "update " + pDataset.getDataTableName() + " set " + filed + strcheange
											+ " where SYS_ID in (" + idString + ")";

									if (pDataset.getDataSource().ExcuteSQL(sql)) {
										for (Geometry geometry : selGeometry) {
											pDataset.getBindGeoLayer().getRender().UpdateSymbol(geometry);
										}

										// lkmap.Tools.Tools
										// .ShowMessageBox("已将所选" +
										// pGeoLayer.getSelSelection().getCount()
										// + "个小班设置成违法违规小班！");
									}
								}

							}

						}
					});

				}
			}

			if (!hasSelected) {
				lkmap.Tools.Tools.ShowMessageBox("请在森林督查图斑验证图层选择您要标注的违法图斑！");
				return;
			}

			PubVar.m_Map.Refresh();
			// PubVar.m_Map.FastRefresh();
			PubVar.m_MapControl.invalidate();
		}

		if (CommandStr.equals("督查_检查卡片")) {

			boolean hasSelect = false;
			List<Integer> selectIndexs = null;
			String layerId = "";

			for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {

				if (pGeoLayer.getSelSelection().getCount() > 0) {

					if (PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId()) == null) {
						continue;
					}
					if (PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pGeoLayer.getId())
							.GetLayerProjecType().equals(ForestryLayerType.DuChaYanZheng)) {
						if (hasSelect) {
							lkmap.Tools.Tools.ShowMessageBox("所选图层不在一个督查图层，请检查后选择！");
							return;
						}
						layerId = pGeoLayer.getId();
						selectIndexs = pGeoLayer.getSelSelection().getGeometryIndexList();
						
						hasSelect = true;
					}
				}
			}

			if (selectIndexs != null) {

				if (selectIndexs.size() > 1) {
					Collections.sort(selectIndexs,new Comparator<Integer>(){

						@Override
						public int compare(Integer o1, Integer o2) {
							return o1.compareTo(o2);
						}
						
					});
					final List<Integer> allIndexs = selectIndexs;
					final String lid = layerId;
					lkmap.Tools.Tools.ShowYesNoMessage(m_Context, "是否将所选中" +selectIndexs.size()+ "个图斑合并检查？", new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								CheckCard checkCard = new CheckCard();

//								String idString = allIndexs.get(0) + "";
//								for (int i = 1; i < allIndexs.size(); i++) {
//									idString += "," + allIndexs.get(i);
//								}
								checkCard.setTuBan(lid, allIndexs,"");
							}
						}

					});
				} else {
					CheckCard checkCard = new CheckCard();
					List<Integer> index = new ArrayList<Integer>();
					index.add(selectIndexs.get(0));
					checkCard.setTuBan(layerId, index,"");
				}

			} else {
				lkmap.Tools.Tools.ShowMessageBox("请至少选择一个督查验证图斑！");
			}

			return;
		}

		if (CommandStr.equals("航迹管理")) {
			if (m_GpsExporter == null) {
				m_GpsExporter = new GPSExporter();
			}
			m_GpsExporter.ShowDialog();
		}

		if (CommandStr.equals("碳汇导出")) {
			if (!this.AlwaysOpenProject())
				return;
			lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("正在导出碳汇数据") + "...", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					List<String> ErrorList = new ArrayList<String>();
					for (v1_Layer pLayer : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
						if (pLayer.GetLayerProjecType() != null && pLayer.GetLayerProjecType().contains("碳汇")) {

							String sql = "select SYS_ID,F1,F2,F3 from " + pLayer.GetDataTableName();
							SQLiteDataReader DR = new DataSource(
									PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName())
											.Query(sql);
							while (DR.Read()) {
								int id = DR.GetInt32(0);
								String ydh = DR.GetString("F1");
								String xbh = DR.GetString("F2");
								String bzdh = DR.GetString("F3");
								if (!exportOneTanhui(pLayer, id, ydh + xbh + bzdh + ".CSV")) {
									ErrorList.add(ydh + xbh + bzdh + ".CSV 导出失败");
								}
							}

							DR.Close();
						}
					}
					if (ErrorList.size() > 0) {
						lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools
								.ToLocale("以下碳汇数据导出失败！\r\n" + lkmap.Tools.Tools.JoinT("\r\n", ErrorList)));
					} else {

						lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools.ToLocale("碳汇数据导出成功！"));
					}
				}
			});
		}

		if (CommandStr.equals("关于系统")) {
			DoCommand("隐藏选项菜单");
			v1_About AB = new v1_About();
			AB.ShowDialog();

		}

		if (CommandStr.equals("退出系统")) {
			DoCommand("隐藏选项菜单");

			// 在此在加入检测是否有采集任务的部分
			if (this.CheckHasDataInTask("正在采集数据中，请停止采集任务后再尝试退出！"))
				return;

			lkmap.Tools.Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, lkmap.Tools.Tools.ToLocale("是否确定退出督查系统？"),
					new ICallback() {
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							DoCommand("完全退出");
						}
					});
		}
		if (CommandStr.equals("完全退出")) {
			lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("正在准备退出") + "...", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (AlwaysOpenProject(false)) {
						// DataSource pDataSource =
						// PubVar.m_Workspace.GetDataSourceByEditing();
						// if (pDataSource != null)
						// {
						// for (Dataset pDataset : pDataSource.getDatasets())
						// {
						// pDataset.Purge(); //清理数据集,清理打上删除标识的实体,更新图层级索引
						// }
						// }
						SaveProjectWorkspace();
						new ProjectBackup().backupProjectFile();
					}

					AuthorizeTools_UserInfo UI = PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo();
					if (UI.SYS_UserType.equals("临时用户")) {
						try {
							// String prjPath =
							// PubVar.m_SysAbsolutePath+"/Data";;
							// File file = new File(prjPath);
							// lkmap.Tools.Tools.DeleteAllSub(file);
							PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().deleteAllEditingData();
						} catch (Exception ex) {
							lkmap.Tools.Tools.ShowMessageBox(ex.getMessage());
						}

					}

					// 清除消息通知栏
					AndroidMap.ClearNotification(m_Context);
					ActivityManager am = (ActivityManager) m_Context.getSystemService(Context.ACTIVITY_SERVICE);
					String PackName = m_Context.getPackageName();
					am.killBackgroundProcesses(PackName); // API Level至少为8才能使用

					System.exit(0);

				}
			});

			return;
		}
		if (CommandStr.equals("系统更新")) {
			UpdateManager um = new UpdateManager(m_Context);
			um.checkUpdate();
		}
		
	}

	private void SaveProjectWorkspace() {
		// 保存数据预览状态图，方便下次加载工程时用到
		lkmap.Tools.Tools.SaveBitmapTo(
				PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataPreviewImageName(), PubVar.m_Map.bp);

		// 记录当前的工程视图范围，下次直接调用相应范围
		PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().SaveShowExtend(PubVar.m_Map.getExtend());
	}

	@SuppressLint("NewApi")
	public boolean exportOneTanhui(v1_Layer layer, int sysID, String fileName) {
		boolean isOkay = false;
		String sdCardDir = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/数据导出/碳汇";

		File saveFile = new File(sdCardDir, fileName);
		if (!lkmap.Tools.Tools.ExistFile(sdCardDir)) {
			(new File(sdCardDir)).mkdirs();
		}

		try {
			String sql = "select * from " + layer.GetDataTableName() + " where SYS_ID =" + sysID;
			SQLiteDataReader DR = new DataSource(
					PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()).Query(sql);
			if (DR.Read()) {
				FileOutputStream bcpFileWriter = new FileOutputStream(saveFile);

				byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
				bcpFileWriter.write(bom);

				String title = ", ,每木检尺表,,";
				bcpFileWriter.write((new String(title.getBytes(), "utf-8")).getBytes());
				bcpFileWriter.write("\n".getBytes());
				bcpFileWriter.write(",,,,\n".getBytes());

				String firstLine = "样地号:" + DR.GetString("F1") + "," + "小班号:" + DR.GetString("F2") + "," + "," + "标准地号："
						+ DR.GetString("F3") + ",\n";
				bcpFileWriter.write((new String(firstLine.getBytes(), "utf-8")).getBytes());

				String titleLine = "编号,树种,胸径(cm),蓄积量\n";
				bcpFileWriter.write((new String(titleLine.getBytes(), "utf-8")).getBytes());

				for (int i = PubVar.MinTanhuiIndex; i < PubVar.maxTanhuiIndex; i++) {
					String jianchiValue = DR.GetString("F" + i);
					if (jianchiValue.isEmpty()) {
						continue;
					}

					String[] all = jianchiValue.split(";");

					for (String per : all) {

						String[] properties = per.split(",");
						String valueLine = properties[0] + "," + properties[1] + "," + properties[3] + ","
								+ properties[4] + ",\n";
						bcpFileWriter.write((new String(valueLine.getBytes(), "utf-8")).getBytes());
					}

				}

				String lastLine = "平均高：" + String.valueOf(DR.GetDouble("F4")) + ",," + " 样地蓄积："
						+ String.valueOf(DR.GetDouble("F6")) + ",";
				bcpFileWriter.write((new String(lastLine.getBytes(), "utf-8")).getBytes());
				bcpFileWriter.flush();
				bcpFileWriter.close();
				lkmap.Tools.Tools.ShowMessageBox(m_Context, "数据成功导出！\r\n\r\n位于：【" + sdCardDir + fileName + "】");
			}

			DR.Close();
			isOkay = true;

		} catch (Exception ex) {
			lkmap.Tools.Tools.ShowMessageBox(ex.getLocalizedMessage());
		}

		return isOkay;
	}

	// 设置主工具条上按钮图片的状态
	private void SetButtomImageStatus(String SelectItemStr) {
		List<String> MenuItemList = new ArrayList<String>();
		MenuItemList.add("MenuItem1,menu_select");
		MenuItemList.add("MenuItem2,menu_feature");
		MenuItemList.add("MenuItem3,menu_line");
		MenuItemList.add("MenuItem4,menu_point");
		MenuItemList.add("MenuItem5,menu_otherpoint");

		Activity aty = (Activity) m_Context;
		for (String MenuItem : MenuItemList) {
			String MenuName = MenuItem.split(",")[0];
			String ICONName = MenuItem.split(",")[1];
			String SelectICONName = ICONName + "_0";
			int id1 = m_Context.getResources().getIdentifier(MenuName, "id", m_Context.getPackageName());
			int id2 = m_Context.getResources().getIdentifier(ICONName, "drawable", m_Context.getPackageName());
			int id3 = m_Context.getResources().getIdentifier(SelectICONName, "drawable", m_Context.getPackageName());
			ImageView IV = ((ImageView) aty.findViewById(id1));
			if (MenuName.equals(SelectItemStr)) {
				LayoutParams para = IV.getLayoutParams();
				para.width = 100;
				para.height = 64;
				IV.setLayoutParams(para);
				IV.setImageResource(id3);
			} else {
				LayoutParams para = IV.getLayoutParams();
				para.width = 74;
				para.height = 48;
				IV.setLayoutParams(para);
				IV.setImageResource(id2);
			}
		}
	}

	/**
	 * 检测是否有采集任务
	 * 
	 * @param showMessageBox
	 * @return
	 */
	public boolean CheckHasDataInTask(String MessageStr) {
		if (this.m_GPSLine.CheckIfStarting() || this.m_GPSPoly.getGPSLine().CheckIfStarting()) {
			if (!MessageStr.equals(""))
				lkmap.Tools.Tools.ShowMessageBox(MessageStr);
			return true;
		}
		return false;
	}

}
