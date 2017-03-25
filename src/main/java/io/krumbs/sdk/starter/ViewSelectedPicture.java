package io.krumbs.sdk.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import io.krumbs.sdk.starter.R;

public class ViewSelectedPicture extends AppCompatActivity {
    String url, date, username, query, emoji,category,location;
    Button button, button1, button2, button3;
    DatabaseHelper dh = new DatabaseHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_selected_picture);
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        date = bundle.getString("date");
        username = bundle.getString("username");
        query = bundle.getString("query");
        ImageView image = (ImageView) findViewById(R.id.imageViewEnlarged);
        image.setRotation(90);
        button = (Button) findViewById(R.id.button);
        button1 = (Button) findViewById(R.id.button2);
        button2 = (Button) findViewById(R.id.button3);
        button3 = (Button) findViewById(R.id.button4);

        date = dh.returnDate(url);
        emoji = dh.returnEmoji(url);
        category = dh.returnCatName(url);
        location = dh.returnExactLocation(url);

        TextView textView = (TextView) findViewById(R.id.textViewDisplayInformation);
        textView.setText("Picture Category: "+ category +"\n"+ "Emoji Used: "+ emoji + "\n"+ "Date of Picture: "+ date +"\n"+ "Location of Picture: "+ location);

        new DownloadImageTask(image)
        .execute(url);
    }

    public void getDate(View view) {
        Intent i = new Intent(ViewSelectedPicture.this, View_Pictures.class);
        Bundle bundle = new Bundle();

        bundle.putString("username", username);
        bundle.putString("query",date);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }

    public void getEmoji(View view) {
        Intent i = new Intent(ViewSelectedPicture.this, View_Pictures.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("query",emoji);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }

    public void getCatName(View view) {
        Intent i = new Intent(ViewSelectedPicture.this, View_Pictures.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("query",category);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }

    public void getLocation(View view) {
        Intent i = new Intent(ViewSelectedPicture.this, View_Pictures.class);
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("query",location);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }




    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
