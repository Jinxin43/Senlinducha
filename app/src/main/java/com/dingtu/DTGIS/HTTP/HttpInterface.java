package com.dingtu.DTGIS.HTTP;

import java.util.Map;

import com.dingtu.DTGIS.Upload.HttpDataDto;
import com.dingtu.DTGIS.Upload.HttpDeviceModel;
import com.dingtu.DTGIS.Upload.HttpLayermodel;
import com.dingtu.DTGIS.Upload.HttpProjectModel;
import com.dingtu.DTGIS.Upload.HttpTracesModel;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public interface HttpInterface {
	
	 	@POST("/api/GISManagerControlller/{action}")
	    //Call<ResponseBody> login(@Path("action") String action, @Query("mIdString")String Account,@Query("mPwdString")String Password,@Query("Device")String Device);
	    Call<ResponseBody> CreateProject(@Path("action") String action, @Body HttpProjectModel projectmodel);

	    @POST("/api/GISManagerControlller/{action}")
	    Call<ResponseBody> CreateLayer(@Path("action") String action, @Body HttpLayermodel layermodel);

	    @POST("/api/GISManagerControlller/{action}")
	    Call<ResponseBody> AddLayerData(@Path("action") String action, @Body HttpDataDto dataDto);
	    @POST("/api/GISManagerControlller/{action}")
	    Call<ResponseBody> AddLayerDataWithEPSG(@Path("action") String action, @Body HttpDataDto dataDto);

	    @Multipart
	    @POST("/api/GISManagerControlller/{action}")
	    Call<ResponseBody> UploadDataPhoto(@Path("action") String action, @PartMap Map<String, RequestBody> params);
	    
	    @POST("/api/GISManagerControlller/{action}")
	    Call<ResponseBody> UploadTraces(@Path("action") String action, @Body HttpTracesModel dataDto);
	    
	    @POST("/api/GISManagerControlller/{action}")
	    Call<ResponseBody> CreateDevice(@Path("action") String action, @Body HttpDeviceModel dataDto);
}
