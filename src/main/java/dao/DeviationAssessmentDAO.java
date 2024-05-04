package dao;


import model.*;
import java.util.logging.Logger;
import utils.DatabaseUtility;
import java.sql.*;
import java.util.logging.*;
import java.util.*;

public class DeviationAssessmentDAO {


/**
     * Retrieves a list of DeviationAssessment objects associated with a specific deviation.
     *
     * @param deviationsId The ID of the deviation for which to retrieve assessments.
     * @return A list of DeviationAssessment objects linked to the specified deviation, or an empty list if no assessments are found.
     */
    public List<DeviationAssessment> viewAssessments(int deviationsId) {
        String sql = "SELECT * FROM deviation_assessments WHERE deviations_id = ?";
        List<DeviationAssessment> assessments = new ArrayList<>();

        try (Connection connection = DatabaseUtility.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, deviationsId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                DeviationAssessment assessment = new DeviationAssessment();
                assessment.setId(resultSet.getInt("id"));
                assessment.setAssessmentDate(resultSet.getDate("assessment_date"));
                assessment.setAssessmentResult(resultSet.getString("assessment_result"));
                assessment.setAssessmentScore(resultSet.getDouble("assessment_score"));
                assessment.setAssessmentType(Enums.AssessmentType.valueOf(resultSet.getString("assessment_type")));
                assessment.setDeviationsId(resultSet.getInt("deviations_id"));
                assessments.add(assessment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error retrieving deviation assessments: " + ex.getMessage());
        }

        return assessments;
    }

/**
     * Updates an existing DeviationAssessment record in the database.
     *
     * @param deviationAssessment The DeviationAssessment object containing the updated data.
     * @return A boolean indicating whether the update was successful or not.
     */
    public boolean updateAssessment(DeviationAssessment deviationAssessment) {
        Connection connection = DatabaseUtility.connect();
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE deviation_assessments SET assessment_date = ?, assessment_result = ?, assessment_score = ?, assessment_type = ? WHERE id = ? AND deviations_id = ?")) {

            statement.setDate(1, new java.sql.Date(deviationAssessment.getAssessmentDate().getTime()));
            statement.setString(2, deviationAssessment.getAssessmentResult());
            statement.setDouble(3, deviationAssessment.getAssessmentScore());
            statement.setString(4, deviationAssessment.getAssessmentType().name());
            statement.setInt(5, deviationAssessment.getId());
            statement.setInt(6, deviationAssessment.getDeviationsId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error updating deviation assessment: " + ex.getMessage());
            return false;
        } finally {
            DatabaseUtility.disconnect(connection);
        }
    }

/**
     * Delete Assessment: Allows users to remove an assessment that is determined to be erroneous or irrelevant. This is crucial to maintain the integrity and relevance of the deviation dossier, ensuring all documented assessments are pertinent and valid.
     * @param id: int4
     * @return boolean
     */
    public boolean deleteAssessment(int id) {
        String sql = "DELETE FROM deviation_assessments WHERE id = ?";
        try (Connection connection = DatabaseUtility.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            Logger.getLogger(this.getClass().getName()).info("Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error deleting assessment: " + ex.getMessage());
            return false;
        }
    }

/**
 * This method is used to create a new deviation assessment and update the deviation status.
 *
 * @param probabilityOfRecurrence The likelihood of the deviation recurring.
 * @param assessmentDate The date the assessment was conducted.
 * @param deviationCloserDate The expected date for deviation closure.
 * @param assessmentResult The outcome or result of the assessment.
 * @param assessmentScore The numerical score or rating of the assessment.
 * @param deviationsId The ID of the deviation associated with this assessment.
 * @param assessmentType The type of assessment conducted (e.g., cause, impact).
 * @param deviationSeverity The severity level of the deviation.
 * @param isTheDeviationRepeated Whether the deviation has occurred before.
 * @param historicalDeviations The number of previous occurrences of the deviation.
 * @param anyMarketActionsRequired Any necessary actions related to the market.
 * @param comments Additional comments or notes about the assessment.
 * @param fileAttachment The ID of any attached file related to the assessment.
 * @return The ID of the newly created deviation assessment.
 * @throws SQLException If a database error occurs.
 */
public Integer createAssessment(Integer probabilityOfRecurrence, Date assessmentDate, Date deviationCloserDate, String assessmentResult, Double assessmentScore, Integer deviationsId, Enums.AssessmentType assessmentType, Enums.DeviationSeverity deviationSeverity, Boolean isTheDeviationRepeated, Integer historicalDeviations, String anyMarketActionsRequired, String comments, Integer fileAttachment) throws SQLException {
    Logger.getLogger(this.getClass().getName()).severe("Creating a new deviation assessment");
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "INSERT INTO deviation_assessments (probability_of_recurrence, assessment_date, deviation_closer_date, assessment_result, assessment_score, deviations_id, assessment_type, deviation_severity, is_the_deviation_repeated, historical_deviations, any_market_actions_required, comments, file_attachment) VALUES (?, ?, ?, ?, ?, ?, ?::assessment_type, ?::deviation_severity, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setObject(1, probabilityOfRecurrence);
        statement.setDate(2, new java.sql.Date(assessmentDate.getTime()));
        statement.setDate(3, new java.sql.Date(deviationCloserDate.getTime()));
        statement.setString(4, assessmentResult);
        statement.setObject(5, assessmentScore);
        statement.setObject(6, deviationsId);
        statement.setString(7, assessmentType.name());
        statement.setString(8, deviationSeverity.name());
        statement.setObject(9, isTheDeviationRepeated);
        statement.setObject(10, historicalDeviations);
        statement.setString(11, anyMarketActionsRequired);
        statement.setString(12, comments);
        statement.setObject(13, fileAttachment);

        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Integer newAssessmentId = generatedKeys.getInt(1);

                // Update deviation status
                sql = "UPDATE deviations SET status = ?::deviation_status WHERE id = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, Enums.DeviationStatus.PENDING_QA_REVIEW.name());
                statement.setInt(2, deviationsId);
                statement.executeUpdate();

                return newAssessmentId;
            }
        }
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return null;
}

/**
 * Assigns the final approver to a deviation and updates its status.
 *
 * @param deviationsId The ID of the deviation for which to assign the final approver.
 * @param id           The ID of the approval record (presumably a serial type).
 * @param userId        The ID of the user assigned as the final approver.
 * @return A boolean value indicating the success or failure of the operation.
 * @throws SQLException If an error occurs during database interaction.
 */
public boolean assignFinalApprover(int deviationsId, int id, int userId) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String query = "UPDATE approvals SET approval_status = 'approved', approver = ? WHERE deviations_id = ? AND id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userId);
        statement.setInt(2, deviationsId);
        statement.setInt(3, id);

        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            // Update the deviation status to 'pending_final_approval'
            query = "UPDATE deviations SET status = 'pending_final_approval' WHERE id = ?";
            statement = connection.prepareStatement(query);
            statement.setInt(1, deviationsId);
            statement.executeUpdate();
            return true;
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error assigning final approver: " + e.getMessage());
        throw e;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return false;
}

}
