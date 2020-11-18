package com.example.hardware;

/**
 * Created by danjj on 2020/11/18 0018.
 */
public class CellInfoBean4 {

    /**
     * mType : 4
     * CellIdentity : {"mMcc":460,"mMnc":1,"mLac":42303,"mCid":24128969,"mPsc":265,"mUarfcn":10663}
     * CellSignalStrength : {"ss":4,"ber":99}
     */

    private int mType;
    private CellIdentityBean CellIdentity;
    private CellSignalStrengthBean CellSignalStrength;

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
        /**
         * mMcc : 460
         * mMnc : 1
         * mLac : 42303
         * mCid : 24128969
         * mPsc : 265
         * mUarfcn : 10663
         */

        private int mMcc;
        private int mMnc;
        private int mLac;
        private int mCid;
        private int mPsc;
        private int mUarfcn;

        public int getMMcc() {
            return mMcc;
        }

        public void setMMcc(int mMcc) {
            this.mMcc = mMcc;
        }

        public int getMMnc() {
            return mMnc;
        }

        public void setMMnc(int mMnc) {
            this.mMnc = mMnc;
        }

        public int getMLac() {
            return mLac;
        }

        public void setMLac(int mLac) {
            this.mLac = mLac;
        }

        public int getMCid() {
            return mCid;
        }

        public void setMCid(int mCid) {
            this.mCid = mCid;
        }

        public int getMPsc() {
            return mPsc;
        }

        public void setMPsc(int mPsc) {
            this.mPsc = mPsc;
        }

        public int getMUarfcn() {
            return mUarfcn;
        }

        public void setMUarfcn(int mUarfcn) {
            this.mUarfcn = mUarfcn;
        }
    }

    public static class CellSignalStrengthBean {
        /**
         * ss : 4
         * ber : 99
         */

        private int ss;
        private int ber;

        public int getSs() {
            return ss;
        }

        public void setSs(int ss) {
            this.ss = ss;
        }

        public int getBer() {
            return ber;
        }

        public void setBer(int ber) {
            this.ber = ber;
        }
    }
}
