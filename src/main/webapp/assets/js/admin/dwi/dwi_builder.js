$(document).ready(
	function() {
		$('.addMetric').click(
			function() {
				var stepId = $(this).attr('data-step-id');
				var metricHTML = '<div class="input-group mb-2">'
					+ '<input type="text" name="metrics_' + stepId + '" class="form-control" placeholder="Enter metric">'
					+ '<button class="btn btn-danger remove-metric" type="button">-</button>' + '</div>';
				$('#metricsContainer' + stepId).append(metricHTML);
			});

		$(document).on('click', '.remove-metric', function() {
			$(this).closest('.input-group').remove();
		});

		$('.addQuestion').click(
			function() {
				var stepId = $(this).attr('data-step-id');
				var questionHTML = '<div class="input-group mb-2">'
					+ '<input type="text" name="questions_' + stepId + '" class="form-control" placeholder="Enter question">'
					+ '<button class="btn btn-danger remove-question" type="button">-</button>' + '</div>';
				$('#questionsContainer' + stepId).append(questionHTML);
			});

		$(document).on('click', '.remove-question', function() {
			$(this).closest('.input-group').remove();
		});

		// Attach click event listener to the Update Journey button
		$('.update-journey-button').click(function() {
			$(this).hide();
			// Change form action
			var feedbackValue = $('#journey_feedback').val();
			$('<input>').attr({
				type: 'hidden',
				name: 'feedback',
				value: feedbackValue
			}).appendTo('#save_journey_form');
			var projectId = $('#current_project_id').val(); // Assuming project_id is available in the template
			$('#save_journey_form').attr('action', '/dwi/JourneyUpdation?project_id=' + projectId);

			// Submit the form
			$('#save_journey_form').submit();
		});


		// Attach a click event listener to elements with the class 'delete-step' for deleting unsaved journey steps

		$('.delete-step').click(function(e) {
			// Remove the closest parent div with class col-md-12 or col-lg-12
			$(this).closest('.col-md-12, .col-lg-12').remove();
		});

	});