FROM dbmi/pic-sure-auth-ui:TARGET_BUILD_VERSION as psama-ui
FROM httpd:2.4.27-alpine


# copy psama_ui repos
COPY --from=psama-ui /usr/local/apache2/htdocs/psamaui /usr/local/apache2/htdocs/psamaui

# then copy overrides
COPY src/main/webapp/psamaui /usr/local/apache2/htdocs/psamaui/

