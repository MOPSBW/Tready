package com.securityapp.security.security.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

/**
 * View Holder helper to track and store selected items in a list for multi-choice selection
 */

public abstract class SelectionAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    private SparseBooleanArray mSelectedItems;

    protected SelectionAdapter(){
        mSelectedItems = new SparseBooleanArray();
    }

    boolean isSelected(int position){
        return getSelectedItems().contains(position);
    }

    /**
     * Puts or removes selection from SparseBooleanArray
     * @param position position in list
     */
    public void toggleSelection(int position){
        if(mSelectedItems.get(position,false)){
            mSelectedItems.delete(position);
        }else{
            mSelectedItems.put(position, true);
        }
        notifyDataSetChanged();
    }

    /**
     * Clears all selected items
     */
    public void clearSelection(){
        List<Integer> selection = getSelectedItems();
        mSelectedItems.clear();
        for(Integer i:selection){
            notifyItemChanged(i);
        }
    }

    /**
     * Gets count of all selected items
     * @return count of selected items in list
     */
    public int getSelectedItemCount(){
        return mSelectedItems.size();
    }

    /**
     * Gets all selected items
     * @return list of selected item positions
     */
    public List<Integer> getSelectedItems(){
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for(int i = 0; i < mSelectedItems.size(); i++){
            items.add(mSelectedItems.keyAt(i));
        }
        return items;
    }
}