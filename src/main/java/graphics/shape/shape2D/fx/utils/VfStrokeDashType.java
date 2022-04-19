/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphics.shape.shape2D.fx.utils;

import java.util.Arrays;
import java.util.List;

/**
 * javafx stroke dash offset types
 * 
 * @author tanxu
 */
public enum VfStrokeDashType {
//    public static final Map<String,List<Double>> PREDEFINED_DASH_PATTERN_NAME_ARRAY_MAP;
//    //https://stackoverflow.com/questions/12786363/javafx-2-x-how-to-draw-dashed-or-dotted-lines
//    private final static List<Double> PATTERN_1 = Arrays.asList(new Double[]{25d, 20d, 5d, 20d});
//    private final static List<Double> PATTERN_2 = Arrays.asList(new Double[]{50d, 40d});
//    private final static List<Double> PATTERN_3 = Arrays.asList(new Double[]{25d, 10d});
//    private final static List<Double> PATTERN_4 = Arrays.asList(new Double[]{2d});
//    private final static List<Double> PATTERN_5 = Arrays.asList(new Double[]{2d, 21d});
//    
//    static{
//        PREDEFINED_DASH_PATTERN_NAME_ARRAY_MAP = new LinkedHashMap<>();
//        PREDEFINED_DASH_PATTERN_NAME_ARRAY_MAP.put("Pattern_1", PATTERN_1);
//        PREDEFINED_DASH_PATTERN_NAME_ARRAY_MAP.put("Pattern_2", PATTERN_2);
//        PREDEFINED_DASH_PATTERN_NAME_ARRAY_MAP.put("Pattern_3", PATTERN_3);
//        PREDEFINED_DASH_PATTERN_NAME_ARRAY_MAP.put("Pattern_4", PATTERN_4);
//        PREDEFINED_DASH_PATTERN_NAME_ARRAY_MAP.put("Pattern_5", PATTERN_5);
//    }
//    
//    
    
    PATTERN_1("PATTERN_1", Arrays.asList(new Double[]{25d, 20d, 5d, 20d})),
    PATTERN_2("PATTERN_2", Arrays.asList(new Double[]{50d, 40d})),
    PATTERN_3("PATTERN_3", Arrays.asList(new Double[]{25d, 10d})),
    PATTERN_4("PATTERN_4", Arrays.asList(new Double[]{2d})),
    PATTERN_5("PATTERN_5", Arrays.asList(new Double[]{2d, 21d}));
    
    private final List<Double> dashArrayList;

    private final String name;
    
    VfStrokeDashType(String name, List<Double> dashArrayList){
        this.name = name;
        this.dashArrayList = dashArrayList;
    }
    
    /**
     * return the dash double value list corresponding to the pattern name that can be used by Shape.getStrokeDashArray().addAll() to set the stroke dash type of a Shape object
     * @param pattern
     * @return 
     */
    public static List<Double> getPatternArray(String pattern){
        for(VfStrokeDashType type:VfStrokeDashType.values()){
            if(type.name.equalsIgnoreCase(pattern)){
                return type.dashArrayList;
            }
        }
        throw new IllegalArgumentException("Given stroke dash type is not recognized:"+pattern);
    }
    
    public List<Double> getDashArrayList() {
        return dashArrayList;
    }
}
