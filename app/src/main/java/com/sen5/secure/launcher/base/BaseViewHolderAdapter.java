package com.sen5.secure.launcher.base;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoudao on 2017/5/26.
 */

public abstract class BaseViewHolderAdapter<T> extends RecyclerView.Adapter {

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    protected List<T> list;



    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addList(ArrayList<T> list) {
        if (this.list == null) {
            this.list = list;
        } else {
            this.list.addAll(list);
        }

        notifyDataSetChanged();

    }

    public void add(T t) {
        if (list == null) {
            list = new ArrayList<>();
            list.add(t);
        } else {
            list.add(t);
        }

        notifyDataSetChanged();
    }





}
