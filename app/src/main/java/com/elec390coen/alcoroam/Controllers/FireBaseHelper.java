package com.elec390coen.alcoroam.Controllers;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireBaseHelper {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
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

