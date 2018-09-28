package com.securityapp.security.security.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.securityapp.security.security.R;
import com.securityapp.security.security.data.Event;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * View Holder for an Event row layout
 */

public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private ViewHolderClickListener mListener;
    private static final String sWebServiceUrl = "http://astralqueen.bw.edu/skunkworks/service.php?eventId=%s&thumbnail=true";
    private Context context;

    @BindView(R.id.img_event_thumbnail) ImageView eventThumbnail;
    @BindView(R.id.txt_event_device_name) TextView deviceName;
    @BindView(R.id.txt_event_date) TextView eventDate;
    @BindView(R.id.txt_event_time) TextView eventTime;
    @BindView(R.id.txt_event_duration) TextView eventDuration;
    @BindView(R.id.img_btn_event_card) ImageButton eventOptions;
    @BindView(R.id.event_card_overlay) View overlay;

    public EventViewHolder(View itemView, ViewHolderClickListener clickListener, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        mListener = clickListener;
        this.context = context;

        //Register click listeners
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    /**
     * Binds Event data to row layout
     * @param event Event for current row
     */
    public void bindEvent(Event event){
        Picasso.with(context).load(String.format(sWebServiceUrl, event.eventId))
                .error(R.drawable.ic_error_outline_red)
                .into(eventThumbnail);

        deviceName.setText(event.nameOfDevice);

        eventDate.setText(event.getDate());

        eventTime.setText(event.getStartTime());

        eventDuration.setText(String.format("%s seconds",event.getDuration()));

        eventOptions.setOnClickListener(view -> mListener.onRowOptionsClicked(getAdapterPosition(), eventOptions));
    }

    @Override
    public void onClick(View view) {
        mListener.onItemClick(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View view) {
        return mListener.onItemLongClick(getAdapterPosition());
    }
}