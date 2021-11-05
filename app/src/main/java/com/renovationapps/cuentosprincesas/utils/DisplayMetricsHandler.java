package com.renovationapps.cuentosprincesas.utils;

import android.content.res.Resources;

public class DisplayMetricsHandler {
        public static int getScreenWidth() {
            return Resources.getSystem().getDisplayMetrics().widthPixels;
        }

        public static int getScreenHeight() {
            return Resources.getSystem().getDisplayMetrics().heightPixels;
        }

        public static float getDensity() {
            return Resources.getSystem().getDisplayMetrics().density;
        }

        public static int getDensityDPI() {
            return Resources.getSystem().getDisplayMetrics().densityDpi;
        }

        public static int imageWidthCalc() {
            double d = (double) Resources.getSystem().getDisplayMetrics().density;
            if (d == 0.75d) {
                return 43;
            }
            if (d == 1.0d) {
                return 57;
            }
            if (d == 1.5d) {
                return 86;
            }
            if (d == 2.0d) {
                return 150;
            }
            if (d == 3.0d) {
                return 195;
            }
            return d == 4.0d ? 0 : 100;
        }

        public static String getDPI() {
            double d = (double) Resources.getSystem().getDisplayMetrics().density;
            if (d == 0.75d) {
                return "LDPI";
            }
            if (d == 1.0d) {
                return "MDPI";
            }
            if (d == 1.5d) {
                return "HDPI";
            }
            if (d == 2.0d) {
                return "XHDPI";
            }
            if (d == 3.0d) {
                return "XXHDPI";
            }
            return d == 4.0d ? "XXXHDPI" : "Unknown";
        }
}
