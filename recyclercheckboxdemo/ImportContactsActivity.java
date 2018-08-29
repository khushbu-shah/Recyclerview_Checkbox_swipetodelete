package com.example.blemoduletest.recyclercheckboxdemo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ImportContactsActivity extends Activity implements OnItemClickListener, OnCheckBoxClickListener
{
    private String TAG = "ImportContactsActivity.class.getSimpleName()";

    public static final int REQUEST_CONTACT = 300;

    private RecyclerView rvContacts;

    private ContactSelectionAdapter contactAdapter;

    private ArrayList<ContactBean> alContactBean = new ArrayList<>();

    private CheckBox cbAllContact;

    private LinearLayout llLoading;

    private RelativeLayout rlAllContact;

    private TextView tvNoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initViews();

        loadData();
    }

    /**
     * Find controls and handle click listener of Checkbox(All contact)
     */
    protected void initViews()
    {
        rvContacts = findViewById(R.id.rvContacts);

        cbAllContact = findViewById(R.id.cbAllContact);

        llLoading = findViewById(R.id.llLoading);

        rlAllContact = findViewById(R.id.rlAllContact);

        tvNoData = findViewById(R.id.tvNoData);

        cbAllContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox chk = (CheckBox) v;

                int itemCount = alContactBean.size();

                for(int i=0 ; i < itemCount ; i++){

                    ContactBean contactBean = alContactBean.get(i);
                    contactBean.setChecked(chk.isChecked());
                }
                if(contactAdapter != null)
                    contactAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Read Device Contact and Set it to recycler adapter
     */
    protected void loadData()
    {

        if ( isReadContactPermissionGranted(this))
        {
            alContactBean = new ArrayList<>();

            ReadContactTask task = new ReadContactTask(this,this);
            task.execute();

            rvContacts.setHasFixedSize(true);
            rvContacts.setLayoutManager(new LinearLayoutManager(ImportContactsActivity.this));

            contactAdapter = new ContactSelectionAdapter(ImportContactsActivity.this, alContactBean,this,this);

            rvContacts.setAdapter(contactAdapter);
        }
    }

    /**
     * Sort the list of Contact : Name wise
     * @param list
     * @return
     */
    ArrayList<ContactBean> sortList(ArrayList<ContactBean> list) {

        if(list != null && list.size() > 0)
        {
            Collections.sort(list, new Comparator<ContactBean>() {
                @Override
                public int compare(ContactBean teamMember1, ContactBean teamMember2) {
                    return teamMember1.getContactName().toUpperCase().compareTo(teamMember2.getContactName().toUpperCase());
                }
            });
        }
        return list;
    }


    /**
     * Set the Full name and First Char both ::
     *
     * Type 2 is for :: First Char
     * Type 1 is For :: Ful Name
     * @param list
     * @return
     */
    ArrayList<ContactBean> addAlphabets(ArrayList<ContactBean> list) {
        int i = 0;
        ArrayList<ContactBean> customList = new ArrayList<ContactBean>();
        ContactBean firstMember = new ContactBean();
        firstMember.setContactName(String.valueOf(list.get(0).getContactName().charAt(0)));
        firstMember.setType(2);
        customList.add(firstMember);
        for (i = 0; i < list.size() - 1; i++) {
            ContactBean ContactBean = new ContactBean();
            String name1 = String.valueOf(list.get(i).getContactName().charAt(0));
            String name2 = String.valueOf(list.get(i + 1).getContactName().charAt(0));
            if (name1.equalsIgnoreCase(name2)) {
                list.get(i).setType(1);
                customList.add(list.get(i));
            } else {
                list.get(i).setType(1);
                customList.add(list.get(i));
                ContactBean.setContactName(String.valueOf(name2).toUpperCase());
                ContactBean.setType(2);
                customList.add(ContactBean);
            }
        }
        list.get(i).setType(1);
        customList.add(list.get(i));
        return customList;
    }

    /**
     * Select all checkbox is clicked Handle
     * @param isAllCheck
     */
    @Override
    public void onCheckBoxClick(boolean isAllCheck) {

        if(contactAdapter.getItemCount()==contactAdapter.getSelectContactList().size())
            cbAllContact.setChecked(true);
        else
            cbAllContact.setChecked(false);
    }

    /**
     * Row Item click callback handle
     * @param position
     * @param v
     */
    @Override
    public void onItemClick(int position, View v) {

    }

    /**
     * Async Task For Read Device Contact
     */
    private class ReadContactTask extends AsyncTask<Void, Void, ArrayList<ContactBean>>
    {
        OnItemClickListener onItemClickListener;
        OnCheckBoxClickListener onCheckBoxClickListener;

        public ReadContactTask(OnItemClickListener onItemClickListener, OnCheckBoxClickListener onCheckBoxClickListener) {

            this.onCheckBoxClickListener = onCheckBoxClickListener;
            this.onItemClickListener = onItemClickListener;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!llLoading.isShown())
            {
                llLoading.setVisibility(View.VISIBLE);
                rlAllContact.setVisibility(View.GONE);
            }
        }

        @Override
        protected ArrayList<ContactBean> doInBackground(Void... voids) {
            return getContactList(ImportContactsActivity.this);
        }

        @Override
        protected void onPostExecute(ArrayList<ContactBean> contactBeans) {
            super.onPostExecute(contactBeans);

            alContactBean.clear();
            if(contactBeans != null && contactBeans.size() > 0)
            {
                alContactBean.addAll(addAlphabets(sortList(contactBeans)));
            }
            if(contactAdapter != null)
                contactAdapter.notifyDataSetChanged();
            else {

                contactAdapter = new ContactSelectionAdapter(ImportContactsActivity.this, alContactBean, onItemClickListener, onCheckBoxClickListener);

                rvContacts.setHasFixedSize(true);
                rvContacts.setLayoutManager(new LinearLayoutManager(ImportContactsActivity.this));

                rvContacts.setAdapter(contactAdapter);
            }

            if(alContactBean != null && alContactBean.size() > 0)
            {
                tvNoData.setVisibility(View.GONE);
            }
            else
            {
                tvNoData.setVisibility(View.VISIBLE);
            }

            if(llLoading.isShown())
            {
                llLoading.setVisibility(View.GONE);
                rlAllContact.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Read Contact Run Time Permission
     * @param activity
     * @return
     */
    public boolean isReadContactPermissionGranted(Activity activity)
    {
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            {
                Log.v(TAG, "Read Contact Permission is granted");
                return true;
            } else
            {
                Log.v(TAG, "Read Contact Permission is revoked");
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
                return false;
            }
        } else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            /*For Contact Read Request Permission*/
            case REQUEST_CONTACT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
                    ReadContactTask task = new ReadContactTask(this,this);
                    task.execute();

                } else
                {

                    dialog_message(ImportContactsActivity.this,
                            "Read Contact permission is needed.Please turn on the permission in Application Settings for additional functionality.");

                }
                break;
        }
    }

    /*Get Contact list from Device*/
    public static ArrayList<ContactBean> getContactList(Context context) {

        ArrayList<ContactBean> alContact = new ArrayList<>();

        ContactBean contactBean;

        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {

                contactBean = new ContactBean();

                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                contactBean.setContactName(name);

//                get Phone number
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactBean.setContactNumber(phoneNo);
                    }
                    pCur.close();
                }


                // get email and type
                Cursor emailCur = cr.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (emailCur.moveToNext()) {
                    // This would allow you get several email addresses
                    // if the email addresses were stored in an array
                    String email = emailCur.getString(
                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    String emailType = emailCur.getString(
                            emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                    contactBean.setContactEmail(email);

                }
                emailCur.close();

                alContact.add(contactBean);

            }
        }
        else
        {
            return new ArrayList<>();
        }
        if(cur!=null){
            cur.close();
        }
        return alContact;
    }

    public static void dialog_message(Context context, String msg)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("SpecBooks");
        alertDialog.setMessage(msg);
        //   alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,R.color.bg_dash_text));

        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                return;
            }
        });

        alertDialog.show();
    }

}
