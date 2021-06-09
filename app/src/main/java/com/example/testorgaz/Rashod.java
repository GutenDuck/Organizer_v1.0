package com.example.testorgaz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

public class Rashod extends AppCompatActivity
{
    //Переменная для "двойное нажатие для выхода из приложения"
    private static long back_pressed;
    //

    //Переменные для перехода по страницам
    AdapterForRazhod AdpRaz;
    private Button btn_inv, btn_block, btn_add, btn_delRez, btn_del;
    private ListView listforraz;
    private TextView textPodStat, textSum, textPlus, textMinus;
    private ArrayList<BlocknotItems> listRaz = new ArrayList<BlocknotItems>(); //Arraylist для заполнения элемнтов txt
    private int summ=0, summPol=0, sumAbs=0;
    private ProgressBar progressBar;
    private Animation animBut,animHug, anim_cl;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rashod);

        //Переменные на страничке
        listforraz = (ListView)findViewById(R.id.ListVisForRaz);
        textPodStat = (TextView)findViewById(R.id.textViewForRaz);
        textSum= (TextView)findViewById(R.id.textViewToSum);
        progressBar=(ProgressBar)findViewById(R.id.progressBar5);
        textPlus=(TextView)findViewById(R.id.textViewPlus);
        textMinus=(TextView)findViewById(R.id.textViewMinus);

        animBut = AnimationUtils.loadAnimation(this,R.anim.but_anim);
        animHug = AnimationUtils.loadAnimation(this,R.anim.btn_hug);
        anim_cl = AnimationUtils.loadAnimation(this,R.anim.btn_click);
        //

        //Методы
        addListenerOnButton();
        AdapBox(listRaz,"",0);
        btn_delRez.setOnClickListener(firstButtonListener);
        btn_del.setClickable(false);
        SummaBar("");
    }


    public void AdapBox(ArrayList f,String g,int pr)
    {
        getSourceToBox(g,pr);
        AdpRaz = new AdapterForRazhod(this,f);  //Делаем адаптер, который перегонит название файлов в String для их отображение на странице
        listforraz.setAdapter(AdpRaz);  //назначаю созданный адаптер для моей ListView
    }

    public  ArrayList<BlocknotItems> getSourceToBox(String Name,int prov){

        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
        String path = internalStorageDir.toString()+"/Rsshod"+Name;   //перегоняем его в стринг + папка для файлов инвинторизатора
        String pathToVale = internalStorageDir.toString()+"/RashodVal"; // берем стринг путь к записи колличесво вещей
        //meow
        //прога ищет файлы с окончанием .txt и пихает их в ArrayList в итоге т к ArrayList связан с ListView они отображаются
        try {
            File folder = new File(path);
            String[] files = folder.list(new FilenameFilter(){
                @Override public boolean accept(File folder, String name) {
                    return name.endsWith(".txt");
                }
            });
            for ( String fileName : files )
            {
                String[] Nam = fileName.split(".txt");// *ВАЖНО* если пользователь в имене будет использовать LUI, то имя отоброзиться не правильно (до LIU)
                String Zag = Nam[0];                         //     это надо, чтоб добавить дату в имя файла дату, чтоб сохранять файлы с одинаковым именем
                                                             //                                      *ПОФИКСИЛИ*

                String contents = readUsingBufferedReader( path+"/"+fileName);
                String Cout = readUsingBufferedReader(pathToVale+"/"+fileName);



                if(prov==1){listRaz.add(new BlocknotItems(Zag,contents,Cout,null,false,1, "Ценность"));} //чтобы заполнить лист элементами с чекбоксами
                if(prov==0){listRaz.add(new BlocknotItems(Zag,contents,Cout,null,false,0,"Ценность"));}//чтобы заполнить лист элементами без чекбоксов
            }
        }catch (NullPointerException e){
            System.out.println("Ошибка" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listRaz;
    }

    //считывание суммы
    public void SummaBar(String Name)
    {
        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
        String pathToVale = internalStorageDir.toString()+"/RashodVal"+Name;

        try {
            File folder = new File(pathToVale);
            String[] files = folder.list(new FilenameFilter(){
                @Override public boolean accept(File folder, String name) {
                    return name.endsWith(".txt");
                }
            });
            for ( String fileName : files )
            {
                //
                String Cout = readUsingBufferedReader(pathToVale+"/"+fileName);
                String [] VL = Cout.split("Р");
                String Val = VL[0];

                Integer vale = Integer.valueOf(Val); //не трогай пока работает

                summ=summ+ vale;

                String SYM = Integer.toString(summ)+"Р";

                textSum.setText(SYM);
                //

                //
                if (vale<0)
                {
                    sumAbs=sumAbs+(vale*(-1));
                }

                else
                {
                    sumAbs=sumAbs+vale;
                    summPol=summPol+vale;
                }
                //
            }

            //
            if (sumAbs!=0) {
                thread.start();
            }
            //


        }catch (NullPointerException e){
            System.out.println("Ошибка" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //метод для чтения текста из файла
    private static String readUsingBufferedReader(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader(fileName));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator"); //в сскобках спец условие, ЗАБЕЙТЕ

        while( ( line = reader.readLine() ) != null ) //в цикле мы делаем построчное считываение и пихаем в stringBuilder каждую строчку и спец условие ЗАБЕЙТЕ
        {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        if (line==null) //косяк в том что если в файле мы ничего не писали, то цикл что выше ломает прогу, а нам это не надо, тогда мы тупо пихаем в наш файл пустое поле вот и се
        {
            stringBuilder.append("");
            stringBuilder.append(ls);
        }

        stringBuilder.deleteCharAt(stringBuilder.length()-1);
        return stringBuilder.toString();
    }


    //Метод для выхода из приложения по двойному касанию.
    @Override
    public void onBackPressed()
    {
        if (back_pressed + 1000 > System.currentTimeMillis())
        {
            //эмулируем нажатие на HOME, сворачивая приложение
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else
            {
            Toast.makeText(getBaseContext(), "Нажмите'Назад'еще раз для выхода из приложения", Toast.LENGTH_SHORT).show();
            }
        back_pressed = System.currentTimeMillis();
    }

    public void showResult()
    {
        String Name;
        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
        String path = internalStorageDir.toString()+"/Rsshod";   //перегоняем его в стринг + папка для файлов инвинторизатора
        String pathToVale = internalStorageDir.toString()+"/RashodVal";

        for (BlocknotItems p : AdpRaz.getBox())
        {
            if (p.box) {
                Name = p.name+".txt";

                File file = new File(path,Name);
                file.delete();
                File fileval = new File(pathToVale,Name);
                fileval.delete();
            }
        }
        listRaz.clear();
        AdapBox(listRaz, "", 1);
    }

    View.OnClickListener firstButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // меняем обработчик нажатия кнопки на второй

            listRaz.clear();
            AdapBox(listRaz,"",1);

            int resId = R.drawable.musorka_udalenie;

            btn_delRez.startAnimation(anim_cl);
            btn_delRez.setBackgroundResource(resId);

            btn_del.startAnimation(animBut);
            btn_add.startAnimation(animHug);

            btn_del.setClickable(true);
            btn_add.setClickable(false);

            textPlus.setVisibility(View.INVISIBLE);
            textMinus.setVisibility(View.INVISIBLE);

            Toast.makeText(getBaseContext(), "Включен режим удаления", Toast.LENGTH_SHORT).show();
            btn_delRez.setOnClickListener(secondButtonListener);
        }
    };

    View.OnClickListener secondButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // возвращаем первый обработчик нажатия кнопки

            listRaz.clear();
            AdapBox(listRaz,"",0);

            int resId = R.drawable.musorka;

            btn_delRez.startAnimation(anim_cl);
            btn_delRez.setBackgroundResource(resId);

            btn_del.startAnimation(animHug);
            btn_add.startAnimation(animBut);

            btn_del.setClickable(false);
            btn_add.setClickable(true);

            textPlus.setVisibility(View.VISIBLE);
            textMinus.setVisibility(View.VISIBLE);

            Toast.makeText(getBaseContext(), "Режим удаления выключен", Toast.LENGTH_SHORT).show();
            btn_delRez.setOnClickListener(firstButtonListener);
        }
    };

    //Кнопки
    public void addListenerOnButton()
    {
        //Кнопки перехода
        btn_inv = (Button)findViewById(R.id.buttonToInvin);
        btn_block = (Button)findViewById(R.id.buttonToBlocknod);
        //

        //Кнопки расходов
        btn_add = (Button)findViewById(R.id.buttonToAddRaz);
        btn_del = (Button)findViewById(R.id.buttonToDelRaz);
        btn_delRez = (Button)findViewById(R.id.buttonToDelRezRaz);
        //Здесь тоже был кот

        btn_del.setVisibility(View.INVISIBLE);
        //Методы кнопок страницы

        btn_add.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        btn_add.startAnimation(anim_cl);
                        Intent intent = new Intent(Rashod.this,rashod_add.class); //новое условие (Intent) (тип раббота с классами) связываем классы чтобы выполнять с ними всякую дичь
                        startActivity(intent);    // стартуем новую страницу ( так называемое активити) в соответвии с заданным намерением
                    }
                }
        );

        btn_del.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        btn_del.startAnimation(anim_cl);
                        AlertDialog.Builder okno= new AlertDialog.Builder(Rashod.this);  //создаем новую переменную клаасса AlertDialog ( тупо появление окошка)  .Builder озхначает что мы его создаем
                        okno.setTitle("Удалить статью(и) расхода/дохода?"); // значение в титульнике
                        okno.setPositiveButton("Да", new DialogInterface.OnClickListener()  //создаем кнопульку, которая означает положительное значение (на самом деле от это заваисит ток справа или слева оно...)
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)  //при нажатии на кнопулю справа (положительную) делаем.....
                            {
                                showResult();//вызываем метод который удаляет файлы с выбраным ччекбоксами в нужной папке
                                listRaz.clear();
                                AdapBox(listRaz,"",1);
                                progressBar.setProgress(0);
                                summ=0;
                                sumAbs=0;
                                summPol=0;
                                textSum.setText("");
                                SummaBar("");
                            }
                        });
                        okno.setNegativeButton("Нет", new DialogInterface.OnClickListener() {   //при нажатии на левую кнопку (отрицалетльную) делаем ничего, просто чтобы окошко пропало
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { }
                        });
                        okno.show(); //делаем чтобы наше окошко стало видимым (это тупо это так рабботает тип если хочешь чтобы чет поивилось пиши .show ....  -___-  )
                    }

                }
        );

        //Методы чтобы кнопки открывали новые странипцы
        //Переход на инвинторизатор из расходов
        btn_inv.setOnClickListener(
                new  View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Rashod.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // установка флагов
                        startActivity(intent);
                    }
                }
        );



        //Переход на блокнод из расходов
        btn_block.setOnClickListener(
                new  View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Rashod.this,BlockNot.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // установка флагов
                        startActivity(intent);
                    }
                }
        );
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            int progress=(summPol*100)/(sumAbs);
            try
            {
                Thread.sleep(1000);
                progressBar.setProgress(progress);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    });
}