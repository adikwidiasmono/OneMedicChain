package ndid.omc.main;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.stepstone.apprating.listener.RatingDialogListener;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import ndid.omc.firebase.FirebaseDB;
import ndid.omc.fragment.AccountFragment;
import ndid.omc.fragment.InsuranceProductsFragment;
import ndid.omc.fragment.TimelineFragment;
import ndid.omc.fragment.ReservationFragment;
import ndid.omc.fragment.MedicalHistoriesFragment;
import ndid.omc.menu.DrawerAdapter;
import ndid.omc.menu.DrawerItem;
import ndid.omc.menu.SimpleItem;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener, RatingDialogListener {

    private static final int POS_RESERVATION = 0;
    private static final int POS_ACCOUNT = 1;
    private static final int POS_TIMELINE = 2;
    private static final int POS_HISTORIES = 3;
    private static final int POS_MY_INSURRANCE = 4;
    private static final int POS_OFFERING_INSURRANCE = 5;
    private static final int POS_LOGOUT = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    private Toolbar toolbar;

    private FloatingActionButton fabSendNotif;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Treatment Reservation");
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_RESERVATION),
                createItemFor(POS_ACCOUNT).setChecked(true),
                createItemFor(POS_TIMELINE),
                createItemFor(POS_HISTORIES),
                createItemFor(POS_MY_INSURRANCE),
                createItemFor(POS_OFFERING_INSURRANCE),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_ACCOUNT);

        fabSendNotif = findViewById(R.id.fab_send_notif);
        fabSendNotif.setVisibility(View.GONE);
        fabSendNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), DoctorRequestActivity.class));
            }
        });

        watchForNotif();

        int notifId = getIntent().getIntExtra("notification_id", 0);
        if (notifId > 0) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notifId);

            toolbar.setTitle("Medical Histories");
            adapter.setSelected(3);
        }
    }

    private void watchForNotif() {
        DatabaseReference fRef = FirebaseDB.init().getDBReference(FirebaseDB.REF_NOTIFICATIONS);
        fRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                createNotification(getApplicationContext(),
                        "Request For Medical Record",
                        "Dr. Marc from Barcelona Hospital is requested for your previous medical record.\n" +
                                "Will you give it or not?", "001");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class onNotificationCancelAction extends BroadcastReceiver {
        private int id;

        @Override
        public void onReceive(Context context, Intent intent) {
            id = intent.getIntExtra("notification_id", 1);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }
    }

    public int notificationId;

    public void createNotification(Context context, String title, String message, String notificationId) {
        this.notificationId = Integer.parseInt(notificationId);

        // manager
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // notification
        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(message);
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.ic_local_hospital_white_24dp);
        mBuilder.setStyle(new Notification.BigTextStyle()
                .bigText(message));

        // cancel intent
        Intent cancelIntent = new Intent(context, onNotificationCancelAction.class);
        Bundle extras = new Bundle();
        extras.putInt("notification_id", this.notificationId);
        cancelIntent.putExtras(extras);
        PendingIntent pendingCancelIntent =
                PendingIntent.getBroadcast(context, this.notificationId, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent okIntent = new Intent(context, MainActivity.class);
        extras = new Bundle();
        extras.putInt("notification_id", this.notificationId);
        okIntent.putExtras(extras);
        PendingIntent pendingOkIntent = PendingIntent.getActivity(
                this,
                this.notificationId,
                okIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.addAction(R.drawable.ic_done, "Accept", pendingOkIntent)
                .addAction(R.drawable.ic_close, "Reject", pendingCancelIntent);

        // notify
        Notification notification = mBuilder.build();
        notificationManager.notify(Integer.parseInt(notificationId), notification);
    }

    @Override
    public void onItemSelected(int position) {
        Fragment selectedScreen = AccountFragment.createFor(screenTitles[position]);
        switch (position) {
            case POS_RESERVATION: {
                toolbar.setTitle("Treatment Reservation");
                /*
                Patient only can make one reservation and then get a QR to be scanned when they come to Hospital
                They can cancel current reservation and then create a new one
                 */
                selectedScreen = ReservationFragment.createFor(screenTitles[position]);
                break;
            }
            case POS_ACCOUNT: {
                toolbar.setTitle("Account");
                selectedScreen = AccountFragment.createFor(screenTitles[position]);
                break;
            }
            case POS_TIMELINE: {
                toolbar.setTitle("Timeline");
                selectedScreen = TimelineFragment.createFor(screenTitles[position]);
                break;
            }
            case POS_HISTORIES: {
                toolbar.setTitle("Medical Histories");
                selectedScreen = MedicalHistoriesFragment.createFor(screenTitles[position]);
                break;
            }
            case POS_MY_INSURRANCE: {
                toolbar.setTitle("My Insurances");
                selectedScreen = InsuranceProductsFragment.createFor(screenTitles[position]);
                break;
            }
            case POS_OFFERING_INSURRANCE: {
                toolbar.setTitle("Offering Insurances");
                selectedScreen = InsuranceProductsFragment.createFor(screenTitles[position]);
                break;
            }
            case POS_LOGOUT: {
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            }
            default: {
                break;
            }
        }
        slidingRootNav.closeMenu();
        showFragment(selectedScreen);
    }

    private void showFragment(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }


    @Override
    public void onPositiveButtonClicked(int i, String s) {
        Toast.makeText(getApplicationContext(), "Thank you for rating", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}