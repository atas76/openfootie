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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Config {
    
    /**
     * Get the value of a configuration variable
     * @param line  A configuration file line
     * @param varName The configuration variable searched for
     * @return The value of the configuration variable if it matches the variable in the current file line, null otherwise
     */
    private static String getConfigValue(String line, String varName) {
        
        String retVal = null;
        
        if (line.indexOf("=") != -1) {
            
            String [] equation = line.split("=");
            
            String lValue = equation[0].trim();
            String rValue = line.substring(line.indexOf("=") + 1).trim();
                
            if (varName.equals(lValue)) {
                retVal = rValue;
            }
        }
        
        return retVal;
        
    }
    
    public static String readConfig(String key) {
        
        String value = null;

        try {
          
            BufferedReader in = new BufferedReader(new FileReader("config.txt"));  
            
            String strLine;
            while ((strLine = in.readLine()) != null) {
                value = getConfigValue(strLine, key);
                if (value != null) break;
            }

            // dispose all the resources after using them.
            in.close();
            
            return value;

        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        
        return null;
        
    }

}
