/*===========================================================================*\
 *           ReaDDy - The Library for Reaction Diffusion Dynamics              *
 * =========================================================================== *
 * Copyright (c) 2010-2013, Johannes Schöneberg, Frank Noé, FU Berlin          *
 * All rights reserved.                                                        *
 *                                                                             *
 * Redistribution and use in source and binary forms, with or without          *
 * modification, are permitted provided that the following conditions are met: *
 *                                                                             *
 *     * Redistributions of source code must retain the above copyright        *
 *       notice, this list of conditions and the following disclaimer.         *
 *     * Redistributions in binary form must reproduce the above copyright     *
 *       notice, this list of conditions and the following disclaimer in the   *
 *       documentation and/or other materials provided with the distribution.  *
 *     * Neither the name of Johannes Schöneberg or Frank Noé or the FU Berlin *
 *       nor the names of its contributors may be used to endorse or promote   *
 *       products derived from this software without specific prior written    *
 *       permission.                                                           *
 *                                                                             *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" *
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE   *
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE  *
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE   *
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR         *
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF        *
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS    *
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN     *
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)     *
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE  *
 * POSSIBILITY OF SUCH DAMAGE.                                                 *
 *                                                                             *
 \*===========================================================================*/
package readdy_trajAnalysis.trajParser;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import readdy.api.assembly.IParticleConfigurationFactory;
import readdy.api.io.in.par_global.IGlobalParameters;
import readdy.api.io.in.tpl_coord.ITplgyCoordinatesFileData;
import readdy.api.io.in.tpl_coord.ITplgyCoordinatesFileDataEntry;
import readdy.api.io.in.tpl_coord.ITplgyCoordinatesFileXMLHandler;
import readdy.api.sim.core.config.IParticleConfiguration;
import readdy.api.sim.core.particle.IParticle;
import readdy.api.sim.core.particle.IParticleParameters;
import readdy.impl.assembly.ParticleConfigurationFactory;
import readdy.impl.io.in.tpl_coord.TplgyCoordinatesFileData;
import readdy.impl.io.in.tpl_coord.TplgyCoordinatesFileDataEntry;
import readdy.impl.sim.core.config.ParticleConfiguration;
import readdy.impl.sim.core.particle.Particle;
import readdy.impl.tools.StringTools;
import readdy_trajAnalysis.frame.ITrajFrame;
import readdy_trajAnalysis.frame.TrajFrame;

/**
 *
 * @author schoeneberg
 */
class TrajFileXMLHandler implements ITrajFileXMLHandler {

    
    
    static final int[] version = new int[]{1, 1};
    
    StringBuffer accumulator = new StringBuffer();  // Accumulate parsed text inside of a tag
    
    ITplgyCoordinatesFileDataEntry coordFileDataEntry = null;
    IParticle particle = null;
    ArrayList<ITplgyCoordinatesFileDataEntry> coordFileDataEntryList = new ArrayList();
    
    HashMap<Integer,ITrajFrame> traj = new HashMap();
    int currentStepId = -1;
    ArrayList<IParticle> particlesInFrame = new ArrayList();
    
    boolean documentFullyParsed = false;
    
    public HashMap<Integer,ITrajFrame> getParsedTraj(){
        return traj;
    }

    private void verifyVersionNumber(String docVersion) {
        boolean versionMatch = true;
        String[] strArr_docVersion = docVersion.split("\\.");
        if (version.length == strArr_docVersion.length) {
            for (int i = 0; i < strArr_docVersion.length; i++) {
                String s = strArr_docVersion[i];
                int versionSubnumber = Integer.parseInt(s);
                if (versionSubnumber != version[i]) {
                    versionMatch = false;
                }
            }
        } else {
            versionMatch = false;
        }
        if (!versionMatch) {
            throw new RuntimeException("version mismatch: inputVersion = " + docVersion + " requestedVersion = " + version[0] + "." + version[1]);
        }
    }

    public TrajFileXMLHandler() {
    }

    

    @Override
    public void setDocumentLocator(Locator lctr) {
    }

    @Override
    public void startDocument() throws SAXException {
        documentFullyParsed = false;
        
    }

    @Override
    public void endDocument() throws SAXException {         
        documentFullyParsed = true;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        accumulator.setLength(0);
        //System.out.println(localName);

        
            if (localName.equals("tplgy_coords")) {
                //System.out.println("tplgyCoords");
                String docVersion = "";
                if (atts != null) {
                    int nAtts = atts.getLength();
                    for (int i = 0; i < nAtts; i++) {
                        if (atts.getLocalName(i).equals("version")) {
                            docVersion = atts.getValue(i);
                        }
                        if (atts.getLocalName(i).equals("stepId")){
                            currentStepId = Integer.parseInt(atts.getValue(i));
                            System.out.println("stepId: "+currentStepId);
                        }
                    }
                }
                verifyVersionNumber(docVersion);
                // clear the list of all coordinates
                coordFileDataEntryList = new ArrayList();
                particlesInFrame = new ArrayList();
                
            }
            
            if (localName.equals("p")) {
            coordFileDataEntry = new TplgyCoordinatesFileDataEntry();
            
            if (atts != null) {
                int nAtts = atts.getLength();

                for (int i = 0; i < nAtts; i++) {
                    if (atts.getLocalName(i).equals("id")) {
                        if (atts.getValue(i).equals("")) {
                            coordFileDataEntry.set_id(-1);
                        } else {
                            coordFileDataEntry.set_id(Integer.parseInt(atts.getValue(i)));
                        }

                    }

                    if (atts.getLocalName(i).equals("type")) {
                        coordFileDataEntry.set_type(Integer.parseInt(atts.getValue(i)));
                    }

                    if (atts.getLocalName(i).equals("c")) {
                        double[] currentCoordsD = StringTools.splitArrayString_convertToDouble(atts.getValue(i));
                        coordFileDataEntry.set_c(currentCoordsD);
                    }
                }

            }
            particle = new Particle(coordFileDataEntry.get_id(),coordFileDataEntry.get_type(),coordFileDataEntry.get_c());
            //coordFileDataEntry.print();
        }
        
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("tplgy_coords")) {
            // we have parsed now one frame. Build a particle configuration out of that.
            for(ITplgyCoordinatesFileDataEntry entry: coordFileDataEntryList){
                //entry.print();
            }
            
               
        }
        
        if (localName.equals("p")) {
            coordFileDataEntryList.add(coordFileDataEntry);
            particlesInFrame.add(particle);
        }
        
        if (localName.equals("tplgy_coords")){
            traj.put(currentStepId,new TrajFrame(currentStepId,particlesInFrame));
        }
        
        
    }

    @Override
    public void characters(char[] buffer, int start, int length) throws SAXException {
        accumulator.append(buffer, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ITrajFileXMLHandler get_nextFrame() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
