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

        name =  view.findViewById(R.id.editTextTextPersonName);
        password = view.findViewById(R.id.editTextTextPassword);
        id = view.findViewById(R.id.editTextNumberDecimal);
        email = view.findViewById(R.id.editTextTextEmailAddress);
        phone = view.findViewById(R.id.editTextPhone);

        view.findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataEntered()) {
                    writeToDb(view, name, password, id, email, phone);
                    Toast.makeText(view.getContext(), "SUCCESS REGISTER!", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        return view;
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean checkDataEntered() {
        boolean res = true;
        if (isEmpty(name)) {
            name.setError("name is required!");
            res = false;
        }
        if (isEmpty(id)) {
            id.setError("Id is required!");
            res = false;
        }
        if (isEmpty(password)) {
            password.setError("Password is required!");
            res = false;
        }
        if (isEmpty(phone)) {
            phone.setError("Phone number is required!");
            res = false;
        }
        if (isEmail(email) == false) {
            email.setError("Enter valid email!");
            res = false;
        }
        return res;

    }

    void writeToDb(View v, EditText name_, EditText password_, EditText id_, EditText email_, EditText phone_){
        String name = name_.getText().toString();
        String password = password_.getText().toString();
        String id = id_.getText().toString();
        String email = email_.getText().toString();
        String phone = phone_.getText().toString();
        String url = String.format(addUserUrl, id, email, password, phone);
        new GetURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        
        try{
            FileOutputStream fileOutputStreamLogin = v.getContext().openFileOutput("usersLogin.txt", 0);
            FileOutputStream fileOutputStreamData = v.getContext().openFileOutput("usersData.txt", 0);
            String strLogin = name + "\n" + password + "\n";
            fileOutputStreamLogin.write(strLogin.getBytes());
            fileOutputStreamLogin.close();
            String allData = id + "\n" + phone + "\n"  + email;
            fileOutputStreamData.write(allData.getBytes());
            fileOutputStreamData.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}