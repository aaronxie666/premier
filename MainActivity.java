package icn.premierandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tapjoy.Tapjoy;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import icn.premierandroid.adapters.ViewPagerAdapter;
import icn.premierandroid.fragments.AboutPremierFragment;
import icn.premierandroid.fragments.BlogFragment;
import icn.premierandroid.fragments.HomeFragment;
import icn.premierandroid.fragments.LikesFragment;
import icn.premierandroid.fragments.ModelTipsFragment;
import icn.premierandroid.fragments.ModelWorldFragment;
import icn.premierandroid.fragments.ScoutMeFragment;
import icn.premierandroid.fragments.StreetStyleFragment;
import icn.premierandroid.interfaces.UserType;
import icn.premierandroid.misc.RedeemAlertReceiver;
import icn.premierandroid.misc.InstagramApp;
import icn.premierandroid.misc.LikesDialog;
import icn.premierandroid.models.ViewPagerModel;

import static icn.premierandroid.misc.CONSTANTS.PARSE_ID_DAILY_LOGIN;

/*
 * Developer: Bradley Wilson
 * Company: International Celebrity Network
 * Association: Holds all fragments
 */

public class MainActivity extends AppCompatActivity {

    public ViewPager viewPager;
    private DrawerLayout pDrawerLayout;
    public Toolbar toolbar;
    public ViewPagerModel model;
    private InstagramApp mApp;
    private Date last_visited;
    private FrameLayout hourly_redeem_container;
    private Animation fadeOut;
    private DateTime date;
    private boolean isClickedBefore = false;
    private boolean firstTime = true;
    private ImageView hourly_likes_image;
    private TextView hourly_likes_text;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.pToolbar);
        setSupportActionBar(toolbar);

        mApp = new InstagramApp(this, App.CLIENT_ID,
                App.CLIENT_SECRET, App.CALLBACK_URL);

        if (ParseUser.getCurrentUser() != null) {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        if (!objects.get(0).getBoolean("isAndroid")) {
                            objects.get(0).put("isAndroid", true);
                            objects.get(0).saveInBackground();
                        }
                    } else {
                        Log.e("failed", "failed" + e.getMessage());
                    }
                }
            });
        }

        // load the animation
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_out);
        hourly_likes_image = (ImageView) findViewById(R.id.hourly_likes_image);
        hourly_likes_text = (TextView) findViewById(R.id.hourly_likes_text);
        hourly_redeem_container = (FrameLayout) findViewById(R.id.hourly_likes);
        hourly_redeem_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClickedBefore = true;
                checkIfItHasBeen1Hour();
                hourly_redeem_container.startAnimation(fadeOut);
                hourly_redeem_container.setVisibility(View.GONE);
                hourly_redeem_container.setClickable(false);
                //                scheduleNotification();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(1);
        model = new ViewPagerModel(viewPager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout;
        tabLayout = (TabLayout) findViewById(R.id.pTabs);
        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.getTabAt(1).setCustomView(R.layout.icon_tab_ss);
//        tabLayout.getTabAt(5).setCustomView(R.layout.icon_tab_likes);

        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false);
        }

        checkIfItHasBeen24Hours();
        pDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ParseUser user = ParseUser.getCurrentUser();
        Log.d("user is", "" + ParseUser.getCurrentUser());
        if (user != null) {
            String loginMethod = user.getString("loginMethod");
            Log.d("loginMethod", "" + user.getString("loginMethod"));
            if (loginMethod != null) {
                switch (loginMethod) {
                    case "Google":
                        AllLoginActivity.userType = UserType.userType.facebookUser;
                        break;
                    case "Facebook":
                        AllLoginActivity.userType = UserType.userType.facebookUser;
                        break;
                    case "Instagram":
                        AllLoginActivity.userType = UserType.userType.instagramUser;
                        break;
                    case "Email":
                        AllLoginActivity.userType = UserType.userType.normalUser;
                        break;
                }
            } else {
                Intent intent = new Intent(MainActivity.this, AllLoginActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            if (AllLoginActivity.userType == UserType.userType.unsignedUser) {
              //do nothing
            } else {
                Intent intent = new Intent(MainActivity.this, AllLoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

    }

    private void checkIfItHasBeen1Hour() {
        Date date_date = ParseUser.getCurrentUser().getDate("hourlyPoints");
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Paris");
        date = new DateTime(date_date, timeZone);
        DateTime newDate = new DateTime(timeZone);
        if (date == null) {
            ParseUser.getCurrentUser().put("hourlyPoints", newDate);
            ParseUser.getCurrentUser().saveInBackground();
        }
        updateNewClickDate();
    }

    private void updateNewClickDate() {
        ParseUser.getCurrentUser().put("hourlyPoints", new Date());
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    compareClickDates();
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void compareClickDates() {
//        DateTimeZone timeZone = DateTimeZone.forID("Europe/Paris");
//        DateTime now = new DateTime(timeZone);
//        DateTime oneHourBeforeNow = now.minusHours(1);
//        Boolean isBeforeOneHour = date.isBefore(oneHourBeforeNow);
            int points = ParseUser.getCurrentUser().getInt("points");
            points = points + 50;
            ParseUser.getCurrentUser().put("points", points);
            HomeFragment.userlikes.setText(String.valueOf(points));
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    scheduleNotification();
                }
            });
    }

    private void updateLastVisited() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Last_Visited");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        j.put("lastVisited", new Date());
                        j.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    compareDates();
                                } else {
                                    Log.e("failed", "Failed" + e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void checkIfItHasBeen24Hours() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Last_Visited");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        last_visited = j.getDate("lastVisited");
                        Log.e("last_visited", "Date: " + last_visited);
                        updateLastVisited();
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void compareDates() {
        DateTimeZone timeZone = DateTimeZone.forID("Europe/Paris");
        DateTime dateTimeInQuestion = new DateTime(last_visited, timeZone);  // Or: new DateTime( someJavaDotUtilDotDateObject );
        DateTime now = new DateTime(timeZone);
        DateTime twentyFourHoursBeforeNow = now.minusHours(24); // Ignores Daylight Saving Time (DST). If you want to adjust for that, call: plusDays( 1 ) instead.
        DateTime oneHourBeforeNow = now.minusMinutes(60);
        Boolean isBeforeYesterday = dateTimeInQuestion.isBefore(twentyFourHoursBeforeNow);
        Boolean isBeforeOneHour = dateTimeInQuestion.isBefore(oneHourBeforeNow);
        if (isBeforeYesterday ) {
            showLikesDialog();
            clearBlogCount();
            clearReadBlogs();
            if (isBeforeOneHour && isClickedBefore && !firstTime) {
                hourly_redeem_container.setVisibility(View.VISIBLE);
                hourly_redeem_container.setClickable(true);
            }
            else if (isBeforeOneHour && firstTime) {
                hourly_redeem_container.setVisibility(View.VISIBLE);
                firstTime = false;
            }
        } else if (isBeforeOneHour && isClickedBefore && !firstTime){
            hourly_redeem_container.setVisibility(View.VISIBLE);
            hourly_redeem_container.setClickable(true);
        } else if (isBeforeOneHour && firstTime) {
            firstTime = false;
            hourly_redeem_container.setVisibility(View.VISIBLE);
        }
//        DateTime dateTime = new DateTime(last_visited); // Convert java.util.Date to Joda-Time DateTime.
//        DateTime yesterday = new DateTime(updated_visited).toDateTime(DateTimeZone.UTC);
//        Log.e("yesterday: ", "" + yesterday);
//        boolean isBeforeYesterday = dateTime.isBefore(yesterday.minusHours(24));
    }

    private void scheduleNotification() {
        Long alertTime = new GregorianCalendar().getTimeInMillis()+60*60*1000;
        Intent alertIntent = new Intent(MainActivity.this, RedeemAlertReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private void clearReadBlogs() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                        JSONArray blogsRead = j.getJSONArray("blogsRead");
                        JSONArray blogs = new JSONArray();
                        Log.e("blogs", blogsRead.toString());
                        if (blogsRead != null) {
                                j.put("blogsRead", blogs);
                                j.put("hasUserRedeemedAdvent", false);
                                j.saveInBackground();
                        } else {
                            Log.e("blogsRead", "is empty");
                        }
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void clearBlogCount() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Last_Visited");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (ParseObject j : objects) {
                            j.put("blogCount", 0);
                            j.put("videoCount", 0);
                            j.saveInBackground();
                    }
                } else {
                    Log.e("failed", "failed" + e.getMessage());
                }
            }
        });
    }

    private void showLikesDialog() {
        LikesDialog likesDialog = new LikesDialog(this, PARSE_ID_DAILY_LOGIN);
        likesDialog.display();
    }

    public void updateUserLikes() {
            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        for (ParseObject j : objects) {
                            int points = j.getInt("points");
                            String pointsValue = String.valueOf(points);
                            HomeFragment.userlikes.setText(pointsValue);
                        }
                    } else {
                        Log.e("failed", e.getMessage());
                    }
                }
            });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(HomeFragment.newInstance(0), "HOME");
        adapter.addFrag(StreetStyleFragment.newInstance(1, viewPager), "STREET STYLE");
        adapter.addFrag(BlogFragment.newInstance(2), "BLOG");
        adapter.addFrag(ModelWorldFragment.newInstance(3), "MODEL WORLD");
        adapter.addFrag(ModelTipsFragment.newInstance(4), "MODEL TIPS");
        adapter.addFrag(LikesFragment.newInstance(5), "LIKES");
        adapter.addFrag(ScoutMeFragment.newInstance(6), "SCOUT ME");
        adapter.addFrag(AboutPremierFragment.newInstance(7), "ABOUT PREMIER");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                pDrawerLayout.openDrawer(GravityCompat.START);
                final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setItemIconTintList(null);
        menu = navigationView.getMenu();
        updateMenuTitles();
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        //Checking if the item is in checked state or not, if not make it in checked state
                        if (menuItem.isChecked()) menuItem.setChecked(false);
                        else menuItem.setChecked(true);

                        //Closing drawer on item click
                        pDrawerLayout.closeDrawers();

                        //Check to see which item was being clicked and perform appropriate action
                        switch (menuItem.getItemId()) {
                            //Replacing the main content with ContentFragment Which is our Inbox View;
                            case R.id.nav_home:
                                viewPager.setCurrentItem(0, false);
                                return true;
                            case R.id.nav_streetstyle:
                                viewPager.setCurrentItem(1, false);
                                return true;
                            case R.id.nav_blog:
                                viewPager.setCurrentItem(2, false);
                                return true;
                            case R.id.nav_modelworld:
                                viewPager.setCurrentItem(3, false);
                                return true;
                            case R.id.nav_modeltips:
                                viewPager.setCurrentItem(4, false);
                                return true;
                            case R.id.nav_likes:
                                viewPager.setCurrentItem(5, false);
                                return true;
                            case R.id.nav_becomeamodel:
                                viewPager.setCurrentItem(6, false);
                                return true;
                            case R.id.nav_aboutpremier:
                                viewPager.setCurrentItem(7, false);
                                return true;
                            case R.id.nav_settings:
                                if (AllLoginActivity.userType == UserType.userType.unsignedUser) {
                                    Toast.makeText(MainActivity.this, "You need to be logged in to adjust application settings.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent l = new Intent(getApplicationContext(), AppSettings.class);
                                    startActivity(l);
                                    finish(); // Always call the superclass
                                }
                                return true;
                            case R.id.nav_logout:
                                ParseUser.logOut();
                                mApp.resetAccessToken();
                                LoginManager.getInstance().logOut();
                                Intent r = new Intent(getApplicationContext(), AllLoginActivity.class);
                                startActivity(r);
                                finish(); // Always call the superclass
                            default:
                                return true;
                        }
                    }
                });
    }

    private void updateMenuTitles() {
            MenuItem bedMenuItem = menu.findItem(R.id.nav_logout);
            if (AllLoginActivity.userType == UserType.userType.unsignedUser) {
                bedMenuItem.setTitle("LOGIN");
            } else {
                bedMenuItem.setTitle("LOGOUT");
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setCurrentPagerItem(0);
    }

    public void setCurrentPagerItem(int currentPagerItem) {
        viewPager.setCurrentItem(currentPagerItem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Tapjoy.onActivityStart(this);
    }

    @Override
    protected void onStop() {
        Tapjoy.onActivityStop(this);
        super.onStop();
    }
}
