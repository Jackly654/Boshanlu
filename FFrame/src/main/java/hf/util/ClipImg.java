package hf.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import hf.data.SFD;
import hf.lib.data.Logger;

/**
 * Created by fanjl on 2017-9-12.
 */

public class ClipImg
{
	public static void clipPhoto(Activity at, Uri uri, int outputX, int outputY)
	{
		try
		{
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
			intent.putExtra("crop", "true");
			// aspectX aspectY 是宽高的比例
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			// outputX outputY 是裁剪图片宽高
			intent.putExtra("outputX", outputX);
			intent.putExtra("outputY", outputY);
			intent.putExtra("scale", true); //黑边
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); // 图片格式
			intent.putExtra("noFaceDetection", false); // 人脸识别
			intent.putExtra("return-data", true);
			at.startActivityForResult(intent, SFD.RESULT_CLIP_HEAD);
		}
		catch(Exception ext)
		{
			Logger.e("clipPhoto()" + ext.toString());
		}
	}
}
