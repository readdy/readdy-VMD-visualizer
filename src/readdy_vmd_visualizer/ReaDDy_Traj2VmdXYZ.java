/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package readdy_vmd_visualizer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import readdy.api.io.in.par_particle.IParamParticlesFileData;
import readdy.api.io.in.par_particle.IParamParticlesFileParser;
import readdy.api.io.in.par_particle.IParticleData;
import readdy.api.io.out.IDataReadyForOutput;
import readdy.api.sim.core.particle.IParticle;
import readdy.impl.io.in.par_particle.ParamParticlesFileParser;
import readdy.impl.io.out.DataReadyForOutput;
import readdy.impl.io.out.XYZ_Writer;
import readdy.impl.sim.core.particle.Particle;
import readdy_vmd_visualizer.vmdTclWriter.VmdTclWriter;
import readdy_trajAnalysis.frame.ITrajFrame;
import readdy_trajAnalysis.trajParser.ITrajFileParser;
import readdy_trajAnalysis.trajParser.TrajFileParser;

/**
 *
 * @author johannesschoeneberg
 */
public class ReaDDy_Traj2VmdXYZ {

    // these are default values that get overwritten during the processing of the script
    static double[] cloakAndDummyParticleCoords = new double[]{0, 0, -100};
    static int invisibilityCloakParticleTypId;
    static double invisibilityCloakParticle_defaultCollisionRadius = 10.;
    
    // generate a cloak particle with type id -1
    private static IParticle dummyParticle = new Particle(-1, -1, cloakAndDummyParticleCoords);
    private static IDataReadyForOutput dataReadyForOutput;
    private static XYZ_Writer xyzWriter = new XYZ_Writer();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String inputPath = "";
        String paramParticlesPath = "";
        if (args.length == 0) {
            System.out.println("please specify input ReaDDy output trajectory ReaDDy_Traj2VmdXYZ <$Path>/<filename>.xml");
        } else {
            inputPath = args[0];
            paramParticlesPath = args[1];
            String invisibilityCloakParameters = args[2];
            String[] chunks = invisibilityCloakParameters.split(",");
            double invisi_x = Double.parseDouble(chunks[0]);
            double invisi_y = Double.parseDouble(chunks[1]);
            double invisi_z = Double.parseDouble(chunks[2]);
            cloakAndDummyParticleCoords = new double[]{invisi_x,invisi_y,invisi_z};
            dummyParticle = new Particle(-1, -1, cloakAndDummyParticleCoords);
            
            System.out.println("input trajPath:" + inputPath);
            System.out.println("input paramParticlesPath:" + inputPath);
        }

        String outputFileName_traj = inputPath +  ".VMD.xyz";
        String outputFileName_vmdTcl = inputPath +  ".VMD.tcl";

        ArrayList<ArrayList<String>> doc = new ArrayList();
        doc.clear();


        /* this is the new solution for properly written output files.
         
         like 
         * <traj>
         * <frame1/>
         * <frame2/>
         * </traj>

         */

        // if the simulation was not completed before, we have to close the <traj> tag in the traj file.
        // ############################################################################################

       
        try {

           
            BufferedReader br = new BufferedReader(new FileReader(inputPath));

            // walk to end of file
            String currentLine = br.readLine();
            String lineBefore = "";
            while (currentLine != null) {
                lineBefore = currentLine;
                currentLine=br.readLine();
            }
            // parse last line
            if(!"</traj>".equals(lineBefore)){
                br.close();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputPath, true))) {
                    bw.write("</traj>");
                }
           }


        } catch (IOException e) {
        }
            // ############################################################################################

            System.out.println("parse tplgyCoordinatesFile");
            ITrajFileParser trajFileParser = new TrajFileParser();
            //try{

            trajFileParser.parse(inputPath);
            //}catch(SAXParseException e){


            HashMap<Integer, ITrajFrame> traj = trajFileParser.getTraj();


            System.out.println("did it work?");
            System.out.println("nSteps = " + traj.size());

            xyzWriter.open(outputFileName_traj);


            // get only the G protein coordinates for each step

            HashMap<Integer, Integer> particleTypId2maxNumberOfParticles = new HashMap();
            HashMap<Integer, Integer> current_particleTypId2maxNumberOfParticles = new HashMap();


            List<Integer> orderedListOfAllStepIDs = new ArrayList<Integer>();
            // ############################################################################
            // evaluate the number of dummy particles
            // ############################################################################
            for (Integer stepId : traj.keySet()) {
                orderedListOfAllStepIDs.add(stepId);
                ITrajFrame frame = traj.get(stepId);
                current_particleTypId2maxNumberOfParticles.clear();
                for (IParticle p : frame.getParticles()) {
                    if (!current_particleTypId2maxNumberOfParticles.containsKey(p.get_type())) {
                        current_particleTypId2maxNumberOfParticles.put(p.get_type(), 0);
                    }
                    current_particleTypId2maxNumberOfParticles.put(p.get_type(), current_particleTypId2maxNumberOfParticles.get(p.get_type()) + 1);

                }
                for (int particleTypeId : current_particleTypId2maxNumberOfParticles.keySet()) {
                    if (!particleTypId2maxNumberOfParticles.containsKey(particleTypeId)) {
                        particleTypId2maxNumberOfParticles.put(particleTypeId, 0);
                    }
                    if (particleTypId2maxNumberOfParticles.get(particleTypeId) < current_particleTypId2maxNumberOfParticles.get(particleTypeId)) {
                        particleTypId2maxNumberOfParticles.put(particleTypeId, current_particleTypId2maxNumberOfParticles.get(particleTypeId));
                    }
                    // monitor the changes
                    System.out.print(" | pTypeID: " + particleTypeId + ", " + particleTypId2maxNumberOfParticles.get(particleTypeId));
                }
                System.out.println();
            }

            //####################################################################################
            // write a VMD tcl template script
            //
            //
            //####################################################################################
            ArrayList<Integer> particleTypeIdListVMD = new ArrayList(particleTypId2maxNumberOfParticles.keySet());
            Collections.sort(particleTypeIdListVMD);
            
            // first, we are going to fill this list with particle radii
            ArrayList<Double> particleDefaultCollisionRadiusList = new ArrayList();
            
            // get the radii of the particles
            IParamParticlesFileParser paramParticlesFileParser = new ParamParticlesFileParser();
            paramParticlesFileParser.parse(paramParticlesPath);
            IParamParticlesFileData paramParticlesFileData = paramParticlesFileParser.get_paramParticlesFileData();
            ArrayList<IParticleData> dataList = paramParticlesFileData.get_particleDataList();
            double maxCollisionRadiusDetected = 0;
            for(int particleTypeId : particleTypeIdListVMD){
            for(IParticleData particleData : dataList){
                if(particleData.getTypeId()==particleTypeId){
                    particleDefaultCollisionRadiusList.add(particleData.get_defaultCollR());
                    if (particleData.get_defaultCollR() > maxCollisionRadiusDetected){
                        maxCollisionRadiusDetected = particleData.get_defaultCollR();
                    }
                }
            }
            }
            
            // finally, append to the list the dummy particle typeID and Radius:
            invisibilityCloakParticleTypId = particleTypeIdListVMD.get(particleTypeIdListVMD.size()-1)+100;
            particleTypeIdListVMD.add(invisibilityCloakParticleTypId);
            invisibilityCloakParticle_defaultCollisionRadius = 1.1 * maxCollisionRadiusDetected;
            particleDefaultCollisionRadiusList.add(invisibilityCloakParticle_defaultCollisionRadius);

            // hand both lists, the typeIDs and the radii to the vmd tcl script writer
            VmdTclWriter vmdTclWriter = new VmdTclWriter();
            vmdTclWriter.open(outputFileName_vmdTcl,outputFileName_traj);
            vmdTclWriter.writeTclScript(particleTypeIdListVMD,particleDefaultCollisionRadiusList);
            vmdTclWriter.close();
            
            

            // ############################################################################
            // assign the dummy particle lines to actual output stuff.
            // i know i could have done that all that stuff in a single iteration over my 
            // trajectory but we have the resources right ;)
            // ############################################################################
            HashMap<Integer, ArrayList<IParticle>> particleTypeId2setOfParticlesPerFrame = new HashMap();

            Collections.sort(orderedListOfAllStepIDs);
            for (Integer stepId : orderedListOfAllStepIDs) {
                particleTypeId2setOfParticlesPerFrame.clear();
                doc.clear();

                ITrajFrame frame = traj.get(stepId);
                for (IParticle p : frame.getParticles()) {
                    if (!particleTypeId2setOfParticlesPerFrame.containsKey(p.get_type())) {
                        particleTypeId2setOfParticlesPerFrame.put(p.get_type(), new ArrayList<IParticle>());
                        particleTypeId2setOfParticlesPerFrame.get(p.get_type()).add(p);
                    } else {
                        particleTypeId2setOfParticlesPerFrame.get(p.get_type()).add(p);
                    }
                }
                // fill in the lists that might not have been recognized yet because
                // some particle species might occur later in the simulation
                for (int particleTypeId : particleTypId2maxNumberOfParticles.keySet()) {
                    if (!particleTypeId2setOfParticlesPerFrame.containsKey(particleTypeId)) {
                        particleTypeId2setOfParticlesPerFrame.put(particleTypeId, new ArrayList<IParticle>());
                    }
                }

                // write the output file
                ArrayList<Integer> particleTypeIdList = new ArrayList(particleTypeId2setOfParticlesPerFrame.keySet());
                Collections.sort(particleTypeIdList);
                for (int particleTypeId : particleTypeIdList) {


                    // add real particle
                    for (IParticle p : particleTypeId2setOfParticlesPerFrame.get(particleTypeId)) {
                        ArrayList<String> line = new ArrayList();
                        line.add(p.get_id() + "");
                        line.add(p.get_type() + "");
                        for (double c : p.get_coords()) {
                            line.add(c + "");
                        }
                        doc.add(line);
                    }


                    // add dummy particle
                    int nActualParticles = particleTypeId2setOfParticlesPerFrame.get(particleTypeId).size();
                    int nDesiredParticles = particleTypId2maxNumberOfParticles.get(particleTypeId);
                    System.out.println("nDummy Particles: " + (nDesiredParticles - nActualParticles));
                    while (nActualParticles < nDesiredParticles) {
                        ArrayList<String> line = new ArrayList();

                        line.add(dummyParticle + "");
                        line.add(particleTypeId + "");
                        for (double c : dummyParticle.get_coords()) {
                            line.add(c + "");
                        }
                        doc.add(line);
                        nActualParticles++;

                    }

                }

                // print the invisibilityCloak particle
                ArrayList<String> line = new ArrayList();
                IParticle p =  new Particle(-1, invisibilityCloakParticleTypId, cloakAndDummyParticleCoords);
                line.add(p.get_id() + "");
                line.add(p.get_type() + "");
                for (double c : p.get_coords()) {
                    line.add(c + "");
                }

                doc.add(line);
                System.out.println("write step " + stepId + " ...");
                xyzWriter.write(stepId, new DataReadyForOutput(doc));


            }

            /*
             * if(p.get_type()==3){
                    
             //System.out.println(p.get_coords()[0]+","+p.get_coords()[1]+","+p.get_coords()[2]);
             out.write(p.get_coords()[0]+","+p.get_coords()[1]+","+p.get_coords()[2]+"\n");
             }
             */
            xyzWriter.flush();

            xyzWriter.close();
        }
    }
