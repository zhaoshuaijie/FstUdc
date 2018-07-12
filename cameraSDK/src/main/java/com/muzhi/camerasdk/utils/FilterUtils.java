package com.muzhi.camerasdk.utils;

import android.content.Context;

import java.util.ArrayList;

import com.muzhi.camerasdk.R;
import com.muzhi.camerasdk.library.filter.util.ImageFilterTools.FilterType;
import com.muzhi.camerasdk.model.Filter_Effect_Info;
import com.muzhi.camerasdk.model.Filter_Sticker_Info;


/**
 * 特效文件
 */
public class FilterUtils {


    /**
     * 获取特效列表
     *
     * @return
     */
    public static ArrayList<Filter_Effect_Info> getEffectList() {

        ArrayList<Filter_Effect_Info> effect_list = new ArrayList<Filter_Effect_Info>();

        effect_list.add(new Filter_Effect_Info("原图", R.drawable.camerasdk_filter_normal, null));
        effect_list.add(new Filter_Effect_Info("创新", R.drawable.camerasdk_filter_in1977, FilterType.I_1977));
        effect_list.add(new Filter_Effect_Info("流年", R.drawable.camerasdk_filter_amaro, FilterType.I_AMARO));
        effect_list.add(new Filter_Effect_Info("淡雅", R.drawable.camerasdk_filter_brannan, FilterType.I_BRANNAN));
        effect_list.add(new Filter_Effect_Info("怡尚", R.drawable.camerasdk_filter_early_bird, FilterType.I_EARLYBIRD));
        effect_list.add(new Filter_Effect_Info("优格", R.drawable.camerasdk_filter_hefe, FilterType.I_HEFE));
        effect_list.add(new Filter_Effect_Info("胶片", R.drawable.camerasdk_filter_hudson, FilterType.I_HUDSON));
        effect_list.add(new Filter_Effect_Info("黑白", R.drawable.camerasdk_filter_inkwell, FilterType.I_INKWELL));
        effect_list.add(new Filter_Effect_Info("个性", R.drawable.camerasdk_filter_lomo, FilterType.I_LOMO));
        effect_list.add(new Filter_Effect_Info("回忆", R.drawable.camerasdk_filter_lord_kelvin, FilterType.I_LORDKELVIN));
        effect_list.add(new Filter_Effect_Info("不羁", R.drawable.camerasdk_filter_nashville, FilterType.I_NASHVILLE));
        effect_list.add(new Filter_Effect_Info("森系", R.drawable.camerasdk_filter_rise, FilterType.I_NASHVILLE));
        effect_list.add(new Filter_Effect_Info("清新", R.drawable.camerasdk_filter_sierra, FilterType.I_SIERRA));
        effect_list.add(new Filter_Effect_Info("摩登", R.drawable.camerasdk_filter_sutro, FilterType.I_SUTRO));
        effect_list.add(new Filter_Effect_Info("绚丽", R.drawable.camerasdk_filter_toaster, FilterType.I_TOASTER));
        effect_list.add(new Filter_Effect_Info("优雅", R.drawable.camerasdk_filter_valencia, FilterType.I_VALENCIA));
        effect_list.add(new Filter_Effect_Info("日系", R.drawable.camerasdk_filter_walden, FilterType.I_WALDEN));
        effect_list.add(new Filter_Effect_Info("新潮", R.drawable.camerasdk_filter_xproii, FilterType.I_XPROII));

        return effect_list;

    }


    /**
     * 获取所有贴纸
     *
     * @return
     */
    public static ArrayList<Filter_Sticker_Info> getStickerList() {

        ArrayList<Filter_Sticker_Info> stickerList = new ArrayList<Filter_Sticker_Info>();
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers528));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers529));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers530));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers531));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers532));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers533));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers534));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers535));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers536));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers537));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers538));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers539));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers540));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers541));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers542));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers543));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers544));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers545));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers546));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers547));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers548));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers549));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers550));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers551));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers552));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers553));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers554));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers555));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers556));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers557));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers558));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers559));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers560));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers561));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers562));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers563));
        stickerList.add(new Filter_Sticker_Info(R.drawable.stickers564));
        //stickerList.add(new Filter_Sticker_Info(R.drawable.camerasdk_stickers,true));
        return stickerList;

    }


    /**
     * 获取所有贴纸
     *
     * @return
     */
    public static void initSticker(Context mContext) {

		
		/*String fileName="sticker/sticker.txt";
    	try{
    		 InputStreamReader inputReader = new InputStreamReader(mContext.getAssets().open(fileName),Charset.defaultCharset()); 
    		 BufferedReader bufReader = new BufferedReader(inputReader);
             String line="";
             String Result="";
             while((line = bufReader.readLine()) != null){
            	 Result += line.trim(); 
             }
             bufReader.close();
             inputReader.close(); 
             if(!"".equals(Result)){                 	
             	emotions=getGson().fromJson(Result, new TypeToken<List<EmotionInfo>>(){}.getType());
             	getEmotionsTask();
             }
    	}
    	catch(Exception e){
    		 e.printStackTrace(); 
    	} */


    }
    
	/*private static void getEmotionsTask() {
    	
    	AssetManager assetManager = AppData.getInstance().getAssets();
    	InputStream inputStream;
    	for(EmotionInfo em : emotions){
    		String fileName="smiley/smileys/"+em.getFileName();
    		//String smileyName=em.getSmileyString();
    		try{
    			inputStream = assetManager.open(fileName);
    			Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    			if(bitmap!=null){
    				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, CommonUtils.dip2px(20), CommonUtils.dip2px(20), true);
    				if(bitmap != scaledBitmap){
    					bitmap.recycle();
    					bitmap = scaledBitmap;
    				}	    				
    				em.setEmotionImage(bitmap);
    			} 
    			inputStream.close();
    		}
    		catch (IOException ignored) {

            }
    	}    	
    }*/


}
