package lkmap.ZRoadMap.Project;

import java.util.ArrayList;
import java.util.List;

import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.ASQLiteDatabase;
import lkmap.Dataset.DataSource;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkDatasetSourceType;
import lkmap.Enum.lkRenderType;
import lkmap.Tools.Tools;

public class v1_LayerExplorer {
	private v1_ProjectDB m_ProjectDB = null;

	/**
	 * �󶨹��̲�����
	 * 
	 * @param projectDB
	 */
	public void SetBindProjectDB(v1_ProjectDB projectDB) {
		this.m_ProjectDB = projectDB;
	}

	// ���̶�Ӧ��ͼ���б�
	private List<v1_Layer> m_LayerList = new ArrayList<v1_Layer>();

	public List<v1_Layer> GetLayerList() {
		return this.m_LayerList;
	}

	/**
	 * ����ͼ��ID����ͼ��
	 * 
	 * @param LayerID
	 * @return
	 */
	public v1_Layer GetLayerByID(String LayerID) {
		for (v1_Layer vLayer : this.m_LayerList) {
			if (vLayer.GetLayerID().equals(LayerID))
				return vLayer;
		}
		return null;
	}

	/**
	 * ���ָ����ͼ��ID�Ƿ����
	 * 
	 * @param LayerID
	 * @return
	 */
	public boolean ExistLayerByID(String LayerID) {
		if (this.GetLayerByID(LayerID) == null)
			return false;
		else
			return true;
	}

	/**
	 * ���ͼ�����Ч��
	 * 
	 * @param LayerID
	 * @return
	 */
	public boolean CheckLayerValid(String LayerID) {
		if (this.GetLayerByID(LayerID) == null) {
			if (!PubVar.VectorBGEditable) {
				// Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context,
				// Tools.ToLocale("��ѡ����Ч������ͼ�㣡"));
			}
			return false;

		} else
			return true;
	}

	/**
	 * ����ͼ���б�
	 * 
	 * @return
	 */
	public List<v1_Layer> CopyLayerList() {
		List<v1_Layer> _NewLayerList = new ArrayList<v1_Layer>();
		for (v1_Layer lyr : this.m_LayerList) {
			_NewLayerList.add(lyr.Clone());
		}
		return _NewLayerList;
	}

	/**
	 * �򿪲ɼ�����Դ
	 * 
	 * @return
	 */
	public boolean OpenDataSource(String dataFileName) {
		DataSource pDataSource = new DataSource(dataFileName);
		pDataSource.setEditing(true);
		// if (pDataSource.OpenDatasets())
		PubVar.m_Workspace.GetDataSourceList().add(pDataSource);

		// �������ݼ�Dataset
		for (v1_Layer vLayer : this.GetLayerList()) {
			Dataset pDataset = new Dataset(pDataSource);
			pDataset.setSourceType(lkDatasetSourceType.enEditingData);
			pDataset.setId(vLayer.GetLayerID());
			pDataset.setType(vLayer.GetLayerType());
			pDataset.GetMapCellIndex().setEnvelope(PubVar.m_Map.getFullExtend());
			pDataSource.getDatasets().add(pDataset);
			pDataset.setPorjectType(vLayer.GetLayerProjecType());

			pDataset.Purge();
		}

		// ��Ⱦͼ�� ��Ҳ���Ǵ���GeoLayer
		for (v1_Layer vLayer : this.GetLayerList()) {
			PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(vLayer);
		}

		return true;

	}

	/**
	 * ���ش˹��̶�Ӧ��ͼ����Ϣ
	 */
	public void LoadLayer() {
		boolean haiTuigeng = false;
		this.m_LayerList.clear();
		// ��ȡ�˹��̶�Ӧ��ͼ����Ϣ
		SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query("select * from T_Layer order by SortID");
		if (DR == null)
			return;
		while (DR.Read()) {
			v1_Layer lyr = new v1_Layer();
			lyr.SetLayerAliasName(DR.GetString("Name")); // ͼ�����ƣ�����
			lyr.SetLayerID(DR.GetString("LayerId")); // ͼ��Id
			lyr.SetLayerTypeName(DR.GetString("Type")); // ͼ�����ͣ��㣬�ߣ��棩
			lyr.SetVisible(Boolean.parseBoolean(DR.GetString("Visible"))); // �ɼ���
			lyr.SetTransparent(Integer.parseInt(DR.GetString("Transparent"))); // ͸���ȣ���㣩
			lyr.SetVisibleScaleMin(Double.parseDouble(DR.GetString("VisibleScaleMin"))); // ��С�ɼ�����
			lyr.SetVisibleScaleMax(Double.parseDouble(DR.GetString("VisibleScaleMax"))); // ���ɼ�����

			lyr.SetFieldList(DR.GetString("FieldList")); // �ֶ��б���ʽ������-�����ֶ�����
			lyr.SetIfLabel(Boolean.parseBoolean(DR.GetString("IfLabel"))); // �Ƿ��ע
			lyr.SetLabelDataField(DR.GetString("LabelField")); // ��ע�ֶ�
			lyr.SetLabelFont(DR.GetString("LabelFont")); // ��ע��ʽ

			lyr.SetMinX(Double.parseDouble(DR.GetString("MinX"))); // ��Ӿ���
			lyr.SetMinY(Double.parseDouble(DR.GetString("MinY")));
			lyr.SetMaxX(Double.parseDouble(DR.GetString("MaxX")));
			lyr.SetMaxY(Double.parseDouble(DR.GetString("MaxY")));

			lyr.SetSelectable(Boolean.parseBoolean(DR.GetString("Selectable"))); // �Ƿ��ѡ��
			lyr.SetEditable(Boolean.parseBoolean(DR.GetString("Editable"))); // �Ƿ�ɱ༭
			lyr.SetSnapable(Boolean.parseBoolean(DR.GetString("Snapable"))); // �Ƿ�ɲ�׽

			// ��ҵ��������
			lyr.SetLayerProjectType(DR.GetString("F1"));
			lyr.setCity(DR.GetString("F2"));
			lyr.setCounty(DR.GetString("F3"));
			lyr.setYear(DR.GetString("F4"));

			if (lyr.GetLayerProjecType().contains("�˸�����")) {
				haiTuigeng = true;
			}

			// ��Ƭ���ͼ��Ҫ��ȡ��������Ƭ�����·�ͼ��
			if (lyr.GetLayerProjecType().contentEquals(ForestryLayerType.WeipianJianchaLayer)) {
				lyr.setWeipianDataLayer(DR.GetString("F5"));
			}

			try {
				lyr.SetShowWaterMark(Boolean.parseBoolean(DR.GetString("F6")));
			} catch (Exception ex) {

			}

			lyr.setWaterMarkDataFields(DR.GetString("F7"));

			String renderType = DR.GetString("RenderType"); // ��Ⱦ����1-��ֵ��2-��ֵ
			if (renderType.equals("2")) // ��ֵ����
			{
				lyr.SetRenderType(lkRenderType.enUniqueValue);
				lyr.GetUniqueSymbolInfoList().put("UniqueValueField",
						Tools.JSONStrToList(DR.GetString("UniqueValueField"))); // Ψһֵ�ֶ�
				lyr.GetUniqueSymbolInfoList().put("UniqueValueList",
						Tools.JSONStrToList(DR.GetString("UniqueValueList"))); // Ψһֵ�б�
				lyr.GetUniqueSymbolInfoList().put("UniqueSymbolList",
						Tools.JSONStrToList(DR.GetString("UniqueSymbolList"))); // Ψһֵ�����б�
				lyr.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", DR.GetString("UniqueDefaultSymbol")); // Ψһֵȱʡ����
			} else
				lyr.SetSimpleSymbol(DR.GetString("SimpleRender"));
			this.m_LayerList.add(lyr);
		}
		DR.Close();

		if (haiTuigeng) {
			String yinzistr = "�Ƿ���,Ȩ��,����,����,��λ,�¶�,����,��������,�������,��������״��,������ʴ�̶�,ֲ������,���ַ�ʽ,��������,����ʱ��,���ط�ʽ,��ľ���,��������,��������";
			String[] yinziList = yinzistr.split(",");
			for (String yinzi : yinziList) {
				PubVar.m_DoEvent.m_DictDataDB.getEnumList("�˸�����", yinzi);
			}
		}

	}

	public boolean deleteAllEditingData() {
		ASQLiteDatabase dataDB = new ASQLiteDatabase();
		dataDB.setDatabaseName(this.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName());
		boolean result = true;
		for (v1_Layer layer : this.m_LayerList) {
			String delData = "delete from " + layer.GetLayerID() + "_D";
			if (dataDB.ExcuteSQL(delData)) {
				String delIndex = "delete from " + layer.GetLayerID() + "_I";
				if (!dataDB.ExcuteSQL(delIndex)) {
					result = false;
				}
			} else {
				result = false;
			}

		}
		dataDB.Close();
		return result;
	}

	/**
	 * ����ͼ��
	 * 
	 * @return
	 */
	public boolean SaveLayerFormLayerList(List<v1_Layer> newLayerList) {
		this.m_LayerList.clear();
		for (v1_Layer lyr : newLayerList) {
			this.m_LayerList.add(lyr);
		}
		return true;
	}

}
