package ir.hadimohammadi.beharfim;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ContactViewHolder> {

    private List<CardInfo> contactList;
    public static Activity context;


    public CardAdapter(List<CardInfo> contactList, Activity activity) {
        this.contactList = contactList;
        context = activity;
    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }


    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        //contactViewHolder.text_id.setText(contactList.get(i).getId()+"");

        CardInfo ci = contactList.get(i);

        contactViewHolder.mainText.setText(ci.Groups_Title);
        contactViewHolder.subText.setText(ci.Groups_About);
        Glide.with(context).load(ci.Groups_Avatar).into(contactViewHolder.profile_image);


        contactViewHolder.cardChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context,Chatroom.class);
                i.putExtra("Groups_Title",ci.Groups_Title);
                i.putExtra("Groups_ID",ci.Groups_ID);
                context.startActivity(i);
            }
        });

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatscard, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView mainText,subText;
        CircleImageView profile_image;
        RelativeLayout cardChats;

        public ContactViewHolder(final View v) {
            super(v);

     //      Typeface font = Typeface.createFromAsset(v.getContext().getAssets(), "iran.ttf");


            mainText = v.findViewById(R.id.mainText);
            subText = v.findViewById(R.id.subText);
            profile_image = v.findViewById(R.id.profile_image);
            cardChats = v.findViewById(R.id.cardChats);

        //    mainText.setTypeface(font);
        //    subText.setTypeface(font);

        }
    }
}
