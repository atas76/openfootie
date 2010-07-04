/*
 * Copyright 2010 Andreas Tasoulas
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *  
 */

package utility;

/**
 * Some mathematic utility functions. They are used in player evaluation (rating generation)
 * 
 * @author Andreas Tasoulas
 *
 */

public class MathUtil {
    
    public final static double NORMAL_CONFIDENCE = 1.645;
    
    /**
     * Calculate the minimum value of the Wilson score confidence
     * @param calcPerc The percentage of successful attempts
     * @param normalParameter The normal parameter which defines the confidence level
     * @param size The number of total attempts
     * @return Wilson score confidence (min)
     */
    public static double getWilsonScoreConfidenceMin(double calcPerc, double normalParameter, int size) {
        
        return
            (calcPerc + 
            Math.pow(normalParameter, 2) / (2 * size) - 
            normalParameter * Math.sqrt((calcPerc * (1 - calcPerc) + Math.pow(normalParameter, 2) / (4 * size)) / size)) /
                (1 + Math.pow(normalParameter, 2) / size);
    }
    
    /**
     * Calculate the maximum value of the Wilson score confidence
     * @param calcPerc The percentage of successful attempts
     * @param normalParameter The normal parameter which defines the confidence level
     * @param size The number of total attempts
     * @return Wilson score confidence (max)
     */
    public static double getWilsonScoreConfidenceMax(double calcPerc, double normalParameter, int size) {
        return
            (calcPerc + 
            Math.pow(normalParameter, 2) / (2 * size) + 
            normalParameter * Math.sqrt((calcPerc * (1 - calcPerc) + Math.pow(normalParameter, 2) / (4 * size)) / size)) /
                (1 + Math.pow(normalParameter, 2) / size);
    }
    
    /**
     * Calculate the average between minimum and maximum Wilson scores. This is for practical purposes, as we need only one value for the player
     * evaluation and this score in itself does not have any mathematical meaning
     * @param calcPerc The percentage of successful attempts
     * @param normalParameter The normal parameter which defines the confidence level
     * @param size The number of total attempts
     * @return The average between minimum and maximum Wilson confidence scores
     */
    public static double getWilsonScoreConfidence(double calcPerc, double normalParameter, int size) {
        return 
            (getWilsonScoreConfidenceMin(calcPerc, normalParameter, size) + getWilsonScoreConfidenceMax(calcPerc, normalParameter, size)) / 2;
    }
}
