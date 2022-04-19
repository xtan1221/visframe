package dependency.vccl.utils.helper;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author tanxu
 *
 * @param <V>
 */
public class CartesianProductUtils {

	/**
	 * build and return the list of list for all possible combinations of the given list
	 * 
	 * for example, given lists is a list of 
	 * list1: 1,2,3
	 * list2: 2,3
	 * list3: 1,2
	 * 
	 * the returned list contains 12=(3*2*2) lists:
	 * 
	 * 1,2,1
	 * 1,2,2
	 * 1,3,1
	 * 1,3,2
	 * 221
	 * 222
	 * 231
	 * 232
	 * 321
	 * 322
	 * 331
	 * 332
	 * 
	 * each with a different combinations of elements from each of the list in the given lists;
	 * 
	 * 
	 * @param <V>
	 * @param lists
	 * @return
	 */
	public static <V> List<List<V>> cartesianProduct(List<List<V>> lists){
		return cartesianProduct(0,lists);
	}
	
	/**
	 * recursively
	 * @param <V>
	 * @param i
	 * @param a
	 * @return
	 */
    private static <V> List<List<V>> cartesianProduct(int i, List<List<V>> a) {
        if(i == a.size() ) {
            List<List<V>> result = new ArrayList<>();
            result.add(new ArrayList<>());
            return result;
        }
        
        List<List<V>> next = cartesianProduct(i+1, a);
        List<List<V>> result = new ArrayList<>();
        for(int j=0; j < a.get(i).size(); j++) {
            for(int k=0; k < next.size(); k++) {
                List<V> concat = new ArrayList<>();
                concat.add(a.get(i).get(j));
                concat.addAll(next.get(k));
                result.add(concat);
            }
        }
        return result;
    }
    
    @SuppressWarnings("serial")
	public static void main(String[] args) {
    	List<Integer> l1 = new ArrayList<>(){{ add(1); add(2); add(3); }};
    	List<Integer> l2 = new ArrayList<>(){{ add(2); add(3);}};
    	List<Integer> l3 = new ArrayList<>(){{ add(1); add(2); }};
    	List<Integer> l4 = new ArrayList<>(){{ add(1); add(2); add(3); }};
    	
    	
    	List<List<Integer>> lists = new ArrayList<>()
    	{
    		{ 
    			add(l1); 
    			add(l2); 
    			add(l3); 
    			add(l4); 
    			}
    		};
    	
    	List<List<Integer>> combinationList = cartesianProduct(lists);
    	
    	System.out.println(combinationList.size());
    	System.out.println("==============");
    	combinationList.forEach(l->{
    		l.forEach(e->{
    			System.out.print(e);
    		});
    		
    		System.out.println();
    	});
    	
    }
}
