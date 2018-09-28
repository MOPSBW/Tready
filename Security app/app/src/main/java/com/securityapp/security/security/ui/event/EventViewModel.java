package com.securityapp.security.security.ui.event;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.securityapp.security.security.R;
import com.securityapp.security.security.data.Event;
import com.securityapp.security.security.repository.EventRepository;
import com.securityapp.security.security.utils.SingleLiveEvent;
import com.securityapp.security.security.utils.SnackbarMessage;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tyler on 1/11/2018.
 */

public class EventViewModel extends ViewModel {

    private EventRepository mEventRepository;

    private final MediatorLiveData<List<Event>> mEvents;

    private final SnackbarMessage mSnackbarMessage = new SnackbarMessage();

    private final SingleLiveEvent<Boolean> mReauthRequired = new SingleLiveEvent<>();

    @Inject
    public EventViewModel(EventRepository eventRepository){
        this.mEventRepository = eventRepository;
        mEvents = new MediatorLiveData<>();

        loadEvents();
    }

    public LiveData<List<Event>> getEvents(){
        return mEvents;
    }

    public SnackbarMessage getSnackbarMessage(){
        return mSnackbarMessage;
    }

    public SingleLiveEvent<Boolean> reauthRequired(){
        return mReauthRequired;
    }

    public void loadEvents(){
        mEvents.addSource(
                mEventRepository.getEvents(), apiResponse -> {
                    switch(apiResponse.status){
                        case SUCCESS:
                            mEvents.setValue(apiResponse.data.getResponseData());
                            break;
                        case SESSION_EXPIRED:
                            mReauthRequired.setValue(true);
                            break;
                        case ERROR:
                            mEvents.setValue(null);
                            break;
                    }
                }
        );
    }

    public void deleteMultipleEvents(List<Event> events){
        int[] eventIds = new int[events.size()];

        for(int i = 0; i < events.size(); i++){
            eventIds[i] = events.get(i).eventId;
        }

        mEvents.addSource(mEventRepository.deleteMultipleEvents(eventIds), deleteEventResponse -> {
            switch(deleteEventResponse.status){
                case SUCCESS:
                    List<Event> newList = mEvents.getValue();
                    newList.removeAll(events);
                    mEvents.setValue(newList);
                    break;
                case SESSION_EXPIRED:
                    mReauthRequired.setValue(true);
                    break;
                case ERROR:
                    mSnackbarMessage.setValue(R.string.networkErrorMsg);
                    break;
            }
        });
    }

    public void deleteSingleEvent(Event event){
        mEvents.addSource(mEventRepository.deleteSingleEvent(event.eventId), deleteEventResponseResource -> {
            switch(deleteEventResponseResource.status){
                case SUCCESS:
                    List<Event> newList = mEvents.getValue();
                    newList.remove(event);
                    mEvents.setValue(newList);
                    break;
                case SESSION_EXPIRED:
                    mReauthRequired.setValue(true);
                    break;
                case ERROR:
                    mSnackbarMessage.setValue(R.string.networkErrorMsg);
                    break;
            }
        });
    }
}