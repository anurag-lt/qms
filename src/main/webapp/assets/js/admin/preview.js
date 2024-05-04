/**
 * 
 */


// Function to extract categories and values from a table by its ID
function getDataFromTable(tableId) {
	var categories = []; // Array to store categories
	var values = []; // Array to store values
	var table = document.getElementById(tableId); // Get the table by its ID
	var rows = table.getElementsByTagName('tbody')[0].rows; // Get all rows in the table's tbody

	// Loop through each row of the table
	for (var i = 0; i < rows.length; i++) {
		// Push the first cell's text (category) into categories array
		categories.push(rows[i].cells[0].innerText);
		// Convert the second cell's text (value) to a float and push into values array
		values.push(parseFloat(rows[i].cells[1].innerText));
	}

	// Return an object containing both arrays
	return { categories, values };
}


$(document).ready(function() {

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
							if(widgetObservation){
								//<span class="mb-1 badge text-bg-danger">Danger</span>
								let xx = ''
						          if(widgetType != 'STATS_WIDGET'){
						        	  xx = '<a href="javascript:void(0)" class="widget-button">'
						            +'<div class="pulse">'
						              +'<button type="button" class="btn btn-sm d-flex bg-warning-subtle w-100 d-block text-warning font-medium">New Observation Detected</button>'
 						            +'</div>'
						          +'</a>';
						          	$(cards).append(xx);

						          }
							}
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





	// Set up an event listener for clicks on elements with class 'widget-button'
	$(document).on("click", ".widget-button", function() {
		// Retrieve widget observation data and construct HTML for the toast notification
		var el = $($(this).parent()[0]);
		var text = el.data('widget_observation') + '<br/><br/><a class="btn btn-sm mb-1 waves-effect waves-light btn-primary" href="/admin/observation/observation_new?widget=' + el.data('widget_id') + '" target="_blank">Click here to investigate</a>';

		// Display the toast notification
		toastr.info(text, "Miracle Max Says", {
			closeButton: true,
			showMethod: "slideDown",
			hideMethod: "slideUp",
			timeOut: 20000,
			positionClass: "toastr toast-bottom-full-width",
			containerId: "toast-bottom-full-width",
			showDuration: "30000",
		});
	});




	// Code to mark the corresponding sidebar item as active based on the current page
	var pageId = $('#page_id').val(); // Assumes page ID is provided by server-side code
	if (pageId) {
		// Find the sidebar item for the current page and mark it as active
		var sidebarItem = $("ul#sidebarnav li[data-page_id='" + pageId + "'] a");
		sidebarItem.addClass("active");
		// Optionally, expand parent menus
		sidebarItem.parents("ul.collapse").addClass("in");
		sidebarItem.parents("li.sidebar-item").addClass("active");
	}


	$('#text-loader').show();

	// Count the total number of items within the carousel container with ID 'sync1'
	var itemCount = $("#sync1 .item").length;

	// Define responsive settings for the Owl Carousel
	// These settings determine how many items are displayed based on the viewport width
	var responsiveSettings = {
		0: {
			items: 1 // Display 1 item on screens narrower than 600px
		},
		600: {
			items: 2 // Display 2 items on screens between 600px and 999px wide
		},
		1000: {
			items: 3 // Display 3 items on screens 1000px wide and above
		}
	};

	// Calculate the maximum number of items displayed at any viewport size
	// This is used to determine if autoplay should be enabled
	var maxItemsDisplayed = Math.max(...Object.values(responsiveSettings).map(setting => setting.items));

	// Initialize Owl Carousel on the element with ID 'sync1'
	$("#sync1").owlCarousel({
		margin: 10, // Space between items
		slideSpeed: 2000, // Speed of the slide animation
		nav: true, // Enable navigation arrows
		autoplay: itemCount > maxItemsDisplayed, // Enable autoplay only if there are more items than can be displayed at once
		dots: true, // Show pagination dots
		loop: true, // Enable looping of items
		responsiveRefreshRate: 200, // Rate at which the carousel refreshes its responsive settings
		responsive: responsiveSettings, // Apply the defined responsive settings
		navText: [ // Custom SVG icons for navigation arrows
			'<svg width="12" height="12" viewBox="0 0 11 20"><path style="fill:none;stroke-width: 3px;stroke: #fff;" d="M9.554,1.001l-8.607,8.607l8.607,8.606"/></svg>',
			'<svg width="12" height="12" viewBox="0 0 11 20" version="1.1"><path style="fill:none;stroke-width: 3px;stroke: #fff;" d="M1.054,18.214l8.606,-8.606l-8.606,-8.607"/></svg>'
		],
	});


	// Call extractTextFromDataHolders function after 5 seconds
	//setTimeout(extractTextFromDataHolders, 5000);





})










function extractTextFromDataHolders() {
	// Select all elements with id="data_holder"
	var dataHolderElements = document.querySelectorAll('[id="data_holder"]');
	// Initialize an array to store the extracted text
	var extractedText = [];

	// Function to recursively extract text from nested elements
	function extractTextRecursively(element) {
		extractedText.push(element.textContent.trim());
		// Iterate through child elements and recursively extract text
		for (var i = 0; i < element.children.length; i++) {
			extractTextRecursively(element.children[i]);
		}
	}

	// Loop through the selected elements and extract text, including nested elements
	dataHolderElements.forEach(function(element) {
		extractTextRecursively(element);
	});

	console.log(extractedText);

	// Perform the AJAX request here after extracting text
	$.ajax({
		url: '/summaryServlet',  // URL of the servlet
		type: 'POST',            // Use POST method
		data: {                  // Data to send in the request
			htmlContent: extractedText.join(' ')  // Send the extracted text as a string
		},
		success: function(response) {
			// Handle the response here
			console.log('Server response:', response);
			$('#summary_card').html(response);
		},
		error: function(xhr, status, error) {
			// Handle errors here
			console.error('AJAX request failed:', error);
		}
	});
}