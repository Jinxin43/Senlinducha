package com.dingtu.DTGIS.Upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.dingtu.DTGIS.DataService.DataDB;
import com.dingtu.DTGIS.DataService.LayerDB;
import com.dingtu.DTGIS.DataService.LogDB;
import com.dingtu.DTGIS.DataService.ProjectDB;
import com.dingtu.DTGIS.DataService.UploadTraceModel;
import com.dingtu.DTGIS.HTTP.RetrofitHttp;
import com.dingtu.DTGIS.Upload.FTPTool.UploadProgressListener;
import com.dingtu.ft.GisTools;
import com.dingtu.senlinducha.R;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_FormTemplate;
import lkmap.CoordinateSystem.CoorSystem;
import lkmap.Dataset.Dataset;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.DataExport.DataExport_SHP;
import lkmap.ZRoadMap.MyControl.v1_HeaderListViewFactory;
import lkmap.ZRoadMap.Project.v1_Layer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadLayer {

	public static final String FTP_CONNECT_SUCCESSS = "ftp���ӳɹ�";
	public static final String FTP_CONNECT_FAIL = "ftp����ʧ��";
	public static final String FTP_DISCONNECT_SUCCESS = "ftp�Ͽ�����";
	public static final String FTP_FILE_NOTEXISTS = "ftp���ļ�������";

	public static final String FTP_UPLOAD_SUCCESS = "ftp�ļ��ϴ��ɹ�";
	public static final String FTP_UPLOAD_FAIL = "ftp�ļ��ϴ�ʧ��";
	public static final String FTP_UPLOAD_LOADING = "ftp�ļ������ϴ�";

	public static final String FTP_DOWN_LOADING = "ftp�ļ���������";
	public static final String FTP_DOWN_SUCCESS = "ftp�ļ����سɹ�";
	public static final String FTP_DOWN_FAIL = "ftp�ļ�����ʧ��";

	public static final String FTP_DELETEFILE_SUCCESS = "ftp�ļ�ɾ���ɹ�";
	public static final String FTP_DELETEFILE_FAIL = "ftp�ļ�ɾ��ʧ��";
	final ProgressDialog m_pDialog = new ProgressDialog(PubVar.m_DoEvent.m_Context);

	private v1_FormTemplate dialogView = null;

	// private static int dataIndex = 0;
	private static int successIndex = 0;
	private static int faildIndex = 0;
	private static int dataCount = 0;

	private static int photoIndex = 0;
	private static int faildPhotoIndex = 0;
	private static int successPhotoIndex = 0;

	// һ���ϴ��ĺ�������
	private int batchUploadTracesIndex = 1000;

	public UploadLayer() {
		dialogView = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		dialogView.SetOtherView(R.layout.dialog_dataupload);
		dialogView.ReSetSize(0.65f, 0.9f);

		dialogView.SetCaption(Tools.ToLocale("�����ϴ�"));

		dialogView.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("�ϴ�����") + "  ,�ϴ�����", mCallback);
		Tools.SetTextViewValueOnID(dialogView, R.id.pn_projectname,
				PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectShortName());
		Tools.SetTextViewValueOnID(dialogView, R.id.tvLocaleText2, "��ǰ����: ");
		dialogView.findViewById(R.id.layout_exportFarmat).setVisibility(View.GONE);

	}

	// �ϴ�30���ڵĺ���
	private void uploadTraces() {
		ShowProcessDialog();
		m_pDialog.setMessage("׼���ϴ�������Ϣ...");
		Date endDate = new java.util.Date();
		Date startDate = new Date(endDate.getTime() + 30 * 24 * 60 * 60 * 1000);
		List<UploadTraceModel> traces = new LogDB().QueryUnloadPoint(startDate, endDate);
		List<JSONObject> tracesJson = new ArrayList<JSONObject>();
		if (traces.size() == 0) {
			m_pDialog.setMessage("û�к�����Ϣ...");
			// m_pDialog.cancel();
			uploadProject();
			return;
		}

		int total = traces.size();
		if (total > batchUploadTracesIndex) {
			int startIndex = 0;
			int endIndex = batchUploadTracesIndex;
			while (endIndex <= total) {
				tracesJson = new ArrayList<JSONObject>();
				List<String> gpsTimes = new ArrayList<String>();

				for (int i = startIndex; i < endIndex; i++) {
					JSONObject traceJSObject = new JSONObject();
					try {
						traceJSObject.put("GPSTime", traces.get(i).getGPSTime());
						traceJSObject.put("Lon", traces.get(i).getLongitude());
						traceJSObject.put("Lat", traces.get(i).getLatitude());
						traceJSObject.put("Altitude", traces.get(i).getAltitude());
						traceJSObject.put("X", traces.get(i).getX());
						traceJSObject.put("Y", traces.get(i).getY());
						gpsTimes.add(traces.get(i).getGPSTime());
						tracesJson.add(traceJSObject);
					} catch (Exception ex) {
						Tools.ShowMessageBox(ex.getMessage());
					}
				}

				if (tracesJson.size() > 0) {
					if (endIndex == total) {
						uploadtraceBatch(tracesJson, gpsTimes, true);
					} else {
						uploadtraceBatch(tracesJson, gpsTimes, false);
					}
				}

				if ((endIndex + batchUploadTracesIndex) < total) {
					startIndex += batchUploadTracesIndex;
					endIndex += batchUploadTracesIndex;
				} else {
					if (endIndex == total) {
						endIndex = total + 1;
					} else {
						startIndex += batchUploadTracesIndex;
						endIndex = total;
					}

				}

			}
		} else {
			List<String> gpsTimes = new ArrayList<String>();

			for (UploadTraceModel trace : traces) {
				JSONObject traceJSObject = new JSONObject();
				try {
					traceJSObject.put("GPSTime", trace.getGPSTime());
					traceJSObject.put("Lon", trace.getLongitude());
					traceJSObject.put("Lat", trace.getLatitude());
					traceJSObject.put("Altitude", trace.getAltitude());
					traceJSObject.put("X", trace.getX());
					traceJSObject.put("Y", trace.getY());
					gpsTimes.add(trace.getGPSTime());
					tracesJson.add(traceJSObject);
				} catch (Exception ex) {
					Tools.ShowMessageBox(ex.getMessage());
				}
			}

			if (tracesJson.size() == 0) {
				m_pDialog.setMessage("û�к�����Ϣ...");
				// m_pDialog.cancel();
			} else {
				uploadtraceBatch(tracesJson, gpsTimes, true);
			}

		}

	}

	// �����ϴ�������ÿ��50��
	private void uploadtraceBatch(List<JSONObject> tracesJson, final List<String> gpsTimes, final boolean isFinal) {
		try {
			JSONObject sendJson = new JSONObject();
			sendJson.put("traces", tracesJson);
			HttpTracesModel tracesModel = new HttpTracesModel();
			tracesModel.setDeviceId(PubVar.softCode);
			tracesModel.setTrackDatasJson(sendJson.toString());
			OkHttpClient.Builder builder = new OkHttpClient.Builder();
			setProcessMessage("�����ϴ�������Ϣ...");
			RetrofitHttp.getRetrofit(builder.build()).UploadTraces("PushTrackData", tracesModel)
					.enqueue(new Callback<ResponseBody>() {

						@Override
						public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
							// TODO Auto-generated method stub
							setProcessMessage(arg1.getMessage());
							if (isFinal) {
								m_pDialog.setCancelable(true);
							}

						}

						@Override
						public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {

							try {
								String body = arg1.body().string();

								JSONObject result = new JSONObject(body);
								if (result.get("success").equals(Boolean.TRUE)) {
									new LogDB().updateUploadStatus(gpsTimes);
								}

							} catch (Exception ex) {

							}

							if (isFinal) {
								setProcessMessage("������Ϣ�ϴ����...");
							}
						}
					});

		} catch (Exception ex) {
			Tools.ShowMessageBox(ex.getMessage());
		}

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
//				m_pDialog.setButton("���", null);
				ViewParent parent =  m_pDialog.getButton(DialogInterface.BUTTON_POSITIVE)  
		                .getParent().getParent();  
		        LinearLayout layout = (LinearLayout) parent;  
		        layout.setVisibility(View.VISIBLE);   
			}
		});
	}

	private ICallback deviceUploadCallback = new ICallback() {

		@Override
		public void OnClick(String Str, Object ExtraStr) {

			// �ϴ��켣
			// if (((CheckBox)
			// dialogView.findViewById(R.id.ck_uploadTrace)).isChecked()) {
			// uploadTraces();
			// } else {
			final List<HashMap<String, String>> expDatasetNameList = new ArrayList<HashMap<String, String>>();
			for (HashMap<String, Object> hashObj : m_HeaderListViewDataItemList) {
				boolean export = Boolean.parseBoolean(hashObj.get("D1").toString());
				if (!export)
					continue;
				String datasetName = hashObj.get("LayerID").toString();
				String dsName = hashObj.get("LayerName").toString();

				HashMap<String, String> expLayer = new HashMap<String, String>();
				expLayer.put("LayerID", datasetName);
				expLayer.put("LayerName", dsName);
				expLayer.put("Type", hashObj.get("D3").toString());
				expDatasetNameList.add(expLayer);
			}

			if (expDatasetNameList.size() <= 0) {
				Tools.ShowMessageBox("��ѡ��Ҫ�ϴ���ͼ��");
			} else {
				ShowProcessDialog();
				m_pDialog.setCancelable(true);
				ProjectDB projectDB = new ProjectDB();
				String sProjectId = projectDB.getProjectServerId();
				if (sProjectId == null || sProjectId.isEmpty())// �����Ƿ��ϴ���
				{
					uploadProject();
				} else {
					uploadLayer(expDatasetNameList, sProjectId);
				}
			}
			// }
		}

	};
	private ICallback mCallback = new ICallback() {

		@Override
		public void OnClick(String Str, Object ExtraStr) {

			if (Str.equals("�ϴ�����")) {
				// TODO:�����������
				// Tools.ShowMessageBox("��������İ汾��û�п�ͨ�˹��ܣ�");
				uploadDeviceInfo();
			}

		}
	};

	private void ShowProcessDialog() {
		// ����ProgressDialog����

		// ���ý�������񣬷��ΪԲ�Σ���ת��
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		m_pDialog.setIcon(R.drawable.v1_messageinfo);

		// ����ProgressDialog ����
		m_pDialog.setTitle(Tools.ToLocale("�ϴ�����"));

		// ����ProgressDialog ��ʾ��Ϣ
		m_pDialog.setMessage("׼����ʼ�ϴ�����...");

		// ����ProgressDialog �Ľ������Ƿ���ȷ
		m_pDialog.setIndeterminate(false);

		// ����ProgressDialog �Ƿ���԰��˻ذ���ȡ��
		m_pDialog.setCancelable(false);
		
		m_pDialog.setButton(DialogInterface.BUTTON_POSITIVE,"���", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				m_pDialog.cancel();
			}
			});
  
		m_pDialog.show();
//		m_pDialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
		
		ViewParent parent =  m_pDialog.getButton(DialogInterface.BUTTON_POSITIVE)  
                .getParent().getParent();  
        LinearLayout layout = (LinearLayout) parent;  
        layout.setVisibility(View.GONE); 
	}

	private void uploadProject() {
		HttpProjectModel pModel = new ProjectDB().getHttpProjectModel();
		pModel.setFromDeviceId(PubVar.softCode);
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		setProcessMessage("�����ϴ�������Ϣ...");
		RetrofitHttp.getRetrofit(builder.build()).CreateProject("CreateProject", pModel)
				.enqueue(new Callback<ResponseBody>() {

					@Override
					public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
						try {

							JSONObject result = new JSONObject(arg1.body().string());

							Boolean IdSaved = false;
							String sId = "";
							if (result.get("success").equals(Boolean.TRUE)) {
								sId = result.get("data").toString();

								if (sId != null) {
									IdSaved = new ProjectDB().updateProjectServerId(sId.toString());

									setProcessMessage("������Ϣ�ϴ��ɹ�...");
								} else {
									setProcessMessage("������Ϣ�ϴ��ɹ�,û�з��ع���ID...");
								}
							} else {
								setProcessMessage("������Ϣ�ϴ�ʧ��...");
								m_pDialog.setCancelable(true);
							}

							if (IdSaved) {
								final List<HashMap<String, String>> expDatasetNameList = new ArrayList<HashMap<String, String>>();
								for (HashMap<String, Object> hashObj : m_HeaderListViewDataItemList) {
									boolean export = Boolean.parseBoolean(hashObj.get("D1").toString());
									if (!export)
										continue;
									String datasetName = hashObj.get("LayerID").toString();
									String dsName = hashObj.get("LayerName").toString();

									HashMap<String, String> expLayer = new HashMap<String, String>();
									expLayer.put("LayerID", datasetName);
									expLayer.put("LayerName", dsName);
									expLayer.put("Type", hashObj.get("D3").toString());
									expDatasetNameList.add(expLayer);
								}

								uploadLayer(expDatasetNameList, sId);
							} else {
								// m_pDialog.cancel();
							}

						} catch (final Exception ex) {
							setProcessMessage("������Ϣ�ϴ�ʧ��:" + ex.getMessage());
							m_pDialog.setCancelable(true);
						}

					}

					@Override
					public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
						setProcessMessage("������Ϣ�ϴ�ʧ�ܡ�" + arg1.getMessage());
						m_pDialog.setCancelable(true);

					}
				});
	}

	private void uploadDeviceInfo() {
		if (PubVar.currentUserInfo == null) {
			Tools.ShowToast(dialogView.getContext(), "�޷���õ�ǰ�豸��Ϣ��");
			return;
		}

		ShowProcessDialog();
		m_pDialog.setMessage("׼���ϴ��豸��Ϣ...");
		HttpDeviceModel deviceModel = new HttpDeviceModel();
		deviceModel.setFromDeviceId(PubVar.currentUserInfo.SYS_SoftCode);
		deviceModel.setDepartment(PubVar.currentUserInfo.OT_UserDepartment);
		deviceModel.setHardcode(PubVar.currentUserInfo.HardCode);
		deviceModel.setName(PubVar.currentUserInfo.OT_UserName);
		deviceModel.setStopdate(PubVar.currentUserInfo.SYS_StopDate);
		deviceModel.setType(PubVar.currentUserInfo.BU_UserType);
		deviceModel.setUserunit(PubVar.currentUserInfo.OT_UserUnit);
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		setProcessMessage("׼���ϴ��豸��Ϣ...");
		RetrofitHttp.getRetrofit(builder.build()).CreateDevice("CreateDevice", deviceModel)
				.enqueue(new Callback<ResponseBody>() {

					@Override
					public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {

						setProcessMessage("�豸��Ϣ�ϴ�ʧ��...");
						m_pDialog.setCancelable(true);
					}

					@Override
					public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
						try {

							JSONObject result = new JSONObject(arg1.body().string());
							if (result.get("success").equals(Boolean.TRUE)) {
								setProcessMessage("�豸��Ϣ�ϴ����...");
								deviceUploadCallback.OnClick("success", null);
							}
						} catch (Exception ex) {
							setProcessMessage("�豸��Ϣ�ϴ�ʧ��...");
						}

					}

				});
	}

	private static int layerCount = 0;
	private static int currentLayerIndex = 0;

	List<HashMap<String, String>> mListLayers = new ArrayList<HashMap<String, String>>();
	List<HashMap<String, String>> mSuccessListLayers = new ArrayList<HashMap<String, String>>();
	String mServerId = "";

	public void updateLayerIndex() {
		currentLayerIndex++;
	}

	private void uploadOneLayer(final int index, final String serverId) {

		if (index >= mListLayers.size()) {

			updateData();
			return;
		}
		final HashMap<String, String> lyr = mListLayers.get(index);

		HttpLayermodel layer = new LayerDB().getHttpLayerModel(lyr.get("LayerID"));
		layer.setProjectId(serverId);
		layer.setFromDeviceId(PubVar.softCode);
		final String layerId = lyr.get("LayerID");
		final String layerType = lyr.get("Type");
		setProcessMessage("�����ϴ�ͼ����Ϣ��" + layerId);
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		RetrofitHttp.getRetrofit(builder.build()).CreateLayer("CreateLayer", layer)
				.enqueue(new Callback<ResponseBody>() {

					@Override
					public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
						// TODO Auto-generated method stub
						setProcessMessage("ͼ����Ϣ�ϴ�ʧ�ܡ�" + arg1.getMessage());
						m_pDialog.setCancelable(true);
						updateLayerIndex();
						uploadOneLayer(index + 1, serverId);
					}

					@Override
					public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {

						updateLayerIndex();

						try {
							String value = arg1.body().string();
							if (value == null || value.isEmpty()) {

								setProcessMessage("ͼ����Ϣ" + layerId + "�ϴ�ʧ��");
								m_pDialog.setCancelable(true);
								return;
							}

							JSONObject result = new JSONObject(value);
							if (result.get("success").equals(Boolean.TRUE)) {
								setProcessMessage("ͼ����Ϣ" + layerId + "�ϴ��ɹ�");

								HashMap<String, String> hmLayer = new HashMap<String, String>();
								mSuccessListLayers.add((HashMap<String, String>) lyr.clone());

							} else {

								String msg = result.get("msg") + "";
								setProcessMessage("ͼ����Ϣ�ϴ�ʧ�ܣ�" + msg);
								m_pDialog.setCancelable(true);
							}

						} catch (final Exception ex) {
							setProcessMessage("ͼ����Ϣ�ϴ�ʧ��," + ex.getMessage());
							m_pDialog.setCancelable(true);
							ex.printStackTrace();
						}

						uploadOneLayer(index + 1, serverId);

					}
				});
	}

	private void uploadLayer(List<HashMap<String, String>> expDatasetNameList, String serverId) {
		layerCount = expDatasetNameList.size();
		currentLayerIndex = 0;

		mServerId = serverId;
		mListLayers = expDatasetNameList;

		setProcessMessage("�����ϴ�ͼ����Ϣ...");
		mSuccessListLayers.clear();
		mPhotoList.clear();
		getAllDataJson.clear();
		
		uploadOneLayer(0, serverId);
		
	}

	String dataMsg = "";

	public void updateSuccess() {
		successIndex++;
		// dataIndex++;
		dataMsg = "�����ϴ�С����Ϣ��" + (successIndex + faildIndex) + "/" + dataCount + " �ɹ���" + successIndex + " ʧ�ܣ�"
				+ faildIndex;
		setProcessMessage(dataMsg);
		if (layerCount == currentLayerIndex) {

			if (dataCount == (successIndex + faildIndex)) {
				dataMsg = "С����Ϣ�ϴ���ɣ�" + (successIndex + faildIndex) + "/" + dataCount + " �ɹ���" + successIndex + " ʧ�ܣ�"
						+ faildIndex;
				if (((CheckBox) dialogView.findViewById(R.id.ck_uploadPhotos)).isChecked()) {
					if (mPhotoList.size() > 0) {
						String msg = dataMsg + "\r\n�����ϴ���Ƭ��0/" + mPhotoList.size();
						setProcessMessage(msg);

					} else {
						String msg = dataMsg + "\r\n��Ƭ��Ϣ�ϴ���ɣ�0/0";
						setProcessMessage(msg);
						setProcessStatus();
					}
				} else {
					setProcessMessage(dataMsg);
					setProcessStatus();
				}
			}
		}

	}

	public void unpdateFailed(String errorMsg) {
		faildIndex++;
		// dataIndex++;
		dataMsg = "�����ϴ�ͼ����Ϣ��" + (successIndex + faildIndex) + "/" + dataCount + " �ɹ���" + successIndex + "  ʧ�ܣ�"
				+ faildIndex;
		setProcessMessage(dataMsg);
		m_pDialog.setCancelable(true);
		if (layerCount == currentLayerIndex) {
			if (dataCount == (successIndex + faildIndex)) {

				dataMsg = "ͼ����Ϣ�ϴ���ɣ�" + (successIndex + faildIndex) + "/" + dataCount + " �ɹ���" + successIndex + " ʧ�ܣ�"
						+ faildIndex;
				if (((CheckBox) dialogView.findViewById(R.id.ck_uploadPhotos)).isChecked()) {
					if (mPhotoList.size() > 0) {
						String msg = dataMsg + "\r\n�����ϴ���Ƭ��0/" + mPhotoList.size();
						setProcessMessage(msg);

					} else {
						String msg = dataMsg + "\r\n��Ƭ��Ϣ�ϴ���ɣ�0/0";
						setProcessMessage(msg);
						setProcessStatus();
//						m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					}

				} else {
					setProcessMessage(dataMsg);
					setProcessStatus();
//					m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				}

			}
		}
	}

	public void unpdateFailedPhoto() {
		faildPhotoIndex++;
		photoIndex++;

		if (photoIndex == mPhotoList.size()) {
			String msg = dataMsg + "\r\n��Ƭ�ϴ���ɣ�" + photoIndex + "/" + mPhotoList.size() + " �ɹ���" + successPhotoIndex
					+ " ʧ��:" + faildPhotoIndex;
			setProcessMessage(msg);
//			m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			setProcessStatus();
		} else {
			String msg = dataMsg + "\r\n�����ϴ���Ƭ��" + photoIndex + "/" + mPhotoList.size() + " �ɹ���" + successPhotoIndex
					+ " ʧ��:" + faildPhotoIndex;
			setProcessMessage(msg);
		}

	}

	public void unpdateSuccessPhoto() {
		successPhotoIndex++;
		photoIndex++;

		if (photoIndex == mPhotoList.size()) {
			String msg = dataMsg + "\r\n��Ƭ�ϴ���ɣ�" + photoIndex + "/" + mPhotoList.size() + " �ɹ���" + successPhotoIndex
					+ " ʧ��:" + faildPhotoIndex;
			setProcessMessage(msg);
			setProcessStatus();
		} else {
			String msg = dataMsg + "\r\n�����ϴ���Ƭ��" + photoIndex + "/" + mPhotoList.size() + " �ɹ���" + successPhotoIndex
					+ " ʧ��:" + faildPhotoIndex;
			setProcessMessage(msg);
		}

	}

	List<HashMap<String, String>> mPhotoList = new ArrayList<HashMap<String, String>>();
	ArrayList<JSONObject> getAllDataJson = new ArrayList<JSONObject>();

	private void updateOneData(final int index) {
		if (dataCount == index) {
			successPhotoIndex=0;
			faildPhotoIndex=0;
			photoIndex =0;
			
			uploadPhoto(0);
			return;
		}
		String LayerId = "";
		for (HashMap<String, String> hm : mSuccessListLayers) {
			Integer count = Integer.parseInt(hm.get("count"));
			if (index < count) {
				LayerId = hm.get("LayerID");
				break;
			}
		}

		final String layerId = LayerId;
		// if (layerId.isEmpty()) {
		// Tools.ShowMessageBox();
		// }

		JSONObject jsonData = getAllDataJson.get(index);
		HttpDataDto dataDto = new HttpDataDto();
		dataDto.setLayerId(LayerId);
		dataDto.setDataJson(jsonData.toString());
		CoorSystem CS = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem();
		Boolean hasDaiHao = CS.GetDH() > 0 ? true : false;
		dataDto.setSrid(GisTools.getSRID(CS.GetName(), CS.getFenDai(), CS.GetCenterMeridian(), hasDaiHao));

		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		try {

			final String photoFiles = jsonData.get("SYS_PHOTO") + "";
			final String OId = jsonData.get("SYS_OID") + "";
			RetrofitHttp.getRetrofit(builder.build()).AddLayerDataWithEPSG("AddLayerDataWithEPSG", dataDto)
					.enqueue(new Callback<ResponseBody>() {

						@Override
						public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {

							String mString = arg1.getMessage();
							unpdateFailed(mString);
							int a = index + 1;
							updateOneData(a);
						}

						@Override
						public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {
							try {
								int a = index + 1;

								updateSuccess();

								if (!((CheckBox) dialogView.findViewById(R.id.ck_uploadPhotos)).isChecked()) {

								} else {
									if (photoFiles != null && !photoFiles.isEmpty()) {

										String[] photos = photoFiles.split(",");
										for (String photo : photos) {

											HashMap<String, String> hmPhoto = new HashMap<String, String>();
											hmPhoto.put("layerID", layerId);
											hmPhoto.put("ObjectId", OId);
											hmPhoto.put("photo", photo);
											mPhotoList.add(hmPhoto);

										}
									}
								}

								// String value;
								// if (arg1.body() == null ||
								// arg1.body().string() == null) {
								// unpdateFailed("");
								// } else {
								// value = arg1.body().string();
								// JSONObject result = new JSONObject(value);
								// if
								// (result.get("success").equals(Boolean.TRUE))
								// {
								//
								//
								//
								// } else {
								// unpdateFailed("");
								// }
								// }

								updateOneData(a);

							} catch (Exception ex) {
								String mString = ex.getMessage();
								unpdateFailed(mString);
								int a = index + 1;
								updateOneData(a);
							}
						}
					});

		} catch (Exception ex) {
			unpdateFailed(ex.getMessage());
			Tools.ShowToast(dialogView.getContext(), ex.getMessage());
			int a = index + 1;
			updateOneData(a);
		}

	}

	private void updateData() {
		getAllDataJson = new ArrayList<JSONObject>();
		for (HashMap<String, String> hm : mSuccessListLayers) {
			List<JSONObject> data = new DataDB().getAllDataJson(hm.get("LayerID"), hm.get("Type"));
			getAllDataJson.addAll(data);

			hm.put("count", getAllDataJson.size() + "");
		}

		dataCount = getAllDataJson.size();
		// �ɹ�ʧ�ܼ���
		// dataIndex = 0;
		faildIndex = 0;
		successIndex = 0;
		if (dataCount > 0) {
			setProcessMessage("�����ϴ�С����Ϣ....");
			updateOneData(0);

		} else {
			setProcessMessage("С����Ϣ�ϴ����:0/0");
		}

	}

	private void uploadPhoto(int i) {

		if(mPhotoList.size()==0)
		{
			return;
		}	
		
		HashMap<String, String> hmPhoto = mPhotoList.get(i);
		OkHttpClient.Builder builder = new OkHttpClient.Builder();

		Map<String, RequestBody> map = new HashMap<String, RequestBody>();
		RequestBody LId = RequestBody.create(MediaType.parse("text/plain"), hmPhoto.get("layerID"));
		map.put("layerId", LId);

		RequestBody featureId = RequestBody.create(MediaType.parse("text/plain"), hmPhoto.get("ObjectId"));
		map.put("featureId", featureId);

		File photoFile = new File(hmPhoto.get("photo"));
		RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpg"), photoFile);
		map.put("uploadedFiles\"; filename=\"" + photoFile.getName(), fileBody);

		final Integer a = i + 1;
		Call<ResponseBody> newPhoto = RetrofitHttp.getRetrofit(builder.build()).UploadDataPhoto("UploadFeaturePhoto",
				map);
		newPhoto.enqueue(new Callback<ResponseBody>() {

			@Override
			public void onFailure(Call<ResponseBody> arg0, Throwable arg1) {
				//
				unpdateFailedPhoto();
				if (a < mPhotoList.size()) {
					uploadPhoto(a);
				}
			}

			@Override
			public void onResponse(Call<ResponseBody> arg0, Response<ResponseBody> arg1) {

				try {
					
					unpdateSuccessPhoto();
//					String value;
//					if (arg1.body() == null) {
//						unpdateFailedPhoto();
//
//					} else {
//						value = arg1.body().string();
//						JSONObject result = new JSONObject(value);
//						if (result.get("success").equals(Boolean.TRUE)) {
//
//							unpdateSuccessPhoto();
//						} else {
//							unpdateFailedPhoto();
//						}
//					}
				} catch (Exception ex) {
					unpdateFailedPhoto();
				}

				if (a < mPhotoList.size()) {
					uploadPhoto(a);
				}

			}

		});
	}

	private void uploadLayerSHP(List<HashMap<String, String>> expDatasetNameList) {
		// ��������·��
		String pathName = Tools.GetTextValueOnID(dialogView, R.id.pn_projectname);
		String ExportPath = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName() + "/�����ϴ�/"
				+ pathName;
		if (!Tools.ExistFile(ExportPath))
			(new File(ExportPath)).mkdirs();

		// ���󵼳�ͼ���б�
		List<String> ExportErrorList = new ArrayList<String>();

		final String remotePath = Tools.GetSystemDateValue();
		// ��ͼ�㵼�����ݣ���ʽ��LayerID,LayerName
		for (HashMap<String, String> lyr : expDatasetNameList) {
			// ��ʼ����
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.get("LayerID"));
			DataExport_SHP DE = new DataExport_SHP();
			String fileName = ExportPath + "/" + lyr.get("LayerName") + "_" + remotePath;
			if (DE.Export(pDataset, fileName,false)) {
				final LinkedList<File> shpFiels = new LinkedList<File>();
				File file1 = new File(fileName + ".shp");
				File file2 = new File(fileName + ".shx");
				File file3 = new File(fileName + ".dbf");
				File file4 = new File(fileName + ".prj");
				shpFiels.add(file1);
				shpFiels.add(file2);
				shpFiels.add(file3);
				shpFiels.add(file4);

				new Thread() {
					@Override
					public void run() {
						FTPTool ftpTool = new FTPTool();
						try {
							ftpTool.uploadMultiFile(shpFiels, remotePath, new UploadProgressListener() {

								@Override
								public void onUploadProgress(String currentStep, long uploadSize, File file) {

								}
							});
						} catch (Exception ex) {
							Log.e("uploadMultiFile", ex.getMessage());

						}
					}
				}.start();

			} else {
				ExportErrorList.add("LayerName");
			}

		}

		// ���������ʾ��Ϣ��ExportErrorList.size()>0��ʾ��ͼ��û�е����ɹ�
		if (ExportErrorList.size() > 0) {
			Tools.ShowMessageBox(dialogView.getContext(), "����ͼ�����ݵ���ʧ�ܣ�\r\n\r\n" + Tools.JoinT("\r\n", ExportErrorList));
		} else {
			Tools.ShowMessageBox(dialogView.getContext(), "���ݳɹ�������\r\n\r\nλ�ڣ���" + ExportPath + "��");
		}

	}

	public void ShowDialog() {
		// �˴���������Ŀ����Ϊ�˼���ؼ��ĳߴ�
		dialogView.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				LoadLayerInfo();
			}
		});
		dialogView.show();
	}

	// ͼ���б�󶨵�������
	private List<HashMap<String, Object>> m_HeaderListViewDataItemList = null;

	/**
	 * ���ؿɵ���ͼ���б���Ϣ
	 */
	private void LoadLayerInfo() {
		// ��ͼ���б�
		v1_HeaderListViewFactory hvf = new v1_HeaderListViewFactory();
		hvf.SetHeaderListView(dialogView.findViewById(R.id.in_result1), "����ͼ���б�");

		// ��ȡ�����̵�ͼ���б�
		this.m_HeaderListViewDataItemList = new ArrayList<HashMap<String, Object>>();
		for (v1_Layer lyr : PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerList()) {
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(lyr.GetLayerID());
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put("LayerID", lyr.GetLayerID()); // ͼ��ID�����ڱ�ʶάһͼ��
			hm.put("LayerName", lyr.GetLayerAliasName()); // ͼ������
			hm.put("D1", false); // �Ƿ�ɵ���
			hm.put("D2", lyr.GetLayerAliasName()); // ͼ������
			hm.put("D3", Tools.ToLocale(lyr.GetLayerTypeName())); // ͼ������
			hm.put("D4", pDataset.GetAllObjectCount()); // ʵ������
			// hm.put("D5", true);
			this.m_HeaderListViewDataItemList.add(hm);
		}
		hvf.BindDataToListView(this.m_HeaderListViewDataItemList);

	}
}
