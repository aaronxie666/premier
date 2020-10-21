package icn.premierandroid.models;

/**
 * Created by ICN on 13/10/2016.
 */
public class LikesDataModel {

    private String imageFileUrl, codeFileUrl, moreInfoFileUrl, redeemFileUrl, finalImageFileUrl, code, website_url, objectid;
    private int points, order;
    private Boolean isAddress, isCode, isCompetition, isUrl, isSpecialCompetition, isMultiCodes, anon;

    public LikesDataModel(String imageFileUrl, String codeFileUrl, String moreInfoFileUrl, String redeemFileUrl, String finalImageFileUrl, String code, String website_url, int points, Boolean isAddress, Boolean isCode, Boolean isCompetition, Boolean isUrl, Boolean isSpecialCompetition, int order, String objectid, boolean anon, Boolean isMultiCodes) {
        this.imageFileUrl = imageFileUrl;
        this.codeFileUrl = codeFileUrl;
        this.moreInfoFileUrl = moreInfoFileUrl;
        this.redeemFileUrl = redeemFileUrl;
        this.finalImageFileUrl = finalImageFileUrl;
        this.code = code;
        this.website_url = website_url;
        this.points = points;
        this.isAddress = isAddress;
        this.isCode = isCode;
        this.isCompetition = isCompetition;
        this.isUrl = isUrl;
        this.isSpecialCompetition = isSpecialCompetition;
        this.order = order;
        this.objectid = objectid;
        this.anon = anon;
        this.isMultiCodes = isMultiCodes;
    }

    public boolean isAnon() {return anon;}

    public String getImageFileUrl() {
        return imageFileUrl;
    }

    public String getCodeFileUrl() {
        return codeFileUrl;
    }

    public String getMoreInfoFileUrl() {
        return moreInfoFileUrl;
    }

    public String getRedeemFileUrl() {
        return redeemFileUrl;
    }

    public String getFinalImageFileUrl() {
        return finalImageFileUrl;
    }

    public String getCode() {
        return code;
    }

    public Boolean isCode() {
        return isCode;
    }

    public Boolean isCompetition() {
        return isCompetition;
    }

    public Boolean isUrl() {
        return isUrl;
    }

    public Boolean isSpecialCompetition() {
        return isSpecialCompetition;
    }

    public String getWebsite_url() {
        return website_url;
    }

    public int getPoints() {
        return points;
    }

    public Boolean isAddress() {
        return isAddress;
    }

    public Boolean isMultiCodes() {return isMultiCodes;}

    public int getOrder() {return order;}

    public String getObjectid() {return objectid;}

}
