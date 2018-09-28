package com.securityapp.security.security.adapter;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.securityapp.security.security.R;
import com.securityapp.security.security.data.Event;
import com.securityapp.security.security.ui.common.EventDiffUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * RecyclerView adapter for Events
 */

public class EventRecyclerAdapter extends SelectionAdapter<EventViewHolder>{

    private List<Event> mEvents = new ArrayList<>(0);
    private final ViewHolderClickListener mListener;
    private Context context;

    @Inject
    public EventRecyclerAdapter(ViewHolderClickListener listener, Context context){
        mListener = listener;
        this.context = context;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view,mListener, context);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.bindEvent(mEvents.get(position));
        holder.overlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return mEvents != null ? mEvents.size() : 0;
    }

    /**
     * Updates view with new list using DiffUtil
     * @param events new list of Events
     */
    @MainThread
    public void updateData(List<Event> events){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EventDiffUtil(this.mEvents, events), false);
        mEvents.clear();
        mEvents.addAll(events);
        diffResult.dispatchUpdatesTo(this);
    }

    public int getSelectedItemId(int position){
        return mEvents.get(position).eventId;
    }

    public List<Event> getSelectedEvents(){
        List<Event> list = new ArrayList<>();
        for(int pos : getSelectedItems()){
            list.add(mEvents.get(pos));
        }
        return list;
    }
}
