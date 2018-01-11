package com.sanganan.app.sample;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sanganan.app.R;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.common.VolleySingleton;
import com.sanganan.app.fragments.BaseFragment;
import com.sanganan.app.fragments.DetailsNeighbourFragment;
import com.sanganan.app.fragments.ImageSlideShow;
import com.sanganan.app.interfaces.ToolbarListner;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.PreviousMessageListQuery;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class SendBirdOpenChatActivity extends FragmentActivity implements ToolbarListner {
    private SendBirdChatFragment mSendBirdChatFragment;

    private View mTopBarContainer;
    private View mSettingsContainer;
    private String mChannelUrl;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;

    private static long backPressed = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.sendbird_slide_in_from_bottom, R.anim.sendbird_slide_out_to_top);
        setContentView(R.layout.activity_sendbird_open_chat);
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mChannelUrl = getIntent().getStringExtra("channel_url");
        if (mChannelUrl == null || mChannelUrl.length() <= 0) {
            finish();
            return;
        }


        initFragment();
        initUIComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * If the minimum SDK version you support is under Android 4.0,
         * you MUST uncomment the below code to receive push notifications.
         */
//        SendBird.notifyActivityResumedForOldAndroids();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * If the minimum SDK version you support is under Android 4.0,
         * you MUST uncomment the below code to receive push notifications.
         */
//        SendBird.notifyActivityPausedForOldAndroids();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeMenubar();
    }

    private void resizeMenubar() {
        ViewGroup.LayoutParams lp = mTopBarContainer.getLayoutParams();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = (int) (28 * getResources().getDisplayMetrics().density);
        } else {
            lp.height = (int) (48 * getResources().getDisplayMetrics().density);
        }
        mTopBarContainer.setLayoutParams(lp);
    }

    @Override
    public void finish() {
        if (mSendBirdChatFragment != null) {
            mSendBirdChatFragment.exitChannel();
        }
        super.finish();
        overridePendingTransition(R.anim.sendbird_slide_in_from_top, R.anim.sendbird_slide_out_to_bottom);
    }


    @Override
    public void onBackPressed() {
        fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1) {
            android.support.v4.app.FragmentManager.BackStackEntry backEntry = getSupportFragmentManager()
                    .getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

            fragmentManager.popBackStack();

        } else if (fragmentManager.getBackStackEntryCount() == 1) {

            finish();


        }
    }

    private void initFragment() {
        mSendBirdChatFragment = new SendBirdChatFragment();
        Bundle args = new Bundle();
        args.putString("channel_url", mChannelUrl);
        mSendBirdChatFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mSendBirdChatFragment).addToBackStack("openchatfragmnet")
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Helper.MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initUIComponents() {
        mTopBarContainer = findViewById(R.id.top_bar_container);

        mSettingsContainer = findViewById(R.id.settings_container);
        mSettingsContainer.setVisibility(View.GONE);

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSettingsContainer.getVisibility() != View.VISIBLE) {
                    mSettingsContainer.setVisibility(View.VISIBLE);
                } else {
                    mSettingsContainer.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.btn_participants).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsContainer.setVisibility(View.GONE);
                Intent intent = new Intent(SendBirdOpenChatActivity.this, SendBirdParticipantListActivity.class);
                intent.putExtra("channel_url", mChannelUrl);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_blocked_users).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSettingsContainer.setVisibility(View.GONE);
                Intent intent = new Intent(SendBirdOpenChatActivity.this, SendBirdBlockedUserListActivity.class);
                startActivity(intent);
            }
        });

        resizeMenubar();
    }

    @Override
    public void onButtonClick(Fragment newfragment, Boolean isCommingBack) {

        Common.hideSoftKeyboard(this);
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        if (isCommingBack) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                    R.anim.slide_in_left, R.anim.slide_in_right);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(fragment.getClass().getName());

            try {
                fragmentTransaction.commit();
            } catch (IllegalStateException ignored) {
            }

        }
    }

    @Override
    public void onButtonClickNoBack(Fragment newfragment) {
        fragment = (BaseFragment) newfragment;
        fragmentManager = getSupportFragmentManager();
        fragment = (BaseFragment) newfragment;
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit,
                R.anim.slide_in_left, R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }

    public static class SendBirdChatFragment extends Fragment {
        private static final int REQUEST_PICK_IMAGE = 100;
        private static final String identifier = "gaurav";

        private ListView mListView;
        private SendBirdChatAdapter mAdapter;
        private EditText mEtxtMessage;
        private Button mBtnSend;
        private ImageButton mBtnUpload;
        private ProgressBar mProgressBtnUpload;
        private String mChannelUrl;
        private OpenChannel mOpenChannel;
        private PreviousMessageListQuery mPrevMessageListQuery;
        private boolean mIsUploading;

        Common common;
        RequestQueue requestQueue;

        public SendBirdChatFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.sendbird_fragment_open_chat, container, false);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            RelativeLayout layoutTopOfChat = (RelativeLayout) getActivity().findViewById(R.id.top_bar_container);
            layoutTopOfChat.setVisibility(View.VISIBLE);


            mChannelUrl = getArguments().getString("channel_url");
            common = Common.getNewInstance(getActivity());
            requestQueue = VolleySingleton.getInstance(getActivity()).getRequestQueue();

            initUIComponents(rootView);

            enterChannel(mChannelUrl);

            return rootView;
        }

        @Override
        public void onPause() {
            super.onPause();
            if (!mIsUploading) {
                SendBird.removeChannelHandler(identifier);
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            if (!mIsUploading) {
                SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
                    @Override
                    public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
                        if (baseChannel.getUrl().equals(mChannelUrl)) {
                            mAdapter.appendMessage(baseMessage);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onMessageDeleted(BaseChannel channel, long msgId) {
                        if (channel.getUrl().equals(mChannelUrl)) {
                            boolean deleteMsg = false;

                            for (int i = 0; i < mAdapter.getCount(); i++) {
                                BaseMessage msg = (BaseMessage) mAdapter.getItem(i);
                                if (msg.getMessageId() == msgId) {
                                    mAdapter.delete(msg);
                                    deleteMsg = true;
                                    break;
                                }
                            }

                            if (deleteMsg) {
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

                refreshChannel();
                loadPrevMessages(true);
            } else {
                mIsUploading = false;

                /**
                 * Set this as true to restart auto-background detection,
                 * when your Activity is shown again after the external Activity is finished.
                 */
                SendBird.setAutoBackgroundDetection(true);
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }

        private void loadPrevMessages(final boolean refresh) {
            if (mOpenChannel == null) {
                return;
            }

            if (refresh || mPrevMessageListQuery == null) {
                mPrevMessageListQuery = mOpenChannel.createPreviousMessageListQuery();
            }

            if (mPrevMessageListQuery.isLoading()) {
                return;
            }

            if (!mPrevMessageListQuery.hasMore()) {
                return;
            }

            mPrevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
                @Override
                public void onResult(List<BaseMessage> list, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (refresh) {
                        mAdapter.clear();
                    }

                    for (BaseMessage message : list) {
                        mAdapter.insertMessage(message);
                    }
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(list.size());
                }
            });
        }

        private void enterChannel(String channelUrl) {
            mProgressBtnUpload.setVisibility(View.VISIBLE);
            OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
                @Override
                public void onResult(final OpenChannel openChannel, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressBtnUpload.setVisibility(View.GONE);
                        getActivity().finish();
                        return;
                    }

                    openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                        @Override
                        public void onResult(SendBirdException e) {
                            if (e != null) {
                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                mProgressBtnUpload.setVisibility(View.GONE);
                                getActivity().finish();
                                return;
                            }

                            mOpenChannel = openChannel;
                            SharedPreferences pref = getActivity().getSharedPreferences(Constants.preference, Context.MODE_PRIVATE);
                            ((TextView) getActivity().findViewById(R.id.txt_channel_name)).setText(pref.getString("userRwaName", "Unknown"));

                            loadPrevMessages(true);
                            mProgressBtnUpload.setVisibility(View.GONE);

                        }
                    });
                }
            });
        }

        private void exitChannel() {
            if (mOpenChannel != null) {
                mOpenChannel.exit(null);
            }
        }

        private void refreshChannel() {
            if (mOpenChannel != null) {
                mOpenChannel.refresh(null);
            }
        }

        private void initUIComponents(View rootView) {
            mAdapter = new SendBirdChatAdapter(getActivity(), mOpenChannel);

            mListView = (ListView) rootView.findViewById(R.id.list);
            turnOffListViewDecoration(mListView);
            mListView.setAdapter(mAdapter);

            mBtnSend = (Button) rootView.findViewById(R.id.btn_send);
            mBtnUpload = (ImageButton) rootView.findViewById(R.id.btn_upload);
            mProgressBtnUpload = (ProgressBar) rootView.findViewById(R.id.progress_btn_upload);
            mEtxtMessage = (EditText) rootView.findViewById(R.id.etxt_message);

            mBtnSend.setEnabled(false);
            mBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send();
                }
            });

            mBtnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Helper.requestReadWriteStoragePermissions(getActivity())) {
                        mIsUploading = true;

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);

                        /**
                         * Set this as false to maintain SendBird connection,
                         * even when an external Activity is started.
                         */
                        SendBird.setAutoBackgroundDetection(false);
                    }
                }
            });

            mEtxtMessage.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            send();
                        }
                        return true; // Do not hide keyboard.
                    }
                    return false;
                }
            });
            mEtxtMessage.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            mEtxtMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    mBtnSend.setEnabled(s.length() > 0);
                }
            });

            mListView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Helper.hideKeyboard(getActivity());
                    return false;
                }
            });
            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == SCROLL_STATE_IDLE) {
                        if (view.getFirstVisiblePosition() == 0 && view.getChildCount() > 0 && view.getChildAt(0).getTop() == 0) {
                            loadPrevMessages(false);
                        }
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Select")
                            .setItems(new String[]{"Delete Message", "Block User"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            final BaseMessage msg0 = (BaseMessage) mAdapter.getItem(position);
                                            mOpenChannel.deleteMessage(msg0, new BaseChannel.DeleteMessageHandler() {
                                                @Override
                                                public void onResult(SendBirdException e) {
                                                    if (e != null) {
                                                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    Toast.makeText(getActivity(), "Message deleted.", Toast.LENGTH_SHORT).show();
                                                    // Message will be deleted at ChannelHandler.
                                                }
                                            });
                                            break;

                                        case 1:
                                            BaseMessage msg1 = (BaseMessage) mAdapter.getItem(position);
                                            User target = null;

                                            if (msg1 instanceof AdminMessage) {
                                                Toast.makeText(getActivity(), "Admin message can not be deleted.", Toast.LENGTH_SHORT).show();
                                                return;
                                            } else if (msg1 instanceof UserMessage) {
                                                target = ((UserMessage) msg1).getSender();
                                            } else if (msg1 instanceof FileMessage) {
                                                target = ((FileMessage) msg1).getSender();
                                            }

                                            SendBird.blockUser(target, new SendBird.UserBlockHandler() {
                                                @Override
                                                public void onBlocked(User user, SendBirdException e) {
                                                    if (e != null) {
                                                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    Toast.makeText(getActivity(), user.getNickname() + " is blocked.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            break;
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null).create().show();

                    return true;
                }
            });
        }

        private void showUploadProgress(boolean tf) {
            if (tf) {
                mBtnUpload.setEnabled(false);
                mBtnUpload.setVisibility(View.INVISIBLE);
                mProgressBtnUpload.setVisibility(View.VISIBLE);
            } else {
                mBtnUpload.setEnabled(true);
                mBtnUpload.setVisibility(View.VISIBLE);
                mProgressBtnUpload.setVisibility(View.GONE);
            }
        }

        private void turnOffListViewDecoration(ListView listView) {
            listView.setDivider(null);
            listView.setDividerHeight(0);
            listView.setHorizontalFadingEdgeEnabled(false);
            listView.setVerticalFadingEdgeEnabled(false);
            listView.setHorizontalScrollBarEnabled(false);
            listView.setVerticalScrollBarEnabled(true);
            listView.setSelector(new ColorDrawable(0x00ffffff));
            listView.setCacheColorHint(0x00000000); // For Gingerbread scrolling bug fix
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
                    upload(data.getData());
                }
            }
        }

        private void send() {
            mOpenChannel.sendUserMessage(mEtxtMessage.getText().toString(), new BaseChannel.SendUserMessageHandler() {
                @Override
                public void onSent(UserMessage userMessage, SendBirdException e) {
                    if (e != null) {
                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }


                    callApiToNotifyUser("sent a message");

                    mAdapter.appendMessage(userMessage);
                    mAdapter.notifyDataSetChanged();
                    mEtxtMessage.setText("");
                }
            });

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Helper.hideKeyboard(getActivity());
            }
        }

        private void callApiToNotifyUser(String type) {

            String uri = Constants.BaseUrl + "chatnotification";
            try {
                JSONObject json = new JSONObject();
                json.put("RWAID", common.getStringValue(Constants.userRwa));
                json.put("UserID", common.getStringValue(Constants.id));
                json.put("Message", common.getStringValue(Constants.FirstName) + " " + type);
                json.put("Title", "mynukad " + common.getStringValue(Constants.userRwaName) + " chat");

                JsonObjectRequest req = new JsonObjectRequest(uri, json,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String s = response.toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                VolleyLog.d("error", error.getMessage());

                            }

                        }) {


                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        return headers;
                    }
                };
                try {
                    req.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(req);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }


        private void upload(Uri uri) {
            Hashtable<String, Object> info = Helper.getFileInfo(getActivity(), uri);
            final String path = (String) info.get("path");
            final File file = new File(path);
            final String name = file.getName();
            final String mime = (String) info.get("mime");
            final int size = (Integer) info.get("size");

            if (path == null) {
                Toast.makeText(getActivity(), "Uploading file must be located in local storage.", Toast.LENGTH_LONG).show();
            } else {
                showUploadProgress(true);
                mOpenChannel.sendFileMessage(file, name, mime, size, "", new BaseChannel.SendFileMessageHandler() {
                    @Override
                    public void onSent(FileMessage fileMessage, SendBirdException e) {
                        showUploadProgress(false);
                        if (e != null) {
                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        callApiToNotifyUser("sent an image");

                        mAdapter.appendMessage(fileMessage);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public static class SendBirdChatAdapter extends BaseAdapter {
        private static final int TYPE_UNSUPPORTED = 0;
        private static final int TYPE_USER_MESSAGE = 1;
        private static final int TYPE_ADMIN_MESSAGE = 2;
        private static final int TYPE_FILE_MESSAGE = 3;
        private static final int TYPE_TYPING_INDICATOR = 4;

        private final Context mContext;
        private final LayoutInflater mInflater;
        private final ArrayList<Object> mItemList;
        private final OpenChannel openChannel;

        public SendBirdChatAdapter(Context context, OpenChannel channel) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItemList = new ArrayList<>();
            openChannel = channel;
        }

        @Override
        public int getCount() {
            return mItemList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position >= mItemList.size()) {
                List<User> members = openChannel.getOperators();
                ArrayList<String> names = new ArrayList<>();
                for (User member : members) {
                    names.add(member.getNickname());
                }

                return names;
            }
            return mItemList.get(position);
        }

        public void delete(Object object) {
            mItemList.remove(object);
        }

        public void clear() {
            mItemList.clear();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void insertMessage(BaseMessage message) {
            mItemList.add(0, message);
        }

        public void appendMessage(BaseMessage message) {
            mItemList.add(message);
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= mItemList.size()) {
                return TYPE_TYPING_INDICATOR;
            }

            Object item = mItemList.get(position);
            if (item instanceof UserMessage) {
                return TYPE_USER_MESSAGE;
            } else if (item instanceof FileMessage) {
                return TYPE_FILE_MESSAGE;
            } else if (item instanceof AdminMessage) {
                return TYPE_ADMIN_MESSAGE;
            }

            return TYPE_UNSUPPORTED;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final Object item = getItem(position);

            if (convertView == null || ((ViewHolder) convertView.getTag()).getViewType() != getItemViewType(position)) {
                viewHolder = new ViewHolder();
                viewHolder.setViewType(getItemViewType(position));

                switch (getItemViewType(position)) {
                    case TYPE_UNSUPPORTED:
                        convertView = new View(mInflater.getContext());
                        convertView.setTag(viewHolder);
                        break;
                    case TYPE_USER_MESSAGE: {
                        TextView tv;
                        ImageView iv;
                        View v;

                        convertView = mInflater.inflate(R.layout.sendbird_view_group_user_message, parent, false);

                        v = convertView.findViewById(R.id.left_container);
                        viewHolder.setView("left_container", v);
                        iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
                        viewHolder.setView("left_thumbnail", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left);
                        viewHolder.setView("left_message", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_name);
                        viewHolder.setView("left_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_time);
                        viewHolder.setView("left_time", tv);

                        v = convertView.findViewById(R.id.right_container);
                        viewHolder.setView("right_container", v);
                        iv = (ImageView) convertView.findViewById(R.id.img_right_thumbnail);
                        viewHolder.setView("right_thumbnail", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right);
                        viewHolder.setView("right_message", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                        viewHolder.setView("right_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_time);
                        viewHolder.setView("right_time", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_status);
                        viewHolder.setView("right_status", tv);

                        convertView.setTag(viewHolder);
                        break;
                    }
                    case TYPE_ADMIN_MESSAGE: {
                        convertView = mInflater.inflate(R.layout.sendbird_view_admin_message, parent, false);
                        viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                        convertView.setTag(viewHolder);
                        break;
                    }
                    case TYPE_FILE_MESSAGE: {
                        TextView tv;
                        ImageView iv;
                        View v;

                        convertView = mInflater.inflate(R.layout.sendbird_view_group_file_message, parent, false);

                        v = convertView.findViewById(R.id.left_container);
                        viewHolder.setView("left_container", v);
                        iv = (ImageView) convertView.findViewById(R.id.img_left_thumbnail);
                        viewHolder.setView("left_thumbnail", iv);
                        iv = (ImageView) convertView.findViewById(R.id.img_left);
                        viewHolder.setView("left_image", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_name);
                        viewHolder.setView("left_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_left_time);
                        viewHolder.setView("left_time", tv);

                        v = convertView.findViewById(R.id.right_container);
                        viewHolder.setView("right_container", v);
                        iv = (ImageView) convertView.findViewById(R.id.img_right_thumbnail);
                        viewHolder.setView("right_thumbnail", iv);
                        iv = (ImageView) convertView.findViewById(R.id.img_right);
                        viewHolder.setView("right_image", iv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_name);
                        viewHolder.setView("right_name", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_time);
                        viewHolder.setView("right_time", tv);
                        tv = (TextView) convertView.findViewById(R.id.txt_right_status);
                        viewHolder.setView("right_status", tv);

                        convertView.setTag(viewHolder);
                        break;
                    }
                    case TYPE_TYPING_INDICATOR: {
                        convertView = mInflater.inflate(R.layout.sendbird_view_group_typing_indicator, parent, false);
                        viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
                        convertView.setTag(viewHolder);
                        break;
                    }
                }
            }

            viewHolder = (ViewHolder) convertView.getTag();

            switch (getItemViewType(position)) {
                case TYPE_UNSUPPORTED:
                    break;
                case TYPE_USER_MESSAGE:
                    final UserMessage message = (UserMessage) item;
                    if (message.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                        viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                        Helper.displayUrlImage(viewHolder.getView("right_thumbnail", ImageView.class), message.getSender().getProfileUrl(), true);
                        viewHolder.getView("right_name", TextView.class).setText(message.getSender().getNickname());
                        viewHolder.getView("right_message", TextView.class).setText(message.getMessage());
                        viewHolder.getView("right_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, message.getCreatedAt()));

                       /* int unreadCount = openChannel.getReadReceipt(message);
                        if (unreadCount > 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread " + unreadCount);
                        } else if (unreadCount == 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread");
                        } else {
                            viewHolder.getView("right_status", TextView.class).setText("");
                        }*/

                        viewHolder.getView("right_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle1 = new Bundle();
                                FragmentActivity activity = (FragmentActivity) mContext;
                                Fragment fragment = new DetailsNeighbourFragment();
                                bundle1.putString("ID", message.getSender().getUserId());
                                bundle1.putBoolean("fromChat", true);
                                fragment.setArguments(bundle1);
                                activity.getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, fragment).addToBackStack("openprofile")
                                        .commit();
                            }
                        });


                    } else {
                        viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                        Helper.displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), message.getSender().getProfileUrl(), true);
                        viewHolder.getView("left_name", TextView.class).setText(message.getSender().getNickname());
                        viewHolder.getView("left_message", TextView.class).setText(message.getMessage());
                        viewHolder.getView("left_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, message.getCreatedAt()));


                        viewHolder.getView("left_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle1 = new Bundle();
                                FragmentActivity activity = (FragmentActivity) mContext;
                                Fragment fragment = new DetailsNeighbourFragment();
                                bundle1.putString("ID", message.getSender().getUserId());
                                bundle1.putBoolean("fromChat", true);
                                fragment.setArguments(bundle1);
                                activity.getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, fragment).addToBackStack("openprofile")
                                        .commit();
                                Log.d("1A", "Here to write code for fragment open big image for me ");
                            }
                        });

                    }
                    break;
                case TYPE_ADMIN_MESSAGE:
                    AdminMessage adminMessage = (AdminMessage) item;
                    viewHolder.getView("message", TextView.class).setText(Html.fromHtml(adminMessage.getMessage()));
                    break;
                case TYPE_FILE_MESSAGE:
                    final FileMessage fileLink = (FileMessage) item;

                    if (fileLink.getSender().getUserId().equals(SendBird.getCurrentUser().getUserId())) {
                        viewHolder.getView("left_container", View.class).setVisibility(View.GONE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.VISIBLE);

                        Helper.displayUrlImage(viewHolder.getView("right_thumbnail", ImageView.class), fileLink.getSender().getProfileUrl(), true);
                        viewHolder.getView("right_name", TextView.class).setText(fileLink.getSender().getNickname());
                        if (fileLink.getType().toLowerCase().startsWith("image")) {
                            Helper.displayUrlImage(viewHolder.getView("right_image", ImageView.class), fileLink.getUrl());
                        } else {
                            viewHolder.getView("right_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                        }
                        viewHolder.getView("right_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, fileLink.getCreatedAt()));

                     /*   int unreadCount = openChannel.getReadReceipt(fileLink);
                        if (unreadCount > 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread " + unreadCount);
                        } else if (unreadCount == 1) {
                            viewHolder.getView("right_status", TextView.class).setText("Unread");
                        } else {
                            viewHolder.getView("right_status", TextView.class).setText("");
                        }*/

                        viewHolder.getView("right_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
                               /* builderSingle.setIcon(R.drawable.);*/
                                builderSingle.setTitle("Select an option");

                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_singlechoice);
                                arrayAdapter.add("Open Profile");
                                arrayAdapter.add("Open Image");


                                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String strName = arrayAdapter.getItem(which);
                                        FragmentActivity activity = (FragmentActivity) mContext;
                                        if (strName.equalsIgnoreCase("Open Profile")) {

                                            Bundle bundle1 = new Bundle();
                                            Fragment fragment = new DetailsNeighbourFragment();
                                            bundle1.putString("ID", fileLink.getSender().getUserId());
                                            bundle1.putBoolean("fromChat", true);
                                            fragment.setArguments(bundle1);
                                            activity.getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.fragment_container, fragment).addToBackStack("openprofile")
                                                    .commit();

                                        } else if (strName.equalsIgnoreCase("Open Image")) {
                                            ArrayList<String> listImage = new ArrayList<String>();
                                            listImage.add(fileLink.getUrl());
                                            DialogFragment fragment = new ImageSlideShow();
                                            Bundle bundle1 = new Bundle();
                                            bundle1.putStringArrayList("imagelist", listImage);
                                            bundle1.putBoolean("fromChat", true);
                                            fragment.setArguments(bundle1);
                                            fragment.setRetainInstance(true);
                                            fragment.show(activity.getSupportFragmentManager(), "tag_delivery");
                                        }

                                    }
                                });
                                builderSingle.show();


                                Log.d("1A", "Here to write code for fragment open big image for me ");
                            }
                        });
                    } else {
                        viewHolder.getView("left_container", View.class).setVisibility(View.VISIBLE);
                        viewHolder.getView("right_container", View.class).setVisibility(View.GONE);

                        Helper.displayUrlImage(viewHolder.getView("left_thumbnail", ImageView.class), fileLink.getSender().getProfileUrl(), true);
                        viewHolder.getView("left_name", TextView.class).setText(fileLink.getSender().getNickname());
                        if (fileLink.getType().toLowerCase().startsWith("image")) {
                            Helper.displayUrlImage(viewHolder.getView("left_image", ImageView.class), fileLink.getUrl());
                        } else {
                            viewHolder.getView("left_image", ImageView.class).setImageResource(R.drawable.sendbird_icon_file);
                        }
                        viewHolder.getView("left_time", TextView.class).setText(Helper.getDisplayDateTime(mContext, fileLink.getCreatedAt()));

                        viewHolder.getView("left_container").setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);
                               /* builderSingle.setIcon(R.drawable.);*/
                                builderSingle.setTitle("Select an option");

                                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_singlechoice);
                                arrayAdapter.add("Open Profile");
                                arrayAdapter.add("Open Image");


                                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String strName = arrayAdapter.getItem(which);
                                        FragmentActivity activity = (FragmentActivity) mContext;
                                        if (strName.equalsIgnoreCase("Open Profile")) {

                                            Bundle bundle1 = new Bundle();
                                            Fragment fragment = new DetailsNeighbourFragment();
                                            bundle1.putString("ID", fileLink.getSender().getUserId());
                                            bundle1.putBoolean("fromChat", true);
                                            fragment.setArguments(bundle1);
                                            activity.getSupportFragmentManager().beginTransaction()
                                                    .replace(R.id.fragment_container, fragment).addToBackStack("openprofile")
                                                    .commit();


                                        } else if (strName.equalsIgnoreCase("Open Image")) {
                                            ArrayList<String> listImage = new ArrayList<String>();
                                            listImage.add(fileLink.getUrl());
                                            DialogFragment fragment = new ImageSlideShow();
                                            Bundle bundle1 = new Bundle();
                                            bundle1.putStringArrayList("imagelist", listImage);
                                            bundle1.putBoolean("fromChat", true);
                                            fragment.setArguments(bundle1);
                                            fragment.setRetainInstance(true);
                                            fragment.show(activity.getSupportFragmentManager(), "tag_delivery");
                                        }

                                    }
                                });
                                builderSingle.show();

                                Log.d("1A", "Here to write code for fragment open big image for other");
                            }

                        });

                    }
                    break;

                case TYPE_TYPING_INDICATOR: {
                    int itemCount = ((List) item).size();
                    String typeMsg = ((List) item).get(0)
                            + ((itemCount > 1) ? " +" + (itemCount - 1) : "")
                            + ((itemCount > 1) ? " are " : " is ")
                            + "typing...";
                    viewHolder.getView("message", TextView.class).setText(typeMsg);
                    break;
                }
            }

            return convertView;
        }

        private class ViewHolder {
            private Hashtable<String, View> holder = new Hashtable<>();
            private int type;

            public int getViewType() {
                return this.type;
            }

            public void setViewType(int type) {
                this.type = type;
            }

            public void setView(String k, View v) {
                holder.put(k, v);
            }

            public View getView(String k) {
                return holder.get(k);
            }

            public <T> T getView(String k, Class<T> type) {
                return type.cast(getView(k));
            }
        }
    }
}