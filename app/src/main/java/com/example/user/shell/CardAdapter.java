package com.example.user.shell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.user.shell.UI.Activity.R;

import java.util.List;



public class CardAdapter extends ArrayAdapter {

    private int resourseId;

    public CardAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super( context, resource, objects );
        resourseId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String num= (String) getItem( position );
        View view= LayoutInflater.from( getContext() ).inflate( resourseId,parent,false );
        TextView cardnum=view.findViewById( R.id.card_num );
        cardnum.setText( "**** **** **** "+num );
        return view;
    }
}
