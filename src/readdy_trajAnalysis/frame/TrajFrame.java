/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package readdy_trajAnalysis.frame;

import java.util.ArrayList;
import readdy.api.io.out.IDataReadyForOutput;
import readdy.api.sim.core.particle.IParticle;
import readdy.impl.io.out.DataReadyForOutput;

/**
 *
 * @author johannesschoeneberg
 */
public class TrajFrame implements ITrajFrame{
    int stepId;
    ArrayList<IParticle> particles;
    
    public TrajFrame(int stepId, ArrayList<IParticle> particles){
        this.stepId = stepId;
        this.particles = particles;
    }
    
    @Override
    public int getStepId() {
        return stepId;
    }

    @Override
    public ArrayList<IParticle> getParticles() {
        return particles;
    }

    @Override
    public IDataReadyForOutput getDataReadyForOutput() {
        IDataReadyForOutput data = new DataReadyForOutput();
        
        ArrayList<ArrayList<String>> dataReadyForOutput= new ArrayList();
        for(IParticle p: particles){
            ArrayList<String> list = new ArrayList();
            list.add(p.get_id()+"");
            list.add(p.get_type()+"");
            list.add(p.get_coords()[0]+"");
            list.add(p.get_coords()[1]+"");
            list.add(p.get_coords()[2]+"");
            dataReadyForOutput.add(list);
        }
        data.set_data(dataReadyForOutput);
        return data;
    }
    

}
