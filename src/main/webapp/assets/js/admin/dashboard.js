$(document).ready(function() {

		$("#zero_config").DataTable({
			"columnDefs": [
				{
				"width": "5%",
				"targets": 0
			},	
			{
				"width": "20%",
				"targets": 1
			},	
			{
				"width": "55%",
				"targets": 2
			}, // Targets the second column (process name)
			{
				"width": "5%",
				"targets": 3
			},
			{
				"width": "15%",
				"targets": 4
			},
			{
            "orderable": false, // Disables sorting
            "targets": 5 // Targets the last column
        }
			]
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
	
	$(document).on('click', '.edit-project-btn', function() {
        var projectId = $(this).data('id');
        var projectName = $(this).data('name');
        var projectDescription = $(this).data('description');
        var projectType = $(this).data('type');

        // Populate the modal fields
        $('#editProjectModal #projectName').val(projectName);
        $('#editProjectModal #projectDescription').val(projectDescription);
        // Populate other fields similarly based on your modal's structure
$('#editProjectModal #projectType').val(projectType);
$('#editProjectModal #projectid').val(projectId);

        // Show the modal
        $('#editProjectModal').modal('show');
    });
});