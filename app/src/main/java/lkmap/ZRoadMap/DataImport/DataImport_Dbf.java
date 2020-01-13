package lkmap.ZRoadMap.DataImport;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.print.PrintAttributes.Resolution;
import android.util.Log;
import lkmap.Enum.lkGeoLayerType;
import lkmap.Tools.BitConverter;
import lkmap.Tools.Tools;

public class DataImport_Dbf 
{
	private RandomAccessFile m_Reader = null;
	private int rowCount = 0;
	public void Close()
	{
		if (this.m_Reader!=null)
			try {
				this.m_Reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	public boolean SetDbf(String dbfFileName)
	{
    	try
    	{
	    	this.m_Reader = new RandomAccessFile(new File(dbfFileName),"r");
	    	byte[] oneByte = new byte[1];
	    	this.m_Reader.read(oneByte);		 //�汾
	    	this.m_Reader.read(oneByte);  //��+1900
	    	this.m_Reader.read(oneByte);  //��
	    	this.m_Reader.read(oneByte);    //��
	    	

	        //�ļ��еļ�¼����,������
	    	//byte[] forByte = new byte[4];
	    	//this.m_Reader.read(forByte);
	    	//int rowCount = DataImport_Shp.BytesToInt(forByte);
	        rowCount = this.m_Reader.readInt();
	        rowCount = BitConverter.BigToLittleInt(rowCount);
	
	        //�ļ�ͷ�е��ֽ������ڴ�֮����ֽ�Ϊ����¼����
	        short headLength = this.m_Reader.readShort();//2
	        headLength = BitConverter.BigToLittleShort(headLength);
	
	        //һ����¼�е��ֽڳ��ȣ���ÿ��������ռ�ĳ���
	        this.m_Reader.readShort();//2
	
	        //�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã�������0����д��
	        this.m_Reader.read(new byte[2]);
	
	        //��ʾδ��ɵĲ���
	        this.m_Reader.read(new byte[1]);
	
	        //dBASE IV�������ǡ�
	        this.m_Reader.read(new byte[1]);
	
	        //�����ֽڣ����ڶ��û�����ʱʹ�á�
	        this.m_Reader.read(new byte[12]);
	
	        /**
	         * DBF�ļ���MDX��ʶ���ڴ���һ��DBF ��ʱ �����ʹ����MDX ��ʽ�������ļ���
	         * ��ô DBF ��ı�ͷ�е�����ֽھ��Զ���������һ����־�������´���ͼ���´����DBF���ʱ��
	         * ����������Զ�ʶ�������־������˱�־Ϊ�棬���������潫��ͼ����Ӧ��MDX �ļ�
	         */
	        this.m_Reader.read(new byte[1]);
	
	        //ҳ����
	        this.m_Reader.read(new byte[1]);
	
	        //�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã�������0����д
	        this.m_Reader.read(new byte[2]);
	
	        /**
	         * 32��N	��x*32�����ֽ�	��γ����ɱ���е����������ֶ�����Field Count��������
	         * ÿ���ֶεĳ���Ϊ32�������x�У���ռ�õĳ���Ϊx*32��
	         * ��ÿ32���ֽ������ְ���涨������ÿ���ֶε����ơ����͵���Ϣ�����������ı�
	         * N��1	1���ֽ�	��Ϊ�ֶζ������ֹ��ʶ��ֵΪ0x0D��
	         */
	
	        //�����ֶ���Ŀ
	        int columnCount = (headLength - 33) / 32;
	
	        for (int i = 1; i <= columnCount; i++)
	        {
	            //�ֶε����ƣ���ASCII��ֵ
                byte[] nameBytes = new byte[11];     	
	           this.m_Reader.read(nameBytes);
	           
	           byte[] type = new byte[1];
	           this.m_Reader.read(type);
	           
	
	            //�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã�Ĭ��Ϊ0
	           this.m_Reader.read(new byte[4]);
	
	            //�ֶεĳ��ȣ���ʾ���ֶζ�Ӧ��ֵ�ں���ļ�¼����ռ�ĳ���
	           byte[] FieldLen = new byte[1];
	           this.m_Reader.read(FieldLen);
	           int FieldSize = FieldLen[0];
	           if (FieldSize<0)
	        	   FieldSize+=256;
	
	            //�ֶεľ���
	           byte[] FieldScope = new byte[1];
	           this.m_Reader.read(FieldScope);
	           int fieldScope = FieldScope[0];
	
	            //�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã�Ĭ��Ϊ0
	           this.m_Reader.read(new byte[2]);
	
	            //������ID
	            this.m_Reader.read(new byte[1]);
	
	            //�����ֽڣ������Ժ�����µ�˵������Ϣʱʹ�ã�Ĭ��Ϊ0
	            this.m_Reader.read(new byte[11]);
	            //10.x���ϱ����ʽĬ����UTF-8
                String FieldName = new String(nameBytes,"GB2312");
//                String FieldName = new String(nameBytes);
                FieldName = FieldName.replace("\0", "");
                //�����ֶ����ֶ�Caption
                HashMap<String,String> fieldHM = new HashMap<String,String>();
                fieldHM.put("Field", "F"+i);
                fieldHM.put("Caption",FieldName);
                fieldHM.put("Length",FieldSize+"");
//                fieldHM.put("Type", type[0]+"");
                fieldHM.put("Type", new String(type));
                fieldHM.put("FieldDecimal", ((int)fieldScope)+"");
                
	            this.m_FiledList.add(fieldHM);
	        }
	
	        //��Ϊ�ֶζ������ֹ��ʶ��ֵΪ0x0D
	        this.m_Reader.read(new byte[1]);	    	
	    	return true;
    	}
    	catch(Exception e)
    	{
    		Log.e("SetDbf", e.getMessage());
    		return false;
    	}
	}
	
	public int getRowCount()
	{
		return rowCount;
	}
	
	//�ֶζ���
	private List<HashMap<String,String>> m_FiledList = new ArrayList<HashMap<String,String>>();
	public List<HashMap<String,String>> GetFieldList()
	{
		return this.m_FiledList;
	}
	public String GetFieldListStr()
	{
		List<String> StrList = new ArrayList<String>();
		for(HashMap<String,String> hm:this.m_FiledList)
		{
			StrList.add(hm.get("Field"));
		}
		return Tools.JoinT(",", StrList);
	}
	
	/**
     * ��ȡdbf����
     */
    public List<String> ReadData()
    {
    	 List<String> valueList = new ArrayList<String>();
    	try
    	{
	        //ÿ�������е�һ��20������
	    	this.m_Reader.read(new byte[1]);
	        for (int z = 0; z < this.m_FiledList.size(); z++)
	        {
	            HashMap<String,String> fieldLenAndType = this.m_FiledList.get(z);
	            
	            int Length = Integer.parseInt(fieldLenAndType.get("Length"));
	            String TypeStr = fieldLenAndType.get("Type");
	            
	            byte[] valueByte = new byte[Length];
	            this.m_Reader.read(valueByte);
	            
	            String FieldValue="";// = new String(valueByte,"GB2312");
//	            String FieldValue = new String(valueByte);
	            //FieldValue = FieldValue.trim();
	            
	            if(TypeStr.equals("C"))
	            {
	            	FieldValue = new String(valueByte,"GB2312");
	            	FieldValue = FieldValue.trim().replace("��", "��").replace("\'", "��").replace("\"", "��");
	            }
	            else if(TypeStr.equals("N"))
	            {
	            	try
	            	{
	            		BigDecimal bigDecimal = new BigDecimal((new String(valueByte)).trim());
	            		FieldValue = bigDecimal.toPlainString();
	            	}
	            	catch(Exception ex)
	            	{
	            		FieldValue = new String(valueByte);
	            	}
	            }
	            else if(TypeStr.equals("F")) 
	            {
	            	try{
	            		BigDecimal bigDecimal = new BigDecimal((new String(valueByte)).trim());
		            	FieldValue = bigDecimal.toPlainString();
	            	}
	            	catch(Exception e)
	            	{
	            		FieldValue = (new String(valueByte)).trim();
	            	}
	            	
	            	
	            }
	            else if(TypeStr.equals("I"))
	            {
	            	FieldValue =Integer.valueOf((new String(valueByte)).trim())+"";
	            }
	            else
	            {
	            	FieldValue = new String(valueByte);
	            	FieldValue = FieldValue.trim().replace("��", "��").replace("\'", "��").replace("\"", "��");
	            }
	            
//	            String fieldType = fieldLenAndType.get("Type")+"";
//	            if(fieldType.equals("70")||fieldType.equals("78"))
//	            {
//	            	try
//	            	{
//	            		if(fieldType.equals("70"))
//	            		{
//	            			FieldValue = BitConverter.ToDouble(valueByte)+"";
//	            		}
//	            		if(fieldType.equals("78"))
//	            		{
//	            			FieldValue = BitConverter.ToInt(valueByte)+"";
//	            		}
//	            		
//	            		
//	            	}
//	            	catch(Exception ex)
//	            	{
//	            		
//	            	}
//	            }
	            
	            
	            valueList.add(FieldValue);
	             
	//            string FileValue = System.Text.Encoding.GetEncoding("gb2312").GetString(tempBytesA).Trim().Replace("\'", "");
	//            FileValue = FileValue.TrimEnd("\0".ToCharArray());
	//            if (fieldLenAndType[1] == "78" || fieldLenAndType[1] == "70")  //N,F��ֵ
	//            {
	//                double D = 0;
	//                if (!double.TryParse(FileValue, out D)) FileValue = "0";
	//                else
	//                {
	//                    //�˴���Ҫ�Ǵ���С�����ȫ��0 �����
	//                    FileValue = D.ToString();
	//                    if (FileValue.Contains(".")) FileValue = FileValue.TrimEnd('0');
	//                    if (FileValue == "") FileValue = "0";
	//                }
	//            }
	//            FieldValueList[z] = "'" + FileValue.Trim() + "'";
	        }

	        return valueList;
    	}
    	catch(Exception e)
    	{
    		Log.e("ReadData", e.getMessage());
    		return valueList;
    	}
    }
}
