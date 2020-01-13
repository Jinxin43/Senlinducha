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

/* ��Ҫ˼·����������Ҫ�ָ��Ĳ����򻯳�������ɾ�����ֻ���������
 *           ��ɾ����ʱ���ǽ�ʵ������ɾ�������Ǹ���ʵ���ģʽ��ǣ�
 *           ��������Ŀ���Ǳ�֤ͼ�㼶������ʵ���б���������һ�£��ӿ�
 *           ��ѯ����ʾ���ٶȡ�
 *           
 * ��ʷ��Ϣ��ʽ���洢UnRedoParaStru�ṹ
 * */
public class IUnRedo {
	// public static Map.OnCompleteEvent OnCompleteEvent;

	private static List<UnRedoParaStru> UndoList = new ArrayList<UnRedoParaStru>(); // Undoջ

	public static int getUndoListCount() {
		return UndoList.size();
	}

	private static List<UnRedoParaStru> RedoList = new ArrayList<UnRedoParaStru>(); // Redoջ

	public static int getRedoListCount() {
		return RedoList.size();
	}

	/// <summary>��Undoջ�м���ָ���Ϣ��������ʷ��Ϣ��ʽ����IUnRedo��
	/// </summary>
	/// <param name="HistoryInfo"></param>
	public static void AddHistory(UnRedoParaStru UnRedoPara) {
		RedoList.clear();
		UndoList.add(0, UnRedoPara); // �¶�����֤��ջ��
	}

	// ����ȫ��Undo��Redo����
	public static boolean Undo() {
		if (UndoList.size() == 0)
			return false;

		// ִ��Undo����
		if (ExceuteDo(UndoList.get(0))) {
			// ��Redolist�м���Undo��������֤��ջ��
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
		// ִ��Redo����
		if (ExceuteDo(RedoList.get(0))) {
			// ��Undolist�����Redo��������֤��ջ��
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

	// ����Undo,Redo����
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

	// �ڵ����
	private static boolean Vertex(UnRedoParaStru UnRedoPara) {
		// //�˴�������������Ŀ�ģ���֤�ڵ���ɾ��ʱ�Ǵ��б�β�˿�ʼ����������쳣
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

				// ʵ��ObjectIndex
				int ObjIndex = urVertex.ObjectId;

				// Part����
				int PartIndex = urVertex.PartIndex;

				// �ڵ�VertexIndex
				int VertexIndex = urVertex.VertexIndex;

				// ������
				Coordinate CoorOld = urVertex.Coor1;

				// ������
				Coordinate CoorNew = urVertex.Coor2;

				// �����ݿ���ȡʵ��
				List<Geometry> pGeometryList = pDataset
						.QueryGeometryFromDB1(Arrays.asList(new String[] { ObjIndex + "" }));
				if (pGeometryList.size() != 1)
					return false;
				Geometry pGeometry = pGeometryList.get(0);
				if (PartIndex >= pGeometry.getPartCount())
					return false;
				Part part = pGeometry.GetPartAt(PartIndex);

				int VertexType = 0; // 1-�ף�2-��
				if (VertexIndex == 0)
					VertexType = 1;
				if (VertexIndex == part.getVertexList().size() - 1)
					VertexType = 2;

				// �ƶ��ڵ�
				if (UnRedoPara.Command == lkReUndoCommand.enVertexMove) {
					Coordinate VertexCoordinate = part.getVertexList().get(VertexIndex);
					if (dataItem.Type == lkReUndoFlag.enRedo) // �ָ���������
					{
						VertexCoordinate.setX(CoorNew.getX());
						VertexCoordinate.setY(CoorNew.getY());
					}

					if (dataItem.Type == lkReUndoFlag.enUndo) {
						VertexCoordinate.setX(CoorOld.getX());
						VertexCoordinate.setY(CoorOld.getY());
					}

				}

				// ����ɾ���ڵ�
				if (UnRedoPara.Command == lkReUndoCommand.enVertexAddDel) {

					if (dataItem.Type == lkReUndoFlag.enRedo) {
						part.getVertexList().add(VertexIndex, CoorOld.Clone());
					}
					if (dataItem.Type == lkReUndoFlag.enUndo) {
						part.getVertexList().remove(VertexIndex);
					}
				}

				// ������ֹ��պϵ����
				if (VertexType == 1) // ���
				{
					Coordinate endPoint = part.getVertexList().get(part.getVertexList().size() - 1);
					endPoint.setX(part.getVertexList().get(0).getX());
					endPoint.setY(part.getVertexList().get(0).getY());
				}
				if (VertexType == 2) // ֹ��
				{
					Coordinate endPoint = part.getVertexList().get(part.getVertexList().size() - 1);
					part.getVertexList().get(0).setX(endPoint.getX());
					part.getVertexList().get(0).setY(endPoint.getY());
				}

				// ����ʵ��
				part.UpdateEnvelope();
				pGeometry.CalEnvelope();

				// ʵʱ����
				v1_CGpsDataObject co = new v1_CGpsDataObject();
				co.SetDataset(pDataset);
				co.SetSYS_ID(pGeometry.getSysId());
				double Len = 0, Area = 0;
				if (pDataset.getType() == lkGeoLayerType.enPolyline)
					Len = ((Polyline) pGeometry).getLength(true);
				if (pDataset.getType() == lkGeoLayerType.enPolygon)
					Area = ((Polygon) pGeometry).getArea(true);
				if (co.SaveGeoToDb(pGeometry, Len, Area) == ObjIndex) {
					// ����ͼ��
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
	 * �ƶ�ʵ��
	 */
	private static boolean MoveObject(UnRedoParaStru UnRedoPara) {
		for (UnRedoDataItem dataItem : UnRedoPara.DataItemList) {
			for (IURDataItem urDataItem : dataItem.DataList) {
				IURDataItem_Move urMove = (IURDataItem_Move) urDataItem;
				Dataset pDataset = PubVar.m_Workspace.GetDatasetById(urMove.LayerId);
				if (pDataset == null)
					continue;

				// ʵ���б�
				List<String> SYS_IDList = new ArrayList<String>();
				for (int id : urMove.ObjectIdList)
					SYS_IDList.add(id + "");

				// ƫ�ƾ���
				double OffsetX = urMove.OffsetX;
				double OffsetY = urMove.OffsetY;

				if (dataItem.Type == lkReUndoFlag.enUndo) {
					OffsetX = -OffsetX;
					OffsetY = -OffsetY;
					dataItem.Type = lkReUndoFlag.enRedo;
				} else
					dataItem.Type = lkReUndoFlag.enUndo;

				// �����ݿ��ȡʵ��
				List<Geometry> pGeometryList = pDataset.QueryGeometryFromDB1(SYS_IDList);
				for (Geometry pGeometry : pGeometryList) {
					pGeometry.UpdateCoordinate(OffsetX, OffsetY);
					pGeometry.CalEnvelope();
				}

				// ���»����ݿ���
				for (Geometry pGeometry : pGeometryList) {
					v1_CGpsDataObject gpsObject = new v1_CGpsDataObject();
					gpsObject.SetDataset(pDataset);
					gpsObject.SetSYS_ID(pGeometry.getSysId());
					if (gpsObject.SaveGeoToDb(pGeometry, -1, -1) != pGeometry.getSysId()) // -1��ʾ������Length��Area
					{
						return false;
					}
				}

				// ����ͼ����ʾ
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

	// ��ɾʵ��
	private static boolean AddDeleteObject(UnRedoParaStru UnRedoPara) {
		// enUndo����ʵ�壬enRedoɾ��ʵ��
		for (UnRedoDataItem urDataItem : UnRedoPara.DataItemList) {
			// ʵ���״̬
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

				// ����ʵ�������״̬��ɾ��=1������=0
				int ObjStatus = 0;
				if (LKSM == lkGeometryStatus.enNormal)
					ObjStatus = 0;
				if (LKSM == lkGeometryStatus.enDelete)
					ObjStatus = 1;
				String UpdateSQL = "Update %1$s Set SYS_STATUS=%2$s where SYS_ID in (%3$s)";
				UpdateSQL = String.format(UpdateSQL, pDataset.getDataTableName(), ObjStatus,
						Tools.Join(",", ObjectIndexList));
				if (pDataset.getDataSource().ExcuteSQL(UpdateSQL)) {
					// ���µ�ǰͼ�㻺���ڵ�ʵ��״̬
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
