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

public class ItemAdapter extends ArrayAdapter {
    private int resourseId;

    public ItemAdapter(@NonNull Context context, int resource, @NonNull List<IntroductionItem> objects) {
        super( context, resource, objects );
        resourseId=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        IntroductionItem item= (IntroductionItem) getItem( position );
        View view= LayoutInflater.from( getContext() ).inflate( resourseId,parent,false );
        TextView t1=view.findViewById( R.id.t1 );
        TextView t2=view.findViewById( R.id.t2 );
        t1.setText( item.text1 );
        t2.setText( item.text2 );
        return view;
    }
}
