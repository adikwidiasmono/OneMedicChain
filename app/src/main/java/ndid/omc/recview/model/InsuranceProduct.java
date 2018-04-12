package ndid.omc.recview.model;

/**
 * Created by adikwidiasmono on 17/11/17.
 */

public class InsuranceProduct {
    private String insuranceProductId;
    private String insuranceIconUrl;
    private String insuranceName;
    private String productName;
    private String shortDesc;
    private String linkToDetailDesc;
    private Long createdDate;

    public InsuranceProduct() {
    }

    public String getInsuranceProductId() {
        return insuranceProductId;
    }

    public void setInsuranceProductId(String insuranceProductId) {
        this.insuranceProductId = insuranceProductId;
    }

    public String getInsuranceIconUrl() {
        return insuranceIconUrl;
    }

    public void setInsuranceIconUrl(String insuranceIconUrl) {
        this.insuranceIconUrl = insuranceIconUrl;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLinkToDetailDesc() {
        return linkToDetailDesc;
    }

    public void setLinkToDetailDesc(String linkToDetailDesc) {
        this.linkToDetailDesc = linkToDetailDesc;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }
}
