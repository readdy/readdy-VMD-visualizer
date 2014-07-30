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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import readdy.api.io.in.par_global.IGlobalParameters;
import readdy.api.io.in.tpl_coord.ITplgyCoordinatesFileData;
import readdy.api.io.in.tpl_coord.ITplgyCoordinatesFileParser;
import readdy.api.sim.core.particle.IParticleParameters;
import readdy_trajAnalysis.frame.ITrajFrame;

/**
 *
 * @author schoeneberg
 */
public class TrajFileParser implements ITrajFileParser  {

    ITplgyCoordinatesFileData coordinatesFileData = null;
    HashMap<Integer,ITrajFrame> traj = null;
    //ArrayList<ITrajFrame> traj = null;
    
    
    public TrajFileParser(){
        
    }

    @Override
    public void parse(String filename) {
        try {

            XMLReader parser = XMLReaderFactory.createXMLReader();
            TrajFileXMLHandler contentHandler = new TrajFileXMLHandler();
            parser.setContentHandler(contentHandler);


            // start parsing
            File inputFile = new File(filename);
            System.out.println("parsing trajectory file: " + inputFile.getAbsolutePath());
            
            parser.parse(filename);
            
            traj = contentHandler.getParsedTraj();
            System.out.println("... done.");

        } catch (SAXException ex) {
            Logger.getLogger(TrajFileParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TrajFileParser.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    @Override
    public ITplgyCoordinatesFileData get_coodinatesFileData() {
        return coordinatesFileData;
    }

    @Override
    public HashMap<Integer,ITrajFrame> getTraj() {
        return traj;
    }
}
