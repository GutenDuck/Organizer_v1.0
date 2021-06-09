package com.example.testorgaz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterForRazhod extends BaseAdapter
{
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<BlocknotItems> objects;
    CheckBox cbBuy;
    TextView Tx;

    AdapterForRazhod(Context context, ArrayList<BlocknotItems> product) {
        ctx = context;
        objects = product;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount()
    {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position)
    {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position)
    {
        return position;
    }
    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null)
        {
            view = lInflater.inflate(R.layout.failsrashod, parent, false);
        }

        BlocknotItems p = getItems(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка

        ((TextView) view.findViewById(R.id.TextNameRaz)).setText(p.name);
        ((TextView) view.findViewById(R.id.TextOsnovRaz)).setText(p.Text + "");
        ((TextView) view.findViewById(R.id.TextCenRaz)).setText(p.tx + "");
        ((TextView) view.findViewById(R.id.TextValRaz)).setText(p.vali+"");

        cbBuy = (CheckBox) view.findViewById(R.id.cbBoxRaz);
        // присваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        // пишем позицию
        cbBuy.setTag(position);
        // заполняем данными из товаров: в корзине или нет
        cbBuy.setChecked(p.box);

        if (p.boxVis==1){cbBuy.setVisibility(View.VISIBLE);}
        else {cbBuy.setVisibility(View.INVISIBLE);}

        return view;
    }

    // товар по позиции
    BlocknotItems getItems(int position)
    {
        return ((BlocknotItems) getItem(position));
    }
    // содержимое корзины
    ArrayList<BlocknotItems> getBox() {
        ArrayList<BlocknotItems> box = new ArrayList<BlocknotItems>();
        for (BlocknotItems p : objects) {
            // если в корзине
            if (p.box)
                box.add(p);
        }
        return box;
    }
    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener()
    {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
        {
            // меняем данные товара (в корзине или нет)
            getItems((Integer) buttonView.getTag()).box = isChecked;
        }
    };
}
