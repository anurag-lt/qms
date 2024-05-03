package dao;


import model.*;
import java.util.logging.Logger;
import utils.DatabaseUtility;
import java.sql.*;
import java.util.logging.*;
import java.util.*;

public class DeviationDAO {


/**
     * Initiate CFT Review: This action allows the user to initiate the CFT review process for a deviation, changing its status from 'department_review_complete' to 'ongoing_cft_review' in the system. This facilitates the formal CFT assessment phase of the deviation management workflow.
     * @param id: The unique identifier of the deviation record.
     * @param status: The updated status reflecting the initiation of the CFT review.
     * @return A boolean value indicating the success or failure of the operation.
     */
    public boolean initiateCFTReview(int id, Enums.DeviationStatus status) {
        Connection connection = DatabaseUtility.connect();
        try {
            String sql = "UPDATE deviations SET status = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, status);
            statement.setInt(2, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error during CFT review initiation: " + ex.getMessage());
            return false;
        } finally {
            DatabaseUtility.disconnect(connection);
        }
    }

/**
 * This method updates a deviation's status from 'pending_cft_review' to either 'complete_cft_review' or 'deviation_returned',
 * depending on the reviewer's decision and captures reviewer comments.
 * @param deviationId the ID of the deviation to be updated
 * @param status the new status of the deviation (either 'complete_cft_review' or 'deviation_returned')
 * @param remarks comments from the reviewer
 * @return a boolean indicating success or failure of the update
 * @throws SQLException if a database error occurs
 */
public boolean completeCFTReview(int deviationId, Enums.DeviationStatus status, String remarks) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ?, remarks = ? WHERE id = ? AND status = 'PENDING_CFT_REVIEW'";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, status.name());
        statement.setString(2, remarks);
        statement.setInt(3, deviationId);
        int rowsAffected = statement.executeUpdate();
        return rowsAffected == 1;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
     * Escalate to QA Review: This action advances the deviation from either 'complete_department_review' or 'complete_cft_review' statuses directly to 'pending_qa_review', facilitating quicker engagement of the QA team in critical cases that demand immediate attention or in scenarios where faster resolution is imperative.
     * @param id
     * @param status
     * @return boolean
     */
    public boolean escalateToQAReview(int id, Enums.DeviationStatus status) {
        String sql = "UPDATE deviations SET status = ? WHERE id = ? AND (status = ? OR status = ?)";
        try (Connection connection = DatabaseUtility.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, Enums.DeviationStatus.PENDING_QA_REVIEW);
            statement.setInt(2, id);
            statement.setObject(3, Enums.DeviationStatus.ONGOING_DEPARTMENT_REVIEW);
            statement.setObject(4, Enums.DeviationStatus.UNDER_CFT_REVIEW);
            int rowsAffected = statement.executeUpdate();
            Logger.getLogger(this.getClass().getName()).info("Escalated deviation " + id + " to QA Review");
            return rowsAffected > 0;
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).severe("Error escalating deviation to QA Review: " + ex.getMessage());
            return false;
        }
    }

/**
 * Initiate Department Review: This action allows the user to initiate the department review process for a deviation, changing its status from 'pending_department_review' to 'ongoing_department_review' in the system. This facilitates the formal departmental assessment phase of the deviation management workflow.
 * @param id
 * @param status
 * @return boolean
 */
public boolean initiateDepartmentReview(int id, Enums.DeviationStatus status) {
    Connection connection = DatabaseUtility.connect();
    try {
        PreparedStatement statement = connection.prepareStatement("UPDATE deviations SET status = ? WHERE id = ?");
        statement.setString(1, status.name());
        statement.setInt(2, id);
        int rowsAffected = statement.executeUpdate();
        return rowsAffected > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during department review initiation: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Link Investigation Outcome: Associates the results of an investigation directly with the deviation record, updating the deviation's data fields concerned with findings, corrective actions suggested, and any changes to risk assessments. This action is crucial for aligning the investigation outcomes with the ongoing deviation resolution process.
 * @param conclusion Textual summary of the investigation's conclusions.
 * @param findings Detailed findings of the investigation.
 * @param deviations_id ID of the deviation record to update.
 * @param risk_assessment Updated risk assessment based on investigation findings.
 * @return Boolean value indicating success or failure of the update operation.
 */
public boolean linkInvestigationOutcome(String conclusion, String findings, int deviations_id, String risk_assessment) {
    boolean isSuccessful = false;
    Connection connection = DatabaseUtility.connect();
    try {
        String updateQuery = "UPDATE deviations SET remarks = ?, risk_assessment = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(updateQuery);
        statement.setString(1, findings);
        statement.setString(2, risk_assessment);
        statement.setInt(3, deviations_id);
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            isSuccessful = true;
            Logger.getLogger(this.getClass().getName()).info("Investigation outcome linked successfully to deviation: " + deviations_id);
        } else {
            Logger.getLogger(this.getClass().getName()).warning("Failed to link investigation outcome to deviation: " + deviations_id);
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during linking investigation outcome: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return isSuccessful;
}

/**
 * Set Review Reminder: Schedules a reminder for upcoming reviews or actions that need to be taken on a deviation, ensuring that all stakeholders are notified in advance to prevent delays in the deviation handling process.
 * @return
 */
public boolean setReviewReminder() {
    // Implement logic to schedule review reminders for deviations
    // This could involve querying for deviations with upcoming review dates
    // and sending notifications to relevant users.
    Logger.getLogger(this.getClass().getName()).severe("Method not implemented: setReviewReminder");
    return false;
}

/**
 * Completes the department review process for a deviation, updating the deviation's status
 * and recording reviewer comments.
 *
 * @param deviationId    The ID of the deviation being reviewed.
 * @param comments        Reviewer comments on the deviation.
 * @param reviewDecision The reviewer's decision, either 'complete_department_review', 'deviation_returned', or 'deviation_dropped'.
 * @return True if the update is successful, false otherwise.
 * @throws SQLException If a database error occurs.
 */
public boolean completeDepartmentReview(int deviationId, String comments, Enums.DeviationStatus reviewDecision) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String updateQuery = "UPDATE deviations SET status = ?, remarks = ? WHERE id = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setString(1, reviewDecision.name());
        updateStatement.setString(2, comments);
        updateStatement.setInt(3, deviationId);

        int rowsUpdated = updateStatement.executeUpdate();
        return rowsUpdated > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Assign CFT Reviewer: This action allows selection of whether a Cross-Functional Team (CFT) assessment is required. If required, a CFT department is selected from a master list, and a user from the list of users is assigned to perform the CFT review.
 * @param user_id id of the user performing the action
 * @param deviation_id id of the deviation being updated
 * @param department_id id of the department responsible for the CFT review
 * @param cross_functional_assessment_required boolean flag indicating if CFT assessment is needed
 * @param c_f_t_department_selection id of the selected CFT department
 * @param user_selection id of the user assigned for CFT review
 * @return boolean indicating success or failure of the operation
 */
public boolean assignCFTReviewer(int user_id, int deviation_id, int department_id, boolean cross_functional_assessment_required, int c_f_t_department_selection, int user_selection) {
    Connection connection = DatabaseUtility.connect();
    try {
        // Update deviation record with CFT assessment requirement and department
        String updateDeviationSql = "UPDATE deviations SET cross_functional_assessment_required = ?, c_f_t_department = ? WHERE id = ?";
        PreparedStatement updateDeviationStmt = connection.prepareStatement(updateDeviationSql);
        updateDeviationStmt.setBoolean(1, cross_functional_assessment_required);
        updateDeviationStmt.setInt(2, c_f_t_department_selection);
        updateDeviationStmt.setInt(3, deviation_id);
        updateDeviationStmt.executeUpdate();

        // If CFT assessment required, assign reviewer
        if (cross_functional_assessment_required) {
            String assignReviewerSql = "INSERT INTO cft_assignments (deviation_id, reviewer_id, department_id) VALUES (?, ?, ?)";
            PreparedStatement assignReviewerStmt = connection.prepareStatement(assignReviewerSql);
            assignReviewerStmt.setInt(1, deviation_id);
            assignReviewerStmt.setInt(2, user_selection);
            assignReviewerStmt.setInt(3, department_id);
            assignReviewerStmt.executeUpdate();
        }
        return true;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error assigning CFT reviewer: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Retry Department Review: Enables the record to be sent back to the department review phase after adjustments or additional information has been added following a CFT or QA review. This action ensures that deviations deemed incompletely reviewed or requiring more details can be re-evaluated at the department level.
 * @param status
 * @param timestamp
 * @param remarks
 * @param deviations_id
 * @return
 */
public boolean retryDepartmentReview(Enums.DeviationStatus status, Timestamp timestamp, String remarks, int deviations_id) {
    boolean result = false;
    Connection connection = DatabaseUtility.connect();
    try {
        String query = "UPDATE deviations SET status = ?, remarks = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, status.name());
        statement.setString(2, remarks);
        statement.setTimestamp(3, timestamp);
        statement.setInt(4, deviations_id);

        int rowsAffected = statement.executeUpdate();
        if (rowsAffected > 0) {
            result = true;
        } else {
            Logger.getLogger(this.getClass().getName()).severe("Failed to update deviation status for retry department review");
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during retry department review: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return result;
}

/**
 * Generate Compliance Report: Compiles and generates reports detailing the deviation handling process including all related reviews, approvals, and corrective actions for compliance and audit purposes. Facilitates adherence to industry regulations and internal standards.
 * @param approval_date date input for the report generation
 * @param end_date date input for the report generation
 * @param deviations_id int4 input for the report generation
 * @param completion_date date input for the report generation
 * @param start_date date input for the report generation
 * @return List<Map<String, Object>> a list of maps containing the report data
 */
public List<Map<String, Object>> generateComplianceReport(Date approval_date, Date end_date, int deviations_id, Date completion_date, Date start_date) {
    Connection connection = DatabaseUtility.connect();
    List<Map<String, Object>> reportData = new ArrayList<>();
    try  {
        // Your code to generate compliance report
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error generating compliance report: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return reportData;
}

/**
 * Capture Investigation Outcome: Post-investigation, this action records the findings and conclusions of the investigation into the deviation record. It updates the 'investigation' and potentially 'risk assessment' and 'remediation action taken' fields within the deviations entry, ensuring the deviation documentation reflects all investigative insights and actions recommended or taken.
 * @param investigation
 * @param riskAssessment
 * @param remediationActionTaken
 * @return
 */
public boolean captureInvestigationOutcome(int investigation, String riskAssessment, String remediationActionTaken) {
    Connection connection = DatabaseUtility.connect();
    try {
        String updateQuery = "UPDATE deviations SET risk_assessment = ?, remarks = ? WHERE id = (SELECT deviations_id FROM investigations WHERE id = ?)";
        PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
        updateStatement.setString(1, riskAssessment);
        updateStatement.setString(2, remediationActionTaken);
        updateStatement.setInt(3, investigation);
        int rowsUpdated = updateStatement.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error updating deviation record with investigation outcome: " + e.getMessage());
        return false;
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
        String query = "INSERT INTO approvals (approval_date, approval_status, approver, approver_role, approval_comments, approver_name, deviations_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setDate(1, approval_date);
        preparedStatement.setObject(2, approval_status);
        preparedStatement.setInt(3, approver);
        preparedStatement.setString(4, approver_role);
        preparedStatement.setString(5, approval_comments);
        preparedStatement.setString(6, approver_name);
        preparedStatement.setInt(7, deviations_id);
        preparedStatement.executeUpdate();
        ResultSet rs = preparedStatement.getGeneratedKeys();
        if (rs.next()) {
            int newApprovalId = rs.getInt(1);
            return newApprovalId;
        }
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error recording approval: " + ex.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return -1;
}


/**
 * Create a deviation incident based on the user input.
 * @param justificationForDelay Justification for delayed reporting (applicable if reporting is delayed).
 * @param timeOfIdentification Time when the deviation was identified.
 * @param dateOfOccurrence Date when the deviation occurred.
 * @param description Detailed description of the deviation.
 * @param riskAssessment Assessment of the risks associated with the deviation.
 * @param deviationDocumentId ID of the associated document, if applicable.
 * @param standardProcedure Standard procedure that was deviated from.
 * @param remarks Additional comments or notes.
 * @param dateOfIdentification Date when the deviation was identified.
 * @param deviationType Type of deviation (Product, Material, Equipment, Document).
 * @param productSelection ID of the associated product, if applicable.
 * @param material ID of the associated material, if applicable.
 * @param equipment ID of the associated equipment, if applicable.
 * @param document ID of the associated document, if applicable.
 * @param batchId ID of the associated batch, if applicable.
 * @param materialLotNumber Lot number of the associated material, if applicable.
 * @param reasonOrRootCauseForDeviation Reason or root cause of the deviation.
 * @param impactOnBatchesInvolved Whether the deviation impacts involved batches.
 * @param immediateActions Immediate actions taken or recommended.
 * @return The ID of the newly created deviation record, or -1 if unsuccessful.
 * @throws SQLException If a database access error occurs.
 */
public int createDeviation(String justificationForDelay, Timestamp timeOfIdentification, Date dateOfOccurrence, String description, String riskAssessment, int deviationDocumentId, String standardProcedure, String remarks, Date dateOfIdentification, Enums.DeviationType deviationType, int productSelection, int material, int equipment, int document, int batchId, String materialLotNumber, String reasonOrRootCauseForDeviation, boolean impactOnBatchesInvolved, String immediateActions) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "INSERT INTO deviations (justification_for_delay, time_of_identification, date_of_occurrence, description, risk_assessment, deviation_document_id, standard_procedure, remarks, date_of_identification, deviation_type, product_selection, material, equipment, document, batch_id, material_lot_number, reason_or_root_cause_for_deviation, impact_on_batches_involved, immediate_actions) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, justificationForDelay);
        statement.setTimestamp(2, timeOfIdentification);
        statement.setDate(3, dateOfOccurrence);
        statement.setString(4, description);
        statement.setString(5, riskAssessment);
        statement.setInt(6, deviationDocumentId);
        statement.setString(7, standardProcedure);
        statement.setString(8, remarks);
        statement.setDate(9, dateOfIdentification);
        statement.setString(10, deviationType.name());
        statement.setInt(11, productSelection);
        statement.setInt(12, material);
        statement.setInt(13, equipment);
        statement.setInt(14, document);
        statement.setInt(15, batchId);
        statement.setString(16, materialLotNumber);
        statement.setString(17, reasonOrRootCauseForDeviation);
        statement.setBoolean(18, impactOnBatchesInvolved);
        statement.setString(19, immediateActions);
        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            Logger.getLogger(this.getClass().getName()).severe("Creating deviation failed, no rows affected.");
            return -1;
        }
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                Logger.getLogger(this.getClass().getName()).severe("Creating deviation failed, no ID obtained.");
                return -1;
            }
        }
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Updates a deviation record in the database with the given details.
 *
 * @param standardProcedure The updated standard procedure related to the deviation.
 * @param remediationActionTaken The remediation action taken for the deviation.
 * @param id The ID of the deviation to update.
 * @param riskAssessment The updated risk assessment for the deviation.
 * @param description The updated description of the deviation.
 * @return true if the update was successful, false otherwise.
 */
public boolean updateDeviationDetails(String standardProcedure, String remediationActionTaken, int id, String riskAssessment, String description) {
    boolean updateSuccessful = false;
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET standard_procedure = ?, remarks = ?, risk_assessment = ?, description = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, standardProcedure);
        statement.setString(2, remediationActionTaken);
        statement.setString(3, riskAssessment);
        statement.setString(4, description);
        statement.setInt(5, id);
        int rowsUpdated = statement.executeUpdate();
        updateSuccessful = rowsUpdated > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error updating deviation details: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return updateSuccessful;
}


/**
 * Close Deviation: This action marks a deviation record as closed (changes the status to 'closed') when all necessary reviews, corrective actions, and approvals have been completed. It finalizes the deviation's lifecycle in the database and ensures compliance with internal protocols and regulatory requirements by formally documenting the resolution of the deviation.
 * @param id
 * @param status
 * @return
 */
public boolean closeDeviation(int id, Enums.DeviationHandlingStatus status) {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, status.toString());
        statement.setInt(2, id);
        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error closing deviation: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}
}
