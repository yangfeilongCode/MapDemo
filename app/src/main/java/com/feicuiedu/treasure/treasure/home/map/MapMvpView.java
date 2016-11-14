package com.feicuiedu.treasure.treasure.home.map;

import com.feicuiedu.treasure.treasure.Treasure;

import java.util.List;


public interface MapMvpView {

    void showMessage(String msg);

    void setData(List<Treasure> list);
}