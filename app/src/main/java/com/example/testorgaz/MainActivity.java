package com.example.testorgaz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity
{
    //Переменная для "двойное нажатие для выхода из приложения"
    private static long back_pressed;
    //

    //Переменные для инвинтаризатора
    AdapterForBlocknotItems Adp; //переменндая нашего адаптера
    private ListView inventorlist;   //переменная листа
    private Button btn_add, btn_back, btn_backDl,btn_delTx,btn_search;//переменные кнопкок
    private EditText Search;   //переменная поле ввода для поиска
    private TextView pok; // Текст чтобы было понятно в какой папке мы
    private String def = "Классы предметов"; //стандартное имя заголовка
    private ArrayList<String> list = new ArrayList<>();    //ArrayList для заполнения в него папок
    private ArrayList<BlocknotItems> list3 = new ArrayList<BlocknotItems>(); //Arraylist для заполнения элемнтов txt
    private int pr=0; // специальная переменная для того чтобы рабботал определенный метод P.S. смотри ниже
    private String Vl;
    private Animation animBut,animHug, anim_cl;
    //

    //Переменные для перехода по страницам
    private  Button btn_block, btn_ras;  //переменные для  кнопок перехода
    //

    //Основной метод
    @Override
    protected void onCreate(Bundle savedInstanceState) //Запуск проги
    {
        //Эти две сторочи посути задают 2 параметра: 1) при создании Activity сохранять данные на ней 2) связываем ее с layout файлом ( это по сути наш интерфейс)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //связываем важные элементы страницы
        inventorlist = (ListView)findViewById(R.id.ListVis);  //Связываем нашу переменную ListView с листом по ID
        Search =(EditText)findViewById(R.id.editTextToPoisk); //связываем переменную с полем ввода по ID
        pok = (TextView)findViewById(R.id.textView); //Связываем переменную с полем текста по ID
        //

        animBut =AnimationUtils.loadAnimation(this,R.anim.but_anim);
        animHug = AnimationUtils.loadAnimation(this,R.anim.btn_hug);
        anim_cl = AnimationUtils.loadAnimation(this,R.anim.btn_click);

        pok.setText(def); //задем начальное значение текста, чтобы понимать в какой папке находимся

            //Запуск методов
            create("DirTxtInv"); //метод для "Создаем директорию для файлов инвинторизатора"
            create("IntorItemVal"); // создаем папку для хранения колличесва вещей
            create("Rsshod");
            create("RashodVal");
            create("ImgDir");
            addListenerOnButton("",0); // метод для работы кнопок на Activity( странице)
            Adap( "");  //отправлю начальные значения при загрузке этого экрана
            ProvInt(pr, ""); //метод для правильной работы listclick
            //

        btn_backDl.setClickable(false);
        btn_back.setClickable(false);
        btn_delTx.setClickable(false);
    }
    //метод выполнение при повторном запуске класса, кароч для того чтобы обновить список после добовления вещей
    @Override
    protected void onResume()
    {
        super.onResume();
        if (pr==1)
        {
            list3.clear();
            AdapBox(list3, Vl, 0);
        }
    }

    //Этот метод для адаптера основы (классы)
    public void Adap(String g)
    {
        getSource(g); //вызываем метот getSourse и отправляем в ее значение, которое получает метот Adap, чтобы считывать файлы из нужной директории
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this,R.layout.failsinvintor,this.list);  //Делаем адаптер, который перегонит название файлов в String для их отображение на странице
        inventorlist.setAdapter(adapter);  //назначаю созданный адаптер для моей ListView
    }
    //Этот метот для адаптера txt файлов и чекбоксов
    public void AdapBox(ArrayList f,String g,int pr)
    {
        getSourceToBox(g,pr); //вызываем метот getSourse и отправляем в ее значение, которое получает метот Adap, чтобы считывать файлы из нужной директории
        Adp = new AdapterForBlocknotItems(this,this.list3);  //Делаем адаптер, который перегонит название файлов в String для их отображение на странице
        inventorlist.setAdapter(Adp);  //назначаю созданный адаптер для моей ListView
    }

    //спец метод блогодаря которому мы можем выполнять разные действия с элементами одинакого списка
    public void ProvInt(int k, String path)
    {
        if (k==0) {listclick();}  //получаем значание k, если оно равно 0 (тип дефолт), то выполняем страндартную функцию для нажатия на элемент списка ( это для папок)
        else {listclick(path);} //если оно не 0 т е люб другое( будем делать 1 чтобы не напутать ничего), то выполняем функию для нажатия на txt элемент списка( это для самих веще в инвинторизаторе а не классов)
    }



    //Создаем папку для файлов инвинторизатора
    private File create(String name)
    {
        File baseDir;  //задаем переменную типа File

        baseDir = getFilesDir();  //Присваевыем ей путь где создаваться

        if (baseDir == null)   //если путь пуст возварашаем обсолют ссылку ( не важно че это, это на программистах)
            return Environment.getExternalStorageDirectory();

        File folder = new File(baseDir, name);  //делаем переменную типа File и пихаем в нее ранее созданный файл с путем, после даем переменную name которая получается из запуска метода

        //спец условия для успешного создания файла
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

    //Чтобы работал лист в инвинтаризаторе
    //для работы листра
    public  List<String> getSource(String Name){

        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
        String path = internalStorageDir.toString()+"/DirTxtInv"+Name;   //перегоняем его в стринг + папка для файлов инвинторизатора

        //прога ищет папки и пихает их в ArrayList в итоге т к ArrayList связан с ListView они отображаются
        try {
            File folder = new File(path);
            String[] files = folder.list(new FilenameFilter(){
                @Override public boolean accept(File folder, String name) {
                    return name.endsWith("");
                }
            });
            for ( String fileName : files ) {
                list.add(fileName); //добовляем все файлы или папки что в листе
            }
        }catch (NullPointerException e){
            System.out.println("Ошибка" + e.getMessage());
        }
        return list;
    }

    public  ArrayList<BlocknotItems> getSourceToBox(String Name,int prov){

        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
        String path = internalStorageDir.toString()+"/DirTxtInv"+Name;   //перегоняем его в стринг + папка для файлов инвинторизатора
        String pathToVale = internalStorageDir.toString()+"/IntorItemVal"; // берем стринг путь к записи колличесво вещей
        String pathImg = internalStorageDir.toString()+"/ImgDir";

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
                String Cout = readUsingBufferedReader(pathToVale+"/"+Zag+".txt");

                File imgF = new File(pathImg,Zag+".jpg");
                Bitmap bm = BitmapFactory.decodeFile(imgF.getAbsolutePath());
                Drawable drawable = new BitmapDrawable(getResources(),bm);

                if(prov==1){list3.add(new BlocknotItems(Zag,contents,Cout,drawable,false,1,"Кол-во:"));} //чтобы заполнить лист элементами с чекбоксами
                if(prov==0){list3.add(new BlocknotItems(Zag,contents,Cout,drawable,false,0,"Кол-во:"));}//чтобы заполнить лист элементами без чекбоксов
            }
        }catch (NullPointerException e){
            System.out.println("Ошибка" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list3;
    }


    // события при нажатии на элемент спеиска, используем популярный метод "перегрузка метода" чтобы отдельно выполнять дейсвия при нажатии на каталог и на предмет в каталоге
    public void listclick ()
    {
            inventorlist.setOnItemClickListener( //для листа inventorlist назначаем собития при нажатии на элемент листа
                new AdapterView.OnItemClickListener() //новое событие
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) //при нажатии на элемент
                    {
                        String prov = (String) inventorlist.getItemAtPosition(i); //нужно для названия директории в которой находимся
                        String val = "/" + inventorlist.getItemAtPosition(i); //делаем переменную котораяы  будет ссылкой на нов папку ( тип берем название предмена а перед ним пизхаем / чтобы стал ссылкой
                        String butPath= (String) inventorlist.getItemAtPosition(i);
                        AdapBox( list3, val,0); //пихаем в него файлы из нужной нам папки
                        addListenerOnButton(butPath,1); //Отправляем в функцию с кнопками значение пути папки в которую тип зашли ( только чтобы можно было добавить txt файл в ту папку)
                        pok.setText(prov); //устанавливаем значание заголовка равной названию папки в которую переходим


                        pr=1; //делаем нашу спец переменную равной 1 т к нам нужно чтобы заработал другой метод при нажатии на элемпент списка
                        ProvInt(pr,prov); //вызываем нашу проверку еще раз, чтобы переключить рабботающий метод "событие при нажатии на элемент списка"
                        btn_back.startAnimation(animBut);
                        btn_back.setClickable(true);
                        btn_delTx.setClickable(false);
                        Vl=val;
                    }
                }
        );
            inventorlist.setOnItemLongClickListener(
                    new AdapterView.OnItemLongClickListener()
                    {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
                        {
                            final String prov = (String) inventorlist.getItemAtPosition(i); //нужно для названия директории в которой находимся
                            AlertDialog.Builder okno= new AlertDialog.Builder(MainActivity.this);  //создаем новую переменную клаасса AlertDialog ( тупо появление окошка)  .Builder озхначает что мы его создаем
                            okno.setTitle("Удалить класс предметов?"); // значение в титульнике
                            okno.setMessage(prov);  //типо тест в основной части окошка(подсказка)
                            okno.setPositiveButton("Да", new DialogInterface.OnClickListener()  //создаем кнопульку, которая означает положительное значение (на самом деле от это заваисит ток справа или слева оно...)
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)  //при нажатии на кнопулю справа (положительную) делаем.....
                                {
                                    final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                                    String path = internalStorageDir.toString()+"/DirTxtInv"+"/"+prov;   //перегоняем его в стринг + папка для файлов инвинторизатора + название самой папки
                                    String DirPath=internalStorageDir.toString()+"/DirTxtInv"+"/"; //путь к нашей папке на удаление
                                    String pathToVale = internalStorageDir.toString()+"/IntorItemVal";

                                    try {
                                        File folder = new File(path); //новый файл который явл путем, в котором мы ищем все txt
                                        String[] files = folder.list(new FilenameFilter(){ //с помошью цыкла (он ищет txt файлы в определенном каталоге до тех пор пока они не закончатся) заполняем строчный массив
                                            @Override public boolean accept(File folder, String name) {
                                                return name.endsWith(".txt");
                                            }
                                        });
                                        for ( String fileName : files ) { //в новом цыкле делаем для всех элементов нашего строчного массива:
                                            File file = new File(path,fileName); // берем файл который лежит в нужной папке, а название этого файла циклично меняется
                                            file.delete();//удаляем этот файл  В ИТОГЕ с помощью цыкла чистим папку от фалйлов формата txt
                                            File fileval = new File(pathToVale,fileName);
                                            fileval.delete();
                                        }
                                    }catch (NullPointerException e){
                                        System.out.println("Ошибка" + e.getMessage());
                                    }

                                    File Dir = new File(DirPath,prov); //после всего удаляем саму папку тем же способом но без цикла
                                    Dir.delete();

                                    list.clear();  //чистим лист
                                    Adap(""); //заполняем его новыми значениями( типо без той папки т к мы ее удалили)
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
    public void listclick (final String k)
    {
        inventorlist.setOnItemClickListener( //для листа inventorlist назначаем собития при нажатии на элемент листа
                new AdapterView.OnItemClickListener() //новое событие
                {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) //при нажатии на элемент
                    {
                        Adp.getView(i,view,inventorlist);
                        final String prov= (String) ((TextView)view.findViewById(R.id.tvZag)).getText()+".txt";

                        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                        String path = internalStorageDir.toString()+"/DirTxtInv";   //перегоняем его в стринг + папка для файлов инвинторизатора
                        String pathImg = internalStorageDir.toString()+"/ImgDir";

                        String[] split = prov.split(".txt");
                        String Zag = split[0];
                        try {
                            String contents = readUsingBufferedReader( path+"/"+k+"/"+Zag+".txt");
                            String val = (String) ((TextView)view.findViewById(R.id.textVal)).getText();

                            File imgF = new File(pathImg,Zag+".jpg");
                            Bitmap bm = BitmapFactory.decodeFile(imgF.getAbsolutePath());
                            Drawable drawable = new BitmapDrawable(getResources(),bm);

                            Intent intent = new Intent(MainActivity.this,InvintorADD.class);
                            intent.putExtra("ZagName",Zag);
                            intent.putExtra("OsnText",contents);
                            intent.putExtra("PathDel","/"+k);
                            intent.putExtra("ValueI", val);
                            startActivity(intent);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        inventorlist.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener()
                {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
                    {
                        btn_delTx.startAnimation(animBut);
                        btn_backDl.startAnimation(animBut);

                        btn_backDl.setClickable(true);
                        btn_delTx.setClickable(true);

                        btn_add.startAnimation(animHug);
                        btn_search.startAnimation(animHug);
                        btn_back.startAnimation(animHug);

                        btn_add.setClickable(false);
                        btn_search.setClickable(false);
                        btn_back.setClickable(false);

                        //кароч обновляем список чтобы появились чекбоксы и исчесли некоторые кнопки, а некоторые появились
                        list3.clear();
                        AdapBox(list3,"/"+k,1);
                        Search.setVisibility(View.INVISIBLE);
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

    //Удаление по чек боксам
    public void showResult(String k)
    {
        String Name;
        String imgName;
        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
        String path = internalStorageDir.toString()+"/DirTxtInv";   //перегоняем его в стринг + папка для файлов инвинторизатора
        String pathToVale = internalStorageDir.toString()+"/IntorItemVal";
        String pathImg = internalStorageDir.toString()+"/ImgDir";

        for (BlocknotItems p : Adp.getBox())
        {
            if (p.box) {
                Name = p.name+".txt";
                imgName = p.name+".jpg";
                File file = new File(path+"/"+k,Name);
                file.delete();
                File fileval = new File(pathToVale,Name);
                fileval.delete();
                File fileImg = new File(pathImg,imgName);
                fileImg.delete();
            }
        }

        list3.clear();
        AdapBox(list3, "/"+k, 1);
    }

    //Метод для выхода из приложения по двойному касанию.
    @Override
    public void onBackPressed()
    {

        if (back_pressed + 1000 > System.currentTimeMillis())
        {
            //эмулируем нажатие на HOME, сворачивает приложение
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

        } else { Toast.makeText(getBaseContext(), "Нажмите'Назад'еще раз для выхода из приложения", Toast.LENGTH_SHORT).show(); }
        back_pressed = System.currentTimeMillis();
    }



    //Кнопки на странице
    public void addListenerOnButton(final String str, final int prov)
    {
        //даем кнопкам ID кнопок в самой проге:

        //Кнопки перехода:
        btn_block = (Button)findViewById(R.id.buttonToBlocknod);
        btn_ras = (Button)findViewById(R.id.buttonToRashod);
        //

        //Кнопки страницы
        btn_add = (Button)findViewById(R.id.buttoToAdd2);
        btn_back = (Button)findViewById(R.id.buttonToBackCl);
        btn_backDl =(Button)findViewById(R.id.buttonToBkOtm);
        btn_delTx=(Button)findViewById(R.id.buttonToDelTx);
        btn_search=(Button)findViewById(R.id.buttonToPoisk);
        //

        //делаем некоторые кнопки невидимы по дефолту ( они для режима удаления )
        btn_back.setVisibility(View.INVISIBLE);
        btn_backDl.setVisibility(View.INVISIBLE);
        btn_delTx.setVisibility(View.INVISIBLE);
        // здесь был кот

        //Кнопка +
        btn_add.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (prov == 0)
                        {
                            btn_add.startAnimation(anim_cl);
                            AlertDialog.Builder okno = new AlertDialog.Builder(MainActivity.this);  //создаем новую переменную клаасса AlertDialog ( тупо появление окошка)  .Builder озхначает что мы его создаем
                            okno.setTitle("Создать новый класс предметов?"); // значение в титульнике
                            okno.setMessage("Введите имя класса");  //типо тест в основной части окошка(подсказка)
                            final EditText input = new EditText(MainActivity.this); // создаем новую переменную типа EditText ( поле для ввода текста)
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            okno.setView(input); // связываем переменную EditText с нашим AlertDialog чтобы он показывался и имел возможность записи файла
                            okno.setPositiveButton("Создать", new DialogInterface.OnClickListener()  //создаем кнопульку, которая означает положительное значение (на самом деле от это заваисит ток справа или слева оно...)
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)  //при нажатии на кнопулю справа (положительную) делаем.....
                                {
                                    String value = input.getText().toString();   // считываем текст что ввел пользователь и пихаем в новую переменную типа String

                                    final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                                    String path = internalStorageDir.toString() + "/DirTxtInv";   //перегоняем его в стринг

                                    File folder = new File(path, value);  //делаем переменную типа File и пихаем в нее ранее созданный файл с путем, после даем переменную value которая является названием файла
                                    folder.mkdirs(); //создает новую папку (класс для предметов)
                                    list.clear();//чистим наш лист
                                    Adap("");//заполняем его снова, чтобы было видно новоиспеченную папку
                                }
                            });
                            okno.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {   //при нажатии на левую кнопку (отрицалетльную) делаем ничего, просто чтобы окошко пропало
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            okno.show(); //делаем чтобы наше окошко стало видимым (это тупо это так рабботает тип если хочешь чтобы чет поивилось пиши .show ....  -___-  )
                        }
                        else
                            {
                                btn_add.startAnimation(anim_cl);
                                Intent intent = new Intent(MainActivity.this,InvintorADD.class); //новое условие (Intent) (тип раббота с классами) связываем классы чтобы выполнять с ними всякую дичь
                                intent.putExtra("TakePath","/"+str); //отправляем в, связанную с нашим MainActivity, InvitorADD значения str, которое получает функция addListenerOnButton из listclick, так же даем ей свой Name "TakePath"
                                startActivity(intent);    // стартуем новую страницу ( так называемое активити) в соответвии с заданным намерением
                            }
                    }
                }
        );

        //Кнопка back to class
        btn_back.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        list.clear();   //чистим лист чтобы не дублировать значения
                        list3.clear(); //чистим лист с тхт файломи
                        Adap(""); // кидаем стандартные значения, чтобы считывал папки
                        pok.setText(def);  //меняем значение нашего зоголовка на стандартный
                        pr=0; //делаем спец переменную равной 0
                        addListenerOnButton("",0); //отправляем пустое значение чтобы сбросить путь к папке в которой мы были до этого
                        ProvInt(pr,"");//делаем вызов проверки чтобы мы могли снова выполнять дейсвия над папками а не txt файлами
                        btn_back.startAnimation(animHug);
                        btn_back.setClickable(false);
                        btn_delTx.setClickable(false);

                    }
                }
        );
        //Удаление в режиме удаления
        btn_delTx.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btn_delTx.startAnimation(anim_cl);
                        AlertDialog.Builder okno= new AlertDialog.Builder(MainActivity.this);  //создаем новую переменную клаасса AlertDialog ( тупо появление окошка)  .Builder озхначает что мы его создаем
                        okno.setTitle("Удалить предмет(ы)?"); // значение в титульнике
                        okno.setPositiveButton("Да", new DialogInterface.OnClickListener()  //создаем кнопульку, которая означает положительное значение (на самом деле от это заваисит ток справа или слева оно...)
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)  //при нажатии на кнопулю справа (положительную) делаем.....
                            {
                                showResult(str);//вызываем метод который удаляет файлы с выбраным ччекбоксами в нужной папке
                                list3.clear();
                                AdapBox(list3,"/"+str,0);

                                btn_back.startAnimation(animBut);
                                btn_search.startAnimation(animBut);
                                btn_add.startAnimation(animBut);

                                btn_back.setClickable(true);
                                btn_search.setClickable(true);
                                btn_add.setClickable(true);

                                btn_delTx.startAnimation(animHug);
                                btn_backDl.startAnimation(animHug);

                                btn_delTx.setClickable(false);
                                btn_backDl.setClickable(false);

                                Search.setVisibility(View.VISIBLE);
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
        //Кнопка назад в режиме удаления
        btn_backDl.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btn_backDl.startAnimation(animHug);
                        btn_delTx.startAnimation(animHug);

                        btn_backDl.setClickable(false);
                        btn_delTx.setClickable(false);


                        btn_add.startAnimation(animBut);
                        btn_search.startAnimation(animBut);
                        btn_back.startAnimation(animBut);

                        btn_add.setClickable(true);
                        btn_search.setClickable(true);
                        btn_back.setClickable(true);

                        //обновляем список, убирая чекбоксы, делаем стандартные кнопки видимы, а для режима удаления кнопки невидимы
                        list3.clear();
                        AdapBox(list3,"/"+str,0);
                        Search.setVisibility(View.VISIBLE);
                    }
                }
        );
        //кнопка поиска
        btn_search.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        btn_search.startAnimation(anim_cl);
                        final File internalStorageDir = getFilesDir();  //это путь к папке нашего приложения
                        String path = internalStorageDir.toString()+"/DirTxtInv/";   //перегоняем его в стринг
                        String val = Search.getText().toString()+".txt"; // береме текст из поля поиска
                        String pathToVale = internalStorageDir.toString()+"/IntorItemVal";
                        Search.setText(""); //очищаем его
                        String pathImg = internalStorageDir.toString()+"/ImgDir";

                        //Кароч вкрации, делаю двойной цикл, первый цикл берет значения папок, второй файлов, в каждой папке ищется файл имя которого мы ввели, если находится все чики брики, если нет, то ничего не происходит
                        try {
                            File folder = new File(path); //новый файл который явл путем, в котором мы ищем все txt
                            String[] files = folder.list(new FilenameFilter(){ //с помошью цыкла (он ищет txt файлы в определенном каталоге до тех пор пока они не закончатся) заполняем строчный массив
                                @Override public boolean accept(File folder, String name) {
                                    return name.endsWith("");
                                }
                            });
                            for ( String fileName : files ) { //в новом цыкле делаем для всех элементов нашего строчного массива:
                                try {
                                    File file = new File(path+fileName+"/"); //новый файл который явл путем, в котором мы ищем все txt
                                    String[] filep = file.list(new FilenameFilter(){ //с помошью цыкла (он ищет txt файлы в определенном каталоге до тех пор пока они не закончатся) заполняем строчный массив
                                        @Override public boolean accept(File folder, String name) {
                                            return name.endsWith(".txt");
                                        }
                                    });
                                    for ( String filee : filep ) //в новом цыкле делаем для всех элементов нашего строчного массива:
                                    {
                                        if (filee.equals(val)) //если такой файл все же есть то делаем:
                                        {
                                            try {
                                                list.clear(); //чистим лист на случай если пользователь сидит в классах
                                                AdapBox(list3,fileName,0); //отправляем поиск предмета в определенной папки, в которой он находится
                                                list3.clear(); //чистим лист т к там находятися все файлы из папки

                                                String [] f = filee.split(".txt");
                                                String fl = f[0];
                                                String prov = fileName; //нужно для названия директории в которой находимся


                                                String[] split = filee.split(".txt");
                                                String Zag = split[0];

                                                String contents = readUsingBufferedReader( path+fileName+"/"+Zag+".txt");
                                                String Cout = readUsingBufferedReader(pathToVale+"/"+Zag+".txt");
                                                File imgF = new File(pathImg,Zag+".jpg");

                                                Bitmap bm = BitmapFactory.decodeFile(imgF.getAbsolutePath());
                                                Drawable drawable = new BitmapDrawable(getResources(),bm);

                                                list3.add(new BlocknotItems(fl,contents,Cout,drawable,false,0,"Кол-во:")); //добоваляем в лист новый элемент, который по сути и есть наш файл
                                                pr=1; //делаем спец переменную равной 1
                                                pok.setText(fileName); //меняем текст
                                                ProvInt(pr,prov); //вызываем нашу проверку еще раз, чтобы переключить рабботающий метод "событие при нажатии на элемент списка"
                                                btn_back.setAnimation(animBut);
                                                btn_back.setClickable(true);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }
                                }catch (NullPointerException e){
                                    System.out.println("Ошибка" + e.getMessage());
                                }

                            }
                        }catch (NullPointerException e){
                            System.out.println("Ошибка" + e.getMessage());
                        }

                    }
                }
        );

        //Методы чтобы кнопки открывать новые странипцы                                ВНИМАНИЕ! нельзя чтобы кнопка открывала страницу на которой уже находится пользхователь т к это преведет к сбою
        //Переход на блокнод из инвиторизатора
        btn_block.setOnClickListener(           // задаем кнопке параменты при нажатии на нее
                new  View.OnClickListener()
                {   // новый параметр при нажатии
                    @Override                   // переписываем метод onClick (важно только программистам)
                    public void onClick(View v)
                    {    //сам метод onClick
                        Intent intent = new Intent(MainActivity.this,BlockNot.class); //новое условие (Intent) (тип раббота с классами) связываем классы чтобы выполнять с ними всякую дичь
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);// установка флагов
                        startActivity(intent);    // стартуем новую страницу ( так называемое активити) в соответвии с заданным намерением
                    }
                }
        );
        //Переход на расходы из инвитаризатора
        btn_ras.setOnClickListener(
                new  View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(MainActivity.this,Rashod.class); //новое условие (Intent) (тип раббота с классами) связываем классы чтобы выполнять с ними всякую дичь
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // установка флагов
                        startActivity(intent);
                    }
                }
        );
    }
}