package com.example.ammar.shopifyandroidchallenge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity extends AppCompatActivity
{

    private GridView imageGrid;
    private ArrayList<Bitmap> bitmapList;

    //way to reference the json file after the thread is finished
    String jsonItems;

    //image adapter from https://www.thepolyglotdeveloper.com/2015/05/make-a-gallery-like-image-grid-using-native-android/
    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private ArrayList<Bitmap> bitmapList;

        public ImageAdapter(Context context, ArrayList<Bitmap> bitmapList)
        {
            this.context = context;
            this.bitmapList = bitmapList;
        }

        public int getCount()
        {
            return this.bitmapList.size();
        }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView;
            if (convertView == null)
            {
                imageView = new ImageView(this.context);
                imageView.setLayoutParams(new GridView.LayoutParams(115, 115));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            else
            {
                imageView = (ImageView) convertView;
            }
            imageView.setImageBitmap(this.bitmapList.get(position));
            return imageView;
        }
    }
    //runnable gets json file from URL
    public class JSONRunnable implements Runnable
    {
        private String url;
        public JSONRunnable(String url)
        {
            this.url = url;
        }
        public void run()
        {
            jsonItems = getJSON(this.url);
        }
    }

    //runnable gets json file from URL
    public class ImageRunnable implements Runnable
    {
        private ArrayList<String> srcs;
        public ImageRunnable(ArrayList<String> srcs)
        {
            this.srcs = srcs;
        }
        public void run()
        {
            try
            {
                for(int i = 0; i < this.srcs.size(); i++)
                {
                    bitmapList.add(urlImageToBitmap(srcs.get(i)));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    //function taken from stackoverflow to get json from URL, used in the runnable
    public static String getJSON(String url)
    {
        HttpsURLConnection con = null;
        try
        {
            URL u = new URL(url);
            con = (HttpsURLConnection) u.openConnection();
            con.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            br.close();
            return sb.toString();
        }
        catch (MalformedURLException ex)
        {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (con != null)
            {
                try
                {
                    con.disconnect();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    //urlToBitmap function from https://www.thepolyglotdeveloper.com/2015/05/make-a-gallery-like-image-grid-using-native-android/
    private Bitmap urlImageToBitmap(String imageUrl) throws Exception
    {
        Bitmap result = null;
        URL url = new URL(imageUrl);
        if(url != null)
        {
            result = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        return result;
    }

    private final class MyAdapter extends BaseAdapter {
        private final List<Item> mItems = new ArrayList<>();
        private final LayoutInflater mInflater;

        public MyAdapter(Context context, ArrayList<JSONObject> items)
        {
            mInflater = LayoutInflater.from(context);
            for (int i = 0; i < items.size(); i++)
            {
                JSONObject temp = items.get(i);
                mItems.add(new Item((String)temp.get("title"),bitmapList.get(i),(String)temp.get("price")));
            }
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Item getItem(int i) {
            return mItems.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = view;
            ImageView picture;
            TextView name;
            TextView title;

            if (v == null) {
                v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
                v.setTag(R.id.title, v.findViewById(R.id.title));
            }

            picture = (ImageView) v.getTag(R.id.picture);
            name = (TextView) v.getTag(R.id.text);
            title = (TextView) v.getTag(R.id.title);

            Item item = getItem(i);

            picture.setImageBitmap(item.drawableId);
            name.setText(item.name);
            title.setText(item.title);


            return v;
        }

        private class Item {
            public final String name;
            public final String title;
            public final Bitmap drawableId;

            Item(String name, Bitmap drawableId, String title) {
                this.name = "$" + title;
                this.drawableId = drawableId;
                this.title =  name;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JSONRunnable myRunnable = new JSONRunnable("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6");
        Thread thread = new Thread(myRunnable);
        thread.start();
        while(thread.isAlive())
        {/*wait for thread to finish */}
        JSONParser jsonParser = new JSONParser();
        ArrayList<String> srcs = new ArrayList<>();
        ArrayList<JSONObject> details = new ArrayList<>();
        try
        {
            //stores the srcs for pictures
           // ArrayList<String> srcs = new ArrayList<>();
            //image details of the respective src
            //parse the returned list from the URL
            JSONObject list = (JSONObject) jsonParser.parse(jsonItems);
            JSONArray products = (JSONArray) list.get("products");
            for (int i = 0; i < products.size(); i ++)
            {
                //looking at every image and image details
                JSONObject temp = (JSONObject) products.get(i);
                JSONArray imageArray = (JSONArray) temp.get("images");
                JSONArray variants = (JSONArray) temp.get("variants");
                JSONObject variant1 = (JSONObject) variants.get(0);

                for (int j = 0; j < imageArray.size(); j++)
                {
                    JSONObject tempObj = (JSONObject) imageArray.get(j);
                    JSONObject tempDetails = new JSONObject();
                    //add 4 details to array to be referenced later for pictures
                    tempDetails.put("product_id",variant1.get("product_id"));
                    tempDetails.put("weight",variant1.get("weight"));
                    tempDetails.put("price",variant1.get("price"));
                    tempDetails.put("title",temp.get("title"));
                    //src is added here to act as an FK between details and the imageView
                    tempDetails.put("src",tempObj.get("src"));
                    details.add(tempDetails);
                    //create src array for easy access to display pictures
                    srcs.add((String) tempObj.get("src"));
                }
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        this.imageGrid = (GridView) findViewById(R.id.gridview);
        this.bitmapList = new ArrayList<>();

        ImageRunnable imageRunnable = new ImageRunnable(srcs);
        Thread thread1 = new Thread(imageRunnable);
        thread1.start();
        while(thread1.isAlive())
        { /*wait for thread1 to finish */}
        this.imageGrid.setAdapter(new MyAdapter(getBaseContext(),details));
    }
}
