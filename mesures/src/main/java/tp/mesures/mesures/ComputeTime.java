package tp.mesures.mesures;

import java.util.ArrayList;
import java.util.List;

public class ComputeTime 
{
    private final static int NB = 100;
    private final static int NB_TEST = 1000;

    public static List<String> numbers = new ArrayList<String>(NB);
    public static StringBuilder stringBuilder = null;
    
    public static void main(String[] args) {
    	long[] resultsInNano = new long[NB_TEST];
    	long[] resultsInMillis = new long[NB_TEST];
    	for (int j = 0; j < NB; j++) {
			numbers.add(String.valueOf(j));
		}
		for (int i = 0; i < NB_TEST; i++) {
			stringBuilder= new StringBuilder();
			long startMillis = System.currentTimeMillis();
			long startNano= System.nanoTime();
			for (int j = 0; j < NB; j++) {
				stringBuilder.append(numbers.get(j));
			}
			long endNano= System.nanoTime();
			long endMillis = System.currentTimeMillis();
			resultsInMillis[i]=endMillis-startMillis;
			resultsInNano[i]=endNano-startNano;
		}
		
		for (int i = 0; i < resultsInNano.length; i++) {
			System.out.println(resultsInMillis[i] +" milli --- " + resultsInNano[i] + " nano ");
		}
	}
    
}