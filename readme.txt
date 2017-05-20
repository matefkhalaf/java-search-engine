How to run:

The project is divided into two parts:

1: Interface: This part contains the interface of the search engine, the interface is developed using javascript and java servlets.

How to setup the interface:

1- Install apache tomcat server.
2- Move SearchEngine.jsp to ...\Apache Software Foundation\Tomcat 8.5\webapps\ROOT.
3- Move DB.java/ActionServlet.java/QueryProcessing.java/SearchForm.java/Stemmer.java to ...\Apache Software Foundation\Tomcat 8.5\webapps\ROOT\WEB-INF\Classes
4- Modify Web.xml file to include the servlets.
5- Move files in jar folder to ...\Apache Software Foundation\Tomcat 8.5\lib
6- Run apache tomcat server.

2: Search engine logic: This part is an eclipse project which contains Indexer/Crawler/Ranker
This part uses sql database which is found in .sql file attached to project contents.
