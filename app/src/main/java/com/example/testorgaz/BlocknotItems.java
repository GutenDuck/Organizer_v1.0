package com.example.testorgaz;

import android.graphics.drawable.Drawable;

public class BlocknotItems
{
    String name;
    String Text;
    String vali;
    Drawable image;
    boolean box;
    int boxVis;
    String tx;
    BlocknotItems(String _Zag, String _Text,String _Vali, Drawable _image,boolean _box,int _boxVis,String _tx)
    {
        name = _Zag;
        Text = _Text;
        vali = _Vali;
        image = _image;
        box = _box;
        boxVis = _boxVis;
        tx=_tx;
    }
}
