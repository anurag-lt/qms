package model;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Represents a deviation instance within the quality management system.
 */
public class Deviation {

    private int id;
    private Integer batchId;
    private Date dateOfOccurrence;
    private String description;
    private String deviationNumber;
    private Enums.DeviationSeverity deviationSeverity;
    private Enums.DeviationType deviationType;
    private Integer equipmentId;
    private Integer materialId;
    private Integer productId;
    private String remarks;
    private String reviewComment;
    private String riskAssessment;
    private String standardProcedure;
    private Enums.DeviationStatus status;
    private Timestamp timeOfIdentification;

    /**
     * Gets the unique identifier for the deviation.
     * 
     * @return the unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the deviation.
     * 
     * @param id the unique identifier to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the batch identifier associated with the deviation.
     * 
     * @return the batch id
     */
    public Integer getBatchId() {
        return batchId;
    }

    /**
     * Sets the batch identifier associated with the deviation.
     * 
     * @param batchId the batch id to set
     */
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    /**
     * Gets the date of occurrence for the deviation.
     * 
     * @return the date of occurrence
     */
    public Date getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    /**
     * Sets the date of occurrence for the deviation.
     * 
     * @param dateOfOccurrence the date of occurrence to set
     */
    public void setDateOfOccurrence(Date dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    /**
     * Gets the description of the deviation.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the deviation.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the deviation number.
     * 
     * @return the deviation number
     */
    public String getDeviationNumber() {
        return deviationNumber;
    }

    /**
     * Sets the deviation number.
     * 
     * @param deviationNumber the deviation number to set
     */
    public void setDeviationNumber(String deviationNumber) {
        this.deviationNumber = deviationNumber;
    }

    /**
     * Gets the severity of the deviation.
     * 
     * @return the deviation severity
     */
    public Enums.DeviationSeverity getDeviationSeverity() {
        return deviationSeverity;
    }

    /**
     * Sets the severity of the deviation.
     * 
     * @param deviationSeverity the deviation severity to set
     */
    public void setDeviationSeverity(Enums.DeviationSeverity deviationSeverity) {
        this.deviationSeverity = deviationSeverity;
    }

    /**
     * Gets the type of the deviation.
     * 
     * @return the deviation type
     */
    public Enums.DeviationType getDeviationType() {
        return deviationType;
    }

    /**
     * Sets the type of the deviation.
     * 
     * @param deviationType the deviation type to set
     */
    public void setDeviationType(Enums.DeviationType deviationType) {
        this.deviationType = deviationType;
    }

    /**
     * Gets the equipment identifier associated with the deviation.
     * 
     * @return the equipment id
     */
    public Integer getEquipmentId() {
        return equipmentId;
    }

    /**
     * Sets the equipment identifier associated with the deviation.
     * 
     * @param equipmentId the equipment id to set
     */
    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * Gets the material identifier associated with the deviation.
     * 
     * @return the material id
     */
    public Integer getMaterialId() {
        return materialId;
    }

    /**
     * Sets the material identifier associated with the deviation.
     * 
     * @param materialId the material id to set
     */
    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    /**
     * Gets the product identifier associated with the deviation.
     * 
     * @return the product id
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * Sets the product identifier associated with the deviation.
     * 
     * @param productId the product id to set
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * Gets supplementary remarks for the deviation.
     * 
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets supplementary remarks for the deviation.
     * 
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the review comment for the deviation.
     * 
     * @return the review comment
     */
    public String getReviewComment() {
        return reviewComment;
    }

    /**
     * Sets the review comment for the deviation.
     * 
     * @param reviewComment the review comment to set
     */
    public void setReviewComment(String reviewComment) {
        this.reviewComment = reviewComment;
    }

    /**
     * Gets the risk assessment associated with the deviation.
     * 
     * @return the risk assessment
     */
    public String getRiskAssessment() {
        return riskAssessment;
    }

    /**
     * Sets the risk assessment associated with the deviation.
     * 
     * @param riskAssessment the risk assessment to set
     */
    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    /**
     * Gets the standard procedure involved in the deviation.
     * 
     * @return the standard procedure
     */
    public String getStandardProcedure() {
        return standardProcedure;
    }

    /**
     * Sets the standard procedure involved in the deviation.
     * 
     * @param standardProcedure the standard procedure to set
     */
    public void setStandardProcedure(String standardProcedure) {
        this.standardProcedure = standardProcedure;
    }

    /**
     * Gets the status of the deviation.
     * 
     * @return the status
     */
    public Enums.DeviationStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the deviation.
     * 
     * @param status the status to set
     */
    public void setStatus(Enums.DeviationStatus status) {
        this.status = status;
    }

    /**
     * Gets the time of identification for the deviation.
     * 
     * @return the time of identification
     */
    public Timestamp getTimeOfIdentification() {
        return timeOfIdentification;
    }

    /**
     * Sets the time of identification for the deviation.
     * 
     * @param timeOfIdentification the time of identification to set
     */
    public void setTimeOfIdentification(Timestamp timeOfIdentification) {
        this.timeOfIdentification = timeOfIdentification;
    }

    @Override
    public String toString() {
        return "Deviation{" +
                "id=" + id +
                ", batchId=" + batchId +
                ", dateOfOccurrence=" + dateOfOccurrence +
                ", description='" + description + '\'' +
                ", deviationNumber='" + deviationNumber + '\'' +
                ", deviationSeverity=" + deviationSeverity +
                ", deviationType=" + deviationType +
                ", equipmentId=" + equipmentId +
                ", materialId=" + materialId +
                ", productId=" + productId +
                ", remarks='" + remarks + '\'' +
                ", reviewComment='" + reviewComment + '\'' +
                ", riskAssessment='" + riskAssessment + '\'' +
                ", standardProcedure='" + standardProcedure + '\'' +
                ", status=" + status +
                ", timeOfIdentification=" + timeOfIdentification +
                '}';
    }
}