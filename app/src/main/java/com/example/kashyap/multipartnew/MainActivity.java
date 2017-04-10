package com.example.kashyap.multipartnew;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity implements View.OnClickListener{

    private LinearLayout lnrImages;
    private Button btnAddPhots;
    private Button btnSaveImages;
    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;
    private Bitmap resized;
    private final int PICK_IMAGE_MULTIPLE =1;
    private File[]  files;
    private int length;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lnrImages = (LinearLayout)findViewById(R.id.lnrImages);
        btnAddPhots = (Button)findViewById(R.id.btnAddPhots);
        btnSaveImages = (Button)findViewById(R.id.btnSaveImages);
        btnAddPhots.setOnClickListener(this);
        btnSaveImages.setOnClickListener(this);
        files=new File[100];
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddPhots:
                Intent intent = new Intent(MainActivity.this,CustomPhotoGalleryActivity1.class);
                startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
                break;
            case R.id.btnSaveImages:
                System.out.println("wellcome to save image");
                //Auth header
                Map<String, String> mHeaderPart= new HashMap<>();
               // mHeaderPart.put("mimeType", "multipart/form-data;");
                mHeaderPart.put("Authorization", "JWT  eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjU4ZDZiZjRkZTk2MmVhYTM4ZWFkOWM2NiIsInJvbGUiOiJwYXRpZW50IiwiaWF0IjoxNDkxNjMwNjMwfQ.6qul4PWW1_G07HoH20d0y-_0W9abHfpPQfAN3vl6T9I");

                //File part
                Map<String, File> mFilePartData= new HashMap<>();
                for(int i=0;i<length;i++) {
                    mFilePartData.put("file", files[i]);

                }


                CustomMultipartRequest mCustomRequest = new CustomMultipartRequest("http://freedoctor.southeastasia.cloudapp.azure.com/reports/add", new Response.Listener<JSONObject>() {
                    Response.Listener<JSONObject> listener=null;
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        System.out.println("success");
                       // listener.onResponse(jsonObject);
                        System.out.println(jsonObject.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        System.out.println("failure");
                        Response.ErrorListener listener=null;
                        //listener.onErrorResponse(volleyError);
                    }
                }, mFilePartData, mHeaderPart);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(mCustomRequest);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<String>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try{
                    lnrImages.removeAllViews();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                length=imagesPath.length;
                for (int i=0;i<length;i++){
                    imagesPathList.add(imagesPath[i]);
                    yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
                   files[i]= saveBitmap(yourbitmap,imagesPath[i]);
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(yourbitmap);
                    imageView.setAdjustViewBounds(true);
                    lnrImages.addView(imageView);
                }
            }

        }

    }
    private File saveBitmap(Bitmap bitmap, String path) {
        File file = null;
        if (bitmap != null) {
            file = new File(path);
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
