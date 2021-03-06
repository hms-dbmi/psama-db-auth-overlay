define(['common/session', 'picSure/settings', 'common/searchParser', 'jquery', 'handlebars', 'text!login/login.hbs', 'text!login/not_authorized.hbs', 'overrides/login', 'util/notification'],
		function(session, settings, parseQueryString, $, HBS, loginTemplate, notAuthorizedTemplate, overrides, notification){

	var loginTemplate = HBS.compile(loginTemplate);

	return {
		showLoginPage : function(){
		    console.log("showLoginPage - DB()");
		    
		    var queryObject = parseQueryString();
            if (queryObject.redirection_url) sessionStorage.redirection_url = queryObject.redirection_url.trim();
            
		    //Attach ajax /authentication call to login form
		    
		    $('#main-content').html(loginTemplate());
		    
            overrides.postRender ? overrides.postRender.apply(this) : undefined;
            
            $("#loginButton").on("click", function(){
            	  $.ajax({
                      url: '/psama/authentication',
                      type: 'post',
                      data: JSON.stringify({
                          username: $("#userInput").val(),
                          password: $("#passInput").val()
                      }),
                      contentType: 'application/json',
                      success: function(data){
                          session.authenticated(data.userId, data.token, data.email, data.permissions, JSON.parse(data.acceptedTOS), JSON.parse(data.mustChangePassword), this.handleNotAuthorizedResponse);
                          console.log("must change is " + data.mustChangePassword);
                          if (data.mustChangePassword == 'true'){
                        	  history.pushState({}, "", "/psamaui/userProfile");
                          } else if (data.acceptedTOS !== 'true'){
                              history.pushState({}, "", "/psamaui/tos");
                          } else {
                              if (sessionStorage.redirection_url) {
                                  window.location = sessionStorage.redirection_url;
                              }
                              else {
                                  history.pushState({}, "", "/psamaui/userManagement");
                              }
                          }
                      }.bind(this),
                      error: function(data){
                    	  message = "Unable to authenticate. Try again or contact administrator if error persists.";
                    	  
                    	  if(data.responseJSON.errorType == "application_error" && data.responseJSON.message){
                    		message = data.responseJSON.message;  
                    	  }
                    		  
                          notification.showFailureMessage(message);
                          history.pushState({}, "", sessionStorage.not_authorized_url? sessionStorage.not_authorized_url : "/psamaui/not_authorized?redirection_url=/picsureui");
                      }
                  });
            	  
            })
            
            //add some enter button handlers for convenience
            $('#userInput').keypress(function(event){
                var keycode = (event.keyCode ? event.keyCode : event.which);
                if(keycode == '13'){
                    $("#passInput").focus();  
                }
            });
            
            $('#passInput').keypress(function(event){
                var keycode = (event.keyCode ? event.keyCode : event.which);
                if(keycode == '13'){
                    $("#loginButton").click();  
                }
            });
		},
		
        handleNotAuthorizedResponse : function () {
            console.log("DB0-handleNotAuthorizedResponse()");

            if (JSON.parse(sessionStorage.session).token) {
                if (sessionStorage.not_authorized_url)
                    window.location = sessionStorage.not_authorized_url;
                else
                    window.location = "/psamaui/not_authorized" + window.location.search;
            }
            else {
                console.log("No token in session, so redirect to logout...");
                return null; //window.location = "/psamaui/logout" + window.location.search;
            }
        },
        displayNotAuthorized : function () {
            console.log("DB-displayNotAuthorized()");
            if (overrides.displayNotAuthorized)
                overrides.displayNotAuthorized()
            else
                $('#main-content').html(HBS.compile(notAuthorizedTemplate)({helpLink:settings.helpLink}));
        }
    };
});
