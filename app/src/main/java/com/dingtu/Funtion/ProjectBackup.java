package com.dingtu.Funtion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.util.Log;
import dingtu.ZRoadMap.PubVar;
import lkmap.Tools.Tools;

public class ProjectBackup {

	private static int saverecords = 10;
	
	public void backupProjectFile()
	{
		String backupPath = PubVar.m_SysAbsolutePath.replace(PubVar.m_SysDictionaryName, "DuChaBackup")+"/"+PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectShortName()+"/";
		Log.e("backupPath", backupPath);
		String projectFullName = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectFullName()+"/Project.dbx";
		String projectJournal = projectFullName+"-journal";
		String DataFile = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName();
		String DataJournal = PubVar.m_DoEvent.m_ProjectDB.GetProjectExplorer().GetProjectDataFileName()+"-journal";
		
		
		String pathName = Tools.GetSystemDate().replace("-", "").replace(":", "").replace(" ", "");
		String toPorjctFullName =  backupPath+pathName+"/Project.dbx";
		String toDataFileName = backupPath+pathName+"/TAData.dbx";
		Tools.CopyFile(projectFullName, toPorjctFullName);
		Tools.CopyFile(DataFile,toDataFileName);
		
		File projectJournalFile = new File(projectJournal);
		if(projectJournalFile.exists())
		{
			String toProjectJournal =  toPorjctFullName+"-journal";
			Tools.CopyFile(projectJournal, toProjectJournal);
		}
		
		File dataJournalFile = new File(DataJournal);
		if(dataJournalFile.exists())
		{
			String toDataJournal =  toDataFileName+"-journal";
			Tools.CopyFile(DataJournal,toDataJournal);
		}
		
		deleteOldFoler(backupPath);
	}
	
	public void deleteOldFoler(String filePath)
	{
		ArrayList<String> FileNameList = new ArrayList<String>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				
				long diff = f2.lastModified() - f1.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
			}
 
			public boolean equals(Object obj) {
				return true;
			}
 
		});
		
		int i=1;
		for (File file1 : files) {
			if (file1.isDirectory()) {
				Log.e("DeleteBak", file1.getName());
				
				if(i>saverecords)
				{
					Tools.deleteDirWihtFile(file1);
					
				}
				i++;
			}
		}
	}
	
	/**
	 * 按文件修改时间排序
	 * @param filePath
	 */
	public static ArrayList<String> orderByDate(String filePath) {
		ArrayList<String> FileNameList = new ArrayList<String>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		Arrays.sort(files, new Comparator<File>() {
			public int compare(File f1, File f2) {
				
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;// 如果 if 中修改为 返回-1 同时此处修改为返回 1 排序就会是递减
			}
 
			public boolean equals(Object obj) {
				return true;
			}
 
		});
 
		for (File file1 : files) {
			if (file1.isDirectory()) {
				FileNameList.add(file1.getName());
			}
		}
		return FileNameList;
	}

}
