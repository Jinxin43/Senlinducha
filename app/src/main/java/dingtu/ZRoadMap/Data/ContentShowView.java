package dingtu.ZRoadMap.Data;

import java.util.HashMap;

import com.dingtu.senlinducha.R;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
public class ContentShowView {

	private v1_FormTemplate _Dialog = null;

	public ContentShowView(String layerName, int id, Coordinate currentPoint) {
		_Dialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
		_Dialog.SetOtherView(R.layout.contentshowview);
		// _Dialog.ReSetSize(0.5f, 0.96f);

		_Dialog.SetButtonInfo("1," + R.drawable.v1_ok + "," + Tools.ToLocale("ȷ��") + "  ,�ر�", pCallback);

		Window dialogWindow = _Dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

		/*
		 * lp.x��lp.y��ʾ�����ԭʼλ�õ�ƫ��.
		 * ������ֵ����Gravity.LEFTʱ,�Ի�����������,����lp.x�ͱ�ʾ�����ߵ�ƫ��,��ֵ����.
		 * ������ֵ����Gravity.RIGHTʱ,�Ի���������ұ�,����lp.x�ͱ�ʾ����ұߵ�ƫ��,��ֵ����.
		 * ������ֵ����Gravity.TOPʱ,�Ի���������ϱ�,����lp.y�ͱ�ʾ����ϱߵ�ƫ��,��ֵ����.
		 * ������ֵ����Gravity.BOTTOMʱ,�Ի���������±�,����lp.y�ͱ�ʾ����±ߵ�ƫ��,��ֵ����.
		 * ������ֵ����Gravity.CENTER_HORIZONTALʱ
		 * ,�Ի���ˮƽ����,����lp.x�ͱ�ʾ��ˮƽ���е�λ���ƶ�lp.x����,��ֵ�����ƶ�,��ֵ�����ƶ�.
		 * ������ֵ����Gravity.CENTER_VERTICALʱ
		 * ,�Ի���ֱ����,����lp.y�ͱ�ʾ�ڴ�ֱ���е�λ���ƶ�lp.y����,��ֵ�����ƶ�,��ֵ�����ƶ�.
		 * gravity��Ĭ��ֵΪGravity.CENTER,��Gravity.CENTER_HORIZONTAL |
		 * Gravity.CENTER_VERTICAL.
		 * 
		 */

		int formWidth = 800;
		int formHeight = 600;
		lp.width = formWidth; // ���
		lp.height = formHeight; // �߶�
		DisplayMetrics dm = PubVar.m_DoEvent.m_Context.getResources().getDisplayMetrics();
		int heightPixel = dm.heightPixels;
		int widthPixel = dm.widthPixels;
		Point currentPT = PubVar.m_MapControl.getMap().getViewConvert().MapToScreen(currentPoint);
		lp.x = currentPT.x;
		lp.y = currentPT.y;
		// lp.alpha = 0.8f; // ͸����
		if (widthPixel + currentPT.x > widthPixel) {
			if (currentPT.x - formWidth > 0) {
				lp.x = currentPT.x - formWidth;
			}

		}

		if (heightPixel + currentPT.y > heightPixel) {
			if (currentPT.y - formHeight > 0) {
				lp.y = currentPT.y - formHeight;
			}
		}

		v1_Layer mLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(layerName);
		_Dialog.SetCaption(layerName);
		v1_CGpsDataObject dataObject = new v1_CGpsDataObject();
		dataObject.SetDataset(PubVar.m_Workspace.GetDatasetById(layerName));
		dataObject.SetSYS_ID(id);
		try {

			HashMap<String, Object> allFeature = dataObject.ReadDataAllFieldsValue(id);
			String photos = allFeature.get("SYS_PHOTO") + "";
			if (photos.length() > 0) {
				fillContent(photos);

				v1_LayerField nameField = mLayer.GetDataFieldByFieldName("����");
				if (nameField == null) {
					nameField = mLayer.GetDataFieldByFieldName("���غ�");
				}
				if (nameField == null) {
					nameField = mLayer.GetDataFieldByFieldName("���ߺ�");
				}
				if (nameField == null) {
					nameField = mLayer.GetDataFieldByFieldName("���ߺ�");
				}

				if (allFeature.containsKey(nameField.GetDataFieldName())) {
					String value = allFeature.get(nameField.GetDataFieldName()) + "";
					_Dialog.SetCaption(value);
					((TextView) _Dialog.findViewById(R.id.tv_Objectname)).setText(value);
				}

				v1_LayerField ndescField = mLayer.GetDataFieldByFieldName("С����");
				if (ndescField != null) {
					String value1 = allFeature.get(ndescField.GetDataFieldName()) + "";
					((TextView) _Dialog.findViewById(R.id.tv_content)).setVisibility(View.VISIBLE);
					((TextView) _Dialog.findViewById(R.id.tv_content)).setText(value1);
				}
				_Dialog.show();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void fillContent(String photos) {

		_Dialog.findViewById(R.id.tv_content).setVisibility(View.GONE);
		String[] photoList = photos.split(",");

		ImageView imageView1 = (ImageView) _Dialog.findViewById(R.id.iv_contentphoto1);
		ImageView imageView2 = (ImageView) _Dialog.findViewById(R.id.iv_contentphoto2);
		ImageView imageView3 = (ImageView) _Dialog.findViewById(R.id.iv_contentphoto3);
		ImageView imageView4 = (ImageView) _Dialog.findViewById(R.id.iv_contentphoto4);

		if (photoList.length > 3) {

			fillImage(imageView1, photoList[0]);
			fillImage(imageView2, photoList[1]);
			fillImage(imageView3, photoList[2]);
			fillImage(imageView4, photoList[3]);
		} else if (photoList.length == 3) {
			fillImage(imageView1, photoList[0]);
			fillImage(imageView2, photoList[1]);
			fillImage(imageView3, photoList[2]);
			imageView4.setVisibility(View.INVISIBLE);
		} else if (photoList.length == 2) {
			fillImage(imageView1, photoList[0]);
			fillImage(imageView2, photoList[1]);
			imageView3.setVisibility(View.GONE);
			imageView4.setVisibility(View.GONE);
		} else if (photoList.length == 1) {
			_Dialog.findViewById(R.id.photolist).setVisibility(View.GONE);
			ImageView imageView = (ImageView) _Dialog.findViewById(R.id.iv_contentphoto);
			imageView.setVisibility(View.VISIBLE);
			fillImage(imageView, photoList[0]);

		}

	}

	private void fillImage(ImageView imageView, final String file) {
		Uri mUri = Uri.parse("file://" + file);
		imageView.setImageURI(mUri);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(Intent.ACTION_VIEW);
				Uri mUri = Uri.parse("file://" + file);
				it.setDataAndType(mUri, "image/*");
				PubVar.m_DoEvent.m_Context.startActivity(it);

			}
		});
	}

	// ��ť�¼�
	private ICallback pCallback = new ICallback() {
		@Override
		public void OnClick(String Str, Object ExtraStr) {
			if (Str.equals("�ر�")) {
				// if (m_SelectItem!=null)
				{
					_Dialog.dismiss();
				}
			}
		}
	};
}
