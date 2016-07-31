package com.redefineeverything.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Iain Forrest on 26/07/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    /**
     * using a standard Holder Pattern with an onClickListener attached to an Intent to open the
     * to the book information screen on the users web browser.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context mContext = getContext();
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item, parent, false);
            holder = new ViewHolder();
            holder.listItemView = convertView;
            holder.titleTextView = (TextView)
                    convertView.findViewById(R.id.title);
            holder.authorTextView = (TextView)
                    convertView.findViewById(R.id.authors);
            holder.descriptionView = (TextView)
                    convertView.findViewById(R.id.description);
            convertView.setTag(holder);
            holder.listItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri webpage = Uri.parse(getItem(holder.position).getmInfoUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                        mContext.startActivity(intent);
                    }
                }
            });

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //get the {@link Book} object located at this position in the list
        Book currentAttraction = (Book)getItem(position);
        holder.titleTextView.setText(currentAttraction.getBookTitle());
        holder.authorTextView.setText(currentAttraction.getAuthorList());
        holder.descriptionView.setText(currentAttraction.getDescription());
        holder.position = position;

        return convertView;

    }

    static class ViewHolder {
        public View listItemView;
        public TextView titleTextView;
        public TextView authorTextView;
        public TextView descriptionView;
        public int position;
    }
}