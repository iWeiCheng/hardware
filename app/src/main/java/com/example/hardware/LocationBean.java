package com.example.hardware;

/**
 * @created : 2020/11/19 15:49
 * @description :
 */
public class LocationBean {

    /**
     * mLocale : {"lang":"zh","country":"CN"}
     * mFeatureName :
     * mMaxAddressLineIndex : 1
     * mAdminArea :
     * mSubAdminArea :
     * mLocality : 广州
     * mSubLocality :
     * mThoroughfare :
     * mSubThoroughfare :
     * mPremises :
     * mPostalCode :
     * mCountryCode :
     * mCountryName : 中国
     * mLatitude :
     * mLongitude :
     * mPhone :
     * mUrl :
     */

    private MLocaleBean mLocale;
    private String mFeatureName;
    private int mMaxAddressLineIndex;
    private String mAdminArea;
    private String mSubAdminArea;
    private String mLocality;
    private String mSubLocality;
    private String mThoroughfare;
    private String mSubThoroughfare;
    private String mPremises;
    private String mPostalCode;
    private String mCountryCode;
    private String mCountryName;
    private String mLatitude;
    private String mLongitude;
    private String mPhone;
    private String mUrl;

    public MLocaleBean getMLocale() {
        return mLocale;
    }

    public void setMLocale(MLocaleBean mLocale) {
        this.mLocale = mLocale;
    }

    public String getMFeatureName() {
        return mFeatureName;
    }

    public void setMFeatureName(String mFeatureName) {
        this.mFeatureName = mFeatureName;
    }

    public int getMMaxAddressLineIndex() {
        return mMaxAddressLineIndex;
    }

    public void setMMaxAddressLineIndex(int mMaxAddressLineIndex) {
        this.mMaxAddressLineIndex = mMaxAddressLineIndex;
    }

    public String getMAdminArea() {
        return mAdminArea;
    }

    public void setMAdminArea(String mAdminArea) {
        this.mAdminArea = mAdminArea;
    }

    public String getMSubAdminArea() {
        return mSubAdminArea;
    }

    public void setMSubAdminArea(String mSubAdminArea) {
        this.mSubAdminArea = mSubAdminArea;
    }

    public String getMLocality() {
        return mLocality;
    }

    public void setMLocality(String mLocality) {
        this.mLocality = mLocality;
    }

    public String getMSubLocality() {
        return mSubLocality;
    }

    public void setMSubLocality(String mSubLocality) {
        this.mSubLocality = mSubLocality;
    }

    public String getMThoroughfare() {
        return mThoroughfare;
    }

    public void setMThoroughfare(String mThoroughfare) {
        this.mThoroughfare = mThoroughfare;
    }

    public String getMSubThoroughfare() {
        return mSubThoroughfare;
    }

    public void setMSubThoroughfare(String mSubThoroughfare) {
        this.mSubThoroughfare = mSubThoroughfare;
    }

    public String getMPremises() {
        return mPremises;
    }

    public void setMPremises(String mPremises) {
        this.mPremises = mPremises;
    }

    public String getMPostalCode() {
        return mPostalCode;
    }

    public void setMPostalCode(String mPostalCode) {
        this.mPostalCode = mPostalCode;
    }

    public String getMCountryCode() {
        return mCountryCode;
    }

    public void setMCountryCode(String mCountryCode) {
        this.mCountryCode = mCountryCode;
    }

    public String getMCountryName() {
        return mCountryName;
    }

    public void setMCountryName(String mCountryName) {
        this.mCountryName = mCountryName;
    }

    public String getMLatitude() {
        return mLatitude;
    }

    public void setMLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public String getMLongitude() {
        return mLongitude;
    }

    public void setMLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getMPhone() {
        return mPhone;
    }

    public void setMPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getMUrl() {
        return mUrl;
    }

    public void setMUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public static class MLocaleBean {
        /**
         * lang : zh
         * country : CN
         */

        private String lang;
        private String country;

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
