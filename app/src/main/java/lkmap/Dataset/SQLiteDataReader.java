package lkmap.Dataset;

import android.database.Cursor;

public class SQLiteDataReader {
	private Cursor _Cursor = null;

	public SQLiteDataReader(Cursor value) {
		_Cursor = value;
		// _Cursor.moveToFirst();
	}

	// 移动记录指针
	public boolean Read() {
		return _Cursor.moveToNext();
	}

	public int GetFieldCount() {
		return _Cursor.getColumnCount();
	}

	public String[] GetFieldNameList() {
		return _Cursor.getColumnNames();
	}

	public int GetCount() {
		return _Cursor.getCount();
	}

	// 读取数据
	public String GetString(int index) {
		return _Cursor.getString(index);
	}

	public String GetString(String FieldName) {
		return _Cursor.getString(_Cursor.getColumnIndex(FieldName));
	}

	public String getUnNullString(String FieldName) {
		String value = _Cursor.getString(_Cursor.getColumnIndex(FieldName));
		return value == null ? "" : value;
	}

	public int GetInt32(int index) {
		return _Cursor.getInt(index);
	}

	public int GetInt32(String fieldName) {
		return _Cursor.getInt(_Cursor.getColumnIndex(fieldName));
	}

	public double GetDouble(String FieldName) {
		return _Cursor.getDouble(_Cursor.getColumnIndex(FieldName));
	}

	public double GetDouble(int index) {
		return _Cursor.getDouble(index);
	}

	public byte[] GetBlob(int index) {
		return _Cursor.getBlob(index);
	}

	public byte[] GetBlob(String FieldName) {
		return _Cursor.getBlob(_Cursor.getColumnIndex(FieldName));
	}

	public int getColumnIndex(String FieldName) {
		return _Cursor.getColumnIndex(FieldName);
	}

	// 关闭CUSOR
	public void Close() {
		_Cursor.close();
	}

}
