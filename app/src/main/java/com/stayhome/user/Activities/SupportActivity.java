package com.stayhome.user.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.stayhome.user.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SupportActivity extends AppCompatActivity {

    @BindView(R.id.copy_text_view)
    TextView copyTextView;

    @BindView(R.id.email_text)
    TextView emailTextView;

    @BindView(R.id.what_text)
    TextView whatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        ButterKnife.bind(this);
        copyTextView.setOnClickListener(view -> {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("email","fieapps@gmail.com");
            if (clipboard!=null)
                clipboard.setPrimaryClip(clip);

            Toast.makeText(SupportActivity.this, "text copied", Toast.LENGTH_LONG).show();
        });

        emailTextView.setOnClickListener(view -> {
            String mailto = "mailto:indstayhome@gmail.com" +
                    "?cc=" + "" +
                    "&subject=" + Uri.encode("")+
                    "&body=" + Uri.encode("");
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(SupportActivity.this,getResources().getString(R.string.email),Toast.LENGTH_LONG).show();
            }

        });

        whatTextView.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=+919351437023";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }
}