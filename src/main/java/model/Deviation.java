package model;

import java.sql.Timestamp;
import java.util.Date;
import model.Enums.DeviationSeverity;
import model.Enums.DeviationStatus;
import model.Enums.DeviationType;

/**
 * Deviation entity representing a unique deviation event within the quality management system.
 */
public class Deviation {
    private int id;
    private Integer batchId;
    private Date dateOfOccurrence;
    private String description;
    private String deviationNumber;
    private DeviationSeverity deviationSeverity;
    private DeviationType deviationType;
    private Integer equipmentId;
    private Integer materialId;
    private Integer productId;
    private String remarks;
    private String riskAssessment;
    private String standardProcedure;
    private DeviationStatus status;
    private Timestamp timeOfIdentification;
    
    public Deviation() {
        
    }

    /**
     * Gets the unique identifier for the deviation.
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the deviation.
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the batch ID associated with the deviation.
     * @return batchId
     */
    public Integer getBatchId() {
        return batchId;
    }

    /**
     * Sets the batch ID associated with the deviation.
     * @param batchId
     */
    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    /**
     * Gets the date of occurrence of the deviation.
     * @return dateOfOccurrence
     */
    public Date getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    /**
     * Sets the date of occurrence of the deviation.
     * @param dateOfOccurrence
     */
    public void setDateOfOccurrence(Date dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    /**
     * Gets the description of the deviation.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the deviation.
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the deviation number.
     * @return deviationNumber
     */
    public String getDeviationNumber() {
        return deviationNumber;
    }

    /**
     * Sets the deviation number.
     * @param deviationNumber
     */
    public void setDeviationNumber(String deviationNumber) {
        this.deviationNumber = deviationNumber;
    }

    /**
     * Gets the severity of the deviation.
     * @return deviationSeverity
     */
    public DeviationSeverity getDeviationSeverity() {
        return deviationSeverity;
    }

    /**
     * Sets the severity of the deviation.
     * @param deviationSeverity
     */
    public void setDeviationSeverity(DeviationSeverity deviationSeverity) {
        this.deviationSeverity = deviationSeverity;
    }

    /**
     * Gets the type of the deviation.
     * @return deviationType
     */
    public DeviationType getDeviationType() {
        return deviationType;
    }

    /**
     * Sets the type of the deviation.
     * @param deviationType
     */
    public void setDeviationType(DeviationType deviationType) {
        this.deviationType = deviationType;
    }

    /**
     * Gets the equipment ID associated with the deviation.
     * @return equipmentId
     */
    public Integer getEquipmentId() {
        return equipmentId;
    }

    /**
     * Sets the equipment ID associated with the deviation.
     * @param equipmentId
     */
    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    /**
     * Gets the material ID associated with the deviation.
     * @return materialId
     */
    public Integer getMaterialId() {
        return materialId;
    }

    /**
     * Sets the material ID associated with the deviation.
     * @param materialId
     */
    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    /**
     * Gets the product ID associated with the deviation.
     * @return productId
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * Sets the product ID associated with the deviation.
     * @param productId
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * Gets the remarks on the deviation.
     * @return remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks on the deviation.
     * @param remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the risk assessment of the deviation.
     * @return riskAssessment
     */
    public String getRiskAssessment() {
        return riskAssessment;
    }

    /**
     * Sets the risk assessment of the deviation.
     * @param riskAssessment
     */
    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    /**
     * Gets the standard procedure involved in the deviation.
     * @return standardProcedure
     */
    public String getStandardProcedure() {
        return standardProcedure;
    }

    /**
     * Sets the standard procedure involved in the deviation.
     * @param standardProcedure
     */
    public void setStandardProcedure(String standardProcedure) {
        this.standardProcedure = standardProcedure;
    }

    /**
     * Gets the status of the deviation.
     * @return status
     */
    public DeviationStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the deviation.
     * @param status
     */
    public void setStatus(DeviationStatus status) {
        this.status = status;
    }

    /**
     * Gets the time of identification of the deviation.
     * @return timeOfIdentification
     */
    public Timestamp getTimeOfIdentification() {
        return timeOfIdentification;
    }

    /**
     * Sets the time of identification of the deviation.
     * @param timeOfIdentification
     */
    public void setTimeOfIdentification(Timestamp timeOfIdentification) {
        this.timeOfIdentification = timeOfIdentification;
    }

    @Override
    public String toString() {
        return "Deviation{" + "id=" + id + ", batchId=" + batchId + ", dateOfOccurrence=" + dateOfOccurrence + 
                ", description='" + description + '\'' + ", deviationNumber='" + deviationNumber + '\'' + 
                ", deviationSeverity=" + deviationSeverity + ", deviationType=" + deviationType + 
                ", equipmentId=" + equipmentId + ", materialId=" + materialId + ", productId=" + productId + 
                ", remarks='" + remarks + '\'' + ", riskAssessment='" + riskAssessment + '\'' + 
                ", standardProcedure='" + standardProcedure + '\'' + ", status=" + status + 
                ", timeOfIdentification=" + timeOfIdentification + '}';
    }
}