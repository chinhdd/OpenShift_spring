$(document).ready(function() {
	var initPage = function() {
		console.log('Hello my page');
		
		$.ajax({
			url : "rest/members",
			data : 'data',
			type : 'GET',
			contentType : "application/json",
			xhrFields: {
				  withCredentials: true
			}
		}).done(function(val) {
			console.log(val);		
		}).fail(function(val) {
			console.log(val);
		});
		
		$.ajax({
			url : "getNumber",
			data : 'data',
			type : 'GET',
			contentType : "application/json",
			xhrFields: {
				  withCredentials: true
			}
		}).done(function(val) {
			console.log(val);		
		}).fail(function(val) {
			console.log(val);
		});
		
		$.ajax({
			url : "getNumber",
			data : 'data',
			type : 'GET',
			contentType : "application/json",
			xhrFields: {
				  withCredentials: true
			}
		}).done(function(val) {
			console.log(val);		
		}).fail(function(val) {
			console.log(val);
		});
	};
	
	initPage();
});