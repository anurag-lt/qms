package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a deviation instance within the quality management system.
 */
public class Deviation {

    // Attributes
    private int id;
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

    // Getters
    /**
     * Gets the unique identifier for the deviation.
     * @return the id of the deviation
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the date of occurrence of the deviation.
     * @return the occurrence date
     */
    public Date getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    /**
     * Gets the description of the deviation.
     * @return the deviation description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the deviation number.
     * @return the deviation number
     */
    public String getDeviationNumber() {
        return deviationNumber;
    }

    /**
     * Gets the severity of the deviation.
     * @return the deviation severity
     */
    public Enums.DeviationSeverity getDeviationSeverity() {
        return deviationSeverity;
    }

    /**
     * Gets the type of the deviation.
     * @return the deviation type
     */
    public Enums.DeviationType getDeviationType() {
        return deviationType;
    }

    /**
     * Gets the remarks related to the deviation.
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Gets the risk assessment of the deviation.
     * @return the risk assessment text
     */
    public String getRiskAssessment() {
        return riskAssessment;
    }

    /**
     * Gets the standard procedure involved in the deviation.
     * @return the standard procedure text
     */
    public String getStandardProcedure() {
        return standardProcedure;
    }

    /**
     * Gets the status of the deviation.
     * @return the deviation status
     */
    public Enums.DeviationStatus getStatus() {
        return status;
    }

    /**
     * Gets the time of identification of the deviation.
     * @return the time of identification
     */
    public Timestamp getTimeOfIdentification() {
        return timeOfIdentification;
    }

    // Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setDateOfOccurrence(Date dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeviationNumber(String deviationNumber) {
        this.deviationNumber = deviationNumber;
    }

    public void setDeviationSeverity(Enums.DeviationSeverity deviationSeverity) {
        this.deviationSeverity = deviationSeverity;
    }

    public void setDeviationType(Enums.DeviationType deviationType) {
        this.deviationType = deviationType;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public void setStandardProcedure(String standardProcedure) {
        this.standardProcedure = standardProcedure;
    }

    public void setStatus(Enums.DeviationStatus status) {
        this.status = status;
    }

    public void setTimeOfIdentification(Timestamp timeOfIdentification) {
        this.timeOfIdentification = timeOfIdentification;
    }

    // toString Method

    @Override
    public String toString() {
        return "Deviation{" +
                "id=" + id +
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