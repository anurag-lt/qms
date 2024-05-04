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
			var projectId = $('#current_project_id').val();// Assuming project_id is available in the template
			$('#save_journey_form').attr('action', '/demo/JourneyUpdation?project_id=' + projectId);

			// Submit the form
			$('#save_journey_form').submit();
		});


		// Attach a click event listener to elements with the class 'delete-step' for deleting unsaved journey steps

		$('.delete-step').click(function(e) {
			// Remove the closest parent div with class col-md-12 or col-lg-12
			$(this).closest('.col-md-12, .col-lg-12').remove();
		});

	$(".widget-container").each(function() { // For each widget container
    var $this = $(this); // Current widget container
    $this.find(".widgetrows").sortable({
        items: ".widgetclass", // Only elements with 'widgetclass' can be sorted
        connectWith: $this.find(".widgetrows"), // Allows items to be moved within the same '.widget-container'
        placeholder: "widget-placeholder", // A class to style the placeholder
        helper: 'clone', // Use a clone as the drag helper to keep the original in place
        start: function(event, ui) {
            // Get the size of the widget being dragged
            var widgetSize = ui.item.find(".text-secondary").text();
            // Set the placeholder's background and size text
            ui.placeholder.addClass("placeholder-highlight "+widgetSize).css({
                               'background-color': 'yellow',
                               'min-height': '60px'
                           }).text('Drop Here');
        },
        stop: function(event, ui) {
            // Optional: Remove custom styles or perform other actions when dragging stops
        },
        update: function(event, ui) {
            // Triggered when a widget is dropped into a new position
            var widgetId = ui.item.data("widgetid"); // Extract widget ID using data attribute
            var newRowId = ui.item.closest(".widgetrows").data("row_id"); // Get the row ID where the widget was dropped
            console.log('newRowId ' + newRowId + ' widgetId ' + widgetId);

            // AJAX call to update widget position
            $.ajax({
                url: '/admin/widget?method=UPDATE_WIDGET_ROW', // Your server-side endpoint
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({ id: widgetId, row_id: newRowId }),
                success: function(response) {
                    console.log('Widget position updated successfully', response);
                },
                error: function(xhr, status, error) {
                    console.error('Error updating widget position', error);
                }
            });
        }
    }).disableSelection(); // Prevents text selection while dragging widgets
});



		$('.edit-widget-btn').on('click', function() {
			// Extract widget data from data attributes
			var widgetId = $(this).data('widget-id');
			var widgetSize = $(this).data('widget-size');
			var widgetQuery = $(this).data('widget-query');
			var widgetName = $(this).data('widgetname');
			var widgetVisualizationType = $(this).data('widget-visualizationtype');
			$('#editWidgetVisualizationType').val(widgetVisualizationType); // Set the select to the correct visualization type

			// Populate the form in the modal
			$('#editWidgetId').val(widgetId);
			$('#editWidgetSize').val(widgetSize);
			$('#editWidgetQuery').val(widgetQuery);
			$('#editWidgetModalLabel').html('Edit ' + widgetName);

			// Show the modal
			$('#editWidgetModal').modal('show');
		});
		
		$(".widgetclass").each(function() {
  var widgetId = $(this).attr("data-widgetid");
  var visualizationType = $(this).find('.text-info').text();
  
  console.log("Widget ID: " + widgetId);
  console.log("Visualization Type: " + visualizationType);
  // Check if visualizationType is not equal to 'STATS_WIDGET'
  if (visualizationType !== 'STATS_WIDGET') {
    // Assume you have a function to get the HTML content URL based on widgetId or visualizationType
    
    // Create an iframe element
    var iframe = $('<iframe>', {
      src: "/chart.jsp?id="+widgetId,
      width: '100%', // Set width as per your requirement
      height: '400px', // Set height as per your requirement
      frameborder: '0'
    });
    
    // Append the iframe to the widget-preview div of this widget
    $(this).find('.' + widgetId + '.widget-preview').append(iframe);
  }else{
	  
	  			    var widgetUrl = '/widget/' + visualizationType;

	   $.ajax({
      url: widgetUrl, // Replace 'YourUrlHere' with the actual URL
      type: 'GET',
      data: {
        id: widgetId,
        id_external: '' // Replace 'YourExternalIdHere' with the actual external ID, if it's dynamic consider using a method to retrieve it.
      },
      success: function(response) {
        // Process each stat widget's response
        // Example: Display the response in the widget-preview div
        $('.' + widgetId + '.widget-preview').html(response); // Adjust this according to how you want to display the response
      },
      error: function(error) {
        console.error('Error loading widget:', error);
      }
    });
	  
  }
  

});
		
		
		
		
		
		

	});
	
	var ajaxPromises = []; // Array to hold promises for AJAX calls

	// Iterate over each widget with class 'unique'
	$('.widget.unique').each(function() {
		// Extract widget data attributes
		var widgetId = $(this).data('widget_id');
		var widgetType = $(this).data('widget_visualizationtype');
		var widgetObservation = $(this).data('widget_observation');
		var cards = $(this);
		var chartJsScript = $(this).data('widget_js_script');

		// Determine the URL for the AJAX call based on widget properties
		var Url = '/widget/' + widgetType;
		if (chartJsScript != null) {
			Url = '/chart2.jsp';
			if (chartJsScript === 'HighGPTFail') {
				Url = '/chart3.jsp';
			}
		}

		// Create a promise for the AJAX call
		var promise = new Promise(function(resolve) {
			$.ajax({
				url: Url,
				type: 'GET',
				data: {
					id: widgetId,
					id_external: '<%=id%>', 
					project_id: '<%=projectid%>'
				},
				success: function(response) {
					// On success, update the widget's HTML and resolve the promise
					try{
							$(cards).html(response);
							 
						}catch(e){
							console.log(widgetType)
							console.log(e)
						}
						//$('#widget_'+widgetId).html(response);"
					resolve();
				},
				error: function(error) {
					// Log error and resolve the promise to ensure all promises complete
					console.error('Error loading widget:', error);
					resolve();
				}
			});
		});

		// Add the promise to the array of promises
		ajaxPromises.push(promise);
	});

	// Wait for all AJAX calls to complete
	Promise.all(ajaxPromises).then(function() {
		// Code to run after all widgets have been processed
	});