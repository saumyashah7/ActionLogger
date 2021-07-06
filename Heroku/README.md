# ActionLogger - Heroku

This is an installation guide for Actionlogger web application using heroku. The version we will be using from heroku is a free tier so please keep an eye out for the limitations of free versions.

## Instructions

### Heroku setup

- Setup a free heroku account from the [link.](https://signup.heroku.com/)
- After setting up, login to the account and click on *New -> create new app* from the dashboard.
- Enter name of the app and click on *Create app*
- Install the Heroku CLI from [here](https://devcenter.heroku.com/articles/heroku-cli#download-and-install) if you haven't already.

###  Database setup

- Go to the heroku dashboard and select the application you ust created.
- Click on Resources -> search for *ClearDB MYSQL* in Add-ons search bar and select the *ClearDB MYSQL* option.
- Select the *Ignite - Free* Plan and **Submit Order Form**.
- Now click on *Settings* from the Dashboard -> Click on **Reveal Config Vars**.
- copy the value of **CLEARDB_DATABASE_URL** which will be in *mysql://user:password@host/database?reconnect=true* format.
- connect to the database using the credentials from above URL and execute commands listed in the [file](https://github.com/saumyashah7/ActionLogger/blob/master/Heroku/initdb/start.sql).

### Webapplication setup

- Go to the local directory where the project repository can be copied and clone the github repository for the project
```
git clone https://github.com/saumyashah7/ActionLogger.git
```

- Login to the Heroku account
```
heroku login
```

- Initialize the git repository for heroku
```
cd ActionLogger\Heroku\
git init
heroku git:remote -a NAME_OF_THE_HEROKU_APP
```

- Edit the databae details in the web application
```
vi src\main\resources\application.properties
```

- Deploy the application 
```
git add .
git commit -am "Initial Commit"
git push heroku master
```

- Access the project using url returned after successful build, just replace eagerapp with name of your application
```
https://eagerapp.herokuapp.com/
```

