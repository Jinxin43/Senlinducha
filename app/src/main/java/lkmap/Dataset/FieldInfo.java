package lkmap.Dataset;

public class FieldInfo 
{
    //�ֶ�����
    private String _Name = "";
    public String getName()
    {
        return _Name;
    }
    public void setName(String value)
    {
        _Name = value;
        _Name = _Name.toUpperCase();
        if (_Name.indexOf("SYS_")>=0) this._Type=true;
//        if (_Name.equals("SYS_ID") || _Name.equals("SYS_STATUS") || _Name.equals("SYS_OID") || _Name.equals("SYS_AUTOID") ||
//            _Name.equals("SYS_BZ1") || _Name.equals("SYS_BZ2") || _Name == "SYS_BZ3" || _Name == "SYS_BZ4" || _Name == "SYS_BZ5")
//        {
//            this._Type = true;
//        }
    }

    //�ֶα��⣬Ҳ�����ֶε����ı���
    private String _Caption = "";
    public String getCaption()
    {
        return _Caption;
    }
    public void setCaption(String value)
    {
    	 _Caption = value;
    }

    //�ֶε����� true=ϵͳ�ֶΣ�false=�����ֶ�
    private boolean _Type = false;
    public boolean getType()
    {
         return _Type; 
    }

}
