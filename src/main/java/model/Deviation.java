package model;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Represents a deviation instance within the quality management system.
 */
public class Deviation {

    private int id;
    private String deviationNumber;
    private LocalDate dateOfOccurrence;
    private LocalDate dateOfIdentification;
    private Timestamp timeOfIdentification;
    private String description;
    private Enums.DeviationType deviationType;
    private Enums.DeviationSeverity deviationSeverity;
    private String standardProcedure;
    private String riskAssessment;
    private String remarks;
    private Enums.DeviationStatus status;

    /**
     * Gets the unique identifier for the deviation.
     *
     * @return The deviation ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the deviation.
     *
     * @param id The deviation ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the deviation number.
     *
     * @return The deviation number.
     */
    public String getDeviationNumber() {
        return deviationNumber;
    }

    /**
     * Sets the deviation number.
     *
     * @param deviationNumber The deviation number.
     */
    public void setDeviationNumber(String deviationNumber) {
        this.deviationNumber = deviationNumber;
    }

    /**
     * Gets the date when the deviation occurred.
     *
     * @return The date of occurrence.
     */
    public LocalDate getDateOfOccurrence() {
        return dateOfOccurrence;
    }

    /**
     * Sets the date when the deviation occurred.
     *
     * @param dateOfOccurrence The date of occurrence.
     */
    public void setDateOfOccurrence(LocalDate dateOfOccurrence) {
        this.dateOfOccurrence = dateOfOccurrence;
    }

    /**
     * Gets the date when the deviation was identified.
     *
     * @return The date of identification.
     */
    public LocalDate getDateOfIdentification() {
        return dateOfIdentification;
    }

    /**
     * Sets the date when the deviation was identified.
     *
     * @param dateOfIdentification The date of identification.
     */
    public void setDateOfIdentification(LocalDate dateOfIdentification) {
        this.dateOfIdentification = dateOfIdentification;
    }

    /**
     * Gets the time when the deviation was identified.
     *
     * @return The time of identification.
     */
    public Timestamp getTimeOfIdentification() {
        return timeOfIdentification;
    }

    /**
     * Sets the time when the deviation was identified.
     *
     * @param timeOfIdentification The time of identification.
     */
    public void setTimeOfIdentification(Timestamp timeOfIdentification) {
        this.timeOfIdentification = timeOfIdentification;
    }

    /**
     * Gets the description of the deviation.
     *
     * @return The deviation description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the deviation.
     *
     * @param description The deviation description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the type of deviation.
     *
     * @return The deviation type.
     */
    public Enums.DeviationType getDeviationType() {
        return deviationType;
    }

    /**
     * Sets the type of deviation.
     *
     * @param deviationType The deviation type.
     */
    public void setDeviationType(Enums.DeviationType deviationType) {
        this.deviationType = deviationType;
    }

    /**
     * Gets the severity of the deviation.
     *
     * @return The deviation severity.
     */
    public Enums.DeviationSeverity getDeviationSeverity() {
        return deviationSeverity;
    }

    /**
     * Sets the severity of the deviation.
     *
     * @param deviationSeverity The deviation severity.
     */
    public void setDeviationSeverity(Enums.DeviationSeverity deviationSeverity) {
        this.deviationSeverity = deviationSeverity;
    }

    /**
     * Gets the standard procedure involved in the deviation.
     *
     * @return The standard procedure.
     */
    public String getStandardProcedure() {
        return standardProcedure;
    }

    /**
     * Sets the standard procedure involved in the deviation.
     *
     * @param standardProcedure The standard procedure.
     */
    public void setStandardProcedure(String standardProcedure) {
        this.standardProcedure = standardProcedure;
    }

    /**
     * Gets the risk assessment for the deviation.
     *
     * @return The risk assessment.
     */
    public String getRiskAssessment() {
        return riskAssessment;
    }

    /**
     * Sets the risk assessment for the deviation.
     *
     * @param riskAssessment The risk assessment.
     */
    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    /**
     * Gets any additional remarks or notes about the deviation.
     *
     * @return The remarks.
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Sets any additional remarks or notes about the deviation.
     *
     * @param remarks The remarks.
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Gets the current status of the deviation.
     *
     * @return The deviation status.
     */
    public Enums.DeviationStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the deviation.
     *
     * @param status The deviation status.
     */
    public void setStatus(Enums.DeviationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Deviation{" +
                "id=" + id +
                ", deviationNumber='" + deviationNumber + '\'' +
                ", dateOfOccurrence=" + dateOfOccurrence +
                ", dateOfIdentification=" + dateOfIdentification +
                ", timeOfIdentification=" + timeOfIdentification +
                ", description='" + description + '\'' +
                ", deviationType=" + deviationType +
                ", deviationSeverity=" + deviationSeverity +
                ", standardProcedure='" + standardProcedure + '\'' +
                ", riskAssessment='" + riskAssessment + '\'' +
                ", remarks='" + remarks + '\'' +
                ", status=" + status +
                '}';
    }
}