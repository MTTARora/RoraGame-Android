package com.rorasoft.roragame.View.GameList;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roragame.LimeLog;
import com.roragame.nvstream.http.ComputerDetails;
import com.roragame.nvstream.http.NvApp;
import com.roragame.nvstream.http.NvHTTP;
import com.roragame.nvstream.http.PairingManager;
import com.rorasoft.roragame.Model.computers.ComputerManagerListener;
import com.rorasoft.roragame.Model.computers.ComputerManagerService;
import com.rorasoft.roragame.R;
import com.rorasoft.roragame.Services.preferences.PreferenceConfiguration;
import com.rorasoft.roragame.Utils.CacheHelper;
import com.rorasoft.roragame.Utils.Dialog;
import com.rorasoft.roragame.Utils.ServerHelper;
import com.rorasoft.roragame.Utils.ShortcutHelper;
import com.rorasoft.roragame.Utils.SpinnerDialog;
import com.rorasoft.roragame.Utils.UiHelper;
import com.rorasoft.roragame.View.Adapter.AdapterFragment;
import com.rorasoft.roragame.View.Adapter.AdapterFragmentCallbacks;
import com.rorasoft.roragame.View.AppView;
import com.rorasoft.roragame.View.Custom.grid.AppGridAdapter;

import java.io.StringReader;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameListFragment extends Fragment {
    private AppGridAdapter appGridAdapter;
    private String uuidString;
    private ShortcutHelper shortcutHelper;

    private ComputerDetails computer;
    private ComputerManagerService.ApplistPoller poller;
    private SpinnerDialog blockingLoadSpinner;
    private String lastRawApplist;
    private int lastRunningAppId;
    private boolean suspendGridUpdates;
    private boolean inForeground;

    private final static int START_OR_RESUME_ID = 1;
    private final static int QUIT_ID = 2;
    private final static int CANCEL_ID = 3;
    private final static int START_WITH_QUIT = 4;
    private final static int VIEW_DETAILS_ID = 5;
    private final static int CREATE_SHORTCUT_ID = 6;

    public final static String NAME_EXTRA = "Name";
    public final static String UUID_EXTRA = "UUID";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ComputerManagerService.ComputerManagerBinder managerBinder;
//    private final ServiceConnection serviceConnection = new ServiceConnection() {
//        public void onServiceConnected(ComponentName className, IBinder binder) {
//            final ComputerManagerService.ComputerManagerBinder localBinder =
//                    ((ComputerManagerService.ComputerManagerBinder)binder);
//
//            // Wait in a separate thread to avoid stalling the UI
//            new Thread() {
//                @Override
//                public void run() {
//                    // Wait for the binder to be ready
//                    localBinder.waitForReady();
//
//                    // Get the computer object
//                    computer = localBinder.getComputer(UUID.fromString(uuidString));
//                    if (computer == null) {
////                        finish();
//                        //*** NEED TO SHOW NOTIFICATION
//                        return;
//                    }
//
//                    try {
//                        appGridAdapter = new AppGridAdapter(getActivity(),
//                                PreferenceConfiguration.readPreferences(getActivity()).listMode,
//                                PreferenceConfiguration.readPreferences(getActivity()).smallIconMode,
//                                computer, localBinder.getUniqueId());
//                    } catch (Exception e) {
//                        e.printStackTrace();
////                        finish();
//                        //*** NEED TO SHOW NOTIFICATION
//                        return;
//                    }
//
//                    // Now make the binder visible. We must do this after appGridAdapter
//                    // is set to prevent us from reaching updateUiWithServerinfo() and
//                    // touching the appGridAdapter prior to initialization.
//                    managerBinder = localBinder;
//
//                    // Load the app grid with cached data (if possible).
//                    // This must be done _before_ startComputerUpdates()
//                    // so the initial serverinfo response can update the running
//                    // icon.
//                    populateAppGridWithCache();
//
//                    // Start updates
//                    startComputerUpdates();
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (getActivity().isFinishing() || getActivity().isChangingConfigurations()) {
//                                return;
//                            }
//
//                            // Despite my best efforts to catch all conditions that could
//                            // cause the activity to be destroyed when we try to commit
//                            // I haven't been able to, so we have this try-catch block.
//                            try {
//                                getFragmentManager().beginTransaction()
//                                        .replace(R.id.appFragmentContainer, new AdapterFragment())
//                                        .commitAllowingStateLoss();
//                            } catch (IllegalStateException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }.start();
//        }
//
//        public void onServiceDisconnected(ComponentName className) {
//            managerBinder = null;
//        }
//    };
//
//    private void startComputerUpdates() {
//        // Don't start polling if we're not bound or in the foreground
//        if (managerBinder == null || !inForeground) {
//            return;
//        }
//
//        managerBinder.startPolling(new ComputerManagerListener() {
//            @Override
//            public void notifyComputerUpdated(final ComputerDetails details) {
//                // Do nothing if updates are suspended
//                if (suspendGridUpdates) {
//                    return;
//                }
//
//                // Don't care about other computers
//                if (!details.uuid.toString().equalsIgnoreCase(uuidString)) {
//                    return;
//                }
//
//                if (details.state == ComputerDetails.State.OFFLINE) {
//                    // The PC is unreachable now
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // Display a toast to the user and quit the activity
//                            Toast.makeText(getActivity(), getResources().getText(R.string.lost_connection), Toast.LENGTH_SHORT).show();
////                            finish();
//                            //*** NEED TO SHOW NOTIFICATION
//                        }
//                    });
//
//                    return;
//                }
//
//                // Close immediately if the PC is no longer paired
//                if (details.state == ComputerDetails.State.ONLINE && details.pairState != PairingManager.PairState.PAIRED) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // Disable shortcuts referencing this PC for now
//                            shortcutHelper.disableShortcut(details.uuid.toString(),
//                                    getResources().getString(R.string.scut_not_paired));
//
//                            // Display a toast to the user and quit the activity
//                            Toast.makeText(getActivity(), getResources().getText(R.string.scut_not_paired), Toast.LENGTH_SHORT).show();
////                            finish();
//                            //** NEED TO SHOW NOTIFICATION
//                        }
//                    });
//
//                    return;
//                }
//
//                // App list is the same or empty
//                if (details.rawAppList == null || details.rawAppList.equals(lastRawApplist)) {
//
//                    // Let's check if the running app ID changed
//                    if (details.runningGameId != lastRunningAppId) {
//                        // Update the currently running game using the app ID
//                        lastRunningAppId = details.runningGameId;
//                        updateUiWithServerinfo(details);
//                    }
//
//                    return;
//                }
//
//                lastRunningAppId = details.runningGameId;
//                lastRawApplist = details.rawAppList;
//
//                try {
//                    updateUiWithAppList(NvHTTP.getAppListByReader(new StringReader(details.rawAppList)));
//                    updateUiWithServerinfo(details);
//
//                    if (blockingLoadSpinner != null) {
//                        blockingLoadSpinner.dismiss();
//                        blockingLoadSpinner = null;
//                    }
//                } catch (Exception ignored) {}
//            }
//        });
//
//        if (poller == null) {
//            poller = managerBinder.createAppListPoller(computer);
//        }
//        poller.start();
//    }
//
//    private void stopComputerUpdates() {
//        if (poller != null) {
//            poller.stop();
//        }
//
//        if (managerBinder != null) {
//            managerBinder.stopPolling();
//        }
//
//        if (appGridAdapter != null) {
//            appGridAdapter.cancelQueuedOperations();
//        }
//    }

    public GameListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GameListFragment newInstance(String param1, String param2) {
        GameListFragment fragment = new GameListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//        // Assume we're in the foreground when created to avoid a race
//        // between binding to CMS and onResume()
//        inForeground = true;
//
//        shortcutHelper = new ShortcutHelper(getActivity());
//
//        UiHelper.setLocale(getActivity());
//
////        setContentView(R.layout.activity_app_view);
//
//        UiHelper.notifyNewRootView(getActivity());
//
//        //Get data
//
//        uuidString = getIntent().getStringExtra(UUID_EXTRA);
//
//        String computerName = getIntent().getStringExtra(NAME_EXTRA);
//
//        TextView label = findViewById(R.id.appListText);
//        setTitle(computerName);
//        label.setText(computerName);
//
//        // Add a launcher shortcut for this PC (forced, since this is user interaction)
//        shortcutHelper.createAppViewShortcut(uuidString, computerName, uuidString, true);
//        shortcutHelper.reportShortcutUsed(uuidString);
//
//        // Bind to the computer manager service
//        bindService(new Intent(this, ComputerManagerService.class), serviceConnection,
//                Service.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    private void populateAppGridWithCache() {
//        try {
//            // Try to load from cache
//            lastRawApplist = CacheHelper.readInputStreamToString(CacheHelper.openCacheFileForInput(getCacheDir(), "applist", uuidString));
//            List<NvApp> applist = NvHTTP.getAppListByReader(new StringReader(lastRawApplist));
//            updateUiWithAppList(applist);
//            LimeLog.info("Loaded applist from cache");
//        } catch (Exception e) {
//            if (lastRawApplist != null) {
//                LimeLog.warning("Saved applist corrupted: "+lastRawApplist);
//                e.printStackTrace();
//            }
//            LimeLog.info("Loading applist from the network");
//            // We'll need to load from the network
//            loadAppsBlocking();
//        }
//    }
//
//    private void loadAppsBlocking() {
//        blockingLoadSpinner = SpinnerDialog.displayDialog(this, getResources().getString(R.string.applist_refresh_title),
//                getResources().getString(R.string.applist_refresh_msg), true);
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        SpinnerDialog.closeDialogs(this);
//        Dialog.closeDialogs();
//
//        if (managerBinder != null) {
//            unbindService(serviceConnection);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Display a decoder crash notification if we've returned after a crash
//        UiHelper.showDecoderCrashDialog(this);
//
//        inForeground = true;
//        startComputerUpdates();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        inForeground = false;
//        stopComputerUpdates();
//    }
//
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
//        AppView.AppObject selectedApp = (AppView.AppObject) appGridAdapter.getItem(info.position);
//        if (lastRunningAppId != 0) {
//            if (lastRunningAppId == selectedApp.app.getAppId()) {
//                menu.add(Menu.NONE, START_OR_RESUME_ID, 1, getResources().getString(R.string.applist_menu_resume));
//                menu.add(Menu.NONE, QUIT_ID, 2, getResources().getString(R.string.applist_menu_quit));
//            }
//            else {
//                menu.add(Menu.NONE, START_WITH_QUIT, 1, getResources().getString(R.string.applist_menu_quit_and_start));
//                menu.add(Menu.NONE, CANCEL_ID, 2, getResources().getString(R.string.applist_menu_cancel));
//            }
//        }
//        menu.add(Menu.NONE, VIEW_DETAILS_ID, 3, getResources().getString(R.string.applist_menu_details));
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Only add an option to create shortcut if box art is loaded
//            // and when we're in grid-mode (not list-mode).
//            ImageView appImageView = info.targetView.findViewById(R.id.grid_image);
//            if (appImageView != null) {
//                // We have a grid ImageView, so we must be in grid-mode
//                BitmapDrawable drawable = (BitmapDrawable)appImageView.getDrawable();
//                if (drawable != null && drawable.getBitmap() != null) {
//                    // We have a bitmap loaded too
//                    menu.add(Menu.NONE, CREATE_SHORTCUT_ID, 4, getResources().getString(R.string.applist_menu_scut));
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onContextMenuClosed(Menu menu) {
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        final AppView.AppObject app = (AppView.AppObject) appGridAdapter.getItem(info.position);
//        switch (item.getItemId()) {
//            case START_WITH_QUIT:
//                // Display a confirmation dialog first
//                UiHelper.displayQuitConfirmationDialog(this, new Runnable() {
//                    @Override
//                    public void run() {
//                        ServerHelper.doStart(AppView.this, app.app, computer, managerBinder);
//                    }
//                }, null);
//                return true;
//
//            case START_OR_RESUME_ID:
//                // Resume is the same as start for us
//                ServerHelper.doStart(AppView.this, app.app, computer, managerBinder);
//                return true;
//
//            case QUIT_ID:
//                // Display a confirmation dialog first
//                UiHelper.displayQuitConfirmationDialog(this, new Runnable() {
//                    @Override
//                    public void run() {
//                        suspendGridUpdates = true;
//                        ServerHelper.doQuit(AppView.this,
//                                ServerHelper.getCurrentAddressFromComputer(computer),
//                                app.app, managerBinder, new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        // Trigger a poll immediately
//                                        suspendGridUpdates = false;
//                                        if (poller != null) {
//                                            poller.pollNow();
//                                        }
//                                    }
//                                });
//                    }
//                }, null);
//                return true;
//
//            case CANCEL_ID:
//                return true;
//
//            case VIEW_DETAILS_ID:
//                Dialog.displayDialog(AppView.this, getResources().getString(R.string.title_details),
//                        getResources().getString(R.string.applist_details_id) + " " + app.app.getAppId(), false);
//                return true;
//
//            case CREATE_SHORTCUT_ID:
//                ImageView appImageView = info.targetView.findViewById(R.id.grid_image);
//                Bitmap appBits = ((BitmapDrawable)appImageView.getDrawable()).getBitmap();
//                if (!shortcutHelper.createPinnedGameShortcut(uuidString + Integer.valueOf(app.app.getAppId()).toString(), appBits, computer, app.app)) {
//                    Toast.makeText(AppView.this, getResources().getString(R.string.unable_to_pin_shortcut), Toast.LENGTH_LONG).show();
//                }
//                return true;
//
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }
//
//    private void updateUiWithServerinfo(final ComputerDetails details) {
//        AppView.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                boolean updated = false;
//
//                // Look through our current app list to tag the running app
//                for (int i = 0; i < appGridAdapter.getCount(); i++) {
//                    AppView.AppObject existingApp = (AppView.AppObject) appGridAdapter.getItem(i);
//
//                    // There can only be one or zero apps running.
//                    if (existingApp.isRunning &&
//                            existingApp.app.getAppId() == details.runningGameId) {
//                        // This app was running and still is, so we're done now
//                        return;
//                    }
//                    else if (existingApp.app.getAppId() == details.runningGameId) {
//                        // This app wasn't running but now is
//                        existingApp.isRunning = true;
//                        updated = true;
//                    }
//                    else if (existingApp.isRunning) {
//                        // This app was running but now isn't
//                        existingApp.isRunning = false;
//                        updated = true;
//                    }
//                    else {
//                        // This app wasn't running and still isn't
//                    }
//                }
//
//                if (updated) {
//                    appGridAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//    }
//
//    private void updateUiWithAppList(final List<NvApp> appList) {
//        AppView.this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                boolean updated = false;
//
//                // First handle app updates and additions
//                for (NvApp app : appList) {
//                    boolean foundExistingApp = false;
//
//                    // Try to update an existing app in the list first
//                    for (int i = 0; i < appGridAdapter.getCount(); i++) {
//                        AppView.AppObject existingApp = (AppView.AppObject) appGridAdapter.getItem(i);
//                        if (existingApp.app.getAppId() == app.getAppId()) {
//                            // Found the app; update its properties
//                            if (!existingApp.app.getAppName().equals(app.getAppName())) {
//                                existingApp.app.setAppName(app.getAppName());
//                                updated = true;
//                            }
//
//                            foundExistingApp = true;
//                            break;
//                        }
//                    }
//
//                    if (!foundExistingApp) {
//                        // This app must be new
//                        appGridAdapter.addApp(new AppView.AppObject(app));
//                        updated = true;
//                    }
//                }
//
//                // Next handle app removals
//                int i = 0;
//                while (i < appGridAdapter.getCount()) {
//                    boolean foundExistingApp = false;
//                    AppView.AppObject existingApp = (AppView.AppObject) appGridAdapter.getItem(i);
//
//                    // Check if this app is in the latest list
//                    for (NvApp app : appList) {
//                        if (existingApp.app.getAppId() == app.getAppId()) {
//                            foundExistingApp = true;
//                            break;
//                        }
//                    }
//
//                    // This app was removed in the latest app list
//                    if (!foundExistingApp) {
//                        appGridAdapter.removeApp(existingApp);
//                        updated = true;
//
//                        // Check this same index again because the item at i+1 is now at i after
//                        // the removal
//                        continue;
//                    }
//
//                    // Move on to the next item
//                    i++;
//                }
//
//                if (updated) {
//                    appGridAdapter.notifyDataSetChanged();
//                }
//            }
//        });
//    }
//
//    @Override
//    public int getAdapterFragmentLayoutId() {
//        return PreferenceConfiguration.readPreferences(this).listMode ?
//                R.layout.list_view : (PreferenceConfiguration.readPreferences(AppView.this).smallIconMode ?
//                R.layout.app_grid_view_small : R.layout.app_grid_view);
//    }
//
//    @Override
//    public void receiveAbsListView(AbsListView listView) {
//        listView.setAdapter(appGridAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
//                                    long id) {
//                AppView.AppObject app = (AppView.AppObject) appGridAdapter.getItem(pos);
//
//                // Only open the context menu if something is running, otherwise start it
//                if (lastRunningAppId != 0) {
//                    openContextMenu(arg1);
//                } else {
//                    ServerHelper.doStart(AppView.this, app.app, computer, managerBinder);
//                }
//            }
//        });
//        registerForContextMenu(listView);
//        listView.requestFocus();
//    }
//
//    public class AppObject {
//        public final NvApp app;
//        public boolean isRunning;
//
//        public AppObject(NvApp app) {
//            if (app == null) {
//                throw new IllegalArgumentException("app must not be null");
//            }
//            this.app = app;
//        }
//
//        @Override
//        public String toString() {
//            return app.getAppName();
//        }
//    }

}
