package dao;


import model.*;
import java.util.logging.Logger;
import utils.DatabaseUtility;
import java.sql.*;
import java.util.logging.*;
import java.util.*;import java.util.Calendar;
import java.util.Date;



public class DeviationDAO {


/**
 * Initiates the CFT review process for a specific deviation.
 * @param status The current status of the deviation.
 * @param id The unique identifier of the deviation.
 * @param reviewerComments Comments from the reviewer.
 * @param reviewDecision The decision made by the reviewer.
 * @param justificationForReturning Justification text for returning the deviation.
 * @return {@code true} if the update is successful, otherwise {@code false}.
 */
public Boolean initiateCFTReview(Enums.DeviationStatus status, int id, String reviewerComments, Boolean reviewDecision, String justificationForReturning) {
  Connection connection = null;
  PreparedStatement preparedStatement = null;
  int result = 0;
  try {
    connection = DatabaseUtility.connect();
    connection.setAutoCommit(false);
    String query = "UPDATE deviations SET status = ?, review_comment = ?, review_decision = ?, justification_for_returning = ? WHERE id = ? AND status = ?::deviation_status";
    preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, Enums.DeviationStatus.ONGOING_CFT_REVIEW.name());
    preparedStatement.setString(2, reviewerComments);
    preparedStatement.setBoolean(3, reviewDecision);
    preparedStatement.setString(4, justificationForReturning);
    preparedStatement.setInt(5, id);
    preparedStatement.setString(6, status.name());
    result = preparedStatement.executeUpdate();
    connection.commit();
  } catch (SQLException e) {
    Logger.getLogger(this.getClass().getName()).severe("Error while initiating CFT review: " + e.getMessage());
    if (connection != null) {
      try {
        connection.rollback();
      } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Rollback failed: " + ex.getMessage());
      }
    }
    return false;
  } finally {
    DatabaseUtility.disconnect(connection);
  }
  return result > 0;
}

/**
 * This method facilitates the escalation of a deviation directly to QA review, bypassing the conventional CFT review step if necessary.
 * It transitions the deviation status to 'pending_qa_review' from either 'complete_department_review' or 'complete_cft_review' states, expediting QA involvement in critical cases.
 * 
 * @param id      The unique identifier of the deviation to be escalated.
 * @param status The current status of the deviation, which should be either 'complete_department_review' or 'complete_cft_review'.
 * @return A boolean value indicating the success of the escalation operation. True signifies successful escalation, while false implies an error or invalid state transition.
 * @throws SQLException If any SQL-related errors occur during the update process.
 */
public boolean escalateToQAReview(int id, Enums.DeviationStatus status) throws SQLException {
    if (!(status == Enums.DeviationStatus.DEPARTMENT_REVIEW_COMPLETED || status == Enums.DeviationStatus.CFT_REVIEW_COMPLETE)) {
        Logger.getLogger(this.getClass().getName()).severe("Invalid state transition. Deviation must be in 'complete_department_review' or 'complete_cft_review' status for escalation to QA review.");
        return false;
    }

    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ?::deviation_status WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, Enums.DeviationStatus.PENDING_QA_REVIEW.name());
        statement.setInt(2, id);
        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Links the investigation outcome with the corresponding deviation record, updating relevant fields.
 *
 * @param findings        The investigation's findings
 * @param conclusion      The investigation's conclusion
 * @param deviationsId   The ID of the associated deviation
 * @param riskAssessment The updated risk assessment
 * @return {@code true} if the update is successful, {@code false} otherwise
 */
public boolean linkInvestigationOutcome(String findings, String conclusion, int deviationsId, String riskAssessment) {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET remarks = ?, review_comment = ?, risk_assessment = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, findings);
        statement.setString(2, conclusion);
        statement.setString(3, riskAssessment);
        statement.setInt(4, deviationsId);

        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error linking investigation outcome: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Set Review Reminder: Schedules a reminder for upcoming reviews or actions that need to be taken on a deviation, ensuring that all stakeholders are notified in advance to prevent delays in the deviation handling process.
 *
 * @param deviationId The ID of the deviation for which the reminder is being set.
 * @param reviewDate The date on which the review or action is due.
 * @param reminderDays The number of days in advance of the review date to send the reminder.
 * @return A boolean value indicating whether the reminder was successfully set.
 */
public boolean setReviewReminder(int deviationId, Date reviewDate, int reminderDays) {
    Connection connection = null;
    try {
        connection = DatabaseUtility.connect();
        // Calculate the reminder date based on the review date and reminder days.
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reviewDate);
        calendar.add(Calendar.DATE, -reminderDays);
        Date reminderDate = calendar.getTime();

        // Prepare the SQL statement to insert the reminder into the notifications table.
        String sql = "INSERT INTO notifications (notification_type, message_content, timestamp, user_recipient_id, deviations_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, Enums.NotificationType.REMINDER.name());
        statement.setString(2, "Reminder: Review for Deviation " + deviationId + " is due on " + reviewDate);
        statement.setTimestamp(3, new Timestamp(reminderDate.getTime()));
        // TODO: Set the user_recipient_id based on the assigned reviewer or responsible person.
        statement.setInt(4, 1); // Replace with actual user ID
        statement.setInt(5, deviationId);

        // Execute the statement and return true if successful.
        int rowsInserted = statement.executeUpdate();
        return rowsInserted > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error setting review reminder: " + e.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Completes the department review of a deviation, updating its status to either
 * 'complete_department_review', 'deviation_returned', or 'deviation_dropped'
 * based on the reviewer's decision. Captures reviewer comments and triggers
 * appropriate next steps: continuation to the next review stage, return to
 * the initiator for re-initiation, or justification for dropping the deviation.
 *
 * @param deviationId The unique identifier of the deviation being reviewed.
 * @param newStatus The updated status of the deviation (complete_department_review, deviation_returned, deviation_dropped).
 * @param reviewComment The reviewer's comments on the deviation.
 * @return A boolean indicating the success or failure of the operation.
 * @throws SQLException If a database error occurs.
 */
public boolean completeDepartmentReview(int deviationId, Enums.DeviationStatus newStatus, String reviewComment) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ?::deviation_status, review_comment = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, newStatus.name());
        statement.setString(2, reviewComment);
        statement.setInt(3, deviationId);

        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Assigns a CFT Reviewer to a deviation if a Cross-Functional Team (CFT) assessment is required.
 *
 * @param userId                     The ID of the user performing the action
 * @param deviationId                The ID of the deviation
 * @param departmentId              The ID of the selected CFT department (if applicable)
 * @param crossFunctionalAssessmentRequired True if a CFT assessment is needed, false otherwise
 * @param cftDepartmentSelection       The ID of the chosen CFT department (if applicable)
 * @param userSelection                The ID of the selected user for CFT review (if applicable)
 * @return True if the operation was successful, false otherwise
 * @throws SQLException If a database error occurs
 */
public boolean assignCFTReviewer(int userId, int deviationId, Integer departmentId, boolean crossFunctionalAssessmentRequired,
        Integer cftDepartmentSelection, Integer userSelection) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        connection.setAutoCommit(false);

        // Update deviation status based on CFT assessment requirement
        String updateDeviationStatusSql = "UPDATE deviations SET status = ? WHERE id = ?";
        PreparedStatement updateDeviationStatusStatement = connection.prepareStatement(updateDeviationStatusSql);
        if (crossFunctionalAssessmentRequired) {
            updateDeviationStatusStatement.setString(1, Enums.DeviationStatus.PENDING_CFT_REVIEW.name());
        } else {
            updateDeviationStatusStatement.setString(1, Enums.DeviationStatus.APPROVED_BY_QA.name()); // Assuming bypass to QA
        }
        updateDeviationStatusStatement.setInt(2, deviationId);
        updateDeviationStatusStatement.executeUpdate();

        if (crossFunctionalAssessmentRequired) {
            // Update deviation with selected CFT department
            String updateCFTDepartmentSql = "UPDATE deviations SET cft_department_id = ? WHERE id = ?";
            PreparedStatement updateCFTDepartmentStatement = connection.prepareStatement(updateCFTDepartmentSql);
            updateCFTDepartmentStatement.setInt(1, cftDepartmentSelection);
            updateCFTDepartmentStatement.setInt(2, deviationId);
            updateCFTDepartmentStatement.executeUpdate();

            // Assign CFT reviewer
            String assignReviewerSql = "INSERT INTO cft_reviewers (deviation_id, user_id) VALUES (?, ?)";
            PreparedStatement assignReviewerStatement = connection.prepareStatement(assignReviewerSql);
            assignReviewerStatement.setInt(1, deviationId);
            assignReviewerStatement.setInt(2, userSelection);
            assignReviewerStatement.executeUpdate();
        }

        // Create audit trail entry
        String auditTrailSql = "INSERT INTO audit_trails (user_id, action_type, description) VALUES (?, ?, ?)";
        PreparedStatement auditTrailStatement = connection.prepareStatement(auditTrailSql);
        auditTrailStatement.setInt(1, userId);
        auditTrailStatement.setString(2, Enums.ActionType.UPDATE.name());
        auditTrailStatement.setString(3, "CFT Reviewer assigned to deviation: " + deviationId);
        auditTrailStatement.executeUpdate();

        connection.commit();
        return true;
    } catch (SQLException e) {
        connection.rollback();
        Logger.getLogger(this.getClass().getName()).severe("Error assigning CFT Reviewer: " + e.getMessage());
        throw e; // Re-throw exception for further handling
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Retries the department review for a deviation, resetting its status and adding a timestamp and remarks.
 *
 * @param status       The new status of the deviation (should be PENDING_DEPARTMENT_REVIEW)
 * @param timestamp    The timestamp of the retry action
 * @param remarks      Additional remarks regarding the retry
 * @param deviationsId The ID of the deviation to retry
 * @return True if the operation was successful, false otherwise
 * @throws SQLException If a database error occurs
 */
public boolean retryDepartmentReview(Enums.DeviationStatus status, Timestamp timestamp, String remarks, int deviationsId) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ?::deviation_status, remarks = ?, updated_at = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, status.name());
        statement.setString(2, remarks);
        statement.setTimestamp(3, timestamp);
        statement.setInt(4, deviationsId);

        int rowsUpdated = statement.executeUpdate();
        return rowsUpdated > 0;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Generates a compliance report for a specific deviation, providing a comprehensive overview of the deviation handling process including reviews, approvals, and corrective actions.
 * 
 * @param approvalDate       The approval date for filtering approvals.
 * @param endDate            The end date for the reporting period.
 * @param deviationsId      The ID of the deviation for which to generate the report.
 * @param completionDate    The completion date for filtering CAPAs.
 * @param startDate          The start date for the reporting period.
 * @return A compliance report containing relevant deviation details and related activities.
 * @throws SQLException If a database access error occurs.
 */
public ComplianceReport generateComplianceReport(Date approvalDate, Date endDate, int deviationsId, Date completionDate, Date startDate) throws SQLException {
    Connection connection = DatabaseUtility.connect();
    try {
        // Implement logic to retrieve and compile compliance report data using provided parameters and database connection
        // ...

        ComplianceReport report = new ComplianceReport(); // Assuming a 'ComplianceReport' class exists to structure the report data
        // Populate the report object with retrieved data
        // ...

        return report;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}

/**
 * Captures the outcome of an investigation and updates the associated deviation record.
 *
 * @param remediationActionTaken The remediation action taken based on the investigation.
 * @param riskAssessment        The updated risk assessment based on the investigation findings.
 * @param investigationId         The ID of the investigation.
 * @return {@code true} if the update was successful, {@code false} otherwise.
 */
public boolean captureInvestigationOutcome(String remediationActionTaken, String riskAssessment, int investigationId) {
    String query = "UPDATE deviations SET risk_assessment = ?, remarks = ? WHERE id = (SELECT deviations_id FROM investigations WHERE id = ?)";
    try (Connection connection = DatabaseUtility.connect();
         PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, riskAssessment);
        statement.setString(2, remediationActionTaken);
        statement.setInt(3, investigationId);
        return statement.executeUpdate() > 0;
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error capturing investigation outcome: " + ex.getMessage());
        return false;
    }
}

/**
 * Records an approval for a specific deviation in the system.
 *
 * @param approvalDate     The date of the approval
 * @param approvalStatus   The status of the approval (e.g., Approved, Rejected, Pending)
 * @param approverId       The ID of the user who provided the approval
 * @param approverRole     The role of the approver within the organization
 * @param approvalComments Any additional comments associated with the approval
 * @param approverName     The name of the approver
 * @param deviationId     The ID of the deviation being approved
 * @return The ID of the newly created approval record, or -1 if an error occurred
 */
public int recordApproval(Date approvalDate, Enums.ApprovalStatus approvalStatus, int approverId,
        String approverRole, String approvalComments, String approverName, int deviationId) {
    int approvalId = -1;
    Connection connection = null;
    try {
        connection = DatabaseUtility.connect();
        String sql = "INSERT INTO approvals (approval_date, approval_status, approver, approver_role, approval_comments, approver_name, deviations_id) VALUES (?, ?::approval_status, ?, ?, ?, ?, ?)"
                + " RETURNING id;";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setDate(1, new java.sql.Date(approvalDate.getTime()));
        statement.setString(2, approvalStatus.name());
        statement.setInt(3, approverId);
        statement.setString(4, approverRole);
        statement.setString(5, approvalComments);
        statement.setString(6, approverName);
        statement.setInt(7, deviationId);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            approvalId = resultSet.getInt("id");
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error recording approval: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return approvalId;
}

/**
     * Creates a new deviation in the database.
     *
     * @param timeOfIdentification The time the deviation was identified
     * @param dateOfOccurrence The date the deviation occurred
     * @param description A description of the deviation
     * @param riskAssessment The risk assessment for the deviation
     * @param standardProcedure The standard procedure that was not followed
     * @param remarks Additional remarks about the deviation
     * @param deviationType The type of deviation
     * @param reasonOrRootCauseForDeviation The reason or root cause for the deviation
     * @param immediateActions Immediate actions to take for the deviation
     * @param productSelection The product associated with the deviation
     * @param batch The batch associated with the deviation
     * @param impactOnBatchesInvolved Whether the deviation impacts batches
     * @param material The material associated with the deviation
     * @param equipment The equipment associated with the deviation
     * @param justificationForDelay Justification for any delay in reporting the deviation
     * @return The ID of the newly created deviation
     * @throws SQLException If there is an error creating the deviation
     */
    public Integer createDeviation(Timestamp timeOfIdentification, Date dateOfOccurrence, String description, String riskAssessment, String standardProcedure, String remarks, Enums.DeviationType deviationType, String reasonOrRootCauseForDeviation, String immediateActions, Integer productSelection, Integer batch, Boolean impactOnBatchesInvolved, Integer material, Integer equipment, String justificationForDelay) throws SQLException {
        Logger.getLogger(this.getClass().getName()).info("Creating deviation");
        Connection connection = DatabaseUtility.connect();
        try {
            String sql = "INSERT INTO deviations (time_of_identification, date_of_occurrence, description, risk_assessment, standard_procedure, remarks, deviation_type, reason_or_root_cause_for_deviation, immediate_actions, product_id, batch_id, impact_on_batches_involved, material_id, equipment_id, justification_for_delay) VALUES (?, ?, ?, ?, ?, ?, ?::deviation_type, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setTimestamp(1, timeOfIdentification);
            statement.setDate(2, new java.sql.Date(dateOfOccurrence.getTime()));
            statement.setString(3, description);
            statement.setString(4, riskAssessment);
            statement.setString(5, standardProcedure);
            statement.setString(6, remarks);
            statement.setString(7, deviationType.name());
            statement.setString(8, reasonOrRootCauseForDeviation);
            statement.setString(9, immediateActions);
            if (productSelection != null) {
                statement.setInt(10, productSelection);
            } else {
                statement.setNull(10, Types.INTEGER);
            }
            if (batch != null) {
                statement.setInt(11, batch);
            } else {
                statement.setNull(11, Types.INTEGER);
            }
            statement.setBoolean(12, impactOnBatchesInvolved);
            if (material != null) {
                statement.setInt(13, material);
            } else {
                statement.setNull(13, Types.INTEGER);
            }
            if (equipment != null) {
                statement.setInt(14, equipment);
            } else {
                statement.setNull(14, Types.INTEGER);
            }
            statement.setString(15, justificationForDelay);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating deviation failed, no ID obtained.");
                }
            } else {
                throw new SQLException("Creating deviation failed, no rows affected.");
            }
        } finally {
            DatabaseUtility.disconnect(connection);
        }
    }

/**
 * Updates specific fields of an existing deviation record.
 *
 * @param id                 the unique identifier of the deviation to update
 * @param description       the updated description of the deviation
 * @param riskAssessment   the updated risk assessment
 * @param standardProcedure the updated standard procedure
 * @param remediationActionTaken the remediation action taken
 * @return true if the update was successful, false otherwise
 */
public boolean updateDeviationDetails(int id, String description, String riskAssessment, String standardProcedure, String remediationActionTaken) {
    boolean updateSuccessful = false;
    String sql = "UPDATE deviations SET description = ?, risk_assessment = ?, standard_procedure = ?, remarks = ? WHERE id = ?";

    try (Connection connection = DatabaseUtility.connect();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, description);
        statement.setString(2, riskAssessment);
        statement.setString(3, standardProcedure);
        statement.setString(4, remediationActionTaken);
        statement.setInt(5, id);

        int rowsUpdated = statement.executeUpdate();
        updateSuccessful = rowsUpdated > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error updating deviation details: " + e.getMessage());
    }

    return updateSuccessful;
}


/**
 * Closes a deviation by updating its status to 'closed'.
 *
 * @param id     the unique identifier of the deviation to close
 * @param status the status to set for the deviation (should be 'closed')
 * @return {@code true} if the deviation was successfully closed, {@code false}
 *         otherwise
 */
public boolean closeDeviation(int id, Enums.DeviationHandlingStatus status) {
    String sql = "UPDATE deviations SET status = ?::deviation_handling_status WHERE id = ?";
    try (Connection connection = DatabaseUtility.connect();
         PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, status.name());
        statement.setInt(2, id);
        return statement.executeUpdate() > 0;
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error closing deviation: " + e.getMessage());
        return false;
    }
}

/**
 * This method facilitates the review of a deviation by the closer department, typically Quality Assurance or Compliance, to ensure all necessary steps have been taken and the deviation can be officially closed.
 * @param deviation_number The unique identifier of the deviation being reviewed.
 * @param status The updated status of the deviation after the closer department's review.
 * @param description A detailed description of the deviation and its resolution process.
 * @param risk_assessment An evaluation of the potential risks associated with the deviation.
 * @param remarks Additional comments or observations regarding the deviation and its closure.
 * @return A boolean value indicating whether the deviation review update was successful.
 */
public boolean reviewByCloserDepartment(String deviation_number, Enums.DeviationStatus status, String description, String risk_assessment, String remarks) {
    boolean success = false;
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET status = ?::deviation_status, description = ?, risk_assessment = ?, remarks = ? WHERE deviation_number = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, status.name());
        statement.setString(2, description);
        statement.setString(3, risk_assessment);
        statement.setString(4, remarks);
        statement.setString(5, deviation_number);
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            success = true;
            Logger.getLogger(this.getClass().getName()).info("Deviation review by closer department updated successfully for deviation number: " + deviation_number);
        } else {
            Logger.getLogger(this.getClass().getName()).warning("Deviation review update failed. Deviation number " + deviation_number + " not found.");
        }
    } catch (SQLException e) {
        Logger.getLogger(this.getClass().getName()).severe("Error during deviation review update: " + e.getMessage());
    } finally {
        DatabaseUtility.disconnect(connection);
    }
    return success;
}

/**
 * Initiates the department review process for a deviation.
 *
 * @param reviewComments     The reviewer's comments on the deviation
 * @param deviationStatus   The new status of the deviation after review
 * @param deviationId       The ID of the deviation being reviewed
 * @param justification     Justification for returning or dropping the deviation (if applicable)
 * @return                  True if the review was successfully submitted, false otherwise
 */
public boolean initiateDepartmentReview(String reviewComments, Enums.DeviationStatus deviationStatus, int deviationId, String justification) {
    Connection connection = DatabaseUtility.connect();
    try {
        String sql = "UPDATE deviations SET review_comment = ?, status = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, reviewComments);
        statement.setString(2, deviationStatus.name());
        statement.setInt(3, deviationId);
        int rowsUpdated = statement.executeUpdate();

        if (deviationStatus == Enums.DeviationStatus.DEVIATION_RETURNED || deviationStatus == Enums.DeviationStatus.DEVIATION_DROPPED) {
            sql = "INSERT INTO deviation_remarks (deviations_id, content, created_at) VALUES (?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, deviationId);
            statement.setString(2, justification);
            statement.setTimestamp(3, new Timestamp(new Date().getTime()));
            statement.executeUpdate();
        }

        return rowsUpdated > 0;
    } catch (SQLException ex) {
        Logger.getLogger(this.getClass().getName()).severe("Error initiating department review: " + ex.getMessage());
        return false;
    } finally {
        DatabaseUtility.disconnect(connection);
    }
}
}
