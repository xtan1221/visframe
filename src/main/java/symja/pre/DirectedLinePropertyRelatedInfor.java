/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package symja.pre;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tanxu
 */
public class DirectedLinePropertyRelatedInfor {
    String x1 = "x1"; //start
    String y1 = "y1"; //start
    String x2 = "x2"; //middle
    String y2 = "y2"; //middle
    String x3 = "x3"; //end
    String y3 = "y3"; //end
    
    String length = "l"; //length, >0
    String theta = "t"; //thata [0,2pi]
    
    
    String equation1 = "x1+x3 == 2x2";
    String equation2 = "y1+y3 == 2y2";
    String equation3 = "l == Sqrt((x3-x1)^2+(y3-y1)^2)";
    
//    String equation4 = "t == arctan((y3-y1)/(x3-x1))";
//    String equation5 = "t == arcsin((y3-y1)/l)";
//    String equation6 = "t == arccos((x3-x1)/l)";
    
    String equation4 = "tan(t) == (y3-y1)/(x3-x1)";
    String equation5 = "sin(t) == (y3-y1)/l";
    String equation6 = "cos(t) == (x3-x1)/l";
    
    
    Set<Set<String>> solutionSets = new HashSet<>();
    
    static{
        Set<String> ss1 = new HashSet<>();
        
        
    }
    
    
    
}
