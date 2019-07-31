package ir.irezaa.badgerviewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import badger_android.BadgerAndroid;

public class MainActivity extends Activity {
    public static float density = 1;

    private RecyclerView listView;
    private ArrayList<String> logArray = new ArrayList<>();
    private Adapter adapter;


    private EditText editText;
    private Button button;
    private Button clearButton;
    private BadgerAndroid badgerAndroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        density = getResources().getDisplayMetrics().density;
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.mainedittext);
        button = findViewById(R.id.mainbutton);
        clearButton = findViewById(R.id.clearButton);

        adapter = new Adapter();
        listView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,true));
        listView.setAdapter(adapter);
        listView.setBackgroundColor(Color.BLACK);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().isEmpty()) {
                    if (badgerAndroid != null) {
                        try {
                            String dump = badgerAndroid.dumpPrefix(editText.getText().toString());
                            addLog(dump);
                        } catch (Exception e) {
                            addLog("Error while dump prefix , error : " + e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logArray.clear();
                adapter.notifyDataSetChanged();
                addLog("Cleared");
            }
        });

        Intent intent = getIntent();
        checkIntent(intent);
    }



    private void checkIntent(Intent intent) {
        File dataFolder = FileUtility.getDataPath(this);

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_VIEW)) {
            Uri data = intent.getData();

            if (data != null) {
                InputStream inputStream = null;
                FileOutputStream output = null;

                File zipFile = new File(dataFolder, "sharing/");
                zipFile.mkdirs();
                zipFile = new File(zipFile, "lastDatabase.zip");

                try {
                    inputStream = getContentResolver().openInputStream(data);

                    output = new FileOutputStream(zipFile);
                    byte[] buffer = new byte[1024 * 20];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    try {
                        if (output != null) {
                            output.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }

                File outputPath = new File(dataFolder.getPath() + "/sharing","lastDatabase");

                try {
                    FileUtility.unzip(zipFile,outputPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                badgerAndroid = new BadgerAndroid();
                badgerAndroid.init(outputPath.getPath());
                try {
                    badgerAndroid.open();
                    addLog("Badger database successfully opened , path : " + outputPath.getPath());
                } catch (Exception e) {
                    addLog("Failed to connect database , error : " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }else {
            File outputPath = new File(dataFolder.getPath() + "/sharing","lastDatabase");

            if (outputPath.exists()) {
                badgerAndroid = new BadgerAndroid();
                badgerAndroid.init(outputPath.getPath());
                try {
                    badgerAndroid.open();
                    addLog("Last used Badger database successfully opened , path : " + outputPath.getPath());
                } catch (Exception e) {
                    addLog("Failed to connect database , error : " + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void addLog(String text) {
        logArray.add(0,text);
        adapter.notifyItemInserted(0);
    }

    public static int dp(float value) {
        if (value == 0) {
            return 0;
        }
        return (int) Math.ceil(density * value);
    }

    private class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(new LogCell(parent.getContext()));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            String item = logArray.get(position);
            ((LogCell) holder.itemView).setText(item);
        }

        @Override
        public int getItemCount() {
            return logArray.size();
        }
    }
}
