package au.edu.jcu.cp5307.proj2.constants;

public final class Constants {
    private Constants() {}

    public static final class Type {
        public static final int INVALID_INT = -1;
        public static final String EMPTY_STRING = "";
    }

    public static final class Conversion {
        public static final int MS_PER_SEC = 1000;
        public static final int SEC_PER_MIN = 60;
        public static final int MIN_PER_HOUR = 60;
    }

    public static final class Ui {
        public static final int INSET_SHOWN = 0;
        public static final float ALPHA_DIM = 0.7f;
        public static final float ALPHA_CLEAR = 1f;
        public static final String DIMENSION_RATIO_SQUARE = "1:1";
    }

    public static final class Settings {
        public static final int BIG_DECIMAL_SCALE = 10;
    }
}
