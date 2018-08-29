package ct.af.utils;

import ct.af.resourceModel.ResourceNeIdRouting;
import ec02.af.utils.AFLog;

import java.util.List;

public class NETypeMapper
{
    public static ResourceNeIdRouting search(String neType, long paramValue)
    {
        if(Config.getResourceNeIdRoutingMap().containsKey(neType))
        {
            List<ResourceNeIdRouting> neRoutingList = Config.getResourceNeIdRoutingMap().get(neType);
            for (ResourceNeIdRouting neRouting : neRoutingList)
            {
                if(paramValue >= Long.parseLong(neRouting.getStart()) && paramValue <= Long.parseLong(neRouting.getEnd()))
                {
                    AFLog.d("ResourceNeIdRouting found: " + neRouting.toString());
                    return neRouting;
                }
            }

            /* find for default */
            for(ResourceNeIdRouting neRouting : neRoutingList)
            {
                if(Long.parseLong(neRouting.getStart()) == 0L && Long.parseLong(neRouting.getEnd()) == 0L)
                {
                    AFLog.d("ResourceNeIdRouting found default: " + neRouting.toString());
                    return neRouting;
                }
            }
            // CASE: paramValue not in range after searching
            AFLog.e("ResourceNeIdRouting default value not found. (neType: " + neType + " paramValue: " + Long.toString(paramValue) + ")");
            return new ResourceNeIdRouting("Error","Error");
        }
        else
        {
            // CASE: neType is not in NERouting
            /*if(!afSubIns.getBso_smessage().contains("NEType Configuration cannot be found in NERouting."))
            {
                if(afSubIns.getTask_smessage().equals(""))
                {
                    afSubIns.setTask_smessage("Configuration cannot be found in NERouting.");
                }
                else
                {
                    afSubIns.setTask_smessage(afSubIns.getTask_smessage() + "; Configuration cannot be found in NERouting.");
                }

                afSubIns.setBso_smessage(afSubIns.getBso_smessage() + "; Configuration cannot be found in NERouting.");
            }*/
            AFLog.e("ResourceNeIdRouting not found. (neType: " + neType + " paramValue: " + Long.toString(paramValue) + ")");
            return new ResourceNeIdRouting("Error","Error");
        }
    }
}
