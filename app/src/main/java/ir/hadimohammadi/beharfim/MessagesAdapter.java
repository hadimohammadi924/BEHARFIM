package ir.hadimohammadi.beharfim;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ContactViewHolder> {

    private List<CardInfo> contactList;
    public static Activity context;


    public MessagesAdapter(List<CardInfo> contactList, Activity activity) {
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
        SharedPreferences sp = context.getSharedPreferences("Beharfim", Context.MODE_PRIVATE);
        String Users_ID = sp.getString("Users_ID","");


        if(ci.GroupMessages_Sender_ID.equals(Users_ID)){
            contactViewHolder.myMessageLayout.setVisibility(View.VISIBLE);
            contactViewHolder.nameMy.setText(ci.UserName);
            contactViewHolder.textMy.setText(ci.GroupMessages_Text);
            Glide.with(context).load(ci.UserImage).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(contactViewHolder.profile_image2);
            if(!ci.GroupMessages_File.equals("null")){
                contactViewHolder.imageMy.setVisibility(View.VISIBLE);
                Glide.with(context).load(ci.GroupMessages_File).into(contactViewHolder.imageMy);
            }
        }else{
            contactViewHolder.otherMessageLayout.setVisibility(View.VISIBLE);
            contactViewHolder.nameOther.setText(ci.UserName);
            contactViewHolder.textOther.setText(ci.GroupMessages_Text);
            Glide.with(context).load(ci.UserImage).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(contactViewHolder.profile_image);
            if(!ci.GroupMessages_File.equals("null")){
                contactViewHolder.imageOther.setVisibility(View.VISIBLE);
                Glide.with(context).load(ci.GroupMessages_File).into(contactViewHolder.imageOther);
            }
        }

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chatroom_chats, viewGroup, false);
        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView nameOther,nameMy,textOther,textMy;
        CircleImageView profile_image,profile_image2;
        ImageView imageMy,imageOther;
        RelativeLayout myMessageLayout;
        RelativeLayout otherMessageLayout;

        public ContactViewHolder(final View v) {
            super(v);

         //   Typeface font = Typeface.createFromAsset(v.getContext().getAssets(), "iran.ttf");


            nameOther = v.findViewById(R.id.nameOther);
            nameMy = v.findViewById(R.id.nameMy);
            textOther = v.findViewById(R.id.textOther);
            textMy = v.findViewById(R.id.textMy);
            profile_image = v.findViewById(R.id.profOther);
            profile_image2 = v.findViewById(R.id.profMy);
            imageMy = v.findViewById(R.id.imageMy);
            imageOther = v.findViewById(R.id.imageOther);
            myMessageLayout = v.findViewById(R.id.myMessageLayout);
            otherMessageLayout = v.findViewById(R.id.otherMessageLayout);


        }
    }
}
