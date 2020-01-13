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
    	_DataDialog.SetCaption(Tools.ToLocale("关于系统"));
    	_DataDialog.SetButtonInfo("1,"+R.drawable.v1_ok+","+Tools.ToLocale("确定")+"  ,确定", new ICallback(){

			@Override
			public void OnClick(String Str, Object ExtraStr) 
			{
//				//上一次语言设置
//				String BeforeLanguage = PubVar.m_HashMap.GetValueObject("Tag_System_Language").Value;
//				String SelLanguage = Tools.GetSpinnerValueOnID(_DataDialog, R.id.spLanguage);
//				if (SelLanguage.equals(Tools.ToLocale("系统语言")))SelLanguage = "系统语言";
//				if (SelLanguage.equals(Tools.ToLocale("中文")))SelLanguage = "中文";
//				if (SelLanguage.equals(Tools.ToLocale("英文")))SelLanguage = "英文";
//				if (!BeforeLanguage.equals(SelLanguage))
//				{
//					HashMap<String,String> itemList = new HashMap<String,String>(); 
//					itemList.put("F2", SelLanguage);
//					
//					//保存选择语言
//					if (PubVar.m_DoEvent.m_UserConfigDB.GetUserParam().SaveUserPara("Tag_System_Language",itemList))
//					{
//						//提示重新引导系统
//						Tools.ShowMessageBox(_DataDialog.getContext(), Tools.ToLocale("系统将在下一次启动时切换至")+"【"+Tools.ToLocale(SelLanguage)+"】"+Tools.ToLocale("语言环境")+"！",new ICallback(){
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
    	
    	//绑定语言列表
    	v1_DataBind.SetBindListSpinner(_DataDialog, Tools.ToLocale("选择语言"), new String[]{Tools.ToLocale("系统语言"),Tools.ToLocale("中文"),Tools.ToLocale("英文")}, R.id.spLanguage);
    	String BeforeLanguage = Tools.ToLocale(PubVar.m_HashMap.GetValueObject("Tag_System_Language").Value);
    	Tools.SetSpinnerValueOnID(_DataDialog, R.id.spLanguage, Tools.ToLocale(BeforeLanguage));
    	_DataDialog.findViewById(R.id.spLanguage).setEnabled(false);
    	
    	//多语言处理
    	int[] localeViewId = new int[]{R.id.tvLocaleText,R.id.tvLocaleText1,R.id.tvLocaleText2,R.id.tvLocaleText3,R.id.tvLocaleText4,R.id.tvLocaleText5,R.id.tvLocaleText6,R.id.tvLocaleText7};
    	for(int id :localeViewId)
    	{
    		Tools.ToLocale(_DataDialog.findViewById(id));
    	}
    }
    
    /**
     * 加载系统信息
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

    	if(!PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo().SYS_UserType.equals("正式用户"))
    	{
    		VersionName+= "(试用版)";
    	}
    	
    	AuthorizeTools_UserInfo UI = PubVar.m_DoEvent.m_AuthorizeTools.GetUserInfo();
        Tools.SetTextViewValueOnID(_DataDialog, R.id.welcome_softname,Tools.ToLocale(SoftName));
        Tools.SetTextViewValueOnID(_DataDialog, R.id.welcome_softver,VersionName);
        Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_code,UI.SYS_SoftCode);
//        if (UI.OT_UserMemo.equals("云南昆明"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"昆明云集汇众科技有限公司\n0871-68015689(TEL)");
//        }
//        if (UI.OT_UserMemo.equals("新疆阿克苏惜金公司"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"阿克苏惜金公司\n张陆军 13369978833(TEL)\nhttp://www.fishgis.com");
//        }
//        if (UI.OT_UserMemo.equals("哈尔滨与途"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"哈尔滨与途科技开发有限公司\n戴占生 13904504019(TEL)\n在线:353138269");
//        }
//        if (UI.OT_UserMemo.equals("天元集思(北京)科技发展有限公司"))
//        {
//        	_DataDialog.findViewById(R.id.l_about_developer).setVisibility(View.GONE);
//        }
//        if (UI.OT_UserMemo.equals("不等明天"))
//        {
//        	Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_developer,"028-87132006");
//        	_DataDialog.findViewById(R.id.l_about_developer).setVisibility(View.GONE);
//        }
        //_DataDialog.findViewById(R.id.l_about_developer).setVisibility(View.GONE);
        
        
        String AText = Tools.ToLocale("已授权")+"【"+Tools.ToLocale(UI.SYS_UserType)+"】";
        if (UI.SYS_UserType.equals("试用用户"))AText+="\r\n"+Tools.ToLocale("试用期至")+"【"+UI.SYS_StopDate+"】";
        if (UI.SYS_UserType.equals("临时用户"))AText=Tools.ToLocale("未授权")+"【"+Tools.ToLocale(UI.SYS_UserType)+"】";
        Tools.SetTextViewValueOnID(_DataDialog, R.id.l_about_pass,AText);
        Tools.SetTextViewValueOnID(_DataDialog,R.id.l_about_workpath, PubVar.m_SysAbsolutePath);
    }
    
    
    //获取应用程 序名称
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
