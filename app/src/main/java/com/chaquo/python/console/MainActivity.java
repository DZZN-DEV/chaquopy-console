package com.chaquo.python.console;

import android.app.*;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import com.chaquo.python.utils.*;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

public class MainActivity extends PythonConsoleActivity {

    private String downloadLink = "";

    // On API level 31 and higher, pressing Back in a launcher activity sends it to the back by
    // default, but that would make it difficult to restart the activity.
    @Override public void onBackPressed() {
        finish();
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLinkInputDialog();
    }

    private void showLinkInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Download Link");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadLink = input.getText().toString();
                startTask();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void startTask() {
        getTaskClass().run(); // Start the Python task
    }

    @Override protected Class<? extends Task> getTaskClass() {
        return Task.class;
    }

    public static class Task extends PythonConsoleActivity.Task {
        public Task(Application app) {
            super(app);
        }

        @Override public void run() {
            Python py = Python.getInstance();
            PyObject scdlModule = py.getModule("scdl");

            MainActivity activity = (MainActivity) getApp().getCurrentActivity();
            String link = activity.downloadLink;

            // Prepare the arguments
            String[] args = new String[]{
                "--l", link,
                "--extract_artist", "true",
                "--force_metadata", "true"
            };

            scdlModule.callAttr("main", (Object) args);
        }
    }
}
