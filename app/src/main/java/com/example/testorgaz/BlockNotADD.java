package com.example.testorgaz;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class BlockNotADD extends AppCompatActivity
{
    //Переменные для данного листа
    private Button btn_save;
    private Button btn_clean;
    private Button btn_back;
    private EditText zagolov;
    private EditText osnova;
    private  int Prov=0;
    private Animation anim_cl;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_not_add);

        // Связываю текстовые поля для их рабботы
        zagolov = (EditText)findViewById(R.id.TextZagolovok);
        osnova = (EditText)findViewById(R.id.TextOsnov);

        anim_cl = AnimationUtils.loadAnimation(this,R.anim.btn_click);

        Bundle argum = getIntent().getExtras(); //получаем значения что отправили
        if (argum != null) //если он не равен ничему, то выполняем... ( нужно тупо чтобы прога не крашнула, тип прога думает, что она в безопасности)
        {
            String name = argum.getString("ZagName"); //получаем заголовок
            String text = argum.getString("OsnText"); //получаем текст


            zagolov.setText(name); //устанавливаем заголовок
            osnova.setText(text); //устанавливваем текст
            if (name!=null){ Prov=1;}
        }

        //метод Работа кнопок
        addListenerOnButton();
    }

    //Чтобы нельзя было мискликом выйти из окна написания текста, в некоторых приложухаха это бесит....
    @Override
    public void onBackPressed(){}

    //Кнопки
    public void addListenerOnButton()
    {
        //прикрепляю точки к ID
        btn_save = (Button)findViewById(R.id.buttonSave);
        btn_clean = (Button)findViewById(R.id.buttonClear);
        btn_back = (Button)findViewById(R.id.buttonBack);

        //Кнопка Save
        btn_save.setOnClickListener(
                new View.OnClickListener()
                {
                    final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                    String path = internalStorageDir.toString()+"/DirTxtBlock";   //перегоняем его в стринг

                    @Override
                    public void onClick(View view)
                    {
                        btn_save.startAnimation(anim_cl);

                        String  myText = osnova.getText().toString();    //Основной текст
                        String myZog = zagolov.getText().toString()+".txt"; //Название идет из заголовка текста
                        Date data = new Date();

                        if (Prov==0) {
                            if (myZog.equals(".txt")) {myZog = "NewTxt " + data + ".txt";}

                            File text = new File(path, myZog);  //Делаем переменную типа File, задаем ему ссылку где он сидит, и его название
                            try {
                                text.createNewFile(); //создаем этот файл
                                FileOutputStream outputStream = new FileOutputStream(text); // открываем поток для записи
                                outputStream.write(myText.getBytes()); //используя открытый поток записи пихаем в него наш текст
                                outputStream.close(); //закрываем поток запиши ибо так надо
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            if (myZog.equals(".txt")) {myZog = "NewTxt " + data + ".txt";}

                            File file = new File(path,myZog); //создаем переменную типа File, в которой хранится ссылка на файл который перезапишем

                            //и да сразу напишу, перезаписать txt файл сложно, поэтому легче его удалить и создать с тем же названием
                            try {
                                file.delete(); //удаляем его
                                file.createNewFile(); //создаем этот файл
                                FileOutputStream outputStream = new FileOutputStream(file); // открываем поток для записи
                                outputStream.write(myText.getBytes()); //используя открытый поток записи пихаем в него наш текст
                                outputStream.close(); //закрываем поток запиши ибо так надо
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent(BlockNotADD.this,BlockNot.class);
                        startActivity(intent);
                    }
                }
        );

        //Кнопка очистить
        btn_clean.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btn_clean.startAnimation(anim_cl);
                        osnova.setText("");   //заменяет весь текст в окне osnova (наш основной текст) на пустоту (тип стирает все)
                    }
                }
        );

        //Кнопка назад
        btn_back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        btn_back.startAnimation(anim_cl);
                       finish();  // прикол в том что для  кнопки назад есть спец метод onBackPressed, но мы уже сделали так, чтобы он ничего не делал, поэтому используем команду finish ( по сути тоже самое что и назад, ЭТО НЕ ЗАКРЫТИЕ ОКНА! а кнопка назад)
                    }
                }
        );
    }
}
