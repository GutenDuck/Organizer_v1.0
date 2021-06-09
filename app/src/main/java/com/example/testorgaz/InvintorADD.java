package com.example.testorgaz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class InvintorADD extends AppCompatActivity
{
    //Переменные для данного листа
    private Button btn_save,btn_clean,btn_back, btn_delImg;
    private EditText zagolov,osnova,vale;
    private ImageView img;
    private int Prov=0;
    static final int GALLERY_REQUEST = 1;
    private Animation anim_cl;
    private Animation animBut,animHug;
    private Bitmap bitmapi=null;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invintor_add);

        zagolov = (EditText)findViewById(R.id.TextZagolovok2);
        osnova = (EditText)findViewById(R.id.TextOsnov2);
        vale = (EditText)findViewById(R.id.editText);
        img = (ImageView)findViewById(R.id.imageView);

        animBut =AnimationUtils.loadAnimation(this,R.anim.but_anim);
        animHug = AnimationUtils.loadAnimation(this,R.anim.btn_hug);
        anim_cl = AnimationUtils.loadAnimation(this,R.anim.btn_click);

        final File internalStorageDir = getFilesDir();
        String pathImg = internalStorageDir.toString()+"/ImgDir";

        addListenerOnButton(); //метод для запуска всех кнопок

        //Если мы решили редачить файл, то прога получает значения заголовка ( названия файла) и основной текст и пихает их в эдитор
        Bundle argum = getIntent().getExtras(); //получаем значения что отправили
        if (argum != null) //если он не равен ничему, то выполняем... ( нужно тупо чтобы прога не крашнула, тип прога думает, что она в безопасности)
            {
                String name = argum.getString("ZagName"); //получаем заголовок
                String text = argum.getString("OsnText"); //получаем текст
                String val = argum.getString("ValueI"); //получаем количество

                File imgF = new File(pathImg,name+".jpg");
                Bitmap bm = BitmapFactory.decodeFile(imgF.getAbsolutePath());
                Drawable drawable = new BitmapDrawable(getResources(),bm);

                zagolov.setText(name); //устанавливаем заголово
                osnova.setText(text); //устанавливваем текст
                vale.setText(val); //устанавливаем количесво
                img.setImageDrawable(drawable);
                bitmapi = bm;
                if (bm!=null){btn_delImg.startAnimation(animBut);
                    btn_delImg.setClickable(true);}


                if (name!=null){ Prov=1;}
            }



        final Button btn_img = findViewById(R.id.buttonToImg);
        btn_img.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btn_img.startAnimation(anim_cl);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                btn_delImg.startAnimation(animBut);
                btn_delImg.setClickable(true);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        switch(requestCode)
        {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK)
                {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(bitmap);
                    bitmapi=bitmap;
                }
        }
    }

    //Чтобы нельзя было мискликом выйти из окна написания текста, в некоторых приложухаха это бесит....
    @Override
    public void onBackPressed(){}

    //Кнопки
    public void addListenerOnButton()
    {
        //прикрепляю точки к ID
        btn_save = (Button)findViewById(R.id.buttonSave2);
        btn_clean = (Button)findViewById(R.id.buttonClear2);
        btn_back = (Button)findViewById(R.id.buttonBack2);
        btn_delImg= (Button)findViewById(R.id.buttonToImgDel);

        btn_delImg.setVisibility(View.INVISIBLE);
        btn_delImg.setClickable(false);

        //Кнопка Save
        btn_save.setOnClickListener(
                new View.OnClickListener()
                {
                    final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                    String path = internalStorageDir.toString()+"/DirTxtInv";   //перегоняем его в стринг
                    String pathToVale = internalStorageDir.toString()+"/IntorItemVal"; // берем стринг путь к записи колличесво вещей
                    String pathImg = internalStorageDir.toString()+"/ImgDir";

                    @Override
                    public void onClick(View view)
                    {
                        btn_save.startAnimation(anim_cl);
                        Bundle argum = getIntent().getExtras(); //получаем значения что отправили
                        if (argum != null) //если он не равен ничему, то выполняем... ( нужно тупо чтобы прога не крашнула, тип прога думает, что она в безопасности)
                        {
                            String puti = argum.getString("TakePath"); //получаем наш путь для записи в определенную папку
                            String pathDel= argum.getString("PathDel"); //получаем значения для перезаписи в определенную папку

                            String myText = osnova.getText().toString(); //берем текст из основного текста и пихаем в переменую типа String
                            String myZog = zagolov.getText().toString(); //берем текст из заголовка и пихаем в переменую типа String
                            String cout = vale.getText().toString();//и здесь)

                            if (bitmapi!=null)
                            {
                                img.setDrawingCacheEnabled(true);
                                img.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                                img.layout(0, 0,
                                        img.getMeasuredWidth(), img.getMeasuredHeight());
                                img.buildDrawingCache(true);
                                bitmapi = Bitmap.createBitmap(img.getDrawingCache());
                                img.setDrawingCacheEnabled(false);
                            }

                            Date data = new Date(); // data это объект класса Date, чтобы можно было брать дату

                            if (Prov==0) { //спец условие, оно выполняется елси мы создаем новый файл
                                if (myZog.equals("")) { myZog = "NewTxt " + data;}//спец условие, тип если наш "ДОРОГОЙ" пользователь не ввел ничего в заголовок, то получи писос пиздос( просто полная дата + на инглише, пусть вводит блять заголовок МРАЗЬ)

                                File text = new File(path + puti, myZog + ".txt");  //Делаем переменную типа File, задаем ему ссылку где он сидит, и его название
                                File valWrite = new File(pathToVale, myZog+".txt");
                                File ImgV = new File(pathImg,myZog+".jpg");

                                try {
                                    if (bitmapi!=null)
                                    {
                                        text.createNewFile(); //создаем этот файл
                                        valWrite.createNewFile();
                                        FileOutputStream outputStream = new FileOutputStream(text); // открываем поток для записи
                                        FileOutputStream outputVal = new FileOutputStream(valWrite);
                                        FileOutputStream outputImg = new FileOutputStream(ImgV);
                                        bitmapi.compress(Bitmap.CompressFormat.JPEG, 75, outputImg);
                                        outputStream.write(myText.getBytes()); //используя открытый поток записи пихаем в него наш текст
                                        if (cout.equals("")) {
                                            outputVal.write(("0").getBytes());
                                        } else {
                                            outputVal.write((cout).getBytes());
                                        }
                                        outputStream.close(); //закрываем поток запиши ибо так надо
                                        outputVal.close();
                                        outputImg.flush();
                                        outputImg.close();
                                    }
                                    else
                                        {
                                            text.createNewFile(); //создаем этот файл
                                            valWrite.createNewFile();
                                            FileOutputStream outputStream = new FileOutputStream(text); // открываем поток для записи
                                            FileOutputStream outputVal = new FileOutputStream(valWrite);
                                            outputStream.write(myText.getBytes()); //используя открытый поток записи пихаем в него наш текст
                                            if (cout.equals("")) {
                                                outputVal.write(("0").getBytes());
                                            } else {
                                                outputVal.write((cout).getBytes());
                                            }
                                            outputStream.close(); //закрываем поток запиши ибо так надо
                                            outputVal.close();
                                        }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else { //если спец условие имеют иное значение мы перезаписываем файл
                                if (myZog.equals("")) { myZog = "NewTxt " + data;} //спец условие, тип если наш "ДОРОГОЙ" пользователь не ввел ничего в заголовок, то получи писос пиздос( просто полная дата + на инглише, пусть вводит блять заголовок МРАЗЬ)

                                File file = new File(path+pathDel,myZog+".txt"); //создаем переменную типа File, в которой хранится ссылка на файл который перезапишем
                                File valWrite = new File(pathToVale, myZog+".txt");
                                File ImgV = new File(pathImg,myZog+".jpg");

                                //и да сразу напишу, перезаписать txt файл сложно, поэтому легче его удалить и создать с тем же названием
                                try {
                                    if (bitmapi!=null)
                                    {
                                        file.delete(); //удаляем его
                                        file.createNewFile(); //создаем этот файл
                                        FileOutputStream outputStream = new FileOutputStream(file); // открываем поток для записи
                                        outputStream.write(myText.getBytes()); //используя открытый поток записи пихаем в него наш текст
                                        outputStream.close(); //закрываем поток запиши ибо так надо

                                        valWrite.delete();
                                        valWrite.createNewFile();
                                        FileOutputStream outputVal = new FileOutputStream(valWrite);
                                        if (cout.equals("")){outputVal.write(("0").getBytes());}
                                        else{outputVal.write((cout).getBytes());}
                                        outputVal.close();

                                        ImgV.delete();
                                        FileOutputStream outputImg = new FileOutputStream(ImgV);
                                        bitmapi.compress(Bitmap.CompressFormat.JPEG, 75, outputImg);
                                        outputImg.flush();
                                        outputImg.close();
                                    }
                                    else
                                        {
                                            file.delete(); //удаляем его
                                            file.createNewFile(); //создаем этот файл
                                            FileOutputStream outputStream = new FileOutputStream(file); // открываем поток для записи
                                            outputStream.write(myText.getBytes()); //используя открытый поток записи пихаем в него наш текст
                                            outputStream.close(); //закрываем поток запиши ибо так надо

                                            valWrite.delete();
                                            valWrite.createNewFile();
                                            FileOutputStream outputVal = new FileOutputStream(valWrite);
                                            if (cout.equals("")){outputVal.write(("0").getBytes());}
                                            else{outputVal.write((cout).getBytes());}
                                            outputVal.close();
                                        }


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }

                            //переход на инвинторизатор
                            finish();
                        }
                    }
                }
        );
        btn_delImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final File internalStorageDir = getFilesDir();
                String pathImg = internalStorageDir.toString()+"/ImgDir";
                String myZog = zagolov.getText().toString();
                String imgName = myZog+".jpg";

                img.setImageDrawable(null);
                bitmapi=null;
                btn_delImg.startAnimation(animHug);
                btn_delImg.setClickable(false);

                File fileImg = new File(pathImg,imgName);
                fileImg.delete();
            }
        });

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
