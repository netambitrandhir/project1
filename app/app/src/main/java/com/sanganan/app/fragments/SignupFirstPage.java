package com.sanganan.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanganan.app.R;
import com.sanganan.app.activities.StartSearchPage;
import com.sanganan.app.activities.TermsANDCondition;
import com.sanganan.app.common.Common;
import com.sanganan.app.common.Constants;
import com.sanganan.app.interfaces.UpdateFlatNo;
import com.sanganan.app.model.Flat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFirstPage extends ActionBarActivity implements UpdateFlatNo {

    EditText flat_No, emailId, phoneNo, fullName, passWord;
    ImageView signUp;
    TextView society_name, society_selected, flatno, blockname, email, phone_no, full_name, password, account_yet, termandcondition;
    Common common;
    private static final String url = Constants.BaseUrl + "registeration";

    Typeface karlaB, karlaR, wsregular;

    RelativeLayout groupspinner;

    CheckBox checkbox1, checkbox;
    TextView textCheckbox1, textCheckbox;
    String residentType = "1";

    public android.support.v4.app.FragmentManager fragmentManager;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Fragment fragment;
    Context context;
    Flat flat;

    public SignupFirstPage() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = Common.getNewInstance(this);
        setContentView(R.layout.sign_up);
        Typeface ubuntuB = Typeface.createFromAsset(SignupFirstPage.this.getAssets(), "Ubuntu-B.ttf");
        karlaB = Typeface.createFromAsset(SignupFirstPage.this.getAssets(), "Karla-Bold.ttf");
        karlaR = Typeface.createFromAsset(SignupFirstPage.this.getAssets(), "Karla-Regular.ttf");
        wsregular = Typeface.createFromAsset(SignupFirstPage.this.getAssets(), "WorkSans-Regular.ttf");

        TextView title = (TextView) findViewById(R.id.titletextView);
        title.setText("Sign-Up");
        title.setTypeface(ubuntuB);
        context = SignupFirstPage.this;

        society_selected = (TextView) findViewById(R.id.society_selected);
        society_name = (TextView) findViewById(R.id.societyName);
        society_name.setText(common.getStringValue("SocietyName"));
        flatno = (TextView) findViewById(R.id.flatno);
        blockname = (TextView) findViewById(R.id.blockname);
        email = (TextView) findViewById(R.id.email);
        phone_no = (TextView) findViewById(R.id.phone_no);
        full_name = (TextView) findViewById(R.id.full_name);
        password = (TextView) findViewById(R.id.password);
        account_yet = (TextView) findViewById(R.id.account_yet);
        termandcondition = (TextView) findViewById(R.id.termandcondition);

        checkbox = (CheckBox) findViewById(R.id.checkbox);
        checkbox1 = (CheckBox) findViewById(R.id.checkbox1);
        textCheckbox = (TextView) findViewById(R.id.textCheckbox);
        textCheckbox1 = (TextView) findViewById(R.id.textCheckbox1);

        TextView phonedesc = (TextView) findViewById(R.id.phonedesc);
        phonedesc.setTypeface(karlaR);


        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setFocusable(false);
                checkbox.setChecked(true);
                checkbox1.setChecked(false);
                residentType = "0";
            }
        });

        checkbox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox1.setFocusable(false);
                checkbox1.setChecked(true);
                checkbox.setChecked(false);
                residentType = "1";
            }
        });


        textCheckbox1.setTypeface(karlaB);
        textCheckbox.setTypeface(karlaB);
        society_selected.setTypeface(karlaB);
        society_name.setTypeface(wsregular);
        flatno.setTypeface(karlaB);
        blockname.setTypeface(karlaR);
        email.setTypeface(karlaB);
        phone_no.setTypeface(karlaB);
        full_name.setTypeface(karlaB);
        password.setTypeface(karlaB);

        account_yet.setTypeface(karlaR);
        termandcondition.setTypeface(karlaB);

        society_name.setText(common.getStringValue("SocietyName"));
        flat_No = (EditText) findViewById(R.id.block);
        //  String str = common.getStringValue("flatNumber");
        //  flat_No.setText(str);

        emailId = (EditText) findViewById(R.id.emailEditText);
        phoneNo = (EditText) findViewById(R.id.phone);
        fullName = (EditText) findViewById(R.id.fullName);
        passWord = (EditText) findViewById(R.id.pswdEditText);

        signUp = (ImageView) findViewById(R.id.signUpButton);
        groupspinner = (RelativeLayout) findViewById(R.id.groupspinner);

        flat_No.setTypeface(wsregular);
        emailId.setTypeface(wsregular);
        phoneNo.setTypeface(wsregular);
        fullName.setTypeface(wsregular);
        passWord.setTypeface(wsregular);

        flat_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlatHintFragment dialog = new FlatHintFragment();
                ActionBarActivity activity = (ActionBarActivity) context;
                dialog.show(activity.getSupportFragmentManager(), "flat hint");
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (common.isNetworkAvailable()) {
                    String flatnumber = flat_No.getText().toString();
                    String emailid = emailId.getText().toString();
                    String phoneNumber = phoneNo.getText().toString();
                    String password = passWord.getText().toString();
                    String fullname = fullName.getText().toString();

                    if (fullname.isEmpty() || flatnumber.isEmpty() || emailid.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                        Toast.makeText(SignupFirstPage.this, "Fill all the mandatory fields", Toast.LENGTH_SHORT).show();
                    } else if (!isValidEmail(emailid)) {
                        emailId.setError("enter valid email");
                    } else if (phoneNumber.length() < 10 || phoneNumber.length() > 12) {
                        phoneNo.setError("enter valid phone number");
                    } else if (!isAlpha(fullname)) {
                        fullName.setError("enter valid name");
                    } else if (password.length() < 4) {
                        passWord.setError("password must be  4 characters.");
                    } else {
                        Intent intent = new Intent(SignupFirstPage.this, SignupSecondPage.class);
                        Bundle b = new Bundle();
                        b.putString("flatnumberKey", flat.getId());
                        b.putString("emailidKey", emailid);
                        b.putString("phonenumberKey", phoneNumber);
                        b.putString("passwordKey", password);
                        b.putString("fullnameKey", fullname);
                        b.putString("residentType", residentType);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(SignupFirstPage.this, "No internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        groupspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupFirstPage.this, StartSearchPage.class);
                Bundle dataTOFragment = new Bundle();
                dataTOFragment.putString("fromclass", SignupFirstPage.class.getName());
                intent.putExtras(dataTOFragment);
                Constants.fromWhere = "signup_first_page";
                startActivity(intent);

            }
        });


        termandcondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupFirstPage.this, TermsANDCondition.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        society_name = (TextView) findViewById(R.id.societyName);
        society_name.setText(common.getStringValue("SocietyName"));
    }


    @Override
    public void setFlat(Flat flat) {
        flat_No.setText(flat.getName());
        this.flat = flat;
    }


    public boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean isAlpha(String name) {
        char[] chars = name.toCharArray();

        for (char c : chars) {
            if (!Character.isLetter(c)) {
                if (!Character.isSpace(c)) {
                    return false;
                }
            }
        }

        return true;
    }
}
