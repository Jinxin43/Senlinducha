package lkmap.Cargeometry;

import java.util.ArrayList;
import java.util.List;

import lkmap.Enum.lkGeoLayerType;
import lkmap.Enum.lkGeometryStatus;
import lkmap.Index.MapCellIndex;
import lkmap.Index.T4Index;
import lkmap.Symbol.ISymbol;

public abstract class Geometry 
{
    //�������жಿ��

    protected  List<Part> _PartList = new ArrayList<Part>();

   /**
    * �ಿ����
    * @return
    */
    public int getPartCount()
    {
        return this._PartList.size();
    }

    /**
     * ��ȡʵ�����������
     */
    public int getVertexCount()
    {
        int CoorCount = 0;
        for(Part part : this._PartList) CoorCount += part.getVertexList().size();
        return CoorCount;
    }

    /**
     * �����²���
     * @param part
     */
    public void AddPart(Part part)
    {
        this._PartList.add(part);
        //if (this._PartList.size() > 1) this.IsSimple = false;
    }
    public void RemovePart(Part part)
    {
    	this._PartList.remove(part);
    }

    /**
     * �õ�ָ�������Ĳ���
     * @param PartIndex
     * @return
     */
    public Part GetPartAt(int PartIndex)
    {
        return this._PartList.get(PartIndex);
    }

    /**
     * ��ȡ�ಿ���ڳ����괮���������
     */
    public List<Integer> GetPartIndexList()
    {
        List<Integer> partIndex = new ArrayList<Integer>();
        partIndex.add(0);
        for(int i=0;i<this.getPartCount()-1;i++)
        {
            int VertexCount = this._PartList.get(i).getVertexList().size();
            partIndex.add(VertexCount + partIndex.get(i));  //�ڵ������ۼ�
        }
        return partIndex;
    }


	/// <summary>
    /// ���ݿ��¼������ֵ��Ҳ���������ݹ�����ΨһֵSYS_ID
    /// </summary>
    private int _SysId = -1;
    public int getSysId()
    {
         return this._SysId; 
    }
    public void setSysId(int value)
    {
    	this._SysId = value;
    }
    
    //Geometry��ʵ��ID�ţ�����ֵ
    private String _ID;                                          
    public String getID()
    {
        return _ID;
    }
    public void setID(String id)
    {
    	_ID = id;
    }
    


    //ʵ���Ƿ�Ϊ��
    public boolean IsNull()
    {
       if (this.getPartCount() == 0) return true; 
       else return false; 
    }

    //����ʵ��ΪNULL
    public void SetNull()
    {
         this._PartList.clear();
    }

    //Geometry����Ӿ���
    private Envelope _Envelope;
    public Envelope getEnvelope()
    {
        if (this._Envelope == null) this.CalEnvelope();
        return _Envelope; 
    }
    public void setEnvelope(Envelope env)
    {
    	this._Envelope = env;
    }
    
    /**
     * ����ʵ�����Ӿ���
     */
    public void CalEnvelope()
    {
        Envelope pEnv = new Envelope(0, 0, 0, 0);
        for (Part part : this._PartList)
        {
        	part.UpdateEnvelope();
            if (pEnv.IsZero()) pEnv = part.getEnvelope();
            else pEnv = pEnv.Merge(part.getEnvelope());
        }
        this._Envelope = pEnv;
    }



    //ʵ���״̬ģʽ������ʵ��༭����
    private lkGeometryStatus _Status = lkGeometryStatus.enNormal;
    public lkGeometryStatus getStatus()
    {
        return _Status;
    }
    public void setStatus(lkGeometryStatus GS)
    {
    	_Status = GS;
    }
    
    //�Ƿ񱻱༭�������ڱ�������
    private boolean _Edited = false;
    public void SetEdited(boolean edited){this._Edited=edited;}
    public boolean GetEdited(){return this._Edited;}


    //ʵ�������ʽ
    private ISymbol _Symbol = null;
    public ISymbol getSymbol()
    {
        return _Symbol;
    }
    public void setSymbol(ISymbol value)
    {
    	_Symbol = value;
    }

    //����������
    private String _Tag = "";
    public String getTag(){return this._Tag; }
    public void setTag(String tag) {this._Tag = tag;}

    private String _Tag_UniqueSymbol = "";
    public String getTagForUniqueSymbol(){return this._Tag_UniqueSymbol; }
    public void setTagForUniqueSymbol(String tag) {this._Tag_UniqueSymbol = tag;}
    
    
//    //���������Ӿ���
//    public void UpdateEnvelope()
//    {
//        this._Envelope = this.CalEnvelope();
//    }
//    
//    //����ʵ�����Ӿ���
//    public Envelope CalEnvelope()
//    {
//        Coordinate Pt;
//        double MinX = 0, MinY = 0, MaxX = 0, MaxY = 0;
//        for (int i = 0; i < this.getItems().size(); i++)
//        {
//            Pt = this.getItems().get(i);
//            if (i == 0)
//            {
//                MinX = Pt.getX(); MinY = Pt.getY(); MaxX = MinX; MaxY = MinY;
//            }
//            if (MinX > Pt.getX()) MinX = Pt.getX();
//            if (MaxX < Pt.getX()) MaxX = Pt.getX();
//            if (MinY > Pt.getY()) MinY = Pt.getY();
//            if (MaxY < Pt.getY()) MaxY = Pt.getY();
//        }
//        //���������Ӿ���
//        return new Envelope(MinX, MaxY, MaxX, MinY);
//
//    }

    //����ƫ��������ʵ���������Ϣ
    public void UpdateCoordinate(double deltX, double deltY)
    {
    	for(int p=0;p<this.getPartCount();p++)
    	{
    		Part part = this.GetPartAt(p);
	        for (Coordinate newPt:part.getVertexList())
	        {
	            newPt.setX(newPt.getX()+deltX);
	            newPt.setY(newPt.getY()+deltY);
	        }
	        part.UpdateEnvelope();
    	}
        this._Envelope = null;
    }

    public abstract Coordinate getCenterPoint();
    
    //����ʵ���������ڵ�����
    public T4Index CalCellIndex(MapCellIndex mapCellIndex)
    {
    	return mapCellIndex.CalOneCellIndex(this.getEnvelope());
    }

    //ѡ��ʵ��
    //abstract public bool Select(Coordinate SelectPoint, double Tolerance);

    //��¡ʵ��
    abstract public Geometry Clone();

    //�������
    abstract public boolean HitTest(Coordinate HitPoint, double Tolerance,Boolean isBGLayer);

    //ƫ��
    abstract public boolean Offset(double OffsetX, double OffsetY);

    //�õ�ʵ�������
    abstract public lkGeoLayerType GetType();
    
}
