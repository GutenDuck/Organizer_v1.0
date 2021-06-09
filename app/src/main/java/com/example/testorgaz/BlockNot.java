package com.example.testorgaz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlockNot extends AppCompatActivity
{
    //Переменная для "двойное нажатие для выхода из приложения"
    private static long back_pressed;
    //

    //Переменные для блокнота
    AdapterForBlocknotItems Adp;
    private ArrayList<BlocknotItems> list = new ArrayList<BlocknotItems>();
    private ListView blocknotlist;
    private Button btn_blocknot;
    private Button btn_delete;
    private Button btn_delKlas;
    private Animation animBut,animHug, anim_cl;
    //

    //Переменные для перехода по страницам
    private Button btn_inv, btn_ras;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_not);

        blocknotlist = (ListView)findViewById(R.id.Listv);  //Связываем нашу переменную ListView с листом по ID

        animBut = AnimationUtils.loadAnimation(this,R.anim.but_anim);
        animHug = AnimationUtils.loadAnimation(this,R.anim.btn_hug);
        anim_cl = AnimationUtils.loadAnimation(this,R.anim.btn_click);

        //Запуск ключивых методов
        create("DirTxtBlock"); //создание и проверка на существование папки txt базы для блокнота
        Adap(list,0);
        addListenerOnButton(); //метод всех кнопок
        listclick(); // метож нажатия на пункт листа
        btn_delete.setOnClickListener(firstButtonListener);
        btn_delKlas.setClickable(false);
    }

    public void Adap(ArrayList f,int g)
    {
        getSource(g); //вызываем метот getSourse и отправляем в ее значение, которое получает метот Adap, чтобы считывать файлы из нужной директории
        Adp = new AdapterForBlocknotItems(this,f);
        blocknotlist.setAdapter(Adp);  //назначаю созданный адаптер для моей ListView
    }

    //Создаем папку для файлов блокнота
    private File create(String name) {
        File baseDir;

            baseDir = getFilesDir();

        if (baseDir == null)
            return Environment.getExternalStorageDirectory();

        File folder = new File(baseDir, name);

        if (folder.exists()) {
            return folder;
        }
        if (folder.isFile()) {
            folder.delete();
        }
        if (folder.mkdirs()) {
            return folder;
        }

        return Environment.getExternalStorageDirectory();
    }

    //для работы листра

    public  List<BlocknotItems> getSource(int prov)
    {
        final File internalStorageDir = getFilesDir();  //это путь к папке с txt файлами нашего приложения- блокнота
        String path = internalStorageDir.toString()+"/DirTxtBlock";   //перегоняем его в стринг
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
                String[] split = fileName.split(".txt");
                String Zag = split[0];



                String contents = readUsingBufferedReader( path+"/"+Zag+".txt");

                if(prov==1){list.add(new BlocknotItems(Zag,contents,"",null,false,1,""));}
                if(prov==0) {list.add(new BlocknotItems(Zag,contents,"",null,false,0,""));}

            }
        }catch (NullPointerException e){
            System.out.println("Ошибка" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // события при нажатии на элемент спеиска
    public void listclick ()
    {
        blocknotlist.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        Adp.getView(i,view,blocknotlist);
                        final String prov= (String) ((TextView)view.findViewById(R.id.tvZag)).getText();

                        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                        String path = internalStorageDir.toString()+"/DirTxtBlock";   //перегоняем его в стринг + папка для файлов инвинторизатора

                        try {
                            String contents = readUsingBufferedReader( path+"/"+prov+".txt");

                            Intent intent = new Intent(BlockNot.this,BlockNotADD.class);
                            intent.putExtra("ZagName",prov);
                            intent.putExtra("OsnText",contents);
                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        blocknotlist.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        Adp.getView(i,view,blocknotlist);
                        final String prov= (String) ((TextView)view.findViewById(R.id.tvZag)).getText()+".txt";

                        AlertDialog.Builder okno= new AlertDialog.Builder(BlockNot.this);  //создаем новую переменную клаасса AlertDialog ( тупо появление окошка)  .Builder озхначает что мы его создаем
                        okno.setTitle("Удалить запись?"); // значение в титульнике
                        okno.setMessage(prov);  //типо текст в основной части окошка(подсказка)
                        okno.setPositiveButton("Да", new DialogInterface.OnClickListener()  //создаем кнопульку, которая означает положительное значение (на самом деле от это заваисит ток справа или слева оно...)
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)  //при нажатии на кнопулю справа (положительную) делаем.....
                            {
                                final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                                String path = internalStorageDir.toString()+"/DirTxtBlock/";   //перегоняем его в стринг + папка для файлов инвинторизатора

                                File file = new File(path,prov); //берем файл с путем и именем на уаление
                                file.delete(); //удаляем наш файл

                                //обновляем лист
                                list.clear();
                                Adap(list,0);
                            }
                        });
                        okno.setNegativeButton("Нет", new DialogInterface.OnClickListener() {   //при нажатии на левую кнопку (отрицалетльную) делаем ничего, просто чтобы окошко пропало
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) { }
                        });
                        okno.show(); //делаем чтобы наше окошко стало видимым (это тупо это так рабботает тип если хочешь чтобы чет поивилось пиши .show ....  -___-  )
                        return true;
                    }
                }
        );
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
    public void showResult()
    {
        String Name;
        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
        String path = internalStorageDir.toString()+"/DirTxtBlock";   //перегоняем его в стринг + папка для файлов инвинторизатора

        for (BlocknotItems p : Adp.getBox())
        {
            if (p.box) {
                Name = p.name+".txt";

                File file = new File(path,Name);
                file.delete();
            }

        }

        Toast.makeText(this, "Удалено", Toast.LENGTH_LONG).show();
        list.clear();
        Adap(list, 1);
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
            Toast.makeText(getBaseContext(), "Нажмите'Назад' еще раз для выхода из приложения", Toast.LENGTH_SHORT).show();
            }
        back_pressed = System.currentTimeMillis();
    }


    View.OnClickListener firstButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // меняем обработчик нажатия кнопки на второй
            list.clear();
            Adap(list,1);

            int resId = R.drawable.musorka_udalenie;

            btn_delete.startAnimation(anim_cl);
            btn_delete.setBackgroundResource(resId);

            btn_blocknot.startAnimation(animHug);
            btn_delKlas.startAnimation(animBut);

            btn_blocknot.setClickable(false);
            btn_delKlas.setClickable(true);

            Toast.makeText(getBaseContext(), "Включен режим удаления", Toast.LENGTH_SHORT).show();
            btn_delete.setOnClickListener(secondButtonListener);
        }
    };

    View.OnClickListener secondButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // возвращаем первый обработчик нажатия кнопки
            list.clear();
            Adap(list,0);

            int resId = R.drawable.musorka;

            btn_delete.startAnimation(anim_cl);
            btn_delete.setBackgroundResource(resId);

            btn_blocknot.startAnimation(animBut);
            btn_delKlas.startAnimation(animHug);

            btn_blocknot.setClickable(true);
            btn_delKlas.setClickable(false);

            Toast.makeText(getBaseContext(), "Режим удаления выключен", Toast.LENGTH_SHORT).show();
            btn_delete.setOnClickListener(firstButtonListener);
        }
    };

    //Кнопки
    public void addListenerOnButton()
    {
        //Кнопки перехода
        btn_inv = (Button)findViewById(R.id.buttonToInvin);
        btn_ras = (Button)findViewById(R.id.buttonToRashod);
        //

        //Кнопки для страницы
        btn_blocknot = (Button)findViewById(R.id.buttonToAdd);
        btn_delete =(Button)findViewById(R.id.buttonToDelete);
        btn_delKlas = (Button)findViewById(R.id.buttonToDeleteKlas);
        //

        btn_delKlas.setVisibility(View.INVISIBLE);

        //Кнопка +
        btn_blocknot.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btn_blocknot.startAnimation(anim_cl);
                        Intent intent = new Intent(BlockNot.this,BlockNotADD.class);
                        startActivity(intent);
                    }
                }
        );

        //кнопка подтвердить в режиме удаленич
        btn_delKlas.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btn_delKlas.startAnimation(anim_cl);
                        AlertDialog.Builder okno= new AlertDialog.Builder(BlockNot.this);  //создаем новую переменную клаасса AlertDialog ( тупо появление окошка)  .Builder озхначает что мы его создаем
                        okno.setTitle("Удалить статью(и) расхода/дохода?"); // значение в титульнике
                        okno.setPositiveButton("Да", new DialogInterface.OnClickListener()  //создаем кнопульку, которая означает положительное значение (на самом деле от это заваисит ток справа или слева оно...)
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)  //при нажатии на кнопулю справа (положительную) делаем.....
                            {
                                showResult();//вызываем метод который удаляет файлы с выбраным ччекбоксами в нужной папке
                                list.clear();
                                Adap(list,0);
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
        //Переход на инвинторизатор из блокнота
        btn_inv.setOnClickListener(
                new  View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(BlockNot.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // установка флагов
                        startActivity(intent);
                    }
                }
        );
        //Переход на расходы из блокнота
        btn_ras.setOnClickListener(
                new  View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(BlockNot.this,Rashod.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // установка флагов
                        startActivity(intent);
                    }
                }
        );
    }
}