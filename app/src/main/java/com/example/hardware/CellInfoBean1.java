package com.example.hardware;

/**
 * Created by danjj on 2020/11/18 0018.
 */
public class CellInfoBean1 {

    /**
     * mType : 1
     * CellIdentity : {"mMcc":460,"mMnc":0,"mLac":9763,"mCid":4852,"mArfcn":49,"mBsic":4}
     * CellSignalStrength : {"ss":15,"ber":99,"mTa":2147483647}
     */

    private int mType;
    private CellIdentityBean CellIdentity;
    private CellSignalStrengthBean CellSignalStrength;

    public CellInfoBean1() {
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
        /**
         * mMcc : 460
         * mMnc : 0
         * mLac : 9763
         * mCid : 4852
         * mArfcn : 49
         * mBsic : 4
         */

        private int mMcc;
        private int mMnc;
        private int mLac;
        private int mCid;
        private int mArfcn;
        private int mBsic;

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

        public int getMArfcn() {
            return mArfcn;
        }

        public void setMArfcn(int mArfcn) {
            this.mArfcn = mArfcn;
        }

        public int getMBsic() {
            return mBsic;
        }

        public void setMBsic(int mBsic) {
            this.mBsic = mBsic;
        }
    }

    public static class CellSignalStrengthBean {
        /**
         * ss : 15
         * ber : 99
         * mTa : 2147483647
         */

        private int ss;
        private int ber;
        private int mTa;

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

        public int getMTa() {
            return mTa;
        }

        public void setMTa(int mTa) {
            this.mTa = mTa;
        }
    }
}
