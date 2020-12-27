package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register extends Fragment {
    EditText name;
    EditText password;
    EditText id;
    EditText email;
    EditText phone;
    private String addUserUrl= "http://35.234.68.144/add_user?id=%s&email=%s&password=%s&phone=%s";

    public Register() {
        // Required empty public constructor
    }

    public static Register newInstance() {
        Register fragment = new Register();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        // Take all the info that the user has inserted on the edit texts field.
        name =  view.findViewById(R.id.editTextTextPersonName);
        password = view.findViewById(R.id.editTextTextPassword);
        id = view.findViewById(R.id.editTextNumberDecimal);
        email = view.findViewById(R.id.editTextTextEmailAddress);
        phone = view.findViewById(R.id.editTextPhone);

        view.findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // After the user has clicked on the Register button this function is happened.
                if (checkDataEntered()) { // Return true only if all the data was inserted and validate the correctness.
                    writeToDb(view, name, password, id, email, phone); // Write to the local db the data, so on login we can it.
                    Toast.makeText(view.getContext(), "SUCCESS REGISTER!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }
    // Check that the inserted data is not empty.
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    // Check that the inserted email is valid.
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }
    // Check that the inserted data is not empty and correct.
    boolean checkDataEntered() {
        boolean res = true;
        if (isEmpty(name)) { //check for email data
            name.setError("name is required!");
            res = false;
        }
        if (isEmpty(id)) {//check for id data
            id.setError("Id is required!");
            res = false;
        }
        if (isEmpty(password)) { //check for password data
            password.setError("Password is required!");
            res = false;
        }
        if (isEmpty(phone)) { //check for phone data
            phone.setError("Phone number is required!");
            res = false;
        }
        if (isEmail(email) == false) {
            email.setError("Enter valid email!");
            res = false;
        }
        return res;

    }
    // After validated all the data is not null and correct its save the data to the db.
    void writeToDb(View v, EditText name_, EditText password_, EditText id_, EditText email_, EditText phone_){
        String name = name_.getText().toString();
        String password = password_.getText().toString();
        String id = id_.getText().toString();
        String email = email_.getText().toString();
        String phone = phone_.getText().toString();
        String url = String.format(addUserUrl, id, email, password, phone);
        new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        
        try{//Open 2 files that will be the local DB.
            FileOutputStream fileOutputStreamLogin = v.getContext().openFileOutput("usersLogin.txt", 0);
            FileOutputStream fileOutputStreamData = v.getContext().openFileOutput("usersData.txt", 0);
            String strLogin = name + "\n" + password + "\n"; // Make user info line by format to the file.
            fileOutputStreamLogin.write(strLogin.getBytes()); // Write the data to the file.
            fileOutputStreamLogin.close();
            String allData = name + "\n" + password + "\n" + id + "\n" + phone + "\n"  + email; // Make user info line by format to the file.
            fileOutputStreamData.write(allData.getBytes()); // Write the data to the db (file).
            fileOutputStreamData.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}