package ct.af.debug;

import java.io.IOException;

public class TestDebug {
    public static void main(String[] args) throws IOException
    {
        CTTest af = CTTest.getInstance();

		/*run(String testName,
        String configFile,
        String requestMsgFile,
        String homeDirectory,
        String libraryDirectory)*/
   //hello

        //af.run("PGZ_UAT", "src/test/resources/conf/PGZ_UAT.EC02.SER.0", "src/test/resources/dummymsgs/test_flow_promotionmanagement.xml", "src/test/resources/", "repo/co/th/PGZ/1.6.0");

        af.run("PGZ_UAT", "src/test/resources/conf/PGZ_UAT.EC02.SER.0", "src/test/resources/dummymsgs/test_flow_billingSubscription.xml", "src/test/resources/", "repo/co/th/PGZ/1.6.0");



    }
}
