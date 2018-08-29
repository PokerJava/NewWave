package ct.af.utils;

import ec02.af.abstracts.AbstractAF;
import ec02.af.utils.AFLog;

public class StatUtils {
    public static interface PEGAZUS {
        //getProfile
        public static final String pgz_idle_trigger = "Pgz Idle Trigger Req";
        public static final String pgz_sdf_send_GET_resourceinventory_success_req = "Pgz Sdf Send GET ResourceInventory Success Req";
        public static final String pgz_sdf_recv_GET_resourceinventory_success_res = "Pgz Sdf Received GET ResourceInventory Success Res";
        public static final String pgz_client_recv_GET_resourceorder_success_req = "Pgz Client Received GET ResourceOrder Success Req";
        public static final String pgz_client_send_POST_ackreq_success_res = "Pgz Client Send POST AckReq Success Res";
        public static final String pgz_sdf_send_GET_reservequotainfra_success_req = "Pgz Sdf Send GET ReserveQuotaInfra Success Req";
        public static final String pgz_sdf_recv_GET_reservequotainfra_success_res = "Pgz Sdf Received GET ReserveQuotaInfra Success Res";
        public static final String pgz_ne_send_PROCESSING_resourceitem_success_req = "Pgz Sdf Send PROCESSING ResourceItem Success Req";
        public static final String pgz_ne_recv_PROCESSING_resourceitem_success_res = "Pgz Sdf Received PROCESSING ResourceItem Success Res";
        public static final String pgz_md_send_HLR_success_req = "Pgz MD Send HLR Success Req";
        public static final String pgz_md_recv_HLR_success_res = "Pgz MD Received HLR Success Res";
        public static final String pgz_md_send_ZTE_success_req = "Pgz MD Send ZTE Success Req";
        public static final String pgz_md_recv_ZTE_success_res = "Pgz MD Received ZTE Success Res";
        public static final String pgz_sdf_send_POST_commitquotainfra_success_req = "Pgz Sdf Send POST CommitQuotaInfra Success Req";
        public static final String pgz_sdf_recv_POST_commitquotainfra_success_res = "Pgz Sdf Received POST CommitQuotaInfra Success Res";
        public static final String pgz_sdf_send_POST_releasequota_success_req = "Pgz Sdf Send POST ReleaseQuota Success Req";
        public static final String pgz_sdf_recv_POST_releasequota_success_res = "Pgz Sdf Received POST ReleaseQuota Success Res";
        public static final String pgz_client_send_POST_result_success_req = "Pgz Client Send POST Result Success Req";
        public static final String pgz_client_recv_POST_result_success_res = "Pgz Client Received POST Result Success Res";
    }

    public static void incrementStats(AbstractAF abstractAF, String statName) {
        abstractAF.getEquinoxUtils().incrementStats(statName);
//        AFLog.d("****************************************************************");
        AFLog.d("STAT E04 : "+statName);
//        AFLog.d("****************************************************************");
    }
}

