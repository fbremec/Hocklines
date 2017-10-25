package com.example.flo.hocklines.licences.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.flo.hocklines.licence.LicenceSelection;
import com.example.flo.hocklines.MainActivity;
import com.example.flo.hocklines.R;
import com.example.flo.hocklines.events.SearchVisibilityEvent;
import com.example.flo.hocklines.licence.LicenceBean;
import com.example.flo.hocklines.licences.events.SearchLicenceEvent;
import com.example.flo.hocklines.utils.UtilsFunction;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.File;
import java.util.HashMap;


public class LicencesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private GridLayout gridLayout;
    private HashMap<String,LicenceBean> listJoueur;

    public LicencesFragment() {
        EventBus.getDefault().register(this);
    }

    /**
     * Returns a new instance of this fragment
     */
    public static LicencesFragment newInstance() {
        return new LicencesFragment();
    }

    //unregister EventBus and set screen orientation portait
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new SearchVisibilityEvent(View.GONE));
        EventBus.getDefault().unregister(this);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    //put licence file png to view
    private void appendLicence(LicenceBean bean){
        if(gridLayout != null){
            gridLayout.setRowCount(gridLayout.getRowCount()+3);
            final ImageView i = new ImageView(getContext());
            Glide.with(getContext()).load(new File(bean.getPathfile())).into(i);
            i.setVisibility(View.GONE);
            TextView t = new TextView(getContext());
            t.setText("Nom : "+bean.getNomjoueur()+" \nN°lic : "+bean.getNumlicence()+" \nBlanc : "+bean.getNumeromaillotblanc()+" Noir : "+bean.getNumeromaillotnoir()+"");
            t.setPadding(40,0,0,0);
            t.setLayoutParams(new GridLayout.LayoutParams());
            t.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
            t.setBackgroundColor(getResources().getColor(R.color.red));
            t.setTextSize(20);
            t.setTextColor(Color.WHITE);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TextView t = (TextView)view;
                    if(i.getVisibility() == View.GONE)
                        i.setVisibility(View.VISIBLE);
                    else
                        i.setVisibility(View.GONE);

                }
            });

            View v = new View(getContext());
            v.setLayoutParams(new GridLayout.LayoutParams());
            v.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
            v.getLayoutParams().height = 40;

            gridLayout.addView(t);
            gridLayout.addView(i);
            gridLayout.addView(v);
        }



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_licences, container, false);
        EventBus.getDefault().post(new SearchVisibilityEvent(View.VISIBLE));
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        gridLayout = (GridLayout)rootView.findViewById(R.id.fragment_licences_gridLayout);
        getLoaderManager().initLoader(0, null, this);
        this.listJoueur = new HashMap<>();
        return rootView;
    }


    //Event start to search a licence
    //Send by MainActivity
    @Subscribe
    public void onSearchLicenceEvent(SearchLicenceEvent event){
        Log.d("Search::::::::::",event.nomLicence);
        if(gridLayout != null)
            gridLayout.removeAllViews();
        for(String key : listJoueur.keySet()){
            String keyTest = key.toLowerCase();
            if(keyTest.contains(event.nomLicence) && listJoueur != null &&listJoueur.get(key) != null){
                this.appendLicence(constructBean(key,listJoueur.get(key).getNumeromaillotblanc()
                        ,listJoueur.get(key).getNumeromaillotnoir(), listJoueur.get(key).getPathfile()
                        ,listJoueur.get(key).getNumlicence()));
            }
        }
    }

    private LicenceBean constructBean(String nomJoueur, int numMaillotBlanc, int numMaillotNoir,String pathFile, int numLicence){
        gridLayout.removeAllViews();
        LicenceBean bean = new LicenceBean();
        bean.setNumeromaillotblanc(numMaillotBlanc);
        bean.setNumeromaillotnoir(numMaillotNoir);
        bean.setPathfile(pathFile);
        bean.setNomjoueur(nomJoueur);
        bean.setNumlicence(numLicence);
        return bean;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            this.adaptImageViewToLandscape();
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            this.adaptImageViewToPortrait();
        }

    }

    private void adaptImageViewToPortrait(){
        for(int i = 0; i<gridLayout.getChildCount();i++){
            View v = gridLayout.getChildAt(i);
            if(v instanceof ImageView){
                ImageView image = (ImageView)v;
                image.setLayoutParams(new GridLayout.LayoutParams());
                image.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
                image.getLayoutParams().height = GridLayout.LayoutParams.MATCH_PARENT;

            }

        }
    }

    private void adaptImageViewToLandscape(){
        for(int i = 0; i<gridLayout.getChildCount();i++){
            View v = gridLayout.getChildAt(i);
            if(v instanceof ImageView){
                ImageView image = (ImageView)v;
                image.setLayoutParams(new GridLayout.LayoutParams());
                image.getLayoutParams().width = GridLayout.LayoutParams.MATCH_PARENT;
                image.getLayoutParams().height = UtilsFunction.getScreenSizeInPixel(getActivity()).y-60;
            }
        }
    }


    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        LicenceSelection where = new LicenceSelection();
        where.equipe(MainActivity.equipe);
        return new android.support.v4.content.CursorLoader(getContext(),where.uri(),null,where.sel(),where.args(),null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.d("onLoad",data.getColumnName(3));
        while (data.moveToNext()){
            LicenceBean bean = constructBean(data.getString(1),data.getInt(5),data.getInt(6),data.getString(3),data.getInt(4));
            if(bean.getPathfile() != null){
                this.listJoueur.put(bean.getNomjoueur(),bean);
                appendLicence(bean);
            }
        }
//        if(!data.moveToFirst())
//            Toast.makeText(getContext(),"impossible de télécharger les données de cette équipe",Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


}
