package icn.premierandroid.models;

import java.util.Date;

/**
 * Created by ICN on 28/11/2016.
 */
public class AdventModel {

    private Boolean isCode, isPoints, isVideo;
    private int awardPoints, position;
    private Date date;
    private String likes_description, videoThumbnailUrl, companyLogoUrl, discountCode, codeDescription, websiteUrl, videoUrl;

    public AdventModel(Boolean isCode, Boolean isPoints, Boolean isVideo, int awardPoints, Date date, String likes_description, String videoThumbnailUrl, String companyLogoUrl, String discountCode, String codeDescription, String websiteUrl, String videoUrl, int position) {
        this.isCode = isCode;
        this.isPoints = isPoints;
        this.isVideo = isVideo;
        this.awardPoints = awardPoints;
        this.position = position;
        this.date = date;
        this.likes_description = likes_description;
        this.videoThumbnailUrl = videoThumbnailUrl;
        this.companyLogoUrl = companyLogoUrl;
        this.discountCode = discountCode;
        this.codeDescription = codeDescription;
        this.websiteUrl = websiteUrl;
        this.videoUrl = videoUrl;
    }

    public Boolean getCode() {
        return isCode;
    }

    public Boolean getPoints() {
        return isPoints;
    }

    public int getAwardPoints() {
        return awardPoints;
    }

    public Boolean getVideo() {
        return isVideo;
    }

    public Date getDate() {
        return date;
    }

    public String getLikes_description() {
        return likes_description;
    }

    public String getVideoThumbnailUrl() {
        return videoThumbnailUrl;
    }

    public String getCompanyLogoUrl() {
        return companyLogoUrl;
    }

    public String getCodeDescription() {
        return codeDescription;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public int getPosition() {
        return position;
    }
}
