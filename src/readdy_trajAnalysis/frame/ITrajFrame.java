/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package readdy_trajAnalysis.frame;

import java.util.ArrayList;
import readdy.api.io.out.IDataReadyForOutput;
import readdy.api.sim.core.particle.IParticle;

/**
 *
 * @author johannesschoeneberg
 */
public interface ITrajFrame {
    int getStepId();
    
    ArrayList<IParticle> getParticles();

    public IDataReadyForOutput getDataReadyForOutput();

    

}
