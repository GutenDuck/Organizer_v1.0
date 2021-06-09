package com.example.testorgaz;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class rashod_add extends AppCompatActivity
{
    //Переменные для данного листа
    private Button btn_save,btn_clean,btn_back;
    private EditText zagolov,osnova,vale;
    private int Prov=0;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int pr=1;
    private Animation anim_cl;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rashod_add);

        zagolov = (EditText)findViewById(R.id.TextZagolovokRaz);
        osnova = (EditText)findViewById(R.id.TextOsnovRaz);
        vale = (EditText)findViewById(R.id.editTextRaz);

        radioGroup=(RadioGroup)findViewById(R.id.RADGRUP);

        anim_cl = AnimationUtils.loadAnimation(this,R.anim.btn_click);

        //Если мы решили редачить файл, то прога получает значения заголовка ( названия файла) и основной текст и пихает их в эдитор

        addListenerOnButton(); //метод для запуска всех кнопок

    }

    public void checkCat(View v)
    {
        int radiobuttonid = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(radiobuttonid);

        switch (radiobuttonid)
        {
            case R.id.radioButtonToDoh:
                pr=1;
                break;

            case R.id.radioButtonToRaz:
                pr=0;
                break;
        }
    }


    //Чтобы нельзя было мискликом выйти из окна написания текста, в некоторых приложухаха это бесит....
    @Override
    public void onBackPressed(){}

    //Кнопки
    public void addListenerOnButton()
    {
        //прикрепляю точки к ID
        btn_save = (Button)findViewById(R.id.buttonSaveRaz);
        btn_clean = (Button)findViewById(R.id.buttonClearRaz);
        btn_back = (Button)findViewById(R.id.buttonBackRaz);

        //Кнопка Save
        btn_save.setOnClickListener(
                new View.OnClickListener()
                {
                    final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                    String path = internalStorageDir.toString()+"/Rsshod";   //перегоняем его в стринг
                    String pathToVale = internalStorageDir.toString()+"/RashodVal"; // берем стринг путь к записи колличесво вещей


                    @Override
                    public void onClick(View view)
                    {
                            btn_save.startAnimation(anim_cl);
                            Date data = new Date(); // data это объект класса Date, чтобы можно было брать дату

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss");

                            String myText = osnova.getText().toString(); //берем текст из основного текста и пихаем в переменую типа String
                            String myZog = zagolov.getText().toString()+"_"+ dateFormat.format(new Date()); //берем текст из заголовка и пихаем в переменую типа String
                            String cout = vale.getText().toString();
                            //и здесь(^^)


                            if (Prov==0) { //спец условие, оно выполняется елси мы создаем новый файл
                                if (myZog.equals("")) { myZog = "NewTxt " + data;}//спец условие, тип если наш "ДОРОГОЙ" пользователь не ввел ничего в заголовок, то получи писос пиздос( просто полная дата + на инглише, пусть вводит блять заголовок МРАЗЬ)

                                File text = new File(path, myZog+ ".txt");  //Делаем переменную типа File, задаем ему ссылку где он сидит, и его название
                                File valWrite = new File(pathToVale, myZog+".txt"); //Добавляем LUI и дату в имя файла, чтоб сохранять файлы с одинаковым именем
                                                                                // могут возникнуть баги (если в имене файла будет использоваться LUI), но они маловероятны (meow)
                                                                                                                            //уже нет)
                                try {

                                    text.createNewFile(); //создаем этот файл
                                    valWrite.createNewFile();
                                    FileOutputStream outputStream = new FileOutputStream(text); // открываем поток для записи
                                    FileOutputStream outputVal = new FileOutputStream(valWrite);
                                    outputStream.write(myText.getBytes()); //используя открытый поток записи пихаем в него наш текст
                                    if (pr==1)
                                    {
                                        if (cout.equals("")){outputVal.write(("0Р").getBytes());}
                                        else{outputVal.write((cout+"Р").getBytes());}
                                    }
                                    if (pr==0)
                                    {
                                        if (cout.equals("")){outputVal.write(("-0Р").getBytes());}
                                        else {outputVal.write(("-"+cout+"Р").getBytes());}
                                    }
                                    outputStream.close(); //закрываем поток запиши ибо так надо
                                    outputVal.close();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            //переход на инвинторизатор
                            Intent intent = new Intent(rashod_add.this,Rashod.class); //новое условие (Intent) (тип раббота с классами) связываем классы чтобы выполнять с ними всякую дичь
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // установка флагов
                            startActivity(intent);
                        }
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
                        osnova.setText("");
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
                        finish();
                    }
                }
        );
    }
}
