package ct.af.utils;


public class RequestIdUtils
{
    private static int requestIdRunningNo = 1;

    public static synchronized int getAndCountRequestIdRunningNo() {
        int oldRequestIdRunningNo = requestIdRunningNo;
        // Start from 1, when reaches 9999 -> reset to 1 instead of 10000
        if(requestIdRunningNo < 9999) {
            requestIdRunningNo++;
        } else {
            requestIdRunningNo = 1;
        }
        return oldRequestIdRunningNo;
    }
}
