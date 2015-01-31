/* Copyright 2010 Santiago Ontanon and Ashwin Ram */
package d2.execution.planbase;

import gatech.mmpm.GameState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author santi
 */
public class GameStateSimilarity {

    /*
     * This function computes a generic similarity metric among game states. It
     * optionally accepts a list of weights as a HashMap that maps "feature
     * name" to "weight", and to other statistics of the features such as min and max values. 
     * If no weights are passed, then they are assumed to be uniform.
     *
     * Each feature is identified by a String in the HashMap.
     */
    public static double gameStateSimilarity(GameStateFeatures gs1, GameStateFeatures gs2, HashMap<String, FeatureStats> weights) {
        ArrayList<Double> featureValue1 = new ArrayList<Double>(), featureValue2 = new ArrayList<Double>();
        ArrayList<Double> featureWeight = new ArrayList<Double>();

        Set<String> featureNames = new HashSet<String>();
        featureNames.addAll(gs1.getFeatureNames());
        featureNames.addAll(gs2.getFeatureNames());

        double accum = 0;
        double accumWeight = 0;
        double totalFeatures = 0;

        for (String f : featureNames) {
            Double v1 = gs1.getFeature(f);
            Double v2 = gs2.getFeature(f);
            FeatureStats s = null;            
            if (v1!=null && v2!=null) {                
                if (weights != null) s = weights.get(f);
                if (s!=null) {
                    s.update(v1);
                    s.update(v2);
                    // If we have the feature statistics, use mean square error:
                    double range = s.max - s.min;
                    double v1_tmp = (v1 - s.min);
                    double v2_tmp = (v2 - s.min);
                    if (range>0) {
                        v1_tmp /= range;
                        v2_tmp /= range;
                    } else {
                        v1_tmp = 0;
                        v2_tmp = 0;
                    }
                    accum += (v1_tmp - v2_tmp) * (v1_tmp - v2_tmp)*s.weight;
                    accumWeight += s.weight;
                    totalFeatures += 1;
                } else {
                    // Otherwise, use whatever we can compute :)
                    if (((double)v1)!=((double)v2)) accum += 1;
//                    System.out.println(f + ": " + v1 + " == " + v2 + " : " + (((double)v1)==((double)v2)));
                    accumWeight += 1;
                    totalFeatures += 1;
                }
            }
        }
        
//        System.out.println(accum);

        if (accumWeight>0) {
            accum /= accumWeight;       // we average according to the weights
            accum *= totalFeatures;     // we make the weights add up to "totalFeatures"
        } else {    
            accum = 0;
        }
        
//        accum = Math.sqrt(accum) / Math.sqrt(totalFeatures);    // euclidean distance, and then normalized between 0 and 1
        accum = accum / totalFeatures;    // euclidean distance, and then normalized between 0 and 1
        
//        System.out.println("Sym: " + (1 - accum) + " totalFeatures: " + totalFeatures + " accumWeight: " + accumWeight);
        
        return 1 - accum;
    }
    

    public static double gameStateSimilarity(GameState rgs1, GameState rgs2, int cycle1, int cycle2, String player1, String player2, HashMap<String, FeatureStats> weights) {
        GameStateFeatures gs1 = (GameStateFeatures) rgs1.getMetaData("GameStateFeatures");
        if (gs1 == null) {
            gs1 = new GameStateFeatures(cycle1, rgs1, player1);
            rgs1.addMetaData("GameStateFeatures", gs1);
        }
        GameStateFeatures gs2 = (GameStateFeatures) rgs2.getMetaData("GameStateFeatures");
        if (gs2 == null) {
            gs2 = new GameStateFeatures(cycle2, rgs2, player2);
            rgs2.addMetaData("GameStateFeatures", gs2);
        }

        return gameStateSimilarity(gs1, gs2, weights);
    }
}
