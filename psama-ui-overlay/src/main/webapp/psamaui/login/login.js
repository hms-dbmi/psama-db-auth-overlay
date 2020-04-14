define(['common/session', 'picSure/settings', 'common/searchParser', 'jquery', 'handlebars', 'text!login/login.hbs', 'text!login/not_authorized.hbs', 'overrides/login', 'util/notification', 'login/fence_login'],
		function(session, settings, parseQueryString, $, HBS, loginTemplate, notAuthorizedTemplate, overrides, notification, fenceLogin){

	var loginTemplate = HBS.compile(loginTemplate);

	var login = {
		showLoginPage : function(){
		    console.log("Auth0-showLoginPage()");
		    
		    //Attach ajax /authentication call to login form
		    
		    $('#main-content').html(loginTemplate());
		    
            overrides.postRender ? overrides.postRender.apply(this) : undefined;
            
            $("#loginButton").on("click", function(){
            	console.log("llee");
            	username = 
            	
            	  $.ajax({
                      url: '/psama/authentication',
                      type: 'post',
                      data: JSON.stringify({
                          username: $("#userInput").val(),
                          password: $("#passInput").val()
                      }),
                      contentType: 'application/json',
                      success: function(data){
                          session.authenticated(data.userId, data.token, data.email, data.permissions, data.acceptedTOS, this.handleNotAuthorizedResponse);
                          if (data.acceptedTOS !== 'true'){
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
                          notification.showFailureMessage("Failed to authenticate with provider. Try again or contact administrator if error persists.")
                          history.pushState({}, "", sessionStorage.not_authorized_url? sessionStorage.not_authorized_url : "/psamaui/not_authorized?redirection_url=/picsureui");
                      }
                  });
            	  
            })
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
            console.log("Auth0-displayNotAuthorized()");
            if (overrides.displayNotAuthorized)
                overrides.displayNotAuthorized()
            else
                $('#main-content').html(HBS.compile(notAuthorizedTemplate)({helpLink:settings.helpLink}));
        }
    };
	return settings.idp_provider == "fence" ? fenceLogin : login;
});
