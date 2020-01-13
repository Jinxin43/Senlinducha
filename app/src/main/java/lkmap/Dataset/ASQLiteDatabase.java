package lkmap.Dataset;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import dingtu.ZRoadMap.PubVar;
import lkmap.Tools.Tools;

public class ASQLiteDatabase {
	private SQLiteDatabase _SQLiteDatabase = null;
	private String _DatabaseName = "";

	public SQLiteDatabase GetSQLiteDatabase() {
		return this._SQLiteDatabase;
	}

	public void setDatabaseName(String value) {
		try {
			if (_SQLiteDatabase != null)
				if (_SQLiteDatabase.isOpen())
					_SQLiteDatabase.close();
			_DatabaseName = value;
			_SQLiteDatabase = SQLiteDatabase.openDatabase(_DatabaseName, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLiteException e) {
			String aa = e.getMessage();
		}
	}

	/**
	 * ���ؼ�¼��
	 * 
	 * @param SQL
	 * @return
	 */
	public SQLiteDataReader Query(String SQL) {
		Cursor CR = _SQLiteDatabase.rawQuery(SQL, null);
		return new SQLiteDataReader(CR);
	}

	/**
	 * ִ��SQL���
	 * 
	 * @param SQL
	 * @return
	 */
	public boolean ExcuteSQL(String SQL) {
		try {
			_SQLiteDatabase.execSQL(SQL);
			return true;
		} catch (SQLiteException e) {
			// TODO:write Log
			// Tools.ShowToast(PubVar.m_DoEvent.m_Context, e.getMessage());
			e.printStackTrace();
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, e.getMessage());
			// MessageBox.Show("���ݸ���ʧ�ܣ�\rԭ��:" + e.Message, "ϵͳ",
			// MessageBoxButtons.OK, MessageBoxIcon.Exclamation,
			// MessageBoxDefaultButton.Button1);
			return false;
		}
	}

	/**
	 * ִ��SQL���
	 * 
	 * @param SQL
	 * @param value
	 *            ����
	 * @return
	 */
	public boolean ExcuteSQL(String SQL, Object[] value) {
		try {
			_SQLiteDatabase.execSQL(SQL, value);
			return true;
		} catch (SQLiteException e) {
			e.printStackTrace();
			Tools.ShowMessageBox(PubVar.m_DoEvent.m_Context, "����ʧ�ܣ�\r\nԭ��" + e.getMessage());
			// MessageBox.Show("���ݸ���ʧ�ܣ�\rԭ��:" + e.getMessage());
			return false;
		}
	}

	// �ر����ݿ�
	public void Close() {
		if (_SQLiteDatabase.isOpen())
			_SQLiteDatabase.close();
	}
}
