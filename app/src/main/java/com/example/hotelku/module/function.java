package com.example.hotelku.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.Build;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.hotelku.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class function {

    public static Date convertStringToDate(String dateString) {
        Date dateData = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy",  new Locale("id", "ID"));
            dateData = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return dateData;
    }

    public static String convertDateToString(Date datedData) {
        String dateString = null;

        SimpleDateFormat sdf = new SimpleDateFormat("d MMMM yyyy",  new Locale("id", "ID"));
        dateString = sdf.format(datedData);

        return dateString;
    }

    // Untuk menampilkan gambar dari Uri
    public static void loadAvatarIntoImageView(Context context, Uri avatarUri , ImageView avatarImageView) {
        if (context != null && avatarImageView != null && avatarUri != null) {
            Glide.with(context)
                    .load(avatarUri)
                    .transform(new CircleCrop())
                    .into(avatarImageView);
        }
    }

    // Untuk menampilkan gambar dari Url
    public static void loadAvatarIntoImageView(Context context, String avatarUrl , ImageView avatarImageView) {
        if (context != null && avatarImageView != null && avatarUrl != null) {
            Glide.with(context)
                    .load(avatarUrl)
                    .transform(new CircleCrop())
                    .into(avatarImageView);
        }
    }

    // Indicate Status
    public static String toAvailable(Boolean status) {
        if (!status) {
            return "Available";
        } else {
            return "Reservated";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Date localDatetoDate(LocalDate localDate) {
        Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Date date = Date.from(instant);

        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();

        LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

        return localDate;
    }

    public static String formatCurrency(Long amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return currencyFormat.format(amount);
    }

    public static void showDatePickerBOD(AppCompatActivity activity, EditText editText) {
        // Buat MaterialDatePicker dengan tipe DATE
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a birth of date");

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        // Set listener saat tanggal dipilih
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Format tanggal yang dipilih
            String formattedDate = function.convertDateToString(new Date(selection));
            editText.setText(formattedDate);
        });

        // Tampilkan date picker
        materialDatePicker.show(activity.getSupportFragmentManager(), "DATE_PICKER");
    }

    public static void showDatePickerStartDate(AppCompatActivity activity, EditText editText) {
        // Buat MaterialDatePicker dengan tipe DATE
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a reservation start date");

        long today = MaterialDatePicker.todayInUtcMilliseconds();

        CalendarConstraints.Builder contraintBuild = new CalendarConstraints.Builder();
        contraintBuild.setValidator(DateValidatorPointForward.from(today));
        builder.setCalendarConstraints(contraintBuild.build());

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        // Set listener saat tanggal dipilih
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Format tanggal yang dipilih
            String formattedDate = function.convertDateToString(new Date(selection));
            editText.setText(formattedDate);
        });

        // Tampilkan date picker
        materialDatePicker.show(activity.getSupportFragmentManager(), "DATE_PICKER");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showDatePickerEndDate(AppCompatActivity activity, EditText editText) {
        // Buat MaterialDatePicker dengan tipe DATE
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select a reservation end date");

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        CalendarConstraints.Builder contraintBuild = new CalendarConstraints.Builder();
        contraintBuild.setValidator(DateValidatorPointForward.from(localDatetoDate(tomorrow).getTime()));
        builder.setCalendarConstraints(contraintBuild.build());

        final MaterialDatePicker<Long> materialDatePicker = builder.build();

        // Set listener saat tanggal dipilih
        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            // Format tanggal yang dipilih
            String formattedDate = function.convertDateToString(new Date(selection));
            editText.setText(formattedDate);
        });

        // Tampilkan date picker
        materialDatePicker.show(activity.getSupportFragmentManager(), "DATE_PICKER");
    }
}
