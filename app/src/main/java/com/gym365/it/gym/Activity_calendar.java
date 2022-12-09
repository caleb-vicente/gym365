package com.gym365.it.gym;

/*  Con ayuda de:

     https://developer.android.com/guide/topics/providers/calendar-provider#intent

     https://developer.android.com/reference/android/widget/CalendarView

     Correo y contraseña creados:
        correo -> grupo8gym365@gmail.com
        contraseña -> gym365uc3m
*/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import static android.hardware.camera2.params.RggbChannelVector.BLUE;


public class Activity_calendar extends AppCompatActivity {

    public static final String TAG = "CalendarActivity";
    private CalendarView mCalendarView;

    private DbAdapter dbAdapter;
    private String date;

    private static final String DATABASE_NAME = "data";
    private static final String PRELOADED_DATABASE_NAME = "preloaded.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //creamos el adaptador de la BD y la abrimos
        dbAdapter = new DbAdapter(this);
        dbAdapter.open();

        // Si no existe la base de datos, la copiamos del directorio assets
        SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
        Boolean firstTime = mPreferences.getBoolean("firstTime", true);
        if (firstTime) {
            try {
                String destPath = "/data/data/" + getPackageName() + "/databases/" + DATABASE_NAME;

                System.out.println("Traza: no existe el fichero");
                InputStream in = getAssets().open(PRELOADED_DATABASE_NAME);
                OutputStream out = new FileOutputStream(destPath);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.flush();
                out.close();
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // se infla el layout
        setContentView(R.layout.activity_calendar);

        mCalendarView = (CalendarView) findViewById(R.id.calendarView);

        mCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

            mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

                @SuppressLint("ResourceType")
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    date = dayOfMonth + " / " + (month+1) + " / " + year;

                    showDialog(date, dayOfMonth, month, year, view);

                }
            });

    }


    public void showDialog(final String date, int dia, int mes, final int ano, View view){

        // obtenemos de la BD las tablas que coincidan con la fecha pulsada
        Cursor cursor_dialog_calendar = dbAdapter.fetchDateTable("'"+date+"'");

        //Para tener acceso a los BottomSheetDialog, ha habido que añadir al Grandle esta implementación: implementation 'com.android.support:design:28.0.0'
        BottomSheetDialog dialog_calendar = new BottomSheetDialog(this);
        View view_dialogcal = getLayoutInflater().inflate(R.layout.dialog_calendar,null);
        dialog_calendar.setContentView(view_dialogcal);

        TextView dialog_date = (TextView) dialog_calendar.findViewById(R.id.calendardate);
        dialog_date.setText(date);

        TextView dialog_title = (TextView) dialog_calendar.findViewById(R.id.calendartitle);
        TextView dialog_subtitle = (TextView) dialog_calendar.findViewById(R.id.calendarsubtitle);
        final TextView dialog_hour = (TextView) dialog_calendar.findViewById(R.id.calendarhour);

        TextView message_addcal = (TextView) dialog_calendar.findViewById(R.id.message_addcal);
        ImageView photocal = (ImageView) dialog_calendar.findViewById(R.id.photocal);
        Button button_cal = (Button) dialog_calendar.findViewById(R.id.buttonAddTableCalendar);

        dialog_calendar.show();

        if (cursor_dialog_calendar.moveToFirst()) {

            final String title = cursor_dialog_calendar.getString(cursor_dialog_calendar.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_NAME));
            dialog_title.setText(title);

            final String description = cursor_dialog_calendar.getString(cursor_dialog_calendar.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_SUBTITLE));
            dialog_subtitle.setText(description);

            String time = cursor_dialog_calendar.getString(cursor_dialog_calendar.getColumnIndexOrThrow(DbAdapter.KEY_TABLES_HOUR));
            dialog_hour.setText(time);

            photocal.setImageResource(R.drawable.tablas_icono);
            message_addcal.setText("Añade la tabla a tu calendario");
            button_cal.setBackgroundResource(R.drawable.icono_addtocalendar);

            // Pasamos la fecha y hora al nuevo evento con tu tabla
            final Calendar beginTime = Calendar.getInstance();
            // cogemos las horas del string entero
            String hora = time.substring(0,2);
            // convertimos a int
            int h = Integer.parseInt(hora);
            // cogemos los minutos y convertimos
            String min = time.substring(5,7);
            int m = Integer.parseInt(min);

            beginTime.set(ano, mes, dia, h, m);

            // Botón para añadir tabla a tu calendario
            Button boton_dialogcal = (Button) view_dialogcal.findViewById(R.id.buttonAddTableCalendar);
            boton_dialogcal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .setType("vnd.android.cursor.item/event")
                            .putExtra(CalendarContract.Events.ORGANIZER, "grupo8gym365@gmail.com")
                            .putExtra(CalendarContract.Events.TITLE,  title)
                            .putExtra(CalendarContract.Events.DESCRIPTION,  description)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,  beginTime.getTimeInMillis());

                    startActivity(i);
                }
            });
        }

        else {

            dialog_title.setText("No hay tablas este día");
            message_addcal.setText("Crea una tabla para este día");
            button_cal.setBackgroundResource(R.drawable.add_button);

            // Botón para crear tabla con esa fecha
            Button boton_dialogcal = (Button) view_dialogcal.findViewById(R.id.buttonAddTableCalendar);
            boton_dialogcal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Activity_calendar.this, Activity_add_table.class);

                    // pasamos la fecha a la tabla
                    Bundle b = new Bundle();
                    b.putString("DATE", date);
                    intent.putExtras(b);

                    startActivity(intent);
                }
            });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // ----------------------------------------------

    public void newEvent(View view) {

        Intent eventIntent = new Intent(Intent.ACTION_INSERT);

        eventIntent.setData(CalendarContract.Events.CONTENT_URI);
        eventIntent.setType("vnd.android.cursor.item/event");

        eventIntent.putExtra(CalendarContract.Events.ORGANIZER, "grupo8gym365@gmail.com");

        startActivity(eventIntent);

    }

    public void myCalendar(View view) {

        Intent calIntent = new Intent(Intent.ACTION_VIEW);

        calIntent.setData(CalendarContract.EventDays.CONTENT_URI);
        calIntent.setType("vnd.android.cursor.item/event");

        startActivity(calIntent);

    }


}
