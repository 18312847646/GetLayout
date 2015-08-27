package midsummer.getlayout;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

/**
 * 项目名称：GetLayout
 * 类描述：
 * 创建人：77.
 * 创建时间：2015/8/24 0024 20:51
 * 修改人：77.
 * 修改时间：2015/8/24 0024 20:51
 * 修改备注：
 */

public class MyService extends Service
{
	private Handler handler = new Handler();
	private Runnable run = new Runnable()
	{
		@Override
		public void run()
		{
			// 抓取资源文件
			try
			{
				getLayout();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			handler.postDelayed(run, 3000);
		}
	};
	
	@Override
	public void onCreate()
	{
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (intent != null)
		{
			// 解析布局文件
			// 循环
			// 开启循环
			handler.post(run);
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void getLayout() throws Exception
	{
		ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(5);
		for (ActivityManager.RunningTaskInfo info : runningTasks)
		{
			ComponentName topActivity = info.topActivity;
			String packageName = topActivity.getPackageName();
			Context otherContext = null;
			otherContext = this.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY | CONTEXT_INCLUDE_CODE);
			PathClassLoader pathClassLoader = new PathClassLoader(otherContext.getPackageResourcePath(), ClassLoader.getSystemClassLoader());
			Class<?> forName = Class.forName(packageName + ".R$layout", true, pathClassLoader);
			Field[] declaredFields = forName.getDeclaredFields();
			for (int j = 0; j < declaredFields.length; j++)
			{
				if (j < 50 && !topActivity.getClassName().contains("Launcher"))
				{
					// 获取50个资源文件Layout
					Field field = declaredFields[j];
					showLog("LayoutName:---->" + field.getName());
					// 通过ID来加载文件
					int resource = 0;
					XmlResourceParser parser = null;
					try
					{
						// 获取Layout的ID
						resource = field.getInt(R.layout.class);
						parser = otherContext.getResources().getLayout(resource);
					} catch (Resources.NotFoundException e)
					{
						e.printStackTrace();
					}
					// XML解析
					int type = parser.getEventType();
					showLog("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
					while (type != XmlResourceParser.END_DOCUMENT)
					{
						String tagName = "";
						// 解析XML分双标记和单标记
						List<String> layouts = new ArrayList<String>();
						List<String> views = new ArrayList<String>();
						switch (type)
						{
							case XmlResourceParser.START_TAG:
								// 解析到了一个开始标记
								// 标记名
								tagName = parser.getName();
								if (tagName.contains("Layout"))
								{
									layouts.add(tagName);
								} else
								{
									views.add(tagName);
								}
								showLog("<" + tagName);
								
								for (int i = 0; i < parser.getAttributeCount(); i++)
								{
									showLog(parser.getAttributeName(i) + "=" + parser.getAttributeValue(i));
								}
								break;
							case XmlResourceParser.END_TAG:
								// 结束标记
								String endTagName = parser.getName();
								if (endTagName.contains("Layout"))
								{
									if (layouts.contains(endTagName))
									{
										showLog(">");
										layouts.remove(endTagName);
									} else
									{
										showLog("</" + endTagName + ">");
									}
								} else
								{
									if (views.contains(endTagName))
									{
										showLog("/>");
									}
								}
								break;
						}
						type = parser.next();
					}
				}
			}
		}
	}
	
	protected void showLog(String show)
	{
		Log.i("77.", show);
	}
	
	@Nullable
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}