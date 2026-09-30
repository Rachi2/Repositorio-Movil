package com.example.proyecto1.presentation.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    // Comprueba si dos marcas de tiempo pertenecen al mismo dia
    public static boolean isSameDay(long a, long b) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(a);
        Calendar cb = Calendar.getInstance();
        cb.setTimeInMillis(b);
        return ca.get(Calendar.YEAR) == cb.get(Calendar.YEAR)
                && ca.get(Calendar.DAY_OF_YEAR) == cb.get(Calendar.DAY_OF_YEAR);
    }

    // Lleva la fecha a las 00:00:00 para poder comparar dias completos
    private static Calendar startOfDay(long timestamp) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(timestamp);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    // Calcula cuantos dias pasaron respecto a hoy
    private static long daysAgo(long timestamp) {
        long today = startOfDay(System.currentTimeMillis()).getTimeInMillis();
        long day = startOfDay(timestamp).getTimeInMillis();
        return Math.round((today - day) / (24.0 * 60 * 60 * 1000));
    }

    // Devuelve el texto segun los dias de diferencia
    public static String formatDayHeader(long timestamp) {
        long days = daysAgo(timestamp);

        if (days == 0) {
            return "Hoy";
        } else if (days == 1) {
            return "Ayer";
        } else if (days >= 2 && days <= 6) {
            String weekday = new SimpleDateFormat("EEEE", new Locale("es")).format(new Date(timestamp));
            // Convierte la primera letra en mayúscula ("jueves" -> "Jueves")
            return weekday.substring(0, 1).toUpperCase() + weekday.substring(1);
        } else {
            // 7 días o más
            return new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es")).format(new Date(timestamp));
        }
    }
}