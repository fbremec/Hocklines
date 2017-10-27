package com.example.flo.hocklines.match.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.flo.hocklines.MainActivity;
import com.example.flo.hocklines.R;
import com.example.flo.hocklines.match.MatchSelection;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static boolean isReadingMatchDetails = false;

    public MatchFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MatchFragment newInstance(String param1, String param2) {
        MatchFragment fragment = new MatchFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getLoaderManager().initLoader(0, null, this);
        View rootView =  inflater.inflate(R.layout.fragment_resultat, container, false);
        View item =  inflater.inflate(R.layout.item_resultat, container, false);
        ((TextView)item.findViewById(R.id.item_match_textview_equipeNameA)).setText("test");
        GridLayout gl = (GridLayout) rootView.findViewById(R.id.fragment_list_match_gridlayout);
        gl.addView(item);

        return rootView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        MatchSelection where = new MatchSelection();
        where.equipe(MainActivity.equipe);
        return new CursorLoader(getContext(),where.uri(),null,where.sel(),where.args(),null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(!isReadingMatchDetails){
            Log.d("Fragment match","je suis la");

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
