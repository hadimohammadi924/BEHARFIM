package ir.hadimohammadi.beharfim;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    LottieAnimationView floa;
    Toolbar toolbar;
    LinearLayout hadi;
    NavigationView navigationView;
    ImageView menu_drawer_right;
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getApplicationContext().getSharedPreferences("Beharfim", Context.MODE_PRIVATE);
        editor = sp.edit();

        if (sp.getString("Users_ID", "").equals("")) {
            finish();
            startActivity(new Intent(MainActivity.this, Login.class));
        }



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupNavMenu();



        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        floa = findViewById(R.id.floa);
        menu_drawer_right = findViewById(R.id.menu_drawer_right);

        floa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermission();
                     //   startActivity(new Intent(MainActivity.this, Contacts.class));
                    }
                    else {
                        startActivity(new Intent(MainActivity.this, Contacts.class));
                    }
                }


            }
        });






        tabLayout.addTab(tabLayout.newTab().setText("شخصی"));
        tabLayout.addTab(tabLayout.newTab().setText("گروه ها"));
        tabLayout.addTab(tabLayout.newTab().setText("کانال ها"));
        tabLayout.addTab(tabLayout.newTab().setText("عمومی"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final MyAdapter adapter = new MyAdapter(this,getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });





    }

    @Override
    public void onBackPressed() { finish(); }


    private void requestPermission() {

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_CONTACTS}, 2);
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "جهت مشاهده مخاطبین خود مجوز بدهید", Toast.LENGTH_SHORT).show();
        }else {
            startActivity(new Intent(MainActivity.this, Contacts.class));

        }

    }


    public void setupNavMenu() {
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationview);
        menu_drawer_right = findViewById(R.id.menu_drawer_right);

/*
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                int id = menuItem.getItemId();
                if (id == R.id.item_home) {

                    Toast.makeText(MainActivity.this, "در ورژن بعدی فعال خواهد شد", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();

                } else if (id == R.id.item_update) {
                    //  tabLayout.getTabAt(1).select();
                    Toast.makeText(MainActivity.this, "در ورژن بعدی فعال خواهد شد", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.item_update) {
                    Toast.makeText(MainActivity.this, "در ورژن بعدی فعال خواهد شد", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.item_fav) {
                    Toast.makeText(MainActivity.this, "در ورژن بعدی فعال خواهد شد", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.item_setting) {
                    Toast.makeText(MainActivity.this, "در ورژن بعدی فعال خواهد شد", Toast.LENGTH_SHORT).show();

                } else if (id == R.id.item_serach) {
                    Toast.makeText(MainActivity.this, "در ورژن بعدی فعال خواهد شد", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                } else if (id == R.id.item_about) {

                   // startActivity(new Intent(MainActivity.this, AboutActivity.class));

                } else if (id == R.id.item_help) {
                    Toast.makeText(MainActivity.this, "" + (Html.fromHtml("" + "" +
                            "" +
                            "" +
                            "<h4><b>راهنمایی</h4></b><br>" +
                            "" +
                            "در ورژن بعدی فعال خواهد شد" +
                            "")), Toast.LENGTH_SHORT).show();

                } else if (id == R.id.item_exit) {
                    if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        finishAffinity();
                    } else {
                        finish();
                    }

                }
                return true;
            }
        });


 */

        menu_drawer_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);




            }
        });

    }




}