package model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Represents a deviation within the quality management system.
 */
public class Deviation {
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

    /**
     * Gets the unique identifier for the deviation.
     * @return id as int
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the deviation.
     * @param id unique identifier
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the date when the deviation was identified.
     * @return identification date
     */
    public Date getDateOfIdentification() {
        return dateOfIdentification;
    }

    /**
     * Sets the date when the deviation was identified.
     * @param dateOfIdentification date of identification
     */
    public void setDateOfIdentification(Date dateOfIdentification) {
        this.dateOfIdentification = dateOfIdentification;
    }

    /**
     * Gets the date when the deviation occurred.
     * @return occurrence date
     */
    public Date getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    /**
     * Sets the date when the deviation occurred.
     * @param dateOfOccurrence date of occurrence
     */
    public void setDateOfOccurrence(Date dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    /**
     * Gets the description of the deviation.
     * @return deviation description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the deviation.
     * @param description deviation description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the deviation number.
     * @return deviation number
     */
    public String getDeviationNumber() {
        return deviationNumber;
    }

    /**
     * Sets the deviation number.
     * @param deviationNumber unique deviation code
     */
    public void setDeviationNumber(String deviationNumber) {
        this.deviationNumber = deviationNumber;
    }

    /**
     * Gets the severity of the deviation.
     * @return deviation severity
     */
    public Enums.DeviationSeverity getDeviationSeverity() {
        return deviationSeverity;
    }

    /**
     * Sets the severity of the deviation.
     * @param deviationSeverity severity level
     */
    public void setDeviationSeverity(Enums.DeviationSeverity deviationSeverity) {
        this.deviationSeverity = deviationSeverity;
    }

    /**
     * Gets the type of the deviation.
     * @return deviation type
     */
    public Enums.DeviationType getDeviationType() {
        return deviationType;
    }

    /**
     * Sets the type of the deviation.
     * @param deviationType type of deviation
     */
    public void setDeviationType(Enums.DeviationType deviationType) {
        this.deviationType = deviationType;
    }

    /**
     * Gets the remarks associated with the deviation.
     * @return remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets the remarks associated with the deviation.
     * @param remarks additional notes
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the risk assessment of the deviation.
     * @return risk assessment
     */
    public String getRiskAssessment() {
        return riskAssessment;
    }

    /**
     * Sets the risk assessment of the deviation.
     * @param riskAssessment risk evaluation
     */
    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    /**
     * Gets the standard procedure involved in the deviation.
     * @return standard procedure
     */
    public String getStandardProcedure() {
        return standardProcedure;
    }

    /**
     * Sets the standard procedure involved in the deviation.
     * @param standardProcedure procedure name
     */
    public void setStandardProcedure(String standardProcedure) {
        this.standardProcedure = standardProcedure;
    }

    /**
     * Gets the status of the deviation.
     * @return deviation status
     */
    public Enums.DeviationStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the deviation.
     * @param status current status
     */
    public void setStatus(Enums.DeviationStatus status) {
        this.status = status;
    }

    /**
     * Gets the time of identification of the deviation.
     * @return time of identification
     */
    public Timestamp getTimeOfIdentification() {
        return timeOfIdentification;
    }

    /**
     * Sets the time of identification of the deviation.
     * @param timeOfIdentification time stamp
     */
    public void setTimeOfIdentification(Timestamp timeOfIdentification) {
        this.timeOfIdentification = timeOfIdentification;
    }

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