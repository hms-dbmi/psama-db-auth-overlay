define(["handlebars", 'common/session', "picSure/userFunctions", "text!options/modal.hbs","text!overrides/userProfile.hbs",'util/notification',], 
	function(HBS, session, userFunctions, modalTemplate, userProfileTemplate,notification, ){
	
		this.modalTemplate = HBS.compile(modalTemplate);
	    this.userProfileTemplate = HBS.compile(userProfileTemplate);
	    
	    this.updatePassword = function(){
	    	oldPass = $("#oldPassInput").val();
	    	newPass = $("#newPassInput").val();
	    	confirmPass = $("#confirmPassInput").val();
	    	
    		$.ajax({
                url: '/psama/changePassword',
                type: 'post',
                data: JSON.stringify({
                	username: session.userId(),
                	oldPass: oldPass,
                	newPass: newPass
                }),
                contentType: 'application/json',
                success: function(data){
                    notification.showSuccessMessage("password successfully changed");
                    $("#oldPassInput").val("");
		        	    $("#newPassInput").val("");
		        	    	$("#confirmPassInput").val("");	
        	    	
		        	    	$("#modalDialog").hide();
		        	    	
        	    	//update session to avoid showing this dialog again
        	    	session.setChangedPW();
        	    	
        	    	userFunctions.refreshUserLongTermToken(this, function(){
        	    		console.log("updated token");
        	    	});
                }.bind(this),
                error: function(data){
                	message = "Failed to update password. Try again or contact administrator if error persists.";
                	if(data.responseJSON.errorType == "application_error" && data.responseJSON.message){
                		message = data.responseJSON.message;  
					}
                    notification.showFailureMessage(message);
                }
            });
	    	
	    }
	    
	    this.matchInputs = function(){
	    	newPass = $("#newPassInput").val();
	    	confirmPass = $("#confirmPassInput").val();
	    	
	    	if(newPass !== confirmPass){
	    		$("#passwordMessage").html("Passwords do not match");
	    		$("#confirmPassInput").addClass("field-invalid");
	    		 $("#change-password-button").prop( "disabled", true);
	    	} else {
	    		$("#passwordMessage").html("");
	    		$("#confirmPassInput").removeClass("field-invalid");
	    		 $("#change-password-button").prop( "disabled", false );
	    	}
	    }
	    
		return {
			userProfile: function (event, baseClass) {
				  userFunctions.meWithToken(this, function(user){
					  	titleStr = "User Profile";
					  	if(session.mustChangePassword()) {
					  		titleStr = "Please Change Password";
					  		$("#tokenBlock").hide();
					  	}
		                
					  	$("#modal-window").html(this.modalTemplate({title: titleStr}));
		                $("#modalDialog").show();
		                $(".modal-body").html(this.userProfileTemplate({user:user}));
		                $("#user-token-copy-button").click(baseClass.copyToken);
		                $("#user-token-refresh-button").click(baseClass.refreshToken);
		                $('#user-token-reveal-button').click(baseClass.revealToken);
		                $("#change-password-button").click(this.updatePassword);
		                
		                //if it's a non-admin user, redirect back to main UI when closed
		                if(window.location.href.endsWith("userProfile")){
		                	$('.close').click(function(){
		                		window.location = "/picsureui/";
		                	});
		                } else {
		                	$('.close').click(baseClass.closeDialog);
		                }
		                
		                $('#confirmPassInput').on('input', this.matchInputs);
		                
		                //hide the token buttons if we need to change the password; it's unnecessary to show
		                if(session.mustChangePassword()) {
					  		$("#tokenBlock").hide();
					  	}
		                
		            }.bind(this));
			}.bind(this),
	    };
});