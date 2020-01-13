package com.dingtu.SLDuCha;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.v1_CGpsDataObject;
import lkmap.Enum.lkRenderType;
import lkmap.Render.UniqueValueRender;
import lkmap.Tools.Tools;

public class DuChaDataObject extends v1_CGpsDataObject {

	private List<Integer> mRelatedIds;

	public DuChaDataObject() {

	}

	public void addRelatedIds(List<Integer> relatedIds) {
		mRelatedIds = relatedIds;
	}

	public boolean SaveFeatureToDb() {
		if (this.SYS_ID == -1) {
			return this.SaveNewAdd(this.GetFeatureList());
		} else {
			return this.UpdateFeatrue(this.GetFeatureList());
		}
	}

	private boolean SaveNewAdd(List<String> FeatureList) {
		try {
			List<String> FieldNameList = new ArrayList<String>();
			List<String> FieldValueList = new ArrayList<String>();
			for (String FV : FeatureList) {
				String fv[] = FV.split("=");
				String FieldName = fv[0];
				String FieldValue = (fv.length != 2 ? "" : fv[1]);
				FieldNameList.add(FieldName);
				FieldValueList.add(FieldValue);
			}

			String SQL = "insert into %1$s (%2$s) values (%3$s)";
			SQL = String.format(SQL, this.m_Dataset.getDataTableName(), Tools.JoinT(",", FieldNameList),
					Tools.JoinT(",", FieldValueList));
			Log.d("", "正在保存数据[" + SQL + "]");
			if (this.m_Dataset.getDataSource().ExcuteSQL(SQL))
				return true;

		} catch (Error e) {
			// Tools.ShowMessageBox(PubVar.m_DoEvent.m_GPSPoint.SubWindow,"["+this.SYS_TYPE
			// + "] 保存失败！\r\n原因："+e.getMessage());
			return false;
		}
		return false;
	}

	// 更新采集实体,FeatureList格式形式：F1='XXX'
	private boolean UpdateFeatrue(List<String> FeatureList) {
		try {
			// 更新实体的属性信息
			// String SQL = "update %1$s set
			// SYS_LABEL='%2$s',SYS_PHOTO='%3$s',SYS_TYPE='%6$s',%4$s where
			// SYS_ID=%5$s";
			String SQL = "update %1$s set SYS_LABEL='%2$s',%3$s where SYS_ID in ( %4$s )";
			String idString = SYS_ID + "";
			if (mRelatedIds != null && mRelatedIds.size() != 0) {
				for (Integer id : mRelatedIds) {
					idString += "," + id;
				}
			}

			SQL = String.format(SQL, this.m_Dataset.getDataTableName(), "", 
					Tools.JoinT(",", FeatureList), idString);

			String SQLPhoto = "update %1$s set SYS_PHOTO='%2$s' where SYS_ID = %3$s ";
			SQLPhoto = String.format(SQLPhoto,this.m_Dataset.getDataTableName(),this.SYS_PHOTO,SYS_ID);
			
			if (this.m_Dataset.getDataSource().ExcuteSQL(SQL)&& this.m_Dataset.getDataSource().ExcuteSQL(SQLPhoto)) {
//			if (this.m_Dataset.getDataSource().ExcuteSQL(SQL)) {
				lkmap.Cargeometry.Geometry pGeometry = this.m_Dataset.GetGeometry(this.SYS_ID);
				if (pGeometry != null) {
					// 更新标注信息
					if (this.m_Dataset.getBindGeoLayer().getRender().getIfLabel()) {
						String[] labelFieldList = this.m_Dataset.getBindGeoLayer().getRender().getLabelField()
								.split(",");
						if (labelFieldList.length != 0) {
							String ValueStr = "";
							for (String feature : FeatureList) {
								String[] fInfo = feature.split("=");
								for (String UVF : labelFieldList) {
									if (fInfo[0].equals(UVF))
										ValueStr += fInfo[1].replace("'", "") + ",";
								}
							}
							if (ValueStr.length() > 0)
								pGeometry.setTag(ValueStr.substring(0, ValueStr.length() - 1));
						}
					}

					// 更新唯一渲染的UniqueValue
					if (this.m_Dataset.getBindGeoLayer().getRender().getType() == lkRenderType.enUniqueValue) {
						List<String> UVFList = ((UniqueValueRender) this.m_Dataset.getBindGeoLayer().getRender())
								.GetUniqueValueFieldList();
						String ValueStr = "";
						for (String feature : FeatureList) {
							String[] fInfo = feature.split("=");
							for (String UVF : UVFList) {
								if (fInfo[0].equals(UVF))
									ValueStr += fInfo[1].replace("'", "") + ",";
							}
						}
						if (ValueStr.length() > 0)
							pGeometry.setTagForUniqueSymbol(ValueStr.substring(0, ValueStr.length() - 1));
					}
					this.m_Dataset.getBindGeoLayer().getRender().UpdateSymbol(pGeometry);
					PubVar.m_Map.Refresh();
				}

				return true;
			}
			return false;
		} catch (Error e) {
			Tools.ShowMessageBox("[" + this.SYS_TYPE + "] 更新失败！\r\n原因：" + e.getMessage());
			return false;
		}
	}

}
