package com.bitm.dailyexpanse;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Add_ExpanseActivity extends AppCompatActivity {

    private EditText amountEt, expanceDateEt, expanceTimeEt;
    private ImageView docomentIv, imageView;
    private Button imageBtn, addExpaceBtn;
    Spinner expanceTypespn;
    private String[] expaceTypes;
    ArrayAdapter<String> adapter;

    private String amount, date, time, docoment, expencetype;
    byte[] imagebyte;
    byte[] image;

    SqlHelper helper;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_expance);
        setTitle("Add Expense");
        init();

        getTimeNDataNImage();

        getData();


        //Toast.makeText(this, "pic"+bytes, Toast.LENGTH_SHORT).show();


        addExpaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setDataToDataBase();
            }
        });


    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();

        bitmap = Bitmap.createScaledBitmap(bitmap, 400, 300, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArrey = stream.toByteArray();
        return byteArrey;
    }

    private void getData() {


    }

    private void setDataToDataBase() {

        imagebyte = imageViewToByte(docomentIv);


        expencetype = expanceTypespn.getSelectedItem().toString();

        amount = amountEt.getText().toString().trim();
        date = expanceDateEt.getText().toString().trim();
        time = expanceTimeEt.getText().toString().trim();

        if (!amount.equals("") & !date.equals("") && !time.equals("")) {

            AlertDialog.Builder builder = new AlertDialog.Builder(Add_ExpanseActivity.this);

            builder.setMessage("Are You sure?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            long id = helper.insertValues(expencetype, amount, date, time, imagebyte);


                            docomentIv.setImageResource(R.drawable.document);
                            amountEt.setText("");
                            expanceDateEt.setText("");
                            expanceTimeEt.setText("");
                            Toast.makeText(Add_ExpanseActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .setNegativeButton("Cancel", null);

            AlertDialog alert = builder.create();
            alert.show();


        } else {
            Toast.makeText(Add_ExpanseActivity.this, "Not Insert something is empty :", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTimeNDataNImage() {

        docomentIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();


                imagebyte = imageViewToByte(docomentIv);

            }
        });


        expanceTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker();

            }
        });

        expanceDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getImageFromGallery();


                imagebyte = imageViewToByte(docomentIv);
            }
        });


    }

    private void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 0) {
                Uri uri = data.getData();
                docomentIv.setImageURI(uri);

            }

        }

    }

    private void openDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                        month = month + 1;
                        String currentDate = year + "/" + month + "/" + day + " 00:00:00";
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

                        Date date = null;

                        try {
                            date = dateFormat.parse(currentDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        expanceDateEt.setText(dateFormat.format(date));
                        long dateInmilis = date.getTime();

                    }
                };

        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Add_ExpanseActivity.this, dateSetListener, year, month, day);
        datePickerDialog.show();


    }

    private void openTimePicker() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Add_ExpanseActivity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_time_picker, null);

        Button doneBtn = view.findViewById(R.id.doneBtn);
        final TimePicker timePicker = view.findViewById(R.id.timePicker);

        builder.setView(view);

        final Dialog dialog = builder.create();
        dialog.show();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");

                @SuppressLint({"NewApi", "LocalSuppress"}) int hour = timePicker.getHour();
                @SuppressLint({"NewApi", "LocalSuppress"}) int min = timePicker.getMinute();

                Time time = new Time(hour, min, 0);

                expanceTimeEt.setText(timeFormat.format(time));
                dialog.dismiss();


            }
        });


    }


    private void init() {
        amountEt = findViewById(R.id.expenseamoutET);
        expanceDateEt = findViewById(R.id.expensedateET);
        expanceTimeEt = findViewById(R.id.expensetimeET);
        docomentIv = findViewById(R.id.choosedocumentIV);
        imageBtn = findViewById(R.id.choosedocumentBTN);
        addExpaceBtn = findViewById(R.id.addexpenceBTN);
        expanceTypespn = findViewById(R.id.expansetypeSP);
        expaceTypes = getResources().getStringArray(R.array.Select_expence_type);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expaceTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        expanceTypespn.setAdapter(adapter);
        expanceDateEt.setFocusable(false);
        expanceTimeEt.setFocusable(false);


        helper = new SqlHelper(this);

    }


    public void onBackPressed() {

        //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("check", 1);
        startActivity(intent);

    }


    public void retionBtn(View view) {

        docomentIv.setRotation(docomentIv.getRotation() + 90);
    }
}
