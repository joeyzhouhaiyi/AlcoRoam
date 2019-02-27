package com.elec390coen.alcoroam.Controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireBaseHelper {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    //private String exception;
    public FireBaseHelper()
    {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth()
    {
        return mAuth;
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public boolean isUserSignedIn()
    {
        return currentUser!=null;
    }

}

