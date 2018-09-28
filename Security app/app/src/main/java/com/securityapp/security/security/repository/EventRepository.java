package com.securityapp.security.security.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.securityapp.security.security.service.network.Resource;
import com.securityapp.security.security.service.network.Webservice;
import com.securityapp.security.security.service.request.DeleteEventRequest;
import com.securityapp.security.security.service.request.DeleteEventsRequest;
import com.securityapp.security.security.service.request.GetEvents;
import com.securityapp.security.security.service.response.DeleteEventResponse;
import com.securityapp.security.security.service.response.DeleteEventsResponse;
import com.securityapp.security.security.service.response.EventResponse;
import com.securityapp.security.security.service.response.LastEventResponse;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository for processing network calls and handling errors
 */
@Singleton
public class EventRepository {

    private Webservice mWebservice;

    @Inject
    public EventRepository(Webservice webservice) {
        mWebservice = webservice;
    }

    /**
     * Sends network request to get all events from web server
     * @return Resource
     */
    public LiveData<Resource<EventResponse>> getEvents(){
        MediatorLiveData<Resource<EventResponse>> eventResponse = new MediatorLiveData<>();
        GetEvents request = new GetEvents();

        eventResponse.addSource(mWebservice.getEvents(request.getRequestParams()), eventResponseApiResponse -> {
            if(eventResponseApiResponse.isSuccessful()){
                eventResponse.setValue(Resource.success(eventResponseApiResponse.getData()));
            }else{
                eventResponse.setValue(Resource.error(eventResponseApiResponse.errorMessage, null));
            }
        });
        return eventResponse;
    }

    /**
     * Send network request to delete multiple events
     * @param eventIds array of Event ids to delete
     * @return DeleteEventsResponse
     */
    public LiveData<Resource<DeleteEventsResponse>> deleteMultipleEvents(int[] eventIds) {
        final MediatorLiveData<Resource<DeleteEventsResponse>> deleteResponse = new MediatorLiveData<>();
        DeleteEventsRequest request = new DeleteEventsRequest(eventIds);

        deleteResponse.addSource(mWebservice.deleteEvents(request.getRequestParams()), apiResponse -> {
            if(apiResponse.isSuccessful()){
                deleteResponse.setValue(Resource.success(apiResponse.getData()));
            }else{
                //TODO: Need to implement partial delete once I can test with more events
                deleteResponse.setValue(Resource.error(apiResponse.errorMessage, null));
            }
        });
        return deleteResponse;
    }

    /**
     * Sends network request to delete a single event
     * @param id event id to delete
     * @return DeleteEventResponse
     */
    public LiveData<Resource<DeleteEventResponse>> deleteSingleEvent(int id){
        MediatorLiveData<Resource<DeleteEventResponse>> deleteResponse = new MediatorLiveData<>();
        DeleteEventRequest request = new DeleteEventRequest(id);

        deleteResponse.addSource(mWebservice.deleteEvent(request.getRequestParams()), apiResponse ->{
            if(apiResponse.isSuccessful()){
                deleteResponse.setValue(Resource.success(apiResponse.getData()));
            }else{
                deleteResponse.setValue(Resource.error(apiResponse.errorMessage, null));
            }
        });
        return deleteResponse;
    }

    public LiveData<Resource<LastEventResponse>> getLastEventId(){
        MediatorLiveData<Resource<LastEventResponse>> lastEventResponse = new MediatorLiveData<>();

        lastEventResponse.addSource(mWebservice.getLastEvent(), apiResponse ->{
            if(apiResponse.isSuccessful()){
                lastEventResponse.setValue(Resource.success(apiResponse.getData()));
            }else{
                lastEventResponse.setValue(Resource.error(apiResponse.errorMessage, null));
            }
        });
        return lastEventResponse;
    }
}