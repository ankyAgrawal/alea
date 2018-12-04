/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xklusac.algorithms.schedule_based;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import xklusac.algorithms.SchedulingPolicy;
import xklusac.environment.ComplexResourceCharacteristics;
import xklusac.environment.GridletInfo;
import xklusac.environment.ResourceInfo;
import xklusac.environment.Scheduler;
import xklusac.extensions.LengthComparator;

/**
 *
 * @author ankit
 */
public class iHlba implements SchedulingPolicy {
    private Scheduler scheduler;

    public iHlba(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    @Override
    public void addNewJob(GridletInfo gi) {
        double runtime1 = new Date().getTime();
        Scheduler.queue.addLast(gi);
        Collections.sort(Scheduler.queue, new LengthComparator());
        Scheduler.runtime += (new Date().getTime() - runtime1);
        System.out.println("New job has been received by iHlba scheduler");
    }
    
        public boolean canExecuteNowiHlba(GridletInfo gi, ResourceInfo ri){
        int required = gi.getNumPE();
        int available = ri.resource.getNumFreePE();
        
        // check the resource usage
        double utilization = ri.resource.getNumBusyPE()/ri.resource.getNumPE();
        if(required < available && utilization<0.6)
            return true;
        else
            return false;
    }
        
    
    private ResourceInfo getRes(HashMap<ResourceInfo,Integer> res_comPower,GridletInfo gi){
        ResourceInfo max_acp_resource = null;
        int max_acp=0;
        for(Map.Entry<ResourceInfo, Integer> entry : res_comPower.entrySet()){
                if(entry.getValue()>max_acp){
                    System.out.println(" max Cp of cluster is " + entry.getValue());
                    max_acp=entry.getValue();
                    max_acp_resource = entry.getKey();
                }
        }
        return max_acp_resource;
//        if(max_acp_resource==null)
//            return null;
//        if(canExecuteNowiHlba(gi,max_acp_resource))
//            return max_acp_resource;
//        else{
//            res_comPower.remove(max_acp_resource);
//            getRes(res_comPower,gi);
//        }
//        return null;
    }
        
    @Override
    public int selectJob() {
        int scheduled = 0;
        ResourceInfo r_cand = null;
        for (int i = 0; i < Scheduler.queue.size(); i++) {
            GridletInfo gi = (GridletInfo) Scheduler.queue.get(i);
            // select the resource for this gridlet 
            
            double max_acp = 0;
            ResourceInfo max_acp_resource = null;
           HashMap<ResourceInfo,Integer> res_comPower = new HashMap<ResourceInfo, Integer>();
            for (int j = 0; j < Scheduler.resourceInfoList.size(); j++) {
              // calculate average computing power of each resource
              // and select the one with highest computing power
              
              
              for(int m = 0; m < Scheduler.resourceInfoList.size(); m++){
                  ResourceInfo ri = (ResourceInfo)Scheduler.resourceInfoList.get(m);
              int acp = ri.calculate_avg_computingPower();
              
              System.out.println("avg_computingPower is "+acp);
               // find the resource with max computing power
              //res_comPower.put(Integer.toString(ri.resource.getResourceID()),Integer.toString(acp));
              res_comPower.put(ri,acp);
              }
              
//              if(acp>max_acp){
//                max_acp=acp;
//                max_acp_resource=ri;
//              } 
            
            max_acp_resource= getRes(res_comPower,gi);
//            if(max_acp_resource==null)
//                System.out.println("MAx Acp resource is null");
//            else{
//                for (int k = 0; k < Scheduler.resourceInfoList.size(); k++) {
//                if (Scheduler.isSuitable(max_acp_resource, gi) && max_acp_resource.canExecuteNow(gi)) {
//
//                    r_cand = max_acp_resource;
//                    break;
//                }
//            }
//            }
            
             
            //&& Scheduler.isSuitable(max_acp_resource, gi) && max_acp_resource.canExecuteNow(gi)
            if (max_acp_resource != null && max_acp_resource.canExecuteNow(gi)) {
                gi = (GridletInfo) Scheduler.queue.remove(i);
                //System.err.println(gi.getID()+" PEs size = "+gi.PEs.size());
                max_acp_resource.addGInfoInExec(gi);
                // set the resource ID for this gridletInfo (this is the final scheduling decision)
                gi.setResourceID(max_acp_resource.resource.getResourceID());
                scheduler.submitJob(gi.getGridlet(), max_acp_resource.resource.getResourceID());
                max_acp_resource.is_ready = true;
                //scheduler.sim_schedule(GridSim.getEntityId("Alea_3.0_scheduler"), 0.0, AleaSimTags.GRIDLET_SENT, gi);
                scheduled++;
                max_acp_resource = null;
                i--;
                return scheduled;
            } else {
                return scheduled;
            }
        }
    }
         return scheduled;
        }

        
    }

