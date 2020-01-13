package com.dingtu.DTGIS.DataService;

import com.dingtu.DTGIS.Upload.HttpLayermodel;

import android.database.sqlite.SQLiteDatabase;
import dingtu.ZRoadMap.PubVar;
import lkmap.Dataset.SQLiteDataReader;

public class LayerDB {
	
	String dbPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/project.dbx";
	SQLiteDatabase m_SQLiteDatabase=null;
	String mTableName = "T_Layer";
	
	public LayerDB()
	{
		m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbPath,null);
	}

	public HttpLayermodel getHttpLayerModel(String layerId)
	{
		HttpLayermodel layerModel = null;
		String sql = "select * from "+mTableName+" where LayerId='"+layerId+"'";
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if(reader.Read())
		{
			layerModel = new HttpLayermodel();			
//			layermodel.setFromDeviceId(reader.GetString(""));
			layerModel.setLayerId(layerId);
			layerModel.setSortId(reader.GetInt32("SortId"));
			layerModel.setName(reader.getUnNullString("Name"));
			layerModel.setType(reader.getUnNullString("Type"));
			layerModel.setVisible(reader.getUnNullString("Visible"));
			layerModel.setTransparente(reader.getUnNullString("Transparent"));
			layerModel.setIfLabel(reader.getUnNullString("IfLabel"));
			layerModel.setLabelFont(reader.getUnNullString("LabelFont"));
			layerModel.setLabelField(reader.getUnNullString("LabelField"));
			layerModel.setFieldList(reader.getUnNullString("FieldList"));
			layerModel.setLabelScaleMin(reader.GetDouble("LabelScaleMin"));
			layerModel.setLabelScaleMax(reader.GetDouble("LabelScaleMax"));
			layerModel.setMinX(reader.GetDouble("MinX"));
			layerModel.setMaxX(reader.GetDouble("MaxX"));
			layerModel.setMinY(reader.GetDouble("MinY"));
			layerModel.setMaxY(reader.GetDouble("MaxY"));
			layerModel.setVisibleScaleMax(reader.GetDouble("VisibleScaleMax"));
			layerModel.setVisibleScaleMin(reader.GetDouble("VisibleScaleMin"));
			layerModel.setSelectable(reader.getUnNullString("Selectable"));
			layerModel.setSnapable(reader.getUnNullString("Snapable"));
			layerModel.setEditable(reader.getUnNullString("Editable"));
			layerModel.setRenderType(reader.getUnNullString("RenderType"));
			layerModel.setSimpleRender(reader.getUnNullString("SimpleRender"));
			layerModel.setUniqueSymbolList(reader.getUnNullString("UniqueSymbolList"));
			layerModel.setUniqueDefaultSymbol(reader.getUnNullString("UniqueDefaultSymbol"));
			layerModel.setUniqueValueField(reader.getUnNullString("UniqueValueField"));
			layerModel.setUniqueValueList(reader.getUnNullString("UniqueValueList"));
			layerModel.setF1(reader.getUnNullString("F1"));
			layerModel.setF2(reader.getUnNullString("F2"));
			layerModel.setF3(reader.getUnNullString("F3"));
			layerModel.setF4(reader.getUnNullString("F4"));
			layerModel.setF5(reader.getUnNullString("F5"));
			layerModel.setF6(reader.getUnNullString("F6"));
			layerModel.setF7(reader.getUnNullString("F7"));
			layerModel.setF8(reader.getUnNullString("F8"));
			layerModel.setF9(reader.getUnNullString("F9"));
			layerModel.setF10(reader.getUnNullString("F10"));
		}
		return layerModel;
	}
}
