package com.example.list.myapplication;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    Context mContext;
    ArrayList<ListItem> list;
    SwipeRefreshLayout swipe_refresh;
    int count;
    int offset;
    int row_cnt;
    boolean is_scroll;
    boolean is_refresh;
    ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipe_refresh = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_widget);
//		swipe_refresh.setColorSchemeColors(Color.GREEN, Color.RED, Color.BLUE);
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            public void onRefresh() {

                init();
                mAdapter.notifyDataSetChanged();
                new NetworkGetList().execute("");

            }
        });

        list = new ArrayList<>();

        mAdapter = new ListAdapter(this, R.layout.item, list);

        ListView mList = (ListView)findViewById(R.id.home_list);

        mList.setAdapter(mAdapter);
        new NetworkGetList().execute("");

        mList.setOnScrollListener(new AbsListView.OnScrollListener() {

            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    if (count != 0 && offset % row_cnt == 0) {
                        if (is_scroll) {
                            is_scroll = false;
                            is_refresh = false;
                            new NetworkGetList().execute("");

                        }

                    }
                }
            }
        });

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem item = (ListItem)parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),item.getmRoomName()+"클릭 되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void init()
    {
        count = 0;
        offset = 0;
        row_cnt= 3;
        is_scroll = true;
        is_refresh = true;
        list.clear();
    }

    private class NetworkGetList extends AsyncTask<String, String, Integer>
    {
        private String err_msg = "Network error.";
        private ProgressBar pro_bar = (ProgressBar)findViewById(R.id.pro_bar);
        private JSONObject jobject;

        protected void onPreExecute() {

            if(!is_refresh)
            {
                pro_bar.setVisibility(View.VISIBLE);
            }

        }

        protected Integer doInBackground(String... params) {

            return processiong();
        }

        private Integer processiong() {

            try {
                HttpClient http_client = new DefaultHttpClient();
                http_client.getParams().setParameter("http.connection.timeout", 7000);
                HttpPost http_post = null;

                ArrayList<NameValuePair> name_value = new ArrayList<NameValuePair>();
                http_post = new HttpPost("http://54.149.51.26/api/get_room_list.php");
//			http_post = new HttpPost(uri);

                name_value.add(new BasicNameValuePair("offset", ""+offset));
                name_value.add(new BasicNameValuePair("row_cnt", ""+row_cnt));

                UrlEncodedFormEntity entityRequset;

                entityRequset = new UrlEncodedFormEntity(name_value, "UTF-8");
                http_post.setEntity(entityRequset);

                HttpResponse response = http_client.execute(http_post);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"), 8);
                StringBuilder builder = new StringBuilder();


                for(String line = null; (line=reader.readLine())!=null;)
                {
                    builder.append(line).append("\n");
                }

                jobject = new JSONObject(builder.toString());

                if(jobject.getInt("err")>0)
                {
                    return jobject.getInt("err");
                }

            } catch (UnsupportedEncodingException e){
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {

            pro_bar.setVisibility(View.GONE);
            swipe_refresh.setRefreshing(false);
            Log.e("result", result + "");
            if(result ==0)
            {
                try {

                    offset += jobject.getInt("cnt");
                    count = jobject.getInt("cnt");

                    for(int i=0;i<jobject.getInt("cnt"); i++)
                    {
                        ListItem item = new ListItem();
                        item.setmRoomKey(jobject.getJSONArray("ret").getJSONObject(i).getString("room_key"));
                        item.setmRoomName(jobject.getJSONArray("ret").getJSONObject(i).getString("room_name"));
                        item.setmRoomInfo(jobject.getJSONArray("ret").getJSONObject(i).getString("room_introduce"));
                        item.setmRoomLocation(jobject.getJSONArray("ret").getJSONObject(i).getString("room_location"));
                        item.setmRoomLanguage(jobject.getJSONArray("ret").getJSONObject(i).getString("room_language"));
                        item.setmRoomMaker(jobject.getJSONArray("ret").getJSONObject(i).getString("create_user_key"));
                        item.setmRoomImage(jobject.getJSONArray("ret").getJSONObject(i).getString("create_room_img_o"));
                        item.setmRoomMember(jobject.getJSONArray("ret").getJSONObject(i).getString("room_user_count"));
                        mAdapter.add(item);
                    }
                    mAdapter.notifyDataSetChanged();
                    is_scroll = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        init();
        mAdapter.notifyDataSetChanged();
        new NetworkGetList().execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
