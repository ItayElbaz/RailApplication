package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {
    EditText name;
    EditText password;

    public Login() { }

    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        name = view.findViewById(R.id.editTextLoginName); // The name that the user has inserted.
        password = view.findViewById(R.id.editTextLoginPassword); // The password that the user has inserted.

        Button loginButton = view.findViewById(R.id.buttonLogin); // The login button - below is the function that executes when the user pushes the login button.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDataEntered()) { // The function returns true only if the user filled all the necessary details.
                    if (isRegister(v)) { // The function returns true only if the user is already signed.
                        Toast.makeText(v.getContext(), "logged", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), RailVoucherActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(v.getContext(), "You must register!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Button toRegisterButton = view.findViewById(R.id.buttonToRegister);
        toRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register fr = new Register();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment_place, fr).addToBackStack(null).commit();
            }
        });

        return view;
    }

    boolean isEmpty(EditText text) { //Checks the filed is not empty.
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private boolean checkDataEntered() { // The function returns true only if the user has inserted all the info and also check correctness.
        boolean res = true;
        if (isEmpty(name)) {
            name.setError("name is required!");
            res = false;
        }

        if (isEmpty(password)) {
            password.setError("Password is required!");
            res = false;
        }
        return res;

    }

    private boolean isRegister(View v) { // The function return true only if the user is already registered.
        try{
            FileInputStream fileInputStream = getActivity().openFileInput("usersLogin.txt"); //On register, the details are saved in this file.
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream); // Create InputStreamReader
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader); // Create a buffer and insert the data
            String fileName = bufferedReader.readLine(); //Return the first line in the file that should be the user name.
            if(fileName == null){
                return  false;
            }
            String filePass = bufferedReader.readLine(); //Return the second line in the file that should be the user password.

            if(!name.getText().toString().equals(fileName) ){ //Validate the user name.
                Toast.makeText(v.getContext(),"User not exist", Toast.LENGTH_SHORT).show();
                return false;
            }

            if(!password.getText().toString().equals(filePass)){ //Validate the user password.
                Toast.makeText(v.getContext(),"Incorrect password!", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}