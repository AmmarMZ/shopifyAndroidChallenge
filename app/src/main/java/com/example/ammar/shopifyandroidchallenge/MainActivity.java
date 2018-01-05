package com.example.ammar.shopifyandroidchallenge;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
    //stores all the bitmaps, is referenced when querying but is never changed itself
    private ArrayList<Bitmap> bitmapList;
    //stores the queried images
    private ArrayList<Bitmap> queriedBitmapList;
    //stores all the picture details
    private ArrayList<JSONObject> details = new ArrayList<>();

    //way to reference the json file after the thread is finished
    String jsonItems;
    private Menu menu;

    //image adapter from https://www.thepolyglotdeveloper.com/2015/05/make-a-gallery-like-image-grid-using-native-android/
    //used in XML file
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

    //adapter for custom images in gridView
    private final class MyAdapter extends BaseAdapter
    {
        private final List<Item> mItems = new ArrayList<>();
        private final LayoutInflater mInflater;
        //items and their details
        ArrayList<JSONObject> items;
        //the associated items bitmaps
        private ArrayList<Bitmap> uploadedBitmapList;

        //adding grid items to grid
        public MyAdapter(Context context, ArrayList<JSONObject> items, ArrayList<Bitmap> bitmapList)
        {
            this.items = items;
            this.uploadedBitmapList = bitmapList;
            mInflater = LayoutInflater.from(context);
            for (int i = 0; i < items.size(); i++)
            {
                JSONObject temp = items.get(i);
                mItems.add(new Item((String)temp.get("title"),this.uploadedBitmapList.get(i),(String)temp.get("price")));
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
            final ImageView picture;
            TextView name;
            TextView title;

            //initializing widgets to their associated values
            if (v == null)
            {
                v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
                v.setTag(R.id.picture, v.findViewById(R.id.picture));
                v.setTag(R.id.text, v.findViewById(R.id.text));
                v.setTag(R.id.title, v.findViewById(R.id.title));
            }

            picture = (ImageView) v.getTag(R.id.picture);
            name = (TextView) v.getTag(R.id.text);
            title = (TextView) v.getTag(R.id.title);

            Item item = getItem(i);
            final int x = i;
            picture.setImageBitmap(item.drawableId);
            name.setText(item.name);
            title.setText(item.title);

            picture.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startIntent(x);
                }
            });
            name.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                  startIntent(x);
                }
            });
            title.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    startIntent(x);
                }
            });

            return v;
        }
        //opens up a bigger picture and details of the item selected
        public void startIntent(int x)
        {
            Intent intent = new Intent(getBaseContext(),DynamicImage.class);
            JSONObject temp = items.get(x);
            intent.putExtra("title",(String)temp.get("title"));
            intent.putExtra("bitmap",this.uploadedBitmapList.get(x));
            intent.putExtra("price",(String) temp.get("price"));
            intent.putExtra("pID",(Long) temp.get("product_id"));
            intent.putExtra("weight",(Double) temp.get("weight"));
            intent.putExtra("stock",(Long) temp.get("inventory_quantity"));
            System.out.println(temp.get("product_ID"));
            startActivity(intent);
        }

        private class Item
        {
            public final String name;
            public final String title;
            public final Bitmap drawableId;

            Item(String name, Bitmap drawableId, String title)
            {
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

        //get JSON from URL using threading/httpRequest
        JSONRunnable myRunnable = new JSONRunnable("https://shopicruit.myshopify.com/admin/products.json?page=1&access_token=c32313df0d0ef512ca64d5b336a0d7c6");
        Thread thread = new Thread(myRunnable);
        thread.start();
        while(thread.isAlive())
        { }
        JSONParser jsonParser = new JSONParser();
        ArrayList<String> srcs = new ArrayList<>();
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
                    tempDetails.put("inventory_quantity",variant1.get("inventory_quantity"));
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
        //thread1 adds the pictures to the gridView async so that they are updated in realTime without having to manually refresh
        Thread thread1 = new Thread(imageRunnable);
        thread1.start();
        while(thread1.isAlive())
        { /*wait for thread1 to finish */}
        this.imageGrid.setAdapter(new MyAdapter(getBaseContext(),details,bitmapList));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //set query listener for searchView
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return true;
            }

            //search updates as user types, no need to click enter
            @Override
            public boolean onQueryTextChange(String newText)
            {
                if (newText.length() == 0)
                {
                   imageGrid.setAdapter(new MyAdapter(getBaseContext(),details,bitmapList));
                    return true;
                }
                ArrayList<JSONObject> newList = queryList(newText);
                imageGrid.setAdapter(new MyAdapter(getBaseContext(),newList,queriedBitmapList));
                return true;
            }
        });

        return true;
    }

    //updates the bitmap and returns the details of the items queried
    public ArrayList<JSONObject> queryList(String query)
    {
        ArrayList<JSONObject> meetsQuery = new ArrayList<>();
        queriedBitmapList = new ArrayList<>();
        query = query.toLowerCase();
        for (int i = 0; i < details.size(); i ++)
        {
            JSONObject temp = details.get(i);
            String name = (String) temp.get("title");
            name = name.toLowerCase();
            if (name.contains(query))
            {
                meetsQuery.add(temp);
                queriedBitmapList.add(bitmapList.get(i));
            }
        }
        return meetsQuery;
    }

}
