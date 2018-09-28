package com.securityapp.security.security.ui.event;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.securityapp.security.security.R;
import com.securityapp.security.security.adapter.EventRecyclerAdapter;
import com.securityapp.security.security.adapter.ViewHolderClickListener;
import com.securityapp.security.security.data.Event;
import com.securityapp.security.security.di.Injectable;
import com.securityapp.security.security.ui.BaseFragment;
import com.securityapp.security.security.ui.common.NavigationController;
import com.securityapp.security.security.ui.main.OnSaveEventVideoListener;
import com.securityapp.security.security.utils.AlertDialogUtils;
import com.securityapp.security.security.utils.SnackbarMessage;
import com.securityapp.security.security.utils.SnackbarUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tyler on 1/11/2018.
 */

public class EventFragment extends BaseFragment implements ViewHolderClickListener, ActionMode.Callback, Injectable{

    @BindString(R.string.events_title) String toolbarTitle;
    @BindString(R.string.editSelectionTitle) String selectionTitle;
    @BindString(R.string.deleteEventsPrompt) String deleteEventsPrompt;
    @BindString(R.string.confirmationMessage) String confirmationMsg;
    @BindString(R.string.eventDeleteSuccessMsg) String deleteSuccessMsg;
    @BindString(R.string.eventCaptureTitle) String eventCaptureTitle;
    @BindString(R.string.noEventsSelectedMsg) String noEventsSelectedMsg;

    @BindView(R.id.event_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.event_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    OnSaveEventVideoListener saveVideoListener;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    NavigationController navigationController;

    private EventViewModel mEventViewModel;
    private EventRecyclerAdapter mEventAdapter;
    private ActionMode mActionMode;

    public EventFragment(){
        //requires empty public constructor
    }

    public static EventFragment newInstance(){
        return new EventFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_event, container, false);
        ButterKnife.bind(this, view);
        setupActionBar(true, toolbarTitle);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mEventViewModel = ViewModelProviders.of(this, viewModelFactory).get(EventViewModel.class);

        setupSnackbar();

        setupListAdapter();

        setupRefreshLayout();

        mEventViewModel.reauthRequired().observe(this, aBoolean -> sessionExpired());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.list_operations_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_edit:
                mActionMode = getActivity().startActionMode(this);
                return true;
            case R.id.menu_refresh:
                swipeRefreshLayout.setRefreshing(true);
                mEventViewModel.loadEvents();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof OnSaveEventVideoListener){
            saveVideoListener = (OnSaveEventVideoListener) context;
        }else{
            throw new ClassCastException(context.toString() + "must implement OnSaveVideoSelected");
        }
    }

    /**
     * Sets up recycler view, adapter, and subscribes to ViewModel to observe
     * changes in Events
     */
    private void setupListAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        mEventAdapter = new EventRecyclerAdapter(this, getContext());
        recyclerView.setAdapter(mEventAdapter);

        //Observe ViewModel for Events
        mEventViewModel.getEvents().observe(this, events -> {
            mEventAdapter.updateData(events);
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
                //show animation as visual cue that list has been updated
                recyclerView.scheduleLayoutAnimation();
                recyclerView.invalidate();
            }
        });
    }

    /**
     * Observes for single events from EventViewModel for messages from network operations
     */
    private void setupSnackbar(){
        mEventViewModel.getSnackbarMessage().observe(this, new SnackbarMessage.SnackbarObserver() {
            @Override
            public void onNewMessage(int snackbarMessageResourceId) {
                SnackbarUtils.showSnackbar(getView(), getString(snackbarMessageResourceId));
            }
        });
    }

    /**
     * Configure refresh listener and notify ViewModel to reload events when activated
     */
    private void setupRefreshLayout(){
        swipeRefreshLayout.setOnRefreshListener(() -> mEventViewModel.loadEvents());
    }

    @Override
    public void onItemClick(int position) {
        if(mActionMode != null){
            toggleSelection(position);
        }else{
            navigationController.navigateToEventVideo(mEventAdapter.getSelectedItemId(position));
        }
    }

    @Override
    public boolean onItemLongClick(int position) {
        if(mActionMode == null){
            mActionMode = getActivity().startActionMode(this);
        }
        toggleSelection(position);
        return true;
    }

    //Show popup menu for row
    @Override
    public void onRowOptionsClicked(int position, View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.event_row_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch(menuItem.getItemId()){
                case R.id.menu_save_event_vid:
                    saveVideoListener.saveEventVideoSelected(mEventAdapter.getSelectedItemId(position));
                    break;
                case R.id.menu_event_details:
                    Toast.makeText(getContext(), "Event details", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    /**
     * Notifies the adapter of which row item was selected and updates the
     * ActionMode title to display the current number of selected items
     * @param position row clicked
     */
    private void toggleSelection(int position){
        mEventAdapter.toggleSelection(position);
        int count = mEventAdapter.getSelectedItemCount();

        if(count == 0){
            mActionMode.finish();
        }else{
            mActionMode.setTitle(String.format(selectionTitle, count));
            mActionMode.invalidate();
        }
    }

    /**
     * Determines if delete call is for a single or multiple Events and supplies all
     * required data to process request
     */
    private void setupDeleteCall(){
        List<Event> selectedEvents= mEventAdapter.getSelectedEvents();

        if(selectedEvents.size() == 1){
            mEventViewModel.deleteSingleEvent(selectedEvents.get(0));
        }else{
            mEventViewModel.deleteMultipleEvents(selectedEvents);
        }

        mActionMode.finish();
    }

    /** ACTION MODE CALLBACKS **/
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.action_menu, menu);
        swipeRefreshLayout.setEnabled(false);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.menu_delete:
                if(mEventAdapter.getSelectedItemCount() > 0){
                    AlertDialogUtils.showAlertDialog(this.getContext(),
                            String.format(deleteEventsPrompt, mEventAdapter.getSelectedItemCount()),
                            confirmationMsg,
                            (dialogInterface, i) -> {
                                if(i == DialogInterface.BUTTON_POSITIVE){
                                    setupDeleteCall();
                                }else{
                                    actionMode.finish();
                                }
                            });
                }else{
                    Toast.makeText(getContext(), noEventsSelectedMsg, Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mEventAdapter.clearSelection();
        mActionMode = null;
        swipeRefreshLayout.setEnabled(true);
    }
}