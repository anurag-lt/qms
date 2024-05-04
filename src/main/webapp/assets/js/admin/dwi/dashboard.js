$(document).ready(function() {

	$(document).ready(function() {
		$("#zero_config").DataTable({
			"columnDefs": [{
				"width": "30%",
				"targets": 2
			}, // Targets the second column (process name)
			]
		});

	});

	window.toggleReadMore = function(projectId) {
		var dots = $("#dots" + projectId);
		var moreText = $("#more" + projectId);
		// Fetch the button by its dynamically generated ID
		var btn = $("#btn-more-" + projectId);

		// Toggle visibility and update button text based on the 'moreText' element's visibility
		if (moreText.is(":visible")) {
			dots.show();
			moreText.hide();
			btn.text("Read more"); // Update button text to "Read more"
		} else {
			dots.hide();
			moreText.show();
			btn.text("Read less"); // Update button text to "Read less"
		}
	}
});