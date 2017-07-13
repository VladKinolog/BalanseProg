package sample;

/**
 * Конвертер велечин.
 */
public class ConvertGrainGram {

    /**
     * Конвертация  гранов в граммы
     * @param grain Вес в гранах.
     * @return Вес в граммах.
     */
    public static double grainToGram (double grain) {
        return grain*0.06479891;
    }

    /**
     * Конвертация  гранов в граммы
     * @param grain
     * @return
     */
    public static double grainToGram (int grain) {
        return grain*0.06479891;
    }

    /**
     * Конвертация граммы в граны
     * @param gram
     * @return
     */
    public static double gramToGrain (double gram){
        return gram/15.432358352941;
    }

    /**
     * Конвертация граммы в граны
     * @param gram
     * @return
     */
    public static double gramToGrain (int gram){
        return gram/15.432358352941;
    }
}

