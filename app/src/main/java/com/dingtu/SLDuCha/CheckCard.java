package com.dingtu.SLDuCha;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.DTGIS.DataService.DuChaDB;
import com.dingtu.senlinducha.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import dingtu.ZRoadMap.PubVar;
import dingtu.ZRoadMap.Data.ICallback;
import dingtu.ZRoadMap.Data.v1_DataBind;
import lkmap.Cargeometry.Polygon;
import lkmap.Dataset.Dataset;
import lkmap.Dataset.SQLiteDataReader;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeoLayersType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Layer.GeoLayer;
import lkmap.Layer.GeoLayers;
import lkmap.Spatial.SpatialAnalysisTools;
import lkmap.Tools.Tools;
import lkmap.ZRoadMap.Project.v1_Layer;
import lkmap.ZRoadMap.Project.v1_LayerField;
import lkmap.ZRoadMap.ToolsBox.v1_UserConfigDB_PolyAnalysisOption;

public class CheckCard {

	public static final String TAG = "duchasetting";

	public static final String JCJB = "duchajb";
	public static final String JCDW = "duchadw";
	public static final String JCRY = "duchary";
	
	private int checkCardId;
	public View mView;
	private Context m_Context;
	public String mLayerId;
	public String mIds;
	private String mXian;
	double allArea = 0;
	double mLeftArea = 0;
	private boolean isSaved = false;
	private List<Integer> allObjIDs;

	public CheckCard() {

		m_Context = PubVar.m_DoEvent.m_Context;
		mView = ((Activity) m_Context).findViewById(R.id.ll_ducha);
		mView.setVisibility(View.VISIBLE);

		initButtonEvent();
		initSpinnerValue();
		
		PubVar.m_DoEvent.m_GlassView.SetVisible(false);
		
	}

	int[] allIds = new int[] { R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7, R.id.a8, R.id.a9, R.id.a10,
			R.id.a11, R.id.a12, R.id.a13, R.id.a14, R.id.a15, R.id.a16, R.id.a17, R.id.a18, R.id.a19, R.id.a20,
			R.id.a21, R.id.a22, R.id.a23, R.id.a24, R.id.a25, R.id.a26, R.id.a27, R.id.a28, R.id.a29, R.id.a30,
			R.id.a31, R.id.a32, R.id.a33, R.id.a34, R.id.a35, R.id.a36, R.id.a37, R.id.a38, R.id.a39, R.id.a40,
			R.id.a41, R.id.a42, R.id.a43, R.id.a44, R.id.a45, R.id.a46, R.id.a47, R.id.a48, R.id.a49, R.id.a50,
			R.id.a51, R.id.a52, R.id.a53, R.id.a54, R.id.a55, R.id.a56, R.id.a57, R.id.a58, R.id.a59, R.id.a60,
			R.id.a61, R.id.a62, R.id.a63, R.id.a64, R.id.a65, R.id.a66, R.id.a67, R.id.a68, R.id.a69, R.id.a70,
			R.id.a71, R.id.a72, R.id.a73, R.id.a74, R.id.a75, R.id.a76, R.id.a77, R.id.a78, R.id.a79, R.id.a80,
			R.id.a81, R.id.a82, R.id.a83, R.id.a84, R.id.a85, R.id.a86, R.id.a87, R.id.a88, R.id.a89, R.id.a90,
			R.id.a91, R.id.a92, R.id.a93, R.id.a94, R.id.a95, R.id.a96, R.id.a97, R.id.a98, R.id.a99, R.id.a100,
			R.id.a101, R.id.a102, R.id.a103, R.id.a104, R.id.a105, R.id.a106, R.id.a107, R.id.a108, R.id.a109,
			R.id.a110, R.id.a111, R.id.a112, R.id.a113, R.id.a114, R.id.a115, R.id.a116, R.id.a117, R.id.a118,
			R.id.a119, R.id.a120, R.id.a121, R.id.a122, R.id.a123, R.id.a124, R.id.a125, R.id.a126, R.id.a127,
			R.id.a128, R.id.a129, R.id.a130, R.id.a131, R.id.a132, R.id.a133, R.id.a134, R.id.a135, R.id.a136,
			R.id.a137, R.id.a138, R.id.a139, R.id.a140, R.id.a141, R.id.a142, R.id.a143, R.id.a144, R.id.a145,
			R.id.a146, R.id.a147, R.id.a148, R.id.a149, R.id.a150, R.id.a151, R.id.a152, R.id.a153, R.id.a154,
			R.id.a155, R.id.a156, R.id.a157, R.id.a158 };

//	public void setTuBan(String layerId, Integer id) {
//		ArrayList<Integer> allIds = new ArrayList<Integer>();
//		allIds.add(id);
//		setTuBan(layerId, allIds,"");
//	}
	public void setTuBan(String layerId, List<Integer> Ids,String xian) {

		mLayerId = layerId;
		mIds = Tools.IntListToStr(Ids);
		allObjIDs=Ids;
		mXian = xian;
		new Handler().post(new Runnable() {

			@Override
			public void run() {

				try
				{
					if (DuChaDB.CreateTable()) {
						getValueToUI(mLayerId, allObjIDs);
					}
				}
				catch(Exception ex)
				{
					Tools.ShowMessageBox(ex.getMessage());
					ex.printStackTrace();
				}
				

			}
		});
	}

	// public void setTuBan(String layerId, String Ids, String tubanhao) {
	//
	// ((EditText) mView.findViewById(R.id.a6)).setText(tubanhao);
	// setTuBan(layerId, Ids);
	//
	// }
	
	/*׷��ͼ��*/
	public void ZhuijiaID(int newId,boolean isSelected)
	{
		if(isSelected)
		{
//			List<String> checkIDs = Tools.StrArrayToList(mIds.split(","));
			if(allObjIDs.contains(newId))
			{
				Toast.makeText(PubVar.m_DoEvent.m_Context, "��ѡͼ���Ѿ������ڸü�鿨Ƭ�У�����׷��", Toast.LENGTH_SHORT).show();
				return;
			}
			
			mView.setVisibility(View.VISIBLE);
			PubVar.m_DoEvent.m_GlassView.SetVisible(false);
			int hasCheckCards = isHasCheckCard(newId);
			if(hasCheckCards ==0)
			{
				addNewIdToCard(newId);
			}
			else if(hasCheckCards ==1)
			{
				final int index = newId;
				Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "ѡ��ͼ���Ѿ���д���ļ�鿨Ƭ���Ƿ�ͼ��׷�ӵ���ǰ��Ŀ/����������֮ǰ��д�Ŀ�Ƭ��", new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						if(Str.equals("YES"))
						{
							if(deleteCheckCard(index))
							{
								addNewIdToCard(index);
							}
							else
							{
								
							}
							
						}
					}
					
				});
			}
			else
			{
				Tools.ShowMessageBox("��ѡͼ���Ѿ�������ͼ��������һ�ż�鿨Ƭ�����Ƚ�ͼ�ߴ�����һ�ſ�Ƭ�Ƴ�����׷�ӣ�");
			}
			
			PubVar.zhuijiaCheckCard = null;
			PubVar.isZhuijiaing = false;
		}
		else
		{
//			List<String> checkIDs = Tools.StrArrayToList(mIds.split(","));
			if(allObjIDs.contains(newId))
			{
				mView.setVisibility(View.VISIBLE);
				PubVar.m_DoEvent.m_GlassView.SetVisible(false);
				
				final int index = newId;
				if(allObjIDs.size()==1)
				{
					Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "��ͼ���ǵ�ǰ��Ŀ/������Ψһһ��ͼ�ߣ���ȷ��Ҫ�Ƴ���", new ICallback(){

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							
							
							if(Str.equals("YES"))
							{
								
								removeId(index);
							}
							
							PubVar.zhuijiaCheckCard = null;
							PubVar.isZhuijiaing = false;
						}
						
					});
				}
				else
				{
					Tools.ShowYesNoMessage(PubVar.m_DoEvent.m_Context, "��ȷ����ͼ�ߴӵ�ǰ��Ŀ/�����Ƴ���", new ICallback(){

						@Override
						public void OnClick(String Str, Object ExtraStr) {
							
							
							if(Str.equals("YES"))
							{
								
								removeId(index);
							}
							
							PubVar.zhuijiaCheckCard = null;
							PubVar.isZhuijiaing = false;
						}
						
					});
				}
				
			}
			else
			{
				clearUIValues();
				PubVar.zhuijiaCheckCard = null;
				PubVar.isZhuijiaing = false;
				Toast.makeText(PubVar.m_DoEvent.m_Context, "��ѡͼ��δ�����ڸü�鿨Ƭ�У������Ƴ�", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private boolean deleteCheckCard(int tubanId)
	{
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
		String newSql = "DELETE FROM T_DuChaJCKP WHERE a159 = '" + mLayerId + "' and a160 = '" + tubanId + "'";
		try {
			m_SQLiteDatabase.execSQL(newSql);
		} catch (Exception ex) {
			Toast.makeText(m_Context, "ɾ���ɿ�Ƭʧ�ܣ�"+ex.getMessage(), Toast.LENGTH_LONG).show();
			return false;
		} 
		return true;
	}
	public void removeId(int newId)
	{
		List<Integer> index = new ArrayList<Integer>();
		index.add(newId);
		if(StartAnalysisPoly(index,false))
		{
			allObjIDs.remove(Integer.valueOf(newId+""));
			mIds = Tools.IntListToStr(allObjIDs);
			getTuBanHao();
			
			SaveValue();
			Toast.makeText(PubVar.m_DoEvent.m_Context, "��ѡͼ���Ѵӵ�ǰ��Ƭ�Ƴ�", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(PubVar.m_DoEvent.m_Context, "�Ƴ�ͼ��ʧ��", Toast.LENGTH_SHORT).show();
		}
	}
	
	/*�ж�ͼ���Ƿ��Ѿ��м�鿨Ƭ��0��ʾû�У�1��ʾһ��ͼ�߶�ռһ����Ƭ��2��ʾ���ͼ��ռ��һ����Ƭ*/
	public int isHasCheckCard(int newId)
	{
		String sql = "select * from T_DuChaJCKP where a159='" + mLayerId + "' and a160 = '"+ newId+"'"; 
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if(reader.GetCount()>0)
		{
			reader.Close();
			m_SQLiteDatabase.close();
			return 1;
		}
		else
		{
			sql ="select * from T_DuChaJCKP where a159='" + mLayerId + "' and ( a160 like '%,"+newId+",%' OR a160 like '"+newId+",%' OR a160 like '%,"+newId+"') ";
			reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
			if(reader.GetCount()>0)
			{
				reader.Close();
				m_SQLiteDatabase.close();
				return 2;
			}
		}
		reader.Close();
		m_SQLiteDatabase.close();
		
		return 0;
	}
	
	private void addNewIdToCard(int newID)
	{
		List<Integer> index = new ArrayList<Integer>();
		index.add(newID);
		if(StartAnalysisPoly(index,true))
		{
			getTuBanHao();
			allObjIDs.add(newID);
			mIds = Tools.IntListToStr(allObjIDs);
			getTuBanHao();
			SaveValue();
			Toast.makeText(PubVar.m_DoEvent.m_Context, "��ѡͼ���Ѿ�׷�ӵ���ǰ��Ƭ", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(PubVar.m_DoEvent.m_Context, "׷��ͼ��ʧ��", Toast.LENGTH_SHORT).show();
		}
		
		
	}
	
	
	public void updateNewIds(String newIds)
	{
		mView.setVisibility(View.VISIBLE);
		PubVar.m_DoEvent.m_GlassView.SetVisible(false);
		
		PubVar.zhuijiaCheckCard = null;
		PubVar.isZhuijiaing = false;
		
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
		String sql = "update T_DuChaJCKP set a160='"+ newIds +"' where a159='" + mLayerId + "' and a160='" + mIds + "'";
		try {
			m_SQLiteDatabase.execSQL(sql);
			mIds = newIds;
			new Handler().post(new Runnable()
					{

						@Override
						public void run() {
							try
							{
								getTuBanHao();
//								StartAnalysisPoly();
							}
							catch(Exception ex)
							{
								
							}
							
						}
				
					});
			
		} catch (Exception ex) {
			Toast.makeText(m_Context, ex.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			m_SQLiteDatabase.close();
		}
	}
	
	private void getValueToUI(String layerId,List<Integer> Ids)
	{
		mIds = Tools.IntListToStr(Ids);
		mLayerId = layerId;
		String sql = "select * from T_DuChaJCKP where a159='" + layerId + "' ";
		String a160="";
		for(Integer id:Ids)
		{
			a160 += " and (a160='"+id+"' OR a160 like '%,"+id+",%' OR a160 like '"+id+",%' OR a160 like '%,"+id+"') ";
		}
		sql += a160;
		
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if (reader.Read()) {
			for (int i = 0; i < allIds.length; i++) {
				String value = reader.GetString("a" + (i + 2));
				if (value != null && value.length()>0) {
					View view = mView.findViewById(allIds[i]);
					
					if (android.widget.Spinner.class.isInstance(view)) {
						 lkmap.Tools.Tools.SetSpinnerValueOnID((Activity)m_Context,
								 allIds[i], value);
					} else {
						((EditText) view).setText(value);
					}
					
				}
			}
			
			mIds = reader.GetString("a160");
			allObjIDs = new ArrayList<Integer>();
			getTuBanHao();
			for(String idx:mIds.split(","))
			{
				allObjIDs.add(Integer.parseInt(idx));
				
			}
			
		
			checkCardId = reader.GetInt32("a1");
			isSaved = true;
			
		} else 
		{

//			Toast.makeText(m_Context, "it's new item", Toast.LENGTH_SHORT).show();
			mIds = Tools.IntListToStr(Ids);
			
			isSaved = false;
//			String newSql = "insert into T_DuChaJCKP (a159,a160) values ('" + layerId + "','" + Ids + "')";
//			try {
//				m_SQLiteDatabase.execSQL(newSql);
//				Toast.makeText(m_Context, "new item to db", Toast.LENGTH_LONG).show();
//			} catch (Exception ex) {
//				Toast.makeText(m_Context, ex.getMessage(), Toast.LENGTH_LONG).show();
//			} finally {
//				m_SQLiteDatabase.close();
//			}
		}
		
		SharedPreferences preferences = m_Context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
		
		String jcdw = ((TextView) mView.findViewById(R.id.a156)).getText().toString();
		if(jcdw == null || jcdw.length()==0)
		{
			Tools.SetTextViewValueOnID((Activity)m_Context, R.id.a156, preferences.getString(JCDW, ""));
		}
		String jcry = ((TextView) mView.findViewById(R.id.a157)).getText().toString();
		if(jcry == null || jcry.length()==0)
		{
			Tools.SetTextViewValueOnID((Activity)m_Context, R.id.a157, preferences.getString(JCRY, ""));
		}
		String jcjb = preferences.getString(JCJB,null);
		if(jcjb != null && jcjb.length()>0)
		{
			Tools.SetSpinnerValueOnID((Activity)m_Context, R.id.a154, jcjb);
		}
		
		String jcrq = ((TextView) mView.findViewById(R.id.a158)).getText().toString();
		if (jcrq == null || jcrq.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			((EditText) mView.findViewById(R.id.a158)).setText(sdf.format(new java.util.Date()));
		}
		
		String xian = ((TextView) mView.findViewById(R.id.a3)).getText().toString();
		if (xian == null || xian.isEmpty()) {
			((EditText) mView.findViewById(R.id.a3)).setText(mXian);
		}
		
		
		getTuBanHao();
		String dcnd = ((TextView) mView.findViewById(R.id.a4)).getText().toString();
		if (dcnd == null || dcnd.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			((EditText) mView.findViewById(R.id.a4)).setText(sdf.format(new java.util.Date()));
			
			StartAnalysisPoly(allObjIDs,true);
		}
		
	}
	

	@SuppressLint("SimpleDateFormat")
	private void getValueToUI(String layerId, String Ids) {
		
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
		String sql = "select * from T_DuChaJCKP where a159='" + layerId + "' and a160='" + Ids + "'";
		mIds = Ids;
		mLayerId = layerId;
		SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
		if (reader.Read()) {
			for (int i = 0; i < allIds.length; i++) {
				String value = reader.GetString("a" + (i + 2));
				if (value != null && value.length()>0) {
					View view = mView.findViewById(allIds[i]);
					
					if (android.widget.Spinner.class.isInstance(view)) {
						 lkmap.Tools.Tools.SetSpinnerValueOnID((Activity)m_Context,
								 allIds[i], value);
					} else {
						((EditText) view).setText(value);
					}
					
				}
			}
		} else {

			String newSql = "insert into T_DuChaJCKP (a159,a160) values ('" + layerId + "','" + Ids + "')";
			try {
				m_SQLiteDatabase.execSQL(newSql);
				Toast.makeText(m_Context, "new item to db", Toast.LENGTH_LONG).show();
			} catch (Exception ex) {
				Toast.makeText(m_Context, ex.getMessage(), Toast.LENGTH_LONG).show();
			} finally {
				m_SQLiteDatabase.close();
			}
		}
		
		SharedPreferences preferences = m_Context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
		
		String jcdw = ((TextView) mView.findViewById(R.id.a156)).getText().toString();
		if(jcdw == null || jcdw.length()==0)
		{
			Tools.SetTextViewValueOnID((Activity)m_Context, R.id.a156, preferences.getString(JCDW, ""));
		}
		String jcry = ((TextView) mView.findViewById(R.id.a157)).getText().toString();
		if(jcry == null || jcry.length()==0)
		{
			Tools.SetTextViewValueOnID((Activity)m_Context, R.id.a157, preferences.getString(JCRY, ""));
		}
		String jcjb = preferences.getString(JCJB,null);
		if(jcjb != null && jcjb.length()>0)
		{
			Tools.SetSpinnerValueOnID((Activity)m_Context, R.id.a154, jcjb);
		}
		
		String jcrq = ((TextView) mView.findViewById(R.id.a158)).getText().toString();
		if (jcrq == null || jcrq.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			((EditText) mView.findViewById(R.id.a158)).setText(sdf.format(new java.util.Date()));
		}
		
		String xian = ((TextView) mView.findViewById(R.id.a3)).getText().toString();
		if (xian == null || xian.isEmpty()) {
			((EditText) mView.findViewById(R.id.a3)).setText(mXian);
		}
		
		
		getTuBanHao();
		String dcnd = ((TextView) mView.findViewById(R.id.a4)).getText().toString();
		if (dcnd == null || dcnd.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			((EditText) mView.findViewById(R.id.a4)).setText(sdf.format(new java.util.Date()));
			
//			StartAnalysisPoly();
		}

	}

	private void initButtonEvent() {
		mView.findViewById(R.id.checkcard_quit).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Tools.ShowYesNoMessage(m_Context, "ȷ��Ҫ�뿪��鿨Ƭ¼����棿", new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						
						if(Str.equals("YES"))
						{
							clearUIValues();
							closeView();
						}
					}
					
				});
			
			}
		});
		
		mView.findViewById(R.id.checkcard_addTuBan).setOnClickListener(new View.OnClickListener()
				{

					@Override
					public void onClick(View v) {
						PubVar.isZhuijiaing = true;
						PubVar.zhuijiaCheckCard = CheckCard.this;
						PubVar.isRemoveTuban = false;
						PubVar.m_Map.ClearSelection();
						closeView();
						
					}
			
				});
		mView.findViewById(R.id.checkcard_removeTuBan).setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v) {
				PubVar.isZhuijiaing = true;
				PubVar.isRemoveTuban = true;
				PubVar.zhuijiaCheckCard = CheckCard.this;
				PubVar.m_Map.ClearSelection();
//				clearUIValues();
				closeView();
				
			}
	
		});

		mView.findViewById(R.id.checkcard_reCalMJ).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Tools.ShowYesNoMessage(m_Context, "�Ƿ�Ҫ���¼����������ľ�ɷ������", new ICallback(){

					@Override
					public void OnClick(String Str, Object ExtraStr) {
						// TODO Auto-generated method stub
						if(Str.equals("YES"))
						{
							int[] allETIds = new int[]{R.id.a34,R.id.a35,R.id.a36,R.id.a37,R.id.a38,R.id.a39,R.id.a40,R.id.a41,R.id.a42,R.id.a43,R.id.a44,R.id.a45,
									R.id.a46,R.id.a47,R.id.a48,R.id.a49,R.id.a50,R.id.a51,R.id.a52,R.id.a117,R.id.a118,R.id.a121,R.id.a122,
									R.id.a127,R.id.a128,R.id.a131,R.id.a132};
							
							for(int id:allETIds)
							{
								Tools.SetTextViewValueOnID(mView, id, "");
							}
							StartAnalysisPoly(allObjIDs,true);
						}
					}
					
				});
				
				
			}
		});
		
		mView.findViewById(R.id.checkcard_save).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(allObjIDs.size()==0)
				{
					if(isSaved)
					{
						Tools.ShowYesNoMessage(m_Context, "�ü�鿨Ƭû�а����κ�ͼ�ߣ�ɾ����ǰ��Ƭ��", new ICallback(){

							@Override
							public void OnClick(String Str, Object ExtraStr) {
								
								if(Str.equals("YES"))
								{
									deleteCheckCard();
									clearUIValues();
									closeView();
								}
							}
							
						});
					}
					else
					{
						clearUIValues();
						closeView();
					}
					
				}
				else
				{
					SaveValue();
					clearUIValues();
					closeView();
				}
				
			}
		});
	}

	private void deleteCheckCard()
	{
		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
		if(isSaved)
		{
			String newSql = "Delete from T_DuChaJCKP where a1 ="+checkCardId+" and a159='"+mLayerId+"'";
			try {
				m_SQLiteDatabase.execSQL(newSql);
//				Toast.makeText(m_Context, "new item to db", Toast.LENGTH_LONG).show();
			} catch (Exception ex) {
				Toast.makeText(m_Context, ex.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
	}
	private void SaveValue() {

		SQLiteDatabase m_SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(DuChaDB.dbPath), null);
		if(!isSaved)
		{
			String newSql = "insert into T_DuChaJCKP (a159,a160) values ('" + mLayerId + "','" + mIds + "')";
			try {
				m_SQLiteDatabase.execSQL(newSql);
//				Toast.makeText(m_Context, "new item to db", Toast.LENGTH_LONG).show();
				
				String sql = "select a1 from T_DuChaJCKP where a159='"+mLayerId+"' and a160='"+mIds+"'";
				SQLiteDataReader reader = new SQLiteDataReader(m_SQLiteDatabase.rawQuery(sql, null));
				if(reader.Read())
				{
					checkCardId = reader.GetInt32("a1");
					isSaved = true;
				}
				
			} catch (Exception ex) {
				Toast.makeText(m_Context, ex.getMessage(), Toast.LENGTH_LONG).show();
			} 
		}
		else
		{
			String newSql = "update T_DuChaJCKP set a160='" + mIds + "' where a1 ="+checkCardId+" and a159='"+mLayerId+"'";
			try {
				m_SQLiteDatabase.execSQL(newSql);
//				Toast.makeText(m_Context, "new item to db", Toast.LENGTH_LONG).show();
			} catch (Exception ex) {
				Toast.makeText(m_Context, ex.getMessage(), Toast.LENGTH_LONG).show();
			} 
		}
		
		
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("update T_DuChaJCKP set ");
		for (int i = 0; i < allIds.length; i++) {
			View view = mView.findViewById(allIds[i]);
			if (android.widget.EditText.class.isInstance(view)) {
				String value = ((TextView) view).getText().toString();
				sBuilder.append("a" + (i + 2) + "='" + value + "',");
			} else {
				String value = Tools.GetSpinnerValueOnID((Activity)m_Context, allIds[i]);
				sBuilder.append("a" + (i + 2) + "='" + value + "',");

			}
		}
		
		

		String sql = new StringBuilder().append(sBuilder.subSequence(0, sBuilder.length() - 1))
				.append(" where  a159='" + mLayerId + "' and a160='" + mIds + "'").toString();
	
		try {
			m_SQLiteDatabase.execSQL(sql);
//			closeView();
//			clearUIValues();
			Toast.makeText(m_Context, "���鿨Ƭ�ѱ���", Toast.LENGTH_SHORT).show();
		} catch (Exception ex) {
			Toast.makeText(m_Context, ex.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			m_SQLiteDatabase.close();
		}

//		closeView();
	}
	
	private void closeView()
	{
		mView.setVisibility(View.GONE);
		
		if (PubVar.m_HashMap.GetValueObject("Tag_System_ZoomGlass").Value.equals("true")) {
			PubVar.m_DoEvent.m_GlassView.SetVisible(true);
		} else {
			PubVar.m_DoEvent.m_GlassView.SetVisible(false);
		}
	}

	private void clearUIValues() {
		for (int i = 0; i < allIds.length; i++) {
			View view = mView.findViewById(allIds[i]);
			if (android.widget.EditText.class.isInstance(view)) {
				((EditText) view).setText("");
			}
		}

		((TextView) mView.findViewById(R.id.tv_djmjhj)).setText("�ϼ�");
		((TextView) mView.findViewById(R.id.tv_djmjhj)).setTextColor(Color.BLACK);
	}

	private void initSpinnerValue() {
		String allSheng = "61-������62-���ࡢ63-�ຣ��64-���ġ�65-�½���85-�½����š�11-������12-���13-�ӱ���14-ɽ����15-���ɹš�"+
				"21-������22-���֡�23-��������31-�Ϻ���32-���ա�33�㽭��34-���ա�35-������36-������37-ɽ����41-���ϡ�42-������43-���ϡ�44�㶫��"+
				"45-������46-���ϡ�50-���졢51-�Ĵ���52-���ݡ�53-���ϡ�54-���ء�81-����ɭ����82-����ɭ����83-�������š�84-���˰���";
		ArrayAdapter<String> zllzAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_item,
				allSheng.split("��"));
		zllzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) mView.findViewById(R.id.a2)).setAdapter(zllzAdapter);

		String jsxz = "��1-�����Խ��衢2-��Ӫ�Խ���";
		ArrayAdapter<String> jsxzAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_item,
				jsxz.split("��"));
		jsxzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner spJSXZ = ((Spinner) mView.findViewById(R.id.a9));
		spJSXZ.setAdapter(jsxzAdapter);

		String stjydj = "1��2��3��4";
		ArrayAdapter<String> zdstjyDJAdapter = new ArrayAdapter<String>(m_Context, android.R.layout.simple_spinner_item,
				stjydj.split("��"));
		zdstjyDJAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((Spinner) mView.findViewById(R.id.a11)).setAdapter(zdstjyDJAdapter);

		String stryt = "��1��·��2��·��3������4ˮ��ˮ�硢5����ͨѶ��6�����ܵ���7����ɿ�8������������������ҵ��9���������Խ��衢10��ҵ԰����11���ز�����ҵ�Կ�����12���Σ����У���ʩ��13������Ӫ�Խ��衢14���֣�ʪ�����ѡ�15��������16��ľ�ɷ�";
		v1_DataBind.SetBindListSpinner(m_Context, "��;", stryt.split("��"), R.id.a10);

		String strLX = "��1 ���̽��衢2 ��彨�衢3 ��ʱռ���ֵ� ��4 ֱ��Ϊ��ҵ��������5���֣�ʪ�����ѡ�6��������7��ľ�ɷ�";
		v1_DataBind.SetBindListSpinner(m_Context, "����", strLX.split("��"), R.id.a11);

		String strSYLDXZ = "��1 δ�����(��)��2 ԽȨ��ˣ�������3 ���ʹ��  ��4  ����ˣ�����ʹ�á�5������ռ�á�6���δ������7��������ʹ���ֵ�";
		v1_DataBind.SetBindListSpinner(m_Context, "ʹ���ֵ�����", strSYLDXZ.split("��"), R.id.a110);

		v1_DataBind.SetBindListSpinner(m_Context, "��鼶��",
				new String[] { "1-�ؼ�",  "2-�м�",  "3-ʡ��",  "4-ֱ��Ժ","5-רԱ��" }, R.id.a154);
		
		String strShuZhong = ",110-��ɼ,120-��ɼ,130-��ɼ,140-��ɼ,150-��Ҷ��,160����,170-������,180-����,190-����,"+
				"200-����,210-��ɽ��,220-��β��,230-������,240-˼é��,250-��ɽ��2,60-������,261-ʪ����,262-�����,270-��ɽ��,"+
				"280-����,290-��������,310-ɼľ,320-��ɼ,330-ˮɼ,340-��ɼ,350-����,360-��ɼ(�춹ɼ),390-����ɼ��,410-����,"+
				"420-��ľ,421-����,422-����,431-ˮ����,432-�����,433-�Ʋ���,440-��ľ,450-�ľ,460-����,465-�̻�,470-ľ��,"+
				"480-����,490-����Ӳ����,510-���,520-��ľ,530-����,535-����,540-��ͩ,550-����,560-��˼,570-ľ���580-���,"+
				"590-����������,821-����,822-�Ͻ�������,823-��ͩ,824-����,825-���,826-��,827-������,828-˨Ƥ��,849-����";
		v1_DataBind.SetBindListSpinner(m_Context, "��Ҫ����",strShuZhong.split(","),R.id.a125);
		v1_DataBind.SetBindListSpinner(m_Context, "��Ҫ����",strShuZhong.split(","),R.id.a135);
		String gnqdj = ",1-���Ҽ�,2-ʡ��,3-�м�,4-�ؼ�";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",gnqdj.split(","),R.id.a139);
		String stqlx = ",1-��Ȼ������, 2-ɭ�ֹ�԰,3-ʪ�ع�԰,4-�羰��ʤ��,5-����";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",stqlx.split(","),R.id.a140);
		
		
		String stqgnq =",1-������,2-������,3-ʵ����,4-���ľ�����, 5-һ�������,6-��̬������,7-���������,8-���������� ,9�ָ��ؽ���,10-����չʾ��,11-����������,12-���������,13-��̬����������� ,14-�ر𱣻��� ,15-�羰������, 16-�羰�ָ���,17-��չ������ ,18-���η����������, 19-����";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",stqgnq.split(","),R.id.a141);
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",stqgnq.split(","),R.id.a143);
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",stqgnq.split(","),R.id.a145);
		
		String jcjg = ",1-һ��,2-��һ��,3-�ؼ����";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",jcjg.split(","),R.id.a155);
		((Spinner)mView.findViewById(R.id.a154)).setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0)
				{
					try
					{
						Tools.SetSpinnerValueOnID((Activity)m_Context, R.id.a155, "3-�ؼ����");
					}
					catch(Exception ex)
					{
						
					}
					
				}
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
			
		});

		String ccqk =",1û�в鴦,2���ڲ鴦,3�鴦��λ,4����鴦";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",ccqk.split(","),R.id.a147);
		String ccsd =",1���ǰ ,2����,3����ڼ�,4����鴦";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",ccsd.split(","),R.id.a148);
		String sfla =",1û������,2������,3��������";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",sfla.split(","),R.id.a149);
		String lasd =",1���ǰ, 2����,3����ڼ�,4��������";
		v1_DataBind.SetBindListSpinner(m_Context, "��̬���ȼ�",lasd.split(","),R.id.a150);
		
		String cffs=",111-�Է�,112-��,113-����,121-͸�ⷥ,122-����,123-�跥,124-������,131-��״����, 132-��״���� ,133-�ۺϸ���,141-н̿�ֲɷ�,142-�����ֲɷ� ,143-���ֲɷ� ,144-ɢ��ľ�ɷ�,145-�������ɷ� ,146-��������ɷ�,221-͸�ⷥ,222-���� ,223-�跥, 224-������,211-��״���� ,212-��״����,213-�񷥸���,231-��״���� ,232-��״����, 233-�ۺϸ���,243-���ֲɷ�,244-ɢ��ľ�ɷ�,245-�������ɷ�,246-��������ɷ�";
		v1_DataBind.SetBindListSpinner(m_Context, "�ɷ���ʽ",cffs.split(","),R.id.a116);
		v1_DataBind.SetBindListSpinner(m_Context, "�ɷ���ʽ",cffs.split(","),R.id.a126);
		
		
	}

	private List<SPValue> getSPList(String[] strList) {
		ArrayList<SPValue> result = new ArrayList<SPValue>();
		for (String value : strList) {
			result.add(new SPValue(value));
		}
		return result;
	}
	
	private void getTuBanHao()
	{
		
		v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayerId);
		if(pLayer == null)
		{
			return;
		}
		try
		{
			String alltuban="";
			String tbDataField = pLayer.GetDataFieldNameByFieldName("ͼ�ߺ�");
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(mLayerId);
			SQLiteDataReader reader = pDataset.getDataSource().Query("select "+tbDataField+" from "+pDataset.getDataTableName()+" where SYS_ID in("+mIds+")");
			while(reader.Read())
			{
				String tbh = reader.GetString(tbDataField);
				if(!alltuban.contains(tbh))
				{
					if(alltuban.length()>0)
					{
						alltuban += ","+tbh;
					}
					else
					{
						alltuban=tbh;
					}
				}
			}
			
			((EditText)mView.findViewById(R.id.a6)).setText(alltuban);
			
		}catch(Exception e)
		{
			
		}
		
		
		
		
	}

	
	
	// ������
	private boolean StartAnalysisPoly(List<Integer> tbIds,boolean isAdd) {
		// 1����ȡ�������������Ϣ����ʽ�����v1_UserConfigDB_PolyAnalysisOption
		v1_UserConfigDB_PolyAnalysisOption m_PAO = new v1_UserConfigDB_PolyAnalysisOption();
		List<HashMap<String, Object>> OptList = m_PAO.GetPolyAnalysisOption();

		// 2����ȡ��Ҫ���������
		List<HashMap<String, Object>> polyDatasetList = new ArrayList<HashMap<String, Object>>();
		for (HashMap<String, Object> Opt : OptList) {
			String LayerId = Opt.get("LayerId") + "";
			Dataset pDataset = PubVar.m_Workspace.GetDatasetById(LayerId);
			if (pDataset == null)
				continue;

			v1_Layer pLayer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(pDataset.getId());
			if (pLayer == null)
				pLayer = PubVar.m_DoEvent.m_ProjectDB.GetBKLayerExplorer().GetVectorLayerExplorer()
						.GetLayerByID(pDataset.getId());

			List<String> FieldList = (List<String>) Opt.get("FieldNameList");

			// �ж�ͳ���ֶ��Ƿ���Ч
			int FieldCount = FieldList.size();
			for (int i = FieldCount - 1; i >= 0; i--) {
				boolean Have = false;
				for (v1_LayerField LF : pLayer.GetFieldList()) {
					if (LF.GetDataFieldName().equals(FieldList.get(i)))
						Have = true;
				}
				if (!Have)
					FieldList.remove(i);
			}
			if (FieldList.size() == 0)
				FieldList.add("SYS_ID");
			// û��ͳ���ֶΣ�Ĭ����SYS_ID����ͳ��
			HashMap<String, Object> hmObj = new HashMap<String, Object>();
			hmObj.put("Dataset", pDataset);
			hmObj.put("FieldNameList", FieldList);
			polyDatasetList.add(hmObj);
		}

		// ���Ϊ0����ʾ��Ҫ���з�������
		if (polyDatasetList.size() == 0) {
			Tools.ShowYesNoMessage(m_Context, "û������ɭ�ֶ�������׼ͼ�㣬�뵽ͼ��->ͼ�����ù�ѡɭ�ֶ�������׼ͼ�㣿", new ICallback() {
				@Override
				public void OnClick(String Str, Object ExtraStr) {
					if (Str.equals("YES")) {
						
					} else {
					
					}
				}
			});
			
			return false;
		}

		return polyAnalysis(polyDatasetList,tbIds,isAdd);
		
	}

	private boolean polyAnalysis(List<HashMap<String, Object>> polyDatasetList,List<Integer> tbIds,boolean isAdd) {
		GeoLayers GeoLayerList = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll);
		GeoLayer pGeoLayer = GeoLayerList.GetLayerById(mLayerId);


		if (pGeoLayer.getType() == lkGeoLayerType.enPolygon) {

//			if (pGeoLayer.getSelSelection().getCount() == 0) {
//				return;
//			}
		
			HashMap<String, HashMap<String, Object>> m_AnalysisResultList = new HashMap<String, HashMap<String, Object>>();
			
			DecimalFormat df = new DecimalFormat("0.0000");
			double totalArea = 0d;
			List<String> listIds = Tools.StrArrayToList(mIds.split(","));
//			for (int idx : pGeoLayer.getSelSelection().getGeometryIndexList()) {
			for (Integer idx : tbIds) {

				Polygon _SelectPoly = (Polygon) pGeoLayer.getDataset().GetGeometry(idx);
				
				if(_SelectPoly == null)
				{
//					Tools.ShowMessageBox("����Ŀ/�����а���ͼ��δ��ͼ����ʾ���޷�׼ȷ��������������");
//					return false;
					continue;
				}
				

				// 4����ʾ��ѡ��������
				// double SelectPolyArea = _SelectPoly.getArea(true);
				totalArea += _SelectPoly.getArea(true);

				for (HashMap<String, Object> pDatasetInfo : polyDatasetList) {
					Dataset pDataset = (Dataset) pDatasetInfo.get("Dataset");
					List<String> FieldList = (List<String>) pDatasetInfo.get("FieldNameList");

					// ����ָ����㣬���ظ�ʽ��result["SYSID"],result["Area"]
					HashMap<String, Object> result = this.CalPolyLayer(pDataset, _SelectPoly);
					if (result.size() == 0)
						continue;
					List<String> SYSIDList = new ArrayList<String>();
					for (String id : result.keySet())
						SYSIDList.add(id);

					// ����������Ҫ�Ǹ�������ѡ���е��ֶ��н��з������
					HashMap<String, Object> STTypeList = new HashMap<String, Object>();
					String SQL = "select (%1$s) as STType,SYS_ID from %2$s where SYS_ID in (%3$s)";
					SQL = String.format(SQL, Tools.JoinT("||','||", FieldList), pDataset.getDataTableName(),
							Tools.JoinT(",", SYSIDList));
					SQLiteDataReader DR = pDataset.getDataSource().Query(SQL);
					if (DR != null)
						while (DR.Read()) {
							String STType = DR.GetString("STType"); // ���磺���أ�ˮ��
							String SYSID = DR.GetString("SYS_ID");
							double B = Double.parseDouble(result.get(SYSID) + ""); // �������

							if (STTypeList.containsKey(STType)) // �ۼ�
							{
								double A = Double.parseDouble(STTypeList.get(STType) + "");
								STTypeList.put(STType, A + B);
							} else {
								STTypeList.put(STType, B);
							}
						}
					DR.Close();
					m_AnalysisResultList.put(idx + "", STTypeList);

				}
			}
			if(!calcSumArea(m_AnalysisResultList,tbIds,isAdd))
			{
				return false;
			}
		}
		
		return true;

	}

	/*
	 * a35 a36 a37 a38 a39 a40 a41 a42 a43 a44 a45 a46 a47 a48 a49 a50 a51 a52
	 */

	private boolean calcSumArea(HashMap<String, HashMap<String, Object>> allResultList,List<Integer> tbIds,boolean isAdd) {
		Double a34 =new Double(0),a35 = new Double(0), a36 = new Double(0), a37 = new Double(0), a38 = new Double(0), a39 = new Double(0), 
				a40 = new Double(0), a41 = new Double(0), a42 =  new Double(0), a43 =  new Double(0), a44 =  new Double(0), 
				a45 =  new Double(0),a46 =  new Double(0), a47 =  new Double(0), a48 =  new Double(0), a49 =  new Double(0),
				a50 =  new Double(0), a51 =  new Double(0), a52 =  new Double(0), a117 =  new Double(0), a118 =  new Double(0), 
				a121 =  new Double(0), a122 =  new Double(0),a127 =  new Double(0), a128 =  new Double(0), a131 =  new Double(0), 
				a132 =  new Double(0);
		double fldArea = 0;
		
		int addOR =1;
		if(!isAdd)
		{
			addOR=-1;
		}
		
//		Double[] allValues=new Double[]{a34,a35,a36,a37,a38,a39,a40,a41,a42,a43,a44,a45,a46,a47,a48,a49,a50,
//				a51,a52,a117,a118,a121,a122,a127,a128,a131,a132};
		ArrayList<Object> allValues = new ArrayList<Object>();
		
		try{
			a34 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a34))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a35 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a35))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a36 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a36))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a37 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a37))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a38 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a38))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a39 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a39))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a40 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a40))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a41 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a41))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a42 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a42))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}

		try{
			a43 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a43))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a44 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a44))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a45 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a45))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a46 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a46))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a47 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a47))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a48 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a48))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a49 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a49))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a50 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a50))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a51 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a51))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a52 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a52))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a117 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a117))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a118 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a118));
		}
		catch(NumberFormatException exallETIds)
		{
			
		}
		
		try{
			a121 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a121))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a122 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a122));
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a127 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a127))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a128 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a128));
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a131 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a131))*10000;
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		try{
			a132 = Double.valueOf(Tools.GetTextValueOnID(mView, R.id.a132));
		}
		catch(NumberFormatException ex)
		{
			
		}
		
		int[] allETIds = new int[]{R.id.a34,R.id.a35,R.id.a36,R.id.a37,R.id.a38,R.id.a39,R.id.a40,R.id.a41,R.id.a42,R.id.a43,R.id.a44,R.id.a45,
				R.id.a46,R.id.a47,R.id.a48,R.id.a49,R.id.a50,R.id.a51,R.id.a52,R.id.a117,R.id.a118,R.id.a121,R.id.a122,
				R.id.a127,R.id.a128,R.id.a131,R.id.a132};

//		for(int i=0;i<allETIds.length;i++)
//		{
//			String valueI = Tools.GetTextValueOnID(mView, allETIds[i]);
//			if(valueI.length()==0||valueI.equals("null"))
//			{
//				
//			}
//			else
//			{
//				Double value = (Double)allValues.get(i);
//				value = Double.valueOf(valueI);
//			}
//		}
		
		allArea = a34;
				
		DecimalFormat df2 = new DecimalFormat("0.0000");
		df2.setRoundingMode(RoundingMode.HALF_UP);
		DecimalFormat dfXJ = new DecimalFormat("0.0");

		GeoLayers GeoLayerList = PubVar.m_Map.getGeoLayers(lkGeoLayersType.enAll);
		GeoLayer pGeoLayer = GeoLayerList.GetLayerById(mLayerId);
		v1_Layer layer = PubVar.m_DoEvent.m_ProjectDB.GetLayerExplorer().GetLayerByID(mLayerId);
		String wfwgDataField = layer.GetDataFieldNameByFieldName("�Ƿ�Υ��Υ��");
		String bhyyDataField = layer.GetDataFieldNameByFieldName("�仯ԭ��");
		HashMap<String, String> hmYZTB = new HashMap<String, String>();
		SQLiteDataReader reader = pGeoLayer.getDataset().getDataSource()
				.Query("select SYS_ID," + wfwgDataField + "," + bhyyDataField + " from "
						+ pGeoLayer.getDataset().getDataTableName() + " where SYS_ID in(" + Tools.IntListToStr(tbIds) + ")");
		while (reader.Read()) {

			int id = reader.GetInt32("SYS_ID");
			String yy = reader.GetString(bhyyDataField);
			int wfwg = reader.GetInt32(wfwgDataField);

			hmYZTB.put(id + "", wfwg + "," + yy);
		}
		
	
		
		for (String sysId : allResultList.keySet()) {
			HashMap<String, Object> resultList = allResultList.get(sysId);

			if (resultList.keySet().size() > 0) {
				for (String key : resultList.keySet()) {
					double area = Double.parseDouble(resultList.get(key) + "")*addOR;
					allArea += area;

					if (key.endsWith(",")) {
						key += " ";
					}
					String[] Keys = key.split(",");

					if (Keys.length < 9) {

						Tools.ShowMessageBox("������������ֶ�����������LD_QS,DI_LEI,SEN_LIN_LB,SHI_QUAN_D,GJGYL_BHDJ,BH_DJ,QI_YUAN,HUO_LMGQXJ,YOU_SHI_SZ�Ÿ��ֶ�.");
						return false;
					}

					String xbsx = hmYZTB.get(sysId);
					if(xbsx == null)
					{
						Tools.ShowMessageBox("�����Ƚ���ͼ����֤��");
						clearUIValues();
						closeView();
						return false;
					}
					String sfwf = xbsx.split(",")[0];
					
					if(xbsx.split(",").length==1)
					{
						Tools.ShowMessageBox("�����Ƚ���ͼ����֤��");
						clearUIValues();
						closeView();
						return false;
					}
					if (!xbsx.split(",")[1].equals("5-��ľ�ɷ�")) {

						if (Keys[5] != null && !Keys[5].isEmpty()) {
							if (Keys[5].equals("1")) {
								a49 += area;
								
								((EditText) mView.findViewById(R.id.a49)).setText(df2.format(a49 / 10000));
								
//								a49 = Double.parseDouble(df2.format(a49 / 10000));//������֤��
							} else if (Keys[5].equals("2")) {
								a50 += area;
								((EditText) mView.findViewById(R.id.a50)).setText(df2.format(a50 / 10000));
								
//								a50 = Double.parseDouble(df2.format(a50 / 10000));//������֤��
							} else if (Keys[5].equals("3")) {
								a51 += area;
								((EditText) mView.findViewById(R.id.a51)).setText(df2.format(a51 / 10000));
								
//								a51 = Double.parseDouble(df2.format(a51 / 10000));//������֤��
							} else if (Keys[5].equals("4")) {
								a52 += area;
								((EditText) mView.findViewById(R.id.a52)).setText(df2.format(a52 / 10000));
								
//								a52 = Double.parseDouble(df2.format(a52 / 10000));//������֤��
							}
						}

						// ��Ȩ�ȼ� 10.���ҹ����� 20.�ط�������
						if (Keys[3] != null && !Keys[3].isEmpty()) {
							if (Keys[3].equals("10")) {
								if (Keys[4] != null && !Keys[4].isEmpty()) {
									if (Keys[4].equals("1")) {
										a45 += area;
										((EditText) mView.findViewById(R.id.a45)).setText(df2.format(a45 / 10000));
//										a45 = Double.parseDouble(df2.format(a45 / 10000));//������֤��
										if(a45<0.5&&a45>-2)
										{
											a45=new Double(0);
											((EditText) mView.findViewById(R.id.a45)).setText("");
										}
									} else if (Keys[4].equals("2")) {
										a46 += area;
										((EditText) mView.findViewById(R.id.a46)).setText(df2.format(a46 / 10000));
//										a46 = Double.parseDouble(df2.format(a46 / 10000));//������֤��
									} else if (Keys[4].equals("3"))// ԭ���ҹ����ֵȼ���3���Ĳ���2��
									{
										a46 += area;
										((EditText) mView.findViewById(R.id.a46)).setText(df2.format(a46 / 10000));
//										a46 = Double.parseDouble(df2.format(a46 / 10000));//������֤��
									}
								}
							} else if (Keys[3].equals("20")) {
								a47 += area;
								((EditText) mView.findViewById(R.id.a47)).setText(df2.format(a47 / 10000));
//								a47 = Double.parseDouble(df2.format(a47 / 10000));//������֤��
							}
						} else {
							if (Keys[2] != null && !Keys[2].isEmpty())// ��Ʒ��
							{
								if (Keys[2].startsWith("02") || Keys[2].startsWith("2")) {
									a48 += area;
									((EditText) mView.findViewById(R.id.a48)).setText(df2.format(a48 / 10000));
//									a48 = Double.parseDouble(df2.format(a48 / 10000));//������֤��
								}
							}
						}

						// ����
						if (Keys[1] != null && !Keys[1].isEmpty()) {
							if (Keys[1].startsWith("11") || Keys[1].startsWith("011")) {
								a35 += area;
								((EditText) mView.findViewById(R.id.a35)).setText(df2.format(a35 / 10000));
//								a35 = Double.parseDouble(df2.format(a35 / 10000));//������֤��

								// ���ֵ����κ�����¶�������ľ�ɷ�
								if (sfwf.equals("1"))// Υ��
								{
									if (Keys[6].startsWith("1"))// ��Ȼ
									{
										a131 += area;
										((EditText) mView.findViewById(R.id.a131)).setText(df2.format(a131 / 10000));
										
										fillShuZhong(false,Keys[8]);
										String huo_LMGQXJ = Keys[7];
										try {

											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a132 += xj;
											((EditText) mView.findViewById(R.id.a132)).setText(dfXJ.format(a132));
										} catch (Exception ex) {
											Log.e("Υ����Ȼ���", ex.getMessage());
										}
									} else if (Keys[6].startsWith("2"))// �˹�
									{
										a127 += area;
										((EditText) mView.findViewById(R.id.a127)).setText(df2.format(a127 / 10000));
										fillShuZhong(false,Keys[8]);
										
										String huo_LMGQXJ = Keys[7];
										try {
											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a128 += xj;
											((EditText) mView.findViewById(R.id.a128)).setText(dfXJ.format(a128));
										} catch (Exception ex) {
											Log.e("Υ���˹����", ex.getMessage());
										}
									}
								} else// ��Υ��
								{
									if (Keys[6].startsWith("1"))// ��Ȼ
									{
										a121 += area;
										((EditText) mView.findViewById(R.id.a121)).setText(df2.format(a121 / 10000));

										fillShuZhong(true,Keys[8]);
										
										String huo_LMGQXJ = Keys[7];
										try {
											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a122 += xj;
											((EditText) mView.findViewById(R.id.a122)).setText(dfXJ.format(a122));
										} catch (Exception ex) {
											Log.e("ƾ֤��Ȼ���", ex.getMessage());
										}
									} else if (Keys[6].startsWith("2"))// �˹�
									{
										a117 += area;
										((EditText) mView.findViewById(R.id.a117)).setText(df2.format(a117 / 10000));

										fillShuZhong(true,Keys[8]);
										
										String huo_LMGQXJ = Keys[7];
										try {
											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a118 += xj;
											((EditText) mView.findViewById(R.id.a118)).setText(dfXJ.format(a118));
										} catch (Exception ex) {
											Log.e("ƾ֤�˹����", ex.getMessage());
										}
									}
								}

							} else if (Keys[1].startsWith("12") || Keys[1].startsWith("012")) {
								a36 += area;
								((EditText) mView.findViewById(R.id.a36)).setText(df2.format(a36 / 10000));
//								a36 = Double.parseDouble(df2.format(a36 / 10000));//������֤��

								// ���ֵ����κ�����¶�������ľ�ɷ�
								if (sfwf.equals("1"))// Υ��
								{
									if (Keys[6].startsWith("1"))// ��Ȼ
									{
										a131 += area;
										((EditText) mView.findViewById(R.id.a131)).setText(df2.format(a131 / 10000));

										fillShuZhong(false,Keys[8]);
										
										String huo_LMGQXJ = Keys[7];
										try {
											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a132 += xj;
											((EditText) mView.findViewById(R.id.a132)).setText(dfXJ.format(a132));
										} catch (Exception ex) {
											Log.e("Υ����Ȼ���", ex.getMessage());
										}
									} else if (Keys[6].startsWith("2"))// �˹�
									{
										a127 += area;
										((EditText) mView.findViewById(R.id.a127)).setText(df2.format(a127 / 10000));
										fillShuZhong(false,Keys[8]);
										
										String huo_LMGQXJ = Keys[7];
										try {
											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a128 += xj;
											((EditText) mView.findViewById(R.id.a128)).setText(dfXJ.format(a128));
										} catch (Exception ex) {
											Log.e("Υ���˹����", ex.getMessage());
										}
									}
								} else// ��Υ��
								{
									if (Keys[6].startsWith("1"))// ��Ȼ
									{
										a121 += area;
										((EditText) mView.findViewById(R.id.a121)).setText(df2.format(a121 / 10000));

										fillShuZhong(true,Keys[8]);
										
										String huo_LMGQXJ = Keys[7];
										try {
											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a122 += xj;
											((EditText) mView.findViewById(R.id.a122)).setText(dfXJ.format(a122));
										} catch (Exception ex) {
											Log.e("ƾ֤��Ȼ���", ex.getMessage());
										}
									} else if (Keys[6].startsWith("2"))// �˹�
									{
										a117 += area;
										((EditText) mView.findViewById(R.id.a117)).setText(df2.format(a117 / 10000));

										fillShuZhong(true,Keys[8]);
										
										String huo_LMGQXJ = Keys[7];
										try {
											double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
											a118 += xj;
											((EditText) mView.findViewById(R.id.a118)).setText(dfXJ.format(a118));
										} catch (Exception ex) {
											Log.e("ƾ֤�˹����", ex.getMessage());
										}
									}
								}

							} else if (Keys[1].equals("131") || Keys[1].equals("0131")) {
								a37 += area;
								((EditText) mView.findViewById(R.id.a37)).setText(df2.format(a37 / 10000));
//								a37 = Double.parseDouble(df2.format(a37 / 10000));//������֤��
							} else if (Keys[1].equals("132") || Keys[1].equals("0132")) {
								a38 += area;
								((EditText) mView.findViewById(R.id.a38)).setText(df2.format(a38 / 10000));
//								a38 = Double.parseDouble(df2.format(a38 / 10000));//������֤��
							} else if (Keys[1].startsWith("14") || Keys[1].startsWith("014")) {
								a39 += area;
								((EditText) mView.findViewById(R.id.a39)).setText(df2.format(a39 / 10000));
//								a39 = Double.parseDouble(df2.format(a39 / 10000));//������֤��
							} else if (Keys[1].startsWith("15") || Keys[1].startsWith("015")) {
								a40 += area;
								((EditText) mView.findViewById(R.id.a40)).setText(df2.format(a40 / 10000));
//								a40 = Double.parseDouble(df2.format(a40 / 10000));//������֤��
							} else if (Keys[1].startsWith("17") || Keys[1].startsWith("017")) {
								a41 += area;
								((EditText) mView.findViewById(R.id.a41)).setText(df2.format(a41 / 10000));
//								a41 = Double.parseDouble(df2.format(a41 / 10000));//������֤��
							} else if (Keys[1].startsWith("1") || Keys[1].startsWith("01")) {
								a42 += area;
								((EditText) mView.findViewById(R.id.a42)).setText(df2.format(a42 / 10000));
//								a42 = Double.parseDouble(df2.format(a42 / 10000));//������֤��
							} else if (Keys[1].startsWith("2") || Keys[1].startsWith("02")) {
								fldArea += area;

							}

						}

						if (Keys[0] != null && !Keys[0].isEmpty()) {
							if (Keys[0].equals("10")) {
								a43 += area;
								((EditText) mView.findViewById(R.id.a43)).setText(df2.format(a43 / 10000));
//								a43 = Double.parseDouble(df2.format(a43 / 10000));//������֤��
							} else if (Keys[0].startsWith("2") || Keys[0].startsWith("02")) {
								a44 += area;
								((EditText) mView.findViewById(R.id.a44)).setText(df2.format(a44 / 10000));
//								a44 = Double.parseDouble(df2.format(a44 / 10000));//������֤��
							}
						}
						
//						((EditText) mView.findViewById(R.id.a34)).setText(df2.format(allArea/10000));
						
						((EditText) mView.findViewById(R.id.a34)).setText(df2.format((allArea)/10000));
//						allArea = Double.parseDouble(Tools.GetTextValueOnID(mView, R.id.a34))*10000;
						
					} else// �仯ԭ������ľ�ɷ�
					{
						if (sfwf.equals("1"))// Υ��
						{
							if (Keys[6].startsWith("1"))// ��Ȼ
							{
								a131 += area;
								((EditText) mView.findViewById(R.id.a131)).setText(df2.format(a131 / 10000));

								fillShuZhong(false,Keys[8]);
								
								String huo_LMGQXJ = Keys[7];
								try {

									double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
									a132 += xj;
									((EditText) mView.findViewById(R.id.a132)).setText(dfXJ.format(a132));
								} catch (Exception ex) {
									Log.e("Υ����Ȼ���", ex.getMessage());
								}
							} else if (Keys[6].startsWith("2"))// �˹�
							{
								a127 += area;
								((EditText) mView.findViewById(R.id.a127)).setText(df2.format(a127 / 10000));
								fillShuZhong(false,Keys[8]);
								
								String huo_LMGQXJ = Keys[7];
								try {
									double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
									a128 += xj;
									((EditText) mView.findViewById(R.id.a128)).setText(dfXJ.format(a128));
								} catch (Exception ex) {
									Log.e("Υ���˹����", ex.getMessage());
								}
							}
						} else// ��Υ��
						{
							if (Keys[6].startsWith("1"))// ��Ȼ
							{
								a121 += area;
								((EditText) mView.findViewById(R.id.a121)).setText(df2.format(a121 / 10000));
								fillShuZhong(true,Keys[8]);
								
								String huo_LMGQXJ = Keys[7];
								try {
									double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
									a122 += xj;
									((EditText) mView.findViewById(R.id.a122)).setText(dfXJ.format(a122));
								} catch (Exception ex) {
									Log.e("ƾ֤��Ȼ���", ex.getMessage());
								}
							} else if (Keys[6].startsWith("2"))// �˹�
							{
								a117 += area;
								((EditText) mView.findViewById(R.id.a117)).setText(df2.format(a117 / 10000));

								fillShuZhong(true,Keys[8]);
								
								String huo_LMGQXJ = Keys[7];
								try {
									double xj = Double.parseDouble(huo_LMGQXJ) * area / 10000;
									a118 += xj;
									((EditText) mView.findViewById(R.id.a118)).setText(dfXJ.format(a118));
								} catch (Exception ex) {
									Log.e("ƾ֤�˹����", ex.getMessage());
								}
							}
						}
					}

				}
			}
		}
		
		
		
		
		
	
	
		
		mLeftArea = Double.parseDouble(df2.format(fldArea / 10000)) ;
		
		if (fldArea >= 1) {
			((TextView) mView.findViewById(R.id.tv_djmjhj)).setText("�ϼ�\n���ֵ�:" + df2.format(fldArea / 10000));
			((TextView) mView.findViewById(R.id.tv_djmjhj)).setTextColor(Color.RED);
			
			mView.findViewById(R.id.tv_djmjhj).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					AlertDialog.Builder splitAreaDialog = new AlertDialog.Builder(m_Context);
					splitAreaDialog.setCancelable(true);
					splitAreaDialog.setTitle("����δͳ�Ʒ��ֵ����");

					LayoutInflater inflater = ((Activity) m_Context).getLayoutInflater();
					final View layout = inflater.inflate(R.layout.layout_checkcard_splitarea, null);
					splitAreaDialog.setView(layout);
					final Spinner sp_dilei = (Spinner) layout.findViewById(R.id.sp_dilei);

					String[] arrRoundType = "���ֵ�,���ֵ�,���ع�,������ľ,δ����,���Ե�,���ֵ�,�����ֵ�".split(",");
					ArrayAdapter<String> roundTypeAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
							android.R.layout.simple_spinner_item, arrRoundType);
					roundTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_dilei.setAdapter(roundTypeAdapter);
					DecimalFormat df = new DecimalFormat("0.0000");

					String strArea = df.format(mLeftArea);
					((EditText) layout.findViewById(R.id.et_allarea)).setText(strArea);
					final EditText etSpileArea = ((EditText) layout.findViewById(R.id.et_splitarea));
					etSpileArea.setText(strArea);

					((EditText) layout.findViewById(R.id.et_dileiarea)).setText(strArea);
					((EditText) layout.findViewById(R.id.et_sllbarea)).setText(strArea);
					((EditText) layout.findViewById(R.id.et_bhdjarea)).setText(strArea);

					final Spinner sp_quanshu = (Spinner) layout.findViewById(R.id.sp_quanshu);
					String[] arrQuanShu = "����,����".split(",");
					ArrayAdapter<String> quanshuAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
							android.R.layout.simple_spinner_item, arrQuanShu);
					quanshuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_quanshu.setAdapter(quanshuAdapter);

					final Spinner sp_sllb = (Spinner) layout.findViewById(R.id.sp_sllb);
					String[] arrLeibie = "����һ��������,���Ҷ���������,�ط�������,��Ʒ��".split(",");
					ArrayAdapter<String> leibieAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
							android.R.layout.simple_spinner_item, arrLeibie);
					leibieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_sllb.setAdapter(leibieAdapter);

					final Spinner sp_bhdj = (Spinner) layout.findViewById(R.id.sp_bhdj);
					String[] arrDengJi = "һ��,����,����,�ļ�,".split(",");
					ArrayAdapter<String> bhdjAdapter = new ArrayAdapter<String>(PubVar.m_DoEvent.m_Context,
							android.R.layout.simple_spinner_item, arrDengJi);
					bhdjAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sp_bhdj.setAdapter(bhdjAdapter);

					splitAreaDialog.setPositiveButton("����", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							double splitMJ = 0;
							try {
								splitMJ = Double.parseDouble(((TextView) etSpileArea).getText().toString());
								if (splitMJ > mLeftArea) {
									Tools.ShowMessageBox("���η���������ܴ��ڷ��ֵ������");
									return;
								}

							} catch (Exception ex) {
								Tools.ShowMessageBox("���η��������д����");
							}

							fillArea(splitMJ, sp_dilei.getSelectedItem().toString(),
									sp_sllb.getSelectedItem().toString(), sp_bhdj.getSelectedItem().toString(),
									(mLeftArea - splitMJ));

							dialog.dismiss();

						}
					});
					splitAreaDialog.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});

					splitAreaDialog.show();
				}
			});
		} else {

			mView.findViewById(R.id.tv_djmjhj).setOnClickListener(null);
		}
		
		if(a35<1.5&&a35>-1)
		{
			a35=new Double(0);
			((EditText) mView.findViewById(R.id.a35)).setText("");
		}
		if(a36<1.5&&a36>-1)
		{
			a36=new Double(0);
			((EditText) mView.findViewById(R.id.a36)).setText("");
		}
		if(a37<1.5&&a37>-1)
		{
			a37=new Double(0);
			((EditText) mView.findViewById(R.id.a37)).setText("");
		}
		if(a38<1.5&&a38>-1)
		{
			a38=new Double(0);
			((EditText) mView.findViewById(R.id.a38)).setText("");
		}
		if(a39<1.5&&a39>-1)
		{
			a39=new Double(0);
			((EditText) mView.findViewById(R.id.a39)).setText("");
		}
		if(a40<1.5&&a40>-1)
		{
			a40=new Double(0);
			((EditText) mView.findViewById(R.id.a40)).setText("");
		}
		if(a41<1.5&&a41>-1)
		{
			a41=new Double(0);
			((EditText) mView.findViewById(R.id.a41)).setText("");
		}
		if(a42<1.5&&a42>-1)
		{
			a42=new Double(0);
			((EditText) mView.findViewById(R.id.a42)).setText("");
		}
		
		a35 = Double.parseDouble(df2.format(a35 / 10000));
		a36 = Double.parseDouble(df2.format(a36 / 10000));
		a37 = Double.parseDouble(df2.format(a37 / 10000));
		a38 = Double.parseDouble(df2.format(a38 / 10000));
		a39 = Double.parseDouble(df2.format(a39 / 10000));
		a40 = Double.parseDouble(df2.format(a40 / 10000));
		a41 = Double.parseDouble(df2.format(a41 / 10000));
		a42 = Double.parseDouble(df2.format(a42 / 10000));
		
		allArea = Double.parseDouble(df2.format(allArea/10000));
		
		if(allArea>-0.00015 && allArea<0.00015)
		{
			allArea=new Double(0);
			((EditText) mView.findViewById(R.id.a34)).setText("");
			
		}
		
		double dileiAll = a35+a36+a37+a38+a39+a40+a41+a42+mLeftArea;
		if(((allArea-dileiAll)>0.00009 && (allArea-dileiAll)<0.0005)||((allArea-dileiAll)<-0.00005 && (allArea-dileiAll)>-0.0002))
		{
			int maxLei = 35;
			double maxValue = a35;
			
			maxLei=maxValue>a36?maxLei:36;
			maxValue = maxValue>a36?maxValue:a36;
			
			maxLei=maxValue>a37?maxLei:37;
			maxValue = maxValue>a37?maxValue:a37;
			
			maxLei=maxValue>a38?maxLei:38;
			maxValue = maxValue>a38?maxValue:a38;
			
			
			maxLei=maxValue>a39?maxLei:39;
			maxValue = maxValue>a39?maxValue:a39;
			
			maxLei=maxValue>a40?maxLei:40;
			maxValue = maxValue>a40?maxValue:a40;
			
			maxLei=maxValue>a41?maxLei:41;
			maxValue = maxValue>a41?maxValue:a41;
			
			maxLei=maxValue>a42?maxLei:42;
			maxValue = maxValue>a42?maxValue:a42;
			
			switch(maxLei)
			{
			case 35:
				a35 = a35+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a35)).setText(df2.format(a35));
				break;
			case 36:
				a36 = a36+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a36)).setText(df2.format(a36));
				break;
			case 37:
				a37 = a37+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a37)).setText(df2.format(a37));
				break;
			case 38:
				a38 = a38+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a38)).setText(df2.format(a38));
				break;
			case 39:
				a39 = a39+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a39)).setText(df2.format(a39));
				break;
			case 40:
				a40 = a40+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a40)).setText(df2.format(a40));
				break;
			case 41:
				a41 = a41+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a41)).setText(df2.format(a41));
				break;
			case 42:
				a42 = a42+(allArea-dileiAll);
				((EditText) mView.findViewById(R.id.a42)).setText(df2.format(a42));
				break;
			}
		}
		
		if(a45<1.5&&a45>-1)
		{
			a45=new Double(0);
			((EditText) mView.findViewById(R.id.a45)).setText("");
		}
		if(a46<1.5&&a46>-1)
		{
			a46=new Double(0);
			((EditText) mView.findViewById(R.id.a46)).setText("");
		}
		if(a47<1.5&&a47>-1)
		{
			a47=new Double(0);
			((EditText) mView.findViewById(R.id.a47)).setText("");
		}
		if(a48<1.5&&a48>-1)
		{
			a48=new Double(0);
			((EditText) mView.findViewById(R.id.a48)).setText("");
		}
		a45 = Double.parseDouble(df2.format(a45 / 10000));
		a46 = Double.parseDouble(df2.format(a46 / 10000));
		a47 = Double.parseDouble(df2.format(a47 / 10000));
		a48 = Double.parseDouble(df2.format(a48 / 10000));
		double leibieAll = a45+a46+a47+a48+mLeftArea;
		if(((allArea-leibieAll)>0.00009 && (allArea-leibieAll)<0.0007)||((allArea-leibieAll)<-0.00005 && (allArea-leibieAll)>-0.0002))
		{
			int maxLei = 45;
			double maxValue = a45;
			
			maxLei=maxValue>a46?maxLei:46;
			maxValue = maxValue>a46?maxValue:a46;
			
			maxLei=maxValue>a47?maxLei:47;
			maxValue = maxValue>a47?maxValue:a47;
			
			maxLei=maxValue>a48?maxLei:48;
			maxValue = maxValue>a48?maxValue:a48;
			
			
			switch(maxLei)
			{
			case 45:
				a45 = a45+(allArea-leibieAll);
				((EditText) mView.findViewById(R.id.a45)).setText(df2.format(a45));
				break;
			case 46:
				a46 = a46+(allArea-leibieAll);
				((EditText) mView.findViewById(R.id.a46)).setText(df2.format(a46));
				break;
			case 47:
				a47 = a47+(allArea-leibieAll);
				((EditText) mView.findViewById(R.id.a47)).setText(df2.format(a47));
				break;
			case 48:
				a48 = a48+(allArea-leibieAll);
				((EditText) mView.findViewById(R.id.a48)).setText(df2.format(a48));
				break;
			}
		}
		
		if(a49<1.5&&a49>-1)
		{
			a49=new Double(0);
			((EditText) mView.findViewById(R.id.a49)).setText("");
		}
		if(a50<1.5&&a50>-1)
		{
			a50=new Double(0);
			((EditText) mView.findViewById(R.id.a50)).setText("");
		}
		if(a51<1.5&&a51>-1)
		{
			a51=new Double(0);
			((EditText) mView.findViewById(R.id.a51)).setText("");
		}
		if(a52<1.5&&a52>-1)
		{
			a52=new Double(0);
			((EditText) mView.findViewById(R.id.a52)).setText("");
		}
		a49 = Double.parseDouble(df2.format(a49 / 10000));
		a50 = Double.parseDouble(df2.format(a50 / 10000));
		a51 = Double.parseDouble(df2.format(a51 / 10000));
		a52 = Double.parseDouble(df2.format(a52 / 10000));
		double dengjiAll = a49+a50+a51+a52+mLeftArea;
		if(((allArea-dengjiAll)>0.00009 && (allArea-dengjiAll)<0.0007)||((allArea-dengjiAll)<-0.00005 && (allArea-dengjiAll)>-0.0002))
		{
			int maxLei = 49;
			double maxValue = a49;
			
			maxLei=maxValue>a50?maxLei:50;
			maxValue = maxValue>a50?maxValue:a50;
			
			maxLei=maxValue>a51?maxLei:51;
			maxValue = maxValue>a51?maxValue:a51;
			
			maxLei=maxValue>a52?maxLei:52;
			maxValue = maxValue>a52?maxValue:a52;
			
			switch(maxLei)
			{
			case 49:
				a49 = a49+(allArea-dengjiAll);
				((EditText) mView.findViewById(R.id.a49)).setText(df2.format(a49));
				break;
			case 50:
				a50 = a50+(allArea-dengjiAll);
				((EditText) mView.findViewById(R.id.a50)).setText(df2.format(a50));
				break;
			case 51:
				a51 = a51+(allArea-dengjiAll);
				((EditText) mView.findViewById(R.id.a51)).setText(df2.format(a51));
				break;
			case 52:
				a52 = a52+(allArea-dengjiAll);
				((EditText) mView.findViewById(R.id.a52)).setText(df2.format(a52));
				break;
			}
		}
		
		
		
		if(a43<-0.00001||(a43>0.00001&& a43<0.00015))
		{
			a43=new Double(0);
			((EditText) mView.findViewById(R.id.a43)).setText("");
			
		}
		if(a44<-0.00001||(a44>0.00001&& a44<0.00015))
		{
			a44=new Double(0);
			((EditText) mView.findViewById(R.id.a44)).setText("");
		}
		
		if(a117<-0.00001||(a117>0.00001&& a117<0.00015))
		{
			a117=new Double(0);
			((EditText) mView.findViewById(R.id.a117)).setText("");
			
		}
		if(a121<-0.00001||(a121>0.00001&& a121<0.00015))
		{
			a121=new Double(0);
			((EditText) mView.findViewById(R.id.a121)).setText("");
			
		}
		if(a127<-0.00001||(a127>0.00001&& a127<0.00015))
		{
			a127=new Double(0);
			((EditText) mView.findViewById(R.id.a127)).setText("");
			
		}
		if(a131<-0.00001||(a131>0.00001&& a131<0.00015))
		{
			a131=new Double(0);
			((EditText) mView.findViewById(R.id.a131)).setText("");
		}
		
		if(a118<-0.000001||(a118>0.000001&& a118<0.1))
		{
			a118=new Double(0);
			((EditText) mView.findViewById(R.id.a118)).setText("");
		}
		if(a122<-0.000001||(a122>0.000001&& a122<0.1))
		{
			a122=new Double(0);
			((EditText) mView.findViewById(R.id.a122)).setText("");
		}
		if(a128<-0.000001||(a128>0.000001&& a128<0.1))
		{
			a128=new Double(0);
			((EditText) mView.findViewById(R.id.a128)).setText("");
		}
		if(a132<-0.000001||(a132>0.000001&& a132<0.1))
		{
			a132=new Double(0);
			((EditText) mView.findViewById(R.id.a132)).setText("");
		}
		
		allArea=allArea*10000;
		
		
		
		return true;

	}

	private void fillShuZhong(boolean isHefa,String strSZ)
	{
		if(strSZ == null || strSZ.length()==0)
		{
			return;
		}
		
		String[] arryShuZhong = ("110-��ɼ,120-��ɼ,130-��ɼ,140-��ɼ,150-��Ҷ��,160����,170-������,180-����,190-����,"+
				"200-����,210-��ɽ��,220-��β��,230-������,240-˼é��,250-��ɽ��2,60-������,261-ʪ����,262-�����,270-��ɽ��,"+
				"280-����,290-��������,310-ɼľ,320-��ɼ,330-ˮɼ,340-��ɼ,350-����,360-��ɼ(�춹ɼ),390-����ɼ��,410-����,"+
				"420-��ľ,421-����,422-����,431-ˮ����,432-�����,433-�Ʋ���,440-��ľ,450-�ľ,460-����,465-�̻�,470-ľ��,"+
				"480-����,490-����Ӳ����,510-���,520-��ľ,530-����,535-����,540-��ͩ,550-����,560-��˼,570-ľ���580-���,"+
				"590-����������,821-����,822-�Ͻ�������,823-��ͩ,824-����,825-���,826-��,827-������,828-˨Ƥ��,849-����").split(",");
		
		if(strSZ.endsWith("000"))
		{
			strSZ = strSZ.replace("000", "");
		}
		
		if(isHefa)
		{
			
			for(String sz:arryShuZhong)
			{
				if(sz.contains(strSZ))
				{
					Tools.SetSpinnerValueOnID((Activity)m_Context, R.id.a125, sz);
				}
			}
		}
		else
		{
			for(String sz:arryShuZhong)
			{
				if(sz.contains(strSZ))
				{
					Tools.SetSpinnerValueOnID((Activity)m_Context, R.id.a135, sz);
				}
			}
		}
	}
	
	private void fillArea(double mj, String dilei, String sllb, String dj, double leftMJ) {

		DecimalFormat df2 = new DecimalFormat("0.0000");
		if (dilei.equals("���ֵ�")) {
			String strA35 = ((TextView) mView.findViewById(R.id.a35)).getText().toString();
			if (strA35 != null && !strA35.isEmpty()) {
				try {
					double a35 = Double.parseDouble(strA35);
					((EditText) mView.findViewById(R.id.a35)).setText(df2.format((a35 + mj)));
				} catch (Exception ex) {
//					 TODO:
					 Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a35)).setText(df2.format((mj)));
			}

		} else if (dilei.equals("���ֵ�")) {

			String strA36 = ((TextView) mView.findViewById(R.id.a36)).getText().toString();
			if (strA36 != null && !strA36.isEmpty()) {
				try {
					double a36 = Double.parseDouble(strA36);
					((EditText) mView.findViewById(R.id.a36)).setText(df2.format((a36 + mj)));
				} catch (Exception ex) {
					// TODO:
					 Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a36)).setText(df2.format((mj)));
			}

		} else if (dilei.equals("���ع�")) {

			String strA37 = ((TextView) mView.findViewById(R.id.a37)).getText().toString();
			if (strA37 != null && !strA37.isEmpty()) {
				try {
					double a37 = Double.parseDouble(strA37);
					((EditText) mView.findViewById(R.id.a37)).setText(df2.format((a37 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a37)).setText(df2.format((mj)));
			}

		} else if (dilei.equals("������ľ")) {

			String strA38 = ((TextView) mView.findViewById(R.id.a38)).getText().toString();
			if (strA38 != null && !strA38.isEmpty()) {
				try {
					double a38 = Double.parseDouble(strA38);
					((EditText) mView.findViewById(R.id.a38)).setText(df2.format((a38 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a38)).setText(df2.format((mj)));
			}

		} else if (dilei.equals("δ����")) {

			String strA39 = ((TextView) mView.findViewById(R.id.a39)).getText().toString();
			if (strA39 != null && !strA39.isEmpty()) {
				try {
					double a39 = Double.parseDouble(strA39);
					((EditText) mView.findViewById(R.id.a39)).setText(df2.format((a39 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a39)).setText(df2.format((mj)));
			}

		} else if (dilei.equals("���Ե�")) {
			String strA40 = ((TextView) mView.findViewById(R.id.a40)).getText().toString();
			if (strA40 != null && !strA40.isEmpty()) {
				try {
					double a40 = Double.parseDouble(strA40);
					((EditText) mView.findViewById(R.id.a40)).setText(df2.format((a40 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a40)).setText(df2.format((mj)));
			}

		} else if (dilei.equals("���ֵ�")) {
			String strA41 = ((TextView) mView.findViewById(R.id.a41)).getText().toString();
			if (strA41 != null && !strA41.isEmpty()) {
				try {
					double a41 = Double.parseDouble(strA41);
					((EditText) mView.findViewById(R.id.a41)).setText(df2.format((a41 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a41)).setText(df2.format((mj)));
			}

		} else if (dilei.equals("�����ֵ�")) {
			String strA42 = ((TextView) mView.findViewById(R.id.a42)).getText().toString();
			if (strA42 != null && !strA42.isEmpty()) {
				try {
					double a42 = Double.parseDouble(strA42);
					((EditText) mView.findViewById(R.id.a42)).setText(df2.format((a42 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a42)).setText(df2.format((mj)));
			}
		}

		if (sllb.equals("����һ��������")) {
			String strA45 = ((TextView) mView.findViewById(R.id.a45)).getText().toString();
			if (strA45 != null && !strA45.isEmpty()) {
				try {
					double a45 = Double.parseDouble(strA45);
					((EditText) mView.findViewById(R.id.a45)).setText(df2.format((a45 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a45)).setText(df2.format((mj)));
			}

		} else if (sllb.equals("���Ҷ���������")) {
			String strA46 = ((TextView) mView.findViewById(R.id.a46)).getText().toString();
			if (strA46 != null && !strA46.isEmpty()) {
				try {
					double a46 = Double.parseDouble(strA46);
					((EditText) mView.findViewById(R.id.a46)).setText(df2.format((a46 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a46)).setText(df2.format((mj)));
			}
		} else if (sllb.equals("�ط�������")) {
			String strA47 = ((TextView) mView.findViewById(R.id.a47)).getText().toString();
			if (strA47 != null && !strA47.isEmpty()) {
				try {
					double a47 = Double.parseDouble(strA47);
					((EditText) mView.findViewById(R.id.a47)).setText(df2.format((a47 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a47)).setText(df2.format((mj)));
			}
		} else if (sllb.equals("��Ʒ��")) {
			String strA48 = ((TextView) mView.findViewById(R.id.a48)).getText().toString();
			if (strA48 != null && !strA48.isEmpty()) {
				try {
					double a48 = Double.parseDouble(strA48);
					((EditText) mView.findViewById(R.id.a48)).setText(df2.format((a48 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a48)).setText(df2.format((mj)));
			}
		}

		if (dj.equals("һ��")) {
			String strA49 = ((TextView) mView.findViewById(R.id.a49)).getText().toString();
			if (strA49 != null && !strA49.isEmpty()) {
				try {
					double a49 = Double.parseDouble(strA49);
					((EditText) mView.findViewById(R.id.a49)).setText(df2.format((a49 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a49)).setText(df2.format((mj)));
			}

		} else if (dj.equals("����")) {
			String strA50 = ((TextView) mView.findViewById(R.id.a50)).getText().toString();
			if (strA50 != null && !strA50.isEmpty()) {
				try {
					double a50 = Double.parseDouble(strA50);
					((EditText) mView.findViewById(R.id.a50)).setText(df2.format((a50 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a50)).setText(df2.format((mj)));
			}
		} else if (dj.equals("����")) {
			String strA51 = ((TextView) mView.findViewById(R.id.a51)).getText().toString();
			if (strA51 != null && !strA51.isEmpty()) {
				try {
					double a51 = Double.parseDouble(strA51);
					((EditText) mView.findViewById(R.id.a51)).setText(df2.format((a51 + mj)));
				} catch (Exception ex) {
					// TODO:
					Tools.ShowMessageBox(ex.getMessage());
					 return;
				}

			} else {
				((EditText) mView.findViewById(R.id.a51)).setText(df2.format((mj)));
			}
		} else if (dj.equals("�ļ�")) {
			String strA52 = ((TextView) mView.findViewById(R.id.a52)).getText().toString();
			if (strA52 != null && !strA52.isEmpty()) {
				try {
					double a52 = Double.parseDouble(strA52);
					((EditText) mView.findViewById(R.id.a52)).setText(df2.format((a52 + mj)));
				} catch (Exception ex) {

					Tools.ShowMessageBox(ex.getMessage());
					 return;

				}

			} else {
				((EditText) mView.findViewById(R.id.a52)).setText(df2.format((mj)));
			}
		}

		allArea = leftMJ;
		if (leftMJ < 0.0001) {
			((TextView) mView.findViewById(R.id.tv_djmjhj)).setText("�ϼ�");
			((TextView) mView.findViewById(R.id.tv_djmjhj)).setTextColor(Color.BLACK);
			mView.findViewById(R.id.tv_djmjhj).setOnClickListener(null);

		} else {
			mLeftArea = leftMJ;
			((TextView) mView.findViewById(R.id.tv_djmjhj)).setText("�ϼ�\n���ֵ�:" + df2.format(leftMJ));
		}
	}

	private HashMap<String, Object> CalPolyLayer(Dataset pDataset, Polygon pSelectPoly) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		GeoLayer pGeoLayer = pDataset.getBindGeoLayer();
		int ShowCount = pGeoLayer.getShowSelection().getCount();
		for (int i = 0; i < ShowCount; i++) {
			int idx = pGeoLayer.getShowSelection().getGeometryIndexList().get(i);
			Polygon Poly2 = (Polygon) pGeoLayer.getDataset().GetGeometry(idx);
			if (Poly2.getStatus() == lkGeometryStatus.enDelete)
				continue;
			if (pSelectPoly.equals(Poly2))
				continue; // �������ж�

			// ��Ӿ����Ƿ��ཻ
			if (!pSelectPoly.getEnvelope().Intersect(Poly2.getEnvelope()))
				continue;

			// �������
			HashMap<String, Object> IntersectResult = SpatialAnalysisTools.Poly_IntersectArea(pSelectPoly, Poly2);

			// �����ཻ���
			double Allarea = Double.parseDouble(IntersectResult.get("Area") + "");
			if(Allarea<0)
			{
				Allarea = Allarea *-1;
			}
			if (Allarea > 0) {
				result.put(Poly2.getSysId() + "", Allarea);
			}
		}

		return result;
	}

	class splitAreaChanged implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

	}

	class SPValue {
		private String mValue = "";

		public SPValue(String value) {
			mValue = value;
		}

		@Override
		public String toString() {
			if (mValue.contains("-")) {
				return mValue.substring(0, mValue.indexOf("-", 0));
			} else {
				return mValue;
			}
		}
	}

}
