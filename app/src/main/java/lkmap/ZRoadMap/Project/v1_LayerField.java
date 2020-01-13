package lkmap.ZRoadMap.Project;

import java.util.List;
import java.util.UUID;

import dingtu.ZRoadMap.PubVar;
import lkmap.Enum.lkFieldType;

public class v1_LayerField {
	// �ֶ�ΨһIDֵ
	private String _LayerID = "T" + (UUID.randomUUID().toString()).replace("-", "").toUpperCase();

	public String GetFieldID() {
		return this._LayerID;
	}

	// �ֶ�����
	private String _FieldName = "";

	public String GetFieldName() {
		return this._FieldName;
	}

	public void SetFieldName(String fieldName) {
		this._FieldName = fieldName;
	}

	// �ֶζ�Ӧ�����ݱ������ֶε����ƣ����磺F1,F2,F3....
	private String _DataFieldName = "";

	public String GetDataFieldName() {
		return this._DataFieldName;
	}

	public void SetDataFieldName(String dataFieldName) {
		this._DataFieldName = dataFieldName;
	}

	// �ֶ�����
	private lkFieldType _FieldType = lkFieldType.enString;

	public lkFieldType GetFieldType() {
		return this._FieldType;
	}

	private String _FieldTypeName = "";

	public String GetFieldTypeName() {
		return _FieldTypeName;
	}

	public void SetFieldTypeName(String fieldTypeName) {
		this._FieldTypeName = fieldTypeName;
		if (fieldTypeName.equals("�ַ���"))
			this._FieldType = lkFieldType.enString;
		else if (fieldTypeName.equals("����"))
			this._FieldType = lkFieldType.enInt;
		else if (fieldTypeName.equals("������"))
			this._FieldType = lkFieldType.enFloat;
		else if (fieldTypeName.equals("˫����"))
			this._FieldType = lkFieldType.enFloat;
		else if (fieldTypeName.equals("������"))
			this._FieldType = lkFieldType.enBoolean;
		else if (fieldTypeName.equals("������"))
			this._FieldType = lkFieldType.enDateTime;
		else
			this._FieldType = lkFieldType.enString;
	}

	// �ֶδ�С
	private int _FieldSize = 255;

	public int GetFieldSize() {
		return this._FieldSize;
	}

	public void SetFieldSize(int fieldSize) {
		this._FieldSize = fieldSize;
	}

	// �ֶξ���
	private int _FieldDecimal = 0;

	public int GetFieldDecimal() {
		return this._FieldDecimal;
	}

	public void SetFieldDecimal(int fieldDecimal) {
		this._FieldDecimal = fieldDecimal;
	}

	// ���������ֵ���Ŀ
	private String _FieldEnumCode = "";

	public String GetFieldEnumCode() {
		return this._FieldEnumCode;
	}
	// public List<String> GetFieldEnumList(String yinzi)
	// {
	//// return
	// PubVar.m_DoEvent.m_ConfigDB.GetDataDictionaryExplorer().GetZDValueList(this._FieldEnumCode);
	// return PubVar.m_DoEvent.m_DictDataDB.getEnumList("�Զ��幤��",
	// this._FieldEnumCode);
	//// return new
	// ArrayList<String>(PubVar.m_DoEvent.m_DictDataDB.getEnumList("�Զ��幤��",
	// this._FieldEnumCode).values());
	// }

	public List<String> GetFieldEnumList(String yinzi, String category) {
		return PubVar.m_DoEvent.m_DictDataDB.getEnumList(yinzi, category);
		// return new
		// ArrayList<String>(PubVar.m_DoEvent.m_DictDataDB.getEnumList(yinzi,
		// category).values());
	}

	public void SetFieldEnumCode(String fieldEnumCode) {
		this._FieldEnumCode = fieldEnumCode;
	}

	// �Ƿ������Ŀ�Ƿ������ֶ�����
	private boolean _FieldEnumEdit = false;

	public boolean GetFieldEnumEdit() {
		return this._FieldEnumEdit;
	}

	public void SetFieldEnumEdit(boolean enumEdit) {
		this._FieldEnumEdit = enumEdit;
	}

	private String fieldShortName = "";

	public void SetFieldShortName(String dbName) {
		if (dbName == null) {
			fieldShortName = "";
		} else {
			fieldShortName = dbName;
		}

	}

	public String GetFieldShortName() {
		return fieldShortName;
	}

	private boolean isSelect = true;

	public void SetIsSelect(boolean s) {
		isSelect = s;
	}

	public boolean getIsSelect() {
		return isSelect;
	}

}
