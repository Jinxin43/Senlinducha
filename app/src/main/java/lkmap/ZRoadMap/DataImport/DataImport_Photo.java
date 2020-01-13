package lkmap.ZRoadMap.DataImport;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.dingtu.senlinducha.R;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.ExifInterface;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Cargeometry.Geometry;
import lkmap.Cargeometry.Point;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.ForestryLayerType;
import lkmap.Enum.lkEditMode;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Index.T4Index;
import lkmap.Map.StaticObject;
import lkmap.Symbol.PointSymbol;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.Project.v1_project_layer_ex;

public class DataImport_Photo {
	private ProgressDialog m_pDialog = new ProgressDialog(PubVar.m_DoEvent.m_Context, ProgressDialog.THEME_HOLO_LIGHT);

	public v1_Layer CreateLayerByPhoto(String photoPath) {

		v1_Layer newLayer = new v1_Layer();
		newLayer.SetLayerType(lkGeoLayerType.enPoint);
		PointSymbol PS = new PointSymbol();
		newLayer.SetSimpleSymbol(PS.ToBase64());
		newLayer.SetEditMode(lkEditMode.enNew);
		newLayer.SetLayerProjectType(ForestryLayerType.HangPaiZhaoPian);
		String layerName = photoPath;
		if (photoPath.contains("/")) {
			int lastIndex = photoPath.lastIndexOf("/");
			if (lastIndex > 0 && lastIndex < (photoPath.length() - 1)) {
				layerName = photoPath.substring(lastIndex + 1);
			}
		}

		// �ж�ͼ�����Ƿ��ظ�
		// for(v1_Layer
		// curLayer:PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList())
		// {
		// if(curLayer.GetLayerAliasName().equals(layerName))
		// {
		//
		// }
		// else
		// {
		//
		// }
		// }

		newLayer.SetLayerAliasName(layerName);
		newLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F1", "", false, 128, 0, ""));
		newLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F2", "", false, 128, 0, ""));
		newLayer.GetFieldList().add(createLayerField("γ��", "�ַ���", "F3", "", false, 128, 0, ""));
		newLayer.GetFieldList().add(createLayerField("����", "�ַ���", "F4", "", false, 14, 0, ""));
		newLayer.GetFieldList().add(createLayerField("X", "������", "F5", "", false, 10, 2, ""));
		newLayer.GetFieldList().add(createLayerField("Y", "������", "F6", "", false, 10, 2, ""));
		newLayer.GetFieldList().add(createLayerField("����ʱ��", "�ַ���", "F7", "", false, 20, 0, ""));// yyyy-MM-DD
																									// HH:mm:ss
		newLayer.GetFieldList().add(createLayerField("��ע", "�ַ���", "F8", "", false, 128, 0, ""));
		newLayer.SetVisibleScaleMax(2000000);
		if (!v1_project_layer_ex.CreateOrUpdateLayer(newLayer)) {
			PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList().add(newLayer);

		}

		File f = new File(photoPath);
		final File[] files = f.listFiles();
		final String layerId = newLayer.GetLayerID();
		final int max = files.length;
		ShowProcessDialog();
		m_pDialog.setMax(max);
		m_pDialog.onStart();

		new Thread() {
			public void run() {
				{
					Dataset pDataset = PubVar.m_Workspace.GetDatasetById(layerId);
					int process = 0;
					int success = 0;
					for (File file : files) {
						process++;
						m_pDialog.setProgress(process);
						if (file.isDirectory()) {
							continue;
						} else {
							String fileName = file.getName().toLowerCase();
							if (fileName.endsWith("jpg") || fileName.endsWith("jpeg")) {

								try {
									Log.e("getAbsolutePath", file.getAbsolutePath());
									ExifInterface exifInfo = new ExifInterface(file.getCanonicalPath());
									String longitude = exifInfo.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
									if (longitude == null || longitude.length() == 0) {
										// TODO:��ʾ��Ƭ��û��GPS��Ϣ
										continue;
									}
									String[] arrayLong = longitude.split(",");
									if (arrayLong.length != 3) {
										// TODO:��ʾ���ȸ�ʽ�Ȳ���
										continue;
									}

									String longD = numDenomHandler(arrayLong[0]) + "";
									String LongF = numDenomHandler(arrayLong[1]) + "";
									String LongM = numDenomHandler(arrayLong[2]) + "";
									Double Lon = numDenomHandler(arrayLong[0]) + numDenomHandler(arrayLong[1]) / 60
											+ numDenomHandler(arrayLong[2]) / 3600;

									Log.e("longitude", longitude);
									String latitude = exifInfo.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
									if (latitude == null || latitude.length() == 0) {
										// TODO:��ʾ��Ƭ��û��GPS��Ϣ
										continue;
									}
									String[] arrayLatitude = latitude.split(",");
									if (arrayLatitude.length != 3) {
										// TODO:��ʾγ�ȸ�ʽ�Ȳ���
										continue;
									}

									String latituD = numDenomHandler(arrayLatitude[0]) + "";
									String latituF = numDenomHandler(arrayLatitude[1]) + "";
									String latituM = numDenomHandler(arrayLatitude[2]) + "";
									Double lat = numDenomHandler(arrayLatitude[0])
											+ numDenomHandler(arrayLatitude[1]) / 60
											+ numDenomHandler(arrayLatitude[2]) / 3600;

									Log.e("latitude", latitude);
									String altitude = exifInfo.getAttribute(ExifInterface.TAG_GPS_ALTITUDE);

									String altitudeD = numDenomHandler(altitude) + "";

									Log.e("altitude", altitudeD);
									Coordinate mGpsCoordinate = StaticObject.soProjectSystem.WGS84ToXY(Lon, lat,
											numDenomHandler(altitude));

									Log.e("mGpsCoordinate", mGpsCoordinate.getX() + "-" + mGpsCoordinate.getY());
									String datetime = exifInfo.getAttribute(ExifInterface.TAG_DATETIME);
									Log.e("datetime", datetime);
									// photoList.add(file.getCanonicalPath());
									Point pt = new Point(mGpsCoordinate.getX(), mGpsCoordinate.getY());

									List<String> valueList = new ArrayList<String>();
									valueList.add(fileName.replace(".jpg", "").replace(".jpeg", ""));
									valueList.add(arrayLong[0].replace("/1", "") + "��" + arrayLong[1].replace("/1", "")
											+ "��" + numDenomHandler(arrayLong[2]) + "��");
									valueList.add(arrayLatitude[0].replace("/1", "") + "��"
											+ arrayLatitude[1].replace("/1", "") + "��"
											+ numDenomHandler(arrayLatitude[2]) + "��");
									valueList.add(altitudeD);
									valueList.add(mGpsCoordinate.getX() + "");
									valueList.add(mGpsCoordinate.getY() + "");
									valueList.add(datetime);
									valueList.add("");

									ImportGeometry(pDataset, pt, file.getAbsolutePath(), valueList);
									success++;

									String dataMsg = "���ڵ�����Ƭ���꣺" + process + "/" + max + "; ����ɹ���" + success + "��; ʧ�ܣ�"
											+ (process - success) + "��";
									m_pDialog.setMessage(dataMsg);

								} catch (Exception ex) {
									String dataMsg = "���ڵ�����Ƭ���꣺" + process + "/" + max + "; ����ɹ���" + success + "��;  ʧ�ܣ�"
											+ (process - success) + "��";
									m_pDialog.setMessage(dataMsg);
									ex.printStackTrace();
								}

							}
						}
					}
				}

				// ViewParent parent =
				// m_pDialog.getButton(DialogInterface.BUTTON_POSITIVE).getParent().getParent();
				// LinearLayout layout = (LinearLayout) parent;
				// layout.setVisibility(View.VISIBLE);
				// m_pDialog.setCancelable(true);
			}

		}.start();

		// Tools.ShowMessageBox("ͼƬ���굼����ɣ���" + max + "����Ƭ�����������" + success +
		// "��");
		return newLayer;
	}

	private Double numDenomHandler(String numDenom) {
		if (numDenom.endsWith("/1")) {
			return Double.parseDouble(numDenom.substring(0, numDenom.length() - 2));
		} else {
			int Index = numDenom.lastIndexOf("/");
			if (Index > 0) {
				String num = numDenom.substring(0, Index);
				String denom = numDenom.substring(Index + 1);
				Double value = Double.parseDouble(num) / Double.parseDouble(denom);
				DecimalFormat df2 = new DecimalFormat("#.0000");
				return Double.valueOf(df2.format(value));
			} else {
				return Double.parseDouble(numDenom);
			}

		}
	}

	// �����¼��ͼ���ڲ�
	private void ImportGeometry(Dataset pDataset, Geometry pGeometry, String photoPath, List<String> featureList) {
		// ��ȡ��ͼ�ζ�Ӧ�����Լ�¼

		// ͼ��ת��Ϊbyte
		byte[] GeoByte = Tools.GeometryToByte(pGeometry);

		double SYS_Length = 0, SYS_Area = 0;

		String SQL_D = "insert into " + pDataset.getDataTableName() + " "
				+ "(SYS_GEO,SYS_STATUS,SYS_TYPE,SYS_OID,SYS_Length,SYS_Area,SYS_PHOTO,%1$s) values "
				+ "(?,0,'Photo','%2$s','%3$s','%4$s','%5$s','%6$s')";
		SQL_D = String.format(SQL_D, "F1,F2,F3,F4,F5,F6,F7,F8", UUID.randomUUID().toString(), SYS_Length, SYS_Area,
				photoPath, Tools.JoinT("','", featureList));

		Log.d("", "��������ͼ������[" + SQL_D + "]");

		if (pDataset.getDataSource().ExcuteSQL(SQL_D, new Object[] { GeoByte })) {
			// ��ȡ�²���ʵ���SYS_ID��
			String SYS_ID = "-1";
			String SQL = "select max(SYS_ID) as objectid from " + pDataset.getDataTableName();
			SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
			if (DR.Read()) {
				SYS_ID = Integer.valueOf(DR.GetString(0)) + "";
			}
			DR.Close();

			// ����������Ϣ
			T4Index TIndex = pGeometry.CalCellIndex(pDataset.GetMapCellIndex());

			String SQL_I = "insert into " + pDataset.getIndexTableName() + " "
					+ "(SYS_ID,RIndex,CIndex,MinX,MinY,MaxX,MaxY) values "
					+ "('%1$s','%2$s','%3$s','%4$s','%5$s','%6$s','%7$s')";
			SQL_I = String.format(SQL_I, SYS_ID, TIndex.GetRow(), TIndex.GetCol(), pGeometry.getEnvelope().getMinX(),
					pGeometry.getEnvelope().getMinY(), pGeometry.getEnvelope().getMaxX(),
					pGeometry.getEnvelope().getMaxY());
			Log.d("", "����������������[" + SQL_I + "]");
			if (pDataset.getDataSource().ExcuteSQL(SQL_I)) {

			}
		}

	}

	private v1_LayerField createLayerField(String fieldName, String fieldType, String dataFileName, String enumCode,
			boolean enumEidt, int size, int deciaml, String shortName) {
		v1_LayerField newFile = new v1_LayerField();
		newFile.SetFieldName(fieldName);
		newFile.SetFieldTypeName(fieldType);
		newFile.SetDataFieldName(dataFileName);
		newFile.SetFieldEnumCode(enumCode);
		newFile.SetFieldEnumEdit(enumEidt);
		newFile.SetFieldSize(size);
		newFile.SetFieldDecimal(deciaml);
		newFile.SetIsSelect(true);
		newFile.SetFieldShortName(shortName);
		return newFile;
	}

	private void setProcessMessage(final String msg) {
		new Handler().post(new Runnable() {
			public void run() {
				m_pDialog.setMessage(msg);
				// m_pDialog.cancel();
			}
		});
	}

	private void setProcessStatus() {
		new Handler().post(new Runnable() {
			public void run() {
				// m_pDialog.setButton("���", null);
				ViewParent parent = m_pDialog.getButton(DialogInterface.BUTTON_POSITIVE).getParent().getParent();
				LinearLayout layout = (LinearLayout) parent;
				layout.setVisibility(View.VISIBLE);
			}
		});
	}

	private void ShowProcessDialog() {
		// ����ProgressDialog����

		;
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		m_pDialog.setIcon(R.drawable.importfromphoto);

		// ����ProgressDialog ����
		m_pDialog.setTitle(Tools.ToLocale("������Ƭ����"));

		// ����ProgressDialog ��ʾ��Ϣ
		m_pDialog.setMessage("׼��������Ƭ����...");

		// ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
		m_pDialog.setCancelable(false);

		m_pDialog.setButton(DialogInterface.BUTTON_POSITIVE, "���", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				m_pDialog.cancel();
			}
		});

		m_pDialog.show();
		// m_pDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);

		// ViewParent parent =
		// m_pDialog.getButton(DialogInterface.BUTTON_POSITIVE).getParent().getParent();
		// LinearLayout layout = (LinearLayout) parent;
		// layout.setVisibility(View.GONE);
	}
}
