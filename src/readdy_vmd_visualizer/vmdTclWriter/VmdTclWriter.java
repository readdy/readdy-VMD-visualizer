/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package readdy_vmd_visualizer.vmdTclWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author johannesschoeneberg
 */
public class VmdTclWriter {

    private BufferedWriter out;
    private static double weirdVMDRadiusCorrectionFactor = 0.58;

    public void open(String outputFileName_vmdTcl, String trajFilenameToDisplay) {
        Date date = new Date();

        File trajFileToDisplay = new File(trajFilenameToDisplay);


        try {
            out = new BufferedWriter(new FileWriter(outputFileName_vmdTcl));
            out.write("#VMD tcl script generated by ReaDDY by j schoeneberg. Run on " + date);
            out.newLine();
            out.write("                                                  ");
            out.newLine();
            out.write("                                                  ");
            out.newLine();
            out.write("mol delete top                                    ");
            out.newLine();
            out.write("mol load xyz " + trajFileToDisplay.getAbsolutePath() + "");
            out.newLine();
            out.write("                                                  ");
            out.newLine();
            out.write("# delete the automatically generated represenation");
            out.newLine();
            out.write("mol delrep 0 top                                  ");
            out.newLine();
            out.write("display resetview                                 ");
            out.newLine();
            out.write("                                                  ");
            out.newLine();
            out.write("                                                  ");
            out.newLine();
            out.write("                                                  ");
            out.newLine();
            out.write("# vmd color code                                  ");
            out.newLine();
            out.write("#------------------------------------------------ ");
            out.newLine();
            out.write("# 0	  blue                                       ");
            out.newLine();
            out.write("# 1   red                                         ");
            out.newLine();
            out.write("# 2   gray                                        ");
            out.newLine();
            out.write("# 3   orange                                      ");
            out.newLine();
            out.write("# 4   yellow                                      ");
            out.newLine();
            out.write("# 5   tan                                         ");
            out.newLine();
            out.write("# 6   silver                                      ");
            out.newLine();
            out.write("# 7   green                                       ");
            out.newLine();
            out.write("# 8   white                                       ");
            out.newLine();
            out.write("# 9   pink                                        ");
            out.newLine();
            out.write("# 10  cyan                                        ");
            out.newLine();
            out.write("# 11  purple                                      ");
            out.newLine();
            out.write("# 12  lime                                        ");
            out.newLine();
            out.write("# 13  mauve                                       ");
            out.newLine();
            out.write("# 14  ochre                                       ");
            out.newLine();
            out.write("# 15  iceblue                                     ");
            out.newLine();
            out.write("# 16  black                                       ");
            out.newLine();
            out.write("# 17  yellow2                                     ");
            out.newLine();
            out.write("# 18  yellow2                                     ");
            out.newLine();
            out.write("# 19  green2                                      ");
            out.newLine();
            out.write("# 20  green3                                      ");
            out.newLine();
            out.write("# 21  cyan2                                       ");
            out.newLine();
            out.write("# 22  cyan3                                       ");
            out.newLine();
            out.write("# 23  blue2                                       ");
            out.newLine();
            out.write("# 24  blue3                                       ");
            out.newLine();
            out.write("# 25  violet                                      ");
            out.newLine();
            out.write("# 26  violet2                                     ");
            out.newLine();
            out.write("# 27  magenta                                     ");
            out.newLine();
            out.write("# 28  magenta2                                    ");
            out.newLine();
            out.write("# 29  red2                                        ");
            out.newLine();
            out.write("# 30  red3                                        ");
            out.newLine();
            out.write("# 31  orange2                                     ");
            out.newLine();
            out.write("# 32  orange3                                     ");
            out.newLine();
            out.write("#------------------------------------------------ ");
            out.newLine();



        } catch (IOException e) {
        }
    }

    public void writeTclScript(ArrayList<Integer> particleTypeIdList, ArrayList<Double> particleDefaultCollisionRadiusList) {
        if (particleTypeIdList.size() != particleDefaultCollisionRadiusList.size()) {
            try {
                throw new Exception("particleTypeID list provided is not equal in length to the raddi list provided. Abort");
            } catch (Exception ex) {
                Logger.getLogger(VmdTclWriter.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            for (int i = 0; i < particleTypeIdList.size(); i++) {
                int particleTypeId = particleTypeIdList.get(i);
                double particleTypeRadius = particleDefaultCollisionRadiusList.get(i);
                try {
                    out.newLine();
                    out.write("mol representation VDW " + particleTypeRadius * weirdVMDRadiusCorrectionFactor + " 16.000000 ");
                    out.newLine();
                    out.write("mol selection name C_" + particleTypeId);
                    out.newLine();
                    out.write("mol material Opaque");
                    out.newLine();
                    out.write("mol color ColorID " + i);
                    out.newLine();
                    out.write("mol addrep top");
                    out.newLine();
                } catch (IOException e) {
                }
            }
        }
    }

    public void close() {
        try {

            out.newLine();
            out.newLine();
            out.newLine();
            out.newLine();
            out.write("#go to first step of the trajectory        ");
            out.newLine();
            out.write("animate goto 0                             ");
            out.newLine();
            out.write("                                           ");
            out.newLine();
            out.write("                                           ");
            out.newLine();
            out.write("# Axes Off                                 ");
            out.newLine();
            out.write("axes location off                          ");
            out.newLine();
            out.write("                                           ");
            out.newLine();
            out.write("# Orthographic display projection          ");
            out.newLine();
            out.write("display projection Orthographic            ");
            out.newLine();
            out.write("                                           ");
            out.newLine();
            out.write("color Display Background white             ");
            out.newLine();



            out.flush();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(VmdTclWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
