/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package readdy_trajAnalysis.trajParser;

import java.util.HashMap;
import readdy.api.io.in.tpl_coord.ITplgyCoordinatesFileData;
import readdy_trajAnalysis.frame.ITrajFrame;

/**
 *
 * @author johannesschoeneberg
 */
public interface ITrajFileParser {

    HashMap<Integer, ITrajFrame> getTraj();

    ITplgyCoordinatesFileData get_coodinatesFileData();

    void parse(String filename);

}
