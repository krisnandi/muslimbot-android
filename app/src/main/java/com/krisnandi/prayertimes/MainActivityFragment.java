package com.krisnandi.prayertimes;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements DelegateBehaviour {

    public MainActivityFragment() {
        Log.d("testing", "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("testing", "wwwwwwwwwwwwwwwwwwwwwww");



    }

    @Override
    public void onDelegateVoid() {
        Log.d("testing", "delegate work 2");
    }
}
