package com.example.hardware;

/**
 * Created by danjj on 2020/11/16 0016.
 */
public class CellInfoBean {

    /**
     * mType : 2
     * CellIdentity : {"mNetworkId":2,"mSystemId":13844,"mBasestationId":11281,"mLongitude":2147483647,"mLatitude":2147483647}
     * CellSignalStrength : {"cdmaDbm":-86,"cdmaEcio":-70,"evdoDbm":2147483647,"evdoEcio":2147483647,"evdoSnr":2147483647}
     */

    private int mType;
    private CellIdentityBean CellIdentity;
    private CellSignalStrengthBean CellSignalStrength;

    public CellInfoBean(int mType, CellIdentityBean cellIdentity, CellSignalStrengthBean cellSignalStrength) {
        this.mType = mType;
        CellIdentity = cellIdentity;
        CellSignalStrength = cellSignalStrength;
    }

    public int getMType() {
        return mType;
    }

    public void setMType(int mType) {
        this.mType = mType;
    }

    public CellIdentityBean getCellIdentity() {
        return CellIdentity;
    }

    public void setCellIdentity(CellIdentityBean CellIdentity) {
        this.CellIdentity = CellIdentity;
    }

    public CellSignalStrengthBean getCellSignalStrength() {
        return CellSignalStrength;
    }

    public void setCellSignalStrength(CellSignalStrengthBean CellSignalStrength) {
        this.CellSignalStrength = CellSignalStrength;
    }

    public static class CellIdentityBean {
        public CellIdentityBean(int mNetworkId, int mSystemId, int mBasestationId, int mLongitude, int mLatitude) {
            this.mNetworkId = mNetworkId;
            this.mSystemId = mSystemId;
            this.mBasestationId = mBasestationId;
            this.mLongitude = mLongitude;
            this.mLatitude = mLatitude;
        }

        /**
         * mNetworkId : 2
         * mSystemId : 13844
         * mBasestationId : 11281
         * mLongitude : 2147483647
         * mLatitude : 2147483647
         */

        private int mNetworkId;
        private int mSystemId;
        private int mBasestationId;
        private int mLongitude;
        private int mLatitude;

        public int getMNetworkId() {
            return mNetworkId;
        }

        public void setMNetworkId(int mNetworkId) {
            this.mNetworkId = mNetworkId;
        }

        public int getMSystemId() {
            return mSystemId;
        }

        public void setMSystemId(int mSystemId) {
            this.mSystemId = mSystemId;
        }

        public int getMBasestationId() {
            return mBasestationId;
        }

        public void setMBasestationId(int mBasestationId) {
            this.mBasestationId = mBasestationId;
        }

        public int getMLongitude() {
            return mLongitude;
        }

        public void setMLongitude(int mLongitude) {
            this.mLongitude = mLongitude;
        }

        public int getMLatitude() {
            return mLatitude;
        }

        public void setMLatitude(int mLatitude) {
            this.mLatitude = mLatitude;
        }
    }

    public static class CellSignalStrengthBean {
        public CellSignalStrengthBean(int cdmaDbm, int cdmaEcio, int evdoDbm, int evdoEcio, int evdoSnr) {
            this.cdmaDbm = cdmaDbm;
            this.cdmaEcio = cdmaEcio;
            this.evdoDbm = evdoDbm;
            this.evdoEcio = evdoEcio;
            this.evdoSnr = evdoSnr;
        }

        /**
         * cdmaDbm : -86
         * cdmaEcio : -70
         * evdoDbm : 2147483647
         * evdoEcio : 2147483647
         * evdoSnr : 2147483647
         */

        private int cdmaDbm;
        private int cdmaEcio;
        private int evdoDbm;
        private int evdoEcio;
        private int evdoSnr;

        public int getCdmaDbm() {
            return cdmaDbm;
        }

        public void setCdmaDbm(int cdmaDbm) {
            this.cdmaDbm = cdmaDbm;
        }

        public int getCdmaEcio() {
            return cdmaEcio;
        }

        public void setCdmaEcio(int cdmaEcio) {
            this.cdmaEcio = cdmaEcio;
        }

        public int getEvdoDbm() {
            return evdoDbm;
        }

        public void setEvdoDbm(int evdoDbm) {
            this.evdoDbm = evdoDbm;
        }

        public int getEvdoEcio() {
            return evdoEcio;
        }

        public void setEvdoEcio(int evdoEcio) {
            this.evdoEcio = evdoEcio;
        }

        public int getEvdoSnr() {
            return evdoSnr;
        }

        public void setEvdoSnr(int evdoSnr) {
            this.evdoSnr = evdoSnr;
        }
    }
}
