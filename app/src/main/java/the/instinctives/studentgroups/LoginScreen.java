package the.instinctives.studentgroups;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by michaeljaison on 1/18/18.
 */

public class LoginScreen extends AppCompatActivity
{
    EditText username;
    EditText password;
    EditText friendname;

    HashMap<String,String> credentials= new HashMap<String, String>()
    {{
        put("mike","111001");
        put("jk","565701");
        put("arthi","123456");
        put("sukan","111046");
    }};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);

        username = findViewById(R.id.uname);
        password = findViewById(R.id.password);
        friendname = findViewById(R.id.friendname);

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(credentials.containsKey(username.getText().toString()))
                {
                    if(credentials.get(username.getText().toString()).equals(password.getText().toString()))
                    {
                        if(!username.getText().toString().equals(friendname.getText().toString()) &&
                                credentials.containsKey(friendname.getText().toString()))
                        {
                            App.editor.putString("user_id", username.getText().toString());
                            App.editor.putString("friend_id", friendname.getText().toString());
                            App.editor.putString("password", password.getText().toString());
                            App.editor.commit();
                            startActivity(new Intent(LoginScreen.this, ChatPage.class));
                        }
                        else
                        {

                        }
                    }
                    else
                    {

                    }
                }
                else
                {

                }
            }
        });
    }
}
