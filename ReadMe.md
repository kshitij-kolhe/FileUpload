# Getting Started

### Running standalone application on embedded tomcat server

1. run command "mvn spring-boot:run" from inside application root

### Running application on external tomcat server

1. Build application using "mvn install"
2. Copy the FileUpload.war file from target folder
3. Go to tomact webapp folder
4. Paste the FileUpload.war file in tomcat webapp folder
5. Start tomcat server
6. Go to "http://localhost:8080/FileUpload" the application will be running

### Unit Tests
There are a total of 5 tests. 

First test is to test upload endpoint which accepts multipart request and stores the file on server.

Second test is to test upload endpoint which accepts multipart request and replaces the file on server if file already exists on server.

Third test is to tes download endpoint which download the file requested using the filename, if present.

Fourth test is to test retrieve list of uploaded files endpoint and return a list of filenames 

Fifth test is to test retrieve list of uploaded files endpoint and return nothing if no files are uploaded. 

### Endpoints (REST API's)
There are 3 endpoints, to upload file using multipart request, to download file using filename, and retrieve a list of uploaded files.

1. Upload Endpoint (**POST** request)
   1. URL (http://localhost:8080/file) if running on embedded tomcat server
   2. URL (http://localhost:8080/FileUpload/file) if running on eternal tomcat serve

2. Download Endpoint (**GET** request)
    1. URL (http://localhost:8080/file?file=filename) if running on embedded tomcat server
    2. URL (http://localhost:8080/FileUpload/file?file=filename) if running on eternal tomcat serve

3. Retrieve list of uploaded file (**GET** request)
   1. URL (http://localhost:8080/files) if running on embedded tomcat server
   2. URL (http://localhost:8080/FileUpload/files) if running on eternal tomcat serve
