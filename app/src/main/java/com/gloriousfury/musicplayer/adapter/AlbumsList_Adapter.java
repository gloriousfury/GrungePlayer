package com.gloriousfury.musicplayer.adapter;

import android.content.Context;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.gloriousfury.musicplayer.R;
import com.gloriousfury.musicplayer.model.AlbumLists;
import com.gloriousfury.musicplayer.model.Audio;

import java.util.ArrayList;
import java.util.List;

public class AlbumsList_Adapter extends RecyclerView.Adapter<AlbumsList_Adapter.ViewHolder> {
    private List<AlbumLists> albumLists;
    Context context;


    String CATEGORY_TITLE = "albumLists_program_title";

    String TRAINING_DESCRIPTION = "albumLists_description";


    private static final String TAG = "CustomAdapter";
    private RecyclerView.OnItemTouchListener onItemTouchListener;



    public AlbumsList_Adapter(Context context){
        this.context = context;

    }

    public AlbumsList_Adapter(Context context, List<AlbumLists> albumLists) {

        this.context = context;
        this.albumLists = albumLists;


    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        TextView albumListsTitle, seeMoreText;
        ImageView albumListsView;


        RecyclerView albumLists_recycler_view;
        RelativeLayout albumListsLayout,albumListsHeader, firstlayout, secondlayout;

        public ViewHolder(View view) {
            super(view);


            albumListsTitle = (TextView) view.findViewById(R.id.title);

//            albumListsTitle.setTypeface(CommonUtil.getBold(context));

            albumLists_recycler_view = (RecyclerView) view.findViewById(R.id.single_albumlist_recycler_view);


            view.setClickable(true);


        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_album_layout, viewGroup, false);
        return new ViewHolder(v);


    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

//            changeBackgroundColor(position, viewHolder);
        ArrayList<Audio> dropDowns = albumLists.get(position).getAlbums();


//        viewHolder.firstlayout.setBackgroundColor(ContextCompat.getColor(context, albumLists.get(position).getBackgroundColor()));
//        viewHolder.secondlayout.setBackgroundColor(ContextCompat.getColor(context, albumLists.get(position).getBackgroundColor2()));
        viewHolder.albumListsTitle.setText(albumLists.get(position).getAlphabet());
//        viewHolder.albumListsView.setBackgroundColor(ContextCompat.getColor(context,albumLists.get(position).getBackgroundColor()));
//        viewHolder.cardView.setBackgroundColor(ContextCompat.getColor(context,albumLists.get(position).getBackgroundColor()));


        renderAlbumList(dropDowns, viewHolder.albumLists_recycler_view);


    }




    private void renderAlbumList(ArrayList<Audio> dropDowns, RecyclerView recyclerView) {



//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
//        recyclerView.setLayoutManager(layoutManager);
        LinearLayoutManager mlayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mlayoutManager);
        AlbumAdapter adapter = new AlbumAdapter(context, dropDowns);
//        recyclerView.addItemDecoration(new DividerItemDecoration(context,
//                LinearLayoutManager.VERTICAL, R.drawable.divider_white));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }


    public void setAlbumData(ArrayList<AlbumLists> albumlist){

        this.albumLists = albumlist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return albumLists.size();
    }


}