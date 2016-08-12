
package cn.explink.db.merge;

public class ValidateDto {

    @Override
    public String toString() {
        return "ValidateDto [countNewAddressStationRelation=" + this.countNewAddressStationRelation
                + ", countOldAddressStationRelation=" + this.countOldAddressStationRelation + ", countNewAddress="
                + this.countNewAddress + ", countOldAddress=" + this.countOldAddress + ", countNewAddressPermissions="
                + this.countNewAddressPermissions + ", countOldAddressPermissions=" + this.countOldAddressPermissions
                + ", countNewAlias=" + this.countNewAlias + ", countOldAlias=" + this.countOldAlias
                + ", countNewClientApplications=" + this.countNewClientApplications + ", countOldClientApplications="
                + this.countOldClientApplications + ", countNewDelivererRules=" + this.countNewDelivererRules
                + ", countOldDelivererRules=" + this.countOldDelivererRules + ", countNewDeliverer="
                + this.countNewDeliverer + ", countOldDeliverer=" + this.countOldDeliverer
                + ", countNewDeliveryStationRules=" + this.countNewDeliveryStationRules
                + ", countOldDeliveryStationRules=" + this.countOldDeliveryStationRules + ", countNewDeliveryStations="
                + this.countNewDeliveryStations + ", countOldDeliveryStations=" + this.countOldDeliveryStations
                + ", countNewKeywordSuffix=" + this.countNewKeywordSuffix + ", countOldKeywordSuffix="
                + this.countOldKeywordSuffix + ", countNewSystemConfig=" + this.countNewSystemConfig
                + ", countOldSystemConfig=" + this.countOldSystemConfig + ", countNewUsers=" + this.countNewUsers
                + ", countOldUsers=" + this.countOldUsers + ", countNewVendors=" + this.countNewVendors
                + ", countOldVendors=" + this.countOldVendors + "]";
    }

    // 2、校验数量address_station_relation
    int countNewAddressStationRelation;

    int countOldAddressStationRelation;

    // 1、删除address
    int countNewAddress;

    int countOldAddress;

    // 3、删除address_permissions
    int countNewAddressPermissions;

    int countOldAddressPermissions;

    // 4、删除alias
    int countNewAlias;

    int countOldAlias;

    // client_applications
    int countNewClientApplications;

    int countOldClientApplications;

    // deliverer_rules
    int countNewDelivererRules;

    int countOldDelivererRules;
    // deliverers

    int countNewDeliverer;

    int countOldDeliverer;

    // delivery_station_rules
    int countNewDeliveryStationRules;

    int countOldDeliveryStationRules;

    // delivery_stations
    int countNewDeliveryStations;

    int countOldDeliveryStations;

    // keyword_suffix
    int countNewKeywordSuffix;

    int countOldKeywordSuffix;

    // old_id
    // system_config
    int countNewSystemConfig;

    int countOldSystemConfig;

    // users
    int countNewUsers;

    int countOldUsers;

    // vendors
    int countNewVendors;

    public int getCountNewAddressStationRelation() {
        return this.countNewAddressStationRelation;
    }

    public void setCountNewAddressStationRelation(int countNewAddressStationRelation) {
        this.countNewAddressStationRelation = countNewAddressStationRelation;
    }

    public int getCountOldAddressStationRelation() {
        return this.countOldAddressStationRelation;
    }

    public void setCountOldAddressStationRelation(int countOldAddressStationRelation) {
        this.countOldAddressStationRelation = countOldAddressStationRelation;
    }

    public int getCountNewAddress() {
        return this.countNewAddress;
    }

    public void setCountNewAddress(int countNewAddress) {
        this.countNewAddress = countNewAddress;
    }

    public int getCountOldAddress() {
        return this.countOldAddress;
    }

    public void setCountOldAddress(int countOldAddress) {
        this.countOldAddress = countOldAddress;
    }

    public int getCountNewAddressPermissions() {
        return this.countNewAddressPermissions;
    }

    public void setCountNewAddressPermissions(int countNewAddressPermissions) {
        this.countNewAddressPermissions = countNewAddressPermissions;
    }

    public int getCountOldAddressPermissions() {
        return this.countOldAddressPermissions;
    }

    public void setCountOldAddressPermissions(int countOldAddressPermissions) {
        this.countOldAddressPermissions = countOldAddressPermissions;
    }

    public int getCountNewAlias() {
        return this.countNewAlias;
    }

    public void setCountNewAlias(int countNewAlias) {
        this.countNewAlias = countNewAlias;
    }

    public int getCountOldAlias() {
        return this.countOldAlias;
    }

    public void setCountOldAlias(int countOldAlias) {
        this.countOldAlias = countOldAlias;
    }

    public int getCountNewClientApplications() {
        return this.countNewClientApplications;
    }

    public void setCountNewClientApplications(int countNewClientApplications) {
        this.countNewClientApplications = countNewClientApplications;
    }

    public int getCountOldClientApplications() {
        return this.countOldClientApplications;
    }

    public void setCountOldClientApplications(int countOldClientApplications) {
        this.countOldClientApplications = countOldClientApplications;
    }

    public int getCountNewDelivererRules() {
        return this.countNewDelivererRules;
    }

    public void setCountNewDelivererRules(int countNewDelivererRules) {
        this.countNewDelivererRules = countNewDelivererRules;
    }

    public int getCountOldDelivererRules() {
        return this.countOldDelivererRules;
    }

    public void setCountOldDelivererRules(int countOldDelivererRules) {
        this.countOldDelivererRules = countOldDelivererRules;
    }

    public int getCountNewDeliverer() {
        return this.countNewDeliverer;
    }

    public void setCountNewDeliverer(int countNewDeliverer) {
        this.countNewDeliverer = countNewDeliverer;
    }

    public int getCountOldDeliverer() {
        return this.countOldDeliverer;
    }

    public void setCountOldDeliverer(int countOldDeliverer) {
        this.countOldDeliverer = countOldDeliverer;
    }

    public int getCountNewDeliveryStationRules() {
        return this.countNewDeliveryStationRules;
    }

    public void setCountNewDeliveryStationRules(int countNewDeliveryStationRules) {
        this.countNewDeliveryStationRules = countNewDeliveryStationRules;
    }

    public int getCountOldDeliveryStationRules() {
        return this.countOldDeliveryStationRules;
    }

    public void setCountOldDeliveryStationRules(int countOldDeliveryStationRules) {
        this.countOldDeliveryStationRules = countOldDeliveryStationRules;
    }

    public int getCountNewDeliveryStations() {
        return this.countNewDeliveryStations;
    }

    public void setCountNewDeliveryStations(int countNewDeliveryStations) {
        this.countNewDeliveryStations = countNewDeliveryStations;
    }

    public int getCountOldDeliveryStations() {
        return this.countOldDeliveryStations;
    }

    public void setCountOldDeliveryStations(int countOldDeliveryStations) {
        this.countOldDeliveryStations = countOldDeliveryStations;
    }

    public int getCountNewKeywordSuffix() {
        return this.countNewKeywordSuffix;
    }

    public void setCountNewKeywordSuffix(int countNewKeywordSuffix) {
        this.countNewKeywordSuffix = countNewKeywordSuffix;
    }

    public int getCountOldKeywordSuffix() {
        return this.countOldKeywordSuffix;
    }

    public void setCountOldKeywordSuffix(int countOldKeywordSuffix) {
        this.countOldKeywordSuffix = countOldKeywordSuffix;
    }

    public int getCountNewSystemConfig() {
        return this.countNewSystemConfig;
    }

    public void setCountNewSystemConfig(int countNewSystemConfig) {
        this.countNewSystemConfig = countNewSystemConfig;
    }

    public int getCountOldSystemConfig() {
        return this.countOldSystemConfig;
    }

    public void setCountOldSystemConfig(int countOldSystemConfig) {
        this.countOldSystemConfig = countOldSystemConfig;
    }

    public int getCountNewUsers() {
        return this.countNewUsers;
    }

    public void setCountNewUsers(int countNewUsers) {
        this.countNewUsers = countNewUsers;
    }

    public int getCountOldUsers() {
        return this.countOldUsers;
    }

    public void setCountOldUsers(int countOldUsers) {
        this.countOldUsers = countOldUsers;
    }

    public int getCountNewVendors() {
        return this.countNewVendors;
    }

    public void setCountNewVendors(int countNewVendors) {
        this.countNewVendors = countNewVendors;
    }

    public int getCountOldVendors() {
        return this.countOldVendors;
    }

    public void setCountOldVendors(int countOldVendors) {
        this.countOldVendors = countOldVendors;
    }

    int countOldVendors;

}
