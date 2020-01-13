package lkmap.CoordinateSystem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dingtu.senlinducha.R;

import dingtu.ZRoadMap.PubVar;
import lkmap.Cargeometry.Coordinate;
import lkmap.Enum.lkCoorTransMethod;
import lkmap.Tools.Tools;

/**
 * ������Ҫ�Ǽ������Σ��ĲΣ��߲�
 * @author lmgk
 *
 */
public class CoorParamTools 
{
	/**
	 * �����Ĳ������������Ͽ��Ƶ㣬���ֻ��һ�����Ƶ���Ϊ����У����ֱ�������
	 * @param CoorList
	 * @return
	 */
	public static HashMap<String,Object> CalFourPara(List<Coordinate> CoorList)
	{
		//ֻ��һ��ƥ���
		if (CoorList.size()==2)
		{
	        Coordinate P1 = CoorList.get(0);
	      
	        Coordinate P2 = CoorList.get(1);
	        CoorSystem CS = (PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetCoorSystem());
	    	if (CS.GetPMTransMethod()==lkCoorTransMethod.enFourPara)
	    	{
	    		P2.setX(P2.getX()+CS.GetTransToP41());
	    		P2.setY(P2.getY()+CS.GetTransToP42());
	    	}
			//�ж����ֵ��Ŀ��-Դ��
			String DX = Tools.ConvertToDigi((P2.getX()-P1.getX())+"",3);
			String DY = Tools.ConvertToDigi((P2.getY()-P1.getY())+"",3);
	        HashMap<String,Object> result = new HashMap<String,Object>();
	        result.put("DX", DX); result.put("DY", DY);
	        result.put("R", 0);result.put("K", 1);
	        return result;
		}
		
		//����ƥ���
		if (CoorList.size()==4)
		{
			return CalFourParaBy2MatchPoint(CoorList);
		}
		
		//�����������߲μ��㷽��
		if (CoorList.size()>=5)
		{
			Coordinate[] aPtSource = new Coordinate[CoorList.size()/2];
			Coordinate[] aPtTo = new Coordinate[CoorList.size()/2];
			int idx = 0;
			for(int i=0;i<CoorList.size()/2;i++)
			{
				aPtSource[i] = CoorList.get(idx);aPtTo[i]=CoorList.get(idx+1);idx+=2;
			}
			HashMap<String,Object> sevenParam = CalSevenPara(aPtSource,aPtTo);
			
	        HashMap<String,Object> result = new HashMap<String,Object>();
	        if (!Tools.IsDouble(sevenParam.get("DX")+""))sevenParam.put("DX","0");
	        if (!Tools.IsDouble(sevenParam.get("DY")+""))sevenParam.put("DY","0");
	        if (!Tools.IsDouble(sevenParam.get("WZ")+""))sevenParam.put("WZ","0");
	        if (!Tools.IsDouble(sevenParam.get("K")+""))sevenParam.put("K","1");
	        result.put("DX", (lkmap.Tools.Tools.ConvertToDigi(Double.parseDouble(sevenParam.get("DX")+""))));
	        result.put("DY", (lkmap.Tools.Tools.ConvertToDigi(Double.parseDouble(sevenParam.get("DY")+""))));
	        result.put("R", (lkmap.Tools.Tools.ConvertToDigi(-Double.parseDouble(sevenParam.get("WZ")+"")*180/Math.PI * 3600)));
	        result.put("K", (lkmap.Tools.Tools.ConvertToDigi(Double.parseDouble(sevenParam.get("K")+""))));
	        return result;
		}
		return null;
	}
	
	/**
	 * ͨ������ƥ�������Ĳ���
	 * @param CoorList
	 * @return
	 */
	private static HashMap<String,Object> CalFourParaBy2MatchPoint(List<Coordinate> CoorList)
	{
		
        Coordinate OP1 = CoorList.get(0);
        Coordinate OP2 = CoorList.get(2);

        Coordinate NP1 = CoorList.get(1);
        Coordinate NP2 = CoorList.get(3);

         double delX1 = NP1.getX() - NP2.getX();
         double delX2 = OP1.getX() - OP2.getX();
         double delY3 = NP1.getY() - NP2.getY();
         double delY4 = OP1.getY() - OP2.getY();

         double delxy = (delX2 * delY3 - delX1 * delY4) / (delX1 * delX2 + delY3 * delY4);

        double TA = Math.atan(delxy);
        double K = delX1 / (delX2 * Math.cos(TA) - delY4 * Math.sin(TA));

        double XP = NP1.getX() - (OP1.getX() * Math.cos(TA) - OP1.getY() * Math.sin(TA)) * K;
        double YP = NP1.getY() - (OP1.getY() * Math.cos(TA) + OP1.getX() * Math.sin(TA)) * K;

        HashMap<String,Object> result = new HashMap<String,Object>();
        if (!Tools.IsDouble(XP+""))XP=0;
        if (!Tools.IsDouble(YP+""))YP=0;
        if (!Tools.IsDouble(TA+""))TA=0;
        if (!Tools.IsDouble(K+""))K=1;
        
        DecimalFormat df = new DecimalFormat("0.000");
        
//        result.put("DX", XP); 
//        result.put("DY", YP);
//        result.put("R", TA*180/Math.PI * 3600);   //����->��
//        result.put("K", K);
        
        result.put("DX", df.format(XP)); 
        result.put("DY", df.format(YP));
        result.put("R", df.format(TA*180/Math.PI * 3600));   //����->��
        result.put("K", df.format(K));
        
        //MessageBox.Show("XP=" + XP + "\r\n" + "YP=" + YP + "\r\nTA=" + TA + "\r\nK=" + K + "\r\nX=" + X + "\r\nY=" + Y);
		return result;
	}
	
    /**
     * ����3������3�����ϵĵ����������ϵ���������7����(��С���˷�) ������С�Ƕ�ת�� bursaģ��
     * @param aPtSource ��֪���Դ����ϵ������
     * @param aPtTo ��֪���������ϵ������
     * @return ���: 7����
     */
    public static HashMap<String,Object> CalSevenPara(Coordinate[] aPtSource, Coordinate[] aPtTo)
    {
        //��A B ����ֵ
    	int arrALen = aPtSource.length * 3;
        double[][] arrA = new double[arrALen][7]; // �����4����֪�㣬 12 * 7����  A*X=B�еľ���A
        for (int i = 0; i < arrALen; i++)
        {
            if (i % 3 == 0)
            {
                arrA[i][0] = 1;
                arrA[i][1] = 0;
                arrA[i][2] = 0;
                arrA[i][3] = aPtSource[i / 3].getX();
                arrA[i][4] = 0;
                arrA[i][5] = -aPtSource[i / 3].getZ();
                arrA[i][6] = aPtSource[i / 3].getY();
            }
            else if (i % 3 == 1)
            {
                arrA[i][0] = 0;
                arrA[i][1] = 1;
                arrA[i][2] = 0;
                arrA[i][3] = aPtSource[i / 3].getY();
                arrA[i][4] = aPtSource[i / 3].getZ();
                arrA[i][5] = 0;
                arrA[i][6] = -aPtSource[i / 3].getX();
            }
            else if (i % 3 == 2)
            {
                arrA[i][0] = 0;
                arrA[i][1] = 0;
                arrA[i][2] = 1;
                arrA[i][3] = aPtSource[i / 3].getZ();
                arrA[i][4] = -aPtSource[i / 3].getY();
                arrA[i][5] = aPtSource[i / 3].getX();
                arrA[i][6] = 0;
            }
        }

        int arrBLen = aPtSource.length * 3;
        double[][] arrB = new double[arrBLen][1]; // A * X = B �еľ���B, �����4���㣬���� 12*1����
        for (int i = 0; i <arrBLen; i++)
        {
            if (i % 3 == 0)
            {
                arrB[i][0] = aPtTo[i / 3].getX();
            }
            else if (i % 3 == 1)
            {
                arrB[i][0] = aPtTo[i / 3].getY();
            }
            else if (i % 3 == 2)
            {
                arrB[i][0] = aPtTo[i / 3].getZ();
            }
        }

        LKMatrix mtrA = new LKMatrix(arrA); // A����
        LKMatrix mtrAT = mtrA.Transpose(); // A��ת��
        LKMatrix mtrB = new LKMatrix(arrB); // B����

        LKMatrix mtrATmulA = mtrAT.Multiply(mtrA); // A��ת�á�A

        //// ��(A��ת�á�A)�������
        mtrATmulA.Inv();

        //// A��ת�� �� B

        LKMatrix mtrATmulB = mtrAT.Multiply(mtrB); // A��ת�� * B

        //// ���
        LKMatrix mtrResult = mtrATmulA.Multiply(mtrATmulB);

        HashMap<String,Object> result = new HashMap<String,Object>();
        result.put("DX", mtrResult.GetData()[0][0]);
        result.put("DY", mtrResult.GetData()[1][0]);
        result.put("DZ", mtrResult.GetData()[2][0]);
        result.put("K", mtrResult.GetData()[3][0]);
        result.put("WX", mtrResult.GetData()[4][0]);
        result.put("WY", mtrResult.GetData()[5][0]);
        result.put("WZ", mtrResult.GetData()[6][0]);
        
        
        return result;
//        return new ServerPara(mtrResult.GetData()[0, 0], mtrResult.GetData()[1, 0], mtrResult.GetData()[2, 0], mtrResult.GetData()[3, 0],
//                          mtrResult.GetData()[4, 0], mtrResult.GetData()[5, 0], mtrResult.GetData()[6, 0]);
//        // PS: ���뿼��cosA = 0 ������Ϊ��ĸ�����
//        // Add code
    }

}
