package model;

import java.util.Date;

/**
 * Class representing deviation assessments within the deviation management system. 
 * Each assessment captures opinions or evaluations regarding the impact, cause, 
 * or remedial actions associated with a deviation.
 */
public class DeviationAssessment {
    
    /**
     * Unique identifier for the deviation assessment.
     */
    private int id;
    
    /**
     * Date on which the assessment was performed.
     */
    private Date assessmentDate;
    
    /**
     * Result or outcome of the assessment.
     */
    private String assessmentResult;
    
    /**
     * Numerical score or rating resulting from the assessment.
     */
    private double assessmentScore;
    
    /**
     * Type of the assessment performed.
     */
    private Enums.AssessmentType assessmentType;
    
    /**
     * Identifier for the deviation to which this assessment is linked.
     */
    private int deviationsId;

    /**
     * Constructs an empty DeviationAssessment instance.
     */
    public DeviationAssessment() {
    }

    // Getters
    public int getId() {
        return id;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public String getAssessmentResult() {
        return assessmentResult;
    }

    public double getAssessmentScore() {
        return assessmentScore;
    }

    public Enums.AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public int getDeviationsId() {
        return deviationsId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAssessmentDate(Date assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public void setAssessmentResult(String assessmentResult) {
        this.assessmentResult = assessmentResult;
    }

    public void setAssessmentScore(double assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public void setAssessmentType(Enums.AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public void setDeviationsId(int deviationsId) {
        this.deviationsId = deviationsId;
    }

    /**
     * Returns a string representation of the DeviationAssessment object.
     * @return A string representing the DeviationAssessment object.
     */
    @Override
    public String toString() {
        return "DeviationAssessment{" + 
               "id=" + id + 
               ", assessmentDate=" + assessmentDate + 
               ", assessmentResult='" + assessmentResult + '\'' + 
               ", assessmentScore='" + assessmentScore + '\'' +
               ", assessmentType=" + assessmentType + 
               ", deviationsId=" + deviationsId + 
               '}';
    }
}