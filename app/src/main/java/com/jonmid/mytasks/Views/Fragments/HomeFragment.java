package com.jonmid.mytasks.Views.Fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jonmid.mytasks.Adapters.UserAdapter;
import com.jonmid.mytasks.HttpManager;
import com.jonmid.mytasks.Models.User;
import com.jonmid.mytasks.Parser.Json;
import com.jonmid.mytasks.R;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    View rootView;
    ProgressBar loader;
    List<User> listUser;
    RecyclerView recyclerView;
    UserAdapter userAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        loader = (ProgressBar) rootView.findViewById(R.id.loader);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.id_recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        onClickButton();

        // Inflate the layout for this fragment
        return rootView;
    }

    public Boolean isOnLine(){
        ConnectivityManager connec = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connec.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }else {
            return false;
        }
    }

    public void onClickButton(){
        if (isOnLine()){
            MyTask task = new MyTask();
            task.execute("https://jsonplaceholder.typicode.com/users");
        }else {
            Toast.makeText(getActivity(), "Sin conexión", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadData(){
        userAdapter = new UserAdapter(getActivity().getApplicationContext(), listUser);
        recyclerView.setAdapter(userAdapter);
    }

    private class MyTask extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String content = null;
            try {
                content = HttpManager.getData(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                listUser = Json.parserJsonUser(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

            loadData();
            loader.setVisibility(View.GONE);
        }
    }
}
