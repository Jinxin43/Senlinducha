package lkmap.UnRedo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DuChaDB;

import android.database.sqlite.SQLiteDatabase;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Part;
import lkmap.Cargeometry.Polygon;
import lkmap.Cargeometry.Polyline;
import lkmap.Dataset.Dataset;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Enum.lkReUndoCommand;
import lkmap.Enum.lkReUndoFlag;
import lkmap.Tools.Tools;

/* 主要思路：将所有需要恢复的操作简化成增加与删除两种基本操作，
 *           在删除的时候不是将实体真正删除，而是更新实体的模式标记，
 *           这样做的目的是保证图层级索引与实体列表中索引号一致，加快
 *           查询与显示的速度。
 *           
 * 历史信息格式：存储UnRedoParaStru结构
 * */
public class IUnRedo {
	// public static Map.OnCompleteEvent OnCompleteEvent;

	private static List<UnRedoParaStru> UndoList = new ArrayList<UnRedoParaStru>(); // Undo栈

	public static int getUndoListCount() {
		return UndoList.size();
	}

	private static List<UnRedoParaStru> RedoList = new ArrayList<UnRedoParaStru>(); // Redo栈

	public static int getRedoListCount() {
		return RedoList.size();
	}

	/// <summary>向Undo栈中加入恢复信息，具体历史信息格式见：IUnRedo类
	/// </summary>
	/// <param name="HistoryInfo"></param>
	public static void AddHistory(UnRedoParaStru UnRedoPara) {
		RedoList.clear();
		UndoList.add(0, UnRedoPara); // 新动作保证在栈项
	}

	// 调用全局Undo，Redo操作
	public static boolean Undo() {
		if (UndoList.size() == 0)
			return false;

		// 执行Undo操作
		if (ExceuteDo(UndoList.get(0))) {
			// 向Redolist中加入Undo动作，保证在栈顶
			RedoList.add(0, UndoList.get(0));
			UndoList.remove(0);
			PubVar.m_Map.Refresh();
//			PubVar.m_Map.FastRefresh();
		}
		return true;
	}

	public static boolean Redo() {
		if (RedoList.size() == 0)
			return false;
		// 执行Redo操作
		if (ExceuteDo(RedoList.get(0))) {
			// 向Undolist中入加Redo动作，保证在栈顶
			UndoList.add(0, RedoList.get(0));
			RedoList.remove(0);
			PubVar.m_Map.FastRefresh();
		}
		return true;
	}

	public static void ClearDo() {
		UndoList.clear();
		RedoList.clear();
	}

	// 处理Undo,Redo操作
	private static boolean ExceuteDo(UnRedoParaStru UnRedoPara) {
		if (UnRedoPara.Command == lkmap.Enum.lkReUndoCommand.enAddDeleteObject) {
			return AddDeleteObject(UnRedoPara);
		}

		if (UnRedoPara.Command == lkmap.Enum.lkReUndoCommand.enMoveObject) {
			return MoveObject(UnRedoPara);
		}

		if (UnRedoPara.Command == lkmap.Enum.lkReUndoCommand.enVertexMove) {
			return Vertex(UnRedoPara);
		}

		if (UnRedoPara.Command == lkmap.Enum.lkReUndoCommand.enVertexAddDel) {
			return Vertex(UnRedoPara);
		}

		return false;
	}

	// 节点操作
	private static boolean Vertex(UnRedoParaStru UnRedoPara) {
		// //此处这样做个倒序目的：保证节点在删除时是从列表尾端开始，避免出现异常
		// if (UnRedoPara.Command == lkReUndoCommand.enVertexAddDel)
		// {
		// Collections.reverse(UnRedoPara.ParaList);
		// }

		for (UnRedoDataItem dataItem : UnRedoPara.DataItemList) {
			for (IURDataItem urDataItem : dataItem.DataList) {
				IURDataItem_Vertex urVertex = (IURDataItem_Vertex) urDataItem;
				Dataset pDataset = PubVar.m_Workspace.GetDatasetById(urVertex.LayerId);
				if (pDataset == null)
					continue;

				// 实体ObjectIndex
				int ObjIndex = urVertex.ObjectId;

				// Part索引
				int PartIndex = urVertex.PartIndex;

				// 节点VertexIndex
				int VertexIndex = urVertex.VertexIndex;

				// 老坐标
				Coordinate CoorOld = urVertex.Coor1;

				// 新坐标
				Coordinate CoorNew = urVertex.Coor2;

				// 从数据库内取实体
				List<Geometry> pGeometryList = pDataset
						.QueryGeometryFromDB1(Arrays.asList(new String[] { ObjIndex + "" }));
				if (pGeometryList.size() != 1)
					return false;
				Geometry pGeometry = pGeometryList.get(0);
				if (PartIndex >= pGeometry.getPartCount())
					return false;
				Part part = pGeometry.GetPartAt(PartIndex);

				int VertexType = 0; // 1-首，2-至
				if (VertexIndex == 0)
					VertexType = 1;
				if (VertexIndex == part.getVertexList().size() - 1)
					VertexType = 2;

				// 移动节点
				if (UnRedoPara.Command == lkReUndoCommand.enVertexMove) {
					Coordinate VertexCoordinate = part.getVertexList().get(VertexIndex);
					if (dataItem.Type == lkReUndoFlag.enRedo) // 恢复成老坐标
					{
						VertexCoordinate.setX(CoorNew.getX());
						VertexCoordinate.setY(CoorNew.getY());
					}

					if (dataItem.Type == lkReUndoFlag.enUndo) {
						VertexCoordinate.setX(CoorOld.getX());
						VertexCoordinate.setY(CoorOld.getY());
					}

				}

				// 增加删除节点
				if (UnRedoPara.Command == lkReUndoCommand.enVertexAddDel) {

					if (dataItem.Type == lkReUndoFlag.enRedo) {
						part.getVertexList().add(VertexIndex, CoorOld.Clone());
					}
					if (dataItem.Type == lkReUndoFlag.enUndo) {
						part.getVertexList().remove(VertexIndex);
					}
				}

				// 处理起止点闭合的情况
				if (VertexType == 1) // 起点
				{
					Coordinate endPoint = part.getVertexList().get(part.getVertexList().size() - 1);
					endPoint.setX(part.getVertexList().get(0).getX());
					endPoint.setY(part.getVertexList().get(0).getY());
				}
				if (VertexType == 2) // 止点
				{
					Coordinate endPoint = part.getVertexList().get(part.getVertexList().size() - 1);
					part.getVertexList().get(0).setX(endPoint.getX());
					part.getVertexList().get(0).setY(endPoint.getY());
				}

				// 更新实体
				part.UpdateEnvelope();
				pGeometry.CalEnvelope();

				// 实时保存
				v1_CGpsDataObject co = new v1_CGpsDataObject();
				co.SetDataset(pDataset);
				co.SetSYS_ID(pGeometry.getSysId());
				double Len = 0, Area = 0;
				if (pDataset.getType() == lkGeoLayerType.enPolyline)
					Len = ((Polyline) pGeometry).getLength(true);
				if (pDataset.getType() == lkGeoLayerType.enPolygon)
					Area = ((Polygon) pGeometry).getArea(true);
				if (co.SaveGeoToDb(pGeometry, Len, Area) == ObjIndex) {
					// 更新图形
					Geometry bGeometry = pDataset.GetGeometry(ObjIndex);
					if (bGeometry != null) {
						bGeometry.SetNull();
						for (int i = 0; i < pGeometry.getPartCount(); i++) {
							bGeometry.AddPart(pGeometry.GetPartAt(i));
							bGeometry.GetPartAt(i).UpdateEnvelope();
						}
						bGeometry.CalEnvelope();

					}
				}
			}

			if (dataItem.Type == lkReUndoFlag.enUndo)
				dataItem.Type = lkReUndoFlag.enRedo;
			else
				dataItem.Type = lkReUndoFlag.enUndo;
		}
		return true;
	}

	/**
	 * 移动实体
	 */
	private static boolean MoveObject(UnRedoParaStru UnRedoPara) {
		for (UnRedoDataItem dataItem : UnRedoPara.DataItemList) {
			for (IURDataItem urDataItem : dataItem.DataList) {
				IURDataItem_Move urMove = (IURDataItem_Move) urDataItem;
				Dataset pDataset = PubVar.m_Workspace.GetDatasetById(urMove.LayerId);
				if (pDataset == null)
					continue;

				// 实体列表
				List<String> SYS_IDList = new ArrayList<String>();
				for (int id : urMove.ObjectIdList)
					SYS_IDList.add(id + "");

				// 偏移距离
				double OffsetX = urMove.OffsetX;
				double OffsetY = urMove.OffsetY;

				if (dataItem.Type == lkReUndoFlag.enUndo) {
					OffsetX = -OffsetX;
					OffsetY = -OffsetY;
					dataItem.Type = lkReUndoFlag.enRedo;
				} else
					dataItem.Type = lkReUndoFlag.enUndo;

				// 从数据库读取实体
				List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYS_IDList);
				for (Geometry pGeometry : pGeometryList) {
					pGeometry.UpdateCoordinate(OffsetX, OffsetY);
					pGeometry.CalEnvelope();
				}

				// 更新回数据库内
				for (Geometry pGeometry : pGeometryList) {
					v1_CGpsDataObject gpsObject = new v1_CGpsDataObject();
					gpsObject.SetDataset(pDataset);
					gpsObject.SetSYS_ID(pGeometry.getSysId());
					if (gpsObject.SaveGeoToDb(pGeometry, -1, -1) != pGeometry.getSysId()) // -1表示不更新Length，Area
					{
						return false;
					}
				}

				// 更新图形显示
				for (Geometry pGeometry : pGeometryList) {
					Geometry aGeometry = pDataset.GetGeometry(pGeometry.getSysId());
					if (aGeometry != null) {
						aGeometry.UpdateCoordinate(OffsetX, OffsetY);
						aGeometry.CalEnvelope();
					}
				}
			}
		}
		return true;
	}

	// 增删实体
	private static boolean AddDeleteObject(UnRedoParaStru UnRedoPara) {
		// enUndo增加实体，enRedo删除实体
		for (UnRedoDataItem urDataItem : UnRedoPara.DataItemList) {
			// 实体的状态
			lkmap.Enum.lkGeometryStatus LKSM = lkmap.Enum.lkGeometryStatus.enNormal;
			if (urDataItem.Type == lkReUndoFlag.enRedo) {
				LKSM = lkmap.Enum.lkGeometryStatus.enDelete;
				urDataItem.Type = lkReUndoFlag.enUndo;
			} else if (urDataItem.Type == lkReUndoFlag.enUndo) {
				LKSM = lkmap.Enum.lkGeometryStatus.enNormal;
				urDataItem.Type = lkReUndoFlag.enRedo;
			}
			for (IURDataItem data : urDataItem.DataList) {
				String LayerId = ((IURDataItem_DeleteAdd) data).LayerId;
				List<Integer> ObjectIndexList = ((IURDataItem_DeleteAdd) data).ObjectIdList;
				Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
				if (pDataset == null)
					continue;

				// 更新实体的属性状态，删除=1，正常=0
				int ObjStatus = 0;
				if (LKSM == lkGeometryStatus.enNormal)
					ObjStatus = 0;
				if (LKSM == lkGeometryStatus.enDelete)
					ObjStatus = 1;
				String UpdateSQL = "Update %1$s Set SYS_STATUS=%2$s where SYS_ID in (%3$s)";
				UpdateSQL = String.format(UpdateSQL, pDataset.getDataTableName(), ObjStatus,
						Tools.Join(",", ObjectIndexList));
				if (pDataset.getDataSource().ExcuteSQL(UpdateSQL)) {
					// 更新当前图层缓存内的实体状态
					for (int SYS_ID : ObjectIndexList) {
						Geometry pGeometry = pDataset.GetGeometry(SYS_ID);
						if (pGeometry != null)
							pGeometry.setStatus(LKSM);
					}
				}
			
				for(HashMap<String,String> hashMap:((IURDataItem_DeleteAdd) data).spilteIDs)
				{
					String oldId=hashMap.get("oneID");
					String splitedIds = hashMap.get("splitedIDs");
					SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
					String updateSql =  "update T_DuChaJCKP set a160 ='"+ oldId +"' where a159='" + LayerId + "' and a160 = '"+ splitedIds+"'";
					try
					{
						m_SQLiteDatabase.execSQL(updateSql);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			}
			
			
		}
		return true;
	}
}
