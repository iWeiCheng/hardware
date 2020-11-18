package com.example.hardware;

/**
 * Created by danjj on 2020/11/18 0018.
 */
public class CellInfoBean3 {


    /**
     * mType : 3
     * CellIdentity : {"mMcc":460,"mMnc":11,"mCi":124946454,"mPci":2,"mTac":30496,"mEarfcn":2452}
     * CellSignalStrength : {"ss":21,"rsrp":-95,"rsrq":-8,"rssnr":2147483647,"cqi":2147483647,"ta":2147483647}
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
         * mMnc : 11
         * mCi : 124946454
         * mPci : 2
         * mTac : 30496
         * mEarfcn : 2452
         */

        private int mMcc;
        private int mMnc;
        private int mCi;
        private int mPci;
        private int mTac;
        private int mEarfcn;

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

        public int getMCi() {
            return mCi;
        }

        public void setMCi(int mCi) {
            this.mCi = mCi;
        }

        public int getMPci() {
            return mPci;
        }

        public void setMPci(int mPci) {
            this.mPci = mPci;
        }

        public int getMTac() {
            return mTac;
        }

        public void setMTac(int mTac) {
            this.mTac = mTac;
        }

        public int getMEarfcn() {
            return mEarfcn;
        }

        public void setMEarfcn(int mEarfcn) {
            this.mEarfcn = mEarfcn;
        }
    }

    public static class CellSignalStrengthBean {
        /**
         * ss : 21
         * rsrp : -95
         * rsrq : -8
         * rssnr : 2147483647
         * cqi : 2147483647
         * ta : 2147483647
         */

        private int ss;
        private int rsrp;
        private int rsrq;
        private int rssnr;
        private int cqi;
        private int ta;

        public int getSs() {
            return ss;
        }

        public void setSs(int ss) {
            this.ss = ss;
        }

        public int getRsrp() {
            return rsrp;
        }

        public void setRsrp(int rsrp) {
            this.rsrp = rsrp;
        }

        public int getRsrq() {
            return rsrq;
        }

        public void setRsrq(int rsrq) {
            this.rsrq = rsrq;
        }

        public int getRssnr() {
            return rssnr;
        }

        public void setRssnr(int rssnr) {
            this.rssnr = rssnr;
        }

        public int getCqi() {
            return cqi;
        }

        public void setCqi(int cqi) {
            this.cqi = cqi;
        }

        public int getTa() {
            return ta;
        }

        public void setTa(int ta) {
            this.ta = ta;
        }
    }
}
