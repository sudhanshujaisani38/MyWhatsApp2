package com.sudhanshujaisani.mywhatsapp;

import android.content.ContentResolver;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    public static ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=(Toolbar)findViewById(R.id.maintoolbar);
        setSupportActionBar(toolbar);
        MainPagerAdapter mainPagerAdapter=new MainPagerAdapter(getSupportFragmentManager());
        viewPager=(ViewPager)findViewById(R.id.mainViewPager);
        viewPager.setAdapter(mainPagerAdapter);
        viewPager.setCurrentItem(0,true);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int pageNo=MainActivity.viewPager.getCurrentItem();
                if(pageNo==3){
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivity(intent);}
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout=(TabLayout)findViewById(R.id.mainTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out_main:
                if(FirebaseAuth.getInstance().getCurrentUser()!=null)
                    FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("online").setValue("false");
                firebaseAuth.signOut();
                sendToStart();
                break;
            case R.id.my_account:
                Intent intent=new Intent(getApplicationContext(),MyAccountActivity.class);
                startActivity(intent);
                break;

            case R.id.all_users:
                 intent=new Intent(getApplicationContext(),UsersActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser==null)
        {
            sendToStart();
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid()).child("online").setValue("false");
    }

    void sendToStart(){
        Intent intent=new Intent(this,StartActivity.class);
        startActivity(intent);finish();
    }
}
