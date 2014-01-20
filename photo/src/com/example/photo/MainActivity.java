package com.example.photo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;









import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	final String t = "http://api-fotki.yandex.ru/api/recent/";
	static Bitmap[] image_Bitmap = new Bitmap[20];
	GridView my;
	ArrayList<String> link = new ArrayList<String>();
	ImageAdapter imageAdapter;
	String ans;
    int my_flag = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageAdapter = new ImageAdapter(this,android.R.layout.simple_list_item_1);
		Button update = (Button)findViewById(R.id.button1);
		my = (GridView) findViewById(R.id.gridView1);
		 adjustGridView();
		update.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				System.out.println(my_flag);
				if (my_flag == 0){
					my_flag++;
				MyTask w = new MyTask();
				w.execute();
				}
			    
				
				
			}
		});
		
		MyTask q = new MyTask();
		q.execute();
		/*my.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public  void onItemClick(AdapterView<?> parent, View p, int position,long id){
				final Intent intent = new Intent(MainActivity.this, big_photo.class);
				intent.putExtra("key", position);
				startActivity(intent);
				
			}
		});*/
		
		
	}
	public void adjustGridView() {
		Display display = getWindowManager().getDefaultDisplay();
		Point size = getDisplaySize(display);
		
		int width = size.x;
		int height = size.y;
		if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			
			my.setNumColumns(2);
			my.setColumnWidth((int)(width*0.35));
		} else 
		{
			
			my.setNumColumns(4);
			my.setColumnWidth((int)(width*0.2));
		}
		my.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
	    
	  }
	

	public class MyTask extends AsyncTask<Void, Void, Void> {

		@Override
		public Void doInBackground(Void... params) {
			link.clear();
			try {
				URL url = new URL(t);
				URLConnection connection;
				connection = url.openConnection();
				if (connection != null){
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				String tek = "";
				while ((tek = reader.readLine()) != null) {

					ans = tek;

					if (link.size() == 20) {
						break;
					}
					if (ans.indexOf("f:img") != -1) {
						String q = parse();
						if (!q.equals("")){
						link.add(parse());
						}
					}
				}
				reader.close();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}
			for (int i = 0; i < 20; i++) {
				  
				try { 
					
					  InputStream input = new  URL(link.get(i)).openStream(); 
					  image_Bitmap[i] = BitmapFactory.decodeStream(input); 
					  input.close(); 
					  } catch (IOException e) 
					  {
						  e.printStackTrace(); 
						  } // Decode Bitmap
						  
						 }
				
			return null;
		}
		
protected void onPostExecute(Void params) {
            //ImageView n = (ImageView)findViewById(R.id.imageView1);
            //n.setImageBitmap(image_Bitmap[10]);
	
	if (image_Bitmap[0] != null){
		
	
	
	my.setOnItemClickListener(new AdapterView.OnItemClickListener()
	{
		@Override
		public  void onItemClick(AdapterView<?> parent, View p, int position,long id){
			final Intent intent = new Intent(MainActivity.this, big_photo.class);
			intent.putExtra("key", position);
			startActivity(intent);
			
		}
	});
	imageAdapter.clear();
	for(int i = 0; i < 20; i++)
		imageAdapter.add(i);
	imageAdapter.notifyDataSetChanged();
	my.setAdapter(imageAdapter);
	
	String ok = "Load finished";
	Toast toast = Toast.makeText(getApplicationContext(),ok,Toast.LENGTH_LONG);
	toast.setGravity(Gravity.CENTER, 0, 0);
	toast.show();
	
	}
	if (my_flag > 0 ){
		my_flag--;
	}
	
}


	
		String parse() {
			int a = 1;

			int flag = 0;
			String tek = "";
			for (int x = a; ans.charAt(x) != '>'; x++) {
				if (ans.charAt(x) == '"') {
					flag++;
				} else {
					if (flag == 3) {
						tek += ans.charAt(x);

					} else {
						if (flag == 4) {
							flag++;

							if (tek.charAt(tek.length() - 1) == 'M') {
								return tek;

							}

							break;
						}
					}
				}
			}
			return "";

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public class ImageAdapter extends ArrayAdapter<Integer> {
	
       public ImageAdapter(Context context, int resource) {
			super(context, resource);
			mContext = context;
			
		}

	Context mContext;
       
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null)
			{
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
			} else
			{
				imageView = (ImageView) convertView;
			}
			/*Display display = getWindowManager().getDefaultDisplay();
			Point size = getDisplaySize(display);
			imageView.setAdjustViewBounds(true);
			int width = size.x;
			int height = size.y;
			if (width < height){*/
			
			if (position <= 19){
			imageView.setImageBitmap(image_Bitmap[position]);
			} else 
			{
				imageView.setImageBitmap(image_Bitmap[0]);
			}
			
			return imageView;
		}
		
		

	}
	@SuppressLint("NewApi")
	public   Point getDisplaySize(final Display display) {
		Point point = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) { // API
																			// LEVEL
																			// 13
			display.getSize(point);
		} else {
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		return point;
	}


}
