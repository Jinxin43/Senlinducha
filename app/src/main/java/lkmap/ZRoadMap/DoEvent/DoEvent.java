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
	public AuthorizeTools m_AuthorizeTools = null; // ��֤����
	public int m_MainActivityID = -1; // �������Activityֵ
	public Context m_Context = null; // �������Context
	public View m_EditBar = null; // �༭������
	public View m_SwipeBar = null; // ����������
	public v1_MyGlass m_GlassView = null; // �Ŵ�
	public v1_ScaleBar m_ScaleBar = null; // �������ϵı�������
	public ToolBar m_MainBottomToolBar = null; // ���ײ�������
	public EditToolbar m_EditToolbar = null; // �Ҳ�༭������
	public GPSLocate m_GPSLocate = null; // GPSͨ����
	public v1_CGpsLine m_GPSLine = null; // �ɼ�·��
	public v1_CGpsPoint m_GPSPoint = null; // �ɼ���λ
	public v1_CGpsPoly m_GPSPoly = null; // �ɼ���
	public v1_Agent_Measure m_Agent_Measure = null; // ������
	public v1_ConfigDB m_ConfigDB = null; // ϵͳ���ò�����
	public DictDataDB m_DictDataDB = null;
	public v1_UserConfigDB m_UserConfigDB = null; // �û��Զ������ò�����
	public v1_ProjectDB m_ProjectDB = null; // �������ò�����
	public DeleteAddObject m_Delete = null; // ɾ��ʵ��
	// public v1_DataEdit m_DataEdit = null; //���ݱ༭��
	public GPSSocket m_GPSSocket = null; // ʵʱ׷����
	public v1_SoundTool m_SoundTool = null; // ϵͳ������
	public v1_Compass m_Compass = null; // ָ����
	public v1_CGpsInfoManage m_GpsInfoManage = null; // GPS��Ϣ������
	public v1_CGpsDataInStatus m_CGpsDataInStatus = null; // ���ݲɼ�״̬��ʾ��
	public GPSExporter m_GpsExporter = null;
	public v1_navigate m_Navigate = null;

	public DoEvent_Project m_DoEvent_Project = null; // ��DoEent���빤�����

	public DoEvent(Context _context) {
		this.m_MapControl = PubVar.m_MapControl;
		this.m_ScaleBar = new v1_ScaleBar();

		this.m_Context = _context;
		PubVar.m_DoEvent = this;

		// ϵͳ������
		this.m_SoundTool = new v1_SoundTool(this.m_Context);

		// ������֤����
		this.m_AuthorizeTools = new AuthorizeTools(this.m_Context);

		this.m_GPSLine = new v1_CGpsLine();
		this.m_GPSPoint = new v1_CGpsPoint();
		this.m_GPSPoly = new v1_CGpsPoly();

		this.m_GpsInfoManage = new v1_CGpsInfoManage();
		this.m_CGpsDataInStatus = new v1_CGpsDataInStatus();

		// ���ÿ������
		this.m_ConfigDB = new v1_ConfigDB();
		m_DictDataDB = new DictDataDB();

		// �û��Զ������ÿ������
		this.m_UserConfigDB = new v1_UserConfigDB();

		this.m_UserConfigDB.LoadSystemConfig();

		// ���̿������
		this.m_ProjectDB = new v1_ProjectDB();
		this.m_DoEvent_Project = new DoEvent_Project(_context);

		// GPS������
		this.m_GPSLocate = new GPSLocate(this.m_MapControl);
		this.m_GPSLocate.m_Context = this.m_Context;
		PubVar.m_GPSLocate = this.m_GPSLocate;

		// �ɼ��ߣ�����
		PubVar.m_GPSMap.SetGpsLine(this.m_GPSLine);
		PubVar.m_GPSMap.SetGpsPoly(this.m_GPSPoly);
		PubVar.m_GPSMap.SetGpsInfoManage(this.m_GpsInfoManage);

		// ���ݱ༭��
		// this.m_DataEdit = new v1_DataEdit();

		// ָ����
		this.m_Compass = new v1_Compass();
		this.m_Navigate = new v1_navigate();

	}

	/**
	 * �жϵ�ǰ�Ƿ��Ѿ��򿪹���
	 * 
	 * @return
	 */
	public boolean AlwaysOpenProject() {
		return this.AlwaysOpenProject(true);
	}

	public boolean AlwaysOpenProject(boolean ShowMessage) {
		if (!this.m_ProjectDB.AlwaysOpenProject()) {
			if (ShowMessage)
				lkmap.Tools.Tools.ShowMessageBox(this.m_Context, lkmap.Tools.Tools.ToLocale("ϵͳû�м����κι�����Ϣ���޷���ɲ�����"));
		}
		return this.m_ProjectDB.AlwaysOpenProject();
	}

	// ִ�и������
	@SuppressLint("NewApi")
	public void DoCommand(String CommandStr) {
		String[] CommandInfo = CommandStr.split("_");
		if (CommandInfo.length == 2) {
			// �����ͽ�����ش����࣬��ʽ�磺����_ѡ��
			if (CommandInfo[0].equals("����")) {
				this.m_DoEvent_Project.DoCommand(CommandStr);
				return;
			}
		}

		if (CommandStr.equals("SelectEndCallBack")) {
			// this.m_DataEdit.IfShowEditToolbarBySelectEnd();
			// //����ѡ��ʵ��������ȷ���༭��������ʾ��ʽ
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

		if (CommandStr.equals("�л��༭ͼ��")) {
			// ѡ"��ǰͼ��"
			if (!PubVar.m_DoEvent.AlwaysOpenProject())
				return;

			// �򿪹���ͼ�����
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
							if (player.GetLayerTypeName().equals("��"))
								PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("��"))
								PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("��"))
								PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
							m_MainBottomToolBar.LoadBottomToolBarByType("ȫ��", false);
							m_MainBottomToolBar.LoadBottomToolBarByType(player.GetLayerTypeName(), true);
							m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "����", false);
							// v1_Layer pLayer =
							// PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
							m_GpsInfoManage.SetCurrentLayerName(player);

						}
					});
					vpl.ShowDialog();
				}
			});
		}

		if (CommandStr.equals("ͼ��")) {
			if (!this.AlwaysOpenProject())
				return;
			// �򿪹���ͼ�����
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
							// �������

							if (player.GetLayerTypeName().equals("��"))
								PubVar.m_DoEvent.m_GPSPoint.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("��"))
								PubVar.m_DoEvent.m_GPSLine.SetDataset(pDataset);
							if (player.GetLayerTypeName().equals("��"))
								PubVar.m_DoEvent.m_GPSPoly.SetDataset(pDataset);
							m_MainBottomToolBar.LoadBottomToolBarByType("ȫ��", false);
							if (Str.equals("Vector")) {
								m_MainBottomToolBar.LoadBottomToolBarByType("Vector", true);
								m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "����", true);
							} else {
								m_MainBottomToolBar.LoadBottomToolBarByType(player.GetLayerTypeName(), true);
								m_EditToolbar.ShowToolsItem(player.GetLayerTypeName() + "����", false);
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

		if (CommandStr.equals("�ҵ�λ��")) {
			if (!this.AlwaysOpenProject())
				return;
			if (!lkmap.Tools.Tools.ReadyGPS(true))
				return;

			Coordinate coordinate = PubVar.m_GPSLocate.getGPSCoordinate();
			if (coordinate != null) {
				this.m_MapControl._Pan.SetNewCenter(PubVar.m_GPSLocate.getGPSCoordinate());
			}

		}

		if (CommandStr.equals("��_����")) // ������
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoint.CheckLayerValid())
				return;
			this.m_GPSPoint.AddPointByInputCoor();
		}

		if (CommandStr.equals("����")) // ������
		{
			CopyObject co = new CopyObject();
		}

		if (CommandStr.equals("��_�ֻ�")) // �ֶ��ɵ�
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoint.CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPoint, this.m_GPSPoint, this.m_GPSPoint);
		}
		if (CommandStr.equals("��_gps")) // GPS�ɵ�
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoint.CheckLayerValid())
				return;
			this.m_GPSPoint.AddGPSPoint();
		}

		if (CommandStr.equals("��_�ֻ�")) // �ֶ�����
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSLine.CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_GPSLine, this.m_GPSLine);
			this.m_GPSLine.Start(lkDataCollectType.enManual);
		}
		if (CommandStr.equals("��_����")) // GPS�������
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
			// //�Ƿ�����ƽ��ֵ�ɵ�
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
		if (CommandStr.equals("��_gps")) // GPS�켣����
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSLine.CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolyline, this.m_GPSLine, this.m_GPSLine);
			this.m_GPSLine.Start(lkDataCollectType.enGps_T);
		}
		if (CommandStr.equals("��_����")) {
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSLine.CheckLayerValid())
				return;
			this.m_GPSLine.ChangeEditDirection();
		}

		if (CommandStr.equals("01_02_01_Feature")) // ���ڲɼ��ߵ�����
		{
			this.m_GPSLine.Edit();
		}

		if (CommandStr.equals("01_02_01_Undo")) // ����һ���ɼ�����
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSLine.Undo();
		}

		if (CommandStr.equals("01_02_01_Cancel")) // ȡ����ǰ�ɼ�����
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSLine.Cancel();
		}

		if (CommandStr.equals("��_���")) // ���ߣ����ɱ���
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSLine.Stop();
		}

		if (CommandStr.equals("��_�ֻ�")) // �ֶ�����
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolygon, this.m_GPSPoly, this.m_GPSPoly);
			this.m_GPSPoly.getGPSLine().Start(lkDataCollectType.enManual);
		}

		if (CommandStr.equals("��_����")) // GPS�������
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
			// //�Ƿ�����ƽ��ֵ�ɵ�
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
		if (CommandStr.equals("��_gps")) // GPS�켣����
		{
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())
				return;
			this.m_MapControl.setActiveTools(Tools.AddPolygon, this.m_GPSPoly, this.m_GPSPoly);
			this.m_GPSPoly.getGPSLine().Start(lkDataCollectType.enGps_T);
		}
		if (CommandStr.equals("��_����")) {
			if (!this.AlwaysOpenProject())
				return;
			if (!this.m_GPSPoly.getGPSLine().CheckLayerValid())
				return;
			this.m_GPSPoly.getGPSLine().ChangeEditDirection();
		}

		if (CommandStr.equals("01_03_01_Feature")) // ���ڲɼ��������
		{
			this.m_GPSPoly.getGPSLine().Edit();
		}

		if (CommandStr.equals("01_03_01_Undo")) // ����һ���ɼ���
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSPoly.getGPSLine().Undo();
		}

		if (CommandStr.equals("01_03_01_Cancel")) // ȡ����ǰ�ɼ���
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSPoly.getGPSLine().Cancel();
		}

		if (CommandStr.equals("��_���")) // ���棬���ɱ���
		{
			if (!this.AlwaysOpenProject())
				return;
			this.m_GPSPoly.getGPSLine().Stop(lkGeoLayerType.enPolygon);
		}

		if (CommandStr.equals("����")) {
			if (!this.AlwaysOpenProject())
				return;
			if (this.m_Agent_Measure == null) {
				this.m_Agent_Measure = new v1_Agent_Measure();
				this.m_Agent_Measure.OnPrepare();
			}
			this.m_Agent_Measure.SetZHMode();
		}

		if (CommandStr.equals("��ͼ")) {
			if (!this.AlwaysOpenProject())
				return;
			Intent inet = new Intent(PubVar.m_DoEvent.m_Context, v1_Data_ClipScreen.class);
			PubVar.m_DoEvent.m_Context.startActivity(inet);
		}

		if (CommandStr.equals("У������")) {
			if (!this.AlwaysOpenProject())
				return;

			// �ڴ��ж��Ƿ�֧��ƽ��ת����Ҳ����Ŀ������ϵͳΪWGS-84����ϵ����֧��
			String CoorSystemName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem().GetName();
			if (CoorSystemName.equals("WGS-84����")) {
				lkmap.Tools.Tools.ShowMessageBox("��ǰĿ������ϵͳΪ��" + CoorSystemName + "������֧��ת��������");
				return;
			}
			v1_transformation_plane_fourparam vtm = new v1_transformation_plane_fourparam();
			vtm.ShowDialog();
		}

		if (CommandStr.equals("����")) {
			if (!this.AlwaysOpenProject())
				return;
			v1_navigate ng = PubVar.m_DoEvent.m_Navigate;
			ng.ShowDialog();
		}

		if (CommandStr.equals("���๤��")) {
			v1_moretools vtm = new v1_moretools();
			vtm.ShowDialog();
		}

		if (CommandStr.equals("�����")) {
			if (!this.AlwaysOpenProject())
				return;

			int PolyCount = lkmap.Tools.Tools.GetSelectObjectsCount(-1, 2);
			if (PolyCount != 1) {
				lkmap.Tools.Tools.ShowMessageBox("��ѡ��һ����״ʵ�壡");
				return;
			}
			// ���������
			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					v1_poly_analysis vpl = new v1_poly_analysis();
					vpl.ShowDialog();
				}
			});

			return;
		}

		if (CommandStr.equals("ɾ��ʵ��")) {
			if (!this.AlwaysOpenProject())
				return;

			// �ڴ��ж��Ƿ��е�ǰ���ڲɼ����ߡ��棬����еĻ�����ɾ������
			if (this.m_GPSPoly.getGPSLine().CheckIfStarting()) {
				lkmap.Tools.Tools.ShowYesNoMessage(this.m_Context, "�Ƿ�ɾ����ǰ���ڲɼ��棿", new ICallback() {

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						m_GPSPoly.getGPSLine().Cancel();
					}
				});
				return;
			}
			if (this.m_GPSLine.CheckIfStarting()) {
				lkmap.Tools.Tools.ShowYesNoMessage(this.m_Context, "�Ƿ�ɾ����ǰ���ڲɼ��ߣ�", new ICallback() {

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

		if (CommandStr.equals("����")) {
			if (!this.AlwaysOpenProject())
				return;

			// ���ж��Ƿ��е�ǰ���ڲɼ�������
			if (this.m_GPSPoly.getGPSLine().CheckIfStarting()) {
				if (this.m_GPSPoly.getGPSLine().Undo())
					return;
			}
			if (this.m_GPSLine.CheckIfStarting()) {
				if (this.m_GPSLine.Undo())
					return;
			}

			lkmap.Tools.Tools.OpenDialog("���ڽ��С����ˡ�����...", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (!IUnRedo.Undo()) {
						lkmap.Tools.Tools.ShowToast(m_Context, lkmap.Tools.Tools.ToLocale("�޿ɡ����ˡ�������¼��"));
					}
					DoCommand("SelectEndCallBack");
				}
			});

		}
		if (CommandStr.equals("����")) {
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
					lkmap.Tools.Tools.ShowToast(m_Context, lkmap.Tools.Tools.ToLocale("�޿ɡ�������������¼��"));
				}
			}

		}

		if (CommandStr.equals("Edit_Move_Move")) {
			if (!this.AlwaysOpenProject())
				return;

			// ��֤�Ƿ���ѡ�еĿ��ƶ�ʵ��
			int SelectObectCount = 0;
			for (GeoLayer pGeoLayer : PubVar.m_Map.getGeoLayers(lkGeoLayersType.enVectorEditingData).getList()) {
				if (pGeoLayer.getDataset().getDataSource().getEditing() && pGeoLayer.getSelSelection().getCount() > 0) {
					SelectObectCount += pGeoLayer.getSelSelection().getCount();
				}
			}
			if (SelectObectCount > 0) {
				PubVar.m_MapControl.setActiveTool(Tools.MoveObject);
			} else {
				lkmap.Tools.Tools.ShowMessageBox(this.m_Context, "���ڿɱ༭ͼ����ѡ����Ҫ�ƶ���ʵ�壡");
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

		if (CommandStr.equals("����GPS")) {
			this.DoCommand("�Զ�����GPS");
			if (m_GPSLocate.GPS_OpenClose) {
				// ����GPS���öԻ���
				GpsDetail gps = new GpsDetail();
				gps.ShowDialog();
				// Intent inet = new Intent(m_Context,v1_GPSSet.class);
				// //startActivityForResult(intent1,MYREQUEST_CODE);
				// //�������intent��ת
				// m_Context.startActivity(inet);
			}
			return;
		}

		if (CommandStr.equals("�Զ�����GPS")) {
			// if (!AuthorizeTools.IfAuthorizePass())return;
			if (!m_GPSLocate.GPS_OpenClose) {
				m_GPSLocate.OpenGPS();
			}
			return;
		}
		if (CommandStr.equals("�ر�GPS")) {
			if (m_GPSLocate.GPS_OpenClose) {
				m_GPSLocate.CloseGPS();
			}
			return;
		}

		if (CommandStr.equals("ϵͳ����")) {
			if (!this.AlwaysOpenProject())
				return;
			v1_SystemSet ss = new v1_SystemSet();
			ss.ShowDialog();
			return;
		}

		if (CommandStr.equals("ת����������")) {
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

		// if (CommandStr.equals("����"))
		// {
		// this.LoadMap();
		// this.m_MapControl.setActiveTool(Tools.FullScreenSize);
		// if
		// (!lkmap.Tools.Tools.RestoreViewExtend())this.m_MapControl.setActiveTool(Tools.FullScreen);
		// this.m_MapControl.setActiveTool(Tools.ZoomInOutPan);
		// //this.m_MapControl.setActiveTool(Tools.Select);
		// return;
		// }

		if (CommandStr.equals("��ѯ����")) {
			if (!this.AlwaysOpenProject())
				return;
			// �򿪹���ͼ�����
			lkmap.Tools.Tools.OpenDialog(new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					v1_Data_Query vdq = new v1_Data_Query();
					vdq.ShowDialog();
				}
			});
		}

		// if (CommandStr.equals("���ع���"))
		// {
		// this.v1_LoadMap();
		// }

		if (CommandStr.equals("��������")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.ZoomInOutPan);
			return;
		}

		if (CommandStr.equals("����")) {
			if (PubVar.m_DoEvent.AlwaysOpenProject()) {
				PubVar.m_MapControl.setActiveTool(Tools.Shutter);
				// PubVar.m_DoEvent.m_EditToolbar.ClearButtonSelect();
				// lkmap.Tools.Tools.SetToolsBarItemSelect(arg0, true);
			}

			return;
		}

		if (CommandStr.equals("�Ŵ�")) {
			this.m_MapControl.setActiveTool(Tools.ZoomIn);
			return;
		}
		if (CommandStr.equals("�����Ŵ�")) {

			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.ZoomOut);
			this.m_MapControl.SetZoomIn();

			// this.m_MapControl.getMap().Refresh();
			return;
		}
		if (CommandStr.equals("��С")) {
			this.m_MapControl.setActiveTool(Tools.ZoomOut);
			return;
		}
		if (CommandStr.equals("������С")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.ZoomIn);
			this.m_MapControl.SetZoomOut();
			return;
		}
		if (CommandStr.equals("����")) {
			this.m_MapControl.setActiveTool(Tools.Pan);
			return;
		}
		if (CommandStr.equals("ȫ��")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.FullScreen);
			return;
		}

		if (CommandStr.equals("ѡ��")) {
			if (!this.AlwaysOpenProject())
				return;
			this.m_MapControl.setActiveTool(Tools.Select);
			return;
		}
		if (CommandStr.equals("����1")) {

			Param GeoLayerName = new Param();
			Param SYSID = new Param();
			if (!lkmap.Tools.Tools.GetSelectOneObjectInfo(GeoLayerName, SYSID)) {
				lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools.ToLocale("��ѡ����Ҫ��ѯ��ʵ�壡"));
				return;
			}
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(GeoLayerName.getStringValue());
			if (pDataset.getDataSource().getEditing()) // �Ƿ�Ϊ���ڱ༭������Դ
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
		if (CommandStr.equals("����")) {
			if (!this.AlwaysOpenProject())
				return;
			
			
			
			// ��ȡ��Ҫ��ʾ��ʵ�壬����ж��ʵ�屻ѡ�У���������Ӧ��ʾ������ѡ��
			int SelectObjectsCount = lkmap.Tools.Tools.GetSelectObjectsCount();
			if (SelectObjectsCount == 1)
				this.DoCommand("����1");
			else if (SelectObjectsCount == 0)
				lkmap.Tools.Tools.ShowToast(this.m_Context, lkmap.Tools.Tools.ToLocale("��ѡ����Ҫ��ѯ��ʵ�壡"));// lkmap.Tools.Tools.ShowMessageBox("��ѡ����Ҫ��ѯ��ʵ�壡");
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
								lkmap.Tools.Tools.ShowMessageBox("��ѡͼ�㲻��һ������ͼ�㣬�����ѡ��");
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
						String tuBanDF = layer.GetDataFieldNameByFieldName("ͼ�ߺ�");
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
								lkmap.Tools.Tools.ShowMessageBox("��ѡͼ�ߺŲ�ͬ��ͬһͼ�ߺŲ��ܺϲ��ֵ���֤��");
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
							lkmap.Tools.Tools.ShowMessageBox("����ͼ��û��ͼ�ߺ��ֶΣ�");
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
//			if (SelectObjectsCount > 1) // ������ѡʵ��ĶԻ��򣬿������н���ѡ����Ҫ��ѯ��ʵ��
//			{
//				v1_Data_MultiSelect dm = new v1_Data_MultiSelect();
//				dm.ShowDialog();
//			}
			return;
		}
		
		

		if (CommandStr.equals("���ݵ���")) {
			if (!this.AlwaysOpenProject())
				return;
			AuthorizeTools_UserInfo UI = PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo();
			if (UI.SYS_UserType.equals("��ʱ�û�")) {
				lkmap.Tools.Tools.ShowMessageBox("��ʱ�û���֧�ֵ������ܣ�");
				return;
			}
			try {
				v1_data_export vps = new v1_data_export();
				vps.ShowDialog();
			} catch (Exception ex) {

			}

		}

		if (CommandStr.equals("�ɼ�ͳ��")) {
			if (!this.AlwaysOpenProject())
				return;
			v1_Data_Statistic ds = new v1_Data_Statistic();
			ds.ShowDialog();
			return;
		}

		if (CommandStr.equals("�����ֵ�")) {
			// v1_DataDictionary_Manage dm = new v1_DataDictionary_Manage();
			// dm.ShowDialog();

			// DataDict ddDataDict = new DataDict();
			// ddDataDict.ShowDialog();

			DataDicitonary DD = new DataDicitonary();
			DD.ShowDialog();
		}

		if (CommandStr.equals("��������")) {
			if (!this.AlwaysOpenProject())
				return;
			lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("���ڱ�������") + "...", new ICallback() {

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
								.ToLocale("����ͼ����������ʧ�ܣ�\r\n" + lkmap.Tools.Tools.JoinT("\r\n", ErrorList)));
					} else {
						IUnRedo.ClearDo();
						SaveProjectWorkspace();
						lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools.ToLocale("���ݱ���ɹ���"));
					}
				}
			});
		}
		
		if (CommandStr.equals("��������")) {
			
			DuChaSetting ducha = new DuChaSetting();
			ducha.ShowDialog();
		
		}

		if (CommandStr.equals("����_Υ��Υ��")) {
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
					String message = "�Ƿ���ѡС�����ó�Υ����";
					if (PubVar.allSelectWF) {
						message = "�Ƿ�ȡ����ѡС�����õ�Υ��״̬��";
					}
					lkmap.Tools.Tools.ShowYesNoMessage(m_Context, message, new ICallback() {

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							if (Str.equals("YES")) {
								Dataset pDataset = PubVar.m_Workspace.GetDatasetById(layerID);
								String filed = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerID)
										.GetDataFieldNameByFieldName("Υ��Υ��");
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
										// .ShowMessageBox("�ѽ���ѡ" +
										// pGeoLayer.getSelSelection().getCount()
										// + "��С�����ó�Υ��Υ��С�࣡");
									}
								}

							}

						}
					});

				}
			}

			if (!hasSelected) {
				lkmap.Tools.Tools.ShowMessageBox("����ɭ�ֶ���ͼ����֤ͼ��ѡ����Ҫ��ע��Υ��ͼ�ߣ�");
				return;
			}

			PubVar.m_Map.Refresh();
			// PubVar.m_Map.FastRefresh();
			PubVar.m_MapControl.invalidate();
		}

		if (CommandStr.equals("����_��鿨Ƭ")) {

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
							lkmap.Tools.Tools.ShowMessageBox("��ѡͼ�㲻��һ������ͼ�㣬�����ѡ��");
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
					lkmap.Tools.Tools.ShowYesNoMessage(m_Context, "�Ƿ���ѡ��" +selectIndexs.size()+ "��ͼ�ߺϲ���飿", new ICallback() {

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
				lkmap.Tools.Tools.ShowMessageBox("������ѡ��һ��������֤ͼ�ߣ�");
			}

			return;
		}

		if (CommandStr.equals("��������")) {
			if (m_GpsExporter == null) {
				m_GpsExporter = new GPSExporter();
			}
			m_GpsExporter.ShowDialog();
		}

		if (CommandStr.equals("̼�㵼��")) {
			if (!this.AlwaysOpenProject())
				return;
			lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("���ڵ���̼������") + "...", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					List<String> ErrorList = new ArrayList<String>();
					for (v1_Layer pLayer : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
						if (pLayer.GetLayerProjecType() != null && pLayer.GetLayerProjecType().contains("̼��")) {

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
									ErrorList.add(ydh + xbh + bzdh + ".CSV ����ʧ��");
								}
							}

							DR.Close();
						}
					}
					if (ErrorList.size() > 0) {
						lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools
								.ToLocale("����̼�����ݵ���ʧ�ܣ�\r\n" + lkmap.Tools.Tools.JoinT("\r\n", ErrorList)));
					} else {

						lkmap.Tools.Tools.ShowMessageBox(lkmap.Tools.Tools.ToLocale("̼�����ݵ����ɹ���"));
					}
				}
			});
		}

		if (CommandStr.equals("����ϵͳ")) {
			DoCommand("����ѡ��˵�");
			v1_About AB = new v1_About();
			AB.ShowDialog();

		}

		if (CommandStr.equals("�˳�ϵͳ")) {
			DoCommand("����ѡ��˵�");

			// �ڴ��ڼ������Ƿ��вɼ�����Ĳ���
			if (this.CheckHasDataInTask("���ڲɼ������У���ֹͣ�ɼ�������ٳ����˳���"))
				return;

			lkmap.Tools.Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, lkmap.Tools.Tools.ToLocale("�Ƿ�ȷ���˳�����ϵͳ��"),
					new ICallback() {
						@Override
						public void OnClick(String Str, Object ExtraStr) {
							DoCommand("��ȫ�˳�");
						}
					});
		}
		if (CommandStr.equals("��ȫ�˳�")) {
			lkmap.Tools.Tools.OpenDialog(lkmap.Tools.Tools.ToLocale("����׼���˳�") + "...", new ICallback() {

				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (AlwaysOpenProject(false)) {
						// DataSource pDataSource =
						// PubVar.m_Workspace.GetDataSourceByEditing();
						// if (pDataSource != null)
						// {
						// for (Dataset pDataset : pDataSource.getDatasets())
						// {
						// pDataset.Purge(); //�������ݼ�,�������ɾ����ʶ��ʵ��,����ͼ�㼶����
						// }
						// }
						SaveProjectWorkspace();
						new ProjectBackup().backupProjectFile();
					}

					AuthorizeTools_UserInfo UI = PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo();
					if (UI.SYS_UserType.equals("��ʱ�û�")) {
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

					// �����Ϣ֪ͨ��
					AndroidMap.ClearNotification(m_Context);
					ActivityManager am = (ActivityManager) m_Context.getSystemService(Context.ACTIVITY_SERVICE);
					String PackName = m_Context.getPackageName();
					am.killBackgroundProcesses(PackName); // API Level����Ϊ8����ʹ��

					System.exit(0);

				}
			});

			return;
		}
		if (CommandStr.equals("ϵͳ����")) {
			UpdateManager um = new UpdateManager(m_Context);
			um.checkUpdate();
		}
		
	}

	private void SaveProjectWorkspace() {
		// ��������Ԥ��״̬ͼ�������´μ��ع���ʱ�õ�
		lkmap.Tools.Tools.SaveBitmapTo(
				PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataPreviewImageName(), PubVar.m_Map.bp);

		// ��¼��ǰ�Ĺ�����ͼ��Χ���´�ֱ�ӵ�����Ӧ��Χ
		PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().SaveShowExtend(PubVar.m_Map.getExtend());
	}

	@SuppressLint("NewApi")
	public boolean exportOneTanhui(v1_Layer layer, int sysID, String fileName) {
		boolean isOkay = false;
		String sdCardDir = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/���ݵ���/̼��";

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

				String title = ", ,ÿľ��߱�,,";
				bcpFileWriter.write((new String(title.getBytes(), "utf-8")).getBytes());
				bcpFileWriter.write("\n".getBytes());
				bcpFileWriter.write(",,,,\n".getBytes());

				String firstLine = "���غ�:" + DR.GetString("F1") + "," + "С���:" + DR.GetString("F2") + "," + "," + "��׼�غţ�"
						+ DR.GetString("F3") + ",\n";
				bcpFileWriter.write((new String(firstLine.getBytes(), "utf-8")).getBytes());

				String titleLine = "���,����,�ؾ�(cm),�����\n";
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

				String lastLine = "ƽ���ߣ�" + String.valueOf(DR.GetDouble("F4")) + ",," + " ���������"
						+ String.valueOf(DR.GetDouble("F6")) + ",";
				bcpFileWriter.write((new String(lastLine.getBytes(), "utf-8")).getBytes());
				bcpFileWriter.flush();
				bcpFileWriter.close();
				lkmap.Tools.Tools.ShowMessageBox(m_Context, "���ݳɹ�������\r\n\r\nλ�ڣ���" + sdCardDir + fileName + "��");
			}

			DR.Close();
			isOkay = true;

		} catch (Exception ex) {
			lkmap.Tools.Tools.ShowMessageBox(ex.getLocalizedMessage());
		}

		return isOkay;
	}

	// �������������ϰ�ťͼƬ��״̬
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
	 * ����Ƿ��вɼ�����
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
