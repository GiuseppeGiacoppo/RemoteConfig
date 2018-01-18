package me.giacoppo.remoteconfig.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import me.giacoppo.remoteconfig.RemoteConfig;
import me.giacoppo.remoteconfig.RemoteResource;
import me.giacoppo.remoteconfig.app.messages.MessagesConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View fetch = findViewById(R.id.fetch);
        View activate = findViewById(R.id.activate);
        View fetchAndActivat = findViewById(R.id.fetch_activate);
        View setDefault = findViewById(R.id.set_default);
        View read = findViewById(R.id.read);
        View clear = findViewById(R.id.clear);
        final TextView hello = findViewById(R.id.hello);
        final RemoteResource<MessagesConfig> remoteResource = RemoteConfig.of(MessagesConfig.class);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Fetch", Toast.LENGTH_SHORT).show();
                remoteResource.fetch().addResponseListener(new RemoteResource.FetchResponse() {
                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remoteResource.activateFetched();
                Toast.makeText(MainActivity.this, "Activated", Toast.LENGTH_SHORT).show();
            }
        });

        fetchAndActivat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remoteResource.fetch().addResponseListener(new RemoteResource.FetchResponse() {
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        remoteResource.activateFetched();
                    }
                });
            }
        });

        setDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagesConfig defaultMessages = new MessagesConfig();
                defaultMessages.setWelcomeMessage("Hello di default");
                remoteResource.setDefaultConfig(defaultMessages);

                Toast.makeText(MainActivity.this, "Default config set", Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remoteResource.clear();
                Toast.makeText(MainActivity.this, "Cleared", Toast.LENGTH_SHORT).show();
            }
        });
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessagesConfig m = remoteResource.get();

                Toast.makeText(MainActivity.this, "Readed", Toast.LENGTH_SHORT).show();
                if (m == null)
                    hello.setText("null");
                else
                    hello.setText(m.getWelcomeMessage());
            }
        });
    }
}
