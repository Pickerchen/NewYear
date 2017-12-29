package com.ipcamera.main.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Base64;

/**
 * 图片工具类
 */
public final class ImageTools {

	/**
	 * 从资源中获取Bitmap
	 * 
	 * @param id
	 *            drawable资源的ID
	 * @return 返回Bitmap图片
	 */
	public static Bitmap fromResGetBmp(Context context, int id) {

		Resources res = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, id);
		return bmp;
	}

	/**
	 * 从资源中获取Drawable
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static Drawable fromResGetDrawable(Context context, int id) {

		Drawable drawable = context.getResources().getDrawable(id);
		return drawable;
	}

	/**
	 * drawable转化为bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 关于 图片转化 质量的设置 ， ARGB_8888>RGB_565，还有其他几种模式可以上网查找，这里就不做一一说明了
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Bitmap转化为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	/**
	 * Input stream转化为bitmap
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static Bitmap inputStreamToBitmap(InputStream inputStream)
			throws Exception {
		return BitmapFactory.decodeStream(inputStream);
	}

	/**
	 * Byte transfer转化为bitmap
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Bitmap byteToBitmap(byte[] byteArray) {
		if (byteArray.length != 0) {
			return BitmapFactory
					.decodeByteArray(byteArray, 0, byteArray.length);
		} else {
			return null;
		}
	}

	/**
	 * Byte transfer转化为drawable
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Drawable byteToDrawable(byte[] byteArray) {
		ByteArrayInputStream ins = null;
		if (byteArray != null) {
			ins = new ByteArrayInputStream(byteArray);
		}
		return Drawable.createFromStream(ins, null);
	}

	/**
	 * Bitmap transfer转化为bytes
	 * 
	 * @param byteArray
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bm) {

		byte[] bytes = null;
		if (bm != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			bytes = baos.toByteArray();
		}
		return bytes;
	}

	/**
	 * Drawable transfer转化为bytes
	 * 
	 * @param drawable
	 * @return
	 */
	public static byte[] drawableToBytes(Drawable drawable) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
		Bitmap bitmap = bitmapDrawable.getBitmap();
		byte[] bytes = bitmapToBytes(bitmap);
		;
		return bytes;
	}

	/**
	 * Base64转化为byte[]
	 */
	public static byte[] base64ToBytes(String base64) throws IOException {
		byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
		return bytes;
	}

	/**
	 * Byte[]转化为base64
	 */
	public static String bytesTobase64(byte[] bytes) {
		String base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
		return base64;
	}

	/**
	 * Create 倒影 images
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
				h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 获取圆角的图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            5 10
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 调整图片（类型：bitmap）大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}

	/**
	 * 调整图片（类型：bitmap）大小
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap) {

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();

		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h - 50);
		return newbmp;
	}

	/**
	 * 调整图片（类型：drawable）大小
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Drawable zoomDrawable(Context context, Drawable drawable,
			int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		matrix.postScale(sx, sy);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(context.getResources(), newbmp);
	}

	/**
	 * 从SDcard中获取图片
	 * 
	 * @param photoName
	 * @return
	 */
	public static Bitmap getPhotoFromSDCard(String path, String photoName) {
		Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName
				+ ".jpg");
		if (photoBitmap == null) {
			return null;
		} else {
			return photoBitmap;
		}
	}

	/**
	 * 根据路径获取图片，如没有则返回默认图片
	 * 
	 * @param path
	 * @param defaultBitmap
	 * @return
	 */
	public static Bitmap getBitmapForPath(String path, Bitmap defaultBitmap) {
		Bitmap decodeFile = BitmapFactory.decodeFile(path);
		if (decodeFile == null) {
			return defaultBitmap;
		}
		return decodeFile;
	}

	/**
	 * 检测SDcard是否存在
	 * 
	 * @return
	 */
	public static boolean checkSDCardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断SDcard中是否存在此图片
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean findPhotoFromSDCard(String path, String photoName) {
		boolean flag = false;

		if (checkSDCardAvailable()) {
			File dir = new File(path);
			if (dir.exists()) {
				File folders = new File(path);
				File photoFile[] = folders.listFiles();
				for (int i = 0; i < photoFile.length; i++) {
					String fileName = photoFile[i].getName().split("\\.")[0];
					if (fileName.equals(photoName)) {
						flag = true;
					}
				}
			} else {
				flag = false;
			}
			// File file = new File(path + "/" + photoName + ".jpg" );
			// if (file.exists()) {
			// flag = true;
			// }else {
			// flag = false;
			// }

		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 保存图片在SDcard中
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static boolean savePhotoToSDCard(Bitmap photoBitmap, String absolutePath,
			String photoName) {
		boolean bool = false;
		
		if (checkSDCardAvailable()) {
		
			File dir = new File(absolutePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File fileOld = new File(absolutePath + File.separator + photoName);
			if (fileOld.exists()) {
				fileOld.delete();
			}

			File photoFile = new File(absolutePath, photoName);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
						// fileOutputStream.close();
					}
				}
				bool = true;
				DLog.e("------------------------0");
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
				bool = false;
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
				bool = false;
			} finally {
				DLog.e("------------------------1");
				try {
					if(null != fileOutputStream){
						
						fileOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		DLog.e("------------------------2");
		return bool;
	}

	/**
	 * 保存图片在SDcard中
	 * 
	 * @param photoBitmap
	 * @param photoName
	 * @param path
	 */
	public static boolean savePhotoToSDCardFile(Bitmap photoBitmap, String fileName,
			String photoName) {
		boolean bool = false;
		
		if (checkSDCardAvailable()) {
			File absoluteFile = Environment.getExternalStorageDirectory().getAbsoluteFile();
			String absolutePath = absoluteFile.getAbsolutePath();
			
			fileName = absolutePath + File.separator + fileName;
			
			File dir = new File(fileName);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File fileOld = new File(fileName + File.separator + photoName);
			if (fileOld.exists()) {
				fileOld.delete();
			}

			File photoFile = new File(fileName, photoName);
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(photoFile);
				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
						// fileOutputStream.close();
					}
				}
				bool = true;
				DLog.e("------------------------0");
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
				bool = false;
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
				bool = false;
			} finally {
				DLog.e("------------------------1");
				try {
					if(null != fileOutputStream){
						
						fileOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		DLog.e("------------------------2");
		return bool;
	}
	
	/**
	 * 从SDcard中删除图片（可以删除整个目录的，或者单张的）
	 * 
	 * @param context
	 * @param path
	 *            file:///sdcard/temp.jpg
	 */
	public static void deleteAllPhoto(String path) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}

	/**
	 * 从SDcard中删除规定的图片
	 * 
	 * @param path
	 * @param fileName
	 */
	public static void deletePhotoAtPathAndName(String path, String fileName) {
		if (checkSDCardAvailable()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().split("\\.")[0].equals(fileName)) {
					files[i].delete();
				}
			}
		}
	}

	/**
	 * 获取drawable需要的图片
	 * 
	 * @param context
	 * @param arrayImage
	 * @return
	 */
	public static ArrayList<Integer> getImagesByArray(Context context,
			int arrayImage) {

		ArrayList<Integer> images = new ArrayList<Integer>();
		String packageName = context.getResources().getResourcePackageName(
				arrayImage);
		String[] extras = context.getResources().getStringArray(arrayImage);
		for (String extra : extras) {
			int res = context.getResources().getIdentifier(extra, "drawable",
					packageName);
			if (res != 0) {
				images.add(res);
			}
		}
		return images;
	}

	public static Bitmap getVidioBitmap(String filePath, int width, int height,
			int kind) {
		// 定義一個Bitmap對象bitmap；
		Bitmap bitmap = null;

		// ThumbnailUtils類的截取的圖片是保持原始比例的，但是本人發現顯示在ImageView控件上有时候有部分沒顯示出來；
		// 調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
		bitmap = ThumbnailUtils.createVideoThumbnail(filePath, kind);
		DLog.e("------------------bitmap0 = " + bitmap);
		// 調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
		// 最後一個參數的具體含義我也不太清楚，因為是閉源的；
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

		// 放回bitmap对象；
		DLog.e("------------------bitmap = " + bitmap);
		return bitmap;
	}

	public static Bitmap createVideoThumbnail(String url, int width, int height) {
		Bitmap bitmap = null;
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		int kind = MediaStore.Video.Thumbnails.MINI_KIND;
		try {
			if (Build.VERSION.SDK_INT >= 14) {
				retriever.setDataSource(url, new HashMap<String, String>());
			} else {
				retriever.setDataSource(url);
			}
			// bitmap = retriever.getFrameAtTime();
			bitmap = retriever.getFrameAtTime(5 * 1000 * 1000,
					MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			DLog.e("------------------bitmap1 = " + bitmap);
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} finally {
			try {
				retriever.release();
			} catch (RuntimeException ex) {
				// Ignore failures while cleaning up.
			}
		}

		if (kind == Images.Thumbnails.MICRO_KIND && bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		DLog.e("------------------bitmap2 = " + bitmap);
		return bitmap;
	}

	public static void getBitmapsFromVideo(String dataPath) {
		String dataPath1 = Environment.getExternalStorageDirectory()
				+ "/testVideo.mp4";
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(dataPath);
		// 取得视频的长度(单位为毫秒)
		String time = retriever
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		// 取得视频的长度(单位为秒)
		int seconds = Integer.valueOf(time) / 1000;
		// 得到每一秒时刻的bitmap比如第一秒,第二秒
		for (int i = 1; i <= 3; i++) {
			Bitmap bitmap = retriever.getFrameAtTime(i * 1000 * 1000,
					MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
			String path = Environment.getExternalStorageDirectory()
					+ File.separator + i + ".jpg";
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(path);
				bitmap.compress(CompressFormat.JPEG, 80, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 根据原图和变长绘制圆形图片
	 * 
	 * @param source
	 * @param min
	 * @return
	 */
	public static Bitmap createCircleImage(Bitmap source, int min) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 首先绘制圆形
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * 使用SRC_IN
		 */
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

}
