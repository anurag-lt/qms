package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * POJO class for Deviation entity.
 */
public class Deviation {

    // Attributes
    private int id;
    private Date dateOfIdentification;
    private Date dateOfOccurrence;
    private String description;
    private String deviationNumber;
    private Enums.DeviationSeverity deviationSeverity;
    private Enums.DeviationType deviationType;
    private String remarks;
    private String riskAssessment;
    private String standardProcedure;
    private Enums.DeviationStatus status;
    private Timestamp timeOfIdentification;

    // Constructors
    public Deviation() {
    }

    // Getters and Setters
    /**
     * Gets the primary key ID of the deviation.
     * @return id as int.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the primary key ID of the deviation.
     * @param id the id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the date of identification.
     * @return dateOfIdentification as Date.
     */
    public Date getDateOfIdentification() {
        return dateOfIdentification;
    }

    /**
     * Sets the date of identification.
     * @param dateOfIdentification the dateOfIdentification to set.
     */
    public void setDateOfIdentification(Date dateOfIdentification) {
        this.dateOfIdentification = dateOfIdentification;
    }

    /**
     * Gets the date of occurrence.
     * @return dateOfOccurrence as Date.
     */
    public Date getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    /**
     * Sets the date of occurrence.
     * @param dateOfOccurrence the dateOfOccurrence to set.
     */
    public void setDateOfOccurrence(Date dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    /**
     * Gets the description of the deviation.
     * @return description as String.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the deviation.
     * @param description the description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the deviation number.
     * @return deviationNumber as String.
     */
    public String getDeviationNumber() {
        return deviationNumber;
    }

    /**
     * Sets the deviation number.
     * @param deviationNumber the deviationNumber to set.
     */
    public void setDeviationNumber(String deviationNumber) {
        this.deviationNumber = deviationNumber;
    }

    /**
     * Gets the severity of the deviation.
     * @return deviationSeverity as Enums.DeviationSeverity.
     */
    public Enums.DeviationSeverity getDeviationSeverity() {
        return deviationSeverity;
    }

    /**
     * Sets the severity of the deviation.
     * @param deviationSeverity the deviationSeverity to set.
     */
    public void setDeviationSeverity(Enums.DeviationSeverity deviationSeverity) {
        this.deviationSeverity = deviationSeverity;
    }

    /**
     * Gets the type of the deviation.
     * @return deviationType as Enums.DeviationType.
     */
    public Enums.DeviationType getDeviationType() {
        return deviationType;
    }

    /**
     * Sets the type of the deviation.
     * @param deviationType the deviationType to set.
     */
    public void setDeviationType(Enums.DeviationType deviationType) {
        this.deviationType = deviationType;
    }

    /**
     * Gets additional remarks.
     * @return remarks as String.
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets additional remarks.
     * @param remarks the remarks to set.
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the risk assessment details.
     * @return riskAssessment as String.
     */
    public String getRiskAssessment() {
        return riskAssessment;
    }

    /**
     * Sets the risk assessment details.
     * @param riskAssessment the riskAssessment to set.
     */
    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    /**
     * Gets the standard procedure related to the deviation.
     * @return standardProcedure as String.
     */
    public String getStandardProcedure() {
        return standardProcedure;
    }

    /**
     * Sets the standard procedure related to the deviation.
     * @param standardProcedure the standardProcedure to set.
     */
    public void setStandardProcedure(String standardProcedure) {
        this.standardProcedure = standardProcedure;
    }

    /**
     * Gets the current status of the deviation.
     * @return status as Enums.DeviationStatus.
     */
    public Enums.DeviationStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the deviation.
     * @param status the status to set.
     */
    public void setStatus(Enums.DeviationStatus status) {
        this.status = status;
    }

    /**
     * Gets the timestamp of identification.
     * @return timeOfIdentification as Timestamp.
     */
    public Timestamp getTimeOfIdentification() {
        return timeOfIdentification;
    }

    /**
     * Sets the timestamp of identification.
     * @param timeOfIdentification the timeOfIdentification to set.
     */
    public void setTimeOfIdentification(Timestamp timeOfIdentification) {
        this.timeOfIdentification = timeOfIdentification;
    }

    // toString Method
    @Override
    public String toString() {
        return "Deviation{" +
                "id=" + id +
                ", dateOfIdentification=" + dateOfIdentification +
                ", dateOfOccurrence=" + dateOfOccurrence +
                ", description='" + description + '\'' +
                ", deviationNumber='" + deviationNumber + '\'' +
                ", deviationSeverity=" + deviationSeverity +
                ", deviationType=" + deviationType +
                ", remarks='" + remarks + '\'' +
                ", riskAssessment='" + riskAssessment + '\'' +
                ", standardProcedure='" + standardProcedure + '\'' +
                ", status=" + status +
                ", timeOfIdentification=" + timeOfIdentification +
                '}';
    }
}