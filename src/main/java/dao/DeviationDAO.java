package dao;


import model.*;
import java.util.logging.Logger;
import utils.DatabaseUtility;
import java.sql.*;
import java.util.logging.*;
import java.util.*;import model.Enums.DeviationStatus;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;



public class DeviationDAO {


/**
 * Initiates the CFT review process for a given deviation, updating its status.
 *
 * @param id the ID of the deviation to update.
 * @param reviewerComments comments provided by the reviewer.
 * @param reviewDecision the decision of the review, true for approved, false for not approved.
 * @param justificationForReturning text justification for returning the deviation, if applicable.
 * @return true if the update was successful, false otherwise.
 */
public Boolean initiateCFTReview(Integer id, String reviewerComments, Boolean reviewDecision, String justificationForReturning) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    Integer result = 0;
    try {
        conn = DatabaseUtility.connect();
        String sql = "UPDATE deviations SET status = ?::deviation_status, reviewer_comments = ?, review_decision = ?, justification_for_returning = ? WHERE id = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, DeviationStatus.ONGOING_CFT_REVIEW.name());
        pstmt.setString(2, reviewerComments);
        pstmt.setBoolean(3, reviewDecision);
        if (justificationForReturning == null) {
            pstmt.setNull(4, Types.VARCHAR);
        } else {
            pstmt.setString(4, justificationForReturning);
        }
        pstmt.setInt(5, id);
        result = pstmt.executeUpdate();
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during initiating CFT Review: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(conn);
    }
    return result > 0;
}

/**
     * Escalate to QA Review: This action advances the deviation from either 'complete_department_review' or 'complete_cft_review' statuses directly to 'pending_qa_review', facilitating quicker engagement of the QA team in critical cases that demand immediate attention or in scenarios where faster resolution is imperative.
     * @param id
     * @param status
     * @return
     */
    public boolean escalateToQAReview(int id, DeviationStatus status) {
        String sql = "UPDATE deviations SET status = ?::deviation_status WHERE id = ? AND (status = ?::deviation_status OR status = ?::deviation_status)";

        try (Connection connection = DatabaseUtility.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setInt(2, id);
            statement.setString(3, DeviationStatus.DEPARTMENT_REVIEW_COMPLETED.name());
            statement.setString(4, DeviationStatus.CFT_REVIEW_COMPLETE.name());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error during escalateToQAReview: " + ex.getMessage());
            return false;
        }
    }

/**
 * Initiate Department Review: This action allows the user to initiate the department review process for a deviation, changing its status from 'pending_department_review' to 'ongoing_department_review' in the system. This facilitates the formal departmental assessment phase of the deviation management workflow.
 * @param comments
 * @param id
 * @param status
 * @param decision__selection
 * @param justification
 * @return
 */
public Boolean initiateDepartmentReview(String comments, Integer id, DeviationStatus status, Boolean decision__selection, String justification) {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET comments = ?, status = ?, decision__selection = ?, justification = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, comments);
        statement.setString(2, status.name());
        if (decision__selection != null) {
            statement.setBoolean(3, decision__selection);
        } else {
            statement.setNull(3, Types.BOOLEAN);
        }
        statement.setString(4, justification);
        statement.setInt(5, id);
        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error during initiating department review: " + ex.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Associates the results of an investigation directly with the deviation record, updating the deviation's data fields concerned with findings, corrective actions suggested, and any changes to risk assessments. This action is crucial for aligning the investigation outcomes with the ongoing deviation resolution process.
 * @param conclusion
 * @param findings
 * @param deviations_id
 * @param risk_assessment
 * @return
 */
public boolean linkInvestigationOutcome(String conclusion, String findings, Integer deviations_id, String risk_assessment) {
    boolean isSuccessful = false;
    Connection connection = DatabaseUtility.connect();
    try {
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE deviations SET remarks = ?, risk_assessment = ? WHERE id = ?");
        statement.setString(1, conclusion);
        statement.setString(2, risk_assessment);
        if (deviations_id != null) {
            statement.setInt(3, deviations_id);
        } else {
            statement.setNull(3, Types.INTEGER);
        }
        int rowsUpdated = statement.executeUpdate();
        isSuccessful = rowsUpdated > 0;
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe(ex.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return isSuccessful;
}


/**
 * Set Review Reminder: Schedules a reminder for upcoming reviews or actions that need to be taken on a deviation, ensuring that all stakeholders are notified in advance to prevent delays in the deviation handling process.
 */
public void setReviewReminder() throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        // Logic to identify deviations requiring reminders based on deadlines or upcoming review dates
        // ...

        // Assuming we have a list of user IDs who need reminders (userIds)
        List<Integer> userIds = new ArrayList<>(); // Replace with actual logic to get user IDs

        for (int userId : userIds) {
            // Create notification for each user
            Notification notification = new Notification();
            notification.setUserRecipientId(userId);
            notification.setNotificationType(NotificationType.REMINDER);
            notification.setMessageContent("Reminder: Action required on a deviation."); // Customize message
            notification.setTimestamp(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));

            // Insert notification into the 'notifications' table
            String sql = "INSERT INTO notifications (user_recipient_id, notification_type, message_content, timestamp) VALUES (?, ?::notification_type, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, notification.getUserRecipientId());
            statement.setString(2, notification.getNotificationType().name());
            statement.setString(3, notification.getMessageContent());
            statement.setTimestamp(4, notification.getTimestamp());
            statement.executeUpdate();
        }
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Completes the department review for a specific deviation, transitioning its status based on the reviewer's decision and capturing their comments.
 * @param deviationId The unique identifier of the deviation undergoing review.
 * @param newStatus The updated status of the deviation after review ('complete_department_review', 'deviation_returned', or 'deviation_dropped').
 * @param reviewerComments Optional comments provided by the reviewer regarding the deviation.
 * @return A boolean indicating whether the department review completion was successful.
 * @throws SQLException If a database access error occurs during the update operation.
 */
public boolean completeDepartmentReview(int deviationId, DeviationStatus newStatus, String reviewerComments) throws SQLException {
    String updateQuery = "UPDATE deviations SET status = ?::deviation_status, remarks = ? WHERE id = ?";
    try (Connection connection = DatabaseUtility.connect();
            PreparedStatement statement = connection.prepareStatement(updateQuery)) {
        statement.setString(1, newStatus.name());
        statement.setString(2, reviewerComments);
        statement.setInt(3, deviationId);
        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Assign CFT Reviewer: This action allows selection of whether a Cross-Functional Team (CFT) assessment is required. If required, a CFT department is selected from a master list, and a user from the list of users is assigned to perform the CFT review.
 * @param user_id
 * @param deviation_id
 * @param department_id
 * @param cross_functional_assessment_required
 * @param c_f_t_department_selection
 * @param user_selection
 * @return
 * @throws SQLException
 */
public boolean assignCFTReviewer(Integer user_id, Integer deviation_id, Integer department_id, Boolean cross_functional_assessment_required, Integer c_f_t_department_selection, Integer user_selection) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String query = "UPDATE deviations SET status = ?::deviation_status WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, DeviationStatus.PENDING_CFT_REVIEW.name());
        statement.setInt(2, deviation_id);
        int rowsUpdated = statement.executeUpdate();
        statement.close();
        if (rowsUpdated > 0) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deviation status updated successfully.");
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Failed to update deviation status.");
            return false;
        }

        // Additional logic for assigning CFT reviewer if required
        if (cross_functional_assessment_required) {
            // Implement logic to assign CFT reviewer based on department and user selection
            // You might need to update the deviations table or create a new record in a related table to track the assigned reviewer
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "CFT assessment required. Further logic needs to be implemented for reviewer assignment.");
        }

        return true;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error assigning CFT reviewer", e);
        throw e;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Retry Department Review: Enables the record to be sent back to the department review phase after adjustments or additional information has been added following a CFT or QA review. This action ensures that deviations deemed incompletely reviewed or requiring more details can be re-evaluated at the department level.
 * @param status
 * @param timestamp
 * @param remarks
 * @param deviationsId
 * @return
 */
public boolean retryDepartmentReview(DeviationStatus status, Timestamp timestamp, String remarks, Integer deviationsId) {
        String query = "UPDATE deviations SET status = ?::deviation_status, remarks = ?, updated_at = ? WHERE id = ?";
        try (Connection connection = DatabaseUtility.connect();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            statement.setString(2, remarks);
            statement.setTimestamp(3, timestamp);
            statement.setInt(4, deviationsId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error during retryDepartmentReview: " + ex.getMessage());
            return false;
        }
    }

/**
     * Generate Compliance Report: Compiles and generates reports detailing the deviation handling process including all related reviews, approvals, and corrective actions for compliance and audit purposes. Facilitates adherence to industry regulations and internal standards.
     *
     * @param approvalDate The date of approval for the deviation
     * @param endDate The end date for the report generation
     * @param deviationsId The ID of the deviation
     * @param completionDate The completion date for the CAPA
     * @param startDate The start date for the report generation
     * @return List<ComplianceReportData> A list of compliance report data objects
     */
    public List<ComplianceReportData> generateComplianceReport(LocalDate approvalDate, LocalDate endDate, Integer deviationsId, LocalDate completionDate, LocalDate startDate) {
        String query = "SELECT a.approval_date, a.approval_status, a.approval_comments, d.deviation_number, d.description, d.deviation_type, d.deviation_severity, d.status AS deviation_status, d.date_of_occurrence, d.risk_assessment, c.action_description, c.start_date, c.completion_date, c.effectiveness_review\n" +
                "FROM approvals a\n" +
                "JOIN deviations d ON a.deviations_id = d.id\n" +
                "LEFT JOIN capas c ON d.id = c.deviations_id\n" +
                "WHERE (a.approval_date >= ? AND a.approval_date <= ?) OR (d.id = ?) OR (c.start_date >= ? AND c.completion_date <= ?)";

        List<ComplianceReportData> reportData = new ArrayList<>();

        try (Connection connection = DatabaseUtility.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, startDate);
            statement.setObject(2, endDate);
            statement.setObject(3, deviationsId);
            statement.setObject(4, startDate);
            statement.setObject(5, completionDate);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ComplianceReportData data = new ComplianceReportData();
                    data.setApprovalDate(resultSet.getDate("approval_date").toLocalDate());
                    data.setApprovalStatus(Enums.ApprovalStatus.valueOf(resultSet.getString("approval_status")));
                    data.setApprovalComments(resultSet.getString("approval_comments"));
                    data.setDeviationNumber(resultSet.getString("deviation_number"));
                    data.setDescription(resultSet.getString("description"));
                    data.setDeviationType(Enums.DeviationType.valueOf(resultSet.getString("deviation_type")));
                    data.setDeviationSeverity(Enums.DeviationSeverity.valueOf(resultSet.getString("deviation_severity")));
                    data.setDeviationStatus(DeviationStatus.valueOf(resultSet.getString("deviation_status")));
                    data.setDateOfOccurrence(resultSet.getDate("date_of_occurrence").toLocalDate());
                    data.setRiskAssessment(resultSet.getString("risk_assessment"));
                    data.setCapaActionDescription(resultSet.getString("action_description"));
                    data.setCapaStartDate(resultSet.getDate("start_date").toLocalDate());
                    data.setCapaCompletionDate(resultSet.getDate("completion_date").toLocalDate());
                    data.setCapaEffectivenessReview(resultSet.getString("effectiveness_review"));
                    reportData.add(data);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error generating compliance report: " + ex.getMessage());
        }

        return reportData;
    }

/**
 * Captures the outcome of an investigation, updating the corresponding deviation record with the investigation's findings, conclusions, and any resulting risk reassessment or remediation actions.
 * @param remediationActionTaken The remediation action taken based on the investigation's findings.
 * @param riskAssessment The updated risk assessment following the investigation.
 * @param investigationId The ID of the investigation record.
 * @return A boolean value indicating the success or failure of the update operation.
 * @throws SQLException If a database access error occurs.
 */
public boolean captureInvestigationOutcome(String remediationActionTaken, String riskAssessment, int investigationId) throws SQLException {
    String updateDeviationSql = "UPDATE deviations SET risk_assessment = ?, remarks = ? WHERE id = (SELECT deviations_id FROM investigations WHERE id = ?)";

    Connection connection = DatabaseUtility.connect();
    try (PreparedStatement statement = connection.prepareStatement(updateDeviationSql)) {
        statement.setString(1, riskAssessment);
        statement.setString(2, remediationActionTaken);
        statement.setInt(3, investigationId);

        return statement.executeUpdate() > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Record Approval: This action entails recording each approval through various stages of the deviation handling process, including department-level, CFT review, and QA closures. It involves inserting a new record in the 'approvals' table linked to the deviation, which is vital for maintaining traceable, auditable records of all authorization steps as mandated by quality management protocols.
 * @param approval_date
 * @param approval_status
 * @param approver
 * @param approver_role
 * @param approval_comments
 * @param approver_name
 * @param deviations_id
 * @return
 */
public int recordApproval(Date approval_date, Enums.ApprovalStatus approval_status, int approver, String approver_role, String approval_comments, String approver_name, int deviations_id) {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "INSERT INTO approvals (approval_date, approval_status, approver, approver_role, approval_comments, approver_name, deviations_id) VALUES (?, ?::approval_status, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setDate(1, approval_date);
        statement.setString(2, approval_status.name());
        statement.setInt(3, approver);
        statement.setString(4, approver_role);
        statement.setString(5, approval_comments);
        statement.setString(6, approver_name);
        statement.setInt(7, deviations_id);

        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                Logger.getLogger(this.getClass().getName()).severe("Failed to retrieve generated ID after inserting approval");
                return -1;
            }
        } else {
            Logger.getLogger(this.getClass().getName()).severe("Failed to insert approval into database");
            return -1;
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error recording approval", e);
        return -1;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Creates a new deviation record and returns its ID.
 *
 * @param time_of_identification The time of identification of the deviation.
 * @param date_of_occurrence The date of occurrence of the deviation.
 * @param description A detailed description of the deviation.
 * @param risk_assessment An assessment of the risks associated with the deviation.
 * @param standard_procedure The standard procedure that should have been followed.
 * @param remarks Any additional remarks or notes about the deviation.
 * @param deviation_type The type of deviation (e.g., product, equipment, material, document).
 * @param equipment The ID of the equipment involved in the deviation (if applicable).
 * @param batch The ID of the batch involved in the deviation (if applicable).
 * @param product_selection The ID of the product involved in the deviation (if applicable).
 * @param impact_on_batches_involved Whether the deviation has an impact on batches.
 * @param immediate_actions Immediate actions taken to address the deviation.
 * @param reason_or_root_cause_for_deviation The reason or root cause of the deviation.
 * @param material The ID of the material involved in the deviation (if applicable).
 * @param justification_for_delay Justification for any delay in reporting the deviation.
 * @return The ID of the newly created deviation record.
 * @throws SQLException If there is an error creating the deviation record.
 */
public int createDeviation(Timestamp time_of_identification, Date date_of_occurrence, String description, String risk_assessment, String standard_procedure, String remarks, Enums.DeviationType deviation_type, Integer equipment, Integer batch, Integer product_selection, Boolean impact_on_batches_involved, String immediate_actions, String reason_or_root_cause_for_deviation, Integer material, String justification_for_delay) throws SQLException {
    Logger.getLogger(this.getClass().getName()).info("Creating deviation record...");

    Connection connection = DatabaseUtility.connect();
    try {
        String query = "INSERT INTO deviations (time_of_identification, date_of_occurrence, description, risk_assessment, standard_procedure, remarks, deviation_type, equipment_id, batch_id, product_id, impact_on_batches_involved, immediate_actions, reason_or_root_cause_for_deviation, material_id, justification_for_delay) VALUES (?, ?, ?, ?, ?, ?, ?::deviation_type, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setTimestamp(1, time_of_identification);
        statement.setDate(2, date_of_occurrence);
        statement.setString(3, description);
        statement.setString(4, risk_assessment);
        statement.setString(5, standard_procedure);
        statement.setString(6, remarks);
        statement.setString(7, deviation_type.name());
        statement.setObject(8, equipment);
        statement.setObject(9, batch);
        statement.setObject(10, product_selection);
        statement.setObject(11, impact_on_batches_involved);
        statement.setString(12, immediate_actions);
        statement.setString(13, reason_or_root_cause_for_deviation);
        statement.setObject(14, material);
        statement.setString(15, justification_for_delay);

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating deviation failed, no rows affected.");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating deviation failed, no ID obtained.");
            }
        }
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Update Deviation Details: This involves updating existing deviation records, which may include modifying descriptions, updating risk assessments, or altering the standard procedure references. This action updates specific fields in an existing record within the 'deviations' table, catering to iterative changes and additional information that becomes available as the deviation handling process advances.
 * @param id
 * @param description
 * @param riskAssessment
 * @param standardProcedure
 * @param remediationActionTaken
 * @return
 */
public Boolean updateDeviationDetails(Integer id, String description, String riskAssessment, String standardProcedure, String remediationActionTaken) {
    Boolean updateStatus = false;
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET description = ?, risk_assessment = ?, standard_procedure = ?, remarks = ? WHERE id = ?;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, description);
        statement.setString(2, riskAssessment);
        statement.setString(3, standardProcedure);
        statement.setString(4, remediationActionTaken);
        statement.setInt(5, id);
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            updateStatus = true;
        }
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error updating deviation details: " + ex.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return updateStatus;
}


/**
     * Closes a deviation by updating its status to 'closed'.
     * @param id the ID of the deviation to close
     * @return true if the deviation was successfully closed, false otherwise
     */
    public boolean closeDeviation(int id) {
        String sql = "UPDATE deviations SET status = ?::deviation_handling_status WHERE id = ?";
        try (Connection connection = DatabaseUtility.connect();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, Enums.DeviationStatus.CLOSED.name());
            statement.setInt(2, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error closing deviation: " + ex.getMessage());
            return false;
        }
    }

/**
 * This method encapsulates the logic for reviewing a deviation by the closer department.
 * It updates the deviation record with the provided details and sets the status
 * to "reviewed_by_closer_department".
 * @param deviationNumber The unique identifier of the deviation.
 * @param status The updated status of the deviation, specifically "reviewed_by_closer_department".
 * @param description A detailed description of the deviation.
 * @param riskAssessment The associated risk assessment for the deviation.
 * @param remarks Additional comments or observations related to the deviation.
 * @return A boolean value indicating the success or failure of the update operation.
 */
public boolean reviewByCloserDepartment(String deviationNumber, DeviationStatus status, String description, String riskAssessment, String remarks) {
    boolean isSuccessful = false;
    Connection connection = null;
    try {
        connection = DatabaseUtility.connect();
        String sql = "UPDATE deviations SET status = ?::deviation_status, description = ?, risk_assessment = ?, remarks = ? WHERE deviation_number = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, status.name());
        statement.setString(2, description);
        statement.setString(3, riskAssessment);
        statement.setString(4, remarks);
        statement.setString(5, deviationNumber);
        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            isSuccessful = true;
            Logger.getLogger(this.getClass().getName()).info("Deviation reviewed by closer department: " + deviationNumber);
        } else {
            Logger.getLogger(this.getClass().getName()).warning("Deviation review failed: " + deviationNumber);
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during deviation review: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return isSuccessful;
}
}
