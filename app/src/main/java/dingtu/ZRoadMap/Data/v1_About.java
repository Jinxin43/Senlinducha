package dingtu.ZRoadMap.Data;

import lkmap.Tools.Tools;

import com.dingtu.senlinducha.R;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import dingtu.ZRoadMap.AuthorizeTools_UserInfo;
import dingtu.ZRoadMap.PubVar;

public class v1_About
{
	private v1_FormTemplate _DataDialog = null; 

    public v1_About()
    {
    	_DataDialog = new v1_FormTemplate(PubVar.m_DoEvent.m_Context);
    	_DataDialog.SetOtherView(R.layout.v1_about);
    	_DataDialog.ReSetSize(0.5f,-1f);
    	_DataDialog.SetCaption(Tools.ToLocale("����ϵͳ"));
    	_DataDialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("ȷ��")+"  ,ȷ��", new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
//				//��һ����������
//				String BeforeLanguage = PubVar.m_HashMap.GetValueObject("Tag_System_Language").Value;
//				String SelLanguage = Tools.GetSpinnerValueOnID(_DataDialog, R.id.spLanguage);
//				if (SelLanguage.equals(Tools.ToLocale("ϵͳ����")))SelLanguage = "ϵͳ����";
//				if (SelLanguage.equals(Tools.ToLocale("����")))SelLanguage = "����";
//				if (SelLanguage.equals(Tools.ToLocale("Ӣ��")))SelLanguage = "Ӣ��";
//				if (!BeforeLanguage.equals(SelLanguage))
//				{
//					HashMap<String,String> itemList = new HashMap<String,String>(); 
//					itemList.put("F2", SelLanguage);
//					
//					//����ѡ������
//					if (PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Tag_System_Language",itemList))
//					{
//						//��ʾ��������ϵͳ
//						Tools.ShowMessageBox(_DataDialog.getContext(), Tools.ToLocale("ϵͳ������һ������ʱ�л���")+"��"+Tools.ToLocale(SelLanguage)+"��"+Tools.ToLocale("���Ի���")+"��",new ICallback(){
//							@Override
//							public void OnClick(String Str, Object ExtraStr) {
//								_DataDialog.dismiss();
//							}});
//						return;
//					}
//				}
				_DataDialog.dismiss();
			}});
    	
    	this.LoadSystemInfo();
    	
    	//�������б�
    	v1_DataBind.SetBindListSpinner(_DataDialog, Tools.ToLocale("ѡ������"), new String[]{Tools.ToLocale("ϵͳ����"),Tools.ToLocale("����"),Tools.ToLocale("Ӣ��")}, R.id.spLanguage);
    	String BeforeLanguage = Tools.ToLocale(PubVar.m_HashMap.GetValueObject("Tag_System_Language").Value);
    	Tools.SetSpinnerValueOnID(_DataDialog, R.id.spLanguage, Tools.ToLocale(BeforeLanguage));
    	_DataDialog.findViewById(R.id.spLanguage).setEnabled(false);
    	
    	//�����Դ���
    	int[] localeViewId = new int[]{R.id.tvLocaleText,R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.tvLocaleText4,R.id.tvLocaleText5,R.id.tvLocaleText6,R.id.tvLocaleText7};
    	for(int id :localeViewId)
    	{
    		Tools.ToLocale(_DataDialog.findViewById(id));
    	}
    }
    
    /**
     * ����ϵͳ��Ϣ
     */
    private void LoadSystemInfo()
    {
    	String SoftName =  this.getApplicationName(); 
    	String VersionName = "";
    	try 
    	{
    		 PackageManager pm = PubVar.m_DoEvent.m_Context.getPackageManager();
    	     PackageInfo pinfo = pm.getPackageInfo(PubVar.m_DoEvent.m_Context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
    	     VersionName = pinfo.versionName;
    	} catch (NameNotFoundException e) {}

    	if(!PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo().SYS_UserType.equals("��ʽ�û�"))
    	{
    		VersionName+= "(���ð�)";
    	}
    	
    	AuthorizeTools_UserInfo UI = PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo();
        Tools.SetTextViewValueOnID(_DataDialog, R.id.welcome_softname,Tools.ToLocale(SoftName));
        Tools.SetTextViewValueOnID(_DataDialog, R.id.welcome_softver,VersionName);
        Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_code,UI.SYS_SoftCode);
//        if (UI.OT_UserMemo.equals("��������"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"�����Ƽ����ڿƼ����޹�˾\n0871-68015689(TEL)");
//        }
//        if (UI.OT_UserMemo.equals("�½�������ϧ��˾"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"������ϧ��˾\n��½�� 13369978833(TEL)\nhttp://www.fishgis.com");
//        }
//        if (UI.OT_UserMemo.equals("��������;"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"��������;�Ƽ��������޹�˾\n��ռ�� 13904504019(TEL)\n����:353138269");
//        }
//        if (UI.OT_UserMemo.equals("��Ԫ��˼(����)�Ƽ���չ���޹�˾"))
//        {
//        	_DataDialog.findViewById(R.id.l_about_developer).setVisibility(View.GONE);
//        }
//        if (UI.OT_UserMemo.equals("��������"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"028-87132006");
//        	_DataDialog.findViewById(R.id.l_about_developer).setVisibility(View.GONE);
//        }
        //_DataDialog.findViewById(R.id.l_about_developer).setVisibility(View.GONE);
        
        
        String AText = Tools.ToLocale("����Ȩ")+"��"+Tools.ToLocale(UI.SYS_UserType)+"��";
        if (UI.SYS_UserType.equals("�����û�"))AText+="\r\n"+Tools.ToLocale("��������")+"��"+UI.SYS_StopDate+"��";
        if (UI.SYS_UserType.equals("��ʱ�û�"))AText=Tools.ToLocale("δ��Ȩ")+"��"+Tools.ToLocale(UI.SYS_UserType)+"��";
        Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_pass,AText);
        Tools.SetTextViewValueOnID(_DataDialog,R.id.l_about_workpath, PubVar.m_SysAbsolutePath);
    }
    
    
    //��ȡӦ�ó� ������
    private String getApplicationName() 
    {
	  PackageManager packageManager = null;
	  ApplicationInfo applicationInfo = null;
	  try 
	  {
    	   packageManager = PubVar.m_DoEvent.m_Context.getApplicationContext().getPackageManager();
    	   applicationInfo = packageManager.getApplicationInfo(PubVar.m_DoEvent.m_Context.getPackageName(), 0);
	  } 
	  catch (PackageManager.NameNotFoundException e) 
	  {
		  applicationInfo = null;
	  }
	  String applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
	  return applicationName;
   }
    
    public void ShowDialog()
    {
    	_DataDialog.show();
    }
}
