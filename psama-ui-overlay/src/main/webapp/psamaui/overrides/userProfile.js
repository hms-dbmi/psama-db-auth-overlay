define(["handlebars", 'common/session', "picSure/userFunctions", "text!options/modal.hbs","text!overrides/userProfile.hbs",'util/notification',], 
	function(HBS, session, userFunctions, modalTemplate, userProfileTemplate,notification, ){
	
		this.modalTemplate = HBS.compile(modalTemplate);
	    this.userProfileTemplate = HBS.compile(userProfileTemplate);
	    
	    this.updatePassword = function(){
	    	oldPass = $("#oldPassInput").val();
	    	newPass = $("#newPassInput").val();
	    	confirmPass = $("#confirmPassInput").val();
	    	
	    	if(newPass !== confirmPass){
	    		console.log("FAIL");
	    		notification.showFailureMessage("Passwords do not match, please try again");
	    	} else {
	    		
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
                        console.log("success");
                        
                        notification.showSuccessMessage("password successfully changed");
                    }.bind(this),
                    error: function(data){
                        notification.showFailureMessage("Failed to update password. Try again or contact administrator if error persists.")
                    }
                });
	    	}
	    }
	    
		return {
			userProfile: function (event, baseClass) {
				  userFunctions.meWithToken(this, function(user){
		                $("#modal-window").html(this.modalTemplate({title: "OVERRIDE"}));
		                $("#modalDialog").show();
		                $(".modal-body").html(this.userProfileTemplate({user:user}));
		                $("#user-token-copy-button").click(baseClass.copyToken);
		                $("#user-token-refresh-button").click(baseClass.refreshToken);
		                $('#user-token-reveal-button').click(baseClass.revealToken);
		                $("#change-password-button").click(this.updatePassword);
		                $('.close').click(baseClass.closeDialog);
		            }.bind(this));
			}.bind(this),
	    };
});