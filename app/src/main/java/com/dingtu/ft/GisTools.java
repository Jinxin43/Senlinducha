package com.dingtu.ft;

import lkmap.Tools.Tools;

public class GisTools {

	public static String getSRID(String CSName, int fendai, float centralMeida, boolean hasDH) {
		if (centralMeida < 75 || centralMeida > 135) {
			Tools.ShowMessageBox("无法判断SRID，中央经线不能大于135且不能小于75！");
			return "";
		}

		String srid = "";
		if (CSName.equals("WGS-84坐标")) {
			srid = "4326";
		} else if (CSName.contains("西安80坐标")) {
			if (fendai == 3) {
				if (hasDH) {
					int id = 2349 + (int) ((centralMeida - 75) / 3);
					srid = id + "";

				} else {
					int id = 2370 + (int) ((centralMeida - 75) / 3);
					srid = id + "";
				}
			} else// 6度分带
			{
				if (hasDH) {
					int id = 2327 + (int) ((centralMeida - 75) / 6);
					srid = id + "";
				} else {
					int id = 2338 + (int) ((centralMeida - 75) / 6);
					srid = id + "";
				}
			}
		} else if (CSName.contains("北京54坐标")) {
			if (fendai == 3) {
				if (hasDH) {
					int id = 2401 + (int) ((centralMeida - 75) / 3);
					srid = id + "";

				} else {
					int id = 2422 + (int) ((centralMeida - 75) / 3);
					srid = id + "";
				}
			} else// 6度分带
			{
				if (hasDH) {
					int id = 21413 + (int) ((centralMeida - 75) / 6);
					srid = id + "";
				} else {
					int id = 21453 + (int) ((centralMeida - 75) / 6);
					srid = id + "";
				}
			}
		} else if (CSName.contains("2000国家大地坐标")) {
			if (fendai == 3) {
				if (hasDH) {
					int id = 4513 + (int) ((centralMeida - 75) / 3);
					srid = id + "";

				} else {
					int id = 4534 + (int) ((centralMeida - 75) / 3);
					srid = id + "";
				}
			} else// 6度分带
			{
				if (hasDH) {
					int id = 4491 + (int) ((centralMeida - 75) / 6);
					srid = id + "";
				} else {
					int id = 4502 + (int) ((centralMeida - 75) / 6);
					srid = id + "";
				}
			}
		}

		return srid;
	}

}
