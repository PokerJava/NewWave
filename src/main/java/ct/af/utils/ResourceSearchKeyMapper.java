package ct.af.utils;

import ct.af.instance.AFSubInstance;
import ct.af.resourceModel.ResourceSearchKeyNode;
import ec02.af.utils.AFLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ResourceSearchKeyMapper
{
    public static String search(String[] input, AFSubInstance afSubIns)
    {
        HashMap<String, ResourceSearchKeyNode> resourceIndexMap = Config.getResourceIndexMap();
        ArrayList<String> subSearchKey = afSubIns.getSubSearchKey();
        subSearchKey.clear();
        ResourceSearchKeyNode deepNode = null;
        for (int keyIndex = 0; keyIndex <= input.length; keyIndex++)
        {
            if (keyIndex == 0)
            // if this is root node (first section of keySearch)
            {
                if (resourceIndexMap.containsKey(input[0]))
                {
                    deepNode = resourceIndexMap.get(input[0]);
                    // root node found
                    subSearchKey.add(input[0]);

                }
                else
                {
                    AFLog.e("[ResourceSearchKeyMapper] ERROR: Node not found for " + Arrays.toString(input) + " step: " + keyIndex);
                    return null;

                }
            }
            else if (keyIndex > 0 && keyIndex != input.length)
            {
                // between node
                //check null because SonarQ concern "A "NullPointerException" could be thrown; "deepNode" is nullable here"
                if(deepNode!=null && deepNode.isChild())
                {
                	//CASE input more than node tree
                    AFLog.e("[ResourceSearchKeyMapper] ERROR: Node not found for " + Arrays.toString(input) + " step: " + keyIndex);
                    return null;
                }
                else
                {
                    HashMap<String, ResourceSearchKeyNode> deepNodeChildren = new HashMap<>();
                    if(deepNode!=null && deepNode.getNodesValue()!=null){
                    	deepNodeChildren = deepNode.getNodesValue();
                    }
                    if(input[keyIndex].equals("") && deepNodeChildren!=null && deepNodeChildren.containsKey("null"))
                    {
                        // null
                        deepNode = deepNodeChildren.get("null");
                        subSearchKey.add("null");
                    }
                    else if(deepNodeChildren!=null && deepNodeChildren.containsKey(input[keyIndex]))
                    {
                        deepNode = deepNodeChildren.get(input[keyIndex]);
                        subSearchKey.add(input[keyIndex]);
                    }
                    else if(deepNodeChildren!=null && deepNodeChildren.containsKey("def"))
                    {
                        // def
                        deepNode = deepNodeChildren.get("def");
                        subSearchKey.add("def");
                    }
                    else
                    {
                        AFLog.e("[ResourceSearchKeyMapper] ERROR: Node not found for " + Arrays.toString(input) + " step: " + keyIndex);
                        return null;
                    }
                }
            }
            else
            {
                // last key searching
                //check null because SonarQ concern "A "NullPointerException" could be thrown; "deepNode" is nullable here"
                if(deepNode!=null && deepNode.isChild())
                {
                    AFLog.d("[ResourceSearchKeyMapper] Result: " + deepNode.getStringValue());
                    return deepNode.getStringValue();
                }
                else
                {
                	//searchkey config more than input searchkey
                    AFLog.e("[ResourceSearchKeyMapper] ERROR: Node not found for " + Arrays.toString(input) + " step: " + keyIndex);
                    return null;
                }
            }
        }
        AFLog.e("[ResourceSearchKeyMapper] ERROR: Unexpected end of code.");
        return null;
    }
}
