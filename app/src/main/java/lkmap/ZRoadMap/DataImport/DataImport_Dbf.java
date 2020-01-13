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
	    	this.m_Reader.read(oneByte);		 //版本
	    	this.m_Reader.read(oneByte);  //年+1900
	    	this.m_Reader.read(oneByte);  //月
	    	this.m_Reader.read(oneByte);    //日
	    	

	        //文件中的记录条数,即行数
	    	//byte[] forByte = new byte[4];
	    	//this.m_Reader.read(forByte);
	    	//int rowCount = DataImport_Shp.BytesToInt(forByte);
	        rowCount = this.m_Reader.readInt();
	        rowCount = BitConverter.BigToLittleInt(rowCount);
	
	        //文件头中的字节数，在此之后的字节为表格记录数据
	        short headLength = this.m_Reader.readShort();//2
	        headLength = BitConverter.BigToLittleShort(headLength);
	
	        //一条记录中的字节长度，即每行数据所占的长度
	        this.m_Reader.readShort();//2
	
	        //保留字节，用于以后添加新的说明性信息时使用，这里用0来填写。
	        this.m_Reader.read(new byte[2]);
	
	        //表示未完成的操作
	        this.m_Reader.read(new byte[1]);
	
	        //dBASE IV编密码标记。
	        this.m_Reader.read(new byte[1]);
	
	        //保留字节，用于多用户处理时使用。
	        this.m_Reader.read(new byte[12]);
	
	        /**
	         * DBF文件的MDX标识。在创建一个DBF 表时 ，如果使用了MDX 格式的索引文件，
	         * 那么 DBF 表的表头中的这个字节就自动被设置了一个标志，当你下次试图重新打开这个DBF表的时候，
	         * 数据引擎会自动识别这个标志，如果此标志为真，则数据引擎将试图打开相应的MDX 文件
	         */
	        this.m_Reader.read(new byte[1]);
	
	        //页码标记
	        this.m_Reader.read(new byte[1]);
	
	        //保留字节，用于以后添加新的说明性信息时使用，这里用0来填写
	        this.m_Reader.read(new byte[2]);
	
	        /**
	         * 32－N	（x*32）个字节	这段长度由表格中的列数（即字段数，Field Count）决定，
	         * 每个字段的长度为32，如果有x列，则占用的长度为x*32，
	         * 这每32个字节里面又按其规定包含了每个字段的名称、类型等信息，具体见下面的表。
	         * N＋1	1个字节	作为字段定义的终止标识，值为0x0D。
	         */
	
	        //计算字段数目
	        int columnCount = (headLength - 33) / 32;
	
	        for (int i = 1; i <= columnCount; i++)
	        {
	            //字段的名称，是ASCII码值
                byte[] nameBytes = new byte[11];     	
	           this.m_Reader.read(nameBytes);
	           
	           byte[] type = new byte[1];
	           this.m_Reader.read(type);
	           
	
	            //保留字节，用于以后添加新的说明性信息时使用，默认为0
	           this.m_Reader.read(new byte[4]);
	
	            //字段的长度，表示该字段对应的值在后面的记录中所占的长度
	           byte[] FieldLen = new byte[1];
	           this.m_Reader.read(FieldLen);
	           int FieldSize = FieldLen[0];
	           if (FieldSize<0)
	        	   FieldSize+=256;
	
	            //字段的精度
	           byte[] FieldScope = new byte[1];
	           this.m_Reader.read(FieldScope);
	           int fieldScope = FieldScope[0];
	
	            //保留字节，用于以后添加新的说明性信息时使用，默认为0
	           this.m_Reader.read(new byte[2]);
	
	            //工作区ID
	            this.m_Reader.read(new byte[1]);
	
	            //保留字节，用于以后添加新的说明性信息时使用，默认为0
	            this.m_Reader.read(new byte[11]);
	            //10.x以上编码格式默认是UTF-8
                String FieldName = new String(nameBytes,"GB2312");
//                String FieldName = new String(nameBytes);
                FieldName = FieldName.replace("\0", "");
                //数据字段与字段Caption
                HashMap<String,String> fieldHM = new HashMap<String,String>();
                fieldHM.put("Field", "F"+i);
                fieldHM.put("Caption",FieldName);
                fieldHM.put("Length",FieldSize+"");
//                fieldHM.put("Type", type[0]+"");
                fieldHM.put("Type", new String(type));
                fieldHM.put("FieldDecimal", ((int)fieldScope)+"");
                
	            this.m_FiledList.add(fieldHM);
	        }
	
	        //作为字段定义的终止标识，值为0x0D
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
	
	//字段定义
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
     * 读取dbf数据
     */
    public List<String> ReadData()
    {
    	 List<String> valueList = new ArrayList<String>();
    	try
    	{
	        //每行数据中第一个20，跳过
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
	            	FieldValue = FieldValue.trim().replace("°", "度").replace("\'", "分").replace("\"", "秒");
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
	            	FieldValue = FieldValue.trim().replace("°", "度").replace("\'", "分").replace("\"", "秒");
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
	//            if (fieldLenAndType[1] == "78" || fieldLenAndType[1] == "70")  //N,F数值
	//            {
	//                double D = 0;
	//                if (!double.TryParse(FileValue, out D)) FileValue = "0";
	//                else
	//                {
	//                    //此处主要是处理小数点后全是0 的情况
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
