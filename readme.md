 Heroku REST Server
=========================

A Simple Java app, which can easily be deployed to Heroku. Implements a RESTful server  to work with git-repos. 
Created for reseach aims.


Overview
==================

REST server has 5 endpoints to access to. Currently only http GET methods accepted, all data is returned in JSON format.

1) /  - returns info about all cloned repositories

2) /repo:
with parameter url - the server clones the git repository which url points to, makes it active and returns general info about it
without parameters - returns info about current active cloned repository.

3) /repo/branches  - returns list of  all branches


4) /content:
with required parameter filename - the server search for a file with the name 'filename' and returns its pointer (sha). If such a file does not exist, returns code 404.

5) /test:
introduced for testing. Returns status code 200 and empty object.

Running Locally
===========================

Dependencies:

Maven  3.x
Java JDK 1.8
Heroku CLI aka [Heroku Toolbelt](https://toolbelt.heroku.com/)

$ cd <restserver root directory>
$ mvn clean install
$ heroku local:start

Your app should now be running on [localhost:5000](http://localhost:5000/).


Deploying to Heroku
============================


$ heroku create
$ git push heroku master
$ heroku open

Reference
=====================


[Java on Heroku](https://devcenter.heroku.com/categories/java)

[Example in Java](https://devcenter.heroku.com/articles/getting-started-with-java)

[JGit main page](http://www.eclipse.org/jgit/)

