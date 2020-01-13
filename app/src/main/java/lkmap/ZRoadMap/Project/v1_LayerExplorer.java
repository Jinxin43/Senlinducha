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
	 * 绑定工程操作类
	 * 
	 * @param projectDB
	 */
	public void SetBindProjectDB(v1_ProjectDB projectDB) {
		this.m_ProjectDB = projectDB;
	}

	// 工程对应的图层列表
	private List<v1_Layer> m_LayerList = new ArrayList<v1_Layer>();

	public List<v1_Layer> GetLayerList() {
		return this.m_LayerList;
	}

	/**
	 * 根据图层ID返回图层
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
	 * 检查指定的图层ID是否存在
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
	 * 检查图层的有效性
	 * 
	 * @param LayerID
	 * @return
	 */
	public boolean CheckLayerValid(String LayerID) {
		if (this.GetLayerByID(LayerID) == null) {
			if (!PubVar.VectorBGEditable) {
				// Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context,
				// Tools.ToLocale("请选择有效的数据图层！"));
			}
			return false;

		} else
			return true;
	}

	/**
	 * 拷贝图层列表
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
	 * 打开采集数据源
	 * 
	 * @return
	 */
	public boolean OpenDataSource(String dataFileName) {
		DataSource pDataSource = new DataSource(dataFileName);
		pDataSource.setEditing(true);
		// if (pDataSource.OpenDatasets())
		PubVar.m_Workspace.GetDataSourceList().add(pDataSource);

		// 创建数据集Dataset
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

		// 渲染图层 ，也就是创建GeoLayer
		for (v1_Layer vLayer : this.GetLayerList()) {
			PubVar.m_DoEvent.m_ProjectDB.GetLayerRenderExplorer().RenderLayerForAdd(vLayer);
		}

		return true;

	}

	/**
	 * 加载此工程对应的图层信息
	 */
	public void LoadLayer() {
		boolean haiTuigeng = false;
		this.m_LayerList.clear();
		// 读取此工程对应的图层信息
		SQLiteDataReader DR = this.m_ProjectDB.GetSQLiteDatabase().Query("select * from T_Layer order by SortID");
		if (DR == null)
			return;
		while (DR.Read()) {
			v1_Layer lyr = new v1_Layer();
			lyr.SetLayerAliasName(DR.GetString("Name")); // 图层名称，汉字
			lyr.SetLayerID(DR.GetString("LayerId")); // 图层Id
			lyr.SetLayerTypeName(DR.GetString("Type")); // 图层类型（点，线，面）
			lyr.SetVisible(Boolean.parseBoolean(DR.GetString("Visible"))); // 可见性
			lyr.SetTransparent(Integer.parseInt(DR.GetString("Transparent"))); // 透明度（面层）
			lyr.SetVisibleScaleMin(Double.parseDouble(DR.GetString("VisibleScaleMin"))); // 最小可见比例
			lyr.SetVisibleScaleMax(Double.parseDouble(DR.GetString("VisibleScaleMax"))); // 最大可见比例

			lyr.SetFieldList(DR.GetString("FieldList")); // 字段列表，格式：名称-数据字段名称
			lyr.SetIfLabel(Boolean.parseBoolean(DR.GetString("IfLabel"))); // 是否标注
			lyr.SetLabelDataField(DR.GetString("LabelField")); // 标注字段
			lyr.SetLabelFont(DR.GetString("LabelFont")); // 标注样式

			lyr.SetMinX(Double.parseDouble(DR.GetString("MinX"))); // 外接矩形
			lyr.SetMinY(Double.parseDouble(DR.GetString("MinY")));
			lyr.SetMaxX(Double.parseDouble(DR.GetString("MaxX")));
			lyr.SetMaxY(Double.parseDouble(DR.GetString("MaxY")));

			lyr.SetSelectable(Boolean.parseBoolean(DR.GetString("Selectable"))); // 是否可选择
			lyr.SetEditable(Boolean.parseBoolean(DR.GetString("Editable"))); // 是否可编辑
			lyr.SetSnapable(Boolean.parseBoolean(DR.GetString("Snapable"))); // 是否可捕捉

			// 林业工程类型
			lyr.SetLayerProjectType(DR.GetString("F1"));
			lyr.setCity(DR.GetString("F2"));
			lyr.setCounty(DR.GetString("F3"));
			lyr.setYear(DR.GetString("F4"));

			if (lyr.GetLayerProjecType().contains("退耕还林")) {
				haiTuigeng = true;
			}

			// 卫片检查图层要获取关联的卫片数据下发图层
			if (lyr.GetLayerProjecType().contentEquals(ForestryLayerType.WeipianJianchaLayer)) {
				lyr.setWeipianDataLayer(DR.GetString("F5"));
			}

			try {
				lyr.SetShowWaterMark(Boolean.parseBoolean(DR.GetString("F6")));
			} catch (Exception ex) {

			}

			lyr.setWaterMarkDataFields(DR.GetString("F7"));

			String renderType = DR.GetString("RenderType"); // 渲染类型1-单值，2-多值
			if (renderType.equals("2")) // 多值符号
			{
				lyr.SetRenderType(lkRenderType.enUniqueValue);
				lyr.GetUniqueSymbolInfoList().put("UniqueValueField",
						Tools.JSONStrToList(DR.GetString("UniqueValueField"))); // 唯一值字段
				lyr.GetUniqueSymbolInfoList().put("UniqueValueList",
						Tools.JSONStrToList(DR.GetString("UniqueValueList"))); // 唯一值列表
				lyr.GetUniqueSymbolInfoList().put("UniqueSymbolList",
						Tools.JSONStrToList(DR.GetString("UniqueSymbolList"))); // 唯一值符号列表
				lyr.GetUniqueSymbolInfoList().put("UniqueDefaultSymbol", DR.GetString("UniqueDefaultSymbol")); // 唯一值缺省符号
			} else
				lyr.SetSimpleSymbol(DR.GetString("SimpleRender"));
			this.m_LayerList.add(lyr);
		}
		DR.Close();

		if (haiTuigeng) {
			String yinzistr = "是否变更,权属,地类,坡向,坡位,坡度,坡向,土壤名称,土壤厚度,土壤肥力状况,土壤侵蚀程度,植被类型,造林方式,立地类型,抚育时间,整地方式,苗木规格,造林林种,造林树种";
			String[] yinziList = yinzistr.split(",");
			for (String yinzi : yinziList) {
				PubVar.m_DoEvent.m_DictDataDB.getEnumList("退耕还林", yinzi);
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
	 * 保存图层
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
