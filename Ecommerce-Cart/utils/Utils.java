package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import Actors.Catalog.Constraints;

public class Utils {

    public static boolean isExpired(Constraints constraints) {
        if(constraints==null){
            return false;
        }
        String expiryDate = constraints.getExpiryDate();
        if(expiryDate == null || expiryDate.trim().isEmpty()){
            return true;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate exp = LocalDate.parse(expiryDate.trim(), formatter);

        return exp.isBefore(LocalDate.now());
    }

}
